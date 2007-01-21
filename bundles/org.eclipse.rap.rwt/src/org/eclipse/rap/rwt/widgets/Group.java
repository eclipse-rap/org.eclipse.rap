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
import org.eclipse.rap.rwt.graphics.Rectangle;


public class Group extends Composite {
  
  private static final int CLIENT_INSET = 6;
  private static final int FONT_HEIGHT = 15;

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
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    this.text = text;
  }

  public String getText() {
    checkWidget();
    return text;
  }
  
  public Rectangle getClientArea () {
    checkWidget();
    Rectangle bounds = getBounds();
    int x = CLIENT_INSET;
    int y = FONT_HEIGHT;
    int width = Math.max( 0, bounds.width - CLIENT_INSET * 2 );
    int height = Math.max( 0, bounds.height - y - CLIENT_INSET );
    return new Rectangle( x, y, width, height );
  }

  private static int checkStyle( final int style ) {
    int result = style | RWT.NO_FOCUS;
    /*
     * Even though it is legal to create this widget with scroll bars, they
     * serve no useful purpose because they do not automatically scroll the
     * widget's client area. The fix is to clear the SWT style.
     */
    return result & ~( RWT.H_SCROLL | RWT.V_SCROLL );
  }
}
