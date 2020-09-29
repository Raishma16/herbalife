package com.herbalife.nutrition.sites.core.models;

import java.util.List;

import com.herbalife.nutrition.sites.core.dtos.FilterOptionTO;
import com.herbalife.nutrition.sites.core.dtos.ResultPageTO;
import com.herbalife.nutrition.sites.core.dtos.ResultTO;

public interface Search {
	public String getSearchTerm();

	public List<ResultTO> getResults();

	public String getTitleText();

	public String getSubText();

	public List<ResultPageTO> getPagination();

	public List<FilterOptionTO> getContentTypes();

	public List<FilterOptionTO> getCategories();

	public boolean isEmpty();
}
