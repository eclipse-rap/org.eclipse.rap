/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.eclipse.ui.internal.testing;

import org.eclipse.rwt.RWT;

//import org.eclipse.osgi.util.NLS;

/**
 * @since 3.6
 *
 */
// RAP [if]: need session aware NLS
//public class ContributionInfoMessages extends NLS {
public class ContributionInfoMessages {
	private static final String BUNDLE_NAME = "org.eclipse.ui.internal.testing.messages";//$NON-NLS-1$


	public String ContributionInfo_Editor;
	public String ContributionInfo_View;
	public String ContributionInfo_ActionSet;
	public String ContributionInfo_Category;
	public String ContributionInfo_ColorDefinition;
	public String ContributionInfo_Wizard;
	public String ContributionInfo_Perspective;
	public String ContributionInfo_Page;
	public String ContributionInfo_EarlyStartupPlugin;
	public String ContributionInfo_Unknown;
	public String ContributionInfo_Job;
	public String ContributionInfo_TableItem;
	public String ContributionInfo_TreeItem;
	public String ContributionInfo_Window;
	public String ContributionInfo_LabelDecoration;
	public String ContributionInfo_ViewContent;

	public String ContributionInfo_ContributedBy;

// RAP [if]: need session aware NLS
//	static {
//		// load message values from bundle file
//		NLS.initializeMessages(BUNDLE_NAME, ContributionInfoMessages.class);
//	}

	/**
     * Load message values from bundle file
     * @return localized message
     */
    public static ContributionInfoMessages get() {
      Class clazz = ContributionInfoMessages.class;
      Object result = RWT.NLS.getISO8859_1Encoded( BUNDLE_NAME, clazz );
      return ( ContributionInfoMessages )result;
    }

}
