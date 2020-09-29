package com.herbalife.nutrition.sites.core.models.impl;

import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobe.cq.dam.cfm.FragmentTemplate;
import com.day.cq.dam.api.Asset;
import com.herbalife.nutrition.sites.core.models.Automation;
import com.herbalife.nutrition.sites.core.services.AutomationService;

@Model(adaptables = { SlingHttpServletRequest.class }, adapters = { Automation.class }, resourceType = {
		AutomationImpl.RESOURCE_TYPE }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AutomationImpl implements Automation {
	protected static final String RESOURCE_TYPE = "herbalifenutrition/components/content/automation";
	@SlingObject
	private ResourceResolver resourceResolver;
	@ValueMapValue
	private String type;
	@ValueMapValue
	private String filePath;
	@ValueMapValue
	private String rootPagePath;
	@ValueMapValue
	private String templateFolderPath;
	@ValueMapValue
	private String metaTitleSuffix;
	@ValueMapValue
	private String assetFolderPath;
	@ValueMapValue
	private String contentRequestor;
	@ValueMapValue
	private String region;
	@ValueMapValue
	private String locale;
	@ValueMapValue
	private Date expiryDate;
	@ValueMapValue
	private String cfType;
	@ValueMapValue
	private String fragmentModelPath;
	@ValueMapValue
	private String fragmentsFolderPath;
	@ValueMapValue
	private String parentPagePath;
	@ValueMapValue
	private String imageFolderPath;
	@ValueMapValue
	private String rootTagPath;
	private Asset fileAsset;
	private FragmentTemplate fragmentTemplate;
	@Inject
	private AutomationService automationService;

	@Override
	public String getMessage() {
		if (type.equals("page"))
			return "Create empty pages using file at path: " + filePath;
		else if (type.equals("asset"))
			return "Update image asset properties using file at path: " + filePath;
		else {
			if (cfType.equals("product"))
				return "Create product content fragments using file at path: " + filePath;
			else if (cfType.equals("article"))
				return "Create article content fragments using file at path: " + filePath;
			else
				return "Create recipe content fragments using file at path: " + filePath;
		}
	}

	private Asset getFileAsset(String path) {
		fileAsset = resourceResolver.getResource(path).adaptTo(Asset.class);
		if (fileAsset != null
				&& fileAsset.getMimeType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
			return fileAsset;
		else
			return null;
	}

	private boolean checkNodeType(String path, String nodeType) {
		Node node;
		String primaryType = null;
		try {
			node = JcrUtils.getNodeIfExists(path, resourceResolver.adaptTo(Session.class));
			primaryType = node.getProperty("jcr:primaryType").getValue().getString();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		if (primaryType.endsWith(nodeType))
			return true;
		else
			return false;
	}

	private FragmentTemplate getfragmentTemplate(String path) {
		fragmentTemplate = resourceResolver.getResource(path).adaptTo(FragmentTemplate.class);
		if (fragmentTemplate != null)
			return fragmentTemplate;
		else
			return null;
	}

	@Override
	public boolean isEmpty() {
		if (StringUtils.isAnyBlank(type, filePath) || getFileAsset(filePath) == null)
			return true;
		else if (type.equals("page") && (StringUtils.isAnyBlank(rootPagePath, templateFolderPath, metaTitleSuffix)
				|| !checkNodeType(rootPagePath, "cq:Page") || !checkNodeType(templateFolderPath, "cq:Page")))
			return true;
		else if (type.equals("asset") && (StringUtils.isAnyBlank(assetFolderPath, contentRequestor, region, locale)
				|| !checkNodeType(assetFolderPath, "Folder") || expiryDate == null))
			return true;
		else if (type.equals("contentFragment") && (StringUtils.isAnyBlank(fragmentModelPath, fragmentsFolderPath,
				parentPagePath, imageFolderPath, rootTagPath) || getfragmentTemplate(fragmentModelPath) == null
				|| !checkNodeType(fragmentsFolderPath, "Folder") || !checkNodeType(parentPagePath, "cq:Page")
				|| !checkNodeType(imageFolderPath, "Folder") || !checkNodeType(rootTagPath, "cq:Tag")))
			return true;
		else
			return false;
	}

	@Override
	public boolean isDone() {
		if (type.equals("page"))
			return automationService.createPages(fileAsset, rootPagePath, templateFolderPath, metaTitleSuffix);
		else if (type.equals("asset"))
			return automationService.updateImageAssetProperties(fileAsset, assetFolderPath, contentRequestor, region,
					locale, expiryDate);
		else
			return automationService.createContentFragments(fileAsset, cfType, fragmentTemplate, fragmentsFolderPath,
					parentPagePath, imageFolderPath, rootTagPath);
	}

	@Override
	public String getTime() {
		long time = automationService.getConversionTime();
		if (time < 1000)
			return "Conversion done in " + time + "ms";
		else {
			long seconds = time / 1000;
			if (seconds < 60)
				return "Conversion done in " + seconds + "s";
			else
				return "Conversion done in " + seconds / 60 + "m : " + seconds % 60 + "s";
		}
	}

	@Override
	public int getTotalCount() {
		return automationService.getTotalCount();
	}

	@Override
	public int getSuccessCount() {
		return automationService.getSuccessCount();
	}

	@Override
	public ArrayList<String> getErrorList() {
		return automationService.getErrorList();
	}
}
