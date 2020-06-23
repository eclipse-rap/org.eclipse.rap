/*******************************************************************************
 * Copyright (c) 2009 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 264286)
 *******************************************************************************/

package org.eclipse.jface.internal.databinding.swt;

import org.eclipse.core.databinding.property.value.DelegatingValueProperty;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.IWidgetValueProperty;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.widgets.Widget;

/**
 * @param <S>
 *            type of the source object
 * @param <T>
 *            type of the value of the property
 *
 * @since 3.3
 */
public abstract class WidgetDelegatingValueProperty<S extends Widget, T> extends DelegatingValueProperty<S, T>
		implements IWidgetValueProperty<S, T> {
	RuntimeException notSupported(Object source) {
		return new IllegalArgumentException(
				"Widget [" + source.getClass().getName() + "] is not supported."); //$NON-NLS-1$//$NON-NLS-2$
	}

	/**
	 */
	public WidgetDelegatingValueProperty() {
	}

	/**
	 * @param valueType
	 */
	public WidgetDelegatingValueProperty(Object valueType) {
		super(valueType);
	}

	@Override
	public ISWTObservableValue<T> observe(S widget) {
		return (ISWTObservableValue<T>) observe(SWTObservables.getRealm(widget
				.getDisplay()), widget);
	}

	@Override
	public ISWTObservableValue<T> observeDelayed(int delay, S widget) {
		return SWTObservables.observeDelayedValue(delay, observe(widget));
	}
}