package com.herbalife.nutrition.sites.core.services.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import javax.jcr.Node;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.FragmentTemplate;
import com.day.cq.dam.api.Asset;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.Template;
import com.day.cq.wcm.api.TemplateManager;
import com.herbalife.nutrition.sites.core.services.AutomationService;

@Component
public class AutomationServiceImpl implements AutomationService {
	@Reference
	private ResourceResolverFactory resolverFactory;
	private int totalCount;
	private int successCount;
	private ArrayList<String> errorList;
	private long startTime = 0;
	private long endTime = 0;

	@Override
	public boolean createPages(Asset fileAsset, String rootPagePath, String templateFolderPath,
			String metaTitleSuffix) {
		ResourceResolver resolver = null;
		startTime = System.currentTimeMillis();
		totalCount = 0;
		successCount = 0;
		errorList = new ArrayList<>();
		try {
			resolver = resolverFactory.getAdministrativeResourceResolver(null);
			InputStream iStream = fileAsset.getOriginal().getStream();
			XSSFWorkbook workbook = new XSSFWorkbook(iStream);
			Sheet sheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = sheet.rowIterator();
			iterator.next();
			PageManager pageManager = resolver.adaptTo(PageManager.class);
			TemplateManager templateManager = resolver.adaptTo(TemplateManager.class);
			String pageNameValidChars = "abcdefghijklmnopqrstuvwxyz1234567890-";
			while (iterator.hasNext()) {
				totalCount++;
				Row row = iterator.next();
				String pageId = row.getCell(0).getStringCellValue();
				String relPath = row.getCell(1).getStringCellValue();
				int index = relPath.lastIndexOf("/");
				String parentPagePath = rootPagePath + relPath.substring(0, index);
				Page parentPage = pageManager.getPage(parentPagePath);
				if (parentPage == null)
					errorList.add("Page ID.: " + pageId + " - Invalid parent page path");
				String templateName = row.getCell(2).getStringCellValue().toLowerCase().replace(" ", "-");
				String templatePath = templateFolderPath + "/" + templateName;
				Template template = templateManager.getTemplate(templatePath);
				if (template == null)
					errorList.add("Page ID.: " + pageId + " - Invalid template path");
				String pageName = relPath.substring(index + 1);
				boolean isNameValid = StringUtils.containsOnly(pageName, pageNameValidChars);
				if (!isNameValid)
					errorList.add("Page ID.: " + pageId + " - Invalid page name");
				if (parentPage != null && template != null && isNameValid) {
					Page page = pageManager.getPage(rootPagePath + relPath);
					String pageTitle = row.getCell(3).getStringCellValue();
					String metaTitle = pageTitle + metaTitleSuffix;
					if (page == null)
						page = pageManager.create(parentPagePath, pageName, templatePath, metaTitle);
					if (page != null) {
						ModifiableValueMap contentMap = page.getContentResource().adaptTo(ModifiableValueMap.class);
						contentMap.put("cq:template", templatePath);
						contentMap.put("jcr:title", metaTitle);
						contentMap.put("metaTitle", metaTitle);
						contentMap.put("pageTitle", pageTitle);
						contentMap.put("navTitle", pageTitle);
						contentMap.put("jcr:description", row.getCell(4).getStringCellValue());
						if (row.getCell(5).getBooleanCellValue())
							contentMap.put("robots-noindex", "noindex");
						if (row.getCell(6).getBooleanCellValue())
							contentMap.put("robots-nofollow", "nofollow");
						resolver.commit();
						successCount++;
					} else
						errorList.add("Page ID.: " + pageId + " - Reason unknown");
				}
			}
			workbook.close();
			resolver.close();
			endTime = System.currentTimeMillis();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			errorList.add("Some exception has occured. Please check the log files.");
		}
		return true;
	}

	@Override
	public boolean updateImageAssetProperties(Asset fileAsset, String assetFolderPath, String contentRequestor,
			String region, String locale, Date expiryDate) {
		ResourceResolver resolver = null;
		startTime = System.currentTimeMillis();
		totalCount = 0;
		successCount = 0;
		errorList = new ArrayList<>();
		try {
			resolver = resolverFactory.getAdministrativeResourceResolver(null);
			InputStream iStream = fileAsset.getOriginal().getStream();
			XSSFWorkbook workbook = new XSSFWorkbook(iStream);
			Sheet sheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = sheet.rowIterator();
			iterator.next();
			while (iterator.hasNext()) {
				totalCount++;
				Row row = iterator.next();
				String folderName = row.getCell(0).getStringCellValue().toLowerCase().replace(" ", "-");
				String imageName = row.getCell(1).getStringCellValue();
				String parentFolderPath = assetFolderPath + "/" + folderName;
				Resource parentFoldeResource = resolver.getResource(parentFolderPath);
				if (parentFoldeResource != null) {
					Node parentFolderNode = parentFoldeResource.adaptTo(Node.class);
					if (parentFolderNode.getProperty("jcr:primaryType").getValue().getString().endsWith("Folder")) {
						Resource imageResource = parentFoldeResource.getChild(imageName);
						if (imageResource != null) {
							Asset imageAsset = imageResource.adaptTo(Asset.class);
							if (imageAsset != null && imageAsset.getMimeType().startsWith("image")) {
								Resource metadataResource = imageResource.getChild("jcr:content/metadata");
								ModifiableValueMap metadataMap = metadataResource.adaptTo(ModifiableValueMap.class);
								metadataMap.put("dc:title", row.getCell(2).getStringCellValue());
								metadataMap.put("dc:description", row.getCell(3).getStringCellValue());
								String[] keywords = StringUtils
										.splitByWholeSeparator(row.getCell(4).getStringCellValue(), ", ");
								if (folderName.equals("product-images"))
									metadataMap.put("dc:subject", keywords);
								else {
									metadataMap.put("dam:search_promote", keywords);
									metadataMap.put("hl.requestor", contentRequestor);
									metadataMap.put("hl.region", region.toLowerCase());
									metadataMap.put("hl.locale", locale);
									Calendar calendar = Calendar.getInstance();
									calendar.setTime(expiryDate);
									metadataMap.put("prism:expirationDate", calendar);
								}
								if (row.getCell(5) != null)
									metadataMap.put("dc:secondaryTitle", row.getCell(5).getStringCellValue());
								if (row.getCell(6) != null)
									metadataMap.put("dc:secondaryTitle", row.getCell(6).getStringCellValue());
								resolver.commit();
								successCount++;
							} else
								errorList.add("Image Name: " + imageName + " - Invalid image type");
						} else
							errorList.add("Image Name: " + imageName + " - Invalid image name");
					} else
						errorList.add("Image Name: " + imageName + " - Invalid folder type");
				} else
					errorList.add("Image Name: " + imageName + " - Invalid folder name");
			}
			workbook.close();
			resolver.close();
			endTime = System.currentTimeMillis();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			errorList.add("Some exception has occured. Please check the log files.");
		}
		return false;
	}

	@Override
	public boolean createContentFragments(Asset fileAsset, String cfType, FragmentTemplate fragmentTemplate,
			String fragmentsFolderPath, String parentPagePath, String imageFolderPath, String rootTagPath) {
		ResourceResolver resolver = null;
		startTime = System.currentTimeMillis();
		totalCount = 0;
		successCount = 0;
		errorList = new ArrayList<>();
		try {
			resolver = resolverFactory.getAdministrativeResourceResolver(null);
			InputStream iStream = fileAsset.getOriginal().getStream();
			XSSFWorkbook workbook = new XSSFWorkbook(iStream);
			Sheet sheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = sheet.rowIterator();
			iterator.next();
			Resource parentFolderResource = resolver.getResource(fragmentsFolderPath);
			String cfNameValidChars = "abcdefghijklmnopqrstuvwxyz1234567890-_";
			TagManager tagManager = resolver.adaptTo(TagManager.class);
			Date today = new Date(startTime);
			while (iterator.hasNext()) {
				totalCount++;
				Row row = iterator.next();
				String cfName = row.getCell(0).getStringCellValue();
				String cfTitle = row.getCell(1).getStringCellValue();
				if (StringUtils.containsOnly(cfName, cfNameValidChars)) {
					ContentFragment contentFragment = null;
					Resource cfResource = parentFolderResource.getChild(cfName);
					if (cfResource != null) {
						contentFragment = cfResource.adaptTo(ContentFragment.class);
						if (contentFragment != null)
							contentFragment.setTitle(cfTitle);
					} else
						contentFragment = fragmentTemplate.createFragment(parentFolderResource, cfName, cfTitle);
					if (contentFragment != null) {
						if (row.getCell(2) != null)
							contentFragment.setDescription(row.getCell(2).getStringCellValue());
						if (row.getCell(3) != null) {
							String[] tagTitles = StringUtils.splitByWholeSeparator(row.getCell(3).getStringCellValue(),
									", ");
							Tag[] validTags = null;
							String invalidTags = "";
							for (String tagTitle : tagTitles) {
								String tagName = tagTitle.toLowerCase().replace(" ", "-");
								Tag tag = tagManager.resolve(rootTagPath + "/" + tagName);
								if (tag != null)
									validTags = ArrayUtils.add(validTags, tag);
								else
									invalidTags += tagTitle + ", ";
							}
							if (!ArrayUtils.isEmpty(validTags)) {
								Resource metadataResource = contentFragment.adaptTo(Resource.class)
										.getChild("jcr:content/metadata");
								tagManager.setTags(metadataResource, validTags);
							}
							if (invalidTags.length() > 0) {
								invalidTags = invalidTags.substring(0, invalidTags.lastIndexOf(", "));
								errorList.add("CF Name: " + cfName + " - Invalid tags: " + invalidTags);
							}
						}
						Resource dataMasterResource = parentFolderResource
								.getChild(cfName + "/jcr:content/data/master");
						ModifiableValueMap masterDataMap = dataMasterResource.adaptTo(ModifiableValueMap.class);
						masterDataMap.put("title-text", row.getCell(4).getStringCellValue());
						String pagePath = parentPagePath + "/" + row.getCell(5).getStringCellValue();
						Resource pageResource = resolver.getResource(pagePath);
						if (pageResource != null) {
							Page page = pageResource.adaptTo(Page.class);
							if (page != null) {
								masterDataMap.put("title-text-link", pagePath);
								if (StringUtils.equalsAny(cfType, "article", "recipe"))
									masterDataMap.put("readmore-text-link", pagePath);
							} else
								errorList.add("CF Name: " + cfName + " - Invalid page type");
						} else
							errorList.add("CF Name: " + cfName + " - Invalid page name");
						String imagePath = imageFolderPath + "/" + row.getCell(6).getStringCellValue();
						Resource imageResource = resolver.getResource(imagePath);
						if (imageResource != null) {
							Asset imageAsset = imageResource.adaptTo(Asset.class);
							if (imageAsset != null && imageAsset.getMimeType().startsWith("image"))
								masterDataMap.put(cfType + "-image", imagePath);
							else
								errorList.add("CF Name: " + cfName + " - Invalid image type");
						} else
							errorList.add("CF Name: " + cfName + " - Invalid image name");
						if (cfType.equals("product")) {
							if (row.getCell(7) != null)
								masterDataMap.put("sku", row.getCell(7).getStringCellValue());
							if (row.getCell(8) != null)
								masterDataMap.put("price", row.getCell(8).getStringCellValue());
							if (row.getCell(9) != null)
								masterDataMap.put("button-text-link", row.getCell(9).getStringCellValue());
							if (row.getCell(10) != null)
								masterDataMap.put("product-overview", row.getCell(10).getStringCellValue());
						} else if (cfType.equals("article")) {
							if (row.getCell(7) != null) {
								Date datePublished = row.getCell(7).getDateCellValue();
								if (datePublished.before(today)) {
									Calendar calendar = Calendar.getInstance();
									calendar.setTime(datePublished);
									masterDataMap.put("date-published", calendar);
								} else
									errorList.add("CF Name: " + cfName + " - Invalid date");
							}
							if (row.getCell(8) != null)
								masterDataMap.put("article-author", row.getCell(8).getStringCellValue());
							if (row.getCell(9) != null)
								masterDataMap.put("article-description", row.getCell(9).getStringCellValue());
							if (row.getCell(10) != null)
								masterDataMap.put("article-content", row.getCell(10).getStringCellValue());
						} else {
							if (row.getCell(7) != null)
								masterDataMap.put("recipe-author", row.getCell(7).getStringCellValue());
							if (row.getCell(8) != null)
								masterDataMap.put("short-description", row.getCell(8).getStringCellValue());
							if (row.getCell(9) != null)
								masterDataMap.put("long-description", row.getCell(9).getStringCellValue());
							if (row.getCell(10) != null)
								masterDataMap.put("serves", row.getCell(10).getStringCellValue());
							if (row.getCell(11) != null)
								masterDataMap.put("prep-time", row.getCell(11).getStringCellValue());
							if (row.getCell(12) != null)
								masterDataMap.put("cook-time", row.getCell(12).getStringCellValue());
							if (row.getCell(13) != null) {
								String[] ingredientsList = StringUtils
										.splitByWholeSeparator(row.getCell(13).getStringCellValue(), " | ");
								masterDataMap.put("ingredients-list", ingredientsList);
							}
							if (row.getCell(14) != null)
								masterDataMap.put("preparation", row.getCell(14).getStringCellValue());
							if (row.getCell(15) != null)
								masterDataMap.put("calories", row.getCell(15).getStringCellValue());
							if (row.getCell(16) != null)
								masterDataMap.put("fat", row.getCell(16).getStringCellValue());
							if (row.getCell(17) != null)
								masterDataMap.put("carbohydrates", row.getCell(17).getStringCellValue());
							if (row.getCell(18) != null)
								masterDataMap.put("sugar", row.getCell(18).getStringCellValue());
							if (row.getCell(19) != null)
								masterDataMap.put("fiber", row.getCell(19).getStringCellValue());
							if (row.getCell(20) != null)
								masterDataMap.put("protein", row.getCell(20).getStringCellValue());
							if (row.getCell(21) != null)
								masterDataMap.put("annotations", row.getCell(21).getStringCellValue());
						}
						resolver.commit();
						successCount++;
					} else
						errorList.add("CF Name: " + cfName + " - Reason unknown");
				} else
					errorList.add("CF Name: " + cfName + " - Invalid content fragment name");
			}
			workbook.close();
			resolver.close();
			endTime = System.currentTimeMillis();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			errorList.add("Some exception has occured. Please check the log files.");
		}
		return true;
	}

	@Override
	public long getConversionTime() {
		return (endTime - startTime);
	}

	@Override
	public int getTotalCount() {
		return totalCount;
	}

	@Override
	public int getSuccessCount() {
		return successCount;
	}

	@Override
	public ArrayList<String> getErrorList() {
		return errorList;
	}

}
