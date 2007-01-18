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
import org.eclipse.rap.rwt.internal.widgets.IListAdapter;

// TODO [rh] H_SCROLL not yet implemented
public class List extends Scrollable {

  private final ListModel model;
  private int focusIndex = -1;
  private IListAdapter listAdapter;

  public List( final Composite parent, final int style ) {
    super( parent, checkStyle( style ) );
    model = new ListModel( ( style & RWT.SINGLE ) != 0 );
  }
  
  /////////////////////
  // Adaptable override
  
  public Object getAdapter( final Class adapter ) {
    Object result;
    if( adapter == IListAdapter.class ) {
      if( listAdapter == null ) {
        listAdapter = new IListAdapter() {
          public void setFocusIndex( final int focusIndex ) {
            List.this.setFocusIndex( focusIndex );
          }
        };
      }
      result = listAdapter;
    } else {
      result = super.getAdapter( adapter );
    }
    return result;
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

  // TODO [rh] selection is not scrolled into view (see List.js)
  public void setSelection( final int selection ) {
    model.setSelection( selection );
    updateFocusIndexAfterSelectionChange();
  }

  public void setSelection( final int[] selection ) {
    model.setSelection( selection );
    updateFocusIndexAfterSelectionChange();
  }
  
  public void setSelection( final int start, final int end ) {
    model.setSelection( start, end );
    updateFocusIndexAfterSelectionChange();
  }
  
  public void setSelection( final String[] selection ) {
    model.setSelection( selection );
    updateFocusIndexAfterSelectionChange();
  }
  
  public void selectAll() {
    model.selectAll();
    updateFocusIndexAfterSelectionChange();
  }

  public void deselectAll() {
    model.deselectAll();
    updateFocusIndexAfterSelectionChange();
  }
  
  public int getFocusIndex() {
    return focusIndex;
  }

  ////////////////////////////////
  // Methods to maintain the items
  
  public void add( final String string ) {
    model.add( string );
    updateFocusIndexAfterItemChange();
  }

  public void add( final String string, final int index ) {
    model.add( string, index );
    updateFocusIndexAfterItemChange();
  }

  public void remove( final int index ) {
    model.remove( index );
    updateFocusIndexAfterItemChange();
  }

  public void remove( final int start, final int end ) {
    model.remove( start, end );
    updateFocusIndexAfterItemChange();
  }
  
  public void remove( final int[] indices ) {
    model.remove( indices );
    updateFocusIndexAfterItemChange();
  }

  public void remove( final String string ) {
    model.remove( string );
    updateFocusIndexAfterItemChange();
  }

  public void removeAll() {
    model.removeAll();
    updateFocusIndexAfterItemChange();
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

  /////////////////////////////////
  // Helping methods for focusIndex 
  
  private void setFocusIndex( final int focusIndex ) {
    int count = model.getItemCount();
    if( focusIndex == -1 || ( focusIndex >= 0 && focusIndex < count ) ) {
      this.focusIndex = focusIndex;
    }
  }
  
  private void updateFocusIndexAfterSelectionChange() {
    if( model.getSelectionIndex() == -1 ) {
      focusIndex = 0;
    } else {
      focusIndex = model.getSelectionIndices()[ 0 ];
    }
  }

  private void updateFocusIndexAfterItemChange() {
    if( model.getItemCount() == 0 ) {
      focusIndex = -1;
    } else if( model.getSelectionIndex() == -1 ){
      focusIndex = model.getItemCount() - 1;
    }
  }
  
  //////////////////
  // Helping methods 
  
  private static int checkStyle( final int style ) {
    return checkBits( style, RWT.SINGLE, RWT.MULTI, 0, 0, 0, 0 );
  }
}
