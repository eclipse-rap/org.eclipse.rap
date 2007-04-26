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

package org.eclipse.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;


public class Group extends Composite {
  
  private static final int TRIM_LEFT = 5;
  private static final int TRIM_TOP = 15;
  private static final int TRIM_RIGHT = 5;
  private static final int TRIM_BOTTOM = 5;

  private String text = "";

  /**
   * <p>The various SHADOW_XXX style are not yet implemented.</p>
   */
  public Group( final Composite parent, final int style ) {
    super( parent, checkStyle( style ) );
  }

  public void setText( final String text ) {
    checkWidget();
    if( text == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    this.text = text;
  }

  public String getText() {
    checkWidget();
    return text;
  }
    
  public Rectangle getClientArea() {
    checkWidget();
    Rectangle bounds = getBounds();
    int width = Math.max( 0, bounds.width - TRIM_LEFT - TRIM_RIGHT );
    int height = Math.max( 0, bounds.height - TRIM_TOP - TRIM_BOTTOM );
    return new Rectangle( TRIM_LEFT, TRIM_TOP, width, height );
  }
  
  public Rectangle computeTrim( final int x,
                                final int y,
                                final int width,
                                final int height ) 
  {
    return super.computeTrim( x - TRIM_LEFT,
                              y - TRIM_TOP,
                              width + TRIM_LEFT + TRIM_RIGHT,
                              height + TRIM_TOP + TRIM_BOTTOM );
  }
  
  private static int checkStyle( final int style ) {
    int result = style | SWT.NO_FOCUS;
    /*
     * Even though it is legal to create this widget with scroll bars, they
     * serve no useful purpose because they do not automatically scroll the
     * widget's client area. The fix is to clear the SWT style.
     */
    return result & ~( SWT.H_SCROLL | SWT.V_SCROLL );
  }
}
