/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
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
package org.eclipse.ui.internal.presentations.defaultpresentation;

import org.eclipse.ui.internal.preferences.AbstractBooleanListener;
import org.eclipse.ui.internal.preferences.IDynamicPropertyMap;

/**
 */
public final class DefaultMultiTabListener extends AbstractBooleanListener {

    private DefaultTabFolder folder;
    
    /**
     * @param map
     * @param propertyId
     * @param defaultValue
     */
    public DefaultMultiTabListener(IDynamicPropertyMap map, String propertyId, DefaultTabFolder folder) {
        super();
        
        this.folder = folder;
        
        attach(map, propertyId, true);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.internal.preferences.AbstractBooleanListener#handleValue(boolean)
     */
    protected void handleValue(boolean b) {
        folder.setSingleTab(!b);
    }

}
