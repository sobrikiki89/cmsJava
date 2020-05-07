package app.core.service;

import java.util.Date;

public interface SequenceService {
	public String getNextSequence(String docCode, String runningNumberFormat);
	
	public String getNextSequence(String docCode, String runningNumberFormat, Date period);
}
