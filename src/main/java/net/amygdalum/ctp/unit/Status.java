package net.amygdalum.ctp.unit;

public enum Status {
	SUCCESS, IGNORE, FAILURE(true), ERROR(true);

	private boolean reportDescription;
	
	private Status(boolean reportDescription) {
		this.reportDescription = reportDescription;
	}
	
	private Status() {
		this(false);
	}
	
	public boolean reportDescription() {
		return reportDescription;
	}

}
