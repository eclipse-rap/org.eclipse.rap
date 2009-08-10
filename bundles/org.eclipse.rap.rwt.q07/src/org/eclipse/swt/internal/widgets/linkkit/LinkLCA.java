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

package org.eclipse.swt.internal.widgets.linkkit;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.widgets.ILinkAdapter;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Widget;

/**
 * Life-cycle adapter for the Link widget
 */
public class LinkLCA extends AbstractWidgetLCA {
  
  //Constants for JS functions names
  private static final String JS_FUNC_ADDTEXT = "addText";
  private static final String JS_FUNC_ADDLINK = "addLink";
  private static final String JS_FUNC_APPLYTEXT = "applyText";
  private static final String JS_FUNC_CLEAR = "clear";

  //Property names for preserveValues
  private static final String PROP_TEXT = "text";

  public void preserveValues( final Widget widget ) {
    Link link = ( Link )widget;
    ControlLCAUtil.preserveValues( link );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( PROP_TEXT, link.getText() );
    boolean hasListeners = SelectionEvent.hasListener( link );
    adapter.preserve( Props.SELECTION_LISTENERS,
                      Boolean.valueOf( hasListeners ) );
    WidgetLCAUtil.preserveCustomVariant( link );
  }

  public void readData( final Widget widget ) {
    Link link = ( Link )widget;
    processSelectionEvent( link );
    ControlLCAUtil.processMouseEvents( link );
    ControlLCAUtil.processKeyEvents( link );
    WidgetLCAUtil.processHelp( link );
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    Link link = ( Link )widget;
    JSWriter writer = JSWriter.getWriterFor( link );
    writer.newWidget( "org.eclipse.swt.widgets.Link" );
    ControlLCAUtil.writeStyleFlags( link );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    Link link = ( Link )widget;
    ControlLCAUtil.writeChanges( link );
    writeSelectionListener( link );
    writeText( link );
    WidgetLCAUtil.writeCustomVariant( link );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }

  private static void writeSelectionListener( final Link link )
    throws IOException
  {
    boolean hasListener = SelectionEvent.hasListener( link );
    Boolean newValue = Boolean.valueOf( hasListener );
    String prop = Props.SELECTION_LISTENERS;
    if( WidgetLCAUtil.hasChanged( link, prop, newValue, Boolean.FALSE ) ) {
      JSWriter writer = JSWriter.getWriterFor( link );
      writer.set( "hasSelectionListener", newValue );
    }
  }

  private static void writeText( final Link link ) throws IOException {
    String newValue = link.getText();
    if( WidgetLCAUtil.hasChanged( link, PROP_TEXT, newValue, "" ) ) {
      JSWriter writer = JSWriter.getWriterFor( link );
      writer.call( JS_FUNC_CLEAR, null );
      ILinkAdapter adapter
        = ( ILinkAdapter )link.getAdapter( ILinkAdapter.class );
      String displayText = adapter.getDisplayText();
      Point[] offsets = adapter.getOffsets();
      int length = displayText.length();
      int pos = 0;
      for( int i = 0; i < offsets.length; i++ ) {
        int start = offsets[ i ].x;
        int end = offsets[ i ].y + 1;
        // before link
        if( pos < start ) {
          writeNormalText( link, displayText.substring( pos, start ) );
        }
        // link itself
        if( start < end ) {
          writeLinkText( link, displayText.substring( start, end ), i );
        }
        pos = end;
      }
      // after last link
      if( pos < length ) {
        writeNormalText( link, displayText.substring( pos, length ) );
      }
      writeApplyText( link );
    }
  }

  private static void writeNormalText( final Link link, final String text )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( link );
    Object[] args = new Object[] {
      // TODO [rst] mnemonics are already parsed by Link#parse()
      //            Revise when we're going to support underline once
      WidgetLCAUtil.escapeText( text, false )
    };
    writer.call( JS_FUNC_ADDTEXT, args );
  }

  private static void writeLinkText( final Link link,
                                     final String text,
                                     final int index )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( link );
    Object[] args = new Object[] {
      // TODO [rst] mnemonics are already parsed by Link#parse()
      //            Revise when we're going to support underline once
      WidgetLCAUtil.escapeText( text, false ),
      new Integer( index )
    };
    writer.call( JS_FUNC_ADDLINK, args );
  }
  
  private static void writeApplyText( final Link link ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( link );
    writer.call( JS_FUNC_APPLYTEXT, null );
  }

  private static void processSelectionEvent( final Link link ) {
    String eventId = JSConst.EVENT_WIDGET_SELECTED;
    if( WidgetLCAUtil.wasEventSent( link, eventId ) ) {
      HttpServletRequest request = ContextProvider.getRequest();
      String indexStr
        = request.getParameter( JSConst.EVENT_WIDGET_SELECTED_INDEX );
      int index = Integer.parseInt( indexStr );
      ILinkAdapter adapter
        = ( ILinkAdapter )link.getAdapter( ILinkAdapter.class );
      String[] ids = adapter.getIds();
      if( index < ids.length ) {
        SelectionEvent event
          = new SelectionEvent( link, null, SelectionEvent.WIDGET_SELECTED );
        event.text = ids[ index ];
        event.processEvent();
      }
    }
  }
}
