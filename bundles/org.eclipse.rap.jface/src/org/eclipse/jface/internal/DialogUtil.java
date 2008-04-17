/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.jface.internal;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.graphics.Font;

/**
 * Only used in RAP to avoid breaking API in Dialog class.
 * @since 1.1
 */
public class DialogUtil {
	
	private static final int HORIZONTAL_DIALOG_UNIT_PER_CHAR = 4;
	private static final int VERTICAL_DIALOG_UNITS_PER_CHAR = 8;
	
  /**
   * Substitute for Dialog#convertHorizontalDLUsToPixels( int ).
   * JFaceResources#getDialogFont() is used to calculate pixels. 
   * 
   * @param dlus the number of horizontal dialog units
   * @return the number of pixels
   */
	public static int convertHorizontalDLUsToPixels(int dlus) {
	  return convertHorizontalDLUsToPixels( JFaceResources.getDialogFont(), 
	                                        dlus );
	}

  /**
   * Substitute for Dialog#convertHorizontalDLUsToPixels( FontMetrics, int ).
   * 
   * @param font the font 
   * @param dlus the number of horizontal dialog units
   * @return the number of pixels
   */
  public static int convertHorizontalDLUsToPixels( Font font, int dlus) {
    // round to the nearest pixel
    float avgCharWidth = Graphics.getAvgCharWidth( font );
    return ( int )( ( avgCharWidth * dlus + HORIZONTAL_DIALOG_UNIT_PER_CHAR / 2 ) / HORIZONTAL_DIALOG_UNIT_PER_CHAR );
  }

  /**
	 * Substitute for Dialog#convertVerticalDLUsToPixels( int ).
	 * JFaceResources#getDialogFont() is used to calculate pixels. 
	 * 
	 * @param dlus the number of vertical dialog units
	 * @return the number of pixels
	 */
	public static int convertVerticalDLUsToPixels( int dlus ) {
	  return convertVerticalDLUsToPixels( JFaceResources.getDialogFont(), dlus );
	}

  /**
   * Substitute for Dialog#convertVerticalDLUsToPixels( FontMetrics, int ).
   * 
   * @param font the font 
   * @param dlus the number of vertical dialog units
   * @return the number of pixels
   */
	public static int convertVerticalDLUsToPixels( Font font, int dlus ) {
	  // round to the nearest pixel
	  int charHeight = Graphics.getCharHeight( font );
    return ( charHeight * dlus + VERTICAL_DIALOG_UNITS_PER_CHAR / 2 ) / VERTICAL_DIALOG_UNITS_PER_CHAR;
	}
}
