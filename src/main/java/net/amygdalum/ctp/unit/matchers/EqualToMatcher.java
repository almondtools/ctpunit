package net.amygdalum.ctp.unit.matchers;

import static java.util.Arrays.asList;

import java.util.List;

import net.amygdalum.comtemplate.engine.Scope;
import net.amygdalum.comtemplate.engine.TemplateImmediateExpression;
import net.amygdalum.comtemplate.engine.expressions.ResolvedMapLiteral;
import net.amygdalum.ctp.unit.FunctionMatcher;

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
