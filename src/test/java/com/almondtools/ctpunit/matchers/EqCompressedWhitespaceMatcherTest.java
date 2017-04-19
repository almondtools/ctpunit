package com.almondtools.ctpunit.matchers;

import static com.almondtools.comtemplate.engine.expressions.StringLiteral.string;
import static com.almondtools.ctpunit.FunctionMatcher.MESSAGE;
import static com.almondtools.ctpunit.FunctionMatcher.STATUS;
import static com.almondtools.ctpunit.Status.ERROR;
import static com.almondtools.ctpunit.Status.FAILURE;
import static com.almondtools.ctpunit.Status.SUCCESS;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.almondtools.comtemplate.engine.Scope;
import com.almondtools.comtemplate.engine.TemplateImmediateExpression;
import com.almondtools.ctpunit.Status;


public class EqCompressedWhitespaceMatcherTest {

	private EqCompressedWhitespaceMatcher matcher;

	@Before
	public void before() {
		matcher = new EqCompressedWhitespaceMatcher();
	}

	@Test
	public void testSuccess() throws Exception {
		Scope scope = mock(Scope.class);

		assertThat(matcher.resolveResult(string("abc"), asList(string("abc")), scope).getAttribute(STATUS).as(Status.class), is(SUCCESS));
		assertThat(matcher.resolveResult(string("abc"), asList(string(" abc")), scope).getAttribute(STATUS).as(Status.class), is(SUCCESS));
		assertThat(matcher.resolveResult(string("abc"), asList(string("abc ")), scope).getAttribute(STATUS).as(Status.class), is(SUCCESS));
		assertThat(matcher.resolveResult(string(" abc"), asList(string("abc")), scope).getAttribute(STATUS).as(Status.class), is(SUCCESS));
		assertThat(matcher.resolveResult(string("abc "), asList(string("abc")), scope).getAttribute(STATUS).as(Status.class), is(SUCCESS));
		assertThat(matcher.resolveResult(string("a b  c"), asList(string("a b c")), scope).getAttribute(STATUS).as(Status.class), is(SUCCESS));
		assertThat(matcher.resolveResult(string(" a \nb\t c\r"), asList(string("a b c")), scope).getAttribute(STATUS).as(Status.class), is(SUCCESS));
	}

	@Test
	public void testFailure() throws Exception {
		Scope scope = mock(Scope.class);

		assertThat(matcher.resolveResult(string("abc"), asList(string("xyz")), scope).getAttribute(STATUS).as(Status.class), is(FAILURE));
		assertThat(matcher.resolveResult(string("abc"), asList(string("xyz")), scope).getAttribute(MESSAGE).as(String.class), equalTo("expected normalized form <xyz>, but was <abc>"));
		assertThat(matcher.resolveResult(string(" a \nb\t c\r"), asList(string("abc")), scope).getAttribute(STATUS).as(Status.class), is(FAILURE));
		assertThat(matcher.resolveResult(string(" a \nb\t c\r"), asList(string("abc")), scope).getAttribute(MESSAGE).as(String.class), equalTo("expected normalized form <abc>, but was <a b c>"));
		assertThat(matcher.resolveResult(string(" a\n b\t c\r"), asList(string("abc")), scope).getAttribute(STATUS).as(Status.class), is(FAILURE));
		assertThat(matcher.resolveResult(string(" a\n b\t c\r"), asList(string("abc")), scope).getAttribute(MESSAGE).as(String.class), equalTo("expected normalized form <abc>, but was <a b c>"));
	}

	@Test
	public void testError() throws Exception {
		Scope scope = mock(Scope.class);
		TemplateImmediateExpression broken = Mockito.mock(TemplateImmediateExpression.class);
		when(broken.getText()).thenThrow(RuntimeException.class);
		
		assertThat(matcher.resolveResult(broken , asList(string("xyz")), scope).getAttribute(STATUS).as(Status.class), is(ERROR));
		assertThat(matcher.resolveResult(broken, asList(string("xyz")), scope).getAttribute(MESSAGE).as(String.class), containsString("RuntimeException"));
	}

}
