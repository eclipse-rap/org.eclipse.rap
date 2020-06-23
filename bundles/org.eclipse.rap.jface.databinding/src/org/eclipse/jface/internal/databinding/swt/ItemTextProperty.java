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

import org.eclipse.swt.widgets.Item;

/**
 * @since 3.3
 * 
 */
public class ItemTextProperty extends WidgetStringValueProperty<Item> {
	@Override
	protected String doGetStringValue(Item source) {
		return source.getText();
	}

	@Override
	protected void doSetStringValue(Item source, String value) {
		source.setText(value == null ? "" : value); //$NON-NLS-1$
	}

	@Override
	public String toString() {
		return "Item.text <String>"; //$NON-NLS-1$
	}
}
