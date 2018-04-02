package net.amygdalum.ctp.unit.matchers;

import static java.util.Arrays.asList;
import static net.amygdalum.ctp.unit.Status.FAILURE;
import static net.amygdalum.ctp.unit.Status.SUCCESS;

import java.util.List;

import net.amygdalum.comtemplate.engine.Scope;
import net.amygdalum.comtemplate.engine.TemplateImmediateExpression;
import net.amygdalum.comtemplate.engine.expressions.ResolvedMapLiteral;
import net.amygdalum.ctp.unit.FunctionMatcher;
import net.amygdalum.ctp.unit.Status;

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

	@Override
	public List<Class<? extends TemplateImmediateExpression>> getResolvedClasses() {
		return asList(TemplateImmediateExpression.class);
	}

}
