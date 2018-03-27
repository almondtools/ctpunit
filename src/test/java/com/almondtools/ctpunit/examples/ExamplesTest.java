package com.almondtools.ctpunit.examples;

import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;

import com.almondtools.ctpunit.CtpUnitExtension.Spec;
import com.almondtools.ctpunit.CtpUnitExtension;
import com.almondtools.ctpunit.CtpUnitTestSuite;

@ExtendWith(CtpUnitExtension.class)
public class ExamplesTest {

	@Spec(src = "src/test/resources")
	@TestFactory
	public Stream<DynamicNode> tests(CtpUnitTestSuite suite) throws Exception {
		return suite.testsuite();
	}
}
