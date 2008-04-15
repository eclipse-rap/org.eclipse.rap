/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jface.dialogs;

/**
 * Various dialog-related constants.
 * <p>
 * Within the dialog framework, all buttons are referred to by a button id.
 * Various common buttons, like "OK", "Cancel", and "Finish", have pre-assigned
 * button ids for convenience. If an application requires other dialog buttons,
 * they should be assigned application-specific button ids counting up from
 * <code>CLIENT_ID</code>.
 * </p>
 * <p>
 * Button label constants are also provided for the common buttons. JFace
 * automatically localizes these strings to the current locale; that is,
 * <code>YES_LABEL</code> would be bound to the string <code>"Si"</code> in
 * a Spanish locale, but to <code>"Oui"</code> in a French one.
 * </p>
 * <p>
 * All margins, spacings, and sizes are given in "dialog units" (DLUs), where
 * <ul>
 * <li>1 horizontal DLU = 1/4 average character width</li>
 * <li>1 vertical DLU = 1/8 average character height</li>
 * </ul>
 * </p>
 */
import org.eclipse.rwt.RWT;

/**
 * IDialogConstants is the interface for common dialog strings and ids
 * used throughout JFace.
 * It is recommended that you use these labels and ids whereever 
 * for consistency with the JFace dialogs.
 * 
 * <p>Note: Due to native language support for multiple sessions the
 * original RCP interface has been switched to a class. This was necessary
 * to support localized labels. The name 'IDialogConstants' was kept to
 * minimize code-changes between RCP and RAP applications.</p>
 */
public class IDialogConstants {
    // button ids
	
	// Note:  if new button ids are added, see 
	// MessageDialogWithToggle.mapButtonLabelToButtonID(String, int)
    /**
     * Button id for an "Ok" button (value 0).
     */
    public final static int OK_ID = 0;

    /**
     * Button id for a "Cancel" button (value 1).
     */
    public final static int CANCEL_ID = 1;

    /**
     * Button id for a "Yes" button (value 2).
     */
    public final static int YES_ID = 2;

    /**
     * Button id for a "No" button (value 3).
     */
    public final static int NO_ID = 3;

    /**
     * Button id for a "Yes to All" button (value 4).
     */
    public final static int YES_TO_ALL_ID = 4;

    /**
     * Button id for a "Skip" button (value 5).
     */
    public final static int SKIP_ID = 5;

    /**
     * Button id for a "Stop" button (value 6).
     */
    public final static int STOP_ID = 6;

    /**
     * Button id for an "Abort" button (value 7).
     */
    public final static int ABORT_ID = 7;

    /**
     * Button id for a "Retry" button (value 8).
     */
    public final static int RETRY_ID = 8;

    /**
     * Button id for an "Ignore" button (value 9).
     */
    public final static int IGNORE_ID = 9;

    /**
     * Button id for a "Proceed" button (value 10).
     */
    public final static int PROCEED_ID = 10;

    /**
     * Button id for an "Open" button (value 11).
     */
    public final static int OPEN_ID = 11;

    /**
     * Button id for a "Close" button (value 12).
     */
    public final static int CLOSE_ID = 12;

    /**
     * Button id for a "Details" button (value 13).
     */
    public final static int DETAILS_ID = 13;

    /**
     * Button id for a "Back" button (value 14).
     */
    public final static int BACK_ID = 14;

    /**
     * Button id for a "Next" button (value 15).
     */
    public final static int NEXT_ID = 15;

    /**
     * Button id for a "Finish" button (value 16).
     */
    public final static int FINISH_ID = 16;

    /**
     * Button id for a "Help" button (value 17).
     */
    public final static int HELP_ID = 17;

    /**
     * Button id for a "Select All" button (value 18).
     */
    public final static int SELECT_ALL_ID = 18;

    /**
     * Button id for a "Deselect All" button (value 19).
     */
    public final static int DESELECT_ALL_ID = 19;

    /**
     * Button id for a "Select types" button (value 20).
     */
    public final static int SELECT_TYPES_ID = 20;

    /**
     * Button id for a "No to All" button (value 21).
     */
    public final static int NO_TO_ALL_ID = 21;

    /**
     * Starting button id reserved for internal use by JFace (value 256). JFace
     * classes make ids by adding to this number.
     */
    public final static int INTERNAL_ID = 256;

    /**
     * Starting button id reserved for use by clients of JFace (value 1024).
     * Clients of JFace should make ids by adding to this number.
     */
    public final static int CLIENT_ID = 1024;

    // button labels
    /**
     * The label for OK buttons.
     */
    public String OK_LABEL;

    /**
     * The label for cancel buttons.
     */
    public String CANCEL_LABEL;

    /**
     * The label for yes buttons.
     */
    public String YES_LABEL;

    /**
     * The label for no buttons.
     */
    public String NO_LABEL;

    /**
     * The label for not to all buttons.
     */
    public String NO_TO_ALL_LABEL;

    /**
     * The label for yes to all buttons.
     */
    public String YES_TO_ALL_LABEL;

    /**
     * The label for skip buttons.
     */
    public String SKIP_LABEL;

    /**
     * The label for stop buttons.
     */
    public String STOP_LABEL;

    /**
     * The label for abort buttons.
     */
    public String ABORT_LABEL;

    /**
     * The label for retry buttons.
     */
    public String RETRY_LABEL;

    /**
     * The label for ignore buttons.
     */
    public String IGNORE_LABEL;

    /**
     * The label for proceed buttons.
     */
    public String PROCEED_LABEL;

    /**
     * The label for open buttons.
     */
    public String OPEN_LABEL;

    /**
     * The label for close buttons.
     */
    public String CLOSE_LABEL;

    /**
     * The label for show details buttons.
     */
    public String SHOW_DETAILS_LABEL;

    /**
     * The label for hide details buttons.
     */
    public String HIDE_DETAILS_LABEL;

    /**
     * The label for back buttons.
     */
    public String BACK_LABEL;

    /**
     * The label for next buttons.
     */
    public String NEXT_LABEL;

    /**
     * The label for finish buttons.
     */
    public String FINISH_LABEL;

    /**
     * The label for help buttons.
     */
    public String HELP_LABEL;

    // Margins, spacings, and sizes
    /**
     * Vertical margin in dialog units (value 7).
     */
    public final  static int VERTICAL_MARGIN = 7;

    /**
     * Vertical spacing in dialog units (value 4).
     */
    public final static int VERTICAL_SPACING = 4;

    /**
     * Horizontal margin in dialog units (value 7).
     */
    public final static int HORIZONTAL_MARGIN = 7;

    /**
     * Horizontal spacing in dialog units (value 4).
     */
    public final static int HORIZONTAL_SPACING = 4;

    /**
     * Height of button bar in dialog units (value 25).
     */
    public final static int BUTTON_BAR_HEIGHT = 25;

    /**
     * Left margin in dialog units (value 20).
     */
    public final static int LEFT_MARGIN = 20;

    /**
     * Button margin in dialog units (value 4).
     */
    public final static int BUTTON_MARGIN = 4;

    /**
     * Button height in dialog units (value 14).
     * 
     * @deprecated This constant is no longer in use.
     * The button heights are now determined by the layout.
     */
    public final static int BUTTON_HEIGHT = 14;

    /**
     * Button width in dialog units (value 61).
     */
    public final static int BUTTON_WIDTH = 61;

    /**
     * Indent in dialog units (value 21).
     */
    public final static int INDENT = 21;

    /**
     * Small indent in dialog units (value 7).
     */
    public final static int SMALL_INDENT = 7;

    /**
     * Entry field width in dialog units (value 200).
     */
    public final static int ENTRY_FIELD_WIDTH = 200;

    /**
     * Minimum width of message area in dialog units (value 300).
     */
    public final static int MINIMUM_MESSAGE_AREA_WIDTH = 300;

    /**
     * Returns an implementation of <code>IDialogConstants</code> according
     * to the current locale.
     * 
     * @return an <code>IDialogConstants</code> implementation for the current locale
     */
    public static IDialogConstants get() {
      String id = "org.eclipse.jface.dialogmessages"; //$NON-NLS-1$
      Class clazz = IDialogConstants.class;
      return ( IDialogConstants )RWT.NLS.getISO8859_1Encoded( id, clazz );
    }
}
