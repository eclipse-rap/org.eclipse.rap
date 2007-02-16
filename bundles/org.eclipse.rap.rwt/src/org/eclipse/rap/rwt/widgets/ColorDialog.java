/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
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

package org.eclipse.rap.rwt.widgets;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.*;
import org.eclipse.rap.rwt.graphics.Color;
import org.eclipse.rap.rwt.graphics.RGB;
import org.eclipse.rap.rwt.layout.*;

/**
 * Instances of this class allow the user to select a color
 * from a predefined set of available colors.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>(none)</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 * <p>
 * IMPORTANT: This class is intended to be subclassed <em>only</em>
 * within the SWT implementation.
 * </p>
 */

public class ColorDialog extends Dialog {
  RGB rgb;
  private Shell shell;

  /**
   * Constructs a new instance of this class given only its parent.
   *
   * @param parent a composite control which will be the parent of the new instance
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
   *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
   * </ul>
   *
   * @see SWT
   * @see Widget#checkSubclass
   * @see Widget#getStyle
   */
  public ColorDialog( Shell parent ) {
    this( parent, RWT.APPLICATION_MODAL );
  }

  /**
   * Constructs a new instance of this class given its parent
   * and a style value describing its behavior and appearance.
   * <p>
   * The style value is either one of the style constants defined in
   * class <code>SWT</code> which is applicable to instances of this
   * class, or must be built by <em>bitwise OR</em>'ing together 
   * (that is, using the <code>int</code> "|" operator) two or more
   * of those <code>SWT</code> style constants. The class description
   * lists the style constants that are applicable to the class.
   * Style bits are also inherited from superclasses.
   * </p>
   *
   * @param parent a composite control which will be the parent of the new instance (cannot be null)
   * @param style the style of control to construct
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
   *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
   * </ul>
   *
   * @see SWT
   * @see Widget#checkSubclass
   * @see Widget#getStyle
   */
  public ColorDialog( Shell parent, int style ) {
    super( parent, style );
    checkSubclass();
    shell = createShell();
    shell.layout();
    shell.pack();
  }

//int CCHookProc (int hdlg, int uiMsg, int lParam, int lpData) {
//switch (uiMsg) {
//case OS.WM_INITDIALOG:
//if (title != null && title.length () != 0) {
///* Use the character encoding for the default locale */
//TCHAR buffer = new TCHAR (0, title, true);
//OS.SetWindowText (hdlg, buffer);
//}
//break;
//}
//return 0;
//}

  /**
   * Returns the currently selected color in the receiver.
   *
   * @return the RGB value for the selected color, may be null
   *
   * @see PaletteData#getRGBs
   */
  public RGB getRGB() {
    return rgb;
  }

  /**
   * Makes the receiver visible and brings it to the front
   * of the display.
   *
   * @return the selected color, or null if the dialog was
   *         cancelled, no color was selected, or an error
   *         occurred
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void open( final Runnable callback ) {
    shell.addShellListener( new ShellAdapter() {
      public void shellClosed( ShellEvent event ) {
        callback.run();
      }
    } );
    shell.open();
  }
  
  private Shell createShell() {
    Shell shell = new Shell( parent, RWT.DIALOG_TRIM | RWT.APPLICATION_MODAL );
    RowLayout layout = new RowLayout();
    shell.setLayout( layout  );
    RGB red = new RGB( 0xFF, 0x00, 0x00 );
    RGB green = new RGB( 0x00, 0xFF, 0x00 );
    RGB blue = new RGB( 0x00, 0x00, 0xFF );
    RGB[] colors = new RGB[] { red, green, blue };
    
    for( int i = 0; i < colors.length; i++ ) {
      RGB rgb = colors[ i ];
      Button button = new Button( shell, RWT.FLAT );
      button.setText( " " );
      button.setBackground( Color.getColor( rgb ) );
      button.setLayoutData( new RowData( 20, 20 ) );
      button.addSelectionListener( getColorButtonListener( rgb ) );      
    }
    
    return shell;
  }

  private SelectionListener getColorButtonListener( final RGB rgb ) {
    return new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        ColorDialog.this.rgb = rgb;
        shell.close();
      }
    };
  }
  
  /**
   * Sets the receiver's selected color to be the argument.
   *
   * @param rgb the new RGB value for the selected color, may be
   *        null to let the platform select a default when
   *        open() is called
   * @see PaletteData#getRGBs
   */
  public void setRGB( RGB rgb ) {
    this.rgb = rgb;
  }
}
