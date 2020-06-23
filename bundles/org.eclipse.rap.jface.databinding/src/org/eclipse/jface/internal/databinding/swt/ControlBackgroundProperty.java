/*******************************************************************************
 * Copyright (c) 2008, 2009 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 194734)
 *     Matthew Hall - bugs 195222, 263413
 ******************************************************************************/

package org.eclipse.jface.internal.databinding.swt;

import org.eclipse.jface.databinding.swt.WidgetValueProperty;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;

/**
 * @param <S> type of the source object
 *
 * @since 3.3
 * 
 */
public class ControlBackgroundProperty<S extends Control> extends WidgetValueProperty<S, Color> {
	@Override
	public Object getValueType() {
		return Color.class;
	}

	@Override
	protected Color doGetValue(S source) {
		return source.getBackground();
	}

	@Override
	protected void doSetValue(S source, Color value) {
		source.setBackground(value);
	}

	@Override
	public String toString() {
		return "Control.background <Color>"; //$NON-NLS-1$
	}
}
