/*******************************************************************************
 * Copyright (c) 2008, 2009 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 213893)
 *     Matthew Hall - bug 263413
 ******************************************************************************/

package org.eclipse.jface.internal.databinding.swt;

import org.eclipse.jface.databinding.swt.WidgetValueProperty;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Widget;

/**
 * @param <S> type of the source object
 *
 * @since 3.3
 * 
 */
public abstract class WidgetImageValueProperty<S extends Widget> extends WidgetValueProperty<S, Image> {
	@Override
	public Object getValueType() {
		return Image.class;
	}

	@Override
	protected Image doGetValue(S source) {
		return doGetImageValue(source);
	}

	@Override
	protected void doSetValue(S source, Image value) {
		doSetImageValue(source, value);
	}

	protected abstract Image doGetImageValue(S source);

	protected abstract void doSetImageValue(S source, Image value);
}
