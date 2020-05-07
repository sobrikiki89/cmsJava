package module.report.generator;

import org.eclipse.birt.report.engine.api.IPageHandler;
import org.eclipse.birt.report.engine.api.IReportDocumentInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BirtPageHandler implements IPageHandler {

	private final static Logger LOGGER = LoggerFactory.getLogger(BirtPageHandler.class);
	private int pageNumber;

	@Override
	public void onPage(int pageNumber, boolean checkpoint, IReportDocumentInfo doc) {
		LOGGER.info("Number: {} ({})", pageNumber, checkpoint);
		// we only want to do something if this is a checkpoint event
		if (checkpoint) {
			// Just let the user know that the next page ranges are ready, then
			// set the last check point to the current page
			LOGGER.info("Pages " + this.pageNumber + " through " + pageNumber + " are ready");
			this.pageNumber = pageNumber;
		}
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
}
