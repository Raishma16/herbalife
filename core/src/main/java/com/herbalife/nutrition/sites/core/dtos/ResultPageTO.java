package com.herbalife.nutrition.sites.core.dtos;

public class ResultPageTO {
	private String label;
	private long offset;
	private boolean active;

	public ResultPageTO(long offset, String label, boolean active) {
		this.label = label;
		this.offset = offset;
		this.active = active;
	}

	public long getOffset() {
		return offset;
	}

	public String getLabel() {
		return label;
	}

	public boolean isActive() {
		return active;
	}

}
