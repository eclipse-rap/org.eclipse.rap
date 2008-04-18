/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.ui.internal;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.internal.misc.StatusUtil;

/**
 * 
 */
public final class StartupThreading {

	// RAP [bm]: 
//	static Workbench workbench;
	// RAPEND: [bm] 

	public static abstract class StartupRunnable implements Runnable {
		private Throwable throwable;

		public final void run() {
			try {
				runWithException();
			} catch (Throwable t) {
				this.throwable = t;
			}
		}

		public abstract void runWithException() throws Throwable;

		public Throwable getThrowable() {
			return throwable;
		}
	}

	// RAP [bm]: 
//	static void setWorkbench(Workbench wb) {
//		workbench = wb;
//	}
	// RAPEND: [bm] 

	public static void runWithWorkbenchExceptions(StartupRunnable r)
			throws WorkbenchException {
		// RAP [bm]: 
//		workbench.getDisplay().syncExec(r);
		Workbench.getInstance().getDisplay().syncExec(r);
		// RAPEND: [bm] 

		Throwable throwable = r.getThrowable();
		if (throwable != null) {
			if (throwable instanceof Error) {
				throw (Error) throwable;
			} else if (throwable instanceof RuntimeException) {
				throw (RuntimeException) throwable;
			} else if (throwable instanceof WorkbenchException) {
				throw (WorkbenchException) throwable;
			} else {
				throw new WorkbenchException(StatusUtil.newStatus(
						WorkbenchPlugin.PI_WORKBENCH, throwable));
			}
		}
	}

	public static void runWithPartInitExceptions(StartupRunnable r)
			throws PartInitException {
		// RAP [bm]: 
//		workbench.getDisplay().syncExec(r);
		Workbench.getInstance().getDisplay().syncExec(r);
		// RAPEND: [bm] 
		Throwable throwable = r.getThrowable();
		if (throwable != null) {
			if (throwable instanceof Error) {
				throw (Error) throwable;
			} else if (throwable instanceof RuntimeException) {
				throw (RuntimeException) throwable;
			} else if (throwable instanceof WorkbenchException) {
				throw (PartInitException) throwable;
			} else {
				throw new PartInitException(StatusUtil.newStatus(
						WorkbenchPlugin.PI_WORKBENCH, throwable));
			}
		}
	}

	public static void runWithThrowable(StartupRunnable r) throws Throwable {
		// RAP [bm]: 
//		workbench.getDisplay().syncExec(r);
		Workbench.getInstance().getDisplay().syncExec(r);
		// RAPEND: [bm] 

		Throwable throwable = r.getThrowable();
		if (throwable != null) {
			throw throwable;
		}
	}

	public static void runWithoutExceptions(StartupRunnable r)
			throws RuntimeException {
		// RAP [bm]: 
//		workbench.getDisplay().syncExec(r);
		Workbench.getInstance().getDisplay().syncExec(r);
		// RAPEND: [bm] 

		Throwable throwable = r.getThrowable();
		if (throwable != null) {
			if (throwable instanceof Error) {
				throw (Error) throwable;
			} else if (throwable instanceof RuntimeException) {
				throw (RuntimeException) throwable;
			} else {
				throw new RuntimeException(throwable);
			}
		}
	}

}
