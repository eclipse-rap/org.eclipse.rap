/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.custom.ctabitemkit;

import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.renderProperty;

import java.io.IOException;

import org.eclipse.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rwt.internal.protocol.IClientObject;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.custom.ICTabFolderAdapter;
import org.eclipse.swt.internal.widgets.IWidgetFontAdapter;
import org.eclipse.swt.widgets.Widget;


public final class CTabItemLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.CTabItem";
  private static final String[] ALLOWED_STYLES = new String[] { "CLOSE" };

  public static final String EVENT_ITEM_CLOSED = "org.eclipse.swt.events.ctabItemClosed";

  private static final String PROP_TEXT = "text";
  private static final String PROP_IMAGE = "image";
  private static final String PROP_SHOWING = "showing";
  private static final String PROP_SHOW_CLOSE = "showClose";

  public void preserveValues( Widget widget ) {
    CTabItem item = ( CTabItem )widget;
    WidgetLCAUtil.preserveCustomVariant( item );
    WidgetLCAUtil.preserveToolTipText( item, item.getToolTipText() );
    WidgetLCAUtil.preserveBounds( item, item.getBounds() );
    WidgetLCAUtil.preserveFont( item, getFont( item ) );
    preserveProperty( item, PROP_TEXT, getText( item ) );
    preserveProperty( item, PROP_IMAGE, getImage( item ) );
    preserveProperty( item, PROP_SHOWING, item.isShowing() );
    preserveProperty( item, PROP_SHOW_CLOSE, item.getShowClose() );
  }

  public void readData( Widget widget ) {
    final CTabItem item = ( CTabItem )widget;
    if( WidgetLCAUtil.wasEventSent( item, EVENT_ITEM_CLOSED ) ) {
      ProcessActionRunner.add( new Runnable() {
        public void run() {
          CTabFolderEvent event = createCloseEvent( item );
          event.processEvent();
          if( event.doit ) {
            item.dispose();
          }
        }
      } );
    }
  }

  public void renderInitialization( Widget widget ) throws IOException {
    CTabItem item = ( CTabItem )widget;
    CTabFolder parent = item.getParent();
    IClientObject clientObject = ClientObjectFactory.getForWidget( item );
    clientObject.create( TYPE );
    clientObject.set( "parent", WidgetUtil.getId( parent ) );
    clientObject.set( "index", parent.indexOf( item ) );
    clientObject.set( "style", WidgetLCAUtil.getStyles( item, ALLOWED_STYLES ) );
  }

  public void renderChanges( Widget widget ) throws IOException {
    CTabItem item = ( CTabItem )widget;
    WidgetLCAUtil.renderCustomVariant( item );
    WidgetLCAUtil.renderToolTip( item, item.getToolTipText() );
    WidgetLCAUtil.renderBounds( item, item.getBounds() );
    WidgetLCAUtil.renderFont( item, getFont( item ) );
    renderProperty( item, PROP_TEXT, getText( item ), "" );
    renderProperty( item, PROP_IMAGE, getImage( item ), null );
    renderProperty( item, PROP_SHOWING, item.isShowing(), true );
    renderProperty( item, PROP_SHOW_CLOSE, item.getShowClose(), false );
  }

  public void renderDispose( Widget widget ) throws IOException {
    ClientObjectFactory.getForWidget( widget ).destroy();
  }

  ////////////////////////////////////////////
  // Helping methods to obtain item properties

  private static String getText( CTabItem item ) {
    return getCTabFolderAdapter( item ).getShortenedItemText( item );
  }

  private static Image getImage( CTabItem item ) {
    return getCTabFolderAdapter( item ).showItemImage( item ) ? item.getImage() : null;
  }

  private static Font getFont( CTabItem item ) {
    return item.getAdapter( IWidgetFontAdapter.class ).getUserFont();
  }

  private static ICTabFolderAdapter getCTabFolderAdapter( CTabItem item ) {
    return item.getParent().getAdapter( ICTabFolderAdapter.class );
  }

  ///////////////
  // Event helper

  private static CTabFolderEvent createCloseEvent( CTabItem item ) {
    CTabFolderEvent result = new CTabFolderEvent( item.getParent(), CTabFolderEvent.CLOSE );
    result.item = item;
    result.doit = true;
    return result;
  }
}
