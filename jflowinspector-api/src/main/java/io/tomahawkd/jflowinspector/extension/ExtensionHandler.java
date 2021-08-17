package io.tomahawkd.jflowinspector.extension;

public interface ExtensionHandler {

	/**
	 * Accept an extension instance
	 *
	 * @param extension extension
	 * @return true if accepted and false if rejected
	 */
	boolean accept(ExtensionPoint extension);

	boolean canAccept(Class<? extends ExtensionPoint> clazz);

	/**
	 * Post-Initialization procedure
	 */
	default void postInitialization() {}
}
