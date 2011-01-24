/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.ui.internal;

import org.eclipse.rwt.RWT;

//import org.eclipse.osgi.util.NLS;

// RAP [if]: need session aware NLS
//public class ExtensionEventHandlerMessages extends NLS {
public class ExtensionEventHandlerMessages {
	private static final String BUNDLE_NAME = "org.eclipse.ui.internal.ExtensionEventHandler";//$NON-NLS-1$
	//
	// Copyright (c) 2003, 2004 IBM Corporation and others.
	// All rights reserved. This program and the accompanying materials
	// are made available under the terms of the Eclipse Public License v1.0
	// which accompanies this distribution, and is available at
	// http://www.eclipse.org/legal/epl-v10.html
	//
	// Contributors:
	//     IBM Corporation - initial API and implementation
	//
	public String ExtensionEventHandler_new_action_set;
	public String ExtensionEventHandler_following_changes;
	public String ExtensionEventHandler_change_format;
	public String ExtensionEventHandler_need_to_reset;
	public String ExtensionEventHandler_reset_perspective;

// RAP [if]: need session aware NLS
//	static {
//		// load message values from bundle file
//		NLS.initializeMessages(BUNDLE_NAME, ExtensionEventHandlerMessages.class);
//	}

	/**
     * Load message values from bundle file
     * @return localized message
     */
    public static ExtensionEventHandlerMessages get() {
      Class clazz = ExtensionEventHandlerMessages.class;
      Object result = RWT.NLS.getISO8859_1Encoded( BUNDLE_NAME, clazz );
      return ( ExtensionEventHandlerMessages )result;
    }
}