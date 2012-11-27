package gov.va.common.xml;

import org.xml.sax.SAXParseException;

public class ValidationException {
	
	private String message;
	private String severity;
	/**
	 * Constructors
	 */
	public ValidationException()
	{
		
	}
	
	public ValidationException(String severity, SAXParseException exception)
	{
		this.severity = severity;
		if (exception.getMessage() != null && exception.getMessage().length() > 0)
			this.message = exception.getMessage();
		else if(exception.getLocalizedMessage() != null && exception.getLocalizedMessage().length() > 0)
			this.message = exception.getLocalizedMessage();
		else
			this.message = exception.getCause().getMessage();
	}

	
	/**
	 * Getters and setters
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
	
	public void setSeveriy(String severity){
		this.severity = severity;
	}
	
	public String getSeverity()
	{
		return severity;
	}

}
