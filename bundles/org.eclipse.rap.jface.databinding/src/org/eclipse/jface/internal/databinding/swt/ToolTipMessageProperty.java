/*******************************************************************************
 * Copyright (c) 2009 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 266563)
 ******************************************************************************/

package org.eclipse.jface.internal.databinding.swt;

import org.eclipse.swt.widgets.ToolTip;

/**
 * @since 1.4
 * 
 */
public class ToolTipMessageProperty extends WidgetStringValueProperty<ToolTip> {
	@Override
	protected String doGetStringValue(ToolTip source) {
		return source.getMessage();
	}

	@Override
	protected void doSetStringValue(ToolTip source, String value) {
		source.setMessage(value == null ? "" : value); //$NON-NLS-1$
	}

	@Override
	public String toString() {
		return "ToolTip.message<String>"; //$NON-NLS-1$
	}
}
