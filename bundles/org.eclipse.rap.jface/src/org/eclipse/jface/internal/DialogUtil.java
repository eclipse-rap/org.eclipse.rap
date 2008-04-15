/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.jface.internal;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.graphics.Font;

/**
 * Only used in RAP to avoid breaking API in Dialog class.
 */
public class DialogUtil {
	
	/**
	 * Number of horizontal dialog units per character, value <code>4</code>.
	 */
	private static final int HORIZONTAL_DIALOG_UNIT_PER_CHAR = 4;

	/**
	 * Number of vertical dialog units per character, value <code>8</code>.
	 */
	private static final int VERTICAL_DIALOG_UNITS_PER_CHAR = 8;
	
	private static Font dialogFont = JFaceResources.getDialogFont(); 
	/**
	 * Returns the number of pixels corresponding to the height of the given
	 * number of characters.
	 * <p>
	 * The required <code>FontMetrics</code> parameter may be created in the
	 * following way: <code>
	 * 	GC gc = new GC(control);
	 *	gc.setFont(control.getFont());
	 *	fontMetrics = gc.getFontMetrics();
	 *	gc.dispose();
	 * </code>
	 * </p>
	 * 
	 * @param chars
	 *            the number of characters
	 * @return the number of pixels
	 * @since 2.0
	 */
	public static int convertHeightInCharsToPixels(int chars) {
		
		return Graphics.getCharHeight(dialogFont)* chars;
	}

	
	/**
	 * Returns the number of pixels corresponding to the given number of
	 * horizontal dialog units.
	 * <p>
	 * The required <code>FontMetrics</code> parameter may be created in the
	 * following way: <code>
	 * 	GC gc = new GC(control);
	 *	gc.setFont(control.getFont());
	 *	fontMetrics = gc.getFontMetrics();
	 *	gc.dispose();
	 * </code>
	 * </p>
	 * 
	 * @param dlus
	 *            the number of horizontal dialog units
	 * @return the number of pixels
	 * @since 2.0
	 */
	public static int convertHorizontalDLUsToPixels(int dlus) {
		// round to the nearest pixel
		return (int) ((Graphics.getAvgCharWidth(dialogFont)* dlus + HORIZONTAL_DIALOG_UNIT_PER_CHAR / 2)
				/ HORIZONTAL_DIALOG_UNIT_PER_CHAR);
	}

	/**
	 * Returns the number of pixels corresponding to the given number of
	 * vertical dialog units.
	 * <p>
	 * The required <code>FontMetrics</code> parameter may be created in the
	 * following way: <code>
	 * 	GC gc = new GC(control);
	 *	gc.setFont(control.getFont());
	 *	fontMetrics = gc.getFontMetrics();
	 *	gc.dispose();
	 * </code>
	 * </p>
	 * 
	 * @param dlus
	 *            the number of vertical dialog units
	 * @return the number of pixels
	 * @since 2.0
	 */
	public static int convertVerticalDLUsToPixels(int dlus) {
		// round to the nearest pixel
		return (Graphics.getCharHeight(dialogFont)* dlus + VERTICAL_DIALOG_UNITS_PER_CHAR / 2)
				/ VERTICAL_DIALOG_UNITS_PER_CHAR;
	}

	/**
	 * Returns the number of pixels corresponding to the width of the given
	 * number of characters.
	 * <p>
	 * The required <code>FontMetrics</code> parameter may be created in the
	 * following way: <code>
	 * 	GC gc = new GC(control);
	 *	gc.setFont(control.getFont());
	 *	fontMetrics = gc.getFontMetrics();
	 *	gc.dispose();
	 * </code>
	 * </p>
	 * 
	 * @param chars
	 *            the number of characters
	 * @return the number of pixels
	 * @since 2.0
	 */
	public static int convertWidthInCharsToPixels(int chars) {
		return (int) (Graphics.getAvgCharWidth(dialogFont)* chars);
	}

}
