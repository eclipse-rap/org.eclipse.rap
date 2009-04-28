/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM - Initial API and implementation
 * Sebastian Davids - bug 128529
 *******************************************************************************/
package org.eclipse.ui.internal;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.lifecycle.UICallBack;
import org.eclipse.swt.widgets.Display;

/**
 * Message class for workbench messages.  These messages are used 
 * throughout the workbench. 
 *
 */
// RAP [fappel]: need session aware NLS
//public class WorkbenchMessages extends NLS {
public class WorkbenchMessages {
	private static final String BUNDLE_NAME = "org.eclipse.ui.internal.messages";//$NON-NLS-1$


	public String BundleSigningTray_Cant_Find_Service;


	public String BundleSigningTray_Determine_Signer_For;


	public String BundleSigningTray_Signing_Certificate;


	public String BundleSigningTray_Signing_Date;


	public String BundleSigningTray_Unget_Signing_Service;


	public String BundleSigningTray_Unknown;


	public String BundleSigningTray_Unsigned;


	public String BundleSigningTray_Working;


	public String NewWorkingSet;


	public String PlatformUI_NoWorkbench;

	public String Workbench_CreatingWorkbenchTwice;

    public String StatusUtil_errorOccurred;
    
	// ==============================================================================
	// Workbench Actions
	// ==============================================================================

	// --- File Menu ---
	public String NewWizardAction_text;
	public String NewWizardAction_toolTip;
	public String CloseAllAction_text;
	public String CloseAllAction_toolTip;
	public String CloseAllSavedAction_text;
	public String CloseAllSavedAction_toolTip;
	public String CloseEditorAction_text;
	public String CloseEditorAction_toolTip;
	public String CloseOthersAction_text;
	public String CloseOthersAction_toolTip;
	public String NewEditorAction_text;
	public String NewEditorAction_tooltip;
	public String SaveAction_text;
	public String SaveAction_toolTip;
	public String SaveAs_text;
	public String SaveAs_toolTip;
	public String SaveAll_text;
	public String SaveAll_toolTip;
	public String Workbench_revert;
	public String Workbench_revertToolTip;
	public String Workbench_move;

	public String Workbench_moveToolTip;
	public String Workbench_rename;
	public String Workbench_renameToolTip;
	public String Workbench_refresh;
	public String Workbench_refreshToolTip;
	public String Workbench_properties;
	public String Workbench_propertiesToolTip;


	public String Workbench_print;
	public String Workbench_printToolTip;
	public String ExportResourcesAction_text;
	public String ExportResourcesAction_fileMenuText;
	public String ExportResourcesAction_toolTip;
	public String ImportResourcesAction_text;
	public String ImportResourcesAction_toolTip;
	public String OpenRecent_errorTitle;
	public String OpenRecent_unableToOpen;
	public String Exit_text;
	public String Exit_toolTip;


	// --- Edit Menu ---
	public String Workbench_undo;
	public String Workbench_undoToolTip;
	public String Workbench_redo;
	public String Workbench_redoToolTip;
	public String Workbench_cut;
	public String Workbench_cutToolTip;
	public String Workbench_copy;
	public String Workbench_copyToolTip;
	public String Workbench_paste;
	public String Workbench_pasteToolTip;
	public String Workbench_delete;
	public String Workbench_deleteToolTip;
	public String Workbench_selectAll;
	public String Workbench_selectAllToolTip;
	public String Workbench_findReplace;
	public String Workbench_findReplaceToolTip;

	// --- Navigate Menu ---
	public String Workbench_goInto;
	public String Workbench_goIntoToolTip;
	public String Workbench_back;
	public String Workbench_backToolTip;
	public String Workbench_forward;
	public String Workbench_forwardToolTip;
	public String Workbench_up;
	public String Workbench_upToolTip;
	public String Workbench_next;
	public String Workbench_nextToolTip;
	public String Workbench_previous;
	public String Workbench_previousToolTip;

	public String NavigationHistoryAction_forward_text;
	public String NavigationHistoryAction_forward_toolTip;
	public String NavigationHistoryAction_backward_text;
	public String NavigationHistoryAction_backward_toolTip;
	public String NavigationHistoryAction_forward_toolTipName;
	public String NavigationHistoryAction_backward_toolTipName;
	public String NavigationHistoryAction_locations;

	public String Workbench_showInNoTargets;
	public String Workbench_showInNoPerspectives;
	public String Workbench_noApplicableItems;

	public String OpenPreferences_text;
	public String OpenPreferences_toolTip;

	// --- Window Menu ---
	public String PerspectiveMenu_otherItem;
	public String SelectPerspective_shellTitle;
	public String Workbench_showPerspectiveError;
	public String ChangeToPerspectiveMenu_errorTitle;
    public String OpenPerspectiveDialogAction_text;
    public String OpenPerspectiveDialogAction_tooltip;

    public String ShowView_title;
	public String ShowView_shellTitle;
	public String ShowView_errorTitle;
	public String ShowView_selectViewHelp;
	public String ShowView_noDesc;
	
	public String ToggleEditor_hideEditors;
	public String ToggleEditor_showEditors;
	public String ToggleEditor_toolTip;

	public String LockToolBarAction_text;
	public String LockToolBarAction_toolTip;

	public String EditActionSetsAction_text;
	public String EditActionSetsAction_toolTip;
	public String ActionSetSelection_customize;
	public String ActionSetDialogInput_viewCategory;
	public String ActionSetDialogInput_perspectiveCategory;
	public String ActionSetDialogInput_wizardCategory;

	public String ActionSetSelection_menuTab;
	public String ActionSetSelection_actionSetsTab;
	public String ActionSetSelection_selectActionSetsLabel;
	public String ActionSetSelection_selectActionSetsHelp;
	public String ActionSetSelection_availableActionSets;
	public String ActionSetSelection_menubarActions;
	public String ActionSetSelection_toolbarActions;
	public String ActionSetSelection_selectMenusLabel;
	public String ActionSetSelection_availableMenus;
	public String ActionSetSelection_availableCategories;
	public String ActionSetSelection_menuItems;
	public String ActionSetSelection_descriptionColumnHeader;
	public String ActionSetSelection_menuColumnHeader;
	public String ActionSetSelection_toolbarLocation;
	public String ActionSetSelection_menubarLocation;
	public String ActionSetSelection_noDesc;

	public String SavePerspective_text;
	public String SavePerspective_toolTip;
	public String SavePerspective_shellTitle;
	public String SavePerspectiveDialog_description;
	public String SavePerspective_name;
	public String SavePerspective_existing;
	public String SavePerspective_overwriteTitle;
	public String SavePerspective_overwriteQuestion;
	public String SavePerspective_singletonQuestion;
	public String SavePerspective_errorTitle;
	public String SavePerspective_errorMessage;

	public String ResetPerspective_text;
	public String ResetPerspective_toolTip;
	public String ResetPerspective_message;
	public String ResetPerspective_title;
	public String RevertPerspective_note;

	public String ClosePerspectiveAction_text;
	public String ClosePerspectiveAction_toolTip;
	public String CloseAllPerspectivesAction_text;
	public String CloseAllPerspectivesAction_toolTip;

	public String OpenInNewWindowAction_text;
	public String OpenInNewWindowAction_toolTip;
	public String OpenInNewWindowAction_errorTitle;
	public String CycleEditorAction_next_text;
	public String CycleEditorAction_next_toolTip;
	public String CycleEditorAction_prev_text;
	public String CycleEditorAction_prev_toolTip;
	public String CycleEditorAction_header;
	public String CyclePartAction_next_text;
	public String CyclePartAction_next_toolTip;
	public String CyclePartAction_prev_text;
	public String CyclePartAction_prev_toolTip;
	public String CyclePartAction_header;
	public String CyclePartAction_editor;
	public String CyclePerspectiveAction_next_text;
	public String CyclePerspectiveAction_next_toolTip;
	public String CyclePerspectiveAction_prev_text;
	public String CyclePerspectiveAction_prev_toolTip;
	public String CyclePerspectiveAction_header;
	public String ActivateEditorAction_text;
	public String ActivateEditorAction_toolTip;
	public String MaximizePartAction_text;
	public String MaximizePartAction_toolTip;
	public String MinimizePartAction_text;
	public String MinimizePartAction_toolTip;



	// --- Help Menu ---
	public String AboutAction_text;
	public String AboutAction_toolTip;
	public String HelpContentsAction_text;
	public String HelpContentsAction_toolTip;
	public String HelpSearchAction_text;
	public String HelpSearchAction_toolTip;
	public String DynamicHelpAction_text;
	public String DynamicHelpAction_toolTip;
	public String AboutDialog_shellTitle;
	public String AboutDialog_featureInfo;
	public String AboutDialog_pluginInfo;
	public String AboutDialog_systemInfo;
	public String AboutDialog_defaultProductName;
	public String AboutDialog_concatenationOfProviderNameAndFeatureName;
	public String ProductInfoDialog_errorTitle;
	public String ProductInfoDialog_unableToOpenWebBrowser;
	public String PreferencesExportDialog_ErrorDialogTitle;
	public String AboutPluginsDialog_shellTitle;
	public String AboutPluginsDialog_pluginName;
	public String AboutPluginsDialog_pluginId;
	public String AboutPluginsDialog_version;
	public String AboutPluginsDialog_signed;
	public String AboutPluginsDialog_provider;
	public String AboutPluginsDialog_state_installed;
	public String AboutPluginsDialog_state_resolved;
	public String AboutPluginsDialog_state_starting;
	public String AboutPluginsDialog_state_stopping;
	public String AboutPluginsDialog_state_uninstalled;
	public String AboutPluginsDialog_state_active;
	public String AboutPluginsDialog_state_unknown;
	public String AboutPluginsDialog_moreInfo;
	public String AboutPluginsDialog_signingInfo_show;
	public String AboutPluginsDialog_signingInfo_hide;
	public String AboutPluginsDialog_errorTitle;
	public String AboutPluginsDialog_unableToOpenFile;
	public String AboutFeaturesDialog_shellTitle;
	public String AboutFeaturesDialog_featureName;
	public String AboutFeaturesDialog_featureId;
	public String AboutFeaturesDialog_version;
	public String AboutFeaturesDialog_signed;
	public String AboutFeaturesDialog_provider;
	public String AboutFeaturesDialog_moreInfo;
	public String AboutFeaturesDialog_pluginsInfo;
	public String AboutFeaturesDialog_noInformation;
	public String AboutFeaturesDialog_pluginInfoTitle;
	public String AboutFeaturesDialog_pluginInfoMessage;
	public String AboutFeaturesDialog_noInfoTitle;
	public String AboutSystemDialog_browseErrorLogName;
	public String AboutSystemDialog_copyToClipboardName;
	public String AboutSystemDialog_noLogTitle;
	public String AboutSystemDialog_noLogMessage;

	// --- Shortcutbar ---
	public String PerspectiveBarContributionItem_toolTip;
	public String PerspectiveBarNewContributionItem_toolTip;

	//--- Coolbar ---
	public String WorkbenchWindow_FileToolbar;
	public String WorkbenchWindow_NavigateToolbar;
	public String WorkbenchWindow_searchCombo_toolTip;
	public String WorkbenchWindow_searchCombo_text;


	public String WorkbenchWindow_close;
	public String WorkbenchPage_PerspectiveFormat;
	public String WorkbenchPage_ErrorCreatingPerspective;
	public String WorkbenchPage_UndefinedPerspective;

	public String SelectWorkingSetAction_text;
	public String SelectWorkingSetAction_toolTip;
	public String EditWorkingSetAction_text;
	public String EditWorkingSetAction_toolTip;
	public String EditWorkingSetAction_error_nowizard_title;
	public String EditWorkingSetAction_error_nowizard_message;
	public String ClearWorkingSetAction_text;
	public String ClearWorkingSetAction_toolTip;
	public String WindowWorkingSets;
	public String NoWorkingSet;
	public String SelectedWorkingSets;
	public String NoApplicableWorkingSets;

	// ==============================================================================
	// Drill Actions
	// ==============================================================================
	public String GoHome_text;
	public String GoHome_toolTip;
	public String GoBack_text;
	public String GoBack_toolTip;
	public String GoInto_text;
	public String GoInto_toolTip;


	public String ICategory_other;

	// ==============================================================================
	// Wizards
	// ==============================================================================
	public String NewWizard_title;
	public String NewWizardNewPage_description;
	public String NewWizardNewPage_wizardsLabel;
	public String NewWizardNewPage_showAll;
	public String WizardList_description;
	public String Select;
	public String NewWizardSelectionPage_description;
	public String NewWizardShortcutAction_errorTitle;
	public String NewWizardShortcutAction_errorMessage;

	public String NewWizardsRegistryReader_otherCategory;
	public String NewWizardDropDown_text;
	
	public String WorkbenchWizard_errorMessage;
	public String WorkbenchWizard_errorTitle;
	public String WizardTransferPage_selectAll;
	public String WizardTransferPage_deselectAll;
	public String TypesFiltering_title;
	public String TypesFiltering_message;
	public String TypesFiltering_otherExtensions;
	public String TypesFiltering_typeDelimiter;

	// --- Import/Export ---
	public String ImportExportPage_chooseImportSource;
	public String ImportExportPage_chooseExportDestination;
	
	// --- Import ---
	public String ImportWizard_title;
	public String ImportWizard_selectSource;

	// --- Export ---
	public String ExportWizard_title;
	public String ExportWizard_selectDestination;
	// --- New Project ---
	public String NewProject_title;

	// ==============================================================================
	// Preference Pages
	// ==============================================================================
	public String PreferenceNode_errorTitle;
	public String PreferenceNode_errorMessage;
    public String PreferenceNode_NotFound;
	public String Preference_note;

	// --- Workbench ---
	public String WorkbenchPreference_showMultipleEditorTabsButton;
	public String WorkbenchPreference_allowInplaceEditingButton;
	public String WorkbenchPreference_useIPersistableEditorButton;
	public String WorkbenchPreference_promptWhenStillOpenButton;
	public String WorkbenchPreference_showTextOnPerspectiveBar;
	public String WorkbenchPreference_stickyCycleButton;
	public String WorkbenchPreference_RunInBackgroundButton;
	public String WorkbenchPreference_RunInBackgroundToolTip;

	// --- Appearance ---
	public String ViewsPreference_currentPresentation;
	public String ViewsPreference_currentPresentationFormat;
	public String ViewsPreference_presentationConfirm_title;
	public String ViewsPreference_presentationConfirm_message;
	public String ViewsPreference_presentationConfirm_yes;
	public String ViewsPreference_presentationConfirm_no;
	public String ViewsPreference_editors;
	public String ViewsPreference_editors_top;
	public String ViewsPreference_editors_bottom;
	public String ViewsPreference_views;
	public String ViewsPreference_views_top;
	public String ViewsPreference_views_bottom;
	public String ViewsPreference_perspectiveBar;
	public String ViewsPreference_perspectiveBar_topRight;
	public String ViewsPreference_perspectiveBar_topLeft;
	public String ViewsPreference_perspectiveBar_left;
	public String ViewsPreference_traditionalTabs;
	public String ViewsPreference_currentTheme;
	public String ViewsPreference_currentThemeDescription;
	public String ViewsPreference_currentThemeFormat;
	public String ViewsPreference_enableAnimations;
	public String ViewsPreference_useColoredLabels;
	public String ViewsPreference_override;
	public String ViewsPreference_restartRequestJobName;
	
	// --- File Editors ---
	public String FileEditorPreference_fileTypes;
	public String FileEditorPreference_add;
	public String FileEditorPreference_remove;
	public String FileEditorPreference_associatedEditors;
	public String FileEditorPreference_addEditor;
	public String FileEditorPreference_removeEditor;
	public String FileEditorPreference_default;
	public String FileEditorPreference_existsTitle;
	public String FileEditorPreference_existsMessage;
	public String FileEditorPreference_defaultLabel;
    public String FileEditorPreference_contentTypesRelatedLink;
    public String FileEditorPreference_isLocked;

	public String FileExtension_extensionEmptyMessage;
	public String FileExtension_fileNameInvalidMessage;
	public String FilteredPreferenceDialog_PreferenceSaveFailed;
	
	public String FileExtension_fileTypeMessage;
	public String FileExtension_fileTypeLabel;
	public String FileExtension_shellTitle;
	public String FileExtension_dialogTitle;

	public String Choose_the_editor_for_file;
	public String EditorSelection_chooseAnEditor;
	public String EditorSelection_internal;
	public String EditorSelection_external;
	public String EditorSelection_browse;
	public String EditorSelection_title;

	// --- Perspectives ---
	public String OpenPerspectiveMode_optionsTitle;
	public String OpenPerspectiveMode_sameWindow;
	public String OpenPerspectiveMode_newWindow;

	public String OpenViewMode_title;
	public String OpenViewMode_embed;
	public String OpenViewMode_fast;

	public String PerspectivesPreference_MakeDefault;
	public String PerspectivesPreference_MakeDefaultTip;
	public String PerspectivesPreference_Reset;
	public String PerspectivesPreference_ResetTip;
	public String PerspectivesPreference_Delete;
	public String PerspectivesPreference_DeleteTip;
	public String PerspectivesPreference_available;
	public String PerspectivesPreference_defaultLabel;
	public String PerspectivesPreference_perspectiveopen_title;
	public String PerspectivesPreference_perspectiveopen_message;

	public String PerspectiveLabelProvider_unknown;

	//---- General Preferences----
	public String PreferencePage_noDescription;
	public String PreferencePageParameterValues_pageLabelSeparator;

	// --- Workbench -----
	public String WorkbenchPreference_openMode;
	public String WorkbenchPreference_doubleClick;
	public String WorkbenchPreference_singleClick;
	public String WorkbenchPreference_singleClick_SelectOnHover;
	public String WorkbenchPreference_singleClick_OpenAfterDelay;
	public String WorkbenchPreference_noEffectOnAllViews;
	public String WorkbenchPreference_HeapStatusButton;
	public String WorkbenchPreference_HeapStatusButtonToolTip;

	// --- Fonts ---
	public String FontsPreference_useSystemFont;

	// --- Decorators ---
	public String DecoratorsPreferencePage_description;
	public String DecoratorsPreferencePage_decoratorsLabel;
	public String DecoratorsPreferencePage_explanation;

	// --- Startup preferences ---
	public String StartupPreferencePage_label;

	// ==============================================================================
	// Property Pages
	// ==============================================================================
	public String PropertyDialog_text;
	public String PropertyDialog_toolTip;
	public String PropertyDialog_messageTitle;
	public String PropertyDialog_noPropertyMessage;
	public String PropertyDialog_propertyMessage;
	public String PropertyPageNode_errorTitle;
	public String PropertyPageNode_errorMessage;

	public String SystemInPlaceDescription_name;
	public String SystemEditorDescription_name;

	// ==============================================================================
	// Dialogs
	// ==============================================================================
	public String Error;
	public String Information;

	public String ErrorPreferencePage_errorMessage;

	public String ListSelection_title;
	public String ListSelection_message;

	public String SelectionDialog_selectLabel;
	public String SelectionDialog_deselectLabel;

	public String ElementTreeSelectionDialog_nothing_available;

	public String CheckedTreeSelectionDialog_nothing_available;
	public String CheckedTreeSelectionDialog_select_all;
	public String CheckedTreeSelectionDialog_deselect_all;

	// ==============================================================================
	// Editor Framework
	// ==============================================================================
	public String EditorManager_saveResourcesMessage;
	public String EditorManager_saveResourcesOptionallyMessage;
	public String EditorManager_saveResourcesTitle;
	public String EditorManager_exceptionRestoringEditor;
	public String EditorManager_unableToCreateEditor;
	public String EditorManager_systemEditorError;
	public String EditorManager_invalidDescriptor;
	public String EditorManager_instantiationError;
    public String EditorManager_errorInInit;
	public String EditorManager_siteIncorrect;
	public String EditorManager_unknownEditorIDMessage;
	public String EditorManager_errorOpeningExternalEditor;
	public String EditorManager_unableToOpenExternalEditor;
	public String EditorManager_operationFailed;
	public String EditorManager_saveChangesQuestion;
	public String EditorManager_closeWithoutPromptingOption;
	public String EditorManager_saveChangesOptionallyQuestion;
    public String EditorManager_missing_editor_descriptor;
    public String EditorManager_no_in_place_support;
    public String EditorManager_invalid_editor_descriptor;
    public String EditorManager_no_persisted_state;
    public String EditorManager_no_input_factory_ID;
    public String EditorManager_bad_element_factory;
    public String EditorManager_create_element_returned_null;
    public String EditorManager_wrong_createElement_result;
    public String EditorManager_backgroundSaveJobName;
    
	public String EditorPane_pinEditor;

	public String ExternalEditor_errorMessage;
	public String Save;
	public String Save_Resource;
	public String Save_All;


	// ==============================================================================
	// Perspective Framework
	// ==============================================================================
	public String OpenNewPageMenu_dialogTitle;
	public String OpenNewPageMenu_unknownPageInput;

	public String OpenNewWindowMenu_dialogTitle;
	public String OpenNewWindowMenu_unknownInput;

	public String OpenPerspectiveMenu_pageProblemsTitle;
	public String OpenPerspectiveMenu_errorUnknownInput;

	public String Perspective_oneError;
	public String Perspective_multipleErrors;

	public String Perspective_problemRestoringTitle;
	public String Perspective_errorReadingState;
	public String Perspective_problemLoadingTitle;
	public String Perspective_errorLoadingState;
	public String WorkbenchPage_problemRestoringTitle;
	public String WorkbenchPage_errorReadingState;

	public String Perspective_problemSavingTitle;
	public String Perspective_problemSavingMessage;

	public String Perspective_unableToLoad;
	public String Perspective_couldNotFind;

	// ==============================================================================
	// Views Framework
	// ==============================================================================
	public String Menu;

	public String StandardSystemToolbar_Minimize;
	public String StandardSystemToolbar_Maximize;
	public String StandardSystemToolbar_Restore;

	public String EditorArea_Tooltip;
	public String ViewPane_fastView;
	public String ViewPane_minimizeView;
	public String ViewPane_moveView;
	public String ViewPane_moveFolder;

	public String EditorPane_moveEditor;

	public String ViewLabel_unknown;

	public String ViewFactory_initException;
	public String ViewFactory_siteException;
	public String ViewFactory_couldNotCreate;
	public String ViewFactory_noMultiple;
	public String ViewFactory_couldNotSave;
	// ==============================================================================
	// Workbench
	// ==============================================================================
	public String Startup_Loading;
	public String Startup_Loading_Workbench;
	public String Startup_Done;

	public String WorkbenchPage_UnknownLabel;

	public String WorkbenchPage_editorAlreadyOpenedMsg;

	// These four keys are marked as unused by the NLS search, but they are indirectly used
	// and should be removed.
	public String PartPane_sizeLeft;
	public String PartPane_sizeRight;
	public String PartPane_sizeTop;
	public String PartPane_sizeBottom;

	public String PartPane_detach;
	public String PartPane_restore;
	public String PartPane_move;
	public String PartPane_size;
	public String PartPane_maximize;
	public String PartPane_close;
	public String PartPane_closeOthers;
	public String PartPane_closeAll;
	public String PartPane_newEditor;
	public String PluginAction_operationNotAvailableMessage;
	public String PluginAction_disabledMessage;
	public String ActionDescriptor_invalidLabel;

	public String XMLMemento_parserConfigError;
	public String XMLMemento_ioError;
	public String XMLMemento_formatError;
	public String XMLMemento_noElement;

	// --- Workbench Errors/Problems ---
	public String WorkbenchWindow_exceptionMessage;
	public String WorkbenchPage_AbnormalWorkbenchCondition;
	public String WorkbenchPage_IllegalSecondaryId;
	public String WorkbenchPage_IllegalViewMode;
	public String WorkbenchPart_AutoTitleFormat;
	public String EditorPart_AutoTitleFormat;
	public String Abnormal_Workbench_Conditi;
	public String WorkbenchPage_ErrorActivatingView;
	public String DecoratorManager_ErrorActivatingDecorator;

	public String EditorRegistry_errorTitle;
	public String EditorRegistry_errorMessage;

	public String ErrorClosing;
	public String ErrorClosingNoArg;
	public String ErrorClosingOneArg;
	public String ErrorReadingState;

	public String Invalid_workbench_state_ve;
	public String Workbench_incompatibleUIState;
	public String Workbench_incompatibleSavedStateVersion;
	public String ProblemSavingState;
	public String SavingProblem;

	public String Problems_Opening_Page;
	public String Restoring_Problems;

	public String Workspace_problemsTitle;

	public String Workbench_problemsRestoringMsg;
	public String Workbench_problemsSavingMsg;
	public String Workbench_problemsRestoring;
	public String Workbench_problemsSaving;
	public String WorkbenchWindow_problemsRestoringWindow;
	public String WorkbenchWindow_problemsSavingWindow;
	public String EditorManager_problemsRestoringEditors;
	public String EditorManager_problemsSavingEditors;
	public String RootLayoutContainer_problemsRestoringPerspective;
	public String RootLayoutContainer_problemsSavingPerspective;
	public String ViewFactory_problemsSavingViews;

	public String EditorManager_unableToSaveEditor;
	public String Perspective_problemsRestoringPerspective;
	public String Perspective_problemsSavingPerspective;
	public String Perspective_problemsRestoringViews;
	public String WorkbenchWindow_unableToRestorePerspective;
	public String WorkbenchPage_unableToRestorePerspective;
	public String WorkbenchPage_unableToSavePerspective;
	public String Perspective_unableToRestorePerspective;
	public String PageLayout_missingRefPart;
	public String PageLayout_duplicateRefPart;
	public String PartStack_incorrectPartInFolder;


	// ==============================================================================
	// Keys used in the reuse editor which is released as experimental.
	// ==============================================================================
	public String EditorManager_openNewEditorLabel;
	public String EditorManager_reuseEditorDialogTitle;
	public String PinEditorAction_text;
	public String PinEditorAction_toolTip;
	public String WorkbenchPreference_reuseEditors;
	public String WorkbenchPreference_reuseDirtyEditorGroupTitle;
	public String WorkbenchPreference_promptToReuseEditor;
	public String WorkbenchPreference_openNewEditor;
	public String WorkbenchPreference_reuseEditorsThreshold;
	public String WorkbenchPreference_reuseEditorsThresholdError;
	public String WorkbenchPreference_recentFiles;
	public String WorkbenchPreference_recentFilesError;
	public String WorkbenchEditorsAction_label;
	public String WorkbookEditorsAction_label;

	public String WorkbenchEditorsDialog_title;
	public String WorkbenchEditorsDialog_label;
	public String WorkbenchEditorsDialog_closeSelected;
	public String WorkbenchEditorsDialog_saveSelected;
	public String WorkbenchEditorsDialog_selectClean;
	public String WorkbenchEditorsDialog_invertSelection;
	public String WorkbenchEditorsDialog_allSelection;
	public String WorkbenchEditorsDialog_showAllPersp;
	public String WorkbenchEditorsDialog_name;
	public String WorkbenchEditorsDialog_path;
	public String WorkbenchEditorsDialog_activate;
	public String WorkbenchEditorsDialog_close;

	public String ShowPartPaneMenuAction_text;
	public String ShowPartPaneMenuAction_toolTip;
	public String ShowViewMenuAction_text;
	public String ShowViewMenuAction_toolTip;
	public String QuickAccessAction_text;
	public String QuickAccessAction_toolTip;

	public String ToggleCoolbarVisibilityAction_show_text;
	public String ToggleCoolbarVisibilityAction_hide_text;
	public String ToggleCoolbarVisibilityAction_toolTip;
	

	// ==============================================================================
	// Working Set Framework.
	// ==============================================================================
	public String ProblemSavingWorkingSetState_message;
	public String ProblemSavingWorkingSetState_title;
	public String ProblemRestoringWorkingSetState_message;

	public String ProblemRestoringWorkingSetState_title;

	public String WorkingSetEditWizard_title;
	public String WorkingSetNewWizard_title;

	public String WorkingSetTypePage_description;
	public String WorkingSetTypePage_typesLabel;

	public String WorkingSetSelectionDialog_title;
	public String WorkingSetSelectionDialog_title_multiSelect;
	public String WorkingSetSelectionDialog_message;
	public String WorkingSetSelectionDialog_message_multiSelect;
	public String WorkingSetSelectionDialog_detailsButton_label;
	public String WorkingSetSelectionDialog_newButton_label;
	public String WorkingSetSelectionDialog_removeButton_label;
	
	public String WorkbenchPage_workingSet_default_label;
	public String WorkbenchPage_workingSet_multi_label;

	// =================================================================
	// System Summary
	// =================================================================
	public String SystemSummary_title;
	public String SystemSummary_timeStamp;
	public String SystemSummary_systemProperties;
	public String SystemSummary_features;
	public String SystemSummary_pluginRegistry;
	public String SystemSummary_userPreferences;
	public String SystemSummary_sectionTitle;
	public String SystemSummary_sectionError;

	// paramter 0 is the feature name, parameter 1 is the version and parameter 2 is the Id
	public String SystemSummary_featureVersion;
	public String SystemMenuMovePane_PaneName;

	public String SystemSummary_descriptorIdVersionState;

	// =================================================================
	// Editor List
	// =================================================================
	public String EditorList_saveSelected_text;
	public String EditorList_saveSelected_toolTip;
	public String EditorList_closeSelected_text;
	public String EditorList_closeSelected_toolTip;

	public String EditorList_selectClean_text;
	public String EditorList_selectClean_toolTip;
	public String EditorList_invertSelection_text;
	public String EditorList_invertSelection_toolTip;
	public String EditorList_selectAll_text;
	public String EditorList_selectAll_toolTip;

	public String EditorList_FullName_text;
	public String EditorList_FullName_toolTip;

	public String EditorList_SortBy_text;
	public String EditorList_SortByName_text;
	public String EditorList_SortByName_toolTip;
	public String EditorList_SortByMostRecentlyUsed_text;
	public String EditorList_SortByMostRecentlyUsed_toolTip;

	public String EditorList_ApplyTo_text;
	public String EditorList_DisplayAllWindows_text;
	public String EditorList_DisplayAllWindows_toolTip;
	public String EditorList_DisplayAllPage_text;
	public String EditorList_DisplayAllPage_toolTip;
	public String EditorList_DisplayTabGroup_text;
	public String EditorList_DisplayTabGroup_toolTip;
	public String DecorationScheduler_UpdateJobName;
	public String DecorationScheduler_CalculationJobName;
	public String DecorationScheduler_UpdatingTask;
	public String DecorationScheduler_CalculatingTask;
	public String DecorationScheduler_ClearResultsJob;
	public String DecorationScheduler_DecoratingSubtask;

	public String PerspectiveBar_showText;
	public String PerspectiveBar_customize;
	public String PerspectiveBar_saveAs;
	public String PerspectiveBar_reset;

	public String PerspectiveSwitcher_dockOn;
	public String PerspectiveSwitcher_topRight;
	public String PerspectiveSwitcher_topLeft;
	public String PerspectiveSwitcher_left;


	public String FastViewBar_view_orientation;
	public String FastViewBar_horizontal;
	public String FastViewBar_vertical;
	public String FastViewBar_0;

	public String WorkbenchPlugin_extension;

	public String EventLoopProgressMonitor_OpenDialogJobName;
	public String DecorationReference_EmptyReference;
	public String RectangleAnimation_Animating_Rectangle;
	public String FilteredList_UpdateJobName;
	public String FilteredTree_ClearToolTip;
	public String FilteredTree_FilterMessage;
	public String FilteredTree_FilteredDialogTitle;
	public String FilteredTree_AccessibleListenerFiltered;
	public String Workbench_restoreDisabled;
	public String Workbench_noStateToRestore;
	public String Workbench_noWindowsRestored;
	public String Workbench_startingPlugins;
	public String ScopedPreferenceStore_DefaultAddedError;

	public String WorkbenchEncoding_invalidCharset;

	public String Dynamic_resetPerspectiveMessage;
	public String Dynamic_resetPerspectiveTitle;

	//==============================================================
	// Undo/Redo Support
	
	public String Operations_undoCommand;
	public String Operations_redoCommand;
	public String Operations_undoTooltipCommand;
	public String Operations_redoTooltipCommand;
	public String Operations_undoRedoCommandDisabled;
	public String Operations_undoProblem;
	public String Operations_redoProblem;
	public String Operations_executeProblem;
	public String Operations_undoInfo;
	public String Operations_redoInfo;
	public String Operations_executeInfo;
	public String Operations_undoWarning;
	public String Operations_redoWarning;
	public String Operations_executeWarning;
	public String Operations_linearUndoViolation;
	public String Operations_linearRedoViolation;
	public String Operations_nonLocalUndoWarning;
	public String Operations_nonLocalRedoWarning;
	public String Operations_discardUndo;
	public String Operations_discardRedo;
	public String Operations_proceedWithNonOKExecuteStatus;
	public String Operations_proceedWithNonOKUndoStatus;
	public String Operations_proceedWithNonOKRedoStatus;
	public String Operations_stoppedOnExecuteErrorStatus;
	public String Operations_stoppedOnUndoErrorStatus;
	public String Operations_stoppedOnRedoErrorStatus;

	//==============================================================
	// Heap Status

	public String HeapStatus_status;
	public String HeapStatus_widthStr;
	public String HeapStatus_memoryToolTip;
	public String HeapStatus_meg;
	public String HeapStatus_maxUnknown;
	public String HeapStatus_noMark;
	public String HeapStatus_buttonToolTip;
	public String SetMarkAction_text;
	public String ClearMarkAction_text;
	public String ShowMaxAction_text;
//	public String ShowKyrsoftViewAction_text;
//	public String ShowKyrsoftViewAction_KyrsoftNotInstalled;
//	public String ShowKyrsoftViewAction_OpenPerspectiveFirst;
//	public String ShowKyrsoftViewAction_ErrorShowingKyrsoftView;


    // ==============================================================================
    // Content Types preference page
    // ==============================================================================
    
    public String ContentTypes_lockedFormat;
    public String ContentTypes_characterSetLabel;
    public String ContentTypes_characterSetUpdateLabel;
    public String ContentTypes_fileAssociationsLabel;
    public String ContentTypes_fileAssociationsAddLabel;
    public String ContentTypes_fileAssociationsEditLabel;
    public String ContentTypes_fileAssociationsRemoveLabel;
    public String ContentTypes_contentTypesLabel;
    public String ContentTypes_errorDialogMessage;
    public String ContentTypes_FileEditorsRelatedLink;
    public String ContentTypes_addDialog_title;
    public String ContentTypes_addDialog_messageHeader;
    public String ContentTypes_addDialog_message;
    public String ContentTypes_addDialog_label;
    public String ContentTypes_editDialog_title;
    public String ContentTypes_editDialog_messageHeader;
    public String ContentTypes_editDialog_message;
    public String ContentTypes_editDialog_label;
    public String Edit;

    // =========================================================================
    // Deprecated actions support
    // =========================================================================
    public String CommandService_AutogeneratedCategoryName;
    public String CommandService_AutogeneratedCategoryDescription;
    public String LegacyActionPersistence_AutogeneratedCommandName;
	
    // ==============================================================================
    // Trim Common UI
    // ==============================================================================
    
    // Trim Menu item labels
    public String TrimCommon_DockOn;
    public String TrimCommon_Left;
    public String TrimCommon_Right;
    public String TrimCommon_Bottom;
    public String TrimCommon_Top;
    public String TrimCommon_Close;

    // Trim area Display Names
    public String TrimCommon_Main_TrimName;
    public String TrimCommon_PerspectiveSwitcher_TrimName;
    public String TrimCommon_FastView_TrimName;
    public String TrimCommon_HeapStatus_TrimName;
    public String TrimCommon_IntroBar_TrimName;
    public String TrimCommon_Progress_TrimName;
    public String TrimCommon_StatusLine_TrimName;
		
    // FilteredItemsSelectionDialog
    public String FilteredItemsSelectionDialog_cacheSearchJob_taskName;
    public String FilteredItemsSelectionDialog_menu;
	public String FilteredItemsSelectionDialog_refreshJob;
	public String FilteredItemsSelectionDialog_progressRefreshJob;
	public String FilteredItemsSelectionDialog_cacheRefreshJob;
	public String FilteredItemsSelectionDialog_cacheRefreshJob_checkDuplicates;
	public String FilteredItemsSelectionDialog_cacheRefreshJob_getFilteredElements;
	public String FilteredItemsSelectionDialog_patternLabel;
	public String FilteredItemsSelectionDialog_listLabel;
	public String FilteredItemsSelectionDialog_toggleStatusAction;
	public String FilteredItemsSelectionDialog_removeItemsFromHistoryAction;
	public String FilteredItemsSelectionDialog_searchJob_taskName;
	public String FilteredItemsSelectionDialog_separatorLabel;
    public String FilteredItemsSelectionDialog_storeError;
    public String FilteredItemsSelectionDialog_restoreError;
    public String FilteredItemsSelectionDialog_nItemsSelected;
	
    // AbstractSearcher
    public String FilteredItemsSelectionDialog_jobLabel; 
    public String FilteredItemsSelectionDialog_jobError;
    public String FilteredItemsSelectionDialog_jobCancel;
    
    // GranualProgressMonitor
    public String FilteredItemsSelectionDialog_taskProgressMessage;
    public String FilteredItemsSelectionDialog_subtaskProgressMessage;
    	
// RAP [fappel]: need session aware NLS
//    static {
//		// load message values from bundle file
//		NLS.initializeMessages(BUNDLE_NAME, WorkbenchMessages.class);
//	}


    public String FastViewBar_show_view;
    
    // Content assist support
    public String ContentAssist_Cue_Description_Key;
	
	//Settings transfer
	public String WorkbenchLayoutSettings_Name;
	public String WorkbenchSettings_CouldNotCreateDirectories;
	public String WorkbenchSettings_CouldNotFindLocation;
	public String WorkingSets_Name;
	public String WorkingSets_CannotSave;

	// StatusDialog
	public String WorkbenchStatusDialog_ActionLabel;
	public String WorkbenchStatusDialog_CopyThisReport;
	public String WorkbenchStatusDialog_ExplanationLabel;
	public String WorkbenchStatusDialog_NotAvailable;
	public String WorkbenchStatusDialog_Support;
	public String WorkbenchStatusDialog_StatusLabel;
	public String WorkbenchStatusDialog_TimestampNotAvailable;
	public String WorkbenchStatusDialog_StatusWithChildren;
	public String WorkbenchStatusDialog_NoMessageAvailable;
	public String WorkbenchStatusDialog_SeeDetails;
	public String WorkbenchStatusDialog_MultipleProblemsHaveOccured;
	public String WorkbenchStatusDialog_ProblemOccurred;
	public String WorkbenchStatusDialog_ProblemOccurredInJob;
	
	public String StackTraceSupportArea_NoStackTrace;
	public String StackTraceSupportArea_CausedBy;
	public String StackTraceSupportArea_Title;
	
	// WorkingSetConfigurationBlock
	public String WorkingSetConfigurationBlock_SelectWorkingSet_button;
	public String WorkingSetConfigurationBlock_WorkingSetText_name;
	
	public String WorkingSetPropertyPage_ReadOnlyWorkingSet_description;
	public String WorkingSetPropertyPage_ReadOnlyWorkingSet_title;
	
	public String WorkingSetGroup_WorkingSets_group;
	public String WorkingSetGroup_WorkingSetSelection_message;
	public String WorkingSetGroup_EnableWorkingSet_button;
	
	/**
	 * Load message values from bundle file
	 * @return localized message
	 */
    public static WorkbenchMessages get() {
      Class clazz = WorkbenchMessages.class;
      Object result = RWT.NLS.getISO8859_1Encoded( BUNDLE_NAME, clazz );
      return ( WorkbenchMessages )result;
    }
    
    public static WorkbenchMessages get( Display display ) {
      final WorkbenchMessages[] result = { null };
      UICallBack.runNonUIThreadWithFakeContext( display, new Runnable() {
        public void run() {
          result[ 0 ] = get();
        }
      } );
      return result[ 0 ];
    }
}
