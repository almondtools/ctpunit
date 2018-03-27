package com.almondtools.ctpunit;

import static com.almondtools.comtemplate.engine.TemplateVariable.var;
import static com.almondtools.comtemplate.engine.expressions.StringLiteral.string;
import static com.almondtools.ctpunit.FunctionMatcher.ACTUAL;
import static com.almondtools.ctpunit.FunctionMatcher.EXPECTED;
import static com.almondtools.ctpunit.FunctionMatcher.MESSAGE;
import static com.almondtools.ctpunit.FunctionMatcher.STATUS;
import static com.almondtools.ctpunit.Status.FAILURE;
import static com.almondtools.ctpunit.Status.IGNORE;
import static com.almondtools.ctpunit.Status.SUCCESS;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicNode;
import org.opentest4j.AssertionFailedError;
import org.opentest4j.TestSkippedException;

import com.almondtools.comtemplate.engine.Scope;
import com.almondtools.comtemplate.engine.TemplateDefinition;
import com.almondtools.comtemplate.engine.TemplateGroup;
import com.almondtools.comtemplate.engine.TemplateImmediateExpression;
import com.almondtools.comtemplate.engine.TemplateInterpreter;
import com.almondtools.comtemplate.engine.expressions.ResolvedMapLiteral;
import com.almondtools.comtemplate.processor.TemplateProcessor;

public class CtpUnitTestSuite {

	private static final String TEST = "test";

	private TemplateInterpreter interpreter;
	private List<TemplateGroup> groups;

	public CtpUnitTestSuite(TemplateInterpreter interpreter, List<TemplateGroup> group) {
		this.interpreter = interpreter;
		this.groups = group;
	}

	public Stream<DynamicNode> testsuite() {
		return groups.stream()
			.map(group -> dynamicContainer(group.getName(), testgroup(group)));

	}

	public Stream<DynamicNode> testgroup(TemplateGroup group) {
		return group.getDefinitions().stream()
			.filter(def -> def.getName().startsWith(TEST))
			.map(def -> dynamicTest(def.getName(), () -> test(def)));
	}

	public void test(TemplateDefinition def) {
		TemplateImmediateExpression evaluated = def.evaluate(interpreter, new Scope(null, def, var(TemplateProcessor.SOURCE, string(".")), var(TemplateProcessor.TARGET, string("."))),
			emptyList());
		ResolvedMapLiteral result = (ResolvedMapLiteral) evaluated;
		Status status = result.getAttribute(STATUS).as(Status.class);
		String message = Optional.ofNullable(result.getAttribute(MESSAGE))
			.map(msg -> msg.as(String.class))
			.orElse(null);

		if (status == SUCCESS) {
			return;
		} else if (status == FAILURE) {
			Optional<String> expected = Optional.ofNullable(result.getAttribute(EXPECTED))
				.map(msg -> msg.as(String.class));
			Optional<String> actual = Optional.ofNullable(result.getAttribute(ACTUAL))
				.map(msg -> msg.as(String.class));
			if (expected.isPresent() && actual.isPresent()) {
				throw new AssertionFailedError(message, expected.get(), actual.get());
			} else {
				throw new AssertionFailedError(message);
			}
		} else if (status == IGNORE) {
			throw new TestSkippedException();
		} else if (status == Status.ERROR) {
			throw new AssertionFailedError(message);
		}
	}

}
