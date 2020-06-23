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

import org.eclipse.swt.widgets.Text;

/**
 * @since 1.4
 * 
 */
public class TextMessageProperty extends WidgetStringValueProperty<Text> {
	@Override
	protected String doGetStringValue(Text source) {
		return source.getMessage();
	}

	@Override
	protected void doSetStringValue(Text source, String value) {
		source.setMessage(value == null ? "" : value); //$NON-NLS-1$
	}

	@Override
	public String toString() {
		return "Text.message<String>"; //$NON-NLS-1$
	}
}
