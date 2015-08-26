package com.almondtools.ctpunit;

import com.almondtools.comtemplate.engine.resolvers.CompoundResolver;
import com.almondtools.ctpunit.matchers.EqNoWhitespaceMatcher;
import com.almondtools.ctpunit.matchers.EqualToMatcher;
import com.almondtools.ctpunit.matchers.EqCompressedWhitespaceMatcher;
import com.almondtools.ctpunit.matchers.FailsMatcher;

public class CtpUnitMatchers extends CompoundResolver {

	public CtpUnitMatchers() {
		super(new EqualToMatcher(),
			new EqCompressedWhitespaceMatcher(),
			new EqNoWhitespaceMatcher(),
			new FailsMatcher());
	}

}
