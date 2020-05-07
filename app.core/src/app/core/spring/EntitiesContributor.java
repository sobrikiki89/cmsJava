package app.core.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class EntitiesContributor implements BeanPostProcessor {
	private String[] packages;

	public String[] getPackages() {
		return this.packages;
	}

	public void setPackages(String[] packages) {
		this.packages = packages;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (EntitiesFactoryBean.class.isAssignableFrom(bean.getClass())) {
			EntitiesFactoryBean f = (EntitiesFactoryBean) bean;
			f.addPackages(packages);
		}
		return bean;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}
}
