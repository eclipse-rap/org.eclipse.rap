/*******************************************************************************
 * Copyright (c) 2010 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 299123)
 ******************************************************************************/

package org.eclipse.jface.internal.databinding.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Slider;

/**
 * @since 1.4
 */
public class SliderSelectionProperty extends WidgetIntValueProperty<Slider> {
	/**
	 *
	 */
	public SliderSelectionProperty() {
		super(SWT.Selection);
	}

	@Override
	protected int doGetIntValue(Slider source) {
		return source.getSelection();
	}

	@Override
	protected void doSetIntValue(Slider source, int value) {
		source.setSelection(value);
	}

	@Override
	public String toString() {
		return "Slider.selection <int>"; //$NON-NLS-1$
	}
}
