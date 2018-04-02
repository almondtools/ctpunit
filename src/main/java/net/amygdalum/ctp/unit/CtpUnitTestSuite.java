package net.amygdalum.ctp.unit;

import static java.util.Collections.emptyList;
import static net.amygdalum.comtemplate.engine.TemplateVariable.var;
import static net.amygdalum.comtemplate.engine.expressions.StringLiteral.string;
import static net.amygdalum.ctp.unit.FunctionMatcher.ACTUAL;
import static net.amygdalum.ctp.unit.FunctionMatcher.EXPECTED;
import static net.amygdalum.ctp.unit.FunctionMatcher.MESSAGE;
import static net.amygdalum.ctp.unit.FunctionMatcher.STATUS;
import static net.amygdalum.ctp.unit.Status.FAILURE;
import static net.amygdalum.ctp.unit.Status.IGNORE;
import static net.amygdalum.ctp.unit.Status.SUCCESS;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicNode;
import org.opentest4j.AssertionFailedError;
import org.opentest4j.TestSkippedException;

import net.amygdalum.comtemplate.engine.Scope;
import net.amygdalum.comtemplate.engine.TemplateDefinition;
import net.amygdalum.comtemplate.engine.TemplateGroup;
import net.amygdalum.comtemplate.engine.TemplateImmediateExpression;
import net.amygdalum.comtemplate.engine.TemplateInterpreter;
import net.amygdalum.comtemplate.engine.expressions.ResolvedMapLiteral;
import net.amygdalum.comtemplate.processor.TemplateProcessor;

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
