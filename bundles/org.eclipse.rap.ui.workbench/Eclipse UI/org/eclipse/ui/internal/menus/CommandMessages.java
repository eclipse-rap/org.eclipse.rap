/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.ui.internal.menus;

import org.eclipse.rwt.RWT;

//import org.eclipse.osgi.util.NLS;

/**
 *
 * @since 3.5
 *
 */
// RAP [if]: need session aware NLS
//public class CommandMessages extends NLS {
public class CommandMessages {

	private static final String BUNDLE_NAME = "org.eclipse.ui.internal.menus.messages";//$NON-NLS-1$

// RAP [if]: need session aware NLS
//	static {
//		// load message values from bundle file
//		NLS.initializeMessages(BUNDLE_NAME, CommandMessages.class);
//	}

	public String Tooltip_Accelerator;

	/**
     * Load message values from bundle file
     * @return localized message
     */
    public static CommandMessages get() {
      Class clazz = CommandMessages.class;
      Object result = RWT.NLS.getISO8859_1Encoded( BUNDLE_NAME, clazz );
      return ( CommandMessages )result;
    }
}
