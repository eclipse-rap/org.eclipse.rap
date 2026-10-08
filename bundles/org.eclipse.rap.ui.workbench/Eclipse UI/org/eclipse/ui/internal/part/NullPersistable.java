/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.internal.part;

import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistable;

/**
 */
public class NullPersistable implements IPersistable {

    /* (non-Javadoc)
     * @see org.eclipse.ui.internal.part.components.interfaces.IPersistable#saveState(org.eclipse.ui.IMemento)
     */
    public void saveState(IMemento memento) {
    }

}
