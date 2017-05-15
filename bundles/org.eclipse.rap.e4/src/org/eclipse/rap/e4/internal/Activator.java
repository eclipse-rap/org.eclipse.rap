package org.eclipse.rap.e4.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.EventAdmin;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {
	private static Activator singleton;
	
	private ServiceRegistration contextServiceReg; 
	private ServiceRegistration handlerServiceReg;
	
	private ServiceTracker eventAdminTracker;
	private BundleContext bundleContext;

	/*
	 * Returns the singleton for this Activator. Callers should be aware that
	 * this will return null if the bundle is not active.
	 */
	public static Activator getDefault() {
		return singleton;
	}

	public void start(BundleContext context) throws Exception {
		bundleContext = context;
		singleton = this;
	}
	
	/*
	 * Return the debug options service, if available.
	 */
	public EventAdmin getEventAdmin() {
		if (eventAdminTracker == null) {
			eventAdminTracker = new ServiceTracker(bundleContext, EventAdmin.class.getName(), null);
			eventAdminTracker.open();
		}
		return (EventAdmin) eventAdminTracker.getService();
	}

	public void stop(BundleContext context) throws Exception {
		if (contextServiceReg != null) {
			contextServiceReg.unregister();
			contextServiceReg = null;
		}
		if (handlerServiceReg != null) {
			handlerServiceReg.unregister();
			handlerServiceReg = null;
		}
		
		if (eventAdminTracker != null) {
			eventAdminTracker.close();
			eventAdminTracker = null;
		}
		bundleContext = null;
		singleton = null;
	}
	
	public BundleContext getBundleContext() {
		return bundleContext;
	}
}