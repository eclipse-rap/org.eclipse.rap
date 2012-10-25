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
package org.eclipse.swt.internal.custom.scrolledcompositekit;

import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.hasChanged;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.renderProperty;

import java.io.IOException;

import org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rap.rwt.internal.protocol.IClientObject;
import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.lifecycle.ControlLCAUtil;
import org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.widgets.ScrollBarLCAUtil;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Widget;


public final class ScrolledCompositeLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.ScrolledComposite";
  private static final String[] ALLOWED_STYLES = new String[] { "H_SCROLL", "V_SCROLL", "BORDER" };

  // Property names
  private static final String PROP_ORIGIN = "origin";
  private static final String PROP_CONTENT = "content";
  private static final String PROP_SHOW_FOCUSED_CONTROL = "showFocusedControl";

  // Default values
  private static final Point DEFAULT_ORIGIN = new Point( 0, 0 );

  @Override
  public void preserveValues( Widget widget ) {
    ScrolledComposite composite = ( ScrolledComposite )widget;
    ControlLCAUtil.preserveValues( composite );
    WidgetLCAUtil.preserveCustomVariant( composite );
    preserveProperty( composite, PROP_ORIGIN, getOrigin( composite ) );
    preserveProperty( composite, PROP_CONTENT, composite.getContent() );
    preserveProperty( composite, PROP_SHOW_FOCUSED_CONTROL, composite.getShowFocusedControl() );
    ScrollBarLCAUtil.preserveValues( composite );
  }

  public void readData( Widget widget ) {
    ScrolledComposite composite = ( ScrolledComposite )widget;
    readOrigin( composite );
    ControlLCAUtil.processEvents( composite );
    ControlLCAUtil.processKeyEvents( composite );
    ControlLCAUtil.processMenuDetect( composite );
    WidgetLCAUtil.processHelp( composite );
    ScrollBarLCAUtil.processSelectionEvent( composite );
  }

  @Override
  public void renderInitialization( Widget widget ) throws IOException {
    ScrolledComposite composite = ( ScrolledComposite )widget;
    IClientObject clientObject = ClientObjectFactory.getClientObject( composite );
    clientObject.create( TYPE );
    clientObject.set( "parent", WidgetUtil.getId( composite.getParent() ) );
    clientObject.set( "style", WidgetLCAUtil.getStyles( composite, ALLOWED_STYLES ) );
    ScrollBarLCAUtil.renderInitialization( composite );
  }

  @Override
  public void renderChanges( Widget widget ) throws IOException {
    ScrolledComposite composite = ( ScrolledComposite )widget;
    ControlLCAUtil.renderChanges( composite );
    WidgetLCAUtil.renderCustomVariant( composite );
    renderOrigin( composite );
    renderProperty( composite, PROP_CONTENT, composite.getContent(), null );
    renderProperty( composite,
                    PROP_SHOW_FOCUSED_CONTROL,
                    composite.getShowFocusedControl(),
                    false );
    ScrollBarLCAUtil.renderChanges( composite );
  }

  @Override
  public void renderDispose( Widget widget ) throws IOException {
    ClientObjectFactory.getClientObject( widget ).destroy();
  }

  //////////////////
  // Helping methods

  private static void readOrigin( ScrolledComposite composite ) {
    Point origin = composite.getOrigin();
    Integer scrollLeft = ScrollBarLCAUtil.readSelection( composite.getHorizontalBar() );
    if( scrollLeft != null ) {
      origin.x = scrollLeft.intValue();
    }
    Integer scrollTop = ScrollBarLCAUtil.readSelection( composite.getVerticalBar() );
    if( scrollTop != null ) {
      origin.y = scrollTop.intValue();
    }
    composite.setOrigin( origin );
  }

  private static void renderOrigin( ScrolledComposite composite ) {
    Point newValue = getOrigin( composite );
    String prop = PROP_ORIGIN;
    if( hasChanged( composite, prop, newValue, DEFAULT_ORIGIN ) ) {
      ScrollBarLCAUtil.renderSelection( composite.getHorizontalBar(), newValue.x );
      ScrollBarLCAUtil.renderSelection( composite.getVerticalBar(), newValue.y );
    }
  }

  private static Point getOrigin( ScrolledComposite composite ) {
    Point result = new Point( 0, 0 );
    ScrollBar horizontalBar = composite.getHorizontalBar();
    if( horizontalBar != null ) {
      result.x = horizontalBar.getSelection();
    }
    ScrollBar verticalBar = composite.getVerticalBar();
    if( verticalBar != null ) {
      result.y = verticalBar.getSelection();
    }
    return result;
  }

}
