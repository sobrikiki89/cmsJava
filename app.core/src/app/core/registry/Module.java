package app.core.registry;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;

import app.core.service.FunctionService;
import app.core.service.MenuService;

public abstract class Module
		implements Ordered, InitializingBean, ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {

	private static final Logger logger = LoggerFactory.getLogger(Module.class);

	private AtomicBoolean initialized = new AtomicBoolean(false);

	public static ApplicationExtension<Module> EXTENSION = new ApplicationExtension<Module>() {
		public Multiplicity getMultiplicity() {
			return Multiplicity.COLLECTION;
		}

		public String getUuid() {
			return Module.class.getName();
		}
	};

	private int order;

	private ApplicationContext springContext;

	@Autowired
	private FunctionService functionService;

	@Autowired
	private MenuService menuService;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.springContext = applicationContext;
	}

	public ApplicationContext getContext() {
		return this.springContext;
	}

	public int getOrder() {
		return this.order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public final void afterPropertiesSet() throws Exception {
		ApplicationExtensionRegistry.getRegistry().register(Module.EXTENSION, this);
		logger.info("Module registered: " + getModuleName());
	}

	public static Collection<Module> findModules() {
		return ApplicationExtensionRegistry.getRegistry().lookupCollection(Module.EXTENSION);
	}

	public static Module findModule(Class<? extends Module> moduleClass) {
		for (Module module : findModules()) {
			if (moduleClass.isAssignableFrom(module.getClass())) {
				return module;
			}
		}
		return null;
	}

	@Override
	public final void onApplicationEvent(ContextRefreshedEvent event) {
		if (initialized.get()) {
			return;
		}
		try {
			functionService.updateFunctions(this);
			menuService.updateMenus(this);
			init();
			initialized.set(true);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	protected abstract void init() throws Exception;

	public abstract String getModuleName();
}
