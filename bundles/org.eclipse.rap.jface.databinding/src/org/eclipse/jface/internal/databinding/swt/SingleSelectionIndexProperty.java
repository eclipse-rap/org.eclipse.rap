/*******************************************************************************
 * Copyright (c) 2009 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 288642)
 ******************************************************************************/

package org.eclipse.jface.internal.databinding.swt;

import org.eclipse.swt.widgets.Widget;

/**
 * @param <S> type of the source object
 *
 * @since 1.4
 * 
 */
public abstract class SingleSelectionIndexProperty<S extends Widget> extends WidgetIntValueProperty<S> {
	/**
	 * @param events
	 */
	public SingleSelectionIndexProperty(int[] events) {
		super(events);
	}

	@Override
	protected void doSetValue(S source, Integer value) {
		super.doSetValue(source, value == null ? Integer.valueOf(-1) : value);
	}
}