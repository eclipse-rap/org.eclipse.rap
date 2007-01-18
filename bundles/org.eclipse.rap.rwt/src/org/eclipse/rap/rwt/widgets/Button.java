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
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.events.SelectionListener;
import org.eclipse.rap.rwt.graphics.Image;


/**
 * TODO: [fappel] comment
 * 
 * <p>Current known limitations:</p>
 * <ul><li>Font property does not have any effect client side</li>
 * <li>Check- and radio button do not change ther selection state when there
 * is no SelectionListener registered.</li>
 * </ul>
 */
// TODO [rh] selection state for check and radio buttons is only changed when a
//      listener was added.
public class Button extends Control {

  private String text = "";
  private boolean selected = false;
  private Image image;

  public Button( final Composite parent, final int style ) {
    super( parent, checkStyle( style ) );
  }

  ////////////////
  // Getter/setter
  
  public void setText( final String text ) {
    if( text == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    if( ( style & RWT.ARROW ) == 0 ) {
      this.text = text;
    }
  }

  public String getText() {
    return text;
  }

  public boolean getSelection() {
    boolean result = false;
    if( ( style & ( RWT.CHECK | RWT.RADIO | RWT.TOGGLE ) ) != 0 ) {
      result = selected;
    }
    return result;
  }
  
  public void setSelection( final boolean selected ) {
    if( ( style & ( RWT.CHECK | RWT.RADIO | RWT.TOGGLE ) ) != 0 ) {
      this.selected = selected;
    }
  }
  
  public Image getImage() {
    return image;
  }

  public void setImage( final Image image ) {
    if( ( style & RWT.ARROW ) == 0 ) {
      this.image = image;
    }
  }
  
  public int getAlignment() {
    int result;
    if( ( style & RWT.ARROW ) != 0 ) {
      if( ( style & RWT.UP ) != 0 ) {
        result = RWT.UP;
      } else if( ( style & RWT.DOWN ) != 0 ) {
        result = RWT.DOWN;
      } else if( ( style & RWT.LEFT ) != 0 ) {
        result = RWT.LEFT;
      } else if( ( style & RWT.RIGHT ) != 0 ) {
        result = RWT.RIGHT;
      } else {
        result = RWT.UP;
      }
    } else {
      if( ( style & RWT.LEFT ) != 0 ) {
        result = RWT.LEFT;
      } else if( ( style & RWT.CENTER ) != 0 ) {
        result = RWT.CENTER;
      } else if( ( style & RWT.RIGHT ) != 0 ) {
        result = RWT.RIGHT;
      } else {
        result = RWT.LEFT;
      }
    }
    return result;
  }
  
  public void setAlignment( final int alignment ) {
    if( ( style & RWT.ARROW ) != 0 ) {
      if( ( style & ( RWT.UP | RWT.DOWN | RWT.LEFT | RWT.RIGHT ) ) != 0 ) {
        style &= ~( RWT.UP | RWT.DOWN | RWT.LEFT | RWT.RIGHT );
        style |= alignment & ( RWT.UP | RWT.DOWN | RWT.LEFT | RWT.RIGHT );
      }
    } else if( ( alignment & ( RWT.LEFT | RWT.RIGHT | RWT.CENTER ) ) != 0 ) {
      style &= ~( RWT.LEFT | RWT.RIGHT | RWT.CENTER );
      style |= alignment & ( RWT.LEFT | RWT.RIGHT | RWT.CENTER );
    }
  }

  ///////////////////////////////////////
  // Listener registration/deregistration
  
  public void addSelectionListener( final SelectionListener listener ) {
    SelectionEvent.addListener( this, listener );
  }

  public void removeSelectionListener( final SelectionListener listener ) {
    SelectionEvent.removeListener( this, listener );
  }

  //////////////////
  // Helping methods
  
  private static int checkStyle( final int style ) {
    int result = checkBits( style,
                            RWT.PUSH,
                            RWT.ARROW,
                            RWT.CHECK,
                            RWT.RADIO,
                            RWT.TOGGLE,
                            0 );
    if( ( result & ( RWT.PUSH | RWT.TOGGLE ) ) != 0 ) {
      result = checkBits( result, RWT.CENTER, RWT.LEFT, RWT.RIGHT, 0, 0, 0 );
    } else if( ( result & ( RWT.CHECK | RWT.RADIO ) ) != 0 ) {
      result = checkBits( result, RWT.LEFT, RWT.RIGHT, RWT.CENTER, 0, 0, 0 );
    } else if( ( result & RWT.ARROW ) != 0 ) {
      result |= RWT.NO_FOCUS;
      result = checkBits( result, RWT.UP, RWT.DOWN, RWT.LEFT, RWT.RIGHT, 0, 0 );
    }
    return result;
  }
}
