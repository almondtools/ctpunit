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
		return position() + " - " + endPosition();
	}

	public String position() {
		return new StringBuilder()
			.append(start.getLine())
			.append(':')
			.append(start.getCharPositionInLine() + 1)
			.toString();
	}

	public String endPosition() {
		return new StringBuilder()
			.append(stop.getLine())
			.append(':')
			.append(stop.getCharPositionInLine() + 2)
			.toString();
	}
}