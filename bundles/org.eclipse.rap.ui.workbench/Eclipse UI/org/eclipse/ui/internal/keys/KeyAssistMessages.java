/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.internal.keys;

import org.eclipse.rwt.RWT;

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
      Class clazz = KeyAssistMessages.class;
      Object result = RWT.NLS.getISO8859_1Encoded( BUNDLE_NAME, clazz );
      return ( KeyAssistMessages )result;
    }
}
