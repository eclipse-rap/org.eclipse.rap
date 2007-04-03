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
import org.eclipse.rap.rwt.events.ModifyEvent;
import org.eclipse.rap.rwt.events.ModifyListener;
import org.eclipse.rap.rwt.graphics.Point;
import org.eclipse.rap.rwt.internal.graphics.FontSizeEstimation;

/**
 * <p>Due to limitations of the JavaScript library, the current WRAP behavior 
 * of a MULI line text is always as if WRAP was set.</p> 
 */
public class Text extends Control {

  public static final int MAX_TEXT_LIMIT = -1;
  
  private String text = "";
  private int textLimit = MAX_TEXT_LIMIT;
  private final Point selection = new Point( 0, 0 );

  public Text( final Composite parent, final int style ) {
    super( parent, checkStyle( style ) );
  }

  public void setText( final String text ) {
    checkWidget();
    if( text == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    this.text = text;
    selection.x = 0;
    selection.y = 0;
    ModifyEvent modifyEvent = new ModifyEvent( this );
    modifyEvent.processEvent();
  }

  public String getText() {
    checkWidget();
    return text;
  }

  public String getLineDelimiter() {
    checkWidget();
    return "\n";
  }
  
  //////////////////////////
  // Imput length constraint
  
  public void setTextLimit( final int textLimit ) {
    checkWidget();
    if( textLimit == 0 ) {
      error( RWT.ERROR_CANNOT_BE_ZERO );
    }
    // Note that we mimic here the behavior of SWT Text with style MULTI on 
    // Windows. In SWT, other operating systems and/or style flags behave 
    // different.  
    this.textLimit = textLimit;
  }

  public int getTextLimit() {
    checkWidget ();
    return textLimit;
  }
  
  ///////////////////////////////////////////
  // Selection start, count and selected text
  
  public void setSelection( final int start ) {
    checkWidget();
    setSelection( start, start );
 }

  public void setSelection( final int start, final int end ) {
    checkWidget();
    if( start >= 0 && end >= 0 && start <= end ) {
      int validatedStart = Math.min( start, text.length() );
      int validatedEnd = Math.min( end, text.length() );
      selection.x = validatedStart;
      selection.y = validatedEnd;
    }
  }

  public void setSelection( final Point selection ) {
    checkWidget();
    if( selection == null ) {
      error( RWT.ERROR_NULL_ARGUMENT );
    }
    setSelection( selection.x, selection.y );
  }

  public Point getSelection() {
    checkWidget();
    return new Point( selection.x, selection.y );
  }

  public int getSelectionCount() {
    checkWidget();
    return selection.y - selection.x;
  }

  public String getSelectionText() {
    checkWidget();
    return text.substring( selection.x, selection.y );
  }
  
  public void clearSelection() {
    checkWidget();
    selection.x = 0;
    selection.y = 0;
  }
  
  public void selectAll() {
    checkWidget();
    selection.x = 0;
    selection.y = text.length();
  }
  
  ///////////
  // Editable

  public void setEditable( final boolean editable ) {
    checkWidget();
    style &= ~RWT.READ_ONLY;
    if( !editable ) {
      style |= RWT.READ_ONLY;
    }
  }

  public boolean getEditable() {
    checkWidget();
    return ( style & RWT.READ_ONLY ) == 0;
  }
  
  ////////////////////
  // Widget dimensions
  
  public Point computeSize( final int wHint, 
                            final int hHint, 
                            final boolean changed ) 
  {
    checkWidget();
    int height = 0, width = 0;
    if( wHint == RWT.DEFAULT || hHint == RWT.DEFAULT ) {
      boolean wrap = ( style & RWT.MULTI ) != 0 && ( style & RWT.WRAP ) != 0;
      int wrapWidth = 0;
      if( wrap && wHint != RWT.DEFAULT ) {
        wrapWidth = wHint;
      }
      Point extent = FontSizeEstimation.textExtent( getText(),
                                                    wrapWidth,
                                                    getFont() );
      if( extent.x != 0 ) {
        width = extent.x + 12;
      }
      if( extent.y != 0 ) {
        height = extent.y + 6;
      }
    }
    if( width == 0 ) {
      width = DEFAULT_WIDTH;
    }
    if( height == 0 ) {
      height = DEFAULT_HEIGHT;
    }
    if( wHint != RWT.DEFAULT ) {
      width = wHint;
    }
    if( hHint != RWT.DEFAULT ) {
      height = hHint;
    }
//    Rectangle trim = computeTrim( 0, 0, width, height );
//    return new Point( trim.width, trim.height );
    return new Point( width, height );
  }
  
  ///////////////////////////////////////
  // Listener registration/deregistration
  
  public void addModifyListener( final ModifyListener listener ) {
    checkWidget();
    ModifyEvent.addListener( this, listener );
  }

  public void removeModifyListener( final ModifyListener listener ) {
    checkWidget();
    ModifyEvent.removeListener( this, listener );
  }
  
  boolean isTabGroup() {
    return true;
  }
  
  ///////////////////////////////////////
  // Helping method to adjust style flags 
  
  private static int checkStyle( final int style ) {
    int result = style;
    if( ( result & RWT.SINGLE ) != 0 && ( result & RWT.MULTI ) != 0 ) {
      result &= ~RWT.MULTI;
    }
    result = checkBits( result, RWT.LEFT, RWT.CENTER, RWT.RIGHT, 0, 0, 0 );
    if( ( result & RWT.SINGLE ) != 0 ) {
      result &= ~( RWT.H_SCROLL | RWT.V_SCROLL | RWT.WRAP );
    }
    if( ( result & RWT.WRAP ) != 0 ) {
      result |= RWT.MULTI;
      result &= ~RWT.H_SCROLL;
    }
    if( ( result & RWT.MULTI ) != 0 ) {
      result &= ~RWT.PASSWORD;
    }
    if( ( result & ( RWT.SINGLE | RWT.MULTI ) ) != 0 ) {
      return result;
    }
    if( ( style & ( RWT.H_SCROLL | RWT.V_SCROLL ) ) != 0 ) {
      return result | RWT.MULTI;
    }
    return result | RWT.SINGLE;
  }
}
