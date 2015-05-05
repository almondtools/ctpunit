package com.almondtools.ctpunit;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;

public class TokenInterval {

	private Interval interval;
	private Token start;
	private Token stop;

	public TokenInterval(Interval interval, Token start, Token stop) {
		this.interval = interval;
		this.start = start;
		this.stop = stop;
	}

	public Interval getInterval() {
		return interval;
	}

	public Token getStart() {
		return start;
	}

	public Token getStop() {
		return stop;
	}

	@Override
	public String toString() {
		return position(start) + " - " + position(stop);
	}

	public String position(Token token) {
		return new StringBuilder()
			.append(token.getLine())
			.append(':')
			.append(token.getCharPositionInLine())
			.toString();
	}

}