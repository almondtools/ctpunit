package com.almondtools.ctpunit.matchers;

import static com.almondtools.comtemplate.engine.TemplateVariable.var;
import static com.almondtools.comtemplate.engine.expressions.StringLiteral.string;
import static com.almondtools.ctpunit.FunctionMatcher.MESSAGE;
import static com.almondtools.ctpunit.FunctionMatcher.STATUS;
import static com.almondtools.ctpunit.Status.ERROR;
import static com.almondtools.ctpunit.Status.FAILURE;
import static com.almondtools.ctpunit.Status.IGNORE;
import static com.almondtools.ctpunit.Status.SUCCESS;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
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
import com.almondtools.comtemplate.engine.expressions.NativeObject;
import com.almondtools.comtemplate.engine.expressions.ResolvedMapLiteral;
import com.almondtools.ctpunit.Status;


public class FailsMatcherTest {

	private FailsMatcher resolver;

	@Before
	public void before() {
		resolver = new FailsMatcher();
	}

	@Test
	public void testSuccess() throws Exception {
		Scope scope = mock(Scope.class);
		ResolvedMapLiteral failure = new ResolvedMapLiteral(var("status", new NativeObject(FAILURE)));

		assertThat(resolver.resolveResult(failure, emptyList(), scope).getAttribute(STATUS).as(Status.class), is(SUCCESS));
	}

	@Test
	public void testFailure() throws Exception {
		Scope scope = mock(Scope.class);
		ResolvedMapLiteral success = new ResolvedMapLiteral(var("status", new NativeObject(SUCCESS)));
		
		assertThat(resolver.resolveResult(success, emptyList(), scope).getAttribute(STATUS).as(Status.class), is(FAILURE));
		assertThat(resolver.resolveResult(success, emptyList(), scope).getAttribute(MESSAGE).as(String.class), equalTo("expecting failing assertion but was success"));
	}

	@Test
	public void testPassThroughIgnore() throws Exception {
		Scope scope = mock(Scope.class);
		ResolvedMapLiteral ignore = new ResolvedMapLiteral(var("status", new NativeObject(IGNORE)), var("message",string("ignore message")));

		assertThat(resolver.resolveResult(ignore, emptyList(), scope).getAttribute(STATUS).as(Status.class), is(IGNORE));
		assertThat(resolver.resolveResult(ignore, emptyList(), scope).getAttribute(MESSAGE).as(String.class), equalTo("ignore message"));
	}

	@Test
	public void testPassThroughError() throws Exception {
		Scope scope = mock(Scope.class);
		ResolvedMapLiteral ignore = new ResolvedMapLiteral(var("status", new NativeObject(ERROR)), var("message",string("error message")));
		
		assertThat(resolver.resolveResult(ignore, emptyList(), scope).getAttribute(STATUS).as(Status.class), is(ERROR));
		assertThat(resolver.resolveResult(ignore, emptyList(), scope).getAttribute(MESSAGE).as(String.class), equalTo("error message"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testError() throws Exception {
		Scope scope = mock(Scope.class);
		TemplateImmediateExpression broken = Mockito.mock(TemplateImmediateExpression.class);
		when(broken.getText()).thenThrow(RuntimeException.class);
		
		assertThat(resolver.resolveResult(broken , asList(string("xyz")), scope).getAttribute(STATUS).as(Status.class), is(ERROR));
		assertThat(resolver.resolveResult(broken, asList(string("xyz")), scope).getAttribute(MESSAGE).as(String.class), containsString("ClassCastException"));
	}

}
