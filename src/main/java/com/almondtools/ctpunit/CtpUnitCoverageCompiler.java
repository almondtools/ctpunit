package com.almondtools.ctpunit;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;

import com.almondtools.comtemplate.engine.InterpreterListener;
import com.almondtools.comtemplate.engine.TemplateCompiler;
import com.almondtools.comtemplate.engine.TemplateExpression;
import com.almondtools.comtemplate.engine.TemplateGroup;
import com.almondtools.comtemplate.engine.TemplateImmediateExpression;
import com.almondtools.comtemplate.engine.TemplateLoader;
import com.almondtools.comtemplate.engine.expressions.EvalAnonymousTemplate;
import com.almondtools.comtemplate.parser.TemplateGroupBuilder;
import com.almondtools.comtemplate.parser.TemplateGroupNode;

public class CtpUnitCoverageCompiler implements TemplateCompiler, InterpreterListener {

	private Map<TemplateExpression, TokenInterval> locations;
	private Map<String, Set<TemplateExpression>> coverableByGroup;
	private Set<TemplateExpression> coverage;

	public CtpUnitCoverageCompiler() {
		this.locations = new IdentityHashMap<>();
		this.coverableByGroup = new LinkedHashMap<>();
		this.coverage = new LinkedHashSet<>();
	}

	public void dumpCoverage(PrintWriter writer) {
		for (String group : getGroupsInScope()) {
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
		Set<TemplateExpression> uncovered = new LinkedHashSet<>(coverableByGroup.get(group));
		uncovered.removeAll(coverage);

		Map<Integer, Token> tokens = tokens(uncovered);
		IntervalSet intervals = intervals(uncovered);

		return intervals.getIntervals().stream()
			.map(interval -> new TokenInterval(interval, tokens.get(interval.a), tokens.get(interval.b)))
			.collect(toList());
	}

	public List<TokenInterval> getCovered(String group) {
		Set<TemplateExpression> covered = new LinkedHashSet<>(coverableByGroup.get(group));
		covered.retainAll(coverage);

		Map<Integer, Token> tokens = tokens(covered);
		IntervalSet intervals = intervals(covered);

		return intervals.getIntervals().stream()
			.map(interval -> new TokenInterval(interval, tokens.get(interval.a), tokens.get(interval.b)))
			.collect(toList());
	}

	public Map<Integer, Token> tokens(Set<TemplateExpression> expressions) {
		return expressions.stream()
			.map(expr -> locations.get(expr))
			.filter(Objects::nonNull)
			.flatMap(tokenInterval -> Stream.of(tokenInterval.getStart(), tokenInterval.getStop()))
			.distinct()
			.collect(groupingBy(token -> token.getTokenIndex(), reducing(null, (oldToken, newToken) -> oldToken == null ? newToken : oldToken)));
	}

	public IntervalSet intervals(Set<TemplateExpression> expressions) {
		return expressions.stream()
			.map(expr -> locations.get(expr))
			.filter(Objects::nonNull)
			.map(tokenInterval -> tokenInterval.getInterval())
			.collect(IntervalSet::new, (intervals, interval) -> intervals.add(interval.a, interval.b), (interval1, interval2) -> interval1.addAll(interval2));
	}

	public Set<String> getGroupsInScope() {
		return coverableByGroup.keySet();
	}

	@Override
	public TemplateGroup compile(String name, InputStream stream, TemplateLoader loader) throws IOException {
		return new TemplateGroupBuilder(name, stream, loader) {
			public TemplateGroupNode visit(ParseTree tree) {
				TemplateGroupNode result = super.visit(tree);
				TemplateExpression expression = result.as(TemplateExpression.class);
				if (isCoverableExpression(expression) && tree instanceof ParserRuleContext) {
					ParserRuleContext ruleContext = (ParserRuleContext) tree;
					Interval coverage = ruleContext.getSourceInterval();
					coverableByGroup.compute(name, mergeCoverage(expression));
					locations.put(expression, new TokenInterval(coverage, ruleContext.start, ruleContext.stop));
				}
				return result;
			}

		}.build();
	}

	public boolean isCoverableExpression(TemplateExpression expression) {
		return expression != null && !(expression instanceof EvalAnonymousTemplate);
	}

	@Override
	public void notify(TemplateExpression source, TemplateImmediateExpression result) {
		coverage.add(source);
	}

	private BiFunction<String, Set<TemplateExpression>, Set<TemplateExpression>> mergeCoverage(TemplateExpression expr) {
		return (group, existing) -> {
			if (existing == null) {
				return Stream.of(expr)
					.collect(toSet());
			} else {
				return Stream.concat(existing.stream(), Stream.of(expr))
					.collect(toSet());
			}
		};
	}

	public RunListener coverageListener() {
		return new RunListener() {
			@Override
			public void testRunFinished(Result result) throws Exception {
				dumpCoverage(new PrintWriter(System.out));
			}
		};
	}

}
