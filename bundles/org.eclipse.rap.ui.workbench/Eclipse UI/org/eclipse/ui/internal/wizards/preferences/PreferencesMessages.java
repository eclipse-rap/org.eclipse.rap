/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others. All rights reserved.   This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.ui.internal.wizards.preferences;

//import org.eclipse.osgi.util.NLS;
import org.eclipse.rwt.RWT;


/**
 * NLS messages class for preferences messages.
 *
 */
// RAP [if]: need session aware NLS
//public class PreferencesMessages extends NLS {
public class PreferencesMessages {
	private static final String BUNDLE_NAME = "org.eclipse.ui.internal.wizards.preferences.messages";//$NON-NLS-1$

	public String WizardPreferences_description;
	public String WizardPreferencesPage_noOptionsSelected;
	public String WizardPreferences_noSpecificPreferenceDescription;

	public String PreferencesExportWizard_export;
	public String WizardPreferencesExportPage1_exportTitle;
	public String WizardPreferencesExportPage1_exportDescription;
	public String WizardPreferencesExportPage1_preferences;
	public String WizardPreferencesExportPage1_noPrefFile;
	public String WizardPreferencesExportPage1_overwrite;
	public String WizardPreferencesExportPage1_title;
	public String WizardPreferencesExportPage1_all;
	public String WizardPreferencesExportPage1_choose;
	public String WizardPreferencesExportPage1_file;

	public String PreferencesExport_error;
	public String PreferencesExport_browse;
	public String PreferencesExport_createTargetDirectory;
	public String PreferencesExport_directoryCreationError;
	public String ExportFile_overwriteExisting;

	public String PreferencesImportWizard_import;
	public String WizardPreferencesImportPage1_importTitle;
	public String WizardPreferencesImportPage1_importDescription;
	public String WizardPreferencesImportPage1_all;
	public String WizardPreferencesImportPage1_choose;
	public String WizardPreferencesImportPage1_file;
	public String WizardPreferencesImportPage1_title;
	public String WizardPreferencesImportPage1_invalidPrefFile;

	public String SelectionDialog_selectLabel;
	public String SelectionDialog_deselectLabel;


	public String WizardDataTransfer_existsQuestion;
	public String WizardDataTransfer_overwriteNameAndPathQuestion;
	public String Question;

// RAP [if]: need session aware NLS
//	static {
//		// load message values from bundle file
//		NLS.initializeMessages(BUNDLE_NAME, PreferencesMessages.class);
//	}

	/**
     * Load message values from bundle file
     * @return localized message
     */
    public static PreferencesMessages get() {
      Class clazz = PreferencesMessages.class;
      Object result = RWT.NLS.getISO8859_1Encoded( BUNDLE_NAME, clazz );
      return ( PreferencesMessages )result;
    }
}