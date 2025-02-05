package net.amygdalum.ctp.unit;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.tree.ParseTree;

import net.amygdalum.comtemplate.engine.InterpreterListener;
import net.amygdalum.comtemplate.engine.Scope;
import net.amygdalum.comtemplate.engine.TemplateCompiler;
import net.amygdalum.comtemplate.engine.TemplateDefinition;
import net.amygdalum.comtemplate.engine.TemplateExpression;
import net.amygdalum.comtemplate.engine.TemplateGroup;
import net.amygdalum.comtemplate.engine.TemplateImmediateExpression;
import net.amygdalum.comtemplate.engine.TemplateLoader;
import net.amygdalum.comtemplate.engine.expressions.EvalAnonymousTemplate;
import net.amygdalum.comtemplate.parser.TemplateGroupBuilder;
import net.amygdalum.comtemplate.parser.TemplateGroupNode;

public class CtpUnitCoverageCompiler implements TemplateCompiler, InterpreterListener {

	private Set<String> groups;
	private Map<TemplateExpression, Set<TokenInterval>> locations;
	private SortedSet<TokenInterval> uncovered;
	private SortedSet<TokenInterval> covered;

	public CtpUnitCoverageCompiler() {
		groups = new LinkedHashSet<>();
		locations = new IdentityHashMap<>();
		uncovered= new TreeSet<>();
		covered= new TreeSet<>();
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run() {
				dumpCoverage(new PrintWriter(System.out));
			}
		}));
	}

	public void dumpCoverage(PrintWriter writer) {
		for (String group : groups) {
			List<TokenInterval> covered = getCovered(group);
			List<TokenInterval> uncovered = getUncovered(group);
			for (TokenInterval c : covered) {
				writer.println(Stream.of(group, "+", c.toString()).collect(joining("\t")));
			}
			for (TokenInterval c : uncovered) {
				writer.println(Stream.of(group, "-", c.toString()).collect(joining("\t")));
			}
		}
		writer.flush();
	}

	public List<TokenInterval> getUncovered(String group) {
		return uncovered.stream()
			.filter(interval -> interval.belongsTo(group))
			.collect(toList());
	}

	public List<TokenInterval> getCovered(String group) {
		return covered.stream()
			.filter(interval -> interval.belongsTo(group))
			.collect(toList());
	}

	public Map<Integer, Token> tokens(List<TemplateExpression> expressions) {
		return expressions.stream()
			.flatMap(expr -> locations.get(expr).stream())
			.filter(Objects::nonNull)
			.flatMap(tokenInterval -> Stream.of(tokenInterval.getStart(), tokenInterval.getStop()))
			.distinct()
			.collect(groupingBy(token -> token.getTokenIndex(), reducing(null, (oldToken, newToken) -> oldToken == null ? newToken : oldToken)));
	}

	public IntervalSet intervals(List<TemplateExpression> expressions) {
		return expressions.stream()
			.flatMap(expr -> locations.get(expr).stream())
			.filter(Objects::nonNull)
			.map(tokenInterval -> tokenInterval.getInterval())
			.collect(IntervalSet::new, (intervals, interval) -> intervals.add(interval.a, interval.b), (interval1, interval2) -> interval1.addAll(interval2));
	}

	@Override
	public TemplateGroup compileLibrary(String name, String resource, InputStream stream, TemplateLoader loader) throws IOException {
		return new CoverageBuilder(name, resource, loader)
			.parseGroup(CharStreams.fromStream(stream)).buildGroup();
	}

	@Override
	public TemplateDefinition compileMain(String name, String resource, InputStream stream, TemplateLoader loader) throws IOException {
		return new CoverageBuilder(name, resource, loader)
			.parseMain(CharStreams.fromStream(stream)).buildMain();
	}

	public boolean isCoverableExpression(TemplateExpression expression) {
		return expression != null && !(expression instanceof EvalAnonymousTemplate);
	}

	@Override
	public void notify(Scope scope, TemplateExpression source, TemplateImmediateExpression result) {
		Set<TokenInterval> covered = locations.computeIfAbsent(source, exp -> new LinkedHashSet<>());
		
		covered.removeAll(this.covered);
		
		this.uncovered.removeAll(covered);
		
		this.covered.addAll(covered);
	}

	private class CoverageBuilder extends TemplateGroupBuilder {

		public CoverageBuilder(String name, String resource, TemplateLoader loader) throws IOException {
			super(name, resource, loader);
			groups.add(name);
		}

		public TemplateGroupNode visit(ParseTree tree) {
			TemplateGroupNode result = super.visit(tree);
			TemplateExpression expression = result.as(TemplateExpression.class);
			if (isCoverableExpression(expression) && tree instanceof ParserRuleContext) {
				ParserRuleContext ruleContext = (ParserRuleContext) tree;
				Interval coverage = ruleContext.getSourceInterval();
				TokenInterval interval = new TokenInterval(getName(), coverage, ruleContext.start, ruleContext.stop);
				if (!covered.contains(interval)) {
					uncovered.add(interval);
				}
				locations.computeIfAbsent(expression, exp -> new LinkedHashSet<>()).add(interval);
			}
			return result;
		}

	}
}
