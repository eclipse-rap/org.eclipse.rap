/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.graphics;

import org.eclipse.swt.graphics.*;

public abstract class GCOperation {

  public static final class SetProperty extends GCOperation {

    public static final int FOREGROUND = 0;
    public static final int BACKGROUND = 1;
    public static final int ALPHA = 2;
    public static final int LINE_WIDTH = 3;
    public static final int LINE_CAP = 4;
    public static final int LINE_JOIN = 5;

    public final int id;
    public final Object value;

    public SetProperty( final int id, final Color value ) {
      this.id = id;
      this.value = value;
    }

    public SetProperty( final int id, final int value ) {
      this.id = id;
      this.value = new Integer( value );
    }
  }

  public static final class SetFont extends GCOperation {

    public final Font font;

    public SetFont( final Font font ) {
      this.font = font;
    }
  }

  public static final class DrawLine extends GCOperation {

    public final int x1;
    public final int y1;
    public final int x2;
    public final int y2;

    public DrawLine( final int x1, final int y1, final int x2, final int y2 ) {
      this.x1 = x1;
      this.y1 = y1;
      this.x2 = x2;
      this.y2 = y2;
    }
  }

  public static final class DrawPoint extends GCOperation {

    public final int x;
    public final int y;

    public DrawPoint( final int x, final int y ) {
      this.x = x;
      this.y = y;
    }
  }

  public static class DrawRectangle extends GCOperation {

    public final int x;
    public final int y;
    public final int width;
    public final int height;
    public final boolean fill;

    public DrawRectangle( final Rectangle bounds, final boolean fill ) {
      this.x = bounds.x;
      this.y = bounds.y;
      this.width = bounds.width;
      this.height = bounds.height;
      this.fill = fill;
    }
  }

  public static final class DrawRoundRectangle extends DrawRectangle {

    public final int arcWidth;
    public final int arcHeight;

    public DrawRoundRectangle( final Rectangle bounds,
                               final int arcWidth,
                               final int arcHeight,
                               final boolean fill )
    {
      super( bounds, fill );
      this.arcWidth = arcWidth;
      this.arcHeight = arcHeight;
    }
  }

  public static final class FillGradientRectangle extends DrawRectangle {

    public final boolean vertical;

    public FillGradientRectangle( final Rectangle bounds,
                                  final boolean vertical )
    {
      super( bounds, true );
      this.vertical = vertical;
    }
  }

  public static final class DrawArc extends GCOperation {

    public final int x;
    public final int y;
    public final int width;
    public final int height;
    public final int startAngle;
    public final int arcAngle;
    public final boolean fill;

    public DrawArc( final Rectangle bounds,
                    final int startAngle,
                    final int arcAngle,
                    final boolean fill )
    {
      this.x = bounds.x;
      this.y = bounds.y;
      this.width = bounds.width;
      this.height = bounds.height;
      this.startAngle = startAngle;
      this.arcAngle = arcAngle;
      this.fill = fill;
    }
  }

  public static final class DrawPolyline extends GCOperation {

    public final int[] points;
    public final boolean close;
    public final boolean fill;

    public DrawPolyline( final int[] points,
                         final boolean close,
                         final boolean fill )
    {
      this.points = new int[ points.length ];
      System.arraycopy( points, 0, this.points, 0, points.length );
      this.close = close;
      this.fill = fill;
    }
  }

  public static final class DrawImage extends GCOperation {

    public final Image image;
    public final int srcX;
    public final int srcY;
    public final int srcWidth;
    public final int srcHeight;
    public final int destX;
    public final int destY;
    public final int destWidth;
    public final int destHeight;
    public final boolean simple;

    public DrawImage( final Image image,
                      final Rectangle src,
                      final Rectangle dest,
                      final boolean simple )
    {
      this.image = image;
      this.srcX = src.x;
      this.srcY = src.y;
      this.srcWidth = src.width;
      this.srcHeight = src.height;
      this.destX = dest.x;
      this.destY = dest.y;
      this.destWidth = dest.width;
      this.destHeight = dest.height;
      this.simple = simple;
    }
  }

  public static final class DrawText extends GCOperation {

    public final String text;
    public final int x;
    public final int y;
    public final int flags;

    public DrawText( final String text,
                     final int x,
                     final int y,
                     final int flags )
    {
      this.text = text;
      this.x = x;
      this.y = y;
      this.flags = flags;
    }
  }
}
