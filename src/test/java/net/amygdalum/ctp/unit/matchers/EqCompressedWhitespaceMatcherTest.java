package net.amygdalum.ctp.unit.matchers;

import static java.util.Arrays.asList;
import static net.amygdalum.comtemplate.engine.expressions.StringLiteral.string;
import static net.amygdalum.ctp.unit.FunctionMatcher.MESSAGE;
import static net.amygdalum.ctp.unit.FunctionMatcher.STATUS;
import static net.amygdalum.ctp.unit.Status.ERROR;
import static net.amygdalum.ctp.unit.Status.FAILURE;
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
import net.amygdalum.ctp.unit.Status;
import net.amygdalum.ctp.unit.matchers.EqCompressedWhitespaceMatcher;


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
