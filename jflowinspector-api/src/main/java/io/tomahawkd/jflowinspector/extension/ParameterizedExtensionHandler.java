package io.tomahawkd.jflowinspector.extension;

public interface ParameterizedExtensionHandler extends ExtensionHandler {
	/**
	 * Accept a extension instance
	 *
	 * @param extension extension
	 * @return true if accepted and false if rejected
	 */
	boolean accept(Class<? extends ParameterizedExtensionPoint> extension);

	/**
	 * This method is ignored in ParameterizedExtension handler.
	 */
	@Deprecated
	default boolean accept(ExtensionPoint extension) {
		return false;
	}
}
