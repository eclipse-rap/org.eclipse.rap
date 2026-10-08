/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.ui.internal.menus;

import org.eclipse.rap.rwt.RWT;

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
      return RWT.NLS.getISO8859_1Encoded( BUNDLE_NAME, CommandMessages.class );
    }
}
