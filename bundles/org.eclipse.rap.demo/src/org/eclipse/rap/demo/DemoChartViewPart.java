/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.demo;

import org.eclipse.rap.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.part.ViewPart;


public class DemoChartViewPart extends ViewPart {

  @Override
  public void createPartControl( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new FillLayout() );
    Canvas canvas = new Canvas( composite, SWT.NONE );
    canvas.addPaintListener( new PaintListener() {
      public void paintControl( PaintEvent event ) {
        drawGrid( event );
        drawBars( event );
      }
    } );
  }

  @Override
  public void setFocus() {
  }

  private static void drawGrid( PaintEvent event ) {
    Display display = event.display;
    GC gc = event.gc;
    gc.setFont( new Font( display, "Arial", 10, SWT.NONE ) );
    for( int i = 2; i < 9; i++ ) {
      gc.setForeground( Graphics.getColor( 0, 0, 0 ) );
      gc.drawString( String.valueOf( 8 - i ), 10, i * 20 - 7 );
      gc.setForeground( Graphics.getColor( 230, 230, 230 ) );
      gc.drawLine( 20, i * 20, 160, i * 20 );
    }
    gc.setForeground( Graphics.getColor( 0, 0, 0 ) );
    gc.drawLine( 20, 20, 20, 160 );
    gc.drawPolygon( new int[] { 20, 10, 23, 20, 17, 20 } );
    gc.drawLine( 20, 160, 160, 160 );
    gc.drawPolygon( new int[] { 170, 160, 160, 157, 160, 163 } );
  }

  private static void drawBars( PaintEvent event ) {
    int[] barHeights = new int[] { 34, 111, 21, 45, 87, 50 };
    Color[] barColors = new Color[] {
      Graphics.getColor( 99, 150, 239 ),
      Graphics.getColor( 239, 130, 123 ),
      Graphics.getColor( 49, 203, 49 ),
      Graphics.getColor( 255, 215, 0 ),
      Graphics.getColor( 132, 113, 255 ),
      Graphics.getColor( 140, 239, 140 )
    };
    String[] barTitles = new String[] { "A", "B", "C", "D", "E", "F" };
    for( int i = 0; i < 6; i++ ) {
      drawSingleBar( event, barTitles[ i ], barColors[ i ], 30 + i * 20, barHeights[ i ] );
    }
  }

  private static void drawSingleBar( PaintEvent event,
                                     String title,
                                     Color color,
                                     int x,
                                     int height )
  {
    Display display = event.display;
    GC gc = event.gc;
    gc.setBackground( color );
    gc.fillRectangle( x, 160 - height, 15, height );
    gc.setForeground( Graphics.getColor( 0, 0, 0 ) );
    gc.setBackground( Graphics.getColor( 0, 0, 0 ) );
    gc.drawRectangle( x, 160 - height, 15, height );
    gc.fillRectangle( x + 13, 160 - height, 2, height );
    gc.setFont( new Font( display, "Arial", 10, SWT.NONE ) );
    gc.setForeground( Graphics.getColor( 0, 0, 0 ) );
    gc.setBackground( Graphics.getColor( 255, 255, 255 ) );
    gc.drawString( title, x + 5, 161 );
  }
}
