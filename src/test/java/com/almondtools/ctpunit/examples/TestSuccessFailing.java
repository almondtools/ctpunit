package com.almondtools.ctpunit.examples;

import org.junit.runner.RunWith;

import com.almondtools.ctpunit.CtpUnitRunner;
import com.almondtools.ctpunit.CtpUnitRunner.Spec;

@RunWith(CtpUnitRunner.class)
public class TestSuccessFailing {

	@Spec(group="success")
	public void success() {
	}
	
	@Spec(group="failing")
	public void failing() {
	}
	
}
