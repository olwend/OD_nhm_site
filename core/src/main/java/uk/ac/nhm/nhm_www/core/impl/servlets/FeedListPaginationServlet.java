package uk.ac.nhm.nhm_www.core.impl.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import uk.ac.nhm.nhm_www.core.componentHelpers.FeedListHelper;
import uk.ac.nhm.nhm_www.core.componentHelpers.PressReleaseFeedListHelper;
import uk.ac.nhm.nhm_www.core.services.FeedListPaginationService;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.PageManagerFactory;

@Component(label = "Natural History Museum Pagination Servlet", description = "Natural History Museum Pagination Servlet", metatype = true, immediate = true)
@Service(value = Servlet.class)
@Properties({
		@Property(name = "sling.servlet.paths", value = { "/bin/list/pagination" }, propertyPrivate = true),
		@Property(name = "sling.servlet.extensions", value = {"json"}),
		@Property(name = "sling.servlet.methods", value = { "GET" }, propertyPrivate = true),
		@Property(name = "service.description", value = "Return Paginated list"),
		@Property(name = "pageNumber", intValue = 0, description = "Default Start Page"),
		@Property(name = "pageSize", intValue = 8, description = "Default page size")
		
})
public class FeedListPaginationServlet extends SlingAllMethodsServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Reference
	protected PageManagerFactory pageManagerFactory;
	
	@Reference
	private FeedListPaginationService paginationService;

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException, NumberFormatException {
		String rootPath = request.getParameter("rootPath");
		Integer pageNumber = Integer.parseInt(request.getParameter("pageNumber"));
		Integer pageSize = Integer.parseInt(request.getParameter("pageSize"));
		final Resource resource = request.getResource();
		
		ResourceResolver resourceResolver = request.getResourceResolver();
		
		PageManager pageManager = pageManagerFactory.getPageManager(resourceResolver);
		
		ValueMap properties = new ValueMapDecorator(new HashMap());
		
		Page rootPage = pageManager.getPage(rootPath);
		Iterator<Page> childPages = rootPage.listChildren(new PageFilter(request));
		List<Object> objects = null;
		FeedListHelper helper = null;
		if(childPages.hasNext() && childPages.next().getProperties().get("cq:template").equals("/apps/nhmwww/templates/pressreleasepage")) { 
			helper = new PressReleaseFeedListHelper(properties, pageManager, rootPage, request, resourceResolver);
			
		} else {
			helper = new FeedListHelper(properties, pageManager, rootPage, request, resourceResolver);
		}
		objects = helper.getChildrenElements();
		
		JSONObject jsonString = paginationService.getJSON(objects, pageNumber, pageSize);
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(jsonString.toString());
	}
	
}