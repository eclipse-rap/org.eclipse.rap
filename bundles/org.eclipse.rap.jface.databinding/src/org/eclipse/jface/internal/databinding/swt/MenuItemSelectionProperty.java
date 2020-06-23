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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MenuItem;

/**
 * @since 1.4
 */
public class MenuItemSelectionProperty extends WidgetBooleanValueProperty<MenuItem> {
	/**
	 * 
	 */
	public MenuItemSelectionProperty() {
		super(SWT.Selection);
	}

	@Override
	protected boolean doGetBooleanValue(MenuItem source) {
		return source.getSelection();
	}

	@Override
	protected void doSetBooleanValue(MenuItem source, boolean value) {
		source.setSelection(value);
	}

	@Override
	public String toString() {
		return "MenuItem.selection <Boolean>"; //$NON-NLS-1$
	}
}
