/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM - Initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.internal.progress;

import org.eclipse.rwt.RWT;

// RAP [fappel]: NLS needs to be session/request aware
//public class ProgressMessages extends NLS{
public class ProgressMessages {
	private static final String BUNDLE_NAME = "org.eclipse.ui.internal.progress.messages";//$NON-NLS-1$

//	public static String PendingUpdateAdapter_PendingLabel;
//	public static String JobInfo_DoneMessage;
//	public static String JobInfo_DoneNoProgressMessage;
//	public static String JobInfo_NoTaskNameDoneMessage;
//	public static String JobsViewPreferenceDialog_Note;
//	public static String JobErrorDialog_CustomJobText;
//	public static String JobInfo_UnknownProgress;
//	public static String JobInfo_Waiting;
//	public static String JobInfo_Sleeping;
//	public static String JobInfo_System;
//	public static String JobInfo_Cancelled;
//	public static String JobInfo_Cancel_Requested;
//	public static String JobInfo_Error;
//	public static String JobInfo_Blocked;
//	public static String JobInfo_Finished;
//	public static String JobInfo_FinishedAt;
//	public static String JobErrorDialog_CloseDialogMessage;
//	public static String Error;
//	public static String DeferredTreeContentManager_NotDeferred;
//	public static String DeferredTreeContentManager_AddingChildren;
//	public static String DeferredTreeContentManager_FetchingName;
//	public static String ProgressView_CancelAction;
//	public static String ProgressView_ClearAllAction;
//	public static String ProgressView_NoOperations;
//	
//	public static String NewProgressView_RemoveAllJobsToolTip;
//	public static String NewProgressView_CancelJobToolTip;
//	public static String NewProgressView_ClearJobToolTip;
//	public static String NewProgressView_errorDialogTitle;
//	public static String NewProgressView_errorDialogMessage;
//	public static String ProgressAnimationItem_tasks;
//	public static String ProgressAnimationItem_ok;
//	public static String ProgressAnimationItem_error;
//	public static String SubTaskInfo_UndefinedTaskName;
//	public static String DeferredTreeContentManager_ClearJob;
//	public static String ProgressContentProvider_UpdateProgressJob;
//	public static String JobErrorDialog_MultipleErrorsTitle;
//	public static String ProgressManager_openJobName;
//	public static String ProgressManager_showInDialogName;
//	public static String ProgressMonitorJobsDialog_DetailsTitle;
//	public static String ProgressMonitorJobsDialog_HideTitle;
//	public static String ErrorNotificationManager_OpenErrorDialogJob;
//	public static String AnimationManager_AnimationStart;
//	public static String ProgressFloatingWindow_EllipsisValue;
//	public static String BlockedJobsDialog_UserInterfaceTreeElement;
//	public static String BlockedJobsDialog_BlockedTitle;
//	public static String WorkbenchSiteProgressService_CursorJob;
//	public static String ProgressMonitorFocusJobDialog_UserDialogJob;
//	public static String ProgressMonitorFocusJobDialog_CLoseDialogJob;
//	public static String ProgressMonitorFocusJobDialog_RunInBackgroundButton;
//
//	public static String JobErrorDialog_MultipleErrorsMessage;
//	public static String JobErrorDialog_CloseDialogTitle;
//	public static String JobsViewPreferenceDialog_Title;
//	public static String JobErrorDialog_DoNotShowAgainMessage;
	public String PendingUpdateAdapter_PendingLabel;
	public String JobInfo_DoneMessage;
	public String JobInfo_DoneNoProgressMessage;
	public String JobInfo_NoTaskNameDoneMessage;
	public String JobsViewPreferenceDialog_Note;
	public String JobErrorDialog_CustomJobText;
	public String JobInfo_UnknownProgress;
	public String JobInfo_Waiting;
	public String JobInfo_Sleeping;
	public String JobInfo_System;
	public String JobInfo_Cancelled;
	public String JobInfo_Cancel_Requested;
	public String JobInfo_Error;
	public String JobInfo_Blocked;
	public String JobInfo_Finished;
	public String JobInfo_FinishedAt;
	public String JobErrorDialog_CloseDialogMessage;
	public String Error;
	public String DeferredTreeContentManager_NotDeferred;
	public String DeferredTreeContentManager_AddingChildren;
	public String DeferredTreeContentManager_FetchingName;
	public String ProgressView_CancelAction;
	public String ProgressView_ClearAllAction;
	public String ProgressView_NoOperations;
	
	public String NewProgressView_RemoveAllJobsToolTip;
	public String NewProgressView_CancelJobToolTip;
	public String NewProgressView_ClearJobToolTip;
	public String NewProgressView_errorDialogTitle;
	public String NewProgressView_errorDialogMessage;
	public String ProgressAnimationItem_tasks;
	public String ProgressAnimationItem_ok;
	public String ProgressAnimationItem_error;
	public String SubTaskInfo_UndefinedTaskName;
	public String DeferredTreeContentManager_ClearJob;
	public String ProgressContentProvider_UpdateProgressJob;
	public String JobErrorDialog_MultipleErrorsTitle;
	public String ProgressManager_openJobName;
	public String ProgressManager_showInDialogName;
	public String ProgressMonitorJobsDialog_DetailsTitle;
	public String ProgressMonitorJobsDialog_HideTitle;
	public String ErrorNotificationManager_OpenErrorDialogJob;
	public String AnimationManager_AnimationStart;
	public String ProgressFloatingWindow_EllipsisValue;
	public String BlockedJobsDialog_UserInterfaceTreeElement;
	public String BlockedJobsDialog_BlockedTitle;
	public String WorkbenchSiteProgressService_CursorJob;
	public String ProgressMonitorFocusJobDialog_UserDialogJob;
	public String ProgressMonitorFocusJobDialog_CLoseDialogJob;
	public String ProgressMonitorFocusJobDialog_RunInBackgroundButton;

	public String JobErrorDialog_MultipleErrorsMessage;
	public String JobErrorDialog_CloseDialogTitle;
	public String JobsViewPreferenceDialog_Title;
	public String JobErrorDialog_DoNotShowAgainMessage;

//	static {
//		// load message values from bundle file
//		NLS.initializeMessages(BUNDLE_NAME, ProgressMessages.class);
//	}
	
    public static ProgressMessages get() {
      Class clazz = ProgressMessages.class;
      Object result = RWT.NLS.getISO8859_1Encoded( BUNDLE_NAME, clazz );
      return ( ProgressMessages )result;
    }
}
