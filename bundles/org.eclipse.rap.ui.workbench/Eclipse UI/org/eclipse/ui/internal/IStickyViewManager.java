/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.ui.internal;

import java.util.Set;

import org.eclipse.ui.IMemento;

/**
 *
 */
interface IStickyViewManager {
	
	void remove(String perspectiveId);
	
	void add(String perspectiveId, Set stickyViewSet);
	
	void clear();
	
	void update(Perspective oldPersp, Perspective newPersp);
	
	void save(IMemento memento);
	 
	void restore(IMemento memento);

}
