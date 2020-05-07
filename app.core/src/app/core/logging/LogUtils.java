package app.core.logging;

import java.util.Iterator;

import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.filter.LevelFilter;
import ch.qos.logback.classic.sift.MDCBasedDiscriminator;
import ch.qos.logback.classic.sift.SiftingAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.util.Duration;

public class LogUtils {
	private static final long TIMEOUT_2_MINUTES = 1000 * 60 * 2L;
	public static final String MDC_LOG_FILE = "MDC_LOG_FILE";

	/**
	 * check if the root logger already has the sift appender, if it is not, add
	 * it and make sure this this just happen once
	 * 
	 * @param logger
	 * @param fileName
	 */
	public static void addSiftAppender(String name) {
		Logger root = (Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		try {
			Iterator<Appender<ILoggingEvent>> enumeration = root.iteratorForAppenders();
			while (enumeration.hasNext()) {
				Appender<ILoggingEvent> appender = enumeration.next();
				if (appender instanceof SiftingAppender && appender.getName() != null
						&& appender.getName().equals(name)) {
					// Appender found, skip from creating a new one
					return;
				}
			}

			root.addAppender(createAppenderConfiguration(root.getLoggerContext(), name));
			root.setAdditive(true);
		} catch (Exception e) {
			root.error("Error on appending new sift appender", e);
		}
	}

	/**
	 * Clean-up the sift logging. After this method is executed, then logs are
	 * written to server.log only.
	 */
	public static void removeSiftAppender(String name) {
		Logger root = (Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		try {
			Iterator<Appender<ILoggingEvent>> enumeration = root.iteratorForAppenders();
			while (enumeration.hasNext()) {
				Appender<ILoggingEvent> appender = enumeration.next();
				if (appender instanceof SiftingAppender && appender.getName() != null
						&& appender.getName().equals(name)) {
					appender.stop();
					root.detachAppender(appender);
					return;
				}
			}
		} catch (Exception e) {
			root.error("Error on detaching sift appender", e);
		}
	}

	/**
	 * Adds the test name to MDC so that sift appender can use it and log the
	 * new log events to a different file
	 * 
	 * @param name
	 *            name of the new log file
	 * @throws Exception
	 */
	public static void startLogging(String fileName) {
		MDC.put(MDC_LOG_FILE, fileName);
	}

	/**
	 * Removes the key (log file name) from MDC
	 * 
	 * @return name of the log file, if one existed in MDC
	 */
	public static String stopLogging() {
		String fileName = MDC.get(MDC_LOG_FILE);
		MDC.remove(MDC_LOG_FILE);
		return fileName;
	}

	/**
	 * Create Logback configuration for enabling sift appender. A new log file
	 * is created for each task.
	 */
	static SiftingAppender createAppenderConfiguration(LoggerContext ctx, String name) {
		SiftingAppender siftingAppender = new SiftingAppender();
		// Set up the filters to ensure things get split as expected
		LevelFilter debugFilter = new LevelFilter();
		debugFilter.setContext(ctx);
		debugFilter.setLevel(Level.TRACE);
		debugFilter.setOnMatch(FilterReply.ACCEPT);
		debugFilter.setOnMismatch(FilterReply.DENY);
		siftingAppender.addFilter(debugFilter);
		siftingAppender.setContext(ctx);
		MDCBasedDiscriminator mdcDiscriminator = new MDCBasedDiscriminator();
		mdcDiscriminator.setContext(ctx);
		mdcDiscriminator.setKey(MDC_LOG_FILE);
		mdcDiscriminator.setDefaultValue(name);
		mdcDiscriminator.start();
		siftingAppender.setContext(ctx);
		siftingAppender.setDiscriminator(mdcDiscriminator);
		siftingAppender.setAppenderFactory(new FileAppenderFactory());
		siftingAppender.setName(name);
		siftingAppender.setTimeout(new Duration(TIMEOUT_2_MINUTES));
		siftingAppender.start();
		return siftingAppender;
	}
}
