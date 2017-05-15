package org.eclipse.rap.e4.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.IInjector;
import org.eclipse.e4.core.di.suppliers.ExtendedObjectSupplier;
import org.eclipse.e4.core.di.suppliers.IObjectDescriptor;
import org.eclipse.e4.core.di.suppliers.IRequestor;
import org.eclipse.e4.core.internal.contexts.ContextObjectSupplier;
import org.eclipse.e4.core.internal.contexts.EclipseContext;
import org.eclipse.e4.core.internal.contexts.IContextDisposalListener;
import org.eclipse.e4.core.internal.di.Requestor;
import org.eclipse.e4.ui.internal.workbench.swt.E4Application;
import org.eclipse.rap.e4.preferences.EPreferenceService;

@SuppressWarnings("restriction")
public abstract class RAPAbstractObjectSupplierDispatcher<S extends ExtendedObjectSupplier> extends ExtendedObjectSupplier {
	private Map<IEclipseContext, ExtendedObjectSupplier> supplierCache = Collections.synchronizedMap(new HashMap<IEclipseContext, ExtendedObjectSupplier>());
	private Map<IEclipseContext, IEclipseContext> rootContextMap = Collections.synchronizedMap(new HashMap<IEclipseContext, IEclipseContext>());
	
	private IContextDisposalListener disposeListener = new IContextDisposalListener() {
		
		@Override
		public void disposed(IEclipseContext context) {
			IEclipseContext rootContext = rootContextMap.remove(context);
			if (rootContext != null) {
				supplierCache.remove(rootContext);
			}
		}
	};
	
	private final Class<S> supplierType;
	
	public RAPAbstractObjectSupplierDispatcher(Class<S> supplierType) {
		this.supplierType = supplierType;
	}
	
	@Override
	public Object get(IObjectDescriptor descriptor, IRequestor requestor,
			boolean track, boolean group) {
		IEclipseContext c = getContext(requestor);
		
		IEclipseContext appContext = rootContextMap.get(c);
		
		if( appContext == null ) {
			IEclipseContext tmp = c;
			while( tmp != null && tmp.getLocal(E4Application.INSTANCEID) == null ) {
				tmp = tmp.getParent();
			}
			
			if( tmp == null ) {
				System.err.println("FATAL: The injection is requested on a context which has no workbench context");
				return IInjector.NOT_A_VALUE;
			}
			
			((EclipseContext)c).notifyOnDisposal(disposeListener);
			appContext = tmp;
			rootContextMap.put(c, appContext);
		}
		
		ExtendedObjectSupplier supplier = supplierCache.get(appContext);
		if( supplier == null ) {
			supplier = ContextInjectionFactory.make(supplierType, appContext);
			supplierCache.put(appContext, supplier);
		}
		
		return supplier.get(descriptor, requestor, track, group);
	}
	
	private static IEclipseContext getContext(IRequestor requestor) {
		Requestor requestorImpl = (Requestor) requestor;
		return ((ContextObjectSupplier)requestorImpl.getPrimarySupplier()).getContext();
	}
}
