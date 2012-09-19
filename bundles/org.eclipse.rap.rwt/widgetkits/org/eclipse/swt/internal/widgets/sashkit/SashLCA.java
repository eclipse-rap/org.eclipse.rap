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
package org.eclipse.swt.internal.widgets.sashkit;

import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_PARAM_DETAIL;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.readEventPropertyValue;

import java.io.IOException;

import org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rap.rwt.internal.protocol.IClientObject;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.events.EventLCAUtil;
import org.eclipse.swt.widgets.*;


public final class SashLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.Sash";
  private static final String[] ALLOWED_STYLES = new String[] {
    "HORIZONTAL", "VERTICAL", "SMOOTH", "BORDER"
  };

  @Override
  public void preserveValues( Widget widget ) {
    ControlLCAUtil.preserveValues( ( Control )widget );
    WidgetLCAUtil.preserveCustomVariant( widget );
  }

  public void readData( Widget widget ) {
    Sash sash = ( Sash )widget;
    processSelection( sash );
    ControlLCAUtil.processEvents( sash );
    ControlLCAUtil.processKeyEvents( sash );
    ControlLCAUtil.processMenuDetect( sash );
    WidgetLCAUtil.processHelp( sash );
  }

  @Override
  public void renderInitialization( Widget widget ) throws IOException {
    Sash sash = ( Sash )widget;
    IClientObject clientObject = ClientObjectFactory.getClientObject( sash );
    clientObject.create( TYPE );
    clientObject.set( "parent", WidgetUtil.getId( sash.getParent() ) );
    clientObject.set( "style", WidgetLCAUtil.getStyles( sash, ALLOWED_STYLES ) );
  }

  @Override
  public void renderChanges( Widget widget ) throws IOException {
    Sash sash = ( Sash )widget;
    ControlLCAUtil.renderChanges( sash );
    WidgetLCAUtil.renderCustomVariant( sash );
  }

  @Override
  public void renderDispose( Widget widget ) throws IOException {
    ClientObjectFactory.getClientObject( widget ).destroy();
  }

  private static void processSelection( Sash sash ) {
    String eventName = ClientMessageConst.EVENT_WIDGET_SELECTED;
    if( WidgetLCAUtil.wasEventSent( sash, eventName ) ) {
      Rectangle bounds = WidgetLCAUtil.readBounds( sash, sash.getBounds() );
      int stateMask = EventLCAUtil.readStateMask( sash, eventName );
      String value = readEventPropertyValue( sash, eventName, EVENT_PARAM_DETAIL );
      int detail = "drag".equals( value ) ? SWT.DRAG : SWT.NONE;
      SelectionEvent event = new SelectionEvent( sash,
                                                 null,
                                                 SelectionEvent.WIDGET_SELECTED,
                                                 bounds,
                                                 stateMask,
                                                 null,
                                                 true,
                                                 detail );
      event.processEvent();
    }
  }
}
