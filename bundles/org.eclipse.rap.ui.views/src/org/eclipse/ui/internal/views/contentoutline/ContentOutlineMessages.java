/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM - Initial API and implementation
 **********************************************************************/

package org.eclipse.ui.internal.views.contentoutline;

import org.eclipse.rwt.RWT;

/**
 * ContentOutlineMessages is the message class for the messages used in the content outline.
 *
 */
// RAP [fappel]: NLS needs to be session/request aware
public class ContentOutlineMessages{
	private static final String BUNDLE_NAME = "org.eclipse.ui.internal.views.contentoutline.messages";//$NON-NLS-1$

	// ==============================================================================
	// Outline View
	// ==============================================================================
	
	/**
	 * The localized message that no outline is available
	 */
	public String ContentOutline_noOutline;

	/**
	 * @return the session/request specific localized messages object
	 */
	 public static ContentOutlineMessages get() {
	    Class clazz = ContentOutlineMessages.class;
        Object result = RWT.NLS.getISO8859_1Encoded( BUNDLE_NAME, clazz );
        return ( ContentOutlineMessages )result;
     }
}