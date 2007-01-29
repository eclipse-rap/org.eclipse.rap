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

package org.eclipse.rap.rwt.internal.widgets.coolitemkit;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.graphics.Point;
import org.eclipse.rap.rwt.graphics.Rectangle;
import org.eclipse.rap.rwt.internal.widgets.*;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.*;
import com.w4t.engine.service.ContextProvider;


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
    CoolItem coolItem = ( CoolItem )widget;
    HttpServletRequest request = ContextProvider.getRequest();
    String movedWidgetId = request.getParameter( JSConst.EVENT_WIDGET_MOVED );
    if( WidgetUtil.getId( coolItem ).equals( movedWidgetId ) ) {
      String value = WidgetLCAUtil.readPropertyValue( coolItem, "bounds.x" );
      int x = Integer.parseInt( value );
      CoolItem[] items = coolItem.getParent().getItems();
      int newOrder = -1;
      int maxX = 0;
      int minX = 0;
      for( int i = 0; newOrder == -1 && i < items.length; i++ ) {
        CoolItem item = items[ i ];
        Rectangle itemBounds = item.getBounds();
        if( item != coolItem && itemBounds.contains( x, itemBounds.y ) ) {
          int[] itemOrder = coolItem.getParent().getItemOrder();
          newOrder = itemOrder[ i ] + 1;
          changeOrder( coolItem, newOrder );
        }
        if( itemBounds.x + itemBounds.width > maxX ) {
          maxX = itemBounds.x + itemBounds.width;
        }
        if( itemBounds.x < minX ) {
          minX = itemBounds.x;
        }
      }
      if( newOrder == -1 && x > maxX ) {
        changeOrder( coolItem, coolItem.getParent().getItemCount() - 1 );
      } else if( newOrder == -1 && x < minX ) {
        changeOrder( coolItem, 0 );
      }
    }
  }
  
  public void renderInitialization( final Widget widget ) throws IOException {
    CoolItem coolItem = ( CoolItem )widget;
    JSWriter writer = JSWriter.getWriterFor( widget );
    Object[] args = new Object[] { jsOrientation( coolItem ) };
    writer.newWidget( "org.eclipse.rap.rwt.widgets.CoolItem", args );
    writer.setParent( WidgetUtil.getId( coolItem.getParent() ) );
    writer.set( "minWidth", 0 );
    writer.set( "minHeight", 0 );
    // TODO [rh] could omit this if ensured that client-side handleSize is in
    //      sync with CoolItem.HANDLE_SIZE
    writer.set( "handleSize", CoolItem.HANDLE_SIZE );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    CoolItem coolItem = ( CoolItem )widget;
    writeBounds( coolItem );
    setJSParent( coolItem );
    // TODO [rh] find a decent solution to place the ctrl contained in CoolItem 
    if( coolItem.getControl() != null ) {
      Point location = coolItem.getControl().getLocation();
      location.x = CoolItem.HANDLE_SIZE;
      coolItem.getControl().setLocation( location );
    }
    writeLocked( coolItem );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }

  //////////////////
  // Helping methods
  
  private static void writeBounds( final CoolItem coolItem ) throws IOException 
  {
    Rectangle bounds = coolItem.getBounds();
    if( WidgetLCAUtil.hasChanged( coolItem, Props.BOUNDS, bounds, null ) ) { 
      int[] args = new int[] {
        bounds.x, bounds.width, bounds.y, bounds.height
      };
      JSWriter writer = JSWriter.getWriterFor( coolItem );
      writer.set( "space", args );
      writer.call( "updateHandleBounds", null );
    }
  }

  private static void writeLocked( final CoolItem coolItem ) throws IOException 
  {
    JSWriter writer = JSWriter.getWriterFor( coolItem );
    Boolean oldValue = Boolean.valueOf( coolItem.getParent().getLocked() );
    if( WidgetLCAUtil.hasChanged( coolItem.getParent(), Props.LOCKED, oldValue ) ) 
    {
      writer.set( "locked", coolItem.getParent().getLocked() );
    }
  }

  private static void setJSParent( final CoolItem coolItem ) {
    Control control = coolItem.getControl();
    if( control != null ) {
      IWidgetAdapter controlAdapter = WidgetUtil.getAdapter( control );
      controlAdapter.setJSParent( WidgetUtil.getId( coolItem ) );
    }
  }

  private static JSVar jsOrientation( final CoolItem coolItem ) {
    JSVar orientation;
    if( ( coolItem.getStyle() & RWT.VERTICAL ) != 0 ) {
      orientation = JSConst.QX_CONST_VERTICAL_ORIENTATION;
    } else {
      orientation = JSConst.QX_CONST_HORIZONTAL_ORIENTATION;
    }
    return orientation;
  }

  private static void changeOrder( final CoolItem coolItem, final int newOrder ) 
  {
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
              && itemOrder[i] > itemOrder[ itemIndex ] ) 
          {
            itemOrder[ i ] -= 1;
          }
        }
      }
      itemOrder[ itemIndex ] = newOrder;
      coolBar.setItemOrder( itemOrder );
    }
  }
}
