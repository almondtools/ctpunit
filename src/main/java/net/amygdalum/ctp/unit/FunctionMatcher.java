package net.amygdalum.ctp.unit;

import static net.amygdalum.comtemplate.engine.TemplateVariable.var;
import static net.amygdalum.comtemplate.engine.expressions.StringLiteral.string;
import static net.amygdalum.ctp.unit.Status.ERROR;
import static net.amygdalum.ctp.unit.Status.FAILURE;
import static net.amygdalum.ctp.unit.Status.SUCCESS;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import net.amygdalum.comtemplate.engine.Scope;
import net.amygdalum.comtemplate.engine.TemplateImmediateExpression;
import net.amygdalum.comtemplate.engine.expressions.NativeObject;
import net.amygdalum.comtemplate.engine.expressions.ResolvedMapLiteral;
import net.amygdalum.comtemplate.engine.resolvers.FunctionResolver;

public abstract class FunctionMatcher extends FunctionResolver {

	public static final String MESSAGE = "message";
	public static final String EXPECTED = "expected";
	public static final String ACTUAL = "actual";
	public static final String STATUS = "status";

	public FunctionMatcher(String name, int arity) {
		super(name, arity);
	}

	public FunctionMatcher(String name) {
		super(name);
	}

	@Override
	public TemplateImmediateExpression resolve(TemplateImmediateExpression base, List<TemplateImmediateExpression> arguments, Scope scope) {
		return resolveResult(base, arguments, scope);
	}

	protected abstract ResolvedMapLiteral resolveResult(TemplateImmediateExpression base, List<TemplateImmediateExpression> arguments, Scope scope);

	protected ResolvedMapLiteral success() {
		return new ResolvedMapLiteral(var(STATUS, new NativeObject(SUCCESS)));
	}

	protected ResolvedMapLiteral failure(String message, String expected, String actual) {
		return new ResolvedMapLiteral(var(STATUS, new NativeObject(FAILURE)), var(MESSAGE, string(message)), var(EXPECTED, string(expected)), var(ACTUAL, string(actual)));
	}

	protected ResolvedMapLiteral failure(String message) {
		return new ResolvedMapLiteral(var(STATUS, new NativeObject(FAILURE)), var(MESSAGE, string(message)));
	}

	protected ResolvedMapLiteral error(RuntimeException e) {
		String msg = e.getMessage();
		String stackTrace = getStacktrace(e);
		return new ResolvedMapLiteral(var(STATUS, new NativeObject(ERROR)), var(MESSAGE, string(msg + '\n' + stackTrace)));
	}

	public String getStacktrace(RuntimeException e) {
		StringWriter writer = new StringWriter();
		e.printStackTrace(new PrintWriter(writer));
		return writer.toString();
	}

}
