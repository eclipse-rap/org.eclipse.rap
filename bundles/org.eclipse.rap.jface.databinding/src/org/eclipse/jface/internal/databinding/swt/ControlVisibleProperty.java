/*******************************************************************************
 * Copyright (c) 2008, 2009 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 194734)
 ******************************************************************************/

package org.eclipse.jface.internal.databinding.swt;

import org.eclipse.swt.widgets.Control;

/**
 * @param <S> type of the source object
 *
 * @since 3.3
 * 
 */
public class ControlVisibleProperty<S extends Control> extends WidgetBooleanValueProperty<S> {
	@Override
	protected boolean doGetBooleanValue(S source) {
		return source.getVisible();
	}

	@Override
	protected void doSetBooleanValue(S source, boolean value) {
		source.setVisible(value);
	}

	@Override
	public String toString() {
		return "Control.visible <boolean>"; //$NON-NLS-1$
	}
}
