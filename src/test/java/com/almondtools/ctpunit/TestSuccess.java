package com.almondtools.ctpunit;

import org.junit.runner.RunWith;

import com.almondtools.ctpunit.CtpUnitRunner.Spec;

@RunWith(CtpUnitRunner.class)
@Spec(group="success")
public class TestSuccess {

	@Spec
	public void success() {
	}
	
}
