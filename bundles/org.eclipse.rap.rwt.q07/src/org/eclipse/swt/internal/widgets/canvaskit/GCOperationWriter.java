/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.canvaskit;

import java.io.IOException;

import org.eclipse.rwt.internal.util.EncodingUtil;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.graphics.GCOperation;
import org.eclipse.swt.internal.graphics.ResourceFactory;
import org.eclipse.swt.internal.graphics.GCOperation.*;
import org.eclipse.swt.widgets.Control;

final class GCOperationWriter {

  private static final JSVar GC_VAR = new JSVar( "gc" );

  private final Control control;
  private boolean initialized;

  GCOperationWriter( final Control control ) {
    this.control = control;
  }

  void write( final GCOperation operation ) throws IOException {
    initialize();
    if( operation instanceof DrawLine ) {
      drawLine( ( DrawLine )operation );
    } else if( operation instanceof DrawPoint ) {
      drawPoint( ( DrawPoint )operation );
    } else if( operation instanceof DrawRoundRectangle ) {
      drawRoundRectangle( ( DrawRoundRectangle )operation );
    } else if( operation instanceof FillGradientRectangle ) {
      fillGradientRectangle( ( FillGradientRectangle )operation );
    } else if( operation instanceof DrawRectangle ) {
      drawRectangle( ( DrawRectangle )operation );
    } else if( operation instanceof DrawArc ) {
      drawArc( ( DrawArc )operation );
    } else if( operation instanceof DrawPolyline ) {
      drawPolyline( ( DrawPolyline )operation );
    } else if( operation instanceof DrawImage ) {
      drawImage( ( DrawImage )operation );
    } else if( operation instanceof DrawText ) {
      drawText( ( DrawText )operation );
    } else if( operation instanceof SetProperty ) {
      setProperty( ( SetProperty )operation );
    } else if( operation instanceof SetFont ) {
      setFont( ( SetFont )operation );
    } else {
      String name = operation.getClass().getName();
      throw new IllegalArgumentException( "Unsupported GCOperation: " + name );
    }
  }

  private void drawLine( final DrawLine operation ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( control );
    Integer[] args = new Integer[] {
      new Integer( operation.x1 ),
      new Integer( operation.y1 ),
      new Integer( operation.x2 ),
      new Integer( operation.y2 )
    };
    writer.call( GC_VAR, "drawLine", args );
  }

  private void drawPoint( final DrawPoint operation ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( control );
    Integer[] args = new Integer[] {
      new Integer( operation.x ),
      new Integer( operation.y )
    };
    writer.call( GC_VAR, "drawPoint", args );
  }

  private void drawRectangle( final DrawRectangle operation ) throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( control );
    Object[] args = new Object[] {
      new Integer( operation.x ),
      new Integer( operation.y ),
      new Integer( operation.width ),
      new Integer( operation.height ),
      new Boolean( operation.fill )
    };
    writer.call( GC_VAR, "drawRectangle", args );
  }

  private void fillGradientRectangle( final FillGradientRectangle operation )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( control );
    Object[] args = new Object[] {
      new Integer( operation.x ),
      new Integer( operation.y ),
      new Integer( operation.width ),
      new Integer( operation.height ),
      new Boolean( operation.vertical )
    };
    writer.call( GC_VAR, "fillGradientRectangle", args );
  }

  private void drawRoundRectangle( final DrawRoundRectangle operation )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( control );
    Object[] args = new Object[] {
      new Integer( operation.x ),
      new Integer( operation.y ),
      new Integer( operation.width ),
      new Integer( operation.height ),
      new Integer( operation.arcWidth ),
      new Integer( operation.arcHeight ),
      new Boolean( operation.fill )
    };
    writer.call( GC_VAR, "drawRoundRectangle", args );
  }

  private void drawArc( final DrawArc operation ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( control );
    Object[] args = new Object[] {
      new Integer( operation.x ),
      new Integer( operation.y ),
      new Integer( operation.width ),
      new Integer( operation.height ),
      new Integer( operation.startAngle ),
      new Integer( operation.arcAngle ),
      new Boolean( operation.fill )
    };
    writer.call( GC_VAR, "drawArc", args );
  }

  private void drawPolyline( final DrawPolyline operation ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( control );
    Integer[] points = new Integer[ operation.points.length ];
    for( int i = 0; i < operation.points.length; i++ ) {
      points[ i ] = new Integer( operation.points[ i ] );
    }
    Object[] args = new Object[] {
      points,
      new Boolean( operation.close ),
      new Boolean( operation.fill )
    };
    writer.call( GC_VAR, "drawPolyline", args );
  }

  private void drawImage( final DrawImage operation ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( control );
    Object[] args = new Object[] {
      ResourceFactory.getImagePath( operation.image ),
      new Integer( operation.srcX ),
      new Integer( operation.srcY ),
      new Integer( operation.srcWidth ),
      new Integer( operation.srcHeight ),
      new Integer( operation.destX ),
      new Integer( operation.destY ),
      new Integer( operation.destWidth ),
      new Integer( operation.destHeight ),
      new Boolean( operation.simple )
    };
    writer.call( GC_VAR, "drawImage", args );
  }

  private void drawText( final DrawText operation ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( control );
    Object[] args = new Object[] {
      processText( operation.text, operation.flags ),
      new Integer( operation.x ),
      new Integer( operation.y ),
      new Boolean( ( operation.flags & SWT.DRAW_TRANSPARENT ) == 0 )
    };
    writer.call( GC_VAR, "drawText", args );
  }

  static String processText( final String text, final int flags ) {
    boolean drawMnemonic = ( flags & SWT.DRAW_MNEMONIC ) != 0;
    String result = WidgetLCAUtil.escapeText( text, drawMnemonic );
    String replacement = "";
    if( ( flags & SWT.DRAW_DELIMITER ) != 0 ) {
      replacement = "</br>";
    }
    result = EncodingUtil.replaceNewLines( result, replacement );
    replacement = "";
    if( ( flags & SWT.DRAW_TAB ) != 0 ) {
      replacement = "&nbsp;&nbsp;&nbsp;&nbsp;";
    }
    result = result.replaceAll( "\t", replacement );
    return result;
  }

  private void setProperty( final SetProperty operation ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( control );
    String jsProperty = null;
    switch( operation.id ) {
      case SetProperty.FOREGROUND:
        jsProperty = "foreground";
      break;
      case SetProperty.BACKGROUND:
        jsProperty = "background";
      break;
      case SetProperty.ALPHA:
        jsProperty = "alpha";
      break;
      case SetProperty.LINE_WIDTH:
        jsProperty = "lineWidth";
      break;
      case SetProperty.LINE_CAP:
        jsProperty = "lineCap";
      break;
      case SetProperty.LINE_JOIN:
        jsProperty = "lineJoin";
      break;
    }
    if( jsProperty != null ) {
      Object[] args = new Object[] { jsProperty, operation.value };
      writer.call( GC_VAR, "setProperty", args );
    }
  }

  private void setFont( final SetFont operation ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( control );
    Object[] args = new Object[] { "font", toCSSFont( operation.font ) };
    writer.call( GC_VAR, "setProperty", args );
  }

  private static String toCSSFont( final Font font ) {
    StringBuffer result = new StringBuffer();
    FontData fontData = font.getFontData()[ 0 ];
    if( ( fontData.getStyle() & SWT.ITALIC ) != 0 ) {
      result.append( "italic " );
    }
    if( ( fontData.getStyle() & SWT.BOLD ) != 0 ) {
      result.append( "bold " );
    }
    result.append( fontData.getHeight() );
    result.append( "px " );
    String name = fontData.getName().replaceAll( "\"", "'" );
    result.append( name );
    return result.toString();
  }

  public void initialize() throws IOException {
    if( !initialized ) {
      JSWriter writer = JSWriter.getWriterFor( control );
      writer.varAssignment( GC_VAR, "getGC" );
      Point size = control.getSize();
      Object[] args = new Object[] {
        new Integer( size.x ),
        new Integer( size.y ),
        toCSSFont( control.getFont() ),
        control.getBackground(),
        control.getForeground()
      };
      writer.call( GC_VAR, "init", args );
      initialized = true;
    }
  }
}
