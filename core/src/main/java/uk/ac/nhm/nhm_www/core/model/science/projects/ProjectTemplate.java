package uk.ac.nhm.nhm_www.core.model.science.projects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ProjectTemplate implements Comparable<ProjectTemplate> {
	private static final Logger LOG = LoggerFactory.getLogger(ProjectTemplate.class);
	protected String url;
	protected String name;
	protected String reportingDate;
	protected String yearsd;
	protected String monthsd;
	protected String daysd;
	protected String yeared;
	protected String monthed;
	protected String dayed;
	
	public ProjectTemplate(final String url, final String title, final String reportingDate, final String yearsd, final String monthsd, 
			final String daysd, final String yeared, final String monthed, final String dayed) {
		this.url = url;
		this.name = title;
		this.yearsd = yearsd;
		this.monthsd = monthsd;
		this.daysd = daysd;
		this.yeared = yeared;
		this.monthed = monthed;
		this.dayed = dayed;
	}

	public abstract String getHTMLContent(final String currentAuthor);
	
	public String getFilteredHTMLContent(final String currentAuthor, String[] parameter){
		String res = getHTMLContent(currentAuthor);
		return res;
	}
	
	public boolean isValid() {
		return true;
	}
	
	public int compareTo(final ProjectTemplate p) {
		if (p.reportingDate == null) {
			return 1;
		}
		
		if (this.reportingDate == null) {
			return -1;
		}
		
		final int reportingDatesComparation = p.reportingDate.compareTo(p.reportingDate) * -1;
		
		if (reportingDatesComparation == 0) {
			return this.name.compareTo(p.name);
		}
		
		return reportingDatesComparation;
		
	}

	public String getTitle() {
		return name;
	}

	public void setTitle(String title) {
		this.name = title;
	}
	
	public String getStartDate(){
		return this.daysd + "/" + this.monthsd + "/" + this.yearsd; 
	}
	
	public String getEndDate(){
		return this.dayed + "/" + this.monthed + "/" + this.yeared; 
	}
}