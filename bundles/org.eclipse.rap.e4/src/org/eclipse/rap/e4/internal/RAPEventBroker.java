package org.eclipse.rap.e4.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.internal.workbench.swt.E4Application;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

public class RAPEventBroker implements IEventBroker {
	    // TBD synchronization

	    private static final String	ASYNC_EVENT = "rap.async.event";

		private Map<EventHandler, Collection<ServiceRegistration<?>>> registrations = new HashMap<EventHandler, Collection<ServiceRegistration<?>>>();

		@Inject
		Logger logger;

		@Inject
		@Optional
		UISynchronize uiSync;

		@Inject
		@Named(E4Application.INSTANCEID)
		String instanceId;

		// This is a temporary code to ensure that bundle containing
		// EventAdmin implementation is started. This code it to be removed once
		// the proper method to start EventAdmin is added.
		static {
			EventAdmin eventAdmin = Activator.getDefault().getEventAdmin();
			if (eventAdmin == null) {
				Bundle[] bundles = Activator.getDefault().getBundleContext().getBundles();
				for (Bundle bundle : bundles) {
					if (!"org.eclipse.equinox.event".equals(bundle.getSymbolicName()))
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

		public RAPEventBroker() {
			// placeholder
		}

		public boolean send(String topic, Object data) {
			Event event = constructEvent(topic, data, false);
			EventAdmin eventAdmin = Activator.getDefault().getEventAdmin();
			if (eventAdmin == null) {
				logger.error(NLS.bind("No EventAdmin", event.toString()));
				return false;
			}
			eventAdmin.sendEvent(event);
			return true;
		}

		public boolean post(String topic, Object data) {
			Event event = constructEvent(topic, data, true);
			EventAdmin eventAdmin = Activator.getDefault().getEventAdmin();
			if (eventAdmin == null) {
				logger.error(NLS.bind("No EventAdmin", event.toString()));
				return false;
			}
			eventAdmin.postEvent(event);
			return true;
		}

		@SuppressWarnings("unchecked")
		private Event constructEvent(String topic, Object data, boolean async) {
			topic = rapifyTopic(instanceId, topic);
			Event event;
			if (data instanceof Dictionary<?,?>) {
				Dictionary<String,Object> properties = (Dictionary<String,Object>)data;
				if (async)
					properties.put(ASYNC_EVENT, Boolean.TRUE);
				event = new Event(topic, properties);
			} else if (data instanceof Map<?,?>) {
				Map<String,Object> properties = (Map<String,Object>)data;
				if (async)
					properties.put(ASYNC_EVENT, Boolean.TRUE);
				event = new Event(topic, properties);
			} else {
				Dictionary<String, Object> d = new Hashtable<String, Object>(3);
				d.put(EventConstants.EVENT_TOPIC, topic);
				if (data != null)
					d.put(IEventBroker.DATA, data);
				if (async)
					d.put(ASYNC_EVENT, Boolean.TRUE);
				event = new Event(topic, d);
			}
			return event;
		}

		public boolean subscribe(String topic, EventHandler eventHandler) {
			return subscribe(topic, null, eventHandler, false);
		}

		public boolean subscribe(String topic, String filter, EventHandler eventHandler, boolean headless) {
			topic = rapifyTopic(instanceId, topic);
			BundleContext bundleContext = Activator.getDefault().getBundleContext();
			if (bundleContext == null) {
				logger.error(NLS.bind("No EventAdmin", topic));
				return false;
			}
			String[] topics = new String[] {topic};
			Dictionary<String, Object> d = new Hashtable<String, Object>();
			d.put(EventConstants.EVENT_TOPIC, topics);
			if (filter != null)
				d.put(EventConstants.EVENT_FILTER, filter);
			EventHandler wrappedHandler = new RAPUIEventHandler(eventHandler, headless ? null : uiSync);
			ServiceRegistration<?> registration = bundleContext.registerService(
					EventHandler.class.getName(), wrappedHandler, d);
			Collection<ServiceRegistration<?>> handled = registrations
					.get(eventHandler);
			if (handled == null) {
				registrations.put(eventHandler,
						handled = new ArrayList<ServiceRegistration<?>>());
			}
			handled.add(registration);
			return true;
		}

		public boolean unsubscribe(EventHandler eventHandler) {
			Collection<ServiceRegistration<?>> handled = registrations
					.remove(eventHandler);
			if (handled == null || handled.isEmpty())
				return false;
			for (ServiceRegistration<?> r : handled) {
				r.unregister();
			}
			return true;
		}

		@PreDestroy
		void dispose() {
			Collection<Collection<ServiceRegistration<?>>> values = new ArrayList<Collection<ServiceRegistration<?>>>(
					registrations.values());
			registrations.clear();
			for (Collection<ServiceRegistration<?>> handled : values) {
				for (ServiceRegistration<?> registration : handled) {
					// System.out.println("EventBroker dispose:" + registration[i] +
					// ")");
					registration.unregister();
				}
			}
		}

		public static String rapifyTopic(String instanceId, String topic) {
			String rv = instanceId + "/" + topic;
//			System.err.println("Original: " + topic + ", RAPified: " + rv);
			return rv;
		}

		public static boolean isAsyncEvent(Event event) {
			return Boolean.TRUE.equals(event.getProperty(ASYNC_EVENT));
		}
}
