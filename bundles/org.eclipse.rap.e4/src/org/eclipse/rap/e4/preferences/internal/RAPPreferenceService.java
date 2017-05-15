package org.eclipse.rap.e4.preferences.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;

import javax.inject.Inject;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.rap.e4.preferences.EPreference;
import org.eclipse.rap.e4.preferences.EPreferenceListener;
import org.eclipse.rap.e4.preferences.EPreferenceService;
import org.eclipse.rap.rwt.service.SettingStore;
import org.eclipse.rap.rwt.service.SettingStoreEvent;
import org.eclipse.rap.rwt.service.SettingStoreListener;

public class RAPPreferenceService implements EPreferenceService {
	
	private final SettingStore settingStore;
	
	private Map<PreferenceImpl, Boolean> trackedPreferences = Collections.synchronizedMap(new WeakHashMap<RAPPreferenceService.PreferenceImpl, Boolean>());
	
	private final SettingStoreListener listener;
	
	@Inject
	public RAPPreferenceService(SettingStore settingStore) {
		this.settingStore = settingStore;
		this.listener = new SettingStoreListener() {
			
			@Override
			public void settingChanged(SettingStoreEvent event) {
				if( event.getAttributeName().contains("#") ) {
					String nodePath = event.getAttributeName().substring(0,event.getAttributeName().indexOf('#'));
					PreferenceImpl[] array = new PreferenceImpl[trackedPreferences.size()];
					synchronized (trackedPreferences) {
						Iterator<Entry<PreferenceImpl, Boolean>> iterator = trackedPreferences.entrySet().iterator();
						int index = 0;
						while( iterator.hasNext() ) {
							Entry<PreferenceImpl, Boolean> entry = iterator.next();
							if( entry.getKey() == null ) {
								iterator.remove();
							} else if( entry.getKey().nodePath.equals(nodePath) ) {
								array[index++] = entry.getKey();
							}
						}
					}
					
					for( int i = 0; i < array.length; i++ ) {
						if( array[i] == null ) {
							break;
						}
						array[i].fireChange();
					}
				}
			}
		};
		this.settingStore.addSettingStoreListener(listener);
	}
	
	public void track(PreferenceImpl impl) {
		trackedPreferences.put(impl, Boolean.TRUE);
	}
	
	public void untrack(PreferenceImpl impl) {
		trackedPreferences.remove(impl);
	}

	@Override
	public boolean getBoolean(String nodePath, String key, boolean defaultValue) {
		String v = settingStore.getAttribute(getAttributeName(nodePath,key));
		if( v == null ) {
			return defaultValue;
		}
		return Boolean.valueOf(v);
	}

	@Override
	public int getInt(String nodePath, String key, int defaultValue) {
		String v = settingStore.getAttribute(getAttributeName(nodePath,key));
		if( v == null ) {
			return defaultValue;
		}
		return Integer.parseInt(v);
	}

	@Override
	public double getDouble(String nodePath, String key, double defaultValue) {
		String v = settingStore.getAttribute(getAttributeName(nodePath,key));
		if( v == null ) {
			return defaultValue;
		}
		return Double.parseDouble(v);
	}

	@Override
	public float getFloat(String nodePath, String key, float defaultValue) {
		String v = settingStore.getAttribute(getAttributeName(nodePath,key));
		if( v == null ) {
			return defaultValue;
		}
		return Float.parseFloat(v);
	}

	@Override
	public long getLong(String nodePath, String key, long defaultValue) {
		String v = settingStore.getAttribute(getAttributeName(nodePath,key));
		if( v == null ) {
			return defaultValue;
		}
		return Long.parseLong(v);
	}

	@Override
	public String getString(String nodePath, String key) {
		return settingStore.getAttribute(getAttributeName(nodePath,key));
	}

	@Override
	public EPreference getNode(String nodePath) {
		return new PreferenceImpl(nodePath);
	}
	
	@Override
	public IStatus setBoolean(String nodePath, String key, boolean value) {
		return setString(nodePath, key, value+"");
	}
	
	@Override
	public IStatus setDouble(String nodePath, String key, double value) {
		return setString(nodePath, key, value+"");
	}
	
	@Override
	public IStatus setFloat(String nodePath, String key, float value) {
		return setString(nodePath, key, value+"");
	}
	
	@Override
	public IStatus setInt(String nodePath, String key, int value) {
		return setString(nodePath, key, value+"");
	}
	
	@Override
	public IStatus setLong(String nodePath, String key, long value) {
		return setString(nodePath, key, value+"");
	}
	
	@Override
	public IStatus setString(String nodePath, String key, String value) {
		try {
			settingStore.setAttribute(getAttributeName(nodePath, key), value);
			return Status.OK_STATUS;
		} catch (IOException e) {
			return new Status(IStatus.ERROR, "org.eclipse.rap.e4", "Unable to store preference", e);
		}
	}
	
	static String getAttributeName(String nodePath, String key) {
		return nodePath+"#"+key;
	}
	
	class PreferenceImpl implements EPreference {
		private String nodePath;
		private List<EPreferenceListener> listenerList = new ArrayList<EPreferenceListener>();
		
		public PreferenceImpl(String nodePath) {
			this.nodePath = nodePath;
		}
		
		@Override
		public boolean getBoolean(String key, boolean defaultValue) {
			return RAPPreferenceService.this.getBoolean(nodePath, key, defaultValue);
		}

		@Override
		public int getInt(String key, int defaultValue) {
			return RAPPreferenceService.this.getInt(nodePath, key, defaultValue);
		}

		@Override
		public double getDouble(String key, double defaultValue) {
			return RAPPreferenceService.this.getDouble(nodePath, key, defaultValue);
		}

		@Override
		public float getFloat(String key, float defaultValue) {
			return RAPPreferenceService.this.getFloat(nodePath, key, defaultValue);
		}

		@Override
		public long getLong(String key, long defaultValue) {
			return RAPPreferenceService.this.getLong(nodePath, key, defaultValue);
		}

		@Override
		public String getString(String key) {
			return RAPPreferenceService.this.getString(nodePath, key);
		}
		
		@Override
		public IStatus setBoolean(String key, boolean value) {
			return RAPPreferenceService.this.setBoolean(nodePath, key, value);
		}
		
		@Override
		public IStatus setDouble(String key, double value) {
			return RAPPreferenceService.this.setDouble(nodePath, key, value);
		}
		
		@Override
		public IStatus setFloat(String key, float value) {
			return RAPPreferenceService.this.setFloat(nodePath, key, value);
		}
		
		@Override
		public IStatus setInt(String key, int value) {
			return RAPPreferenceService.this.setInt(nodePath, key, value);
		}
		
		@Override
		public IStatus setLong(String key, long value) {
			return RAPPreferenceService.this.setLong(nodePath, key, value);
		}
		
		@Override
		public IStatus setString(String key, String value) {
			return RAPPreferenceService.this.setString(nodePath, key, value);
		}

		@Override
		public void addChangeListener(EPreferenceListener listener) {
			listenerList.add(listener);
			if( listenerList.size() == 1 ) {
				RAPPreferenceService.this.track(this);
			}
		}

		@Override
		public void removeChangeListener(EPreferenceListener listener) {
			listenerList.remove(listener);
			if( listenerList.isEmpty() ) {
				RAPPreferenceService.this.untrack(this);
			}
		}
		
		public void fireChange() {
			if( ! listenerList.isEmpty() ) {
				EPreferenceListener[] listeners = listenerList.toArray(new EPreferenceListener[listenerList.size()]);
				for( int i = 0; i < listeners.length; i++ ) {
					listeners[i].preferenceChange();
				}
			}
		}
	}
}
