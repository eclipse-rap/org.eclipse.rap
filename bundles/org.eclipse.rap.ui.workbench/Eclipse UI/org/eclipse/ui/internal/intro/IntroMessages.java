/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * IBM - Initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.internal.intro;

import org.eclipse.rap.rwt.RWT;


/**
 * The IntroMessages are the messages used in the intro support.
 */
public class IntroMessages {
//public class IntroMessages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.ui.internal.intro.intro";//$NON-NLS-1$

	// RAP [bm]: I18n
//	public static String Intro_could_not_create_part;
//	public static String Intro_could_not_create_proxy;
//	public static String Intro_could_not_create_descriptor;
//	public static String Intro_action_text;
//	public static String Intro_default_title;
//    public static String Intro_missing_product_title;
//    public static String Intro_missing_product_message;

	public String Intro_could_not_create_part;
	public String Intro_could_not_create_proxy;
	public String Intro_could_not_create_descriptor;
	public String Intro_action_text;
	public String Intro_default_title;
    public String Intro_missing_product_title;
    public String Intro_missing_product_message;
    // ENDRAP


	// RAP [bm]: different NLS due to multiple user/session capability
//    static {
//    	// load message values from bundle file
//    	NLS.initializeMessages(BUNDLE_NAME, IntroMessages.class);
//    }
    public static IntroMessages get() {
      return RWT.NLS.getISO8859_1Encoded( BUNDLE_NAME, IntroMessages.class );
    }
}
