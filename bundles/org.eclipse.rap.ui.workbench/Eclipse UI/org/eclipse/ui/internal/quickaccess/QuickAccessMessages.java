/*******************************************************************************
 * Copyright (c) 2006, 2016 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Patrik Suzzi <psuzzi@gmail.com> - Bug 488926, 459989
 *******************************************************************************/

package org.eclipse.ui.internal.quickaccess;

//import org.eclipse.osgi.util.NLS;
import org.eclipse.rap.rwt.RWT;

/**
 * @since 3.2
 *
 */
//RAP need session aware NLS
//public class QuickAccessMessages extends NLS {
public class QuickAccessMessages  {
	private static final String BUNDLE_NAME = "org.eclipse.ui.internal.quickaccess.messages"; //$NON-NLS-1$
	public String QuickAccess_TooltipDescription;
	public String QuickAccess_TooltipDescription_Empty;
	public String QuickAccess_Perspectives;
	public String QuickAccess_Commands;
	public String QuickAccess_Properties;
	public String QuickAccess_Editors;
	public String QuickAccess_Menus;
	public String QuickAccess_New;
	public String QuickAccess_Preferences;
	public String QuickAccess_Previous;
	public String QuickAccess_Views;
	public String QuickAccess_PressKeyToShowAllMatches;
	public String QuickAccess_StartTypingToFindMatches;
	public String QuickAccess_AvailableCategories;
	public String QuickAccess_ViewWithCategory;
	public String QuickAccessContents_NoMatchingResults;
	public String QuickAccessContents_PressKeyToLimitResults;
	public String QuickAccessContents_QuickAccess;
	public String QuickAccessContents_SearchInHelpLabel;
	public String QuickAccessContents_HelpCategory;
	public String QuickAccessContents_activate;
	public String QuickAccessContents_computeMatchingEntries_displayFeedback_jobName;
	public String QuickaAcessContents_computeMatchingEntries;
	public String QuickAccessContents_processingProviderInUI;

	//RAP need session aware NLS	
//	static {
//		// initialize resource bundle
//		NLS.initializeMessages(BUNDLE_NAME, QuickAccessMessages.class);
//	}

    public static QuickAccessMessages get() {
        return RWT.NLS.getISO8859_1Encoded( BUNDLE_NAME, QuickAccessMessages.class );
      }
	
	private QuickAccessMessages() {
	}
}
