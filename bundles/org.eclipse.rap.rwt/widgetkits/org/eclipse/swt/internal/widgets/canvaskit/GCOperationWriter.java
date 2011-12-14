/*******************************************************************************
 * Copyright (c) 2010, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.canvaskit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rwt.internal.protocol.IClientObject;
import org.eclipse.rwt.internal.util.EncodingUtil;
import org.eclipse.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.internal.graphics.FontUtil;
import org.eclipse.swt.internal.graphics.GCOperation;
import org.eclipse.swt.internal.graphics.GCOperation.DrawArc;
import org.eclipse.swt.internal.graphics.GCOperation.DrawImage;
import org.eclipse.swt.internal.graphics.GCOperation.DrawLine;
import org.eclipse.swt.internal.graphics.GCOperation.DrawPoint;
import org.eclipse.swt.internal.graphics.GCOperation.DrawPolyline;
import org.eclipse.swt.internal.graphics.GCOperation.DrawRectangle;
import org.eclipse.swt.internal.graphics.GCOperation.DrawRoundRectangle;
import org.eclipse.swt.internal.graphics.GCOperation.DrawText;
import org.eclipse.swt.internal.graphics.GCOperation.FillGradientRectangle;
import org.eclipse.swt.internal.graphics.GCOperation.SetProperty;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.widgets.Control;


final class GCOperationWriter {

  private final Control control;
  private boolean initialized;
  private ArrayList<Object[]> operations;
  private int lineWidth;
  private RGB foreground;
  private RGB background;

  GCOperationWriter( Control control ) {
    this.control = control;
  }

  void initialize() {
    if( !initialized ) {
      IClientObject gc = ClientObjectFactory.getForGC( control );
      Point size = control.getSize();
      lineWidth = 1;
      foreground = control.getForeground().getRGB();
      background = control.getBackground().getRGB();
      Map<String, Object> arg = new HashMap<String, Object>();
      arg.put( "width", new Integer( size.x ) );
      arg.put( "height", new Integer( size.y ) );
      arg.put( "font", toCSSFont( FontUtil.getData( control.getFont() ) ) );
      arg.put( "fillStyle", getColorValueAsArray( control.getBackground() ) );
      arg.put( "strokeStyle", getColorValueAsArray( control.getForeground() ) );
      gc.call( "init", arg );
      operations = new ArrayList<Object[]>();
      initialized = true;
    }
  }

  void write( GCOperation operation ) {
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
    } else {
      String name = operation.getClass().getName();
      throw new IllegalArgumentException( "Unsupported GCOperation: " + name );
    }
  }

  void render() {
    if( operations != null ) {
      Object[] array = operations.toArray();
      if( array.length > 0 ) {
        IClientObject gc = ClientObjectFactory.getForGC( control );
        Map<String, Object> arg = new HashMap<String, Object>();
        arg.put( "operations", array );
        gc.call( "draw", arg );
      }
      operations = null;
    }
  }

  private void drawLine( DrawLine operation ) {
    float offset = getOffset( false );
    addClientOperation( "beginPath" );
    addClientOperation( "moveTo", operation.x1 + offset, operation.y1 + offset );
    addClientOperation( "lineTo", operation.x2 + offset, operation.y2 + offset );
    addClientOperation( "stroke" );
  }

  private void drawPoint( DrawPoint operation ) {
    float x = operation.x;
    float y = operation.y;
    addClientOperation( "save" );
    addToOperations( "fillStyle", getColorValueAsArray( foreground ) );
    addClientOperation( "lineWidth", 1 );
    addClientOperation( "beginPath" );
    addClientOperation( "rect", x, y, 1, 1 );
    addClientOperation( "fill" );
    addClientOperation( "restore" );
  }

  private void drawRectangle( DrawRectangle operation ) {
    float offset = getOffset( operation.fill );
    float x = operation.x + offset;
    float y = operation.y + offset;
    float width = operation.width;
    float height = operation.height;
    addClientOperation( "beginPath" );
    addClientOperation( "rect", x, y, width, height );
    addClientOperation( operation.fill ? "fill" : "stroke" );
  }

  private void fillGradientRectangle( FillGradientRectangle operation )  {
    boolean vertical = operation.vertical;
    float width = operation.width;
    float height = operation.height;
    float x1 = operation.x;
    float y1 = operation.y;
    boolean swapColors = false;
    if( width < 0 ) {
      x1 += width;
      if( !vertical ) {
        swapColors  = true;
      }
    }
    if( height < 0 ) {
      y1 += height;
      if( vertical ) {
        swapColors = true;
      }
    }
    RGB startColor = swapColors ? background : foreground;
    RGB endColor = swapColors ? foreground : background ;
    float x2 = vertical ? x1 : x1 + Math.abs( width );
    float y2 = vertical ? y1 + Math.abs( height ) : y1;
    addClientOperation( "save" );
    addClientOperation( "createLinearGradient", x1, y1, x2, y2 );
    addToOperations( "addColorStop", new Integer( 0 ), getColorValueAsArray( startColor ) );
    addToOperations( "addColorStop", new Integer( 1 ), getColorValueAsArray( endColor ) );
    addClientOperation( "fillStyle", "linearGradient" );
    addClientOperation( "beginPath" );
    addClientOperation( "rect", x1, y1, width, height );
    addClientOperation( "fill" );
    addClientOperation( "restore" );
  }

  private void drawRoundRectangle( DrawRoundRectangle operation ) {
    // NOTE: the added "+1" in arcSize is the result of a visual comparison of RAP to SWT/Win.
    float offset = getOffset( operation.fill );
    float x = operation.x + offset;
    float y = operation.y + offset;
    float w = operation.width;
    float h = operation.height;
    float rx = ( ( float )operation.arcWidth ) / 2 + 1;
    float ry = ( ( float )operation.arcHeight ) / 2 + 1;
    addClientOperation( "beginPath" );
    addClientOperation( "moveTo", x, y + ry );
    addClientOperation( "lineTo", x, y + h - ry );
    addClientOperation( "quadraticCurveTo", x, y + h, x + rx, y + h );
    addClientOperation( "lineTo", x + w - rx, y + h );
    addClientOperation( "quadraticCurveTo", x + w, y + h, x + w, y + h - ry );
    addClientOperation( "lineTo", x + w, y + ry );
    addClientOperation( "quadraticCurveTo", x + w, y, x + w - rx, y );
    addClientOperation( "lineTo", x + rx, y );
    addClientOperation( "quadraticCurveTo", x, y, x, y + ry );
    addClientOperation( operation.fill ? "fill" : "stroke" );
  }

  private void drawArc( DrawArc operation ) {
    double factor = Math.PI / 180;
    float offset = getOffset( operation.fill );
    float x = operation.x + offset;
    float y = operation.y + offset;
    float width = operation.width;
    float height = operation.height;
    float startAngle = round( operation.startAngle * factor * -1, 4 );
    float arcAngle = round( operation.arcAngle * factor * -1, 4 );
    addClientOperation( "beginPath" );
    addToOperations( 
      "arc",
      new Float( x + width / 2 ),
      new Float( y + height / 2 ),
      new Float( width / 2 ),
      new Float( height / 2 ),
      new Float( startAngle ),
      new Float( startAngle + arcAngle ),
      Boolean.TRUE
    );
    addClientOperation( operation.fill ? "fill" : "stroke" );
  }

  private void drawPolyline( DrawPolyline operation ) {
    int[] points = operation.points;
    float offset = getOffset( operation.fill );
    addClientOperation( "beginPath" );
    for( int i = 0; i < points.length; i += 2 ) {
      if( i == 0 ) {
        addClientOperation( "moveTo", points[ i ] + offset, points[ i + 1 ] + offset );
      } else {
        addClientOperation( "lineTo", points[ i ] + offset, points[ i + 1 ] + offset );
      }
    }
    if( operation.close && points.length > 1 ) {
      addClientOperation( "lineTo", points[ 0 ] + offset, points[ 1 ] + offset );
    }
    addClientOperation( operation.fill ? "fill" : "stroke" );
  }

  private void drawImage( DrawImage operation ) {
    String path = ImageFactory.getImagePath( operation.image );
    if( operation.simple ) {
      addClientOperation( "drawImage", path, operation.destX, operation.destY );
    } else {
      addClientOperation( 
        "drawImage", 
        path, 
        operation.srcX, 
        operation.srcY,
        operation.srcWidth,
        operation.srcHeight,
        operation.destX,
        operation.destY,
        operation.destWidth,
        operation.destHeight
      );
    }
    
  }

  private void drawText( DrawText operation ) {
    boolean fill = ( operation.flags & SWT.DRAW_TRANSPARENT ) == 0;
    String text = processText( operation.text, operation.flags );
    addClientOperation( 
      fill ? "fillText" : "strokeText", 
      text,
      operation.x,
      operation.y
    );
  }

  static String processText( String text, int flags ) {
    boolean drawMnemonic = ( flags & SWT.DRAW_MNEMONIC ) != 0;
    String result = WidgetLCAUtil.escapeText( text, drawMnemonic );
    String replacement = "";
    if( ( flags & SWT.DRAW_DELIMITER ) != 0 ) {
      replacement = "<br/>";
    }
    result = EncodingUtil.replaceNewLines( result, replacement );
    replacement = "";
    if( ( flags & SWT.DRAW_TAB ) != 0 ) {
      replacement = "&nbsp;&nbsp;&nbsp;&nbsp;";
    }
    result = result.replaceAll( "\t", replacement );
    return result;
  }

  private void setProperty( SetProperty operation ) {
    String name;
    Object value;
    switch( operation.id ) {
      case SetProperty.FOREGROUND:
        name = "strokeStyle";
        foreground = ( RGB )operation.value;
        value = getColorValueAsArray( foreground );
      break;
      case SetProperty.BACKGROUND:
        name = "fillStyle";
        background = ( RGB )operation.value;
        value = getColorValueAsArray( background );
      break;
      case SetProperty.ALPHA:
        float alpha = ( ( Integer )operation.value ).floatValue();
        float globalAlpha = round( alpha / 255, 2 );
        name = "globalAlpha";
        value = new Float( globalAlpha ); 
      break;
      case SetProperty.LINE_WIDTH:
        name = "lineWidth";
        int width = ( ( Integer )operation.value ).intValue();
        width = width < 1 ? 1 : width;
        value = new Integer( width );
        lineWidth = width;
      break;
      case SetProperty.LINE_CAP:
        name = "lineCap";
        switch( ( ( Integer )operation.value ).intValue() ) {
          default:
          case SWT.CAP_FLAT:
            value = "butt";
          break;
          case SWT.CAP_ROUND:
            value = "round";
          break;
          case SWT.CAP_SQUARE:
            value = "square";
          break;
        }
      break;
      case SetProperty.LINE_JOIN:
        name = "lineJoin";
        switch( ( ( Integer )operation.value ).intValue() ) {
          default:
          case SWT.JOIN_BEVEL:
            value = "bevel";
            break;
          case SWT.JOIN_MITER:
            value = "miter";
            break;
          case SWT.JOIN_ROUND:
            value = "round";
            break;
        }
      break;
      case SetProperty.FONT:
        name = "font";
        value = toCSSFont( ( FontData )operation.value );
      break;
      default:
        String msg = "Unsupported operation id: " + operation.id;
        throw new RuntimeException( msg );
    }
    addToOperations( name, value );
  }

  private void addToOperations( Object... args ) {
    operations.add( args );
  }

  private void addClientOperation( String name, float... args ) {
    addClientOperation( new Object[]{ name }, args );
  }
  
  private void addClientOperation( String name, String argText, float... args ) {
    addClientOperation( new Object[]{ name, argText }, args );
  }
  
  private void addClientOperation( Object[] objects, float[] numbers ) {
    Object[] operation = new Object[ objects.length + numbers.length ];
    for( int i = 0; i < objects.length; i++ ) {
      operation[ i ] = objects[ i ];
    }
    for( int i = objects.length; i < operation.length; i++ ) {
      operation[ i ] = new Float( numbers[ i - objects.length ] );
    }
    operations.add( operation );
  }
  
  private static String toCSSFont( FontData fontData ) {
    StringBuilder result = new StringBuilder();
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

  private static int[] getColorValueAsArray( Color color ) {
    RGB rgb = color == null ? new RGB( 0, 0, 0 ) : color.getRGB();
    return getColorValueAsArray( rgb );
  }

  private static int[] getColorValueAsArray( RGB rgb ) {
    int[] result = new int[ 3 ];
    result[ 0 ] = rgb.red;
    result[ 1 ] = rgb.green;
    result[ 2 ] = rgb.blue;
    return result;
  }
  
  private float getOffset( boolean fill ) {
    float result = 0;
    if( !fill && lineWidth % 2 != 0 ) {
      result = ( float )0.5;
    }
    return result;
  }
  
  float round( double value, int decimals ) {
    int factor = ( int )Math.pow( 10, decimals );
    return ( ( float )Math.round( factor * value ) ) / factor;
  }
  
}
