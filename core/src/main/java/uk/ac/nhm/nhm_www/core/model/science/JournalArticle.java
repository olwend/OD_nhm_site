package uk.ac.nhm.nhm_www.core.model.science;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class JournalArticle extends Publication{
	private String journalName;
	private int volume;
	private int issue;
	/* Pagination */
	private int paginationBeginPage;
	private int paginationEndPage;
	
	private String doiText;
	private String doiLink;
	
	public JournalArticle(final String title, final List<String> authorsList, boolean favorite, final int publicationYear,
			final String href, final String reportingDate, final String journalName, final int volume, final int issue,
			final int paginationBeginPage, final int paginationEndPage, final String doiText,
			final String doiLink) {
		super(title, authorsList, favorite, publicationYear, href, reportingDate);
		this.journalName = journalName;
		this.volume = volume;
		this.issue = issue;
		this.paginationBeginPage = paginationBeginPage;
		this.paginationEndPage = paginationEndPage;
		this.doiText = doiText;
		this.doiLink = doiLink;
	}

	public String getJournalName() {
		return journalName;
	}

	public int getVolume() {
		return volume;
	}

	public int getIssue() {
		return issue;
	}

	public int getPaginationBeginPage() {
		return paginationBeginPage;
	}

	public int getPaginationEndPage() {
		return paginationEndPage;
	}

	public String getDoiText() {
		return doiText;
	}

	public String getDoiLink() {
		return doiLink;
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
		// We store the author name with a single initial	e.g: Ouvrard D
		String firstInitial = normalizeName(author, true);
		
		Iterator<String> authorsIt = authors.iterator();
		List<String> processedAuthors = new ArrayList<String>();
		
		while( authorsIt.hasNext() ){
			String authorName = authorsIt.next().toString();
			processedAuthors.add(normalizeName(authorName, false));
		}
		
		if (processedAuthors.size() > 5 && isFavourite) {
			authorsString = StringUtils.join(processedAuthors.toArray(new String[processedAuthors.size()]), ", ", 0, 5) + ", et al. ";
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
		//Remove name delimiters placed there by the normalizer
		authorsString = authorsString.replaceAll("#", "");
		
//		LOG.error("After being replaced: " + authorsString);
		
		final StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(" >>>>>> This is a Journal <<<<< ");
		
		// Author NM, Author NM
		stringBuffer.append(authorsString);
		
		// (Year)
		stringBuffer.append(" (");
		stringBuffer.append(this.getPublicationYear());
		stringBuffer.append(") ");
		
		// ArticleTitle.
		stringBuffer.append(this.getTitle());
		stringBuffer.append(". ");
		
		// <i>JournalName</i>
		stringBuffer.append("<i>");
		stringBuffer.append(this.getJournalName());
		stringBuffer.append("</i>");
		stringBuffer.append(", ");
		
		// <b>Volume</b>
		if (this.volume > 0) {
			stringBuffer.append("<b>");
			stringBuffer.append(this.volume);
			stringBuffer.append("</b>");
			stringBuffer.append(" ");
		}
		
		// Issue
		if (this.issue >= 0) {
			stringBuffer.append("");
			stringBuffer.append(this.issue);
			stringBuffer.append(" : ");
		}
		
		// : PagesBegin-PagesEnd
		if (this.paginationBeginPage > 0 && this.paginationEndPage > 0) {
			stringBuffer.append(" ");
			stringBuffer.append(this.paginationBeginPage);
			stringBuffer.append(" - ");
			stringBuffer.append(this.paginationEndPage);
			stringBuffer.append(". ");
		}
		
		// DOI hyperlink
		if (this.doiLink != null && this.doiText != null) {
			stringBuffer.append("<a href=\"");
			stringBuffer.append(this.doiLink);
			stringBuffer.append("\">");
			stringBuffer.append("doi: ");
			stringBuffer.append(this.doiText);
			stringBuffer.append("</a>");
		}
		
		return stringBuffer.toString();
	}
	
}
