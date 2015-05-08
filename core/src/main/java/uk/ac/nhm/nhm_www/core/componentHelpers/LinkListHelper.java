package uk.ac.nhm.nhm_www.core.componentHelpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.nhm.nhm_www.core.utils.NodeUtils;

import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

public class LinkListHelper extends ListHelper {

	protected static final Logger LOG = LoggerFactory.getLogger(LinkListHelper.class);	
	
	private String numColumns;
	private String description;
	private String backgroundColor;
	private boolean isFullWidth = false;

	List<String> headersList = new ArrayList<String>();
	List<String> headersLinksList = new ArrayList<String>();	
	List<Boolean> headersLinksNewWindowsList = new ArrayList<Boolean>();
	List<Boolean> hasHeadersList = new ArrayList<Boolean>();
	List<String[]> columnItemsList = new ArrayList<String[]>();

	public LinkListHelper(ValueMap properties, PageManager pageManager, Page currentPage, HttpServletRequest request, ResourceResolver resourceResolver) {
		super(properties, pageManager, currentPage, request, resourceResolver);
	}
	
	@Override
	protected void init() {
		String emptyStr = StringUtils.EMPTY;
		Collections.addAll(headersList, emptyStr, emptyStr, emptyStr);
		Collections.addAll(headersLinksList, emptyStr, emptyStr, emptyStr);
		Collections.addAll(headersLinksNewWindowsList, false, false, false);
		Collections.addAll(hasHeadersList, false, false, false);
		
		// Links Description 
		if(this.properties.get("description", String.class) != null){
			LOG.error("Getting Properties > Description is empty" );
			this.description = this.properties.get("description", String.class);
			LOG.error("Getting Properties > Description should not be empty now and is : " + this.description);
		}
		
		// Number of Columns being used, probably will be removed
		if(this.properties.get("numColumns", String.class) != null){
			this.numColumns = this.properties.get("numColumns", String.class);
		}
		
		// Choose between white and grey background
		if(this.properties.get("backgroundcolor", String.class) != null){
			this.backgroundColor = this.properties.get("backgroundcolor", String.class);
		}
		
		// Links for the Three Columns
		if(this.properties.get("firstLinkListItems", String.class) != null){
			this.columnItemsList.set(0, this.properties.get("firstLinkListItems", String[].class));
			
			if(this.properties.get("secondLinkListItems", String.class) != null){
				this.columnItemsList.set(1, this.properties.get("secondLinkListItems", String[].class));
				
				if(this.properties.get("thirdLinkListItems", String.class) != null){
					this.columnItemsList.set(2, this.properties.get("thirdLinkListItems", String[].class));
				}
			}
		}
		
		// First Column	Header
		if(this.properties.get("firstHeader", String.class) != null){
			this.headersList.set(0, this.properties.get("firstHeader", String.class));
			this.headersLinksList.set(0, this.properties.get("firstHeaderLink", String.class));
			this.headersLinksNewWindowsList.set(0, this.properties.get("firstHeaderNewwindow", false));
			this.hasHeadersList.set(0, true);
			
			// Second Column
			if(this.properties.get("secondHeader", String.class) != null){
				this.headersList.set(1, this.properties.get("secondHeader", String.class));
				this.headersLinksList.set(1, this.properties.get("secondHeaderLink", String.class));
				this.headersLinksNewWindowsList.set(1, this.properties.get("secondHeaderNewwindow", false));
				this.hasHeadersList.set(1, true);
			}
			
			// Third Column
			if(this.properties.get("thirdHeader", String.class) != null){
				this.headersList.set(2, this.properties.get("thirdHeader", String.class));
				this.headersLinksList.set(2, this.properties.get("thirdHeaderLink", String.class));
				this.headersLinksNewWindowsList.set(2, this.properties.get("thirdHeaderNewwindow", false));
				this.hasHeadersList.set(2, true);
			}
		}
		super.init();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	
	public void setIsFullWidth(Resource resource) throws AccessDeniedException, ItemNotFoundException, RepositoryException {
		Node parentNode = resource.getParent().adaptTo(Node.class);
		if (NodeUtils.getRowType(parentNode) == NodeUtils.RowType.ROWFULLWIDTH) {
			this.isFullWidth = true;
		} else {
			this.isFullWidth = false;
		}
	}
	
	public int getWidthNumber() {
		int ret = 6;
		if(this.isFullWidth){
			ret = 12;
		} else if (numColumns.equals("firstcolumn")){
			ret = 4;
		}
		return ret;
	}
	
	public StringBuffer displayColumns() throws JSONException {
		init();
		StringBuffer columns = new StringBuffer();
		columns.append("<ul class=\"linklist--column-container"
				+ " small-block-grid-1"
				+ " medium-block-grid-" + getColumnStyles() + ""
				+ " large-block-grid-" + getColumnStyles() + "\">");
		Integer i = 0;
		Boolean exit = false;
		
		// Will exit if there are no links in columns 1 then 2 then 3.
		while (i <= 2 && !exit) {
			if(this.columnItemsList.get(i) != null){
				columns.append(addList(i));
			} else {
				exit = true;
			}
		}
		return columns;
	}

	public Integer getColumnStyles() {
		int ret = 0;
		switch (this.numColumns) {
		case "firstcolumn":
			ret = 1;
			break;
		case "secondcolumn":
			ret = 2;
			break;
		case "thirdcolumn":
			ret = 3;
			break;
		}
		return ret;
	}

	private String addHeader(Integer columnNumber) {
		String ret = StringUtils.EMPTY;
		if(this.headersList.get(columnNumber) != null){
			ret = "<h3 class=\"linklist--column--header\">" + this.headersList.get(columnNumber) + "</h3>";
		}
		return ret;
	}
	
	public StringBuffer addList(Integer columnNumber) throws JSONException {
		StringBuffer columnString = new StringBuffer();
		
		columnString.append("<li>");
			columnString.append("<div class=\"linklist--column" + hasHeaders(columnNumber) + "\">");
				columnString.append(addHeader(columnNumber));
				columnString.append("<ul class=\"linklist--column--items\">");
		
			    if (this.columnItemsList.get(columnNumber) != null)
			    {
			        for (String linkItem : this.columnItemsList.get(columnNumber)) {
			            JSONObject json = new JSONObject(linkItem);
			            
						String linkTitle = json.getString("text");
						
			            String linkURL = json.getString("url");
			            
						Boolean isNewWindow = json.getBoolean("openInNewWindow"); 
						String windowTarget = "";
						if (isNewWindow == true) {
							windowTarget = "_blank";
						}
						else {
							windowTarget = "_self";
						}
						columnString.append(createListItem(linkTitle, linkURL, windowTarget));
			        }
			    }
			    columnString.append("</ul>"
				    		+ "</div>"
			    		+ "</li>");
	    return columnString;
	}
	
	public String hasHeaders(Integer columnNumber){
		String ret = StringUtils.EMPTY;
		switch (columnNumber) {
		case 2:
			if(this.hasHeadersList.get(0) && !this.hasHeadersList.get(1)){
				ret = " linklist--column--no-header";
			}
			break;
		case 3:
			if(this.hasHeadersList.get(0) && !this.hasHeadersList.get(2)){
				ret = " linklist--column--no-header";
			}
			break;
		}
		return ret;
	}
	
	public StringBuffer createListItem(String title, String url, String target){
		StringBuffer listItem = new StringBuffer();
		listItem.append(""
				+ "<li>"
					+ "<a href=\"" + url + "\" target=\"" + target + "\">"
						+ title
					+ "</a>"
				+ "</li>");
		return listItem;
	}
}
