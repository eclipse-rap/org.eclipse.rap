/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.internal.cheatsheets;

//import org.eclipse.osgi.util.NLS;
import org.eclipse.rwt.RWT;

// RAP [if]: need session aware NLS
//public final class Messages extends NLS {
public final class Messages {

	private static final String BUNDLE_NAME = "org.eclipse.ui.internal.cheatsheets.Messages";//$NON-NLS-1$

	private Messages() {
		// Do not instantiate
	}

	public String ERROR_READING_STATE_FILE;
	public String ERROR_WRITING_STATE_FILE;
	public String CHEAT_SHEET_SELECTION_DIALOG_TITLE;
	public String CHEAT_SHEET_SELECTION_DIALOG_MSG;
	public String COLLAPSE_ALL_BUT_CURRENT_TOOLTIP;
	public String CATEGORY_OTHER;
	public String RESTORE_ALL_TOOLTIP;
	public String CHEAT_SHEET_OTHER_MENU;
	public String PERFORM_TASK_TOOLTIP;
	public String SKIP_TASK_TOOLTIP;
	public String COMPLETE_TASK_TOOLTIP;
	public String RESTART_TASK_TOOLTIP;
	public String ERROR_FINDING_PLUGIN_FOR_ACTION;
	public String ERROR_DATA_MISSING;
	public String ERROR_DATA_MISSING_LOG;
	public String ERROR_CONDITIONAL_DATA_MISSING_LOG;
	public String ERROR_LOADING_CLASS_FOR_ACTION;
	public String ERROR_CREATING_CLASS_FOR_ACTION;
	public String START_CHEATSHEET_TOOLTIP;
	public String RESTART_CHEATSHEET_TOOLTIP;
	public String ADVANCE_TASK_TOOLTIP;
	public String RETURN_TO_INTRO_TOOLTIP;
	public String HELP_BUTTON_TOOLTIP;
	public String ERROR_RUNNING_ACTION;
	public String ERROR_INVALID_CHEATSHEET_ID;
	public String ERROR_CHEATSHEET_DOESNOT_EXIST;
	public String ERROR_APPLYING_STATE_DATA;
	public String CHEATSHEET_STATE_RESTORE_FAIL_TITLE;
	public String CHEATSHEET_STATE_RESET_CONFIRM;
	public String CHEATSHEET_FROM_URL_WITH_EXEC;
	public String CHEATSHEET_FROM_URL_WITH_EXEC_TITLE;
	public String ERROR_APPLYING_STATE_DATA_LOG;
	public String INITIAL_VIEW_DIRECTIONS;
	public String ERROR_LOADING_CHEATSHEET_CONTENT;
	public String ERROR_PAGE_MESSAGE;
	public String ERROR_LOADING_CLASS;
	public String ERROR_CREATING_CLASS;
	public String CHEAT_SHEET_OTHER_CATEGORY;
	public String LAUNCH_SHEET_ERROR;
	public String CHEAT_SHEET_ERROR_OPENING;
	public String ERROR_OPENING_PERSPECTIVE;
	public String ERROR_SAVING_STATEFILE_URL;
	public String CHEAT_SHEET_INTRO_TITLE;
	public String ERROR_TITLE;
	public String ERROR_CREATING_DOCUMENT_BUILDER;
	public String ERROR_DOCUMENT_BUILDER_NOT_INIT;
	public String ERROR_OPENING_FILE;
	public String ERROR_OPENING_FILE_IN_PARSER;
	public String ERROR_SAX_PARSING;
	public String ERROR_SAX_PARSING_WITH_LOCATION;
	public String ERROR_PARSING_CHEATSHEET_CONTENTS;
	public String ERROR_PARSING_CHEATSHEET_ELEMENT;
	public String ERROR_PARSING_NO_INTRO;
	public String ERROR_PARSING_MORE_THAN_ONE_INTRO;
	public String ERROR_PARSING_NO_ITEM;
	public String ERROR_PARSING_PARAM_INVALIDRANGE;
	public String ERROR_PARSING_PARAM_INVALIDNUMBER;
	public String ERROR_PARSING_NO_DESCRIPTION;
	public String ERROR_PARSING_MULTIPLE_DESCRIPTION;
	public String ERROR_PARSING_NO_SUBITEM;
	public String ERROR_PARSING_NO_ACTION;
	public String ERROR_PARSING_NO_TITLE;
	public String ERROR_PARSING_NO_CLASS;
	public String ERROR_PARSING_NO_PLUGINID;
	public String ERROR_PARSING_NO_CONDITION;
	public String ERROR_PARSING_NO_VALUES;
	public String ERROR_PARSING_NO_LABEL;
	public String ERROR_PARSING_NO_SERIALIZATION;
	public String ERROR_PARSING_INCOMPATIBLE_CHILDREN;
	public String ERROR_PARSING_DUPLICATE_CHILD;
	public String ERROR_PARSING_REQUIRED_CONFIRM;
	public String ERROR_COMMAND_ID_NOT_FOUND;
	public String ERROR_COMMAND_ERROR_STATUS;
	public String ERROR_COMMAND_SERVICE_UNAVAILABLE;
	public String WARNING_PARSING_UNKNOWN_ATTRIBUTE;
	public String WARNING_PARSING_UNKNOWN_ELEMENT;
	public String WARNING_PARSING_DESCRIPTION_UNKNOWN_ELEMENT;
	public String WARNING_PARSING_ON_COMPLETION_UNKNOWN_ELEMENT;
	public String EXCEPTION_RUNNING_ACTION;
	public String ACTION_FAILED;
	public String ERROR_MULTIPLE_ERRORS;
	public String ERROR_PARSING_ROOT_NODE_TYPE;
	public String COMPLETED_TASK;
	public String ERROR_PARSING_DUPLICATE_TASK_ID;
	public String ERROR_PARSING_NO_VALUE;
	public String ERROR_PARSING_NO_NAME;
	public String ERROR_PARSING_NO_ID;
	public String ERROR_PARSING_MULTIPLE_ROOT;
	public String ERROR_PARSING_NO_ROOT;
	public String ERROR_PARSING_INVALID_ID;
	public String ERROR_PARSING_CYCLE_DETECTED;
	public String ERROR_PARSING_CYCLE_CONTAINS;
	public String SELECTION_DIALOG_FILEPICKER_TITLE;
	public String SELECTION_DIALOG_FILEPICKER_BROWSE;
	public String SELECTION_DIALOG_OPEN_REGISTERED;
	public String SELECTION_DIALOG_OPEN_FROM_FILE;
	public String SELECTION_DIALOG_OPEN_FROM_URL;
	public String COMPOSITE_PAGE_REVIEW_TASK;
	public String COMPOSITE_PAGE_GOTO_TASK;
	public String COMPOSITE_PAGE_START_TASK;
	public String COMPOSITE_PAGE_SKIP_TASK;
	public String COMPOSITE_PAGE_SKIP_TASK_GROUP;
	public String COMPOSITE_MENU_SKIP;
	public String COMPOSITE_MENU_START;
	public String COMPOSITE_MENU_REVIEW;
	public String COMPOSITE_MENU_RESET;
	public String COMPOSITE_PAGE_BLOCKED;
	public String COMPOSITE_PAGE_TASK_NOT_COMPLETE;
	public String EXPLORER_PULLDOWN_MENU;
	public String COMPOSITE_RESTART_DIALOG_TITLE;
	public String COMPOSITE_RESTART_CONFIRM_MESSAGE;
	public String RESTART_ALL_MENU;
	public String RESTART_MENU;
	public String ERROR_EDITABLE_TASK_WITH_CHILDREN;
	public String ERROR_PARSING_TASK_NO_NAME;
	public String ERROR_PARSING_CCS_NO_NAME;
	public String ERROR_PARSING_TASK_NO_KIND;
	public String ERROR_PARSING_TASK_INVALID_KIND;
	public String ERROR_PARSING_CHILDLESS_TASK_GROUP;
	public String THIS_TASK_SKIPPED;
	public String PARENT_SKIPPED;
	public String PARENT_COMPLETED;
	public String PARENT_BLOCKED;
	public String COMPOSITE_RESET_TASK_DIALOG_TITLE;
	public String COMPOSITE_RESET_TASK_DIALOG_MESSAGE;
	public String COMPOSITE_PAGE_END_REVIEW;
	public String CHEATSHEET_TASK_NO_ID;

// RAP [if]: need session aware NLS
//	static {
//		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
//	}

	public String CheatSheetCategoryBasedSelectionDialog_showAll;

	/**
     * Load message values from bundle file
     * @return localized message
     */
    public static Messages get() {
      Class clazz = Messages.class;
      Object result = RWT.NLS.getISO8859_1Encoded( BUNDLE_NAME, clazz );
      return ( Messages )result;
    }
}
