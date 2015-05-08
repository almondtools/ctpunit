package com.almondtools.ctpunit;

import org.junit.runner.RunWith;

import com.almondtools.ctpunit.CtpUnitRunner.Spec;

@RunWith(CtpUnitRunner.class)
@Spec(group = "failing")
public class TestFailing {

	@Spec
	public void failing() {
	}

}
