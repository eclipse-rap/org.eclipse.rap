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
import org.eclipse.swt.widgets.List;

/**
 * @since 3.3
 * 
 */
public class ListSelectionProperty extends WidgetStringValueProperty<List> {
	/**
	 * 
	 */
	public ListSelectionProperty() {
		super(SWT.Selection);
	}

	@Override
	protected String doGetStringValue(List source) {
		int index = source.getSelectionIndex();
		if (index >= 0)
			return source.getItem(index);
		return null;
	}

	@Override
	protected void doSetStringValue(List source, String value) {
		String items[] = source.getItems();
		int index = -1;
		if (items != null && value != null) {
			for (int i = 0; i < items.length; i++) {
				if (value.equals(items[i])) {
					index = i;
					break;
				}
			}
			source.select(index);
		}
	}

	@Override
	public String toString() {
		return "List.selection <String>"; //$NON-NLS-1$
	}
}
