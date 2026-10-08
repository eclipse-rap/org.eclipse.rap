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
package org.eclipse.ui.internal.presentations;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.presentations.IStackPresentationSite;
import org.eclipse.ui.presentations.StackPresentation;
import org.eclipse.ui.presentations.WorkbenchPresentationFactory;

/**
 * A presentation factory using native widgets.
 * <p>
 * EXPERIMENTAL
 * </p>
 * 
 */
public class NativePresentationFactory extends WorkbenchPresentationFactory {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.presentations.AbstractPresentationFactory
     */
    public StackPresentation createEditorPresentation(Composite parent,
            IStackPresentationSite site) {
        return new NativeStackPresentation(parent, site);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.presentations.AbstractPresentationFactory
     */
    public StackPresentation createViewPresentation(Composite parent,
            IStackPresentationSite site) {
        return new NativeStackPresentation(parent, site);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.presentations.AbstractPresentationFactory
     */
    public StackPresentation createStandaloneViewPresentation(Composite parent,
            IStackPresentationSite site, boolean showTitle) {
        // TODO honour showTitle
        return new NativeStackPresentation(parent, site);
    }

}
