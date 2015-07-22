/*******************************************************************************
 * Copyright (c) 2005, 2009 IBM Corporation and others.
 * Copyright (c) 2014 vogella GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Simon Scholz <simon.scholz@vogella.com> - initial API and implementation based on SWTObservable.getRealm
 *     Hannes Erven <hannes@erven.at> - getRealm() for compatibility with upstream JFACE
 *******************************************************************************/
package org.eclipse.jface.databinding.swt;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.swt.widgets.Display;

/**
 * This class is used to get a {@link Realm} for a certain {@link Display}.
 *
 * @since 3.1
 *
 */
public class DisplayRealm extends Realm {

	/**
	 * Returns the realm representing the UI thread for the given display.
	 *
	 * @param display
	 * @return the realm representing the UI thread for the given display
	 */
	public static Realm getRealm(final Display display) {
		return SWTObservables.getRealm(display);
	}

	final Display display;

	/**
	 * @param display
	 */
	DisplayRealm(Display display) {
		this.display = display;
	}

	@Override
	public boolean isCurrent() {
		return Display.getCurrent() == display;
	}

	@Override
	public void asyncExec(final Runnable runnable) {
		Runnable safeRunnable = new Runnable() {
			@Override
			public void run() {
				safeRun(runnable);
			}
		};
		if (!display.isDisposed()) {
			display.asyncExec(safeRunnable);
		}
	}

	@Override
	public void timerExec(int milliseconds, final Runnable runnable) {
		if (!display.isDisposed()) {
			Runnable safeRunnable = new Runnable() {
				@Override
				public void run() {
					safeRun(runnable);
				}
			};
			display.timerExec(milliseconds, safeRunnable);
		}
	}

	@Override
	public int hashCode() {
		return (display == null) ? 0 : display.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final DisplayRealm other = (DisplayRealm) obj;
		if (display == null) {
			if (other.display != null)
				return false;
		} else if (!display.equals(other.display))
			return false;
		return true;
	}
}