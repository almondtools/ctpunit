package com.almondtools.ctpunit.matchers;

import java.util.List;
import java.util.regex.Pattern;

import com.almondtools.comtemplate.engine.Scope;
import com.almondtools.comtemplate.engine.TemplateImmediateExpression;
import com.almondtools.comtemplate.engine.expressions.ResolvedMapLiteral;
import com.almondtools.ctpunit.FunctionMatcher;

public class EvaluatesToMatcher extends FunctionMatcher {

	private static Pattern SKIP = Pattern.compile("(^\\s+)|(\\s+$)|([\\r\\n]+\\s*)"); 
	private static Pattern COMPRESS = Pattern.compile("\\s+"); 
	
	public EvaluatesToMatcher() {
		super("evaluatesTo", 1);
	}

	@Override
	protected ResolvedMapLiteral resolveResult(TemplateImmediateExpression base, List<TemplateImmediateExpression> arguments, Scope scope) {
		try {  
			String actual = normalized(base.getText());
			String expected = normalized(arguments.get(0).getText());
			if (actual.equals(expected)) {
				return success();
			} else {
				return failure("expected normalized form <" + expected + ">, but was <" + actual + ">");
			}
		} catch (RuntimeException e) {
			return error(e);
		}
	}

	private String normalized(String string) {
		String skipped = SKIP.matcher(string).replaceAll("");
		String compressed = COMPRESS.matcher(skipped).replaceAll(" ");
		return compressed;
	}

}
