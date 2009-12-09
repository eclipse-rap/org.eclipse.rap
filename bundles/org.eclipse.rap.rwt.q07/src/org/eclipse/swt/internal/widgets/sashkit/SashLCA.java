/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.sashkit;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.events.EventLCAUtil;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;


public final class SashLCA extends AbstractWidgetLCA {

  private static final String QX_TYPE = "org.eclipse.swt.widgets.Sash";

  public void preserveValues( final Widget widget ) {
    ControlLCAUtil.preserveValues( ( Control )widget );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( Props.SELECTION_LISTENERS,
                      SelectionEvent.getListeners( widget ) );
    WidgetLCAUtil.preserveCustomVariant( widget );
  }

  public void readData( final Widget widget ) {
    Sash sash = ( Sash )widget;
    processSelection( sash );
    ControlLCAUtil.processMouseEvents( sash );
    ControlLCAUtil.processKeyEvents( sash );
    WidgetLCAUtil.processHelp( sash );
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    Sash sash = ( Sash )widget;
    JSWriter writer = JSWriter.getWriterFor( sash );
    writer.newWidget( QX_TYPE );
    JSVar orientation
      = ( sash.getStyle() & SWT.HORIZONTAL ) != 0
      ? JSConst.QX_CONST_HORIZONTAL_ORIENTATION
      : JSConst.QX_CONST_VERTICAL_ORIENTATION;
    writer.set( JSConst.QX_FIELD_ORIENTATION, orientation );
    ControlLCAUtil.writeStyleFlags( sash );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    Sash sash = ( Sash )widget;
    ControlLCAUtil.writeChanges( sash );
    WidgetLCAUtil.writeCustomVariant( sash );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }

  private static void processSelection( final Sash sash ) {
    HttpServletRequest request = ContextProvider.getRequest();
    String eventId = JSConst.EVENT_WIDGET_SELECTED;
    if( WidgetLCAUtil.wasEventSent( sash, eventId ) ) {
      int eventType = SelectionEvent.WIDGET_SELECTED;
      Rectangle bounds = WidgetLCAUtil.readBounds( sash, sash.getBounds() );
      int stateMask
        = EventLCAUtil.readStateMask( JSConst.EVENT_WIDGET_SELECTED_MODIFIER );
      String detailStr = request.getParameter( eventId + ".detail" );
      int detail = "drag".equals( detailStr ) ? SWT.DRAG : SWT.NONE;      
      SelectionEvent event = new SelectionEvent( sash,
                                                 null,
                                                 eventType,
                                                 bounds,
                                                 stateMask,
                                                 null,
                                                 true,
                                                 detail );
      event.processEvent();
    }
  }
}
