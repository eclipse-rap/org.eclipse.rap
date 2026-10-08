/*******************************************************************************
 * Copyright (c) 2009 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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