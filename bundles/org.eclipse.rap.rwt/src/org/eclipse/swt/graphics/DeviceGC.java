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

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;


class DeviceGC extends GCDelegate {
  private final Device device;
  private Color background;
  private Color foreground;
  private Font font;
  private int alpha;
  private int lineWidth;
  private int lineCap;
  private int lineJoin;

  DeviceGC( final Device device ) {
    this.device = device;
    this.background = device.getSystemColor( SWT.COLOR_WHITE );
    this.foreground = device.getSystemColor( SWT.COLOR_BLACK );
    this.font = device.getSystemFont();
    this.alpha = 255;
    this.lineWidth = 0;
    this.lineCap = SWT.CAP_FLAT;
    this.lineJoin = SWT.JOIN_MITER;
  }

  void setBackground( final Color color ) {
    background = color;
  }

  Color getBackground() {
    return background;
  }

  void setForeground( final Color color ) {
    foreground = color;
  }

  Color getForeground() {
    return foreground;
  }

  void setFont( final Font font ) {
    this.font = font;
  }

  Font getFont() {
    return font;
  }
  
  Font getDefaultFont() {
    return device.getSystemFont();
  }

  void setAlpha( final int alpha ) {
    this.alpha = alpha;
  }

  int getAlpha() {
    return alpha;
  }

  void setLineWidth( final int lineWidth ) {
    this.lineWidth = lineWidth;
  }

  int getLineWidth() {
    return lineWidth;
  }

  void setLineCap( final int lineCap ) {
    this.lineCap = lineCap;
  }

  int getLineCap() {
    return lineCap;
  }

  void setLineJoin( final int lineJoin ) {
    this.lineJoin = lineJoin;
  }

  int getLineJoin() {
    return lineJoin;
  }

  Rectangle getClipping() {
    return device.getBounds();
  }

  Point stringExtent( final String string ) {
    return Graphics.stringExtent( font, string );
  }

  Point textExtent( final String string, final int wrapWidth ) {
    return Graphics.textExtent( font, string, wrapWidth );
  }

  void drawPoint( final int x, final int y ) {
  }

  void drawLine( final int x1, final int y1, final int x2, final int y2 ) {
  }

  void drawPolyline( final int[] pointArray,
                     final boolean close,
                     final boolean fill ) {
  }

  void drawRectangle( final Rectangle bounds, final boolean fill ) {
  }

  void drawRoundRectangle( final Rectangle bounds,
                           final int arcWidth,
                           final int arcHeight,
                           final boolean fill )
  {
  }

  void fillGradientRectangle( final Rectangle bounds, final boolean vertical ) {
  }

  void drawArc( final Rectangle boundsx,
                final int startAngle,
                final int arcAngle,
                final boolean fill )
  {
  }

  void drawImage( final Image image,
                  final Rectangle src,
                  final Rectangle dest,
                  final boolean simple )
  {
  }

  void drawText( final String string,
                 final int x,
                 final int y,
                 final int flags )
  {
  }
}
