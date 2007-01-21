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
import org.eclipse.rap.rwt.graphics.Image;

public class Label extends Control {

  private String text = "";
  private Image image;

  public Label( final Composite parent, final int style ) {
    super( parent, checkStyle( style ) );
  }

  public void setText( final String text ) {
    checkWidget();
    if( text == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    if( ( style & RWT.SEPARATOR ) == 0 ) {
      this.text = text;
    }
  }

  public String getText() {
    checkWidget();
    return text;
  }
  
  /**
   * <p>The LCA does not yet handle images. So, setting an image currently 
   * does not have any effect.</p>
   */
  public void setImage( final Image image ) {
    checkWidget();
    if( ( style & RWT.SEPARATOR ) == 0 ) {
      this.image = image;
    } 
  }
  
  public Image getImage() {
    checkWidget();
    return image;
  }

  public void setAlignment( final int alignment ) {
    checkWidget();
    if(    ( style & RWT.SEPARATOR ) == 0 
        && ( alignment & ( RWT.LEFT | RWT.RIGHT | RWT.CENTER ) ) != 0 ) 
    {
      style &= ~( RWT.LEFT | RWT.RIGHT | RWT.CENTER );
      style |= alignment & ( RWT.LEFT | RWT.RIGHT | RWT.CENTER );
    }
  }

  public int getAlignment() {
    checkWidget();
    int result;
    if( ( style & RWT.SEPARATOR ) != 0 ) {
      result = 0;
    } else if( ( style & RWT.LEFT ) != 0 ) {
      result = RWT.LEFT;
    } else if( ( style & RWT.CENTER ) != 0 ) {
      result = RWT.CENTER;
    } else if( ( style & RWT.RIGHT ) != 0 ) {
      result = RWT.RIGHT;
    } else {
      result = RWT.LEFT;
    }
    return result;
  }

  private static int checkStyle( final int style ) {
    int result = style;
    result |= RWT.NO_FOCUS;
    if( ( style & RWT.SEPARATOR ) != 0 ) {
      result = checkBits( result, RWT.VERTICAL, RWT.HORIZONTAL, 0, 0, 0, 0 );
      result = checkBits ( result, 
                           RWT.SHADOW_OUT, 
                           RWT.SHADOW_IN, 
                           RWT.SHADOW_NONE, 
                           0, 
                           0, 
                           0 );
    }
    result = checkBits( result, RWT.LEFT, RWT.CENTER, RWT.RIGHT, 0, 0, 0 );
    return result;
  } 
}
