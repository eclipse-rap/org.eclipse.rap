///*******************************************************************************
// * Copyright (c) 2008, 2010 IBM Corporation and others.
// * All rights reserved. This program and the accompanying materials
// * are made available under the terms of the Eclipse Public License 2.0
// * which is available at
// * https://www.eclipse.org/legal/epl-2.0
// *
// * SPDX-License-Identifier: EPL-2.0
// *
// * Contributors:
// *     IBM Corporation - initial API and implementation
// *******************************************************************************/
//package org.eclipse.ui.tests;
//
//import org.eclipse.equinox.app.IApplication;
//import org.eclipse.equinox.app.IApplicationContext;
//import org.eclipse.swt.widgets.Display;
//import org.eclipse.ui.IWorkbench;
//import org.eclipse.ui.PlatformUI;
//import org.eclipse.ui.tests.harness.util.RCPTestWorkbenchAdvisor;
//
//public class RCPSessionApplication implements IApplication {
//
//	private boolean windowlessApp = false;
//
//	public Object start(IApplicationContext context) throws Exception {
//		Display display = PlatformUI.createDisplay();
//		try {
//			PlatformUI.createAndRunWorkbench(display, new RCPTestWorkbenchAdvisor(windowlessApp));
//		} finally {
//			if (display != null)
//				display.dispose();
//		}
//		return EXIT_OK;
//	}
//
//	public void stop() {
//		final IWorkbench workbench = PlatformUI.getWorkbench();
//		if (workbench == null)
//			return;
//		final Display display = workbench.getDisplay();
//		display.syncExec(new Runnable() {
//			public void run() {
//				if (!display.isDisposed())
//					workbench.close();
//			}
//		});
//	}
//
//	/**
//	 * @param windowlessApp The windowlessApp to set.
//	 */
//	public void setWindowlessApp(boolean windowlessApp) {
//		this.windowlessApp = windowlessApp;
//	}
//
//}