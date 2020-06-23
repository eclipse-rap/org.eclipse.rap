/*******************************************************************************
 * Copyright (c) 2008, 2009 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 194734)
 *     Matthew Hall - bug 195222, 263413
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
public abstract class WidgetBooleanValueProperty<S extends Widget> extends WidgetValueProperty<S, Boolean> {
	WidgetBooleanValueProperty() {
		super();
	}

	WidgetBooleanValueProperty(int event) {
		super(event);
	}

	WidgetBooleanValueProperty(int[] events) {
		super(events);
	}

	@Override
	public Object getValueType() {
		return Boolean.TYPE;
	}

	@Override
	protected Boolean doGetValue(S source) {
		return doGetBooleanValue(source);
	}

	@Override
	protected void doSetValue(S source, Boolean value) {
		if (value == null)
			value = false;
		doSetBooleanValue(source, value);
	}

	protected abstract boolean doGetBooleanValue(S source);

	protected abstract void doSetBooleanValue(S source, boolean value);
}