/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.coolitemkit;

import static org.eclipse.rap.rwt.internal.protocol.JsonUtil.createJsonArray;
import static org.eclipse.rap.rwt.internal.protocol.ProtocolUtil.readCallPropertyValueAsString;
import static org.eclipse.rap.rwt.internal.protocol.ProtocolUtil.wasCallReceived;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.getStyles;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.renderProperty;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getAdapter;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;

import java.io.IOException;

import org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rap.rwt.internal.protocol.IClientObject;
import org.eclipse.rap.rwt.internal.util.NumberFormatUtil;
import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.lifecycle.ProcessActionRunner;
import org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.ICoolBarAdapter;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Widget;


public class CoolItemLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.CoolItem";
  private static final String[] ALLOWED_STYLES = new String[] { "DROP_DOWN", "VERTICAL" };

  static final String PROP_CONTROL = "control";

  /* (intentionally not JavaDoc'ed)
   * Unnecesary to call ItemLCAUtil.preserve, CoolItem does neither use text
   * nor image
   */
  @Override
  public void preserveValues( Widget widget ) {
    CoolItem item = ( CoolItem )widget;
    preserveProperty( item, PROP_CONTROL, item.getControl() );
    preserveProperty( item, Props.BOUNDS, item.getBounds() );
    WidgetLCAUtil.preserveCustomVariant( item );
    WidgetLCAUtil.preserveData( item );
  }

  public void readData( Widget widget ) {
    final CoolItem item = ( CoolItem )widget;
    String methodName = "move";
    if( wasCallReceived( getId( item ), methodName ) ) {
      String left = readCallPropertyValueAsString( getId( item ), methodName, "left" );
      final int newLeft = NumberFormatUtil.parseInt( left );
      ProcessActionRunner.add( new Runnable() {
        public void run() {
          moveItem( item, newLeft );
        }
      } );
    }
  }

  @Override
  public void renderInitialization( Widget widget ) throws IOException {
    CoolItem item = ( CoolItem )widget;
    IClientObject clientObject = ClientObjectFactory.getClientObject( item );
    clientObject.create( TYPE );
    clientObject.set( "parent", getId( item.getParent() ) );
    clientObject.set( "style", createJsonArray( getStyles( item, ALLOWED_STYLES ) ) );
  }

  @Override
  public void renderChanges( Widget widget ) throws IOException {
    CoolItem item = ( CoolItem )widget;
    WidgetLCAUtil.renderBounds( item, item.getBounds() );
    renderProperty( item, PROP_CONTROL, item.getControl(), null );
    WidgetLCAUtil.renderCustomVariant( item );
    WidgetLCAUtil.renderData( item );
  }

  ///////////////////////////////
  // Methods for item re-ordering

  private static void moveItem( CoolItem coolItem, int newX ) {
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
      getAdapter( coolItem ).preserve( Props.BOUNDS, null );
    }
  }

  private static boolean changeOrder( CoolItem coolItem, int newOrder ) {
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
      coolBar.getAdapter( ICoolBarAdapter.class ).setItemOrder( targetOrder );
      result = true;
    } else {
      result = false;
    }
    return result;
  }

}
