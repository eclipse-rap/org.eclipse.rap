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
package org.eclipse.ui.internal.keys;

import org.eclipse.rap.rwt.RWT;

//import org.eclipse.osgi.util.NLS;



/**
 * The KeyAssistMessages class is the class that manages the messages
 * used in the KeyAssistDialog.
 *
 */
// RAP [if]: need session aware NLS
//public class KeyAssistMessages extends NLS {
public class KeyAssistMessages {
	private static final String BUNDLE_NAME = "org.eclipse.ui.internal.keys.KeyAssistDialog";//$NON-NLS-1$

	public String NoMatches_Message;
	public String openPreferencePage;

// RAP [if]: need session aware NLS
//	static {
//		// load message values from bundle file
//		NLS.initializeMessages(BUNDLE_NAME, KeyAssistMessages.class);
//	}

	/**
     * Load message values from bundle file
     * @return localized message
     */
    public static KeyAssistMessages get() {
      return RWT.NLS.getISO8859_1Encoded( BUNDLE_NAME, KeyAssistMessages.class );
    }
}
