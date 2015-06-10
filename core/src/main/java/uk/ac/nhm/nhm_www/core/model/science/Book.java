package uk.ac.nhm.nhm_www.core.model.science;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class Book extends Publication{
	private List<String> editors;
	private String publisher;
	private String place;
	private int page;
	
	public Book(final String title, final List<String> authorsList, boolean favorite, final int publicationYear,
			final String href, final String reportingDate, final List<String> editorsSet, final String publisher, final String place,
			final int page) {
		super(title, authorsList, favorite, publicationYear, href, reportingDate);
		
		this.editors = editorsSet;
		this.publisher = publisher;
		this.place = place;
		this.page = page;
	}

	public List<String> getEditors() {
		return editors;
	}

	public String getPublisher() {
		return publisher;
	}

	public String getPlace() {
		return place;
	}

	public int getPage() {
		return page;
	}

	@Override
	public String getHTMLContent(final String author, final boolean isFavourite) {
		// Butler, R. J. and Barrett, P. M. (2013) Global Cambrian trilobite palaeobiogeography assessed using parsimony analysis of endemicity. In: D.A.T Harper and T Servais (eds) Early Palaeozoic Biogeography and Palaeogeography, pp. 273-296. Geological Society, London.
		// Authors (publication year) Title. Editors, Book Title, startPage - endPage. Publisher. Place.
		final List<String> authors = this.getAuthors();
		String authorsString = StringUtils.EMPTY;
		
		// First we normalize the author's name e.g: 
		// Ouvrard D N M  || OUVRARD DNM >> will become Ouvrard DNM
		String currentAuthor = normalizeName(author, false);
		String firstInitial = normalizeName(currentAuthor, true);
		
		Iterator<String> authorsIt = authors.iterator();
		List<String> processedAuthors = new ArrayList<String>();
		
		while( authorsIt.hasNext() ){
			String authorName = authorsIt.next().toString();
			processedAuthors.add(normalizeName(authorName, false));
		}
		
		if (processedAuthors.size() > 5 && isFavourite) {
			authorsString = StringUtils.join(processedAuthors.toArray(new String[processedAuthors.size()]), ", ", 0, 5) + ", et al";
		} else {
			authorsString = StringUtils.join(processedAuthors.toArray(new String[processedAuthors.size()]), ", ");
		}
		
//		LOG.error("This is the list of authors parsed: " + authorsString);
//		LOG.error("Current Author: " + currentAuthor);
//		LOG.error("First Initial Author: " + firstInitial);
		if (authorsString.contains(currentAuthor)) {
			authorsString = authorsString.replaceAll(currentAuthor, "<b>" + currentAuthor + "</b>");
		} else if (authorsString.contains(firstInitial)) {
			authorsString = authorsString.replaceAll(firstInitial, "<b>" + currentAuthor + "</b>");
		}
//		LOG.error("After being replaced: " + authorsString);
		
		final StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(" >>>>>>>> This is a Book <<<<<<<< ");
		stringBuffer.append(authorsString);
		stringBuffer.append(". ");
		
		final List<String> editors = this.getEditors();
		if (editors != null) {
			String editorsString = StringUtils.join(editors.toArray(new String[editors.size()]), ", ");
			editorsString = editorsString.replaceAll(currentAuthor, "<b>" + currentAuthor + "</b>");
			
			if (editorsString.contains(currentAuthor)) {
				editorsString = editorsString.replaceAll(currentAuthor, "<b>" + currentAuthor + "</b>");
			} else if (authorsString.contains(firstInitial)) {
				editorsString = editorsString.replaceAll(firstInitial, "<b>" + currentAuthor + "</b>");
			}
			
			stringBuffer.append(", ");
			stringBuffer.append(editorsString);
		}
		
		stringBuffer.append(" (");
		stringBuffer.append(this.getPublicationYear());
		stringBuffer.append(") ");
		
		stringBuffer.append("<i>");
		stringBuffer.append(this.getTitle());
		stringBuffer.append("</i>. ");
		
		stringBuffer.append(this.publisher);
		stringBuffer.append(". ");
		
		if (this.place != null) {
			stringBuffer.append(this.place);
			stringBuffer.append(".");
		}
		
		if (this.page > 0) {
			stringBuffer.append(" ");
			stringBuffer.append(this.page);
		}
			
		return stringBuffer.toString();
	}
	
}
