package com.almondtools.ctpunit.matchers;

import static java.util.Arrays.asList;

import java.util.List;

import com.almondtools.comtemplate.engine.Scope;
import com.almondtools.comtemplate.engine.TemplateImmediateExpression;
import com.almondtools.comtemplate.engine.expressions.ResolvedListLiteral;
import com.almondtools.comtemplate.engine.expressions.ResolvedMapLiteral;
import com.almondtools.ctpunit.FunctionMatcher;

public class ContainsMatcher extends FunctionMatcher {

	public ContainsMatcher() {
		super("contains", 1);
	}

	@Override
	protected ResolvedMapLiteral resolveResult(TemplateImmediateExpression base, List<TemplateImmediateExpression> arguments, Scope scope) {
		try {
			String actual = base.getText();
			TemplateImmediateExpression argument = arguments.get(0);
			if (argument instanceof ResolvedListLiteral) {
				int start = 0;
				for (TemplateImmediateExpression argumentItem : ((ResolvedListLiteral) argument).getList()) {
					String expected = argumentItem.getText();
					int foundAt = actual.indexOf(expected);
					if (foundAt < start) {
						return failure("expected containing all of <" + argument.getText() + ">, but was <" + actual + ">", expected, actual);
					} else {
						start = foundAt + expected.length();
					}
				}
				return success();
			} else {
				String expected = argument.getText();
				if (actual.contains(expected)) {
					return success();
				} else {
					return failure("expected containing <" + expected + ">, but was <" + actual + ">", expected, actual);
				}
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
