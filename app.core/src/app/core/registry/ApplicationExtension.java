package app.core.registry;

public interface ApplicationExtension<T> {
	public static enum Multiplicity {
		SINGLETON, COLLECTION;
	}

	public Multiplicity getMultiplicity();

	public String getUuid();
}
