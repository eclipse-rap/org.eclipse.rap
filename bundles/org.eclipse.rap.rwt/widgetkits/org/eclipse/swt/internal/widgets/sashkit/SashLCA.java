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

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rwt.internal.protocol.IClientObject;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.lifecycle.*;
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

  public void preserveValues( Widget widget ) {
    ControlLCAUtil.preserveValues( ( Control )widget );
    WidgetLCAUtil.preserveCustomVariant( widget );
  }

  public void readData( Widget widget ) {
    Sash sash = ( Sash )widget;
    processSelection( sash );
    ControlLCAUtil.processMouseEvents( sash );
    ControlLCAUtil.processKeyEvents( sash );
    ControlLCAUtil.processMenuDetect( sash );
    WidgetLCAUtil.processHelp( sash );
  }

  public void renderInitialization( Widget widget ) throws IOException {
    Sash sash = ( Sash )widget;
    IClientObject clientObject = ClientObjectFactory.getForWidget( sash );
    clientObject.create( TYPE );
    clientObject.setProperty( "parent", WidgetUtil.getId( sash.getParent() ) );
    clientObject.setProperty( "style", WidgetLCAUtil.getStyles( sash, ALLOWED_STYLES ) );
  }

  public void renderChanges( Widget widget ) throws IOException {
    Sash sash = ( Sash )widget;
    ControlLCAUtil.renderChanges( sash );
    WidgetLCAUtil.renderCustomVariant( sash );
  }

  public void renderDispose( Widget widget ) throws IOException {
    ClientObjectFactory.getForWidget( widget ).destroy();
  }

  private static void processSelection( Sash sash ) {
    HttpServletRequest request = ContextProvider.getRequest();
    String eventId = JSConst.EVENT_WIDGET_SELECTED;
    if( WidgetLCAUtil.wasEventSent( sash, eventId ) ) {
      int eventType = SelectionEvent.WIDGET_SELECTED;
      Rectangle bounds = WidgetLCAUtil.readBounds( sash, sash.getBounds() );
      int stateMask = EventLCAUtil.readStateMask( JSConst.EVENT_WIDGET_SELECTED_MODIFIER );
      String detailStr = request.getParameter( eventId + ".detail" );
      int detail = "drag".equals( detailStr ) ? SWT.DRAG : SWT.NONE;
      SelectionEvent event
        = new SelectionEvent( sash, null, eventType, bounds, stateMask, null, true, detail );
      event.processEvent();
    }
  }
}
