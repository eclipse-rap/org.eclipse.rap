/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.internal.widgets.IListAdapter;

/**
 * <p>Not yet implemented:</p>
 * <ul><li>topIndex</li>
 * <li>itemHeight (may not be implemented at all)</li>
 * <li>showSelection</li>
 * <li>all select and deselect methods</li>
 * </ul>
 * <p><strong>Note:</strong> Setting only one of <code>H_SCROLL</code> or 
 * <code>V_SCROLL</code> leads - at least in IE 7 - to unexpected behavior 
 * (items are drawn outside list bounds). Setting none or both scroll style 
 * flags works as expected. We will work on a solution for this.</p>
 */
public class List extends Scrollable {

  private final ListModel model;
  private int focusIndex = -1;
  private IListAdapter listAdapter;

  public List( final Composite parent, final int style ) {
    super( parent, checkStyle( style ) );
    model = new ListModel( ( style & SWT.SINGLE ) != 0 );
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

  public String [] getSelection() {
    checkWidget();
    int[] selectionIndices = model.getSelectionIndices();
    String[] result = new String[ selectionIndices.length ];
    for( int i = 0; i < result.length; i++ ) {
      result[ i ] = model.getItem( selectionIndices[ i ] );
    }
    return result;
  }
  
  public int getSelectionIndex() {
    checkWidget();
    return model.getSelectionIndex();
  }

  public int[] getSelectionIndices() {
    checkWidget();
    return model.getSelectionIndices();
  }
  
  public int getSelectionCount() {
    checkWidget();
    return model.getSelectionCount();
  }

  // TODO [rh] selection is not scrolled into view (see List.js)
  public void setSelection( final int selection ) {
    checkWidget();
    model.setSelection( selection );
    updateFocusIndexAfterSelectionChange();
  }

  public void setSelection( final int[] selection ) {
    checkWidget();
    model.setSelection( selection );
    updateFocusIndexAfterSelectionChange();
  }
  
  public void setSelection( final int start, final int end ) {
    checkWidget();
    model.setSelection( start, end );
    updateFocusIndexAfterSelectionChange();
  }
  
  public void setSelection( final String[] selection ) {
    checkWidget();
    model.setSelection( selection );
    updateFocusIndexAfterSelectionChange();
  }
  
  public void selectAll() {
    checkWidget();
    model.selectAll();
    updateFocusIndexAfterSelectionChange();
  }

  public void deselectAll() {
    checkWidget();
    model.deselectAll();
    updateFocusIndexAfterSelectionChange();
  }
  
  public boolean isSelected( final int index ) {
    checkWidget();
    boolean result;
    if( ( style & SWT.SINGLE ) != 0 ) {
      result = index == getSelectionIndex();
    } else {
      int[] selectionIndices = getSelectionIndices();
      result = false;
      for( int i = 0; !result && i < selectionIndices.length; i++ ) {
        if( index == selectionIndices[ i ] ) {
          result = true;
        }
      }
    }
    return result;
  }

  public int getFocusIndex() {
    checkWidget();
    return focusIndex;
  }

  ////////////////////////////////
  // Methods to maintain the items
  
  public void add( final String string ) {
    checkWidget();
    model.add( string );
    updateFocusIndexAfterItemChange();
  }

  public void add( final String string, final int index ) {
    checkWidget();
    model.add( string, index );
    updateFocusIndexAfterItemChange();
  }

  public void remove( final int index ) {
    checkWidget();
    model.remove( index );
    updateFocusIndexAfterItemChange();
  }

  public void remove( final int start, final int end ) {
    checkWidget();
    model.remove( start, end );
    updateFocusIndexAfterItemChange();
  }
  
  public void remove( final int[] indices ) {
    checkWidget();
    model.remove( indices );
    updateFocusIndexAfterItemChange();
  }

  public void remove( final String string ) {
    checkWidget();
    model.remove( string );
    updateFocusIndexAfterItemChange();
  }

  public void removeAll() {
    checkWidget();
    model.removeAll();
    updateFocusIndexAfterItemChange();
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

  public int getItemCount() {
    checkWidget();
    return model.getItemCount();
  }

  public String[] getItems() {
    checkWidget();
    return model.getItems();
  }
  
  public int indexOf( final String string ) {
    checkWidget();
    return indexOf( string, 0 );
  }

  public int indexOf( final String string, final int start ) {
    checkWidget();
    if( string == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    int result = -1;
    int count = getItemCount();
    for( int i = start; result == -1 && i < count; i++ ) {
      if( string.equals( getItem( i ) ) ) {
        result = i;
      }
    }
    return result;
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
  
  boolean isTabGroup() {
    return true;
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
    return checkBits( style, SWT.SINGLE, SWT.MULTI, 0, 0, 0, 0 );
  }
}
