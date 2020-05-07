package app.core.spring;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

public class EntitiesFactoryBean
        implements FactoryBean<String[]> {
    private static final Logger logger = LoggerFactory.getLogger(EntitiesFactoryBean.class);

    private List<String> packages = new ArrayList<String>();

    @Override
    public String[] getObject() throws Exception {
        return packages.toArray(new String[0]);
    }

    @Override
    public Class<?> getObjectType() {
        return String[].class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public void addPackage(String packageName) {
        if (packageName != null && packageName.trim().length() > 0) {
            if (logger.isTraceEnabled()) {
                logger.trace("Adding package: " + packageName);
            }
            packages.add(packageName);
        }
    }

    public void addPackages(String[] packageNames) {
        if (packageNames != null) {
            for (String p : packageNames) {
                addPackage(p);
            }
        }
    }
}
