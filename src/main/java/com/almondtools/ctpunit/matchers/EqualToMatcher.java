package com.almondtools.ctpunit.matchers;

import static java.util.Arrays.asList;

import java.util.List;

import com.almondtools.comtemplate.engine.Scope;
import com.almondtools.comtemplate.engine.TemplateImmediateExpression;
import com.almondtools.comtemplate.engine.expressions.ResolvedMapLiteral;
import com.almondtools.ctpunit.FunctionMatcher;

public class EqualToMatcher extends FunctionMatcher {

	public EqualToMatcher() {
		super("equalTo", 1);
	}

	@Override
	protected ResolvedMapLiteral resolveResult(TemplateImmediateExpression base, List<TemplateImmediateExpression> arguments, Scope scope) {
		try {
			String actual = base.getText();
			String expected = arguments.get(0).getText();
			if (actual.equals(expected)) {
				return success();
			} else {
				return failure("expected <" + expected + ">, but was <" + actual + ">", expected, actual);
			}
		} catch (RuntimeException e) {
			return error(e);
		}
	}

	@Override
	public List<Class<? extends TemplateImmediateExpression>> getResolvedClasses() {
		return asList(TemplateImmediateExpression.class);
	}

}
