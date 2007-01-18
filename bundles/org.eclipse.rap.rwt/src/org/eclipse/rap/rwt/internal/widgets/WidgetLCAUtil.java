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

package org.eclipse.rap.rwt.internal.widgets;

import java.io.IOException;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.graphics.Font;
import org.eclipse.rap.rwt.graphics.Rectangle;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.*;
import com.w4t.W4TContext;
import com.w4t.util.browser.Mozilla;


public final class WidgetLCAUtil {
  
  private WidgetLCAUtil() {
    // prevent instantiation
  }
  
  public static void writeBounds( final Widget widget, 
                                  final Control parent, 
                                  final Rectangle bounds, 
                                  final boolean clip ) 
    throws IOException 
  {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    // TODO [rh] replace code below with WidgetUtil.hasChanged
    Rectangle oldBounds = ( Rectangle )adapter.getPreserved( Props.BOUNDS );
    Rectangle newBounds = bounds;
    if( !adapter.isInitialized() || !newBounds.equals( oldBounds ) ) {
      
      // the RWT coordinates for client area differ in some cases to
      // the widget realisation of qooxdoo
      if( parent != null ) {
        AbstractWidgetLCA parentLCA = WidgetUtil.getLCA( parent );
        newBounds = parentLCA.adjustCoordinates( newBounds ); 
      }
      
      JSWriter writer = JSWriter.getWriterFor( widget );
      
      //////////////////////////////////////////////////////////////////
      // TODO: [fappel] height values of controls are not displayed 
      //                proper in mozilla. This is a very rude approximation
      //                and should be eighter solved in qooxdoo or by a more
      //                sophisticated approach...
      int[] args;
      if(    W4TContext.getBrowser() instanceof Mozilla
          && widget instanceof Control )
      {
        if( newBounds.height > 30 ) {
          args = new int[] {
            newBounds.x, newBounds.width, newBounds.y, newBounds.height - 4
          };
        } else {
          args = new int[] {
            newBounds.x, newBounds.width, newBounds.y, newBounds.height
          };
        }
      } else {
        args = new int[] {
          newBounds.x, newBounds.width, newBounds.y, newBounds.height
        };
      }
      //////////////////////////////////////////////////////////////////
      
      writer.set( "space", args );
      if( !WidgetUtil.getAdapter( widget ).isInitialized() ) {
        writer.set( "minWidth", 0 );
        writer.set( "minHeight", 0 );
      }
      if( clip ) {
        writer.set( "clipHeight", args[ 3 ] );
        writer.set( "clipWidth", args[ 1 ] );
      }
    }
  }

  public static void writeMenu( final Widget widget, final Menu menu )
    throws IOException
  {
    if( WidgetUtil.hasChanged( widget, Props.MENU, menu, null ) ) {
      JSWriter writer = JSWriter.getWriterFor( widget );
      writer.set( "contextMenu", menu );
      if( menu == null ) {
        writer.removeListener( JSConst.QX_EVENT_CONTEXTMENU, 
                               JSConst.JS_CONTEXT_MENU );
      } else {
        writer.addListener( JSConst.QX_EVENT_CONTEXTMENU, 
                            JSConst.JS_CONTEXT_MENU );
      }
    }
  }

  public static void writeToolTip( final Widget widget, final String newText ) 
    throws IOException 
  {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    if( adapter.isInitialized() ) {
      if( WidgetUtil.hasChanged( widget, Props.TOOL_TIP_TEXT, newText ) ) {
        doWriteToolTip( widget, newText );
      }
    } else if( newText != null && !"".equals( newText ) ) {
      doWriteToolTip( widget, newText );
    }
  }

  private static void doWriteToolTip( final Widget widget, final String text ) 
    throws IOException 
  {
    JSWriter writer = JSWriter.getWriterFor( widget );
    Object[] args = new Object[] { widget, text };
    writer.call( JSWriter.WIDGET_MANAGER_REF, "setToolTip", args );
  }
  
  /////////////////////////////////////////////////
  // write-methods used by other ...LCAUtil classes
  
  static void writeFont( final Widget widget, final Font font )
    throws IOException
  {
    Font systemFont = widget.getDisplay().getSystemFont();
    if( WidgetUtil.hasChanged( widget, Props.FONT, font, systemFont ) ) {
      JSWriter writer = JSWriter.getWriterFor( widget );
      Object[] args = new Object[]{
        widget,
        font.getName(),
        new Integer( font.getSize() ),
        Boolean.valueOf( ( font.getStyle() & RWT.BOLD ) != 0 ),
        Boolean.valueOf( ( font.getStyle() & RWT.ITALIC ) != 0 )
      };
      writer.call( JSWriter.WIDGET_MANAGER_REF, "setFont", args );
    }
  }
}
