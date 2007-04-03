/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.rwt.internal.widgets.linkkit;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.graphics.Point;
import org.eclipse.rap.rwt.internal.widgets.ILinkAdapter;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.Link;
import org.eclipse.rap.rwt.widgets.Widget;
import com.w4t.engine.service.ContextProvider;

public class LinkLCA extends AbstractWidgetLCA {

  private static final String PROP_TEXT = "text";
  private static final String PROP_SEL_LISTENER = "selectionListener";

  public void preserveValues( final Widget widget ) {
    Link link = ( Link )widget;
    ControlLCAUtil.preserveValues( link );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( PROP_TEXT, link.getText() );
    Boolean newValue = Boolean.valueOf( SelectionEvent.hasListener( link ) );
    adapter.preserve( PROP_SEL_LISTENER, newValue );
  }

  public void readData( final Widget widget ) {
    String eventId = JSConst.EVENT_WIDGET_SELECTED;
    if( WidgetLCAUtil.wasEventSent( widget, eventId ) ) {
      HttpServletRequest request = ContextProvider.getRequest();
      String indexStr
        = request.getParameter( JSConst.EVENT_WIDGET_SELECTED + ".index" );
      int index = Integer.parseInt( indexStr );
      SelectionEvent event = new SelectionEvent( widget,
                                                 null,
                                                 SelectionEvent.WIDGET_SELECTED );
      event.text = getIdText( ( Link )widget, index );
      event.processEvent();
    }
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.newWidget( "qx.ui.layout.HorizontalBoxLayout" );
    writer.set( JSConst.QX_FIELD_APPEARANCE, "link" );
    Object[] args = new Object[] { widget };
    writer.callStatic( "org.eclipse.rap.rwt.LinkUtil.init", args  );
    ControlLCAUtil.writeStyleFlags( widget );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    Link link = ( Link )widget;
    ControlLCAUtil.writeChanges( link );
    writeSelectionListener( link );
    writeText( link );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }

  private void writeSelectionListener( final Link link ) throws IOException {
    Boolean newValue = Boolean.valueOf( SelectionEvent.hasListener( link ) );
    Boolean defValue = Boolean.FALSE;
    if( WidgetLCAUtil.hasChanged( link, PROP_SEL_LISTENER, newValue, defValue ) )
    {
      JSWriter writer = JSWriter.getWriterFor( link );
      Object[] args = new Object[]{ link, newValue };
      writer.callStatic( "org.eclipse.rap.rwt.LinkUtil.setSelectionListener",
                         args );
    }
  }

  private String getIdText( final Link link, final int index ) {
    ILinkAdapter adapter = ( ILinkAdapter )link.getAdapter( ILinkAdapter.class );
    String[] ids = adapter.getIds();
    return ids[ index ];
  }

  private void writeText( final Link link ) throws IOException {
    String newValue = link.getText();
    if( WidgetLCAUtil.hasChanged( link, PROP_TEXT, newValue, "" ) ) {
      JSWriter writer = JSWriter.getWriterFor( link );
      Object[] args = new Object[]{ link };
      writer.callStatic( "org.eclipse.rap.rwt.LinkUtil.clear", args );
      ILinkAdapter adapter = ( ILinkAdapter )link.getAdapter( ILinkAdapter.class );
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
    }
  }

  private void writeNormalText( final Link link, final String text )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( link );
    Object[] args = new Object[]{ link, text };
    writer.callStatic( "org.eclipse.rap.rwt.LinkUtil.addText", args );
  }

  private void writeLinkText( final Link link,
                              final String text,
                              final int index ) throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( link );
    Object[] args = new Object[]{ link, text, new Integer( index ) };
    writer.callStatic( "org.eclipse.rap.rwt.LinkUtil.addLink", args );
  }

}
