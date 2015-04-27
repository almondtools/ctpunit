package com.almondtools.ctpunit.matchers;

import static com.almondtools.ctpunit.Status.FAILURE;
import static com.almondtools.ctpunit.Status.SUCCESS;

import java.util.List;

import com.almondtools.comtemplate.engine.Scope;
import com.almondtools.comtemplate.engine.TemplateImmediateExpression;
import com.almondtools.comtemplate.engine.expressions.ResolvedMapLiteral;
import com.almondtools.ctpunit.FunctionMatcher;
import com.almondtools.ctpunit.Status;

public class FailsMatcher extends FunctionMatcher {

	public FailsMatcher() {
		super("fails");
	}

	@Override
	protected ResolvedMapLiteral resolveResult(TemplateImmediateExpression base, List<TemplateImmediateExpression> arguments, Scope scope) {
		try {
			ResolvedMapLiteral map = (ResolvedMapLiteral) base;
			Status status = map.getAttribute(FunctionMatcher.STATUS).as(Status.class);
			if (status == FAILURE) {
				return success();
			} else if (status == SUCCESS) {
				return failure("expecting failing assertion but was success");
			} else {
				return map;
			}
		} catch (RuntimeException e) {
			return error(e);
		}
	}

}
