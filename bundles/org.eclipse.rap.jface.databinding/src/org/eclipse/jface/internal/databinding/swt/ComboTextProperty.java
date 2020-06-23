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
import org.eclipse.swt.widgets.Combo;

/**
 * @since 3.3
 * 
 */
public class ComboTextProperty extends WidgetStringValueProperty<Combo> {
	/**
	 * 
	 */
	public ComboTextProperty() {
		super(SWT.Modify);
	}

	@Override
	protected String doGetStringValue(Combo source) {
		return source.getText();
	}

	@Override
	protected void doSetStringValue(Combo source, String value) {
		source.setText(value != null ? value : ""); //$NON-NLS-1$
	}

	@Override
	public String toString() {
		return "Combo.text <String>"; //$NON-NLS-1$
	}
}
