package com.herbalife.nutrition.sites.core.dtos;

public class FilterOptionTO {
	private String label;
	private String value;
	private boolean active;

	public FilterOptionTO(String value, String label, boolean active) {
		this.label = label;
		this.value = value;
		this.active = active;
	}

	public String getLabel() {
		return label;
	}

	public String getValue() {
		return value;
	}

	public boolean isActive() {
		return active;
	}
}
