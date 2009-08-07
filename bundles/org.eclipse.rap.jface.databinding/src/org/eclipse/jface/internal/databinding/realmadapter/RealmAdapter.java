/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.jface.internal.databinding.realmadapter;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.widgets.Display;

/**
 * An Adapter class that allows to mark the dependencies from the workbench to
 * the databinding bundles as optional.
 * 
 * <p>Note that this class is not meant to be used by clients. </p>
 */
public class RealmAdapter implements Runnable {
	
	private Display display;
	private Runnable runnable;

	/**
	 * Creates a new instance of RealAdapter.
	 * 
	 * @param display the display the default <code>Realm</code> is assigned to.
	 * @param runnable the runnable that will spin the UI loop.
	 */
	public RealmAdapter( final Display display, final Runnable runnable ) {
		this.display = display;
		this.runnable = runnable;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		Realm.runWithDefault(SWTObservables.getRealm(display), runnable);
	}
}
