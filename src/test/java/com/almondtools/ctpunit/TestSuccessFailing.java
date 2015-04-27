package com.almondtools.ctpunit;

import org.junit.runner.RunWith;

import com.almondtools.ctpunit.CtpUnitRunner.Spec;

@RunWith(CtpUnitRunner.class)
@Spec
public class TestSuccessFailing {

	@Spec(group="success")
	public void success() {
	}
	
	@Spec(group="failing")
	public void failing() {
	}
	
}
