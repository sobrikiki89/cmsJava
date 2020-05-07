package web.core.helper;

import java.text.*;
import java.util.Locale;
import java.util.regex.Pattern;
import java.text.NumberFormat;

public class Formatter {
	public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
			Pattern.CASE_INSENSITIVE);

	private Locale locale;

	public Formatter() {
		this(Locale.ENGLISH);
	}

	public Formatter(Locale locale) {
		setLocale(locale);
	}

	public String currency(double amount) {
		if (locale.getLanguage().equals("en") || locale.getLanguage().isEmpty()) {
			Format fmt = NumberFormat.getCurrencyInstance();
			return fmt.format(new Double(amount));
		}

		else {
			Format fmt = NumberFormat.getNumberInstance(locale);
			return fmt.format(new Double(amount));
		}
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}
}
