/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.coolitemkit;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.*;
import org.eclipse.swt.widgets.*;



public class CoolItemLCA extends AbstractWidgetLCA {

  /* (intentionally not JavaDoc'ed)
   * Unnecesary to call ItemLCAUtil.preserve, CoolItem does neither use text
   * nor image
   */
  public void preserveValues( final Widget widget ) {
    CoolItem coolItem = ( CoolItem )widget;
    IWidgetAdapter adapter = WidgetUtil.getAdapter( coolItem );
    adapter.preserve( Props.CONTROL, coolItem.getControl() );
    adapter.preserve( Props.BOUNDS, coolItem.getBounds() );
  }

  public void readData( final Widget widget ) {
    // TODO [rh] clean up this mess
    final CoolItem coolItem = ( CoolItem )widget;
    HttpServletRequest request = ContextProvider.getRequest();
    String movedWidgetId = request.getParameter( JSConst.EVENT_WIDGET_MOVED );
    if( WidgetUtil.getId( coolItem ).equals( movedWidgetId ) ) {
      String value = WidgetLCAUtil.readPropertyValue( coolItem, "bounds.x" );
      final int x = Integer.parseInt( value );
      ProcessActionRunner.add( new Runnable() {
        public void run() {
          moveItem( coolItem, x );
        }
      } );
    }
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    CoolItem coolItem = ( CoolItem )widget;
    JSWriter writer = JSWriter.getWriterFor( widget );
    Object[] args = new Object[] { jsOrientation( coolItem ) };
    writer.newWidget( "org.eclipse.swt.widgets.CoolItem", args );
    writer.setParent( WidgetUtil.getId( coolItem.getParent() ) );
    WidgetLCAUtil.writeCustomVariant( widget );
    writer.set( "minWidth", 0 );
    writer.set( "minHeight", 0 );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    CoolItem coolItem = ( CoolItem )widget;
    writeBounds( coolItem );
    setJSParent( coolItem );
    // TODO [rh] find a decent solution to place the ctrl contained in CoolItem
    Control control = coolItem.getControl();
    if( control != null ) {
      Point location = control.getLocation();
      location.x = 6; // TODO [rst] Use CoolItem.HANDLE_SIZE + margin;
      location.y = 0;
      control.setLocation( location );
    }
    writeLocked( coolItem );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }

  public void createResetHandlerCalls( final String typePoolId ) throws IOException {
  }

  public String getTypePoolId( final Widget widget ) {
    return null;
  }


  //////////////////
  // Helping methods

  private static void writeBounds( final CoolItem coolItem ) throws IOException
  {
    Rectangle bounds = coolItem.getBounds();
    WidgetLCAUtil.writeBounds( coolItem, coolItem.getParent(), bounds );
    if( WidgetLCAUtil.hasChanged( coolItem, Props.BOUNDS, bounds, null ) ) {
      JSWriter writer = JSWriter.getWriterFor( coolItem );
      writer.call( "updateHandleBounds", null );
    }
  }

  private static void writeLocked( final CoolItem coolItem ) throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( coolItem );
    CoolBar parent = coolItem.getParent();
    Boolean oldValue = Boolean.valueOf( parent.getLocked() );
    Boolean defValue = Boolean.FALSE;
    if( WidgetLCAUtil.hasChanged( parent, Props.LOCKED, oldValue, defValue ) )
    {
      writer.set( "locked", parent.getLocked() );
    }
  }

  private static void setJSParent( final CoolItem coolItem ) {
    Control control = coolItem.getControl();
    if( control != null ) {
      WidgetAdapter controlAdapter
        = ( WidgetAdapter )WidgetUtil.getAdapter( control );
      controlAdapter.setJSParent( WidgetUtil.getId( coolItem ) );
    }
  }

  private static JSVar jsOrientation( final CoolItem coolItem ) {
    JSVar orientation;
    if( ( coolItem.getStyle() & SWT.VERTICAL ) != 0 ) {
      orientation = JSConst.QX_CONST_VERTICAL_ORIENTATION;
    } else {
      orientation = JSConst.QX_CONST_HORIZONTAL_ORIENTATION;
    }
    return orientation;
  }

  ///////////////////////////////
  // Methods for item re-ordering

  private static void moveItem( final CoolItem coolItem, final int newX ) {
    CoolItem[] items = coolItem.getParent().getItems();
    boolean changed = false;
    int newOrder = -1;
    int maxX = 0;
    int minX = 0;
    for( int i = 0; newOrder == -1 && i < items.length; i++ ) {
      CoolItem item = items[ i ];
      Rectangle itemBounds = item.getBounds();
      if( item != coolItem && itemBounds.contains( newX, itemBounds.y ) ) {
        int[] itemOrder = coolItem.getParent().getItemOrder();
        newOrder = Math.min( itemOrder[ i ] + 1, itemOrder.length - 1 );
        changed = changeOrder( coolItem, newOrder );
      }
      maxX = Math.max( maxX, itemBounds.x + itemBounds.width );
      minX = Math.min( minX, itemBounds.x );
    }
    if( newOrder == -1 && newX > maxX ) {
      // item was moved after the last item
      changed = changeOrder( coolItem, coolItem.getParent().getItemCount() - 1 );
    } else if( newOrder == -1 && newX < minX ) {
      // item was moved before the first item
      changed = changeOrder( coolItem, 0 );
    }
    // In case an item was moved but that didn't cause it to change its order,
    // we need to let it 'snap back' to its previous position
    if( !changed ) {
      // TODO [rh] HACK: a decent solution would mark the item as 'bounds
      //      changed' and that mark could be evaluated by writeBounds.
      //      A more flexible writeBounds implementation on WidgetLCAUtil is
      //      necessary therefore.
      IWidgetAdapter adapter = WidgetUtil.getAdapter( coolItem );
      adapter.preserve( Props.BOUNDS, null );
    }
  }

  private static boolean changeOrder( final CoolItem coolItem,
                                      final int newOrder )
  {
    boolean result;
    CoolBar coolBar = coolItem.getParent();
    int itemIndex = coolBar.indexOf( coolItem );
    int[] itemOrder = coolBar.getItemOrder();
    int oldOrder = itemOrder[ itemIndex ];
    if( oldOrder != newOrder ) {
      if( newOrder < oldOrder ) {
        for( int i = 0; i < itemOrder.length; i++ ) {
          CoolItem item = coolBar.getItem( i );
          if(    item != coolItem
              && itemOrder[ i ] >= newOrder
              && itemOrder[ i ] < itemOrder[ itemIndex ] )
          {
            itemOrder[ i ] += 1;
          }
        }
      } else {
        for( int i = 0; i < itemOrder.length; i++ ) {
          CoolItem item = coolBar.getItem( i );
          if(     item != coolItem
              && itemOrder[ i ] <= newOrder
              && itemOrder[ i ] > itemOrder[ itemIndex ] )
          {
            itemOrder[ i ] -= 1;
          }
        }
      }
      result = itemOrder[ itemIndex ] != newOrder;
      if( result ) {
        itemOrder[ itemIndex ] = newOrder;

        Object adapter = coolBar.getAdapter( ICoolBarAdapter.class );
        ICoolBarAdapter cba = (ICoolBarAdapter) adapter;

        cba.setItemOrder( itemOrder );
      }
    } else {
      result = false;
    }
    return result;
  }
}
