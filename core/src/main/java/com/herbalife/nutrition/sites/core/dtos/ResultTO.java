package com.herbalife.nutrition.sites.core.dtos;

public class ResultTO {
	private String title;
	private String description;
	private String path;
	
	private String label;
	private String the_link;

	public ResultTO(String title, String description, String path) {
		this.title = title;
		this.description = description;
		this.path = path;
	}
	
	public ResultTO(String label,  String the_link) {
		this.label = label;
		this.the_link = the_link;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getPath() {
		return path;
	}
	
	public String getLabel() {
		return label;
	}
	
	public String getThe_link() {
		return the_link;
	}

}
