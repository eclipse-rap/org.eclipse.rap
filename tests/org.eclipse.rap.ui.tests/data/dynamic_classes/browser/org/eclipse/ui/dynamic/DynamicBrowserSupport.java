/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
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
package org.eclipse.ui.dynamic;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.browser.AbstractWorkbenchBrowserSupport;
import org.eclipse.ui.browser.IWebBrowser;

/**
 * @since 3.1
 */
public class DynamicBrowserSupport extends AbstractWorkbenchBrowserSupport {

    /**
     * 
     */
    public DynamicBrowserSupport() {
        super();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.browser.IWorkbenchBrowserSupport#createBrowser(int, java.lang.String, java.lang.String, java.lang.String)
     */
    public IWebBrowser createBrowser(int style, String browserId, String name,
            String tooltip) throws PartInitException {
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.browser.IWorkbenchBrowserSupport#createBrowser(java.lang.String)
     */
    public IWebBrowser createBrowser(String browserId) throws PartInitException {
        return null;
    }

}
