/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.graphics;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;

/**
 * TODO [fappel] comment
 */
public abstract class Device {
  

  // TODO [rh] this must be in sync with font in DefaultAppearanceTheme.js
  private static final String SYSTEM_FONT_NAME 
    = "Segoe UI, Corbel, Calibri, Tahoma, Lucida Sans Unicode, sans-serif";
  
  private static final String ICON_PATH = "resource/widget/rap/dialog";
  public static final String ERROR_IMAGE_PATH = ICON_PATH + "/error.png";
  public static final String INFO_IMAGE_PATH = ICON_PATH + "/information.png";
  public static final String QUESTION_IMAGE_PATH = ICON_PATH + "/question.png";
  public static final String WARNING_IMAGE_PATH = ICON_PATH + "/warning.png";

  public final Font systemFont;
  public Image errorImage;
  public Image infoImage;
  public Image questionImage;
  public Image warningImage;

  
  public Device() {
    systemFont = Font.getFont( SYSTEM_FONT_NAME, 11, SWT.NORMAL );
  }
  
  /**
   * Returns the matching standard color for the given
   * constant, which should be one of the color constants
   * specified in class <code>SWT</code>. Any value other
   * than one of the SWT color constants which is passed
   * in will result in the color black. This color should
   * not be free'd because it was allocated by the system,
   * not the application.
   * 
   * @param id the color constant
   * @return the matching color
   *
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *    <li>ERROR_DEVICE_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   *
   * @see SWT
   */
  public Color getSystemColor( final int id ) {
    checkDevice();
    int pixel = 0x02000000;
    switch( id ) {
      case SWT.COLOR_WHITE:
        pixel = 0x02FFFFFF;
      break;
      case SWT.COLOR_BLACK:
        pixel = 0x02000000;
      break;
      case SWT.COLOR_RED:
        pixel = 0x020000FF;
      break;
      case SWT.COLOR_DARK_RED:
        pixel = 0x02000080;
      break;
      case SWT.COLOR_GREEN:
        pixel = 0x0200FF00;
      break;
      case SWT.COLOR_DARK_GREEN:
        pixel = 0x02008000;
      break;
      case SWT.COLOR_YELLOW:
        pixel = 0x0200FFFF;
      break;
      case SWT.COLOR_DARK_YELLOW:
        pixel = 0x02008080;
      break;
      case SWT.COLOR_BLUE:
        pixel = 0x02FF0000;
      break;
      case SWT.COLOR_DARK_BLUE:
        pixel = 0x02800000;
      break;
      case SWT.COLOR_MAGENTA:
        pixel = 0x02FF00FF;
      break;
      case SWT.COLOR_DARK_MAGENTA:
        pixel = 0x02800080;
      break;
      case SWT.COLOR_CYAN:
        pixel = 0x02FFFF00;
      break;
      case SWT.COLOR_DARK_CYAN:
        pixel = 0x02808000;
      break;
      case SWT.COLOR_GRAY:
        pixel = 0x02C0C0C0;
      break;
      case SWT.COLOR_DARK_GRAY:
        pixel = 0x02808080;
      break;
    }
    return Color.getColor( pixel );
  }

  /**
   * Returns a reasonable font for applications to use.
   * On some platforms, this will match the "default font"
   * or "system font" if such can be found.  This font
   * should not be free'd because it was allocated by the
   * system, not the application.
   * <p>
   * Typically, applications which want the default look
   * should simply not set the font on the widgets they
   * create. Widgets are always created with the correct
   * default font for the class of user-interface component
   * they represent.
   * </p>
   *
   * @return a font
   *
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   *    <li>ERROR_DEVICE_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   */
  public Font getSystemFont() {
    checkDevice();
    return systemFont;
  }

  public void checkDevice() {
    // TODO [rh] implementation missing
  }

}
