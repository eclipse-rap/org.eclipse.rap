/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH and others.
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
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_SELECTION;
import static org.eclipse.rap.rwt.internal.protocol.JsonUtil.createJsonArray;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.getStyles;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.readEventPropertyValue;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.wasEventSent;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;

import java.io.IOException;

import org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rap.rwt.internal.protocol.IClientObject;
import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.lifecycle.ControlLCAUtil;
import org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.events.EventLCAUtil;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Widget;


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
    clientObject.set( "parent", getId( sash.getParent() ) );
    clientObject.set( "style", createJsonArray( getStyles( sash, ALLOWED_STYLES ) ) );
  }

  @Override
  public void renderChanges( Widget widget ) throws IOException {
    Sash sash = ( Sash )widget;
    ControlLCAUtil.renderChanges( sash );
    WidgetLCAUtil.renderCustomVariant( sash );
  }

  private static void processSelection( Sash sash ) {
    String eventName = EVENT_SELECTION;
    if( wasEventSent( sash, eventName ) ) {
      String x = readEventPropertyValue( sash, EVENT_SELECTION, "x" );
      String y = readEventPropertyValue( sash, EVENT_SELECTION, "y" );
      String width = readEventPropertyValue( sash, EVENT_SELECTION, "width" );
      String height = readEventPropertyValue( sash, EVENT_SELECTION, "height" );
      Rectangle bounds = new Rectangle(
        Integer.parseInt( x ),
        Integer.parseInt( y ),
        Integer.parseInt( width ),
        Integer.parseInt( height )
      );
      int stateMask = EventLCAUtil.readStateMask( sash, eventName );
      String value = readEventPropertyValue( sash, eventName, EVENT_PARAM_DETAIL );
      int detail = "drag".equals( value ) ? SWT.DRAG : SWT.NONE;
      Event event = new Event();
      event.setBounds( bounds );
      event.detail = detail;
      event.stateMask = stateMask;
      sash.notifyListeners( SWT.Selection, event );
    }
  }

}
