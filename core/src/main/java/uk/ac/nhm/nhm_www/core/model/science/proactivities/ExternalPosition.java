package uk.ac.nhm.nhm_www.core.model.science.proactivities;



public class ExternalPosition extends ProfessionalActivity {

	public ExternalPosition(String title, final String reportingDate, String yearStartDate, String monthStartDate, String dayStartDate, String yearEndDate, String monthEndDate, String dayEndDate) {
		super(title, reportingDate, yearStartDate, monthStartDate, dayStartDate, yearEndDate, monthEndDate, dayEndDate);
	}

	@Override
	public String getHTMLContent(String currentAuthor) {
		final StringBuffer stringBuffer = new StringBuffer();
		
		stringBuffer.append("####This is an ExternalPosition####");
		stringBuffer.append(" ");
		
		// Title_
		if (this.title != null){
			stringBuffer.append(this.title);
			stringBuffer.append(" ");
		}
		
		if (this.dayed != null && this.monthed != null && this.yeared != null){
			stringBuffer.append(this.yearsd);
			stringBuffer.append(" - ");
			stringBuffer.append(this.yeared);
		} else {
			stringBuffer.append("Ongoing");
		}
		
		return stringBuffer.toString();
	}
}