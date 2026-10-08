/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
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
package org.eclipse.ui.internal;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.internal.util.Util;
import org.eclipse.ui.views.IViewDescriptor;

/**
 * A view container manages the services for a view.
 */
public class ViewSite extends PartSite implements IViewSite {
    
    public ViewSite(IViewReference ref, IViewPart view, WorkbenchPage page,
            String id, String pluginId, String registeredName) {

        super(ref, view, page);
        
        setId(id);
        setRegisteredName(registeredName);
        setPluginId(pluginId);
    }
    
    /**
     * Creates a new ViewSite.
     */
    public ViewSite(IViewReference ref, IViewPart view, WorkbenchPage page,
            IViewDescriptor desc) {
        super(ref, view, page);
        setConfigurationElement((IConfigurationElement) Util.getAdapter(desc, IConfigurationElement.class));
    }

    /**
     * Returns the secondary id or <code>null</code>.
     */
    public String getSecondaryId() {
        return ((IViewReference) getPartReference()).getSecondaryId();
    }

    /**
     * Returns the view.
     */
    public IViewPart getViewPart() {
        return (IViewPart) getPart();
    }
}
