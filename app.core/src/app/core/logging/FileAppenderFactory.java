package app.core.logging;

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.sift.AppenderFactory;

public class FileAppenderFactory implements AppenderFactory<ILoggingEvent> {

	private static final String ENCODER_PATTERN = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n";

	/**
	 * @param context
	 * @param discriminatingValue
	 *            path of log file
	 */
	@Override
	public FileAppender<ILoggingEvent> buildAppender(Context context, String discriminatingValue) {
		PatternLayoutEncoder consoleEncoder = new PatternLayoutEncoder();
		consoleEncoder.setContext(context);
		consoleEncoder.setPattern(ENCODER_PATTERN);
		consoleEncoder.start();
		FileAppender<ILoggingEvent> appender = new FileAppender<ILoggingEvent>();
		appender.setContext(context);
		appender.setEncoder(consoleEncoder);
		appender.setFile(discriminatingValue);
		appender.start();
		return appender;
	}

}
