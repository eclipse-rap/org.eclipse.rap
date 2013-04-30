/*******************************************************************************
 * Copyright (c) 2010, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.demo.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;


public final class CanvasTab extends ExampleTab {

  public CanvasTab() {
    super( "Canvas" );
  }

  @Override
  protected void createStyleControls( Composite parent ) {
    createStyleButton( "BORDER", SWT.BORDER );
    cteateRoundedBorderGroup();
    createVisibilityButton();
    createEnablementButton();
    createBgColorButton();
    createBgGradientButton();
    createBgImageButton();
  }

  @Override
  protected void createExampleControls( Composite parent ) {
    parent.setLayout( new FillLayout() );
    final Canvas canvas = new Canvas( parent, getStyle() );
    canvas.setLayout( new RowLayout( SWT.HORIZONTAL ) );
    canvas.addPaintListener(  new PaintListener() {
      public void paintControl( final PaintEvent event ) {
        event.gc.drawPoint( 230, 100 );
        paintLines( event.display, event.gc );
        paintRectangles( event.display, event.gc );
        paintArcs( event.display, event.gc );
        paintImages( event.display, event.gc );
        paintTexts( event.display, event.gc );
        paintPolylines( event.display, event.gc );
        paintPath( event.display, event.gc );
      }
    } );
    canvas.redraw();
    registerControl( canvas );
    Label label = new Label( canvas, SWT.NONE );
    label.setText( "Label" );
    Button pushButton = new Button( canvas, SWT.PUSH );
    pushButton.setText( "Push Button" );
  }

  private void paintLines( Display display, GC gc ) {
    gc.drawLine( 30, 130, 400, 130 );
    gc.setLineWidth( 10 );
    gc.setForeground( display.getSystemColor( SWT.COLOR_GREEN ) );
    gc.setAlpha( 64 );
    gc.drawLine( 30, 140, 400, 140 );
    gc.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
    gc.setLineJoin( SWT.JOIN_ROUND );
    gc.setLineCap( SWT.CAP_ROUND );
    int[] pointArray = new int[] {
      70, 120,
      100, 150,
      130, 120,
      160, 150
    };
    gc.drawPolyline( pointArray );
    gc.setForeground( display.getSystemColor( SWT.COLOR_DARK_MAGENTA ) );
    gc.setLineJoin( SWT.JOIN_BEVEL );
    gc.setLineCap( SWT.CAP_SQUARE );
    pointArray = new int[] {
      170, 120,
      200, 150,
      230, 120,
      260, 150
    };
    gc.drawPolyline( pointArray );
    gc.setForeground( display.getSystemColor( SWT.COLOR_DARK_RED ) );
    gc.setLineJoin( SWT.JOIN_MITER );
    gc.setLineCap( SWT.CAP_FLAT );
    pointArray = new int[] {
      270, 120,
      300, 150,
      330, 120,
      360, 150
    };
    gc.drawPolyline( pointArray );
    gc.setLineWidth( 1 );
    gc.setAlpha( 255 );
  }

  private void paintRectangles( Display display, GC gc ) {
    gc.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
    gc.drawRectangle( 30, 160, 50, 50 );
    gc.setForeground( display.getSystemColor( SWT.COLOR_BLUE ) );
    gc.drawRoundRectangle( 90, 160, 50, 50, 10, 10 );
    gc.setBackground( display.getSystemColor( SWT.COLOR_GREEN ) );
    gc.fillRectangle( 150, 160, 50, 50 );
    gc.fillRoundRectangle( 210, 160, 50, 50, 10, 10 );
    gc.fillGradientRectangle( 270, 160, 50, 50, true );
    gc.fillGradientRectangle( 330, 160, 50, 50, false );
  }

  private void paintArcs( Display display, GC gc ) {
    gc.setForeground( display.getSystemColor( SWT.COLOR_BLUE ) );
    gc.drawOval( 30, 220, 50, 25 );
    gc.setBackground( display.getSystemColor( SWT.COLOR_WHITE ) );
    gc.setForeground( display.getSystemColor( SWT.COLOR_GREEN ) );
    gc.drawArc( 90, 220, 50, 25, 45, 180 );
    gc.setBackground( display.getSystemColor( SWT.COLOR_YELLOW ) );
    gc.fillArc( 150, 220, 50, 25, 45, 180 );
    gc.setBackground( display.getSystemColor( SWT.COLOR_BLUE ) );
    gc.fillOval( 210, 220, 50, 50 );
  }

  private void paintImages( Display display, GC gc ) {
    Image image = display.getSystemImage( SWT.ICON_INFORMATION );
    int width = image.getImageData().width;
    int height = image.getImageData().height;
    gc.drawImage( image, 30, 280 );
    gc.setAlpha( 64 );
    gc.drawImage( image, 90, 280 );
    gc.setAlpha( 255 );
    gc.drawImage( image,
                  9,
                  3,
                  width - 20,
                  height - 6,
                  150,
                  280,
                  width - 10,
                  height + 4 );
  }

  private void paintTexts( Display display, GC gc ) {
    gc.setForeground( display.getSystemColor( SWT.COLOR_WHITE ) );
    gc.drawString( "Hello RAP World!", 200, 280, false );
    Font font = new Font( display, "Arial, Verdana, Tahoma", 16, SWT.BOLD | SWT.ITALIC );
    gc.setFont( font );
    gc.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
    gc.drawText( "\tHello RAP World!\nAgain!", 200, 350, true );
  }

  private void paintPolylines( Display display, GC gc ) {
    int[] pointArray = new int[] {
      55, 340,
      80, 365,
      55, 390,
      30, 365
    };
    gc.drawPolygon( pointArray );
    pointArray = new int[] {
      105, 340,
      130, 365,
      105, 390,
      80, 365
    };
    gc.fillPolygon( pointArray );
    pointArray = new int[] {
      155, 340,
      180, 365,
      155, 390,
      130, 365
    };
    gc.drawPolyline( pointArray );
  }

  private void paintPath( Display display, GC gc ) {
    gc.setForeground( display.getSystemColor( SWT.COLOR_BLUE ) );
    gc.setLineWidth( 5 );
    Path path = new Path( display );
    path.moveTo( 100, 420 );
    path.lineTo( 200, 560 );
    path.quadTo( 230, 600, 250, 520 );
    path.cubicTo( 290, 360, 300, 600, 400, 550 );
    path.lineTo( 500, 490 );
    gc.drawPath( path );
    path.dispose();

    gc.setBackground( display.getSystemColor( SWT.COLOR_RED ) );
    gc.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
    path = new Path( display );
    path.moveTo( 30, 430 );
    path.lineTo( 150, 550 );
    path.cubicTo( 60, 470, 60, 470, 70, 550 );
    path.close();
    gc.fillPath( path );
    path.dispose();
  }

}
