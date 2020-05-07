package app.core.utils;

import java.text.SimpleDateFormat;

public interface AppConstant {
	public static final long MENU_HOME_ID = 1L;

	public static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");
	
	public static final String DATE_FORMAT = "dd-MMM-yyyy";
	public static final String DATE_TIME_FORMAT = "dd-MMM-yyyy HH:mm:ss";
	public static final String DATE_TIME_FORMAT_NO_SEC = "dd-MMM-yyyy HH:mm";

	public static final String NUMBER_FORMAT = "###,###,###,##0.00";
}
