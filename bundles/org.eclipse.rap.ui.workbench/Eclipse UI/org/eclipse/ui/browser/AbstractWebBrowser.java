/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
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
package org.eclipse.ui.browser;

/**
 * Clients that supply implementation of the workbench browser support should
 * extend this class for web browser instances they manage. Clients should not
 * implement the <code>IWebBrowser</code> interface.
 * 
 * @since 1.0
 */
public abstract class AbstractWebBrowser implements IWebBrowser {
	private String id;

	/**
	 * The constructor that accepts the unique browser identifier.
	 * 
	 * @param id
	 *            the unique browser identifier
	 */
	public AbstractWebBrowser(String id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.browser.IWebBrowser#getId()
	 */
	public String getId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.browser.IWebBrowser#close()
	 */
	public boolean close() {
		return false;
	}
}
