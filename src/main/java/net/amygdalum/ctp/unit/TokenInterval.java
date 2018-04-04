package net.amygdalum.ctp.unit;

import static java.util.Comparator.naturalOrder;

import java.util.Objects;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;

public class TokenInterval implements Comparable<TokenInterval>{

	private String group;
	private Interval interval;
	private Token start;
	private Token stop;

	public TokenInterval(String group, Interval interval, Token start, Token stop) {
		this.group = group;
		this.interval = interval;
		this.start = start;
		this.stop = stop;
	}
	
	public String getGroup() {
		return group;
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
			.append(stop.getCharPositionInLine() + stop.getText().length() + 1 )
			.toString();
	}

	@Override
	public int hashCode() {
		return group.hashCode()
			+ interval.hashCode() * 7;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TokenInterval that = (TokenInterval) obj;
		return Objects.equals(this.group,that.group)
			&& Objects.equals(this.interval,  that.interval);
	}
	
	@Override
	public int compareTo(TokenInterval that) {
		int compare = Objects.compare(this.group, that.group, naturalOrder());
		if (compare == 0) {
			compare = Integer.compare(this.interval.a, that.interval.a);
		}
		if (compare == 0) {
			compare = Integer.compare(this.interval.b, that.interval.b);
		}
		return compare;
	}

	public boolean belongsTo(String group) {
		return this.group.equals(group);
	}
	
	
}