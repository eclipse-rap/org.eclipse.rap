package org.eclipse.rap.e4.preferences;

import org.eclipse.core.runtime.IStatus;

public interface EPreference {

	void addChangeListener(EPreferenceListener listener);
	void removeChangeListener(EPreferenceListener listener);
	
	boolean getBoolean(String key, boolean defaultValue);
	int getInt(String key, int defaultValue);
	double getDouble(String key, double defaultValue);
	float getFloat(String key, float defaultValue);
	long getLong(String key, long defaultValue);
	String getString(String key);
	
	IStatus setBoolean(String key, boolean value);
	IStatus setInt(String key, int value);
	IStatus setDouble(String key, double value);
	IStatus setFloat(String key, float value);
	IStatus setLong(String key, long value);
	IStatus setString(String key, String value);
}
