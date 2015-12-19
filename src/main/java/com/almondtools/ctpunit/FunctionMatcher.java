package com.almondtools.ctpunit;

import static com.almondtools.comtemplate.engine.TemplateVariable.var;
import static com.almondtools.comtemplate.engine.expressions.StringLiteral.string;
import static com.almondtools.ctpunit.Status.ERROR;
import static com.almondtools.ctpunit.Status.FAILURE;
import static com.almondtools.ctpunit.Status.SUCCESS;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import com.almondtools.comtemplate.engine.Scope;
import com.almondtools.comtemplate.engine.TemplateImmediateExpression;
import com.almondtools.comtemplate.engine.expressions.NativeObject;
import com.almondtools.comtemplate.engine.expressions.ResolvedMapLiteral;
import com.almondtools.comtemplate.engine.resolvers.FunctionResolver;

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
