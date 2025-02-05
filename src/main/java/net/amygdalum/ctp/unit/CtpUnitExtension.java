package net.amygdalum.ctp.unit;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static net.amygdalum.comtemplate.engine.GlobalTemplates.defaultTemplates;
import static net.amygdalum.comtemplate.engine.ResolverRegistry.defaultRegistry;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import net.amygdalum.comtemplate.engine.ConfigurableTemplateLoader;
import net.amygdalum.comtemplate.engine.DefaultErrorHandler;
import net.amygdalum.comtemplate.engine.DefaultTemplateInterpreter;
import net.amygdalum.comtemplate.engine.TemplateGroup;
import net.amygdalum.comtemplate.engine.TemplateInterpreter;
import net.amygdalum.comtemplate.engine.TemplateLoader;

public class CtpUnitExtension implements BeforeEachCallback, ParameterResolver {

	private static ThreadLocal<CtpUnitCoverageCompiler> compiler = ThreadLocal.withInitial(CtpUnitCoverageCompiler::new);

	private String src;
	private List<String> modules;
	private TemplateLoader loader;
	private DefaultTemplateInterpreter interpreter;


	@Override
	public void beforeEach(ExtensionContext context) throws Exception {
		Spec spec = context.getRequiredTestMethod().getAnnotation(Spec.class);
		src = spec.src();
		modules = asList(spec.modules());
		loader = new ConfigurableTemplateLoader(compiler.get())
			.withSource(Paths.get(src))
			.withClasspath(true);
		interpreter = new DefaultTemplateInterpreter(loader, defaultRegistry(), defaultTemplates(), new DefaultErrorHandler());
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
				.filter(p -> modules.contains(groupNameFrom(p)))
				.map(p -> loadTests(p))
				.collect(toList()));
		} catch (IOException e) {
			throw new ParameterResolutionException("resolution of tests in '" + src + "' failed:", e);
		}
	}

	private TemplateGroup loadTests(Path p) {
		String templateName = templateNameFrom(p);

		return loader.loadGroup(templateName);
	}

	private String groupNameFrom(Path p) {
		Path parent = p.getParent();
		if (parent == null) {
			return "";
		}
		String pathName = parent.toString();
		return pathName.replace(File.separatorChar, '.');
	}

	private String templateNameFrom(Path p) {
		String pathName = p.toString();
		return pathName.substring(0, pathName.length() - 4).replace(File.separatorChar, '.');
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD })
	public @interface Spec {
		String src();

		String[] modules() default {};
	}

}
