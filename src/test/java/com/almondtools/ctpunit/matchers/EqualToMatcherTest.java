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


public class EqualToMatcherTest {

	private EqualToMatcher resolver;

	@Before
	public void before() {
		resolver = new EqualToMatcher();
	}

	@Test
	public void testSuccess() throws Exception {
		Scope scope = mock(Scope.class);

		assertThat(resolver.resolveResult(string("abc"), asList(string("abc")), scope).getAttribute(STATUS).as(Status.class), is(SUCCESS));
		assertThat(resolver.resolveResult(string("a b  c"), asList(string("a b  c")), scope).getAttribute(STATUS).as(Status.class), is(SUCCESS));
	}

	@Test
	public void testFailure() throws Exception {
		Scope scope = mock(Scope.class);

		assertThat(resolver.resolveResult(string("abc"), asList(string("xyz")), scope).getAttribute(STATUS).as(Status.class), is(FAILURE));
		assertThat(resolver.resolveResult(string("abc"), asList(string("xyz")), scope).getAttribute(MESSAGE).as(String.class), equalTo("expected <xyz>, but was <abc>"));
		assertThat(resolver.resolveResult(string("abc"), asList(string(" abc ")), scope).getAttribute(STATUS).as(Status.class), is(FAILURE));
		assertThat(resolver.resolveResult(string("abc"), asList(string(" abc ")), scope).getAttribute(MESSAGE).as(String.class), equalTo("expected < abc >, but was <abc>"));
		assertThat(resolver.resolveResult(string("a b  c"), asList(string("a b c")), scope).getAttribute(STATUS).as(Status.class), is(FAILURE));
		assertThat(resolver.resolveResult(string("a b  c"), asList(string("a b c")), scope).getAttribute(MESSAGE).as(String.class), equalTo("expected <a b c>, but was <a b  c>"));
		assertThat(resolver.resolveResult(string("a b  c"), asList(string("ab  c")), scope).getAttribute(STATUS).as(Status.class), is(FAILURE));
		assertThat(resolver.resolveResult(string("a b  c"), asList(string("ab  c")), scope).getAttribute(MESSAGE).as(String.class), equalTo("expected <ab  c>, but was <a b  c>"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testError() throws Exception {
		Scope scope = mock(Scope.class);
		TemplateImmediateExpression broken = Mockito.mock(TemplateImmediateExpression.class);
		when(broken.getText()).thenThrow(RuntimeException.class);
		
		assertThat(resolver.resolveResult(broken , asList(string("xyz")), scope).getAttribute(STATUS).as(Status.class), is(ERROR));
		assertThat(resolver.resolveResult(broken, asList(string("xyz")), scope).getAttribute(MESSAGE).as(String.class), containsString("RuntimeException"));
	}

}
