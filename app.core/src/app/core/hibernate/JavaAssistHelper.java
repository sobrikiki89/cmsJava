package app.core.hibernate;

import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyFactory.ClassLoaderProvider;

// Solution from https://hibernate.onjira.com/browse/HHH-3826
public class JavaAssistHelper {
	public static ClassLoaderProvider createJavaAssistClassLoader() {
		ProxyFactory.classLoaderProvider = new ProxyFactory.ClassLoaderProvider() {
			public ClassLoader get(ProxyFactory pf) {
				return Thread.currentThread().getContextClassLoader();
			}
		};
		return ProxyFactory.classLoaderProvider;
	}
}