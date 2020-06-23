/*******************************************************************************
 * Copyright (c) 2008, 2009 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 194734)
 *     Matthew Hall - bugs 195222, 256543, 263413, 262287
 ******************************************************************************/

package org.eclipse.jface.internal.databinding.swt;

import org.eclipse.jface.databinding.swt.WidgetValueProperty;
import org.eclipse.swt.widgets.Widget;

/**
 * @param <S> type of the source object
 *
 * @since 3.3
 * 
 */
public abstract class WidgetStringValueProperty<S extends Widget> extends WidgetValueProperty<S, String> {
	WidgetStringValueProperty() {
		super();
	}

	WidgetStringValueProperty(int event) {
		super(event);
	}

	WidgetStringValueProperty(int[] events) {
		super(events);
	}

	WidgetStringValueProperty(int[] events, int[] staleEvents) {
		super(events, staleEvents);
	}

	@Override
	public Object getValueType() {
		return String.class;
	}

	@Override
	protected String doGetValue(S source) {
		return doGetStringValue(source);
	}

	@Override
	protected void doSetValue(S source, String value) {
		doSetStringValue(source, value);
	}

	protected abstract String doGetStringValue(S source);

	protected abstract void doSetStringValue(S source, String value);
}
