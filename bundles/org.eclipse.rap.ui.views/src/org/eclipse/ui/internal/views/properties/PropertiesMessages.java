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

package org.eclipse.ui.internal.views.properties;

import org.eclipse.rwt.RWT;

/**
 * PropertiesMessages is the message class for the messages used in the properties view.
 *
 */
// RAP [fappel]: NLS needs to be session/request aware
public class PropertiesMessages {
	private static final String BUNDLE_NAME = "org.eclipse.ui.internal.views.properties.messages";//$NON-NLS-1$

	// package: org.eclipse.ui.views.properties

	// ==============================================================================
	// Properties View
	// ==============================================================================

	/** */
	public String Categories_text;
	/** */
	public String Categories_toolTip;

	/** */
	public String CopyProperty_text;

	/** */
	public String Defaults_text;
	/** */
	public String Defaults_toolTip;

	/** */
	public String Filter_text;
	/** */
	public String Filter_toolTip;

	/** */
	public String PropertyViewer_property;
	/** */
	public String PropertyViewer_value;
	/** */
	public String PropertyViewer_misc;

	/** */
	public String CopyToClipboardProblemDialog_title;
	/** */
	public String CopyToClipboardProblemDialog_message;

    /**
     * @return the session/request specific localized messages object
     */
	public static PropertiesMessages get() {
      Class clazz = PropertiesMessages.class;
      Object result = RWT.NLS.getISO8859_1Encoded( BUNDLE_NAME, clazz );
      return ( PropertiesMessages )result;
    }
}