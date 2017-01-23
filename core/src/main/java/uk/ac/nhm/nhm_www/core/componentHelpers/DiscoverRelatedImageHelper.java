package uk.ac.nhm.nhm_www.core.componentHelpers;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.xss.XSSAPI;
import com.day.cq.commons.ImageResource;
import com.day.cq.dam.api.Asset;

import uk.ac.nhm.nhm_www.core.utils.LinkUtils;
import uk.ac.nhm.nhm_www.core.utils.NodeUtils;

public class DiscoverRelatedImageHelper {
	private String path;
	private String originalImagePath;
	private String extension;
	private String suffix;
	private String alt;
	private String caption;
	private String imageLinkURL;
	private String mobileImagePath;
	private Boolean addMarginBottom; // WR-890
	private boolean newwindow;
	private Boolean activated;
	private Resource rs;
	private Node parentNode;
	private Asset asset;
	private ResourceResolver resourceResolver;
	public enum ImageInterchangeSize {DEFAULT, SMALL, MEDIUM, LARGE, RETINA, SMALL_RETINA}
	
	protected static final Logger logger = LoggerFactory.getLogger(DiscoverRelatedImageHelper.class);

	public DiscoverRelatedImageHelper(ValueMap properties, Resource resource, ResourceResolver resourceResolver, HttpServletRequest request, XSSAPI xssAPI) {
		
		this.activated = false;
		
		this.addMarginBottom = false; // WR-890
		//this.rs = resource;
		this.resourceResolver = resourceResolver;
		// WR-890 - set the addMarginBottom Boolean
		this.addMarginBottom = properties.get("addMarginBottom",false);
		
		String fileReference = properties.get("fileReference", "");
		String mobileFileReference = properties.get("mobileFileReference", "");
		
		logger.error("mobile File Rdeference: " + mobileFileReference);
		this.originalImagePath = fileReference;
		this.mobileImagePath = mobileFileReference;
		if (fileReference.length() != 0 || resource.getChild("file") != null) {
			ImageResource image = new ImageResource(resource);
			String tempPath = request.getContextPath() + resource.getPath();
			String tempAlt = xssAPI.encodeForHTMLAttr(properties.get("alt", image.getTitle()));
			
			// Handle extensions on both fileReference and file type images
			String tempExtension = "jpg";
			String tempSuffix = "";
			if (fileReference.length() != 0) {
				tempExtension = fileReference.substring(fileReference.lastIndexOf(".") + 1);
				tempSuffix = image.getSuffix();
				tempSuffix = tempSuffix.substring(0, tempSuffix.indexOf('.') + 1) + tempExtension;
			} else {
				Resource fileJcrContent = resource.getChild("file").getChild("jcr:content");
				if (fileJcrContent != null) {
					ValueMap fileProperties = fileJcrContent.adaptTo(ValueMap.class);
					String mimeType = fileProperties.get("jcr:mimeType", "jpg");
					tempExtension = mimeType.substring(mimeType.lastIndexOf("/") + 1);
				}
			}
			
			/*if (mobileFileReference.length() != 0) {
				tempExtension = mobileFileReference.substring(fileReference.lastIndexOf(".") + 1);
				tempSuffix = image.getSuffix();
				tempSuffix = tempSuffix.substring(0, tempSuffix.indexOf('.') + 1) + tempExtension;
			} */
			tempExtension = xssAPI.encodeForHTMLAttr(tempExtension);
			this.caption = properties.get("caption","");
			this.imageLinkURL = properties.get("image-path","");
			if(this.imageLinkURL != null && !this.imageLinkURL.equals("")){
				this.imageLinkURL = LinkUtils.getFormattedLink(this.imageLinkURL);
			}
			if (properties.get("newwindow") != null) {
				this.newwindow = properties.get("newwindow",false);
			}
			this.path= tempPath;
			this.extension = tempExtension;
			this.suffix = tempSuffix;
			this.alt = tempAlt;
			this.activated = true;
		}
	}
	
	private int getImageHeight() {
		return Integer.parseInt(this.getAsset().getMetadataValue("tiff:imageLength"));
	}
	
	private int getImageWidth() {
		String damAssetPath = this.getAsset().getPath();
		Resource  imageMedataData = this.resourceResolver.getResource(damAssetPath+"/jcr:content/metadata");
		ValueMap imageProperies = imageMedataData.getValueMap();
		String imageWidth = imageProperies.get("tiff:ImageWidth", "0");
		return Integer.parseInt(imageWidth);
	}
	
	private Asset getAsset() {
		if (this.asset == null)
		{
			String path = this.getPath();
			String extension = this.getExtension();
			String suffix = this.getSuffix();

			this.rs = this.resourceResolver.getResource(this.originalImagePath);
			String imagePath = path + extension + suffix;
			this.asset = rs.adaptTo(Asset.class);
		}
		return this.asset;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public String getOriginalImagePath() {
		return originalImagePath;
	}

	public void setOriginalImagePath(String originalImagePath) {
		this.originalImagePath = originalImagePath;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}


	public String getSuffix() {
		return suffix;
	}


	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}


	public String getAlt() {
		return alt;
	}


	public void setAlt(String alt) {
		this.alt = alt;
	}


	public Boolean isActivated() {
		return activated;
	}


	public void setActivated(Boolean activated) {
		this.activated = activated;
	}


	public String getCaption() {
		return caption;
	}


	public void setCaption(String caption) {
		this.caption = caption;
	}


	public String getImageLinkURL() {
		return imageLinkURL;
	}


	public void setImageLinkURL(String imageLinkURL) {
		this.imageLinkURL = imageLinkURL;
	}	
	
	public String getNewWindowHtml() {
		if (this.newwindow)
		{	
			return " target=\"_blank\"";
		}
		else
		{
			return "";
		}
	}
	
	// WR-890
	public Boolean getAddMarginBottom() {
		return addMarginBottom;
	}
	
	// WR-890
	public void setAddMarginBottom(Boolean addMarginBottom) {
		this.addMarginBottom = addMarginBottom;
	}
	
	private boolean hasMobileImage(String path) {
		
		return false;
	}


	public String getMobileImagePath() {
		/*String mobilePath = this.originalImagePath.substring(0, this.originalImagePath.lastIndexOf(".")) + "_mobile." + this.extension;
		
		return mobilePath;*/
		return this.mobileImagePath;
	}

	public void setMobileImagePath(String mobileImagePath) {
		this.mobileImagePath = mobileImagePath;
	}
	
	
	
	
		
}
