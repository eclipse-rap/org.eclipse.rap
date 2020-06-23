/*******************************************************************************
 * Copyright (c) 2008-2009 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation
 *         (from WidgetValueProperty.java)
 *     Matthew Hall - bug 294810
 ******************************************************************************/

package org.eclipse.jface.internal.databinding.swt;

import org.eclipse.core.databinding.observable.IDiff;
import org.eclipse.core.databinding.property.IProperty;
import org.eclipse.core.databinding.property.ISimplePropertyListener;
import org.eclipse.core.databinding.property.NativePropertyListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

/**
 * @param <S>
 *            type of the source object
 * @param <D>
 *            type of the diff handled by this listener
 * @since 1.4
 */
public class WidgetListener<S, D extends IDiff> extends NativePropertyListener<S, D>
		implements Listener {
	private final int[] changeEvents;
	private final int[] staleEvents;

	/**
	 * @param property
	 * @param listener
	 * @param changeEvents
	 * @param staleEvents
	 */
	public WidgetListener(IProperty property, ISimplePropertyListener<S, D> listener,
			int[] changeEvents, int[] staleEvents) {
		super(property, listener);
		this.changeEvents = changeEvents;
		this.staleEvents = staleEvents;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handleEvent(Event event) {
		if (staleEvents != null)
			for (int staleEvent : staleEvents)
				if (event.type == staleEvent) {
					fireStale((S) event.widget);
					break;
				}

		if (changeEvents != null)
			for (int changeEvent : changeEvents)
				if (event.type == changeEvent) {
					fireChange((S) event.widget, null);
					break;
				}
	}

	@Override
	protected void doAddTo(S source) {
		if (changeEvents != null) {
			for (int event : changeEvents) {
				if (event != SWT.None) {
					WidgetListenerUtil.asyncAddListener((Widget) source, event, this);
				}
			}
		}
		if (staleEvents != null) {
			for (int event : staleEvents) {
				if (event != SWT.None) {
					WidgetListenerUtil.asyncAddListener((Widget) source, event, this);
				}
			}
		}
	}

	@Override
	protected void doRemoveFrom(S source) {
		if (!((Widget) source).isDisposed()) {
			if (changeEvents != null) {
				for (int event : changeEvents) {
					if (event != SWT.None)
						WidgetListenerUtil.asyncRemoveListener((Widget) source, event, this);
				}
			}
			if (staleEvents != null) {
				for (int event : staleEvents) {
					if (event != SWT.None) {
						WidgetListenerUtil.asyncRemoveListener((Widget) source, event, this);
					}
				}
			}
		}
	}
}