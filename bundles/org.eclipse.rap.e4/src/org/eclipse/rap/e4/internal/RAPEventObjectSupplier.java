package org.eclipse.rap.e4.internal;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.eclipse.e4.core.di.IInjector;
import org.eclipse.e4.core.di.InjectionException;
import org.eclipse.e4.core.di.extensions.EventTopic;
import org.eclipse.e4.core.di.internal.extensions.util.EventUtils;
import org.eclipse.e4.core.di.suppliers.ExtendedObjectSupplier;
import org.eclipse.e4.core.di.suppliers.IObjectDescriptor;
import org.eclipse.e4.core.di.suppliers.IRequestor;
import org.eclipse.e4.ui.internal.workbench.swt.E4Application;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

@SuppressWarnings("restriction")
public class RAPEventObjectSupplier extends ExtendedObjectSupplier {

	// This is a temporary code to ensure that bundle containing
	// EventAdmin implementation is started. This code it to be removed once
	// the proper method to start EventAdmin is added.
	static {
		if (getEventAdmin() == null) {
			Bundle[] bundles = Activator.getDefault().getBundleContext().getBundles();
			for (Bundle bundle : bundles) {
				if (!"org.eclipse.equinox.event".equals(bundle.getSymbolicName())) //$NON-NLS-1$
					continue;
				try {
					bundle.start(Bundle.START_TRANSIENT);
				} catch (BundleException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}

	protected Map<String, Event> currentEvents = new HashMap<String, Event>();

	class DIEventHandler implements EventHandler {

		final private IRequestor requestor;
		final private String topic;

		public DIEventHandler(String topic, IRequestor requestor) {
			this.topic = topic;
			this.requestor = requestor;
		}

		public void handleEvent(Event event) {
			if (!requestor.isValid()) {
				unsubscribe(requestor);
				return;
			}

			addCurrentEvent(topic, event);
			requestor.resolveArguments(false);
			removeCurrentEvent(topic);

			requestor.execute();
		}
	}

	// A combo of { IRequestor + topic } used in Map lookups
	static private class Subscriber {
		private IRequestor requestor;
		private String topic;

		public Subscriber(IRequestor requestor, String topic) {
			super();
			this.requestor = requestor;
			this.topic = topic;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((requestor == null) ? 0 : requestor.hashCode());
			result = prime * result + ((topic == null) ? 0 : topic.hashCode());
			return result;
		}

		public IRequestor getRequestor() {
			return requestor;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Subscriber other = (Subscriber) obj;
			if (requestor == null) {
				if (other.requestor != null)
					return false;
			} else if (!requestor.equals(other.requestor))
				return false;
			if (topic == null) {
				if (other.topic != null)
					return false;
			} else if (!topic.equals(other.topic))
				return false;
			return true;
		}

	}

	private Map<Subscriber, ServiceRegistration> registrations = new HashMap<Subscriber, ServiceRegistration>();

	@Inject
	@Named(E4Application.INSTANCEID)
	protected String instanceId;

	protected void addCurrentEvent(String topic, Event event) {
		synchronized (currentEvents) {
			currentEvents.put(topic, event);
		}
	}

	protected void removeCurrentEvent(String topic) {
		synchronized (currentEvents) {
			currentEvents.remove(topic);
		}
	}

	@Override
	public Object get(IObjectDescriptor descriptor, IRequestor requestor, boolean track, boolean group) {
		if (descriptor == null)
			return null;
		String topic = getTopic(descriptor);
		EventAdmin eventAdmin = getEventAdmin();
		if (topic == null || eventAdmin == null || topic.length() == 0)
			return IInjector.NOT_A_VALUE;

		if (track)
			subscribe(topic, eventAdmin, requestor);
		else
			unsubscribe(requestor);

		if (!currentEvents.containsKey(topic))
			return IInjector.NOT_A_VALUE;

		// convert to fit destination
		Class<?> descriptorsClass = getDesiredClass(descriptor.getDesiredType());
		if (descriptorsClass.equals(Event.class))
			return currentEvents.get(topic);
		return currentEvents.get(topic).getProperty(EventUtils.DATA);
	}

	private void subscribe(String topic, EventAdmin eventAdmin, IRequestor requestor) {
		Subscriber subscriber = new Subscriber(requestor, topic);
		synchronized (registrations) {
			if (registrations.containsKey(subscriber))
				return;
		}
		BundleContext bundleContext = Activator.getDefault().getBundleContext();
		if (bundleContext == null)
			throw new InjectionException("Unable to subscribe to events: org.eclipse.e4.core.di.extensions bundle is not activated"); //$NON-NLS-1$

		String[] topics = new String[] {topic};
		Dictionary<String, Object> d = new Hashtable<String, Object>();
		d.put(EventConstants.EVENT_TOPIC, topics);
		EventHandler wrappedHandler = makeHandler(topic, requestor);
		ServiceRegistration registration = bundleContext.registerService(EventHandler.class.getName(), wrappedHandler, d);
		// due to the way requestors are constructed this limited synch should be OK
		synchronized (registrations) {
			registrations.put(subscriber, registration);
		}
	}

	protected EventHandler makeHandler(String topic, IRequestor requestor) {
		return new DIEventHandler(topic, requestor);
	}

	protected String getTopic(IObjectDescriptor descriptor) {
		if (descriptor == null)
			return null;
		EventTopic qualifier = descriptor.getQualifier(EventTopic.class);
		String topic = qualifier.value();
		topic = RAPEventBroker.rapifyTopic(instanceId, topic);
		return topic;
	}

	static private EventAdmin getEventAdmin() {
		return Activator.getDefault().getEventAdmin();
	}

	protected void unsubscribe(IRequestor requestor) {
		if (requestor == null)
			return;
		synchronized (registrations) {
			Iterator<Entry<Subscriber, ServiceRegistration>> i = registrations.entrySet().iterator();
			while (i.hasNext()) {
				Entry<Subscriber, ServiceRegistration> entry = i.next();
				Subscriber key = entry.getKey();
				if (!requestor.equals(key.getRequestor()))
					continue;
				ServiceRegistration registration = entry.getValue();
				registration.unregister();
				i.remove();
			}
		}
	}

	@PreDestroy
	public void dispose() {
		ServiceRegistration[] array;
		synchronized (registrations) {
			Collection<ServiceRegistration> values = registrations.values();
			array = values.toArray(new ServiceRegistration[values.size()]);
			registrations.clear();
		}
		for (int i = 0; i < array.length; i++) {
			array[i].unregister();
		}
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
}
