package com.almondtools.ctpunit.examples;

import org.junit.runner.RunWith;

import com.almondtools.ctpunit.CtpUnitRunner;
import com.almondtools.ctpunit.CtpUnitRunner.Spec;

@RunWith(CtpUnitRunner.class)
@Spec(group = "partiallyUncovered")
public class TestPartiallyUncovered {

	@Spec
	public void test() {
	}

}
