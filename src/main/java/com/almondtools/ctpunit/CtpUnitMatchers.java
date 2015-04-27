package com.almondtools.ctpunit;

import com.almondtools.comtemplate.engine.resolvers.CompoundResolver;
import com.almondtools.ctpunit.matchers.EqualToMatcher;
import com.almondtools.ctpunit.matchers.EvaluatesToMatcher;
import com.almondtools.ctpunit.matchers.FailsMatcher;

public class CtpUnitMatchers extends CompoundResolver {

	public CtpUnitMatchers() {
		super(new EqualToMatcher(),
			new EvaluatesToMatcher(),
			new FailsMatcher());
	}

}
