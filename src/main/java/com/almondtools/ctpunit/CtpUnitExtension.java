package com.almondtools.ctpunit;

import static com.almondtools.comtemplate.engine.GlobalTemplates.defaultTemplates;
import static com.almondtools.comtemplate.engine.ResolverRegistry.defaultRegistry;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import com.almondtools.comtemplate.engine.ConfigurableTemplateLoader;
import com.almondtools.comtemplate.engine.DefaultErrorHandler;
import com.almondtools.comtemplate.engine.TemplateEventNotifier;
import com.almondtools.comtemplate.engine.TemplateGroup;
import com.almondtools.comtemplate.engine.TemplateInterpreter;
import com.almondtools.comtemplate.engine.TemplateLoader;

public class CtpUnitExtension implements BeforeEachCallback, ParameterResolver {

	private static ThreadLocal<CtpUnitCoverageCompiler> compiler = ThreadLocal.withInitial(CtpUnitCoverageCompiler::new);

	private String src;
	private TemplateLoader loader;
	private TemplateEventNotifier interpreter;

	@Override
	public void beforeEach(ExtensionContext context) throws Exception {
		src = context.getRequiredTestMethod().getAnnotation(Spec.class).src();
		loader = new ConfigurableTemplateLoader(compiler.get())
			.withSource(Paths.get(src))
			.withClasspath(true);
		interpreter = new TemplateEventNotifier(loader, defaultRegistry(), defaultTemplates(), new DefaultErrorHandler());
		interpreter.addListener(compiler.get());
	}

	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		return parameterContext.getParameter().getType() == TemplateLoader.class
			|| parameterContext.getParameter().getType() == TemplateInterpreter.class
			|| parameterContext.getParameter().getType() == CtpUnitTestSuite.class;
	}

	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		if (parameterContext.getParameter().getType() == TemplateLoader.class) {
			return loader;
		} else if (parameterContext.getParameter().getType() == TemplateInterpreter.class) {
			return interpreter;
		} else if (parameterContext.getParameter().getType() == CtpUnitTestSuite.class) {
			return templateGroups();
		} else {
			return null;
		}
	}

	private CtpUnitTestSuite templateGroups() {
		try {
			Path path = Paths.get(src);
			return new CtpUnitTestSuite(interpreter, Files.walk(path)
				.filter(p -> Files.isRegularFile(p) && p.getFileName().toString().endsWith(".ctp"))
				.map(p -> path.relativize(p))
				.map(p -> loadTests(p))
				.collect(toList()));
		} catch (IOException e) {
			throw new ParameterResolutionException("resolution of tests in '" + src + "' failed:", e);
		}
	}

	private TemplateGroup loadTests(Path p) {
		String templateFileName = p.toString();
		String templateName = templateFileName.substring(0, templateFileName.length() - 4).replace(File.separatorChar, '.');

		return loader.loadGroup(templateName);
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD })
	public @interface Spec {
		String src();
	}

}
