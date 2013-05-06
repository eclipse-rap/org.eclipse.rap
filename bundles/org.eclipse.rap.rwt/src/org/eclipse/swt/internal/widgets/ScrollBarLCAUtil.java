/*******************************************************************************
 * Copyright (c) 2012, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets;

import static org.eclipse.rap.rwt.internal.protocol.JsonUtil.createJsonArray;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.getStyles;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveListener;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.renderListener;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.renderProperty;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.wasEventSent;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.eclipse.swt.internal.events.EventLCAUtil.isListening;

import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rap.rwt.internal.protocol.IClientObject;
import org.eclipse.rap.rwt.lifecycle.WidgetAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Scrollable;


public class ScrollBarLCAUtil {

  private static final String TYPE = "rwt.widgets.ScrollBar";
  private static final String[] ALLOWED_STYLES = new String[] {
    "HORIZONTAL", "VERTICAL"
  };

  private static final String PROP_VISIBILITY = "visibility";
  private static final String PROP_SELECTION_LISTENER = "Selection";

  private ScrollBarLCAUtil() {
    // prevent instantiation
  }

  public static void preserveValues( Scrollable scrollable ) {
    preserveValues( scrollable.getHorizontalBar() );
    preserveValues( scrollable.getVerticalBar() );
  }

  private static void preserveValues( ScrollBar scrollBar ) {
    if( scrollBar != null ) {
      preserveProperty( scrollBar, PROP_VISIBILITY, scrollBar.getVisible() );
      preserveListener( scrollBar,
                        PROP_SELECTION_LISTENER,
                        isListening( scrollBar, SWT.Selection ) );
    }
  }

  public static void renderInitialization( Scrollable scrollable ) {
    renderCreate( scrollable.getHorizontalBar() );
    renderCreate( scrollable.getVerticalBar() );
  }

  private static void renderCreate( ScrollBar scrollBar ) {
    if( scrollBar != null ) {
      IClientObject clientObject = ClientObjectFactory.getClientObject( scrollBar );
      clientObject.create( TYPE );
      clientObject.set( "parent", getId( scrollBar.getParent() ) );
      clientObject.set( "style", createJsonArray( getStyles( scrollBar, ALLOWED_STYLES ) ) );
    }
  }

  public static void renderChanges( Scrollable scrollable ) {
    renderChanges( scrollable.getHorizontalBar() );
    renderChanges( scrollable.getVerticalBar() );
    markInitialized( scrollable );
  }

  private static void renderChanges( ScrollBar scrollBar ) {
    if( scrollBar != null ) {
      renderProperty( scrollBar, PROP_VISIBILITY, scrollBar.getVisible(), false );
      renderListener( scrollBar,
                      PROP_SELECTION_LISTENER,
                      isListening( scrollBar, SWT.Selection ),
                      false );
    }
  }

  static void markInitialized( Scrollable scrollable ) {
    ScrollBar hScroll = scrollable.getHorizontalBar();
    if( hScroll != null ) {
      getAdapter( hScroll ).setInitialized( true );
    }
    ScrollBar vScroll = scrollable.getVerticalBar();
    if( vScroll != null ) {
      getAdapter( vScroll ).setInitialized( true );
    }
  }

  //////////////////
  // Selection event

  public static void processSelectionEvent( Scrollable scrollable ) {
    processSelectionEvent( scrollable.getHorizontalBar() );
    processSelectionEvent( scrollable.getVerticalBar() );
  }

  private static void processSelectionEvent( ScrollBar scrollBar ) {
    if( scrollBar != null && wasEventSent( scrollBar, ClientMessageConst.EVENT_SELECTION ) ) {
      scrollBar.notifyListeners( SWT.Selection, new Event() );
    }
  }

  //////////////////
  // Helping methods

  private static WidgetAdapterImpl getAdapter( ScrollBar scrollBar ) {
    return ( WidgetAdapterImpl )scrollBar.getAdapter( WidgetAdapter.class );
  }

}
