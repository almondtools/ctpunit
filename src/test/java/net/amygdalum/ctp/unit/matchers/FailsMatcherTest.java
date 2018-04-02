package net.amygdalum.ctp.unit.matchers;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static net.amygdalum.comtemplate.engine.TemplateVariable.var;
import static net.amygdalum.comtemplate.engine.expressions.StringLiteral.string;
import static net.amygdalum.ctp.unit.FunctionMatcher.MESSAGE;
import static net.amygdalum.ctp.unit.FunctionMatcher.STATUS;
import static net.amygdalum.ctp.unit.Status.ERROR;
import static net.amygdalum.ctp.unit.Status.FAILURE;
import static net.amygdalum.ctp.unit.Status.IGNORE;
import static net.amygdalum.ctp.unit.Status.SUCCESS;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import net.amygdalum.comtemplate.engine.Scope;
import net.amygdalum.comtemplate.engine.TemplateImmediateExpression;
import net.amygdalum.comtemplate.engine.expressions.NativeObject;
import net.amygdalum.comtemplate.engine.expressions.ResolvedMapLiteral;
import net.amygdalum.ctp.unit.Status;
import net.amygdalum.ctp.unit.matchers.FailsMatcher;


public class FailsMatcherTest {

	private FailsMatcher matcher;

	@Before
	public void before() {
		matcher = new FailsMatcher();
	}

	@Test
	public void testSuccess() throws Exception {
		Scope scope = mock(Scope.class);
		ResolvedMapLiteral failure = new ResolvedMapLiteral(var("status", new NativeObject(FAILURE)));

		assertThat(matcher.resolveResult(failure, emptyList(), scope).getAttribute(STATUS).as(Status.class), is(SUCCESS));
	}

	@Test
	public void testFailure() throws Exception {
		Scope scope = mock(Scope.class);
		ResolvedMapLiteral success = new ResolvedMapLiteral(var("status", new NativeObject(SUCCESS)));
		
		assertThat(matcher.resolveResult(success, emptyList(), scope).getAttribute(STATUS).as(Status.class), is(FAILURE));
		assertThat(matcher.resolveResult(success, emptyList(), scope).getAttribute(MESSAGE).as(String.class), equalTo("expecting failing assertion but was success"));
	}

	@Test
	public void testPassThroughIgnore() throws Exception {
		Scope scope = mock(Scope.class);
		ResolvedMapLiteral ignore = new ResolvedMapLiteral(var("status", new NativeObject(IGNORE)), var("message",string("ignore message")));

		assertThat(matcher.resolveResult(ignore, emptyList(), scope).getAttribute(STATUS).as(Status.class), is(IGNORE));
		assertThat(matcher.resolveResult(ignore, emptyList(), scope).getAttribute(MESSAGE).as(String.class), equalTo("ignore message"));
	}

	@Test
	public void testPassThroughError() throws Exception {
		Scope scope = mock(Scope.class);
		ResolvedMapLiteral ignore = new ResolvedMapLiteral(var("status", new NativeObject(ERROR)), var("message",string("error message")));
		
		assertThat(matcher.resolveResult(ignore, emptyList(), scope).getAttribute(STATUS).as(Status.class), is(ERROR));
		assertThat(matcher.resolveResult(ignore, emptyList(), scope).getAttribute(MESSAGE).as(String.class), equalTo("error message"));
	}
	
	@Test
	public void testError() throws Exception {
		Scope scope = mock(Scope.class);
		TemplateImmediateExpression broken = Mockito.mock(TemplateImmediateExpression.class);
		when(broken.getText()).thenThrow(RuntimeException.class);
		
		assertThat(matcher.resolveResult(broken , asList(string("xyz")), scope).getAttribute(STATUS).as(Status.class), is(ERROR));
		assertThat(matcher.resolveResult(broken, asList(string("xyz")), scope).getAttribute(MESSAGE).as(String.class), containsString("ClassCastException"));
	}

}
