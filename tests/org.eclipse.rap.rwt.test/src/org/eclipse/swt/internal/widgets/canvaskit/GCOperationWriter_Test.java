/*******************************************************************************
 * Copyright (c) 2010, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.canvaskit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.rap.rwt.graphics.Graphics;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CallOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.internal.graphics.GCOperation;
import org.eclipse.swt.internal.graphics.IGCAdapter;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GCOperationWriter_Test {

  private Display display;
  private Canvas canvas;
  private GC gc;

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakeResponseWriter();
    display = new Display();
    Shell control = new Shell( display );
    canvas = new Canvas( control, SWT.NONE );
    gc = new GC( canvas );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testSetLineWidth() {
    gc.setLineWidth( 13 );

    JSONArray ops = getGCOperations( canvas );
    assertEquals( "\"lineWidth\",13", getOperation( 0, ops ) );
  }

  @Test
  public void testSetLineWidthZero() {
    gc.setLineWidth( 10 );
    gc.setLineWidth( 0 );

    JSONArray ops = getGCOperations( canvas );
    assertEquals( "\"lineWidth\",10", getOperation( 0, ops ) );
    assertEquals( "\"lineWidth\",1", getOperation( 1, ops ) );
  }

  @Test
  public void testForeground() {
    gc.setForeground( new Color( display, 155, 11, 24 ) );

    JSONArray ops = getGCOperations( canvas );
    assertEquals( "\"strokeStyle\",[155,11,24,255]", getOperation( 0, ops ) );
  }

  @Test
  public void testBackground() {
    gc.setBackground( new Color( display, 155, 11, 24 ) );

    JSONArray ops = getGCOperations( canvas );
    assertEquals( "\"fillStyle\",[155,11,24,255]", getOperation( 0, ops ) );
  }

  @Test
  public void testAlpha() {
    gc.setAlpha( 100 );

    JSONArray ops = getGCOperations( canvas );
    assertEquals( "\"globalAlpha\",0.39", getOperation( 0, ops ) );
  }

  @Test
  public void testLineCapFlat() {
    gc.setLineCap( SWT.CAP_ROUND );
    gc.setLineCap( SWT.CAP_FLAT );

    JSONArray ops = getGCOperations( canvas );
    assertEquals( "\"lineCap\",\"butt\"", getOperation( 1, ops ) );
  }

  @Test
  public void testLineCapRound() {
    gc.setLineCap( SWT.CAP_ROUND );

    JSONArray ops = getGCOperations( canvas );
    assertEquals( "\"lineCap\",\"round\"", getOperation( 0, ops ) );
  }

  @Test
  public void testLineCapSquare() {
    gc.setLineCap( SWT.CAP_SQUARE );

    JSONArray ops = getGCOperations( canvas );
    assertEquals( "\"lineCap\",\"square\"", getOperation( 0, ops ) );
  }

  @Test
  public void testLineJoinBevel() {
    gc.setLineJoin( SWT.JOIN_BEVEL );

    JSONArray ops = getGCOperations( canvas );
    assertEquals( "\"lineJoin\",\"bevel\"", getOperation( 0, ops ) );
  }

  @Test
  public void testLineJoinMiter() {
    gc.setLineJoin( SWT.JOIN_ROUND );
    gc.setLineJoin( SWT.JOIN_MITER );

    JSONArray ops = getGCOperations( canvas );
    assertEquals( "\"lineJoin\",\"miter\"", getOperation( 1, ops ) );
  }

  @Test
  public void testLineJoinRound() {
    gc.setLineJoin( SWT.JOIN_ROUND );

    JSONArray ops = getGCOperations( canvas );
    assertEquals( "\"lineJoin\",\"round\"", getOperation( 0, ops ) );
  }

  @Test
  public void testFont() {
    gc.setFont( new Font( display, "Arial", 12, SWT.BOLD ) );

    JSONArray ops = getGCOperations( canvas );
    assertEquals( "\"font\",[[\"Arial\"],12,true,false]", getOperation( 0, ops ) );
  }

  @Test
  public void testDrawLine() {
    gc.setLineWidth( 2 );
    gc.drawLine( 10, 11, 20, 21 );

    JSONArray ops = getGCOperations( canvas );
    assertEquals( "\"beginPath\"", getOperation( 1, ops ) );
    assertEquals( "\"moveTo\",10,11", getOperation( 2, ops ) );
    assertEquals( "\"lineTo\",20,21", getOperation( 3, ops ) );
    assertEquals( "\"stroke\"", getOperation( 4, ops ) );
  }

  @Test
  public void testDrawLineOffset() {
    gc.drawLine( 10, 11, 20, 21 );

    JSONArray ops = getGCOperations( canvas );
    assertEquals( "\"beginPath\"", getOperation( 0, ops ) );
    assertEquals( "\"moveTo\",10.5,11.5", getOperation( 1, ops ) );
    assertEquals( "\"lineTo\",20.5,21.5", getOperation( 2, ops ) );
    assertEquals( "\"stroke\"", getOperation( 3, ops ) );
  }

  @Test
  public void testDrawPoint() {
    gc.setForeground( new Color( display, 255, 0, 7 ) );
    gc.drawPoint( 27, 44 );

    JSONArray ops = getGCOperations( canvas );
    assertEquals( "\"strokeStyle\",[255,0,7,255]", getOperation( 0, ops ) );
    assertEquals( "\"save\"", getOperation( 1, ops ) );
    assertEquals( "\"fillStyle\",[255,0,7,255]", getOperation( 2, ops ) );
    assertEquals( "\"lineWidth\",1", getOperation( 3, ops ) );
    assertEquals( "\"beginPath\"", getOperation( 4, ops ) );
    assertEquals( "\"rect\",27,44,1,1", getOperation( 5, ops ) );
    assertEquals( "\"fill\"", getOperation( 6, ops ) );
    assertEquals( "\"restore\"", getOperation( 7, ops ) );
  }

  @Test
  public void testDrawRectangle() {
    gc.setLineWidth( 2 );
    gc.drawRectangle( 10, 20, 55, 56 );

    JSONArray ops = getGCOperations( canvas );
    assertEquals( "\"beginPath\"", getOperation( 1, ops ) );
    assertEquals( "\"rect\",10,20,55,56", getOperation( 2, ops ) );
    assertEquals( "\"stroke\"", getOperation( 3, ops ) );
  }

  @Test
  public void testFillRectangle() {
    gc.setLineWidth( 2 );
    gc.fillRectangle( 10, 20, 55, 56 );

    JSONArray ops = getGCOperations( canvas );
    assertEquals( "\"beginPath\"", getOperation( 1, ops ) );
    assertEquals( "\"rect\",10,20,55,56", getOperation( 2, ops ) );
    assertEquals( "\"fill\"", getOperation( 3, ops ) );
  }

  @Test
  public void testDrawRectangleOffset() {
    gc.setLineWidth( 1 );
    gc.drawRectangle( 10, 20, 55, 56 );

    JSONArray ops = getGCOperations( canvas );
    assertEquals( "\"beginPath\"", getOperation( 1, ops ) );
    assertEquals( "\"rect\",10.5,20.5,55,56", getOperation( 2, ops ) );
    assertEquals( "\"stroke\"", getOperation( 3, ops ) );
  }

  @Test
  public void testDrawPolyLine() {
    gc.setLineWidth( 2 );
    gc.drawPolyline( new int[]{ 10, 20, 30, 40, 50, 60, 90, 100 } );

    JSONArray ops = getGCOperations( canvas );
    assertEquals( "\"beginPath\"", getOperation( 1, ops ) );
    assertEquals( "\"moveTo\",10,20", getOperation( 2, ops ) );
    assertEquals( "\"lineTo\",30,40", getOperation( 3, ops ) );
    assertEquals( "\"lineTo\",50,60", getOperation( 4, ops ) );
    assertEquals( "\"lineTo\",90,100", getOperation( 5, ops ) );
    assertEquals( "\"stroke\"", getOperation( 6, ops ) );
  }

  @Test
  public void testDrawPolygon() {
    gc.setLineWidth( 2 );
    gc.drawPolygon( new int[]{ 10, 20, 30, 40, 50, 60, 90, 100 } );

    JSONArray ops = getGCOperations( canvas );
    assertEquals( "\"beginPath\"", getOperation( 1, ops ) );
    assertEquals( "\"moveTo\",10,20", getOperation( 2, ops ) );
    assertEquals( "\"lineTo\",30,40", getOperation( 3, ops ) );
    assertEquals( "\"lineTo\",50,60", getOperation( 4, ops ) );
    assertEquals( "\"lineTo\",90,100", getOperation( 5, ops ) );
    assertEquals( "\"lineTo\",10,20", getOperation( 6, ops ) );
    assertEquals( "\"stroke\"", getOperation( 7, ops ) );
  }

  @Test
  public void testfillPolygon() {
    gc.setLineWidth( 2 );
    gc.fillPolygon( new int[]{ 10, 20, 30, 40, 50, 60, 90, 100 } );

    JSONArray ops = getGCOperations( canvas );
    assertEquals( "\"beginPath\"", getOperation( 1, ops ) );
    assertEquals( "\"moveTo\",10,20", getOperation( 2, ops ) );
    assertEquals( "\"lineTo\",30,40", getOperation( 3, ops ) );
    assertEquals( "\"lineTo\",50,60", getOperation( 4, ops ) );
    assertEquals( "\"lineTo\",90,100", getOperation( 5, ops ) );
    assertEquals( "\"lineTo\",10,20", getOperation( 6, ops ) );
    assertEquals( "\"fill\"", getOperation( 7, ops ) );
  }

  @Test
  public void testDrawPolyLineOffset() {
    gc.drawPolyline( new int[]{ 10, 20, 30, 40, 50, 60, 90, 100 } );

    JSONArray ops = getGCOperations( canvas );
    assertEquals( "\"beginPath\"", getOperation( 0, ops ) );
    assertEquals( "\"moveTo\",10.5,20.5", getOperation( 1, ops ) );
    assertEquals( "\"lineTo\",30.5,40.5", getOperation( 2, ops ) );
    assertEquals( "\"lineTo\",50.5,60.5", getOperation( 3, ops ) );
    assertEquals( "\"lineTo\",90.5,100.5", getOperation( 4, ops ) );
    assertEquals( "\"stroke\"", getOperation( 5, ops ) );
  }

  @Test
  public void testDrawRoundRect() {
    gc.setLineWidth( 2 );
    gc.drawRoundRectangle( 10, 20, 100, 200, 1, 3 );

    JSONArray ops = getGCOperations( canvas );
    assertEquals( "\"beginPath\"", getOperation( 1, ops ) );
    assertEquals( "\"moveTo\",10,22.5", getOperation( 2, ops ) );
    assertEquals( "\"lineTo\",10,217.5", getOperation( 3, ops ) );
    assertEquals( "\"quadraticCurveTo\",10,220,11.5,220", getOperation( 4, ops ) );
    assertEquals( "\"lineTo\",108.5,220", getOperation( 5, ops ) );
    assertEquals( "\"quadraticCurveTo\",110,220,110,217.5", getOperation( 6, ops ) );
    assertEquals( "\"lineTo\",110,22.5", getOperation( 7, ops ) );
    assertEquals( "\"quadraticCurveTo\",110,20,108.5,20", getOperation( 8, ops ) );
    assertEquals( "\"lineTo\",11.5,20", getOperation( 9, ops ) );
    assertEquals( "\"quadraticCurveTo\",10,20,10,22.5", getOperation( 10, ops ) );
    assertEquals( "\"stroke\"", getOperation( 11, ops ) );
  }

  @Test
  public void testDrawRoundRectOffset() {
    gc.setLineWidth( 1 );
    gc.drawRoundRectangle( 10, 20, 100, 200, 1, 3 );

    JSONArray ops = getGCOperations( canvas );
    assertEquals( "\"beginPath\"", getOperation( 1, ops ) );
    assertEquals( "\"moveTo\",10.5,23", getOperation( 2, ops ) );
    assertEquals( "\"lineTo\",10.5,218", getOperation( 3, ops ) );
    assertEquals( "\"quadraticCurveTo\",10.5,220.5,12,220.5", getOperation( 4, ops ) );
    assertEquals( "\"lineTo\",109,220.5", getOperation( 5, ops ) );
    assertEquals( "\"quadraticCurveTo\",110.5,220.5,110.5,218", getOperation( 6, ops ) );
    assertEquals( "\"lineTo\",110.5,23", getOperation( 7, ops ) );
    assertEquals( "\"quadraticCurveTo\",110.5,20.5,109,20.5", getOperation( 8, ops ) );
    assertEquals( "\"lineTo\",12,20.5", getOperation( 9, ops ) );
    assertEquals( "\"quadraticCurveTo\",10.5,20.5,10.5,23", getOperation( 10, ops ) );
    assertEquals( "\"stroke\"", getOperation( 11, ops ) );
  }

  @Test
  public void testFillRoundRect() {
    gc.setLineWidth( 2 );
    gc.fillRoundRectangle( 10, 20, 100, 200, 1, 3 );

    JSONArray ops = getGCOperations( canvas );
    assertEquals( "\"beginPath\"", getOperation( 1, ops ) );
    assertEquals( "\"moveTo\",10,22.5", getOperation( 2, ops ) );
    assertEquals( "\"lineTo\",10,217.5", getOperation( 3, ops ) );
    assertEquals( "\"quadraticCurveTo\",10,220,11.5,220", getOperation( 4, ops ) );
    assertEquals( "\"lineTo\",108.5,220", getOperation( 5, ops ) );
    assertEquals( "\"quadraticCurveTo\",110,220,110,217.5", getOperation( 6, ops ) );
    assertEquals( "\"lineTo\",110,22.5", getOperation( 7, ops ) );
    assertEquals( "\"quadraticCurveTo\",110,20,108.5,20", getOperation( 8, ops ) );
    assertEquals( "\"lineTo\",11.5,20", getOperation( 9, ops ) );
    assertEquals( "\"quadraticCurveTo\",10,20,10,22.5", getOperation( 10, ops ) );
    assertEquals( "\"fill\"", getOperation( 11, ops ) );
  }

  @Test
  public void testFillGradientRectangleVertical() {
    gc.setLineWidth( 2 );
    gc.setForeground( new Color( display, new RGB( 0, 10, 20 ) ) );
    gc.setBackground( new Color( display, new RGB( 30, 40, 50 ) ) );
    gc.fillGradientRectangle( 10, 20, 100, 200, true );

    JSONArray ops = getGCOperations( canvas );
    assertEquals( "\"save\"", getOperation( 3, ops ) );
    assertEquals( "\"createLinearGradient\",10,20,10,220", getOperation( 4, ops ) );
    assertEquals( "\"addColorStop\",0,[0,10,20,255]", getOperation( 5, ops ) );
    assertEquals( "\"addColorStop\",1,[30,40,50,255]", getOperation( 6, ops ) );
    assertEquals( "\"fillStyle\",\"linearGradient\"", getOperation( 7, ops ) );
    assertEquals( "\"beginPath\"", getOperation( 8, ops ) );
    assertEquals( "\"rect\",10,20,100,200", getOperation( 9, ops ) );
    assertEquals( "\"fill\"", getOperation( 10, ops ) );
    assertEquals( "\"restore\"", getOperation( 11, ops ) );
  }

  @Test
  public void testFillGradientRectangleHorizontal() {
    gc.setLineWidth( 2 );
    gc.setForeground( new Color( display, new RGB( 0, 10, 20 ) ) );
    gc.setBackground( new Color( display, new RGB( 30, 40, 50 ) ) );
    gc.fillGradientRectangle( 10, 20, 100, 200, false );

    JSONArray ops = getGCOperations( canvas );
    assertEquals( "\"save\"", getOperation( 3, ops ) );
    assertEquals( "\"createLinearGradient\",10,20,110,20", getOperation( 4, ops ) );
    assertEquals( "\"addColorStop\",0,[0,10,20,255]", getOperation( 5, ops ) );
    assertEquals( "\"addColorStop\",1,[30,40,50,255]", getOperation( 6, ops ) );
    assertEquals( "\"fillStyle\",\"linearGradient\"", getOperation( 7, ops ) );
    assertEquals( "\"beginPath\"", getOperation( 8, ops ) );
    assertEquals( "\"rect\",10,20,100,200", getOperation( 9, ops ) );
    assertEquals( "\"fill\"", getOperation( 10, ops ) );
    assertEquals( "\"restore\"", getOperation( 11, ops ) );
  }

  @Test
  public void testFillGradientRectangleVerticalSwapped() {
    gc.setLineWidth( 2 );
    gc.setForeground( new Color( display, new RGB( 0, 10, 20 ) ) );
    gc.setBackground( new Color( display, new RGB( 30, 40, 50 ) ) );
    gc.fillGradientRectangle( 100, 200, -10, -20, true );

    JSONArray ops = getGCOperations( canvas );
    assertEquals( "\"save\"", getOperation( 3, ops ) );
    assertEquals( "\"createLinearGradient\",90,180,90,200", getOperation( 4, ops ) );
    assertEquals( "\"addColorStop\",0,[30,40,50,255]", getOperation( 5, ops ) );
    assertEquals( "\"addColorStop\",1,[0,10,20,255]", getOperation( 6, ops ) );
    assertEquals( "\"fillStyle\",\"linearGradient\"", getOperation( 7, ops ) );
    assertEquals( "\"beginPath\"", getOperation( 8, ops ) );
    assertEquals( "\"rect\",90,180,-10,-20", getOperation( 9, ops ) );
    assertEquals( "\"fill\"", getOperation( 10, ops ) );
    assertEquals( "\"restore\"", getOperation( 11, ops ) );
  }

  @Test
  public void testFillGradientRectangleHorizontalSwapped() {
    gc.setLineWidth( 2 );
    gc.setForeground( new Color( display, new RGB( 0, 10, 20 ) ) );
    gc.setBackground( new Color( display, new RGB( 30, 40, 50 ) ) );
    gc.fillGradientRectangle( 100, 200, -10, -20, false );

    JSONArray ops = getGCOperations( canvas );
    assertEquals( "\"save\"", getOperation( 3, ops ) );
    assertEquals( "\"createLinearGradient\",90,180,100,180", getOperation( 4, ops ) );
    assertEquals( "\"addColorStop\",0,[30,40,50,255]", getOperation( 5, ops ) );
    assertEquals( "\"addColorStop\",1,[0,10,20,255]", getOperation( 6, ops ) );
    assertEquals( "\"fillStyle\",\"linearGradient\"", getOperation( 7, ops ) );
    assertEquals( "\"beginPath\"", getOperation( 8, ops ) );
    assertEquals( "\"rect\",90,180,-10,-20", getOperation( 9, ops ) );
    assertEquals( "\"fill\"", getOperation( 10, ops ) );
    assertEquals( "\"restore\"", getOperation( 11, ops ) );
  }

  @Test
  public void testDrawArc() {
    gc.setLineWidth( 2 );
    gc.drawArc( 10, 20, 100, 200, 50, 100 );

    JSONArray ops = getGCOperations( canvas );
    assertEquals( "\"beginPath\"", getOperation( 1, ops ) );
    assertEquals( "\"ellipse\",60,120,50,100,0,-0.8727,-2.618,true", getOperation( 2, ops ) );
    assertEquals( "\"stroke\"", getOperation( 3, ops ) );
  }

  @Test
  public void testDrawArcOffset() {
    gc.setLineWidth( 1 );
    gc.drawArc( 10, 20, 100, 200, 50, 100 );

    JSONArray ops = getGCOperations( canvas );
    assertEquals( "\"beginPath\"", getOperation( 1, ops ) );
    assertEquals( "\"ellipse\",60.5,120.5,50,100,0,-0.8727,-2.618,true", getOperation( 2, ops ) );
    assertEquals( "\"stroke\"", getOperation( 3, ops ) );
  }

  @Test
  public void testFillArc() {
    gc.setLineWidth( 2 );
    gc.fillArc( 10, 20, 100, 200, 50, 100 );

    JSONArray ops = getGCOperations( canvas );
    assertEquals( "\"beginPath\"", getOperation( 1, ops ) );
    assertEquals( "\"ellipse\",60,120,50,100,0,-0.8727,-2.618,true", getOperation( 2, ops ) );
    assertEquals( "\"lineTo\",60,120", getOperation( 3, ops ) );
    assertEquals( "\"fill\"", getOperation( 4, ops ) );
  }

  @Test
  public void testFillArcClockwise() {
    gc.setLineWidth( 2 );
    gc.fillArc( 10, 20, 100, 200, 50, -100 );

    JSONArray ops = getGCOperations( canvas );
    assertEquals( "\"beginPath\"", getOperation( 1, ops ) );
    assertEquals( "\"ellipse\",60,120,50,100,0,-0.8727,0.8726001,false", getOperation( 2, ops ) );
    assertEquals( "\"lineTo\",60,120", getOperation( 3, ops ) );
    assertEquals( "\"fill\"", getOperation( 4, ops ) );
  }

  @Test
  public void testDrawImage() {
    Image image = Graphics.getImage( Fixture.IMAGE_50x100, canvas.getClass().getClassLoader() );
    String imageLocation = ImageFactory.getImagePath( image );

    gc.drawImage( image, 10, 50 );

    JSONArray ops = getGCOperations( canvas );
    String expected = "\"drawImage\",\"" + imageLocation + "\",10,50";
    assertEquals( expected, getOperation( 0, ops ) );
  }

  @Test
  public void testDrawImagePart() {
    Image image = Graphics.getImage( Fixture.IMAGE_50x100, canvas.getClass().getClassLoader() );
    String imageLocation = ImageFactory.getImagePath( image );

    gc.drawImage( image, 10, 20, 30, 40, 100, 110, 400, 500  );

    JSONArray ops = getGCOperations( canvas );
    String expected = "\"drawImage\",\"" + imageLocation + "\",10,20,30,40,100,110,400,500";
    assertEquals( expected, getOperation( 0, ops ) );
  }

  @Test
  public void testDrawText() {
    gc.drawText( "foo", 30, 34, true );

    JSONArray ops = getGCOperations( canvas );
    assertEquals( "\"strokeText\",\"foo\",false,true,true,30,34", getOperation( 0, ops ) );
  }

  @Test
  public void testDrawTextWithMenmonic() {
    gc.drawText( "foo", 30, 34, SWT.DRAW_MNEMONIC );

    JSONArray ops = getGCOperations( canvas );
    assertEquals( "\"fillText\",\"foo\",true,false,false,30,34", getOperation( 0, ops ) );
  }

  @Test
  public void testDrawTextWithDelimiter() {
    gc.drawText( "foo", 30, 34, SWT.DRAW_DELIMITER );

    JSONArray ops = getGCOperations( canvas );
    assertEquals( "\"fillText\",\"foo\",false,true,false,30,34", getOperation( 0, ops ) );
  }

  @Test
  public void testDrawTextWithTab() {
    gc.drawText( "foo", 30, 34, SWT.DRAW_TAB );

    JSONArray ops = getGCOperations( canvas );
    assertEquals( "\"fillText\",\"foo\",false,false,true,30,34", getOperation( 0, ops ) );
  }

  @Test
  public void testFillText() {
    gc.drawText( "foo", 30, 34 );

    JSONArray ops = getGCOperations( canvas );
    assertEquals( "\"fillText\",\"foo\",false,true,true,30,34", getOperation( 0, ops ) );
  }

  // bug 351216: [GC] Throws unexpected "Graphic is diposed" exception
  @Test
  public void testWriteColorOperationWithDisposedColor() {
    Color color = new Color( canvas.getDisplay(), 1, 2, 3 );
    gc.setForeground( color );
    color.dispose();

    JSONArray ops = getGCOperations( canvas );
    assertEquals( "\"strokeStyle\",[1,2,3,255]", getOperation( 0, ops ) );
  }

  // bug 351216: [GC] Throws unexpected "Graphic is diposed" exception
  @Test
  public void testWriteFontOperationWithDisposedFont() {
    Font font = new Font( canvas.getDisplay(), "font-name", 1, SWT.NORMAL );
    gc.setFont( font );
    font.dispose();

    JSONArray ops = getGCOperations( canvas );
    assertEquals( "\"font\",[[\"font-name\"],1,false,false]", getOperation( 0, ops ) );
  }

  // bug 351216: [GC] Throws unexpected "Graphic is diposed" exception
  @Test
  public void testWriteImageOperationWithDisposedImage() {
    Image image = new Image( canvas.getDisplay(), 100, 100 );
    gc.drawImage( image, 0, 0 );
    image.dispose();

    writeGCOperations( canvas );

    JSONArray ops = getGCOperations( canvas );
    assertTrue( getOperation( 0, ops ).contains( "drawImage" ) );
  }

  private static JSONArray getGCOperations( Canvas canvas ) {
    writeGCOperations( canvas );
    Message message = Fixture.getProtocolMessage();
    String id = CanvasLCA_Test.getGcId( canvas );
    CallOperation draw = message.findCallOperation( id, "draw" );
    JSONArray operations = ( JSONArray )draw.getProperty( "operations" );
    return operations;
  }

  private static String getOperation( int i, JSONArray operations ) {
    String result = null;
    try {
      JSONArray op = operations.getJSONArray( i );
      result = op.join( "," );
    } catch( JSONException e ) {
      fail();
    }
    return result;
  }

  private static void writeGCOperations( Canvas canvas ) {
    IGCAdapter adapter = canvas.getAdapter( IGCAdapter.class );
    GCOperation[] operations = adapter.getGCOperations();
    GCOperationWriter operationWriter = new GCOperationWriter( canvas );
    for( GCOperation operation : operations ) {
      operationWriter.write( operation );
    }
    operationWriter.render();
  }
}
