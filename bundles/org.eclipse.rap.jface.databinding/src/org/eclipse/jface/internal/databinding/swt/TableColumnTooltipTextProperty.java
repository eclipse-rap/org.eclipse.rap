/*******************************************************************************
 * Copyright (c) 2008, 2009 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 194734)
 *     Matthew Hall - bug 262946
 ******************************************************************************/

package org.eclipse.jface.internal.databinding.swt;

import org.eclipse.swt.widgets.TableColumn;

/**
 * @since 3.3
 * 
 */
public class TableColumnTooltipTextProperty extends WidgetStringValueProperty<TableColumn> {
	@Override
	protected String doGetStringValue(TableColumn source) {
		return source.getToolTipText();
	}

	@Override
	protected void doSetStringValue(TableColumn source, String value) {
		source.setToolTipText(value);
	}

	@Override
	public String toString() {
		return "TableColumn.toolTipText <String>"; //$NON-NLS-1$
	}
}
