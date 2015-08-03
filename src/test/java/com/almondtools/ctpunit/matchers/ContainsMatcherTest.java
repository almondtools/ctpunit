package com.almondtools.ctpunit.matchers;

import static com.almondtools.comtemplate.engine.expressions.StringLiteral.string;
import static com.almondtools.ctpunit.FunctionMatcher.MESSAGE;
import static com.almondtools.ctpunit.FunctionMatcher.STATUS;
import static com.almondtools.ctpunit.Status.ERROR;
import static com.almondtools.ctpunit.Status.FAILURE;
import static com.almondtools.ctpunit.Status.SUCCESS;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.almondtools.comtemplate.engine.Scope;
import com.almondtools.comtemplate.engine.TemplateImmediateExpression;
import com.almondtools.comtemplate.engine.expressions.ResolvedListLiteral;
import com.almondtools.ctpunit.Status;

public class ContainsMatcherTest {

	private ContainsMatcher matcher;

	@Before
	public void before() {
		matcher = new ContainsMatcher();
	}

	@Test
	public void testSuccessOnSingleText() throws Exception {
		Scope scope = mock(Scope.class);

		assertThat(matcher.resolveResult(string("abc"), asList(string("b")), scope).getAttribute(STATUS).as(Status.class), is(SUCCESS));
		assertThat(matcher.resolveResult(string("abc"), asList(string("a")), scope).getAttribute(STATUS).as(Status.class), is(SUCCESS));
		assertThat(matcher.resolveResult(string("abc"), asList(string("c")), scope).getAttribute(STATUS).as(Status.class), is(SUCCESS));
	}

	@Test
	public void testFailureOnSingleText() throws Exception {
		Scope scope = mock(Scope.class);

		assertThat(matcher.resolveResult(string("abc"), asList(string("d")), scope).getAttribute(STATUS).as(Status.class), is(FAILURE));
	}

	@Test
	public void testSuccessOnTextList() throws Exception {
		Scope scope = mock(Scope.class);

		assertThat(matcher.resolveResult(string("abc"), asList(new ResolvedListLiteral(string("b"))), scope).getAttribute(STATUS).as(Status.class), is(SUCCESS));
		assertThat(matcher.resolveResult(string("abc"), asList(new ResolvedListLiteral(string("a"), string("b"))), scope).getAttribute(STATUS).as(Status.class), is(SUCCESS));
		assertThat(matcher.resolveResult(string("abc"), asList(new ResolvedListLiteral(string("b"), string("c"))), scope).getAttribute(STATUS).as(Status.class), is(SUCCESS));
		assertThat(matcher.resolveResult(string("abc"), asList(new ResolvedListLiteral(string("a"), string("b"), string("c"))), scope).getAttribute(STATUS).as(Status.class), is(SUCCESS));
	}

	@Test
	public void testFailureOnTextList() throws Exception {
		Scope scope = mock(Scope.class);

		assertThat(matcher.resolveResult(string("abc"), asList(new ResolvedListLiteral(string("d"))), scope).getAttribute(STATUS).as(Status.class), is(FAILURE));
		assertThat(matcher.resolveResult(string("abc"), asList(new ResolvedListLiteral(string("b"), string("a"))), scope).getAttribute(STATUS).as(Status.class), is(FAILURE));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testError() throws Exception {
		Scope scope = mock(Scope.class);
		TemplateImmediateExpression broken = Mockito.mock(TemplateImmediateExpression.class);
		when(broken.getText()).thenThrow(RuntimeException.class);

		assertThat(matcher.resolveResult(broken, asList(string("xyz")), scope).getAttribute(STATUS).as(Status.class), is(ERROR));
		assertThat(matcher.resolveResult(broken, asList(string("xyz")), scope).getAttribute(MESSAGE).as(String.class), containsString("RuntimeException"));
	}

}
