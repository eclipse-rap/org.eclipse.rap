/*******************************************************************************
 * Copyright (c) 2009, 2010 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 265561)
 *     Ovidio Mallo - bug 270494
 ******************************************************************************/

package org.eclipse.jface.internal.databinding.viewers;

import org.eclipse.core.databinding.observable.IDiff;
import org.eclipse.core.databinding.property.IProperty;
import org.eclipse.core.databinding.property.ISimplePropertyListener;
import org.eclipse.core.databinding.property.NativePropertyListener;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;

class SelectionChangedListener<S extends ISelectionProvider, D extends IDiff> extends NativePropertyListener<S, D>
		implements
		ISelectionChangedListener {

	private final boolean isPostSelection;

	SelectionChangedListener(IProperty property,
			ISimplePropertyListener<S, D> listener, boolean isPostSelection) {
		super(property, listener);
		this.isPostSelection = isPostSelection;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		fireChange((S) event.getSource(), null);
	}

	@Override
	public void doAddTo(ISelectionProvider source) {
		if (isPostSelection) {
			((IPostSelectionProvider) source).addPostSelectionChangedListener(this);
		} else {
			source.addSelectionChangedListener(this);
		}
	}

	@Override
	public void doRemoveFrom(ISelectionProvider source) {
		if (isPostSelection) {
			((IPostSelectionProvider) source)
					.removePostSelectionChangedListener(this);
		} else {
			source.removeSelectionChangedListener(this);
		}
	}
}