/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.coolitemkit;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.internal.lifecycle.IRenderRunnable;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.*;
import org.eclipse.swt.internal.widgets.coolbarkit.CoolBarLCA;
import org.eclipse.swt.widgets.*;


public class CoolItemLCA extends AbstractWidgetLCA {

  private static final String SET_CONTROL = "setControl";
  static final String PROP_CONTROL = "control";
  
  /* (intentionally not JavaDoc'ed)
   * Unnecesary to call ItemLCAUtil.preserve, CoolItem does neither use text
   * nor image
   */
  public void preserveValues( final Widget widget ) {
    CoolItem coolItem = ( CoolItem )widget;
    IWidgetAdapter adapter = WidgetUtil.getAdapter( coolItem );
    adapter.preserve( PROP_CONTROL, coolItem.getControl() );
    adapter.preserve( Props.BOUNDS, coolItem.getBounds() );
    WidgetLCAUtil.preserveCustomVariant( coolItem );
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
    writer.set( "minWidth", 0 );
    writer.set( "minHeight", 0 );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    CoolItem coolItem = ( CoolItem )widget;
    writeBounds( coolItem );
    writeControl( coolItem );
    writeLocked( coolItem );
    WidgetLCAUtil.writeCustomVariant( coolItem );
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
    String prop = CoolBarLCA.PROP_LOCKED;
    Boolean oldValue = Boolean.valueOf( parent.getLocked() );
    Boolean defValue = Boolean.FALSE;
    if( WidgetLCAUtil.hasChanged( parent, prop, oldValue, defValue ) )
    {
      writer.set( "locked", parent.getLocked() );
    }
  }

  private static void writeControl( final CoolItem coolItem ) throws IOException {
    Control control = coolItem.getControl();
    if( WidgetLCAUtil.hasChanged( coolItem, PROP_CONTROL, control, null ) ) {
      final JSWriter writer = JSWriter.getWriterFor( coolItem );
      final Object[] args = new Object[] { control };
      if( control != null ) {
        // defer call since controls are rendered after items
        WidgetAdapter adapter 
          = ( WidgetAdapter )WidgetUtil.getAdapter( control );
        adapter.setRenderRunnable( new IRenderRunnable() {
          public void afterRender() throws IOException {
            writer.call( SET_CONTROL, args );
          }
        } );
      } else {
        writer.call( SET_CONTROL, args );
      }
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
        if( coolItem.getBounds().x > newX ) {
          newOrder = i + 1;
        } else {
          newOrder = i;
        }
        changed = changeOrder( coolItem, newOrder );
      }
      maxX = Math.max( maxX, itemBounds.x + itemBounds.width );
      minX = Math.min( minX, itemBounds.x );
    }
    if( newOrder == -1 && newX > maxX ) {
      // item was moved after the last item
      int last = coolItem.getParent().getItemCount() - 1;
      changed = changeOrder( coolItem, last );
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
    int length = itemOrder.length;
    int[] targetOrder = new int[ length ];
    int index = 0;
    if ( itemIndex != newOrder ) {
      for( int i = 0; i < length; i++ ) {
        if( i == newOrder ) {
          targetOrder[ i ] = itemOrder[ itemIndex ];
        } else {
          if( index == itemIndex ) {
            index++;
          }
          targetOrder[ i ] = itemOrder[ index ];
          index++;
        }
      }
      Object adapter = coolBar.getAdapter( ICoolBarAdapter.class );
      ICoolBarAdapter cba = (ICoolBarAdapter) adapter;
      cba.setItemOrder( targetOrder );
      result = true;
    } else {
      result = false;
    }
    return result;
  }
}
