/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM - Initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.internal.intro;

import org.eclipse.rwt.RWT;


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
      Class clazz = IntroMessages.class;
      Object result = RWT.NLS.getISO8859_1Encoded( BUNDLE_NAME, clazz );
      return ( IntroMessages )result;
    }
}
