/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.widgets;

import java.util.*;
import java.util.List;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.events.SelectionListener;


public abstract class AbstractList extends Scrollable {

  final List items = new ArrayList();

  abstract void setNullSelection();

  public AbstractList( final Composite parent, final int style ) {
    super( parent, checkStyle( style ) );
  }

  static int checkStyle( final int style ) {
    int result = RWT.NONE;
    if( style > 0 ) {
      result = style;
    }
    return result;
  }

  public void add( final String string ) {
    checkString( string );
    items.add( string );
    setNullSelection();
  }

  public void add( final String string, int index ) {
    checkIndex( index );
    checkString( string );
    items.add( index, string );
    setNullSelection();
  }

  public String getItem( final int index ) {
    checkIndex( index );
    return ( String )items.get( index );
  }

  public int getItemCount() {
    return items.size();
  }

  public String[] getItems() {
    String[] result = new String[ items.size() ];
    items.toArray( result );
    return result;
  }

  public void remove( final int index ) {
    checkIndex( index );
    items.remove( index );
    setNullSelection();
  }

  public void remove( final int start, final int end ) {
    checkIndex( start );
    checkIndex( end );
    for( int i = start; i <= end; i++ ) {
      items.remove( start );
    }
    setNullSelection();
  }

  public void remove( final String string ) {
    checkString( string );
    boolean removed = false;
    Iterator it = items.iterator();
    while( it.hasNext() && !removed ) {
      String name = ( String )it.next();
      if( name.equals( string ) ) {
        it.remove();
        removed = true;
        setNullSelection();
      }
    }
    if( !removed ) {
      RWT.error( RWT.ERROR_INVALID_ARGUMENT );
    }
  }

  public void removeAll() {
    items.clear();
    setNullSelection();
  }

  public void setItem( int index, String string ) {
    checkIndex( index );
    checkString( string );
    items.remove( index );
    items.add( index, string );
    setNullSelection();
  }

  public void setItems( final String[] items ) {
    if( items == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    } else {
      List tempList = new ArrayList( items.length );
      for( int i = 0; i < items.length; i++ ) {
        if( items[ i ] == null )
          RWT.error( RWT.ERROR_INVALID_ARGUMENT );
        tempList.add( items[ i ] );
      }
      this.items.clear();
      this.items.addAll( tempList );
      setNullSelection();
    }
  }

  public void addSelectionListener( final SelectionListener listener ) {
    SelectionEvent.addListener( this, listener );
  }

  public void removeSelectionListener( final SelectionListener listener ) {
    SelectionEvent.removeListener( this, listener );
  }

  // helperMethods
  final void checkString( final String string ) {
    if( string == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
  }

  final void checkIndex( final int index ) {
    if( index >= items.size() || index < 0 ) {
      RWT.error( RWT.ERROR_INVALID_RANGE );
    }
  }
}
