/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.widgets;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.events.SelectionListener;


public class List extends Scrollable {

  private final ListModel model;

  public List( final Composite parent, final int style ) {
    super( parent, checkStyle( style ) );
    model = new ListModel( ( ( style & RWT.SINGLE ) != 0 ) );
  }
  
  ///////////////////////////////
  // Methods to get/set selection

  public int getSelectionIndex() {
    return model.getSelectionIndex();
  }

  public int[] getSelectionIndices() {
    return model.getSelectionIndices();
  }
  
  public int getSelectionCount() {
    return model.getSelectionCount();
  }

  public void setSelection( final int selection ) {
    model.setSelection( selection );
  }

  public void setSelection( final int[] selection ) {
    model.setSelection( selection );
  }
  
  public void setSelection( final int start, final int end ) {
    model.setSelection( start, end );
  }
  
  public void setSelection( final String[] selection ) {
    model.setSelection( selection );
  }

  public void deselectAll() {
    model.deselectAll();
  }
  
  ////////////////////////////////
  // Methods to maintain the items
  
  public void add( final String string ) {
    model.add( string );
  }

  public void add( final String string, final int index ) {
    model.add( string, index );
  }

  public void remove( final int index ) {
    model.remove( index );
  }

  public void remove( final int start, final int end ) {
    model.remove( start, end );
  }
  
  public void remove( final int[] indices ) {
    model.remove( indices );
  }

  public void remove( final String string ) {
    model.remove( string );
  }

  public void removeAll() {
    model.removeAll();
  }

  public void setItem( final int index, final String string ) {
    model.setItem( index, string );
  }

  public void setItems( final String[] items ) {
    model.setItems( items );
  }

  public String getItem( final int index ) {
    return model.getItem( index );
  }

  public int getItemCount() {
    return model.getItemCount();
  }

  public String[] getItems() {
    return model.getItems();
  }

  /////////////////////////////////////////
  // Listener registration/de-registration

  public void addSelectionListener( final SelectionListener listener ) {
    SelectionEvent.addListener( this, listener );
  }

  public void removeSelectionListener( final SelectionListener listener ) {
    SelectionEvent.removeListener( this, listener );
  }

  //////////////////
  // Helping methods 
  
  private static int checkStyle( final int style ) {
    return checkBits( style, RWT.SINGLE, RWT.MULTI, 0, 0, 0, 0 );
  }
}
