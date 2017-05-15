/*******************************************************************************
 * Copyright (c) 2012, 2017 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Sternberg - initial API and implementation
 *    EclipseSource - ongoing development
 *******************************************************************************/
package org.eclipse.rap.e4;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.internal.workbench.E4Workbench;
import org.eclipse.e4.ui.internal.workbench.swt.E4Application;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.lifecycle.PreSave;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.rap.rwt.application.EntryPointFactory;
import org.eclipse.rap.rwt.service.ServerPushSession;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

@SuppressWarnings("restriction")
public class E4EntryPointFactory implements EntryPointFactory {

	private static final String PLUGIN_ID = "org.eclipse.e4.ui.workbench.rap";

	private String productName;

	private E4ApplicationConfig config;

	public E4EntryPointFactory(E4ApplicationConfig config) {
		this.config = config;
	}

	public EntryPoint create() {
		return new EntryPoint() {

			public int createUI() {
				try {
					// E4Starter.createAndRunApplication(productName);
					createWorkbench();
				} catch (Exception exception) {
					logProblem(exception);
				}
				return 0;
			}
		};
	}

	private int createWorkbench() {
		Display display = new Display();
		E4Application e4App = new E4Application();
		E4Workbench workbench = e4App.createE4Workbench(getApplicationContext(config),
				display);

		if( config.isDefaultPush() ) {
			ServerPushSession session = new ServerPushSession();
			session.start();
		}

//		instanceLocation = (Location) workbench.getContext().get(
//				E4Workbench.INSTANCE_LOCATION);
		Shell shell = display.getActiveShell();
		if (shell == null) {
			shell = new Shell();
			// place it off so it's not visible
			shell.setLocation(0, 10000);
		}
//		if (!checkInstanceLocation(instanceLocation, shell,
//				workbench.getContext()))
//			return EXIT_OK;

		if( workbench != null ) {
			IEclipseContext workbenchContext = workbench.getContext();
			IEclipseContext serviceContext = workbenchContext.getParent();

			// Create and run the UI (if any)
			try {
			    workbench.createAndRunUI(workbench.getApplication());
			} finally {
				e4App.saveModel();
				workbench.close();
				serviceContext.dispose();
			}

			if (workbench.isRestart()) {
				return IApplication.EXIT_RESTART;
			}
		}

		return IApplication.EXIT_OK;
	}

	private IApplicationContext getApplicationContext(final E4ApplicationConfig config) {
		return new IApplicationContext() {

			public org.osgi.framework.Bundle getBrandingBundle() {
				return null;
			}

			@Override
			public void setResult(Object result, IApplication application) {
				// TODO Auto-generated method stub

			}

			@Override
			public String getBrandingProperty(String key) {
				if( IWorkbench.XMI_URI_ARG.equals(key) ) {
					return config.getXmiURI();
				} else if( IWorkbench.LIFE_CYCLE_URI_ARG.equals(key) ) {
					return config.getLifecycleURI();
				} else if( IWorkbench.PRESENTATION_URI_ARG .equals(key)) {
					return config.getPresentationURI();
				} else if( IWorkbench.CLEAR_PERSISTED_STATE.equals(key) ) {
					return config.isClearPersistedState() + "";
				} else if( IWorkbench.PERSIST_STATE.equals(key) ) {
					return config.isPersistState() + "";
				}
				return null;
			}

			@Override
			public String getBrandingName() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getBrandingId() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getBrandingDescription() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getBrandingApplication() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Map getArguments() {
				Map<Object, Object> rv = new HashMap<Object, Object>();
				rv.put(IApplicationContext.APPLICATION_ARGS, new String[0]);
				return rv;
			}

			@Override
			public void applicationRunning() {
				// TODO Auto-generated method stub

			}
		};
	}

	private void logProblem(Throwable problem) {
//		String message = "Error when starting application, productName="
//				+ productName;
//		Logger logger = new WorkbenchLogger(PLUGIN_ID);
//		logger.error(new RuntimeException(message, problem));
		System.err.println("Problem");
		problem.printStackTrace();
	}

}
