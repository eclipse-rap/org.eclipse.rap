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
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.internal.graphics.FontSizeEstimation;

/**
 * <p>Currently the controlResized event of the ControlListener is fired to
 * indicate that a TableColumn was resized. Moving columns and notifications
 * thereof are not yet implemented.</p> 
 */
public class TableColumn extends Item {

  public static final int HEIGHT = 20;
  
  private final Table parent;
  private int width;

  public TableColumn( final Table parent, final int style ) {
    this( parent, checkStyle( style ), checkNull( parent ).getColumnCount() );
  }

  public TableColumn( final Table parent, final int style, final int index ) {
    super( parent, checkStyle( style ) );
    this.parent = parent;
    this.parent.createColumn( this, index );
  }
  
  public Display getDisplay() {
    checkWidget();
    return parent.getDisplay();
  }
  
  public Table getParent() {
    checkWidget();
    return parent;
  }

  public int getWidth() {
    checkWidget();
    return width;
  }

  public void setWidth( final int width ) {
    checkWidget();
    if( width >= 0 ) {
      this.width = width;
    } else if( width == -2 ) {
      // Compute width from current column text
      Font font = parent.getFont();
      this.width = FontSizeEstimation.stringExtent( getText(), font ).x;
      // Mimic Windows behaviour that has a minimal width 
      if( this.width < 12 ) {
        this.width = 12;
      }
    } else {
      // TODO [rh] revise this: seems to be some minimal column width, maybe
      //      depends on whether column is resizeable or movable or style flags 
      this.width = 6; 
    }
    ControlEvent event = new ControlEvent( this, ControlEvent.CONTROL_RESIZED );
    event.processEvent();
  }
  
  ///////////////////////////////////////
  // Listener registration/deregistration
  
  
  public void addControlListener( final ControlListener listener ) {
    ControlEvent.addListener( this, listener );
  }

  public void removeControlListener( final ControlListener listener ) {
    ControlEvent.removeListener( this, listener );
  }
  
  public void addSelectionListener( final SelectionListener listener ) {
    SelectionEvent.addListener( this, listener );
  }

  public void removeSelectionListener( final SelectionListener listener ) {
    SelectionEvent.removeListener( this, listener );
  }

  ///////////////////////////////////
  // Methods to dispose of the widget

  protected void releaseChildren() {
  }

  protected void releaseParent() {
    parent.destroyColumn( this );
  }

  protected void releaseWidget() {
  }
  
  //////////////////
  // Helping methods

  private static int checkStyle( final int style ) {
    return checkBits( style, SWT.LEFT, SWT.CENTER, SWT.RIGHT, 0, 0, 0 );
  }

  private static Table checkNull( final Table table ) {
    if( table == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    return table;
  }
}
