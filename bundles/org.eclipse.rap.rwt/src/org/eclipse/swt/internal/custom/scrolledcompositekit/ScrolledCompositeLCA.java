/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.custom.scrolledcompositekit;

import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.preserveListener;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.renderListener;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.renderProperty;

import java.io.IOException;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rwt.internal.protocol.IClientObject;
import org.eclipse.rwt.internal.util.NumberFormatUtil;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.events.EventLCAUtil;
import org.eclipse.swt.widgets.*;


public final class ScrolledCompositeLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.ScrolledComposite";

  // Request parameter names
  private static final String PARAM_H_BAR_SELECTION = "horizontalBar.selection";
  private static final String PARAM_V_BAR_SELECTION = "verticalBar.selection";

  // Property names
  private static final String PROP_ORIGIN = "origin";
  private static final String PROP_CONTENT = "content";
  private static final String PROP_SHOW_FOCUSED_CONTROL = "showFocusedControl";
  // TODO: [if] Move scrollbars synchronization to the ScrollBarLCA once it exists
  private static final String PROP_SCROLLBARS_VISIBLE = "scrollBarsVisible";
  private static final String PROP_SCROLLBARS_SELECTION_LISTENER = "scrollBarsSelection";

  // Default values
  private static final Point DEFAULT_ORIGIN = new Point( 0, 0 );
  private static final boolean[] DEFAULT_SCROLLBARS_VISIBLE = new boolean[] { true, true };

  public void preserveValues( Widget widget ) {
    ScrolledComposite composite = ( ScrolledComposite )widget;
    ControlLCAUtil.preserveValues( composite );
    WidgetLCAUtil.preserveCustomVariant( composite );
    preserveProperty( composite, PROP_ORIGIN, composite.getOrigin() );
    preserveProperty( composite, PROP_CONTENT, composite.getContent() );
    preserveProperty( composite, PROP_SHOW_FOCUSED_CONTROL, composite.getShowFocusedControl() );
    // TODO: [if] Move scrollbars synchronization to the ScrollBarLCA once it exists
    preserveProperty( composite, PROP_SCROLLBARS_VISIBLE, getScrollBarsVisible( composite ) );
    preserveListener( composite,
                      PROP_SCROLLBARS_SELECTION_LISTENER,
                      hasScrollBarsSelectionListener( composite ) );
  }

  public void readData( Widget widget ) {
    ScrolledComposite composite = ( ScrolledComposite )widget;
    Point origin = composite.getOrigin();
    String value = WidgetLCAUtil.readPropertyValue( widget, PARAM_H_BAR_SELECTION );
    ScrollBar hScroll = composite.getHorizontalBar();
    if( value != null && hScroll != null ) {
      origin.x = NumberFormatUtil.parseInt( value );
      processSelection( hScroll );
    }
    value = WidgetLCAUtil.readPropertyValue( widget, PARAM_V_BAR_SELECTION );
    ScrollBar vScroll = composite.getVerticalBar();
    if( value != null && vScroll != null ) {
      origin.y = NumberFormatUtil.parseInt( value );
      processSelection( vScroll );
    }
    composite.setOrigin( origin );
    ControlLCAUtil.processMouseEvents( composite );
    ControlLCAUtil.processKeyEvents( composite );
    ControlLCAUtil.processMenuDetect( composite );
    WidgetLCAUtil.processHelp( composite );
  }

  public void renderInitialization( Widget widget ) throws IOException {
    ScrolledComposite scrolledComposite = ( ScrolledComposite )widget;
    IClientObject clientObject = ClientObjectFactory.getForWidget( scrolledComposite );
    clientObject.create( TYPE );
    clientObject.setProperty( "parent", WidgetUtil.getId( scrolledComposite.getParent() ) );
    clientObject.setProperty( "style", WidgetLCAUtil.getStyles( scrolledComposite ) );
  }

  public void renderChanges( Widget widget ) throws IOException {
    ScrolledComposite composite = ( ScrolledComposite )widget;
    ControlLCAUtil.renderChanges( composite );
    WidgetLCAUtil.renderCustomVariant( composite );
    renderProperty( composite, PROP_CONTENT, composite.getContent(), null );
    renderProperty( composite, PROP_ORIGIN, composite.getOrigin(), DEFAULT_ORIGIN );
    renderProperty( composite,
                    PROP_SHOW_FOCUSED_CONTROL,
                    composite.getShowFocusedControl(),
                    false );
    // TODO: [if] Move scrollbars synchronization to the ScrollBarLCA once it exists
    renderProperty( composite,
                    PROP_SCROLLBARS_VISIBLE,
                    getScrollBarsVisible( composite ),
                    DEFAULT_SCROLLBARS_VISIBLE );
    renderListener( composite,
                    PROP_SCROLLBARS_SELECTION_LISTENER,
                    hasScrollBarsSelectionListener( composite ),
                    false );
  }

  public void renderDispose( Widget widget ) throws IOException {
    ClientObjectFactory.getForWidget( widget ).destroy();
  }

  //////////////////
  // Helping methods

  private static boolean[] getScrollBarsVisible( ScrolledComposite composite ) {
    return new boolean[] { hasHScrollBar( composite ), hasVScrollBar( composite ) };
  }

  private static boolean hasHScrollBar( ScrolledComposite composite ) {
    ScrollBar horizontalBar = composite.getHorizontalBar();
    return horizontalBar != null && horizontalBar.getVisible();
  }

  private static boolean hasVScrollBar( ScrolledComposite composite ) {
    ScrollBar verticalBar = composite.getVerticalBar();
    return verticalBar != null && verticalBar.getVisible();
  }

  private static boolean hasScrollBarsSelectionListener( ScrolledComposite composite ) {
    boolean result = false;
    ScrollBar horizontalBar = composite.getHorizontalBar();
    if( horizontalBar != null ) {
      result = result || SelectionEvent.hasListener( horizontalBar );
    }
    ScrollBar verticalBar = composite.getVerticalBar();
    if( verticalBar != null ) {
      result = result || SelectionEvent.hasListener( verticalBar );
    }
    return result;
  }

  private static void processSelection( ScrollBar scrollBar ) {
    SelectionEvent evt = new SelectionEvent( scrollBar, null, SelectionEvent.WIDGET_SELECTED );
    evt.stateMask = EventLCAUtil.readStateMask( JSConst.EVENT_WIDGET_SELECTED_MODIFIER );
    evt.processEvent();
  }
}
