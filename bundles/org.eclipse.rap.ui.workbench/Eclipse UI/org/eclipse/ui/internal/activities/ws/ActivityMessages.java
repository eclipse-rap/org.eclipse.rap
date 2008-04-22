/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.internal.activities.ws;

import org.eclipse.rwt.RWT;
import org.eclipse.ui.internal.WorkbenchMessages;


/**
 * The ActivtyMessages are the messages used by the activities
 * support.
 *
 */
// RAP [fappel]: different NLS due to multiple user/session capability
//  public class ActivityMessages extends NLS {
public class ActivityMessages {
	private static final String BUNDLE_NAME = "org.eclipse.ui.internal.activities.ws.messages";//$NON-NLS-1$
	
// RAP [fappel]: different NLS due to multiple user/session capability
//	public static String ActivityEnabler_description;
//	public static String ActivityEnabler_activities;
//    public static String ActivityEnabler_categories;
//	public static String ActivityEnabler_selectAll;
//	public static String ActivityEnabler_deselectAll;
//    public static String ActivitiesPreferencePage_advancedDialogTitle;
//    public static String ActivitiesPreferencePage_advancedButton;
//    public static String ActivitiesPreferencePage_lockedMessage;
//    public static String ActivitiesPreferencePage_captionMessage;
//    public static String ActivitiesPreferencePage_requirements;
//	public static String ManagerTask;
//	public static String ManagerWindowSubTask;
//	public static String ManagerViewsSubTask;
//	public static String Perspective_showAll;
//	public static String activityPromptButton;
//	public static String activityPromptToolTip;
	public String ActivityEnabler_description;
	public String ActivityEnabler_activities;
    public String ActivityEnabler_categories;
	public String ActivityEnabler_selectAll;
	public String ActivityEnabler_deselectAll;
    public String ActivitiesPreferencePage_advancedDialogTitle;
    public String ActivitiesPreferencePage_advancedButton;
    public String ActivitiesPreferencePage_lockedMessage;
    public String ActivitiesPreferencePage_captionMessage;
    public String ActivitiesPreferencePage_requirements;
	public String ManagerTask;
	public String ManagerWindowSubTask;
	public String ManagerViewsSubTask;
	public String Perspective_showAll;
	public String activityPromptButton;
	public String activityPromptToolTip;

// RAP [fappel]: different NLS due to multiple user/session capability
//	static {
//		// load message values from bundle file
//		NLS.initializeMessages(BUNDLE_NAME, ActivityMessages.class);
//	}
    public static ActivityMessages get() {
      Class clazz = WorkbenchMessages.class;
      Object result = RWT.NLS.getISO8859_1Encoded( BUNDLE_NAME, clazz );
      return ( ActivityMessages )result;
    }
}
