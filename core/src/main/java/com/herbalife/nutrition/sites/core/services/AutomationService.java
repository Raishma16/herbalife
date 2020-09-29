package com.herbalife.nutrition.sites.core.services;

import java.util.ArrayList;
import java.util.Date;

import com.adobe.cq.dam.cfm.FragmentTemplate;
import com.day.cq.dam.api.Asset;

public interface AutomationService {
	public boolean createPages(Asset fileAsset, String rootPagePath, String templateFolderPath, String metaTitleSuffix);

	public boolean createContentFragments(Asset fileAsset, String cfType, FragmentTemplate fragmentTemplate,
			String fragmentsFolderPath, String parentPagePath, String imageFolderPath, String rootTagPath);

	public boolean updateImageAssetProperties(Asset fileAsset, String assetFolderPath, String contentRequestor,
			String region, String locale, Date expiryDate);

	public long getConversionTime();

	public int getTotalCount();

	public int getSuccessCount();

	public ArrayList<String> getErrorList();
}
