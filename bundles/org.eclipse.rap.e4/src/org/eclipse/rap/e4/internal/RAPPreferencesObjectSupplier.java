package org.eclipse.rap.e4.internal;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferenceNodeVisitor;
import org.eclipse.e4.core.di.IInjector;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.di.suppliers.ExtendedObjectSupplier;
import org.eclipse.e4.core.di.suppliers.IObjectDescriptor;
import org.eclipse.e4.core.di.suppliers.IRequestor;
import org.eclipse.rap.e4.preferences.EPreference;
import org.eclipse.rap.e4.preferences.EPreferenceListener;
import org.eclipse.rap.e4.preferences.EPreferenceService;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

@SuppressWarnings("restriction")
public class RAPPreferencesObjectSupplier extends ExtendedObjectSupplier {

	static private class PrefInjectionListener implements EPreferenceListener {

		final private IRequestor requestor;
		final private EPreference node;

		public PrefInjectionListener(EPreference node, IRequestor requestor) {
			this.node = node;
			this.requestor = requestor;
		}

		@Override
		public void preferenceChange() {
			if (!requestor.isValid()) {
				node.removeChangeListener(this);
				return;
			}
			requestor.resolveArguments(false);
			requestor.execute();
		}

		public IRequestor getRequestor() {
			return requestor;
		}

		public void stopListening() {
			node.removeChangeListener(this);
		}
	}

	private Map<String, List<PrefInjectionListener>> listenerCache = new HashMap<String, List<PrefInjectionListener>>();

	@Inject
	EPreferenceService preferenceService;
	
	public RAPPreferencesObjectSupplier() {
//		DIEActivator.getDefault().registerPreferencesSupplier(this);
	}

	@Override
	public Object get(IObjectDescriptor descriptor, IRequestor requestor, boolean track, boolean group) {
		if (descriptor == null)
			return null;
		Class<?> descriptorsClass = getDesiredClass(descriptor.getDesiredType());
		String nodePath = getNodePath(descriptor, requestor.getRequestingObjectClass());
		if (IEclipsePreferences.class.equals(descriptorsClass)) {
			return new WrappedPreference(getPreferencesService().getNode(nodePath));
//			return getPreferencesService().getNode(nodePath);
		} else if( EPreference.class.equals(descriptorsClass) ) {
			return getPreferencesService().getNode(nodePath);
		}

		String key = getKey(descriptor);
		if (key == null || nodePath == null || key.length() == 0 || nodePath.length() == 0)
			return IInjector.NOT_A_VALUE;
		if (track)
			addListener(nodePath, requestor);

		if (descriptorsClass.isPrimitive()) {
			if (descriptorsClass.equals(boolean.class))
				return getPreferencesService().getBoolean(nodePath, key, false);
			else if (descriptorsClass.equals(int.class))
				return getPreferencesService().getInt(nodePath, key, 0);
			else if (descriptorsClass.equals(double.class))
				return getPreferencesService().getDouble(nodePath, key, 0.0d);
			else if (descriptorsClass.equals(float.class))
				return getPreferencesService().getFloat(nodePath, key, 0.0f);
			else if (descriptorsClass.equals(long.class))
				return getPreferencesService().getLong(nodePath, key, 0L);
		}

		if (String.class.equals(descriptorsClass))
			return getPreferencesService().getString(nodePath, key);
		else if (Boolean.class.equals(descriptorsClass))
			return getPreferencesService().getBoolean(nodePath, key, false);
		else if (Integer.class.equals(descriptorsClass))
			return getPreferencesService().getInt(nodePath, key, 0);
		else if (Double.class.equals(descriptorsClass))
			return getPreferencesService().getDouble(nodePath, key, 0.0d);
		else if (Float.class.equals(descriptorsClass))
			return getPreferencesService().getFloat(nodePath, key, 0.0f);
		else if (Long.class.equals(descriptorsClass))
			return getPreferencesService().getLong(nodePath, key, 0L);

		return getPreferencesService().getString(nodePath, key);
	}

	private Class<?> getDesiredClass(Type desiredType) {
		if (desiredType instanceof Class<?>)
			return (Class<?>) desiredType;
		if (desiredType instanceof ParameterizedType) {
			Type rawType = ((ParameterizedType) desiredType).getRawType();
			if (rawType instanceof Class<?>)
				return (Class<?>) rawType;
		}
		return null;
	}

	private String getKey(IObjectDescriptor descriptor) {
		if (descriptor == null)
			return null;
		Preference qualifier = descriptor.getQualifier(Preference.class);
		return qualifier.value();
	}

	private String getNodePath(IObjectDescriptor descriptor, Class<?> requestingObject) {
		if (descriptor == null)
			return null;
		Preference qualifier = descriptor.getQualifier(Preference.class);
		String nodePath = qualifier.nodePath();

		if (nodePath == null || nodePath.length() == 0) {
			if (requestingObject == null)
				return null;
			nodePath = FrameworkUtil.getBundle(requestingObject).getSymbolicName();
		}
		return nodePath;
	}

	private EPreferenceService getPreferencesService() {
//		return DIEActivator.getDefault().getPreferencesService();
		return preferenceService;
	}

	private void addListener(String nodePath, final IRequestor requestor) {
		if (requestor == null)
			return;
		synchronized (listenerCache) {
			if (listenerCache.containsKey(nodePath)) {
				for (PrefInjectionListener listener : listenerCache.get(nodePath)) {
					IRequestor previousRequestor = listener.getRequestor();
					if (previousRequestor.equals(requestor))
						return; // avoid adding duplicate listeners
				}
			}
		}
		final EPreference node = getPreferencesService().getNode(nodePath);
		PrefInjectionListener listener = new PrefInjectionListener(node, requestor);
		node.addChangeListener(listener);

		synchronized (listenerCache) {
			if (listenerCache.containsKey(nodePath))
				listenerCache.get(nodePath).add(listener);
			else {
				List<PrefInjectionListener> listeningRequestors = new ArrayList<PrefInjectionListener>();
				listeningRequestors.add(listener);
				listenerCache.put(nodePath, listeningRequestors);
			}
		}
	}

	public void removeAllListeners() {
		synchronized (listenerCache) {
			for (List<PrefInjectionListener> listeners : listenerCache.values()) {
				if (listeners == null)
					continue;
				for (PrefInjectionListener listener : listeners) {
					listener.stopListening();
				}
			}
			listenerCache.clear();
		}
	}
	
	@PreDestroy
	void shutdown() {
		removeAllListeners();
	}
	
	public static class WrappedPreference implements IEclipsePreferences {
		private EPreference node;
		
		public WrappedPreference(EPreference node) {
			this.node = node;
		}

		@Override
		public String absolutePath() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String[] childrenNames() throws BackingStoreException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void clear() throws BackingStoreException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void flush() throws BackingStoreException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String get(String arg0, String arg1) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean getBoolean(String arg0, boolean arg1) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public byte[] getByteArray(String arg0, byte[] arg1) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public double getDouble(String arg0, double arg1) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public float getFloat(String arg0, float arg1) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getInt(String arg0, int arg1) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public long getLong(String arg0, long arg1) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public String[] keys() throws BackingStoreException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String name() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean nodeExists(String arg0) throws BackingStoreException {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Preferences parent() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void put(String arg0, String arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void putBoolean(String arg0, boolean arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void putByteArray(String arg0, byte[] arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void putDouble(String arg0, double arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void putFloat(String arg0, float arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void putInt(String arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void putLong(String arg0, long arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void remove(String arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void sync() throws BackingStoreException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void accept(IPreferenceNodeVisitor arg0)
				throws BackingStoreException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void addNodeChangeListener(INodeChangeListener arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void addPreferenceChangeListener(IPreferenceChangeListener arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Preferences node(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void removeNode() throws BackingStoreException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void removeNodeChangeListener(INodeChangeListener arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void removePreferenceChangeListener(
				IPreferenceChangeListener arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
