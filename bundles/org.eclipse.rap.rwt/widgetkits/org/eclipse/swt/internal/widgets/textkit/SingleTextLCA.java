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
package org.eclipse.swt.internal.widgets.textkit;

import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.readEventPropertyValue;

import java.io.IOException;

import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.internal.events.EventLCAUtil;
import org.eclipse.swt.widgets.Text;


final class SingleTextLCA extends AbstractTextDelegateLCA {

  @Override
  void preserveValues( Text text ) {
    TextLCAUtil.preserveValues( text );
  }

  /* (intentionally non-JavaDoc'ed)
   * readData does not explicitly handle modifyEvents. They are fired implicitly
   * by updating the text property in TextLCAUtil.readText( Text ).
   */
  @Override
  void readData( Text text ) {
    TextLCAUtil.readTextAndSelection( text );
    processSelection( text );
    ControlLCAUtil.processEvents( text );
    ControlLCAUtil.processKeyEvents( text );
    ControlLCAUtil.processMenuDetect( text );
    WidgetLCAUtil.processHelp( text );
  }

  @Override
  void renderInitialization( Text text ) throws IOException {
    TextLCAUtil.renderInitialization( text );
  }

  @Override
  void renderChanges( Text text ) throws IOException {
    TextLCAUtil.renderChanges( text );
  }

  private static void processSelection( Text text ) {
    if( WidgetLCAUtil.wasEventSent( text, ClientMessageConst.EVENT_SELECTION ) ) {
      createSelectionEvent( text, SelectionEvent.WIDGET_SELECTED ).processEvent();
    }
    if( WidgetLCAUtil.wasEventSent( text, ClientMessageConst.EVENT_DEFAULT_SELECTION ) ) {
      createSelectionEvent( text, SelectionEvent.WIDGET_DEFAULT_SELECTED ).processEvent();
    }
  }

  private static SelectionEvent createSelectionEvent( Text text, int type ) {
    SelectionEvent result = new SelectionEvent( text, null, type );
    String eventName = ClientMessageConst.EVENT_SELECTION;
    if( type == SelectionEvent.WIDGET_DEFAULT_SELECTED ) {
      eventName = ClientMessageConst.EVENT_DEFAULT_SELECTION;
      result.detail = getWidgetDefaultSelectedDetail( text );
    }
    result.stateMask = EventLCAUtil.readStateMask( text, eventName );
    return result;
  }

  private static int getWidgetDefaultSelectedDetail( Text text ) {
    String value = readEventPropertyValue( text,
                                           ClientMessageConst.EVENT_DEFAULT_SELECTION,
                                           ClientMessageConst.EVENT_PARAM_DETAIL );
    int result = SWT.NONE;
    if( "search".equals( value ) ) {
      result = SWT.ICON_SEARCH;
    } else if( "cancel".equals( value ) ) {
      result = SWT.ICON_CANCEL;
    }
    return result;
  }

}