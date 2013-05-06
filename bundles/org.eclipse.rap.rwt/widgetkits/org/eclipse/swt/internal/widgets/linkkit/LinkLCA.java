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

package org.eclipse.swt.internal.widgets.linkkit;

import static org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory.getClientObject;
import static org.eclipse.rap.rwt.internal.protocol.JsonUtil.createJsonArray;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.getStyles;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.hasChanged;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveListener;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.readEventPropertyValue;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.renderListener;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.wasEventSent;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.eclipse.swt.internal.events.EventLCAUtil.isListening;

import java.io.IOException;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.internal.protocol.IClientObject;
import org.eclipse.rap.rwt.internal.util.NumberFormatUtil;
import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.lifecycle.ControlLCAUtil;
import org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.events.EventLCAUtil;
import org.eclipse.swt.internal.widgets.ILinkAdapter;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Widget;

public class LinkLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.Link";
  private static final String[] ALLOWED_STYLES = new String[] { "BORDER" };

  static final String PROP_TEXT = "text";
  static final String PROP_SELECTION_LISTENER = "Selection";

  @Override
  public void preserveValues( Widget widget ) {
    Link link = ( Link )widget;
    ControlLCAUtil.preserveValues( link );
    WidgetLCAUtil.preserveCustomVariant( link );
    preserveProperty( link, PROP_TEXT, link.getText() );
    preserveListener( link, PROP_SELECTION_LISTENER, isListening( link, SWT.Selection ) );
  }

  public void readData( Widget widget ) {
    Link link = ( Link )widget;
    processSelectionEvent( link );
    ControlLCAUtil.processEvents( link );
    ControlLCAUtil.processKeyEvents( link );
    ControlLCAUtil.processMenuDetect( link );
    WidgetLCAUtil.processHelp( link );
  }

  @Override
  public void renderInitialization( Widget widget ) throws IOException {
    Link link = ( Link )widget;
    IClientObject clientObject = getClientObject( link );
    clientObject.create( TYPE );
    clientObject.set( "parent", getId( link.getParent() ) );
    clientObject.set( "style", createJsonArray( getStyles( link, ALLOWED_STYLES ) ) );
  }

  @Override
  public void renderChanges( Widget widget ) throws IOException {
    Link link = ( Link )widget;
    ControlLCAUtil.renderChanges( link );
    WidgetLCAUtil.renderCustomVariant( link );
    renderText( link );
    renderListener( link, PROP_SELECTION_LISTENER, isListening( link, SWT.Selection ), false );
  }

  ///////////////////////////////////////////////////
  // Helping methods to render the changed properties

  private static void renderText( Link link ) {
    String newValue = link.getText();
    if( hasChanged( link, PROP_TEXT, newValue, "" ) ) {
      getClientObject( link ).set( PROP_TEXT, getTextObject( link ) );
    }
  }

  //////////////////
  // Helping methods

  private static JsonArray getTextObject( Link link ) {
    ILinkAdapter adapter = link.getAdapter( ILinkAdapter.class );
    String displayText = adapter.getDisplayText();
    Point[] offsets = adapter.getOffsets();
    JsonArray result = new JsonArray();
    int length = displayText.length();
    int pos = 0;
    for( int i = 0; i < offsets.length; i++ ) {
      int start = offsets[ i ].x;
      int end = offsets[ i ].y + 1;
      // before link
      if( pos < start ) {
        result.add( new JsonArray().add( displayText.substring( pos, start ) )
                                   .add( JsonObject.NULL ) );
      }
      // link itself
      if( start < end ) {
        result.add( new JsonArray().add( displayText.substring( start, end ) ).add( i ) );
      }
      pos = end;
    }
    // after last link
    if( pos < length ) {
      result.add( new JsonArray().add( displayText.substring( pos, length ) )
                                 .add( JsonObject.NULL ) );
    }
    return result;
  }

  private static void processSelectionEvent( Link link ) {
    String eventName = ClientMessageConst.EVENT_SELECTION;
    if( wasEventSent( link, eventName ) ) {
      String value = readEventPropertyValue( link, eventName, ClientMessageConst.EVENT_PARAM_INDEX );
      int index = NumberFormatUtil.parseInt( value );
      ILinkAdapter adapter = link.getAdapter( ILinkAdapter.class );
      String[] ids = adapter.getIds();
      if( index < ids.length ) {
        Event event = new Event();
        event.text = ids[ index ];
        event.stateMask = EventLCAUtil.readStateMask( link, eventName );
        link.notifyListeners( SWT.Selection, event );
      }
    }
  }
}
