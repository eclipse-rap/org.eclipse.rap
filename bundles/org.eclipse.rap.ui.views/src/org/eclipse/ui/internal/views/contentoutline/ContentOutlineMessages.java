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
 *     IBM - Initial API and implementation
 **********************************************************************/

package org.eclipse.ui.internal.views.contentoutline;

import org.eclipse.rap.rwt.RWT;

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
        return RWT.NLS.getISO8859_1Encoded( BUNDLE_NAME, ContentOutlineMessages.class );
     }
}