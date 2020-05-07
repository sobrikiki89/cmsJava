package app.core.registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import app.core.registry.ApplicationExtension.Multiplicity;

@Component
public class ApplicationExtensionRegistry implements InitializingBean, DisposableBean {
	private final Map<ApplicationExtension<?>, Collection<?>> registrations = new HashMap<ApplicationExtension<?>, Collection<?>>();

	private final List<Listener> listeners = new ArrayList<Listener>();

	private static ApplicationExtensionRegistry registry;

	private boolean registrationDone = false;

	public static final ApplicationExtensionRegistry getRegistry() {
		return registry;
	}

	public static interface Listener {
		public void registered(ApplicationExtension<?> point, Object extension);

		public void unregistered(ApplicationExtension<?> point, Object extension);
	}

	public static interface Callback<T> {
		public static enum Status {
			CONTINUE, STOP
		}

		Status processExtension(T extension);
	}

	private ApplicationExtensionRegistry() {
	}

	@SuppressWarnings("unchecked")
	private <T> Collection<T> get(ApplicationExtension<T> point) {
		return (Collection<T>) registrations.get(point);
	}

	/**
	 * Registeres a new {@link Listener}
	 * 
	 * @param listener
	 *            listener
	 * @param replay
	 *            if {@code true} the listener will receive registration events
	 *            for all currently registered extensions
	 */
	public synchronized void register(Listener listener, boolean replay) {
		if (registrationDone) {
			throw new IllegalStateException("Cannot register after registration phase finished.");
		}
		listeners.add(listener);

		if (replay) {
			// replay past registrations
			for (ApplicationExtension<?> point : registrations.keySet()) {
				final Collection<?> extensions = registrations.get(point);
				for (Object extension : extensions) {
					listener.registered(point, extension);
				}
			}
		}
	}

	public synchronized <T> void unregister(ApplicationExtension<T> point, T extension) {
		if (registrationDone) {
			throw new IllegalStateException("Cannot register after registration phase finished.");
		}
		Collection<T> extensions = get(point);
		extensions.remove(extension);
		for (Listener listener : listeners) {
			listener.unregistered(point, extension);
		}
	}

	public synchronized <T> void register(ApplicationExtension<T> point, T extension) {
		if (registrationDone) {
			throw new IllegalStateException("Cannot register after registration phase finished.");
		}
		Collection<T> extensions = get(point);
		if (extensions == null) {
			extensions = new LinkedList<T>();
			registrations.put(point, extensions);
		}

		final Multiplicity multiplicity = point.getMultiplicity();
		switch (multiplicity) {
		case SINGLETON:
			for (Object removed : extensions) {
				for (Listener listener : listeners) {
					listener.unregistered(point, removed);
				}
			}
			extensions.clear();
			break;
		case COLLECTION:
			break;

		}

		extensions.add(extension);
	}

	private <T> Collection<T> lookup(ApplicationExtension<T> point) {
		Collection<T> extensions = get(point);
		if (extensions == null) {
			return Collections.emptySet();
		} else {
			ArrayList<T> copy = new ArrayList<T>(extensions.size());
			copy.addAll(extensions);
			return Collections.unmodifiableCollection(copy);
		}
	}

	public <T> Collection<T> lookupCollection(ApplicationExtension<T> point) {
		// check multiplicity
		switch (point.getMultiplicity()) {
		case COLLECTION:
			break;
		default:
			throw new IllegalArgumentException("Extension point: " + point.getUuid()
					+ " has unsupported multiplicity of: " + point.getMultiplicity() + ". Must be "
					+ Multiplicity.COLLECTION);
		}
		Collection<T> extensions = lookup(point);
		return extensions;
	}

	public <T> void lookupCollection(ApplicationExtension<T> point, Callback<T> callback) {
		Collection<T> extensions = lookupCollection(point);
		for (T extension : extensions) {
			Callback.Status status = callback.processExtension(extension);
			if (status == Callback.Status.STOP) {
				break;
			}
		}
	}

	public <T> T lookupSingleton(ApplicationExtension<T> point) {
		// check multiplicity
		switch (point.getMultiplicity()) {
		case SINGLETON:
			break;
		default:
			throw new IllegalArgumentException("Extension point: " + point.getUuid()
					+ " has unsupported multiplicity of: " + point.getMultiplicity() + ". Must be "
					+ Multiplicity.SINGLETON);
		}

		Iterator<T> it = lookup(point).iterator();

		if (it.hasNext()) {
			return it.next();
		} else {
			return null;
		}
	}

	public synchronized void registrationDone() {
		this.registrationDone = true;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		registry = this;
	}

	@Override
	public void destroy() throws Exception {
		registry = null;
	}
}
