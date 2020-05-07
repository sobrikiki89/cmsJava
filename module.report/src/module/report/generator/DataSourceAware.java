package module.report.generator;

import javax.sql.DataSource;

public interface DataSourceAware {
	public void setDataSource(DataSource ds);
}
