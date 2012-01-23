/*******************************************************************************
 * Copyright (c) 2008, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.expanditemkit;

import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.renderProperty;

import java.io.IOException;

import org.eclipse.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rwt.internal.protocol.IClientObject;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.events.ExpandEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.IExpandBarAdapter;
import org.eclipse.swt.internal.widgets.ItemLCAUtil;
import org.eclipse.swt.widgets.*;


public final class ExpandItemLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.ExpandItem";

  // Request parameters that denote ExpandEvents
  public static final String EVENT_ITEM_EXPANDED = "org.eclipse.swt.events.expandItemExpanded";
  public static final String EVENT_ITEM_COLLAPSED = "org.eclipse.swt.events.expandItemCollapsed";

  public static final String PROP_EXPANDED = "expanded";
  public static final String PROP_HEADER_HEIGHT = "headerHeight";

  public static final int DEFAULT_HEADER_HEIGHT = 24;

  public void preserveValues( Widget widget ) {
    ExpandItem item = ( ExpandItem )widget;
    WidgetLCAUtil.preserveCustomVariant( item );
    WidgetLCAUtil.preserveBounds( item, getBounds( item ) );
    ItemLCAUtil.preserve( item );
    preserveProperty( item, PROP_EXPANDED, item.getExpanded() );
    preserveProperty( item, PROP_HEADER_HEIGHT, item.getHeaderHeight() );
  }

  public void readData( Widget widget ) {
    final ExpandItem item = ( ExpandItem )widget;
    if( WidgetLCAUtil.wasEventSent( item, EVENT_ITEM_EXPANDED ) ) {
      ProcessActionRunner.add( new Runnable() {
        public void run() {
          item.setExpanded( true );
          createEvent( item, ExpandEvent.EXPAND ).processEvent();
        }
      } );
    }
    if( WidgetLCAUtil.wasEventSent( item, EVENT_ITEM_COLLAPSED ) ) {
      ProcessActionRunner.add( new Runnable() {
        public void run() {
          item.setExpanded( false );
          createEvent( item, ExpandEvent.COLLAPSE ).processEvent();
        }
      } );
    }
  }

  public void renderInitialization( Widget widget ) throws IOException {
    ExpandItem item = ( ExpandItem )widget;
    IClientObject clientObject = ClientObjectFactory.getForWidget( item );
    clientObject.create( TYPE );
    clientObject.setProperty( "parent", WidgetUtil.getId( item.getParent() ) );
  }

  public void renderChanges( Widget widget ) throws IOException {
    ExpandItem item = ( ExpandItem )widget;
    WidgetLCAUtil.renderCustomVariant( widget );
    WidgetLCAUtil.renderBounds( item, getBounds( item ) );
    ItemLCAUtil.renderChanges( item );
    renderProperty( item, PROP_EXPANDED, item.getExpanded(), false );
    renderProperty( item, PROP_HEADER_HEIGHT, item.getHeaderHeight(), DEFAULT_HEADER_HEIGHT );
  }

  public void renderDispose( Widget widget ) throws IOException {
    ClientObjectFactory.getForWidget( widget ).destroy();
  }

  ////////////////
  // Event helper

  private static ExpandEvent createEvent( ExpandItem item, int id ) {
    return new ExpandEvent( item.getParent(), item, id );
  }

  //////////////////
  // Helping methods

  private static Rectangle getBounds( ExpandItem item ) {
    return getExpandBarAdapter( item ).getBounds( item );
  }

  private static IExpandBarAdapter getExpandBarAdapter( ExpandItem item ) {
    return item.getParent().getAdapter( IExpandBarAdapter.class );
  }
}
