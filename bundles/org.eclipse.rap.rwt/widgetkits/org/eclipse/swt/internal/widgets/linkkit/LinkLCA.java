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

package org.eclipse.swt.internal.widgets.linkkit;

import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.preserveListener;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.renderListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rwt.internal.protocol.IClientObject;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.util.NumberFormatUtil;
import org.eclipse.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rwt.lifecycle.ControlLCAUtil;
import org.eclipse.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.events.EventLCAUtil;
import org.eclipse.swt.internal.widgets.ILinkAdapter;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Widget;

public class LinkLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.Link";

  static final String PROP_TEXT = "text";
  static final String PROP_SELECTION_LISTENER = "selection";

  public void preserveValues( Widget widget ) {
    Link link = ( Link )widget;
    ControlLCAUtil.preserveValues( link );
    WidgetLCAUtil.preserveCustomVariant( link );
    preserveProperty( link, PROP_TEXT, link.getText() );
    preserveListener( link, PROP_SELECTION_LISTENER, SelectionEvent.hasListener( link ) );
  }

  public void readData( Widget widget ) {
    Link link = ( Link )widget;
    processSelectionEvent( link );
    ControlLCAUtil.processMouseEvents( link );
    ControlLCAUtil.processKeyEvents( link );
    ControlLCAUtil.processMenuDetect( link );
    WidgetLCAUtil.processHelp( link );
  }

  public void renderInitialization( Widget widget ) throws IOException {
    Link link = ( Link )widget;
    IClientObject clientObject = ClientObjectFactory.getForWidget( link );
    clientObject.create( TYPE );
    clientObject.setProperty( "parent", WidgetUtil.getId( link.getParent() ) );
    clientObject.setProperty( "style", WidgetLCAUtil.getStyles( link ) );
  }

  public void renderChanges( Widget widget ) throws IOException {
    Link link = ( Link )widget;
    ControlLCAUtil.renderChanges( link );
    WidgetLCAUtil.renderCustomVariant( link );
    renderText( link );
    renderListener( link, PROP_SELECTION_LISTENER, SelectionEvent.hasListener( link ), false );
  }

  public void renderDispose( Widget widget ) throws IOException {
    ClientObjectFactory.getForWidget( widget ).destroy();
  }

  ///////////////////////////////////////////////////
  // Helping methods to render the changed properties

  private static void renderText( Link link ) {
    String newValue = link.getText();
    if( WidgetLCAUtil.hasChanged( link, PROP_TEXT, newValue, "" ) ) {
      IClientObject clientObject = ClientObjectFactory.getForWidget( link );
      clientObject.setProperty( PROP_TEXT, getTextObject( link ) );
    }
  }

  //////////////////
  // Helping methods

  private static Object getTextObject( Link link ) {
    ILinkAdapter adapter = link.getAdapter( ILinkAdapter.class );
    String displayText = adapter.getDisplayText();
    Point[] offsets = adapter.getOffsets();
    List<Object[]> result = new ArrayList<Object[]>();
    int length = displayText.length();
    int pos = 0;
    for( int i = 0; i < offsets.length; i++ ) {
      int start = offsets[ i ].x;
      int end = offsets[ i ].y + 1;
      // before link
      if( pos < start ) {
        result.add( new Object[] { displayText.substring( pos, start ), null } );
      }
      // link itself
      if( start < end ) {
        result.add( new Object[] { displayText.substring( start, end ), new Integer( i ) } );
      }
      pos = end;
    }
    // after last link
    if( pos < length ) {
      result.add( new Object[] { displayText.substring( pos, length ), null } );
    }
    return result.toArray();
  }

  private static void processSelectionEvent( Link link ) {
    String eventId = JSConst.EVENT_WIDGET_SELECTED;
    if( WidgetLCAUtil.wasEventSent( link, eventId ) ) {
      HttpServletRequest request = ContextProvider.getRequest();
      String indexStr = request.getParameter( JSConst.EVENT_WIDGET_SELECTED_INDEX );
      int index = NumberFormatUtil.parseInt( indexStr );
      ILinkAdapter adapter = link.getAdapter( ILinkAdapter.class );
      String[] ids = adapter.getIds();
      if( index < ids.length ) {
        SelectionEvent event = new SelectionEvent( link, null, SelectionEvent.WIDGET_SELECTED );
        event.text = ids[ index ];
        event.stateMask = EventLCAUtil.readStateMask( JSConst.EVENT_WIDGET_SELECTED_MODIFIER );
        event.processEvent();
      }
    }
  }
}
