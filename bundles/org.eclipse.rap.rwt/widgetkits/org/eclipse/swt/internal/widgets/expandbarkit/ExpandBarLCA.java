/*******************************************************************************
 * Copyright (c) 2008, 2013 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.expandbarkit;

import static org.eclipse.rap.rwt.internal.protocol.JsonUtil.createJsonArray;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.getStyles;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveListener;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.readEventPropertyValue;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.renderListener;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.renderProperty;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.wasEventSent;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.find;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;

import java.io.IOException;

import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rap.rwt.internal.protocol.IClientObject;
import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.lifecycle.ControlLCAUtil;
import org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.IExpandBarAdapter;
import org.eclipse.swt.internal.widgets.ScrollBarLCAUtil;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Widget;


public final class ExpandBarLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.ExpandBar";
  private static final String[] ALLOWED_STYLES = new String[] { "NO_RADIO_GROUP", "BORDER" };

  private static final String PROP_BOTTOM_SPACING_BOUNDS = "bottomSpacingBounds";
  private static final String PROP_VSCROLLBAR_MAX = "vScrollBarMax";
  private static final String PROP_EXPAND_LISTENER = "Expand";
  private static final String PROP_COLLAPSE_LISTENER = "Collapse";

  @Override
  public void preserveValues( Widget widget ) {
    ExpandBar expandBar = ( ExpandBar )widget;
    ControlLCAUtil.preserveValues( expandBar );
    WidgetLCAUtil.preserveCustomVariant( expandBar );
    preserveProperty( expandBar, PROP_BOTTOM_SPACING_BOUNDS, getBottomSpacingBounds( expandBar ) );
    preserveProperty( expandBar, PROP_VSCROLLBAR_MAX, getVScrollBarMax( expandBar ) );
    preserveListener( expandBar, PROP_EXPAND_LISTENER, hasExpandListener( expandBar ) );
    preserveListener( expandBar, PROP_COLLAPSE_LISTENER, hasCollapseListener( expandBar ) );
    ScrollBarLCAUtil.preserveValues( expandBar );
  }

  public void readData( Widget widget ) {
    ExpandBar expandBar = ( ExpandBar )widget;
    ControlLCAUtil.processKeyEvents( expandBar );
    ControlLCAUtil.processMenuDetect( expandBar );
    WidgetLCAUtil.processHelp( expandBar );
    processExpandEvent( expandBar, SWT.Expand, "Expand" );
    processExpandEvent( expandBar, SWT.Collapse, "Collapse" );
    ScrollBarLCAUtil.processSelectionEvent( expandBar );
  }

  @Override
  public void renderInitialization( Widget widget ) throws IOException {
    ExpandBar expandBar = ( ExpandBar )widget;
    IClientObject clientObject = ClientObjectFactory.getClientObject( expandBar );
    clientObject.create( TYPE );
    clientObject.set( "parent", getId( expandBar.getParent() ) );
    clientObject.set( "style", createJsonArray( getStyles( expandBar, ALLOWED_STYLES ) ) );
    ScrollBarLCAUtil.renderInitialization( expandBar );
  }

  @Override
  public void renderChanges( Widget widget ) throws IOException {
    ExpandBar expandBar = ( ExpandBar )widget;
    ControlLCAUtil.renderChanges( expandBar );
    WidgetLCAUtil.renderCustomVariant( expandBar );
    renderProperty( expandBar,
                    PROP_BOTTOM_SPACING_BOUNDS,
                    getBottomSpacingBounds( expandBar ),
                    null );
    renderProperty( expandBar, PROP_VSCROLLBAR_MAX, getVScrollBarMax( expandBar ), 0 );
    renderListener( expandBar, PROP_EXPAND_LISTENER, hasExpandListener( expandBar ), false );
    renderListener( expandBar, PROP_COLLAPSE_LISTENER, hasCollapseListener( expandBar ), false );
    ScrollBarLCAUtil.renderChanges( expandBar );
  }

  //////////////////
  // Helping methods

  private static Rectangle getBottomSpacingBounds( ExpandBar bar ) {
    return getExpandBarAdapter( bar ).getBottomSpacingBounds();
  }

  private static int getVScrollBarMax( ExpandBar bar ) {
    int result = 0;
    if( ( bar.getStyle() & SWT.V_SCROLL ) != 0 ) {
      IExpandBarAdapter expandBarAdapter = getExpandBarAdapter( bar );
      ExpandItem[] items = bar.getItems();
      for( int i = 0; i < items.length; i++ ) {
        result += expandBarAdapter.getBounds( items[ i ] ).height;
      }
      result += bar.getSpacing() * ( items.length + 1 );
    }
    return result;
  }

  private static boolean hasExpandListener( ExpandBar bar ) {
    // Always render listen for Expand and Collapse, currently required for item's control
    // visibility and bounds update.
    return true;
  }

  private static boolean hasCollapseListener( ExpandBar bar ) {
    // Always render listen for Expand and Collapse, currently required for item's control
    // visibility and bounds update.
    return true;
  }

  public static IExpandBarAdapter getExpandBarAdapter( ExpandBar bar ) {
    return bar.getAdapter( IExpandBarAdapter.class );
  }

  /////////////////////////////////
  // Process expand/collapse events

  private static void processExpandEvent( ExpandBar bar, int eventType, String eventName ) {
    if( wasEventSent( bar, eventName ) ) {
      String value = readEventPropertyValue( bar, eventName, ClientMessageConst.EVENT_PARAM_ITEM );
      Event event = new Event();
      event.item = getItem( bar, value );
      bar.notifyListeners( eventType, event );
    }
  }

  private static ExpandItem getItem( ExpandBar bar, String itemId ) {
    return ( ExpandItem )find( bar, itemId );
  }
}
