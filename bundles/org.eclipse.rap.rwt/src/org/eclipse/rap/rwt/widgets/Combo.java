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
import org.eclipse.rap.rwt.graphics.Point;

// TODO [rh] SWT sends an SWT.Modify event when selection is changed or items
//      are aded/removed
public class Combo extends Scrollable {

  private final ListModel model;
  
  public Combo( final Composite parent, final int style ) {
    super( parent, checkStyle( style ) );
    model = new ListModel( true );
  }

  //////////////////////////////////////
  // Methods to manipulate the selection
  
  public int getSelectionIndex() {
    checkWidget();
    return model.getSelectionIndex();
  }

  public void select( final int selectionIndex ) {
    checkWidget();
    model.setSelection( selectionIndex );
  }

  public void deselect( final int index ) {
    checkWidget();
    if( index == model.getSelectionIndex() ) {
      model.setSelection( -1 );
    }
  }
  
  public void deselectAll() {
    checkWidget();
    model.deselectAll();
  }
  
  ///////////////////////////////////////
  // Methods to manipulate and get items
  
  public void add( final String string ) {
    checkWidget();
    model.add( string );
  }
  
  public void add( final String string, final int index ) {
    checkWidget();
    model.add( string, index );
  }
  
  public void remove( final int index ) {
    checkWidget();
    model.remove( index );
  }
  
  public void remove( final int start, final int end ) {
    checkWidget();
    model.remove( start, end );
  }
  
  public void remove( final String string ) {
    checkWidget();
    model.remove( string );
  }
  
  public void removeAll() {
    checkWidget();
    model.removeAll();
  }
  
  public void setItem( final int index, final String string ) {
    checkWidget();
    model.setItem( index, string );
  }

  public void setItems( final String[] items ) {
    checkWidget();
    model.setItems( items );
  }
  
  public String getItem( final int index ) {
    checkWidget();
    return model.getItem( index );
  }
  
  public String[] getItems() {
    checkWidget();
    return model.getItems();
  }
  
  public int getItemCount() {
    checkWidget();
    return model.getItemCount();
  }
  
  ////////////////////
  // Widget dimensions

  public Point computeSize( final int wHint,
                            final int hHint,
                            final boolean changed )
  {
    checkWidget();
    int width = 0;
    int height = 0;
    if( wHint == RWT.DEFAULT ) {
      // TODO [rst] resonable implementation
      width = 85;
    }
    if( wHint == RWT.DEFAULT ) {
      // TODO [rst] resonable implementation
      height = 22;
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
    return new Point( width, height );
  }
  
  /////////////////////////////////////////
  // Listener registration/de-registration

  public void addSelectionListener( final SelectionListener listener ) {
    checkWidget();
    SelectionEvent.addListener( this, listener );
  }

  public void removeSelectionListener( final SelectionListener listener ) {
    checkWidget();
    SelectionEvent.removeListener( this, listener );
  }

  //////////////////
  // Helping methods 
  
  private static int checkStyle( final int style ) {
    int result = style;
    /*
     * Feature in Windows.  It is not possible to create
     * a combo box that has a border using Windows style
     * bits.  All combo boxes draw their own border and
     * do not use the standard Windows border styles.
     * Therefore, no matter what style bits are specified,
     * clear the BORDER bits so that the SWT style will
     * match the Windows widget.
     *
     * The Windows behavior is currently implemented on
     * all platforms.
     */
    result &= ~RWT.BORDER;
    
    /*
     * Even though it is legal to create this widget
     * with scroll bars, they serve no useful purpose
     * because they do not automatically scroll the
     * widget's client area.  The fix is to clear
     * the SWT style.
     */
    result &= ~( RWT.H_SCROLL | RWT.V_SCROLL );
    result = checkBits( result, RWT.DROP_DOWN, RWT.SIMPLE, 0, 0, 0, 0 );
    if( ( result & RWT.SIMPLE ) != 0 ) {
      return result & ~RWT.READ_ONLY;
    }
    result |= RWT.H_SCROLL; // Copied from SWT Combo constructor
    return result;
  }
}
