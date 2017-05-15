package org.eclipse.rap.e4.preferences;

import org.eclipse.core.runtime.IStatus;


public interface EPreferenceService {
	public EPreference getNode(String nodePath);

	public boolean getBoolean(String nodePath, String key, boolean b);

	public int getInt(String nodePath, String key, int i);

	public double getDouble(String nodePath, String key, double d);

	public float getFloat(String nodePath, String key, float f);

	public long getLong(String nodePath, String key, long l);

	public String getString(String nodePath, String key);
	
	public IStatus setBoolean(String nodePath, String key, boolean value);
	
	public IStatus setDouble(String nodePath, String key, double value);
	
	public IStatus setFloat(String nodePath, String key, float value);
	
	public IStatus setInt(String nodePath, String key, int value);
	
	public IStatus setLong(String nodePath, String key, long value);
	
	public IStatus setString(String nodePath, String key, String value);
}
