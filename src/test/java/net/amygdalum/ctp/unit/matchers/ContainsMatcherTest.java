package net.amygdalum.ctp.unit.matchers;

import static java.util.Arrays.asList;
import static net.amygdalum.comtemplate.engine.expressions.StringLiteral.string;
import static net.amygdalum.ctp.unit.FunctionMatcher.MESSAGE;
import static net.amygdalum.ctp.unit.FunctionMatcher.STATUS;
import static net.amygdalum.ctp.unit.Status.ERROR;
import static net.amygdalum.ctp.unit.Status.FAILURE;
import static net.amygdalum.ctp.unit.Status.SUCCESS;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import net.amygdalum.comtemplate.engine.Scope;
import net.amygdalum.comtemplate.engine.TemplateImmediateExpression;
import net.amygdalum.comtemplate.engine.expressions.ResolvedListLiteral;
import net.amygdalum.ctp.unit.Status;

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

	@Test
	public void testError() throws Exception {
		Scope scope = mock(Scope.class);
		TemplateImmediateExpression broken = Mockito.mock(TemplateImmediateExpression.class);
		when(broken.getText()).thenThrow(RuntimeException.class);

		assertThat(matcher.resolveResult(broken, asList(string("xyz")), scope).getAttribute(STATUS).as(Status.class), is(ERROR));
		assertThat(matcher.resolveResult(broken, asList(string("xyz")), scope).getAttribute(MESSAGE).as(String.class), containsString("RuntimeException"));
	}

}
