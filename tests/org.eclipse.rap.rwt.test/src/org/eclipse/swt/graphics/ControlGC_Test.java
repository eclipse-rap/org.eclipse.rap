/*******************************************************************************
 * Copyright (c) 2011 Rüdiger Herrmann and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Rüdiger Herrmann - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.graphics;

import java.util.Arrays;

import org.eclipse.rwt.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.graphics.*;
import org.eclipse.swt.internal.graphics.GCOperation.*;
import org.eclipse.swt.widgets.*;

import junit.framework.TestCase;


public class ControlGC_Test extends TestCase {
  private Display display;
  private Canvas canvas;
  private GC gc;

  public void testInitialValues() {
    assertEquals( canvas.getFont(), gc.getFont() );
    assertEquals( canvas.getBackground(), gc.getBackground() );
    assertEquals( canvas.getForeground(), gc.getForeground() );
  }


  public void testSetFont() {
    Font font = createFont();
    gc.setFont( font );
    GCOperation[] gcOperations = getGCOperations( gc );
    SetFont operation = ( SetFont )gcOperations[ 0 ];
    assertEquals( font, operation.font );
  }
  
  public void testSetFontWithNullFont() {
    Font font = createFont();
    gc.setFont( font );
    gc.setFont( null );
    assertEquals( display.getSystemFont(), gc.getFont() );
  }
  
  public void testSetFontWithSameFont() {
    Font font = createFont();
    gc.setFont( font );
    IGCAdapter adapter = getGCAdapter( gc );
    adapter.clearGCOperations();
    gc.setFont( font );
    GCOperation[] gcOperations = getGCOperations( gc );
    assertEquals( 0, gcOperations.length );
  }
  
  public void testSetBackground() {
    Color color = createColor();
    gc.setBackground( color );
    GCOperation[] gcOperations = getGCOperations( gc );
    SetProperty operation = ( SetProperty )gcOperations[ 0 ];
    assertEquals( SetProperty.BACKGROUND, operation.id );
    assertEquals( color, operation.value );
  }

  public void testSetForeground() {
    Shell shell = new Shell( display );
    GC gc = new GC( shell );
    Color color = createColor();
    gc.setForeground( color );
    GCOperation[] gcOperations = getGCOperations( gc );
    SetProperty operation = ( SetProperty )gcOperations[ 0 ];
    assertEquals( SetProperty.FOREGROUND, operation.id );
    assertEquals( color, operation.value );
  }
  
  public void testGetGCAdapterForCanvasWidget() {
    IGCAdapter adapter1 = getGCAdapter( gc );
    assertNotNull( adapter1 );
    IGCAdapter adapter2 = getGCAdapter( gc );
    assertSame( adapter2, adapter1 );
  }

  public void testGetGCAdapterForNonCanvasWidget() {
    Shell shell = new Shell( display );
    Button button = new Button( shell, SWT.NONE );
    GC gc = new GC( button );
    assertNull( getGCAdapter( gc ) );
  }
  
  public void testDrawOperationWithNonCanvas() {
    gc.drawLine( 1, 2, 3, 4 );
    // This test has no assert. Ensures that no NPE is thrown.
  }

  public void testSetAlpha() {
    gc.setAlpha( 123 );
    assertEquals( 123, gc.getAlpha() );
    GCOperation[] gcOperations = getGCOperations( gc );
    SetProperty operation = ( SetProperty )gcOperations[ 0 ];
    assertEquals( SetProperty.ALPHA, operation.id );
    assertEquals( new Integer( 123 ), operation.value );
  }
  
  public void testSetAlphaWithSameValue() {
    gc.setAlpha( 123 );
    getGCAdapter( gc ).clearGCOperations();
    gc.setAlpha( 123 );
    assertEquals( 0, getGCOperations( gc ).length );
  }

  public void testSetAlphaWithInvalidValue() {
    gc.setAlpha( 777 );
    GCOperation[] gcOperations = getGCOperations( gc );
    assertEquals( 0, gcOperations.length );
  }
  
  public void testSetLineWidth() {
    gc.setLineWidth( 5 );
    assertEquals( 5, gc.getLineWidth() );
    GCOperation[] gcOperations = getGCOperations( gc );
    SetProperty operation = ( SetProperty )gcOperations[ 0 ];
    assertEquals( SetProperty.LINE_WIDTH, operation.id );
    assertEquals( new Integer( 5 ), operation.value );
  }
  
  public void testSetLineCap() {
    gc.setLineCap( SWT.CAP_ROUND );
    assertEquals( SWT.CAP_ROUND, gc.getLineCap() );
    GCOperation[] gcOperations = getGCOperations( gc );
    SetProperty operation = ( SetProperty )gcOperations[ 0 ];
    assertEquals( SetProperty.LINE_CAP, operation.id );
    assertEquals( new Integer( SWT.CAP_ROUND ), operation.value );
  }
  
  public void testSetLineCapWithUnchangeValue() {
    gc.setLineCap( SWT.CAP_ROUND );
    getGCAdapter( gc ).clearGCOperations();
    gc.setLineCap( SWT.CAP_ROUND );
    assertEquals( 0, getGCOperations( gc ).length );
  }
  
  public void testSetLineJoin() {
    gc.setLineJoin( SWT.JOIN_ROUND );
    assertEquals( SWT.JOIN_ROUND, gc.getLineJoin() );
    GCOperation[] gcOperations = getGCOperations( gc );
    SetProperty operation = ( SetProperty )gcOperations[ 0 ];
    assertEquals( SetProperty.LINE_JOIN, operation.id );
    assertEquals( new Integer( SWT.JOIN_ROUND ), operation.value );
  }
  
  public void testSetLineJoinWithUnchangedValue() {
    gc.setLineJoin( SWT.JOIN_ROUND );
    getGCAdapter( gc ).clearGCOperations();
    gc.setLineJoin( SWT.JOIN_ROUND );
    assertEquals( 0, getGCOperations( gc ).length );
  }
  
  public void testSetLineAttributes() {
    LineAttributes attributes
      = new LineAttributes( 5, SWT.CAP_ROUND, SWT.JOIN_BEVEL );
    gc.setLineAttributes( attributes );
    GCOperation[] gcOperations = getGCOperations( gc );
    SetProperty operation = ( SetProperty )gcOperations[ 0 ];
    assertEquals( SetProperty.LINE_WIDTH, operation.id );
    assertEquals( new Integer( 5 ), operation.value );
    operation = ( SetProperty )gcOperations[ 1 ];
    assertEquals( SetProperty.LINE_CAP, operation.id );
    assertEquals( new Integer( SWT.CAP_ROUND ), operation.value );
    operation = ( SetProperty )gcOperations[ 2 ];
    assertEquals( SetProperty.LINE_JOIN, operation.id );
    assertEquals( new Integer( SWT.JOIN_BEVEL ), operation.value );
  }
  
  public void testDrawLine() {
    gc.drawLine( 1, 2, 3, 4 );
    GCOperation[] gcOperations = getGCOperations( gc );
    DrawLine operation = ( DrawLine )gcOperations[ 0 ];
    assertEquals( 1, operation.x1 );
    assertEquals( 2, operation.y1 );
    assertEquals( 3, operation.x2 );
    assertEquals( 4, operation.y2 );
  }

  public void testDrawPoint() {
    gc.drawPoint( 1, 2 );
    GCOperation[] gcOperations = getGCOperations( gc );
    DrawPoint operation = ( DrawPoint )gcOperations[ 0 ];
    assertEquals( 1, operation.x );
    assertEquals( 2, operation.y );
  }

  public void testDrawRectangle() {
    gc.drawRectangle( 1, 2, 3, 4 );
    GCOperation[] gcOperations = getGCOperations( gc );
    DrawRectangle operation = ( DrawRectangle )gcOperations[ 0 ];
    assertEquals( 1, operation.x );
    assertEquals( 2, operation.y );
    assertEquals( 3, operation.width );
    assertEquals( 4, operation.height );
    assertFalse( operation.fill );
  }
  
  public void testDrawRectangleWithZeroWidthAndHeight() {
    gc.drawRectangle( 1, 2, 0, 0 );
    GCOperation[] gcOperations = getGCOperations( gc );
    assertEquals( 0, gcOperations.length );
  }
  
  public void testDrawFocus() {
    gc.drawFocus( 1, 2, 3, 4 );
    GCOperation[] gcOperations = getGCOperations( gc );
    DrawRectangle operation = ( DrawRectangle )gcOperations[ 0 ];
    assertEquals( 1, operation.x );
    assertEquals( 2, operation.y );
    assertEquals( 3, operation.width );
    assertEquals( 4, operation.height );
    assertFalse( operation.fill );
  }

  public void testFillRectangle() {
    gc.fillRectangle( 1, 2, 3, 4 );
    GCOperation[] gcOperations = getGCOperations( gc );
    DrawRectangle operation = ( DrawRectangle )gcOperations[ 0 ];
    assertEquals( 1, operation.x );
    assertEquals( 2, operation.y );
    assertEquals( 3, operation.width );
    assertEquals( 4, operation.height );
    assertTrue( operation.fill );
  }
  
  public void testFillGradientRectangle() {
    gc.fillGradientRectangle( 1, 2, 3, 4, true );
    gc.fillGradientRectangle( 5, 6, 7, 8, false );
    GCOperation[] gcOperations = getGCOperations( gc );
    FillGradientRectangle operation 
      = ( FillGradientRectangle )gcOperations[ 0 ];
    assertEquals( 1, operation.x );
    assertEquals( 2, operation.y );
    assertEquals( 3, operation.width );
    assertEquals( 4, operation.height );
    assertTrue( operation.vertical );
    assertTrue( operation.fill );
    operation = ( FillGradientRectangle )gcOperations[ 1 ];
    assertEquals( 5, operation.x );
    assertEquals( 6, operation.y );
    assertEquals( 7, operation.width );
    assertEquals( 8, operation.height );
    assertFalse( operation.vertical );
    assertTrue( operation.fill );
  }

  public void testDrawRoundRectangle() {
    gc.drawRoundRectangle( 1, 2, 3, 4, 5, 6 );
    IGCAdapter adapter = getGCAdapter( gc );
    GCOperation[] gcOperations = adapter.getGCOperations();
    DrawRoundRectangle operation = ( DrawRoundRectangle )gcOperations[ 0 ];
    assertEquals( 1, operation.x );
    assertEquals( 2, operation.y );
    assertEquals( 3, operation.width );
    assertEquals( 4, operation.height );
    assertEquals( 5, operation.arcWidth );
    assertEquals( 6, operation.arcHeight );
    assertFalse( operation.fill );
  }

  public void testDrawRoundRectangleWithZeroArcWidth() {
    gc.drawRoundRectangle( 1, 2, 3, 4, 0, 6 );
    GCOperation[] gcOperations = getGCOperations( gc );
    DrawRectangle operation = ( DrawRectangle )gcOperations[ 0 ];
    assertEquals( 1, operation.x );
    assertEquals( 2, operation.y );
    assertEquals( 3, operation.width );
    assertEquals( 4, operation.height );
    assertFalse( operation.fill );
  }

  public void testDrawRoundRectangleWithZeroArcHeight() {
    gc.drawRoundRectangle( 1, 2, 3, 4, 5, 0 );
    GCOperation[] gcOperations = getGCOperations( gc );
    DrawRectangle operation = ( DrawRectangle )gcOperations[ 0 ];
    assertEquals( 1, operation.x );
    assertEquals( 2, operation.y );
    assertEquals( 3, operation.width );
    assertEquals( 4, operation.height );
    assertFalse( operation.fill );
  }
  
  public void testFillRoundRectangle() {
    gc.fillRoundRectangle( 1, 2, 3, 4, 5, 6 );
    GCOperation[] gcOperations = getGCOperations( gc );
    DrawRoundRectangle operation = ( DrawRoundRectangle )gcOperations[ 0 ];
    assertEquals( 1, operation.x );
    assertEquals( 2, operation.y );
    assertEquals( 3, operation.width );
    assertEquals( 4, operation.height );
    assertEquals( 5, operation.arcWidth );
    assertEquals( 6, operation.arcHeight );
    assertTrue( operation.fill );
  }
  
  public void testFillRoundRectangleWithZeroArcWidth() {
    gc.fillRoundRectangle( 1, 2, 3, 4, 0, 6 );
    GCOperation[] gcOperations = getGCOperations( gc );
    DrawRectangle operation = ( DrawRectangle )gcOperations[ 0 ];
    assertEquals( 1, operation.x );
    assertEquals( 2, operation.y );
    assertEquals( 3, operation.width );
    assertEquals( 4, operation.height );
    assertTrue( operation.fill );
  }

  public void testFillRoundRectangleWithZeroArcHeight() {
    gc.fillRoundRectangle( 1, 2, 3, 4, 5, 0 );
    GCOperation[] gcOperations = getGCOperations( gc );
    DrawRectangle operation = ( DrawRectangle )gcOperations[ 0 ];
    assertEquals( 1, operation.x );
    assertEquals( 2, operation.y );
    assertEquals( 3, operation.width );
    assertEquals( 4, operation.height );
    assertTrue( operation.fill );
  }
  
  public void testDrawArc() {
    gc.drawArc( 1, 2, 3, 4, 5, 6 );
    IGCAdapter adapter = getGCAdapter( gc );
    GCOperation[] gcOperations = adapter.getGCOperations();
    DrawArc operation = ( DrawArc )gcOperations[ 0 ];
    assertEquals( 1, operation.x );
    assertEquals( 2, operation.y );
    assertEquals( 3, operation.width );
    assertEquals( 4, operation.height );
    assertEquals( 5, operation.startAngle );
    assertEquals( 6, operation.arcAngle );
    assertFalse( operation.fill );
  }

  public void testDrawArcWithZeroWidth() {
    gc.drawArc( 1, 2, 0, 5, 5, 5 );
    GCOperation[] gcOperations = getGCOperations( gc );
    assertEquals( 0, gcOperations.length );
  }

  public void testDrawArcWithZeroHeight() {
    gc.drawArc( 1, 2, 3, 0, 5, 5 );
    GCOperation[] gcOperations = getGCOperations( gc );
    assertEquals( 0, gcOperations.length );
  }
  
  public void testDrawArcWithZeroArcAngle() {
    gc.drawArc( 1, 2, 3, 4, 5, 0 );
    GCOperation[] gcOperations = getGCOperations( gc );
    assertEquals( 0, gcOperations.length );
  }
  
  public void testFillArc() {
    gc.fillArc( 1, 2, 3, 4, 5, 6 );
    IGCAdapter adapter = getGCAdapter( gc );
    GCOperation[] gcOperations = adapter.getGCOperations();
    DrawArc operation = ( DrawArc )gcOperations[ 0 ];
    assertEquals( 1, operation.x );
    assertEquals( 2, operation.y );
    assertEquals( 3, operation.width );
    assertEquals( 4, operation.height );
    assertEquals( 5, operation.startAngle );
    assertEquals( 6, operation.arcAngle );
  }

  public void testFillArcWithNegativeWidthAndHeight() {
    gc.fillArc( 1, 2, -3, -4, 5, 6 );
    IGCAdapter adapter = getGCAdapter( gc );
    GCOperation[] gcOperations = adapter.getGCOperations();
    DrawArc operation = ( DrawArc )gcOperations[ 0 ];
    assertEquals( -2, operation.x );
    assertEquals( -2, operation.y );
    assertEquals( 3, operation.width );
    assertEquals( 4, operation.height );
    assertEquals( 5, operation.startAngle );
    assertEquals( 6, operation.arcAngle );
    assertTrue( operation.fill );
  }
  
  public void testFillArcWithZeroWidth() {
    gc.fillArc( 1, 2, 0, 4, 5, 6 );
    IGCAdapter adapter = getGCAdapter( gc );
    GCOperation[] gcOperations = adapter.getGCOperations();
    assertEquals( 0, gcOperations.length );
  }
  
  public void testFillArcWithZeroHeight() {
    gc.fillArc( 1, 2, 3, 0, 5, 6 );
    IGCAdapter adapter = getGCAdapter( gc );
    GCOperation[] gcOperations = adapter.getGCOperations();
    assertEquals( 0, gcOperations.length );
  }
  
  public void testFillArcWithZeroArcAngle() {
    gc.fillArc( 1, 2, 3, 4, 5, 0 );
    IGCAdapter adapter = getGCAdapter( gc );
    GCOperation[] gcOperations = adapter.getGCOperations();
    assertEquals( 0, gcOperations.length );
  }

  public void testFillPolygon() {
    Control control = new Shell( display );
    GC gc = new GC( control );
    int[] pointArray = new int[] { 1, 2, 3, 4 };
    gc.fillPolygon( pointArray );
    GCOperation[] gcOperations = getGCOperations( gc );
    DrawPolyline operation = ( DrawPolyline )gcOperations[ 0 ];
    assertTrue( Arrays.equals( pointArray, operation.points ) );
    assertTrue( operation.close );
    assertTrue( operation.fill );
  }

  public void testDrawPolyline() {
    Control control = new Shell( display );
    GC gc = new GC( control );
    int[] pointArray = new int[] { 1, 2, 3, 4 };
    gc.drawPolyline( pointArray );
    GCOperation[] gcOperations = getGCOperations( gc );
    DrawPolyline operation = ( DrawPolyline )gcOperations[ 0 ];
    assertTrue( Arrays.equals( pointArray, operation.points ) );
    assertFalse( operation.close );
    assertFalse( operation.fill );
  }

  public void testDrawOval() {
    gc.drawOval( 1, 2, 3, 4 );
    IGCAdapter adapter = getGCAdapter( gc );
    GCOperation[] gcOperations = adapter.getGCOperations();
    DrawArc operation = ( DrawArc )gcOperations[ 0 ];
    assertEquals( 1, operation.x );
    assertEquals( 2, operation.y );
    assertEquals( 3, operation.width );
    assertEquals( 4, operation.height );
    assertEquals( 0, operation.startAngle );
    assertEquals( 360, operation.arcAngle );
    assertFalse( operation.fill );
  }

  public void testDrawOvalWithZeroWidthAndHeight() {
    gc.drawOval( 1, 2, 0, 0 );
    IGCAdapter adapter = getGCAdapter( gc );
    GCOperation[] gcOperations = adapter.getGCOperations();
    assertEquals( 0, gcOperations.length );
  }
  
  public void testFillOval() {
    gc.fillOval( 1, 2, 3, 4 );
    IGCAdapter adapter = getGCAdapter( gc );
    GCOperation[] gcOperations = adapter.getGCOperations();
    DrawArc operation = ( DrawArc )gcOperations[ 0 ];
    assertEquals( 1, operation.x );
    assertEquals( 2, operation.y );
    assertEquals( 3, operation.width );
    assertEquals( 4, operation.height );
    assertEquals( 0, operation.startAngle );
    assertEquals( 360, operation.arcAngle );
    assertTrue( operation.fill );
    adapter.clearGCOperations();
  }

  public void testDrawPolygon() {
    int[] pointArray = new int[] { 1, 2, 3, 4 };
    gc.drawPolygon( pointArray );
    GCOperation[] gcOperations = getGCOperations( gc );
    DrawPolyline operation = ( DrawPolyline )gcOperations[ 0 ];
    assertTrue( Arrays.equals( pointArray, operation.points ) );
    assertTrue( operation.close );
    assertFalse( operation.fill );
  }

  public void testDrawStringWithEmptyString() {
    gc.drawString( "", 10, 10, false );
    GCOperation[] gcOperations = getGCOperations( gc );
    assertEquals( 0, gcOperations.length );
  }
  
  public void testDrawImage() {
    Image image = display.getSystemImage( SWT.ICON_INFORMATION );
    gc.drawImage( image, 1, 2 );
    GCOperation[] gcOperations = getGCOperations( gc );
    DrawImage operation = ( DrawImage )gcOperations[ 0 ];
    assertSame( image, operation.image );
    assertEquals( 0, operation.srcX );
    assertEquals( 0, operation.srcY );
    assertEquals( -1, operation.srcWidth );
    assertEquals( -1, operation.srcHeight );
    assertEquals( 1, operation.destX );
    assertEquals( 2, operation.destY );
    assertEquals( -1, operation.destWidth );
    assertEquals( -1, operation.destHeight );
    assertTrue( operation.simple );
  }
  
  public void testDrawImageCopy() {
    Image image = display.getSystemImage( SWT.ICON_INFORMATION );
    gc.drawImage( image, 1, 2, 3, 4, 5, 6, 7, 8 );
    GCOperation[] gcOperations = getGCOperations( gc );
    DrawImage operation = ( DrawImage )gcOperations[ 0 ];
    assertSame( image, operation.image );
    assertEquals( 1, operation.srcX );
    assertEquals( 2, operation.srcY );
    assertEquals( 3, operation.srcWidth );
    assertEquals( 4, operation.srcHeight );
    assertEquals( 5, operation.destX );
    assertEquals( 6, operation.destY );
    assertEquals( 7, operation.destWidth );
    assertEquals( 8, operation.destHeight );
    assertFalse( operation.simple );
  }
  
  public void testDrawText() {
    gc.drawText( "text", 10, 10, SWT.DRAW_TRANSPARENT );
    GCOperation[] gcOperations = getGCOperations( gc );
    DrawText operation = ( DrawText )gcOperations[ 0 ];
    assertEquals( "text", operation.text );
    assertEquals( 10, operation.x );
    assertEquals( 10, operation.y );
    assertEquals( SWT.DRAW_TRANSPARENT, operation.flags );
  }

  public void testDrawTextWithEmptyString() {
    gc.drawText( "", 10, 10, SWT.DRAW_TRANSPARENT );
    GCOperation[] gcOperations = getGCOperations( gc );
    assertEquals( 0, gcOperations.length );
  }

  public void testDrawString() {
    gc.drawString( "text", 10, 10, true );
    GCOperation[] gcOperations = getGCOperations( gc );
    DrawText operation = ( DrawText )gcOperations[ 0 ];
    assertEquals( "text", operation.text );
    assertEquals( 10, operation.x );
    assertEquals( 10, operation.y );
    assertEquals( SWT.DRAW_TRANSPARENT, operation.flags );
  }

  public void testGetClipping() {
    canvas.setSize( 100, 100 );
    GC gc = new GC( canvas );
    Rectangle clipping = gc.getClipping();
    assertEquals( new Rectangle( 0, 0, 100, 100 ), clipping );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    Shell shell = new Shell( display );
    canvas = new Canvas( shell, SWT.NONE );
    gc = new GC( canvas );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  private static GCOperation[] getGCOperations( final GC gc ) {
    GCAdapter adapter = getGCAdapter( gc );
    return adapter.getGCOperations();
  }

  private static GCAdapter getGCAdapter( GC gc ) {
    GCAdapter result = null;
    GCDelegate delegate = gc.getGCDelegate();
    if( delegate instanceof ControlGC ) {
      result = ( ( ControlGC )delegate ).getGCAdapter();
    }
    return result;
  }

  private Font createFont() {
    return new Font( display, "font-name", 11, SWT.NORMAL );
  }

  private Color createColor() {
    return new Color( display, 1, 2, 3 );
  }
}
