package com.herbalife.nutrition.sites.core.models;

import java.util.ArrayList;

public interface Automation {
	String getMessage();

	boolean isEmpty();

	boolean isDone();

	String getTime();

	int getTotalCount();

	int getSuccessCount();

	ArrayList<String> getErrorList();
}
