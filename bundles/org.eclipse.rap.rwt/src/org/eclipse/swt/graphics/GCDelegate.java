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



abstract class GCDelegate {

  abstract void setBackground( Color color );
  abstract Color getBackground();

  abstract void setForeground( Color color );
  abstract Color getForeground();

  abstract void setFont( Font font );
  abstract Font getFont();
  
  abstract Font getDefaultFont();
  abstract void setAlpha( int alpha );
  abstract int getAlpha();
  
  abstract void setLineWidth( int lineWidth );
  abstract int getLineWidth();
  abstract void setLineCap( int lineCap );
  abstract int getLineCap();
  abstract void setLineJoin( int lineJoin );
  abstract int getLineJoin();
  
  abstract Rectangle getClipping();

  abstract Point stringExtent( String string );
  abstract Point textExtent( String string , int wrapWidth );
  
  abstract void drawPoint( int x, int y );
  abstract void drawLine( int x1, int y1, int x2, int y2 );
  abstract void drawPolyline( int[] pointArray, boolean close, boolean fill );
  abstract void drawRectangle( Rectangle bounds, boolean fill );
  abstract void drawRoundRectangle( Rectangle bounds,
                                    int arcWidth,
                                    int arcHeight,
                                    boolean fill );
  abstract void fillGradientRectangle( Rectangle bounds, boolean vertical );
  abstract void drawArc( Rectangle bounds,
                         int startAngle,
                         int arcAngle,
                         boolean fill );

  abstract void drawImage( Image image, 
                           Rectangle src, 
                           Rectangle dest, 
                           boolean simple );

  abstract void drawText( String string, int x, int y, int flags );
}
