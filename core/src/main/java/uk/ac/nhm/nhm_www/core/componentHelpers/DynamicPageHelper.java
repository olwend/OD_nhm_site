package uk.ac.nhm.nhm_www.core.componentHelpers;

import javax.servlet.http.HttpServletRequest;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import com.day.cq.wcm.api.Page;

public class DynamicPageHelper {
	private Resource resource;
	private ValueMap properties;
	private HttpServletRequest request;
	private String pageIntroduction;
	private String legacyApp;
	private String protocol;
	private Boolean defaultLegacyCSS;
	
	public DynamicPageHelper(Resource resource,ValueMap properties, HttpServletRequest request)
	{
//		this.image = getProperties().get("image", String.class);
		setResource(resource);
		setProperties(properties);
		setRequest(request);
		init();
	}

	public DynamicPageHelper(Page page) {
		Resource pageResource = page.adaptTo(Resource.class);
		setResource(pageResource);
		setProperties(page.getProperties());;
		init();
	}
	private void init() {
		String pageIntroduction = "";
		if(getProperties().get("pageIntroduction") !=null) {
			pageIntroduction = getProperties().get("pageIntroduction", String.class);
		}
		setPageIntroduction(pageIntroduction);	
		String legacyApp = "";
		if(getProperties().get("legacy-app") !=null) {
			legacyApp = getProperties().get("legacy-app", String.class);
		}
		setLegacyApp(legacyApp);
		this.defaultLegacyCSS = getProperties().get("defaultLegacyCSS", true);
		if(this.request != null) { 
			this.protocol = request.getProtocol();
		}
		
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public ValueMap getProperties() {
		return properties;
	}

	public void setProperties(ValueMap properties) {
		this.properties = properties;
	}
	
	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getPageIntroduction() {
		return pageIntroduction;
	}

	public void setPageIntroduction(String pageIntroduction) {
		this.pageIntroduction = pageIntroduction;
	}

	public String getLegacyApp() {
		return legacyApp;
	}

	public void setLegacyApp(String legacyApp) {
		this.legacyApp = legacyApp;
	}

	public Boolean isDefaultLegacyCSS() {
		return defaultLegacyCSS;
	}

	public void setDefaultLegacyCSS(Boolean defaultLegacyCSS) {
		this.defaultLegacyCSS = defaultLegacyCSS;
	}

	public String getProtocol() {
		if (this.protocol != null && this.protocol.startsWith("HTTPS")) {
			return "https://";
		} else {
			return "http://";
		}
		
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	
	
	
	
	
	
	
}
