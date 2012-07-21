/*******************************************************************************
 * Copyright (c) 2011 Rüdiger Herrmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rüdiger Herrmann - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.graphics;

import org.eclipse.rap.rwt.graphics.Graphics;
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

  DeviceGC( Device device ) {
    this.device = device;
    this.background = device.getSystemColor( SWT.COLOR_WHITE );
    this.foreground = device.getSystemColor( SWT.COLOR_BLACK );
    this.font = device.getSystemFont();
    this.alpha = 255;
    this.lineWidth = 0;
    this.lineCap = SWT.CAP_FLAT;
    this.lineJoin = SWT.JOIN_MITER;
  }

  void setBackground( Color color ) {
    background = color;
  }

  Color getBackground() {
    return background;
  }

  void setForeground( Color color ) {
    foreground = color;
  }

  Color getForeground() {
    return foreground;
  }

  void setFont( Font font ) {
    this.font = font;
  }

  Font getFont() {
    return font;
  }
  
  Font getDefaultFont() {
    return device.getSystemFont();
  }

  void setAlpha( int alpha ) {
    this.alpha = alpha;
  }

  int getAlpha() {
    return alpha;
  }

  void setLineWidth( int lineWidth ) {
    this.lineWidth = lineWidth;
  }

  int getLineWidth() {
    return lineWidth;
  }

  void setLineCap( int lineCap ) {
    this.lineCap = lineCap;
  }

  int getLineCap() {
    return lineCap;
  }

  void setLineJoin( int lineJoin ) {
    this.lineJoin = lineJoin;
  }

  int getLineJoin() {
    return lineJoin;
  }

  Rectangle getClipping() {
    return device.getBounds();
  }

  Point stringExtent( String string ) {
    return Graphics.stringExtent( font, string );
  }

  Point textExtent( String string, int wrapWidth ) {
    return Graphics.textExtent( font, string, wrapWidth );
  }

  void drawPoint( int x, int y ) {
  }

  void drawLine( int x1, int y1, int x2, int y2 ) {
  }

  void drawPolyline( int[] pointArray, boolean close, boolean fill ) {
  }

  void drawRectangle( Rectangle bounds, boolean fill ) {
  }

  void drawRoundRectangle( Rectangle bounds, int arcWidth, int arcHeight, boolean fill ) {
  }

  void fillGradientRectangle( Rectangle bounds, boolean vertical ) {
  }

  void drawArc( Rectangle boundsx, int startAngle, int arcAngle, boolean fill ) {
  }

  void drawImage( Image image, Rectangle src, Rectangle dest, boolean simple ) {
  }

  void drawText( String string, int x, int y, int flags ) {
  }
}
