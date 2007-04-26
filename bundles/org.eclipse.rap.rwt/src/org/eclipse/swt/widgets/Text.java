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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.graphics.FontSizeEstimation;

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
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
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
      error( SWT.ERROR_CANNOT_BE_ZERO );
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
      error( SWT.ERROR_NULL_ARGUMENT );
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
    style &= ~SWT.READ_ONLY;
    if( !editable ) {
      style |= SWT.READ_ONLY;
    }
  }

  public boolean getEditable() {
    checkWidget();
    return ( style & SWT.READ_ONLY ) == 0;
  }
  
  ////////////////////
  // Widget dimensions
  
  public Point computeSize( final int wHint, 
                            final int hHint, 
                            final boolean changed ) 
  {
    checkWidget();
    int height = 0, width = 0;
    if( wHint == SWT.DEFAULT || hHint == SWT.DEFAULT ) {
      boolean wrap = ( style & SWT.MULTI ) != 0 && ( style & SWT.WRAP ) != 0;
      int wrapWidth = 0;
      if( wrap && wHint != SWT.DEFAULT ) {
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
    if( wHint != SWT.DEFAULT ) {
      width = wHint;
    }
    if( hHint != SWT.DEFAULT ) {
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
    if( ( result & SWT.SINGLE ) != 0 && ( result & SWT.MULTI ) != 0 ) {
      result &= ~SWT.MULTI;
    }
    result = checkBits( result, SWT.LEFT, SWT.CENTER, SWT.RIGHT, 0, 0, 0 );
    if( ( result & SWT.SINGLE ) != 0 ) {
      result &= ~( SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP );
    }
    if( ( result & SWT.WRAP ) != 0 ) {
      result |= SWT.MULTI;
      result &= ~SWT.H_SCROLL;
    }
    if( ( result & SWT.MULTI ) != 0 ) {
      result &= ~SWT.PASSWORD;
    }
    if( ( result & ( SWT.SINGLE | SWT.MULTI ) ) != 0 ) {
      return result;
    }
    if( ( style & ( SWT.H_SCROLL | SWT.V_SCROLL ) ) != 0 ) {
      return result | SWT.MULTI;
    }
    return result | SWT.SINGLE;
  }
}
