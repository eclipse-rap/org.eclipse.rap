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

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.graphics.*;
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
import org.eclipse.swt.widgets.Control;


class ControlGC extends GCDelegate {
  private final Control control;
  private Color background;
  private Color foreground;
  private Font font;
  private int alpha;
  private int lineWidth;
  private int lineCap;
  private int lineJoin;

  ControlGC( Control control ) {
    this.control = control;
    this.background = control.getBackground();
    this.foreground = control.getForeground();
    this.font = control.getFont();
    this.alpha = 255;
    this.lineWidth = 0;
    this.lineCap = SWT.CAP_FLAT;
    this.lineJoin = SWT.JOIN_MITER;
  }

  void setBackground( Color color ) {
    background = color;
    GCOperation operation = new SetProperty( SetProperty.BACKGROUND, color.getRGB() );
    addGCOperation( operation );
  }

  Color getBackground() {
    return background;
  }

  void setForeground( Color color ) {
    foreground = color;
    GCOperation operation = new SetProperty( SetProperty.FOREGROUND, color.getRGB() );
    addGCOperation( operation );
  }

  Color getForeground() {
    return foreground;
  }

  void setFont( Font font ) {
    this.font = font;
    GCOperation operation = new SetProperty( cloneFontData( FontUtil.getData( font ) ) );
    addGCOperation( operation );
  }

  Font getFont() {
    return font;
  }
  
  Font getDefaultFont() {
    return control.getDisplay().getSystemFont();
  }

  void setAlpha( int alpha ) {
    this.alpha = alpha;
    GCOperation operation = new SetProperty( SetProperty.ALPHA, alpha );
    addGCOperation( operation );
  }

  int getAlpha() {
    return alpha;
  }

  void setLineWidth( int lineWidth ) {
    this.lineWidth = lineWidth;
    GCOperation operation
      = new SetProperty( SetProperty.LINE_WIDTH, lineWidth );
    addGCOperation( operation );
  }

  int getLineWidth() {
    return lineWidth;
  }

  void setLineCap( int lineCap ) {
    this.lineCap = lineCap;
    GCOperation operation = new SetProperty( SetProperty.LINE_CAP, lineCap );
    addGCOperation( operation );
  }

  int getLineCap() {
    return lineCap;
  }

  void setLineJoin( int lineJoin ) {
    this.lineJoin = lineJoin;
    GCOperation operation = new SetProperty( SetProperty.LINE_JOIN, lineJoin );
    addGCOperation( operation );
  }

  int getLineJoin() {
    return lineJoin;
  }

  Rectangle getClipping() {
    return control.getBounds();
  }
  
  Point stringExtent( String string ) {
    return Graphics.stringExtent( font, string );
  }
  
  Point textExtent( String string, int wrapWidth ) {
    return Graphics.textExtent( font, string, wrapWidth );
  }

  void drawPoint( int x, int y ) {
    GCOperation operation = new DrawPoint( x, y );
    addGCOperation( operation );
  }

  void drawLine( int x1, int y1, int x2, int y2 ) {
    GCOperation operation = new DrawLine( x1, y1, x2, y2 );
    addGCOperation( operation );
  }
  
  void drawPolyline( int[] pointArray, boolean close, boolean fill ) {
    DrawPolyline operation = new DrawPolyline( pointArray, close, fill );
    addGCOperation( operation );
  }

  void drawRectangle( Rectangle bounds, boolean fill ) {
    GCOperation operation = new DrawRectangle( bounds, fill );
    addGCOperation( operation );
  }

  void drawRoundRectangle( Rectangle bounds, int arcWidth, int arcHeight, boolean fill ) {
    GCOperation operation
      = new DrawRoundRectangle( bounds, arcWidth, arcHeight, fill );
    addGCOperation( operation );
  }

  void fillGradientRectangle( Rectangle bounds, boolean vertical ) {
    GCOperation operation = new FillGradientRectangle( bounds, vertical );
    addGCOperation( operation );
  }

  void drawArc( Rectangle bounds, int startAngle, int arcAngle, boolean fill ) {
    GCOperation operation = new DrawArc( bounds, startAngle, arcAngle, fill );
    addGCOperation( operation );
  }

  void drawImage( Image image, Rectangle src, Rectangle dest, boolean simple ) {
    GCOperation operation = new DrawImage( image, src, dest, simple );
    addGCOperation( operation );
  }

  void drawText( String string, int x, int y, int flags ) {
    GCOperation operation = new DrawText( string, x, y, flags );
    addGCOperation( operation );
  }

  GCAdapter getGCAdapter() {
    return ( GCAdapter )control.getAdapter( IGCAdapter.class );
  }

  private void addGCOperation( GCOperation operation ) {
    GCAdapter adapter = getGCAdapter();
    if( adapter != null ) {
      adapter.addGCOperation( operation );
    }
  }

  private static FontData cloneFontData( FontData fontData ) {
    FontData result = new FontData( fontData.getName(), fontData.getHeight(), fontData.getStyle() );
    result.setLocale( fontData.getLocale() );
    return result;
  }
}
