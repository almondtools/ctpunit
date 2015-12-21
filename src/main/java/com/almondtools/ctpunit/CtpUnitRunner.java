package com.almondtools.ctpunit;

import static com.almondtools.comtemplate.engine.GlobalTemplates.defaultTemplates;
import static com.almondtools.ctpunit.FunctionMatcher.ACTUAL;
import static com.almondtools.ctpunit.FunctionMatcher.EXPECTED;
import static com.almondtools.ctpunit.FunctionMatcher.MESSAGE;
import static com.almondtools.ctpunit.FunctionMatcher.STATUS;
import static com.almondtools.ctpunit.Status.FAILURE;
import static com.almondtools.ctpunit.Status.IGNORE;
import static com.almondtools.ctpunit.Status.SUCCESS;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.ComparisonFailure;
import org.junit.runner.Description;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

import com.almondtools.comtemplate.engine.ConfigurableTemplateLoader;
import com.almondtools.comtemplate.engine.DefaultErrorHandler;
import com.almondtools.comtemplate.engine.ResolverRegistry;
import com.almondtools.comtemplate.engine.Scope;
import com.almondtools.comtemplate.engine.TemplateEventNotifier;
import com.almondtools.comtemplate.engine.TemplateGroup;
import com.almondtools.comtemplate.engine.TemplateImmediateExpression;
import com.almondtools.comtemplate.engine.TemplateLoader;
import com.almondtools.comtemplate.engine.ValueDefinition;
import com.almondtools.comtemplate.engine.expressions.ErrorExpression;
import com.almondtools.comtemplate.engine.expressions.ResolvedMapLiteral;

public class CtpUnitRunner extends ParentRunner<ValueDefinition> implements Filterable {

	private static final String TEST = "test";

	private static ThreadLocal<CtpUnitCoverageCompiler> compiler = ThreadLocal.withInitial(CtpUnitCoverageCompiler::new);

	private TemplateLoader loader;
	private TemplateEventNotifier interpreter;

	public CtpUnitRunner(Class<?> testClass) throws InitializationError {
		super(testClass);
		loader = new ConfigurableTemplateLoader(compiler.get())
			.withClasspath(true);
		interpreter = new TemplateEventNotifier(ResolverRegistry.defaultRegistry(), defaultTemplates(), new DefaultErrorHandler());
		interpreter.addListener(compiler.get());
	}

	@Override
	protected List<ValueDefinition> getChildren() {
		List<ValueDefinition> children = new ArrayList<ValueDefinition>();
		TestClass testClass = getTestClass();
		SpecData data = new SpecData(testClass.getAnnotation(Spec.class));
		for (FrameworkMethod method : testClass.getAnnotatedMethods()) {
			SpecData methodData = data.merge(method.getAnnotation(Spec.class));
			children.addAll(getSpecsFor(methodData));
		}
		return children;
	}

	@Override
	protected Description describeChild(ValueDefinition child) {
		return Description.createTestDescription(getTestClass().getJavaClass(), child.getGroup().getName() + "." + child.getName());
	}

	@Override
	protected void runChild(ValueDefinition child, RunNotifier notifier) {
		Description description = describeChild(child);
		notifier.fireTestStarted(description);
		TemplateImmediateExpression evaluated = child.evaluate(interpreter, new Scope(null, child), emptyList());
		try {
			ResolvedMapLiteral result = (ResolvedMapLiteral) evaluated;
			Status status = result.getAttribute(STATUS).as(Status.class);
			String message = Optional.ofNullable(result.getAttribute(MESSAGE))
				.map(msg -> msg.as(String.class))
				.orElse(null);
			if (child.getName().startsWith(TEST)) {
				if (status == SUCCESS) {
					notifier.fireTestFinished(description);
				} else if (status == FAILURE) {
					Optional<String> expected = Optional.ofNullable(result.getAttribute(EXPECTED))
						.map(msg -> msg.as(String.class));
					Optional<String> actual = Optional.ofNullable(result.getAttribute(ACTUAL))
						.map(msg -> msg.as(String.class));
					if (expected.isPresent() && actual.isPresent()) {
						notifier.fireTestFailure(failure(description, message, expected.get(), actual.get()));
					} else {
						notifier.fireTestFailure(failure(description, message));
					}
				} else if (status == IGNORE) {
					notifier.fireTestIgnored(description);
				} else if (status == Status.ERROR) {
					notifier.fireTestFailure(error(description, message));
				}
			} else {
				notifier.fireTestFailure(error(description, child));
			}
		} catch (RuntimeException e) {
			if (evaluated instanceof ErrorExpression) {
				notifier.fireTestFailure(error(description, ((ErrorExpression) evaluated).getMessage()));
			} else {
				notifier.fireTestFailure(error(description, child));
			}
		}
	}

	public Failure failure(Description description, String message) {
		return new Failure(description, new AssertionError(message));
	}

	public Failure failure(Description description, String message, String expected, String actual) {
		return new Failure(description, new ComparisonFailure(message, expected, actual));
	}

	public Failure error(Description description, ValueDefinition child) {
		return error(description, child.getName() + " is not a valid test case");
	}

	public Failure error(Description description, String message) {
		return new Failure(description, new IllegalArgumentException(message));
	}

	private List<ValueDefinition> getSpecsFor(SpecData data) {
		TemplateGroup group = loader.loadGroup(data.group);
		return group.getDefinitions().stream()
			.filter(def -> def instanceof ValueDefinition)
			.map(def -> (ValueDefinition) def)
			.filter(con -> con.getName().startsWith(TEST))
			.collect(toList());
	}

	private static class SpecData {
		public String group;

		public SpecData(Spec spec) {
			if (spec != null) {
				this.group = spec.group();
			}
		}

		public SpecData(String group) {
			this.group = group;
		}

		public SpecData merge(Spec spec) {
			if (spec != null) {
				String file = this.group == null || this.group.length() == 0 ? spec.group() : this.group;
				return new SpecData(file);
			} else {
				return this;
			}
		}

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.TYPE, ElementType.METHOD })
	public @interface Spec {
		String group() default "";
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.TYPE })
	public @interface Matcher {
		Class<? extends FunctionMatcher>[] value();
	}

}
