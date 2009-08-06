/*******************************************************************************
 * Copyright (c) 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.graphics;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.eclipse.rwt.internal.ConfigurationReader;
import org.eclipse.rwt.internal.IEngineConfig;
import org.eclipse.rwt.internal.resources.ResourceManager;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.graphics.ResourceFactory;
import org.eclipse.swt.widgets.Display;

// RAP [bm]: e4-enabling hacks
public class GC extends Resource {

  private static final String IMAGE_FORMAT = "jpeg";
  private final int style;
  private Image image;
  private BufferedImage bufferedImage;
  private Graphics2D graphics;
  private Stroke lineStroke;
  private Color background;
  private Color foreground;
  private float lineWidth;
  private int lineCap;
  private int lineJoin;
  private int lineStyle;
  private float[] lineDashes;
  private float lineDashesOffset;
  private float lineMiterLimit;

  // TODO [rh] change ctor to GC(Image) and GC(Image,int)? would be less
  // compatible with SWT but show problems at compile time.
  public GC( final Drawable drawable ) {
    this( drawable, SWT.NONE );
  }

  public GC( final Drawable drawable, final int style ) {
    if( drawable == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    image = ( Image )drawable;
    this.style = checkStyle( style );
    this.bufferedImage = createBufferedImage( image );
    this.graphics = bufferedImage.createGraphics();
    initDefaults();
  }

  public int getStyle() {
    checkGC();
    return style;
  }

  // /////////////////
  // Attribute setter
  public void setBackground( final Color color ) {
    checkGC();
    if( color == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    background = color;
  }

  public Color getBackground() {
    checkGC();
    return background;
  }

  public void setForeground( final Color color ) {
    checkGC();
    if( color == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    foreground = color;
  }

  public Color getForeground() {
    checkGC();
    return foreground;
  }

  public void setAntialias( final int antialias ) {
    checkGC();
    Object hintValue = null;
    switch( antialias ) {
      case SWT.DEFAULT:
        hintValue = RenderingHints.VALUE_ANTIALIAS_DEFAULT;
      break;
      case SWT.OFF:
        hintValue = RenderingHints.VALUE_ANTIALIAS_OFF;
      break;
      case SWT.ON:
        hintValue = RenderingHints.VALUE_ANTIALIAS_ON;
      break;
      default:
        SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    graphics.setRenderingHint( RenderingHints.KEY_ANTIALIASING, hintValue );
  }

  public int getAntialias() {
    checkGC();
    int result = SWT.DEFAULT;
    Object value = graphics.getRenderingHint( RenderingHints.KEY_ANTIALIASING );
    if( RenderingHints.VALUE_ANTIALIAS_DEFAULT.equals( value ) ) {
      result = SWT.DEFAULT;
    } else if( RenderingHints.VALUE_ANTIALIAS_OFF.equals( value ) ) {
      result = SWT.OFF;
    } else if( RenderingHints.VALUE_ANTIALIAS_ON.equals( value ) ) {
      result = SWT.ON;
    }
    return result;
  }

  // /////////////
  // Clip methods
  public void setClipping( final Rectangle rect ) {
    checkGC();
    if( rect == null ) {
      graphics.setClip( null );
    } else {
      setClipping( rect.x, rect.y, rect.width, rect.height );
    }
  }

  public void setClipping( final int x,
                           final int y,
                           final int width,
                           final int height )
  {
    checkGC();
    graphics.setClip( x, y, width, height );
  }

  public Rectangle getClipping() {
    checkGC();
    java.awt.Rectangle rect = graphics.getClipBounds();
    Rectangle result;
    if( rect == null ) {
      Rectangle bounds = image.getBounds();
      result = new Rectangle( bounds.x, bounds.y, bounds.width, bounds.height );
    } else {
      result = new Rectangle( rect.x, rect.y, rect.width, rect.height );
    }
    return result;
  }

  // //////////////
  // Fill methods
  public void fillRectangle( final Rectangle rect ) {
    if( rect == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    fillRectangle( rect.x, rect.y, rect.width, rect.height );
  }

  public void fillRectangle( final int x,
                             final int y,
                             final int width,
                             final int height )
  {
    checkGC();
    applyColor( background );
    graphics.fillRect( x, y, width - 1, height - 1 );
  }

  public void fillGradientRectangle( final int x,
                                     final int y,
                                     final int width,
                                     final int height,
                                     final boolean vertical )
  {
    java.awt.Color fromColor = toAWTColor( foreground );
    java.awt.Color toColor = toAWTColor( background );
    LinearGradientPaint gradientPaint = new LinearGradientPaint( x,
                                                                 y,
                                                                 x + width,
                                                                 y + height,
                                                                 new float[]{
                                                                   0.0f, 1.0f
                                                                 },
                                                                 new java.awt.Color[]{
                                                                   fromColor,
                                                                   toColor
                                                                 } );
    graphics.setPaint( gradientPaint );
    graphics.fill( new java.awt.Rectangle( x, y, width, height ) );
  }

  // ////////////////
  // Line attributes
  public void setLineWidth( final int lineWidth ) {
    checkGC();
    if( this.lineWidth != lineWidth ) {
      this.lineWidth = lineWidth;
      lineStroke = null;
    }
  }

  public int getLineWidth() {
    checkGC();
    return ( int )lineWidth;
  }

  public void setLineCap( final int lineCap ) {
    checkGC();
    if( this.lineCap != lineCap ) {
      switch( lineCap ) {
        case SWT.CAP_ROUND:
        case SWT.CAP_FLAT:
        case SWT.CAP_SQUARE:
        break;
        default:
          SWT.error( SWT.ERROR_INVALID_ARGUMENT );
      }
      this.lineCap = lineCap;
      lineStroke = null;
    }
  }

  public int getLineCap() {
    checkGC();
    return lineCap;
  }

  public void setLineJoin( final int lineJoin ) {
    checkGC();
    if( this.lineJoin != lineJoin ) {
      switch( lineJoin ) {
        case SWT.JOIN_MITER:
        case SWT.JOIN_ROUND:
        case SWT.JOIN_BEVEL:
        break;
        default:
          SWT.error( SWT.ERROR_INVALID_ARGUMENT );
      }
      this.lineJoin = lineJoin;
      lineStroke = null;
    }
  }

  public int getLineJoin() {
    checkGC();
    return lineJoin;
  }

  public void setLineStyle( final int lineStyle ) {
    checkGC();
    if( this.lineStyle != lineStyle ) {
      switch( lineStyle ) {
        case SWT.LINE_SOLID:
        case SWT.LINE_DASH:
        case SWT.LINE_DOT:
        case SWT.LINE_DASHDOT:
        case SWT.LINE_DASHDOTDOT:
        case SWT.LINE_CUSTOM:
        break;
        default:
          SWT.error( SWT.ERROR_INVALID_ARGUMENT );
      }
      if( lineStyle == SWT.LINE_CUSTOM && lineDashes == null ) {
        this.lineStyle = SWT.LINE_SOLID;
      } else {
        this.lineStyle = lineStyle;
      }
      lineStroke = null;
    }
  }

  public int getLineStyle() {
    checkGC();
    return lineStyle;
  }

  public void setLineDash( int[] dashes ) {
    checkGC();
    float[] lineDashes = this.lineDashes;
    if( dashes != null && dashes.length > 0 ) {
      boolean changed = this.lineStyle != SWT.LINE_CUSTOM
                        || lineDashes == null
                        || lineDashes.length != dashes.length;
      for( int i = 0; i < dashes.length; i++ ) {
        int dash = dashes[ i ];
        if( dash <= 0 ) {
          SWT.error( SWT.ERROR_INVALID_ARGUMENT );
        }
        if( !changed && lineDashes[ i ] != dash )
          changed = true;
      }
      if( changed ) {
        this.lineDashes = new float[ dashes.length ];
        for( int i = 0; i < dashes.length; i++ ) {
          this.lineDashes[ i ] = dashes[ i ];
        }
        this.lineStyle = SWT.LINE_CUSTOM;
        lineStroke = null;
      }
    } else {
      if( this.lineStyle != SWT.LINE_SOLID
          || ( lineDashes != null && lineDashes.length != 0 ) )
      {
        this.lineDashes = null;
        this.lineStyle = SWT.LINE_SOLID;
        lineStroke = null;
      }
    }
  }

  public int[] getLineDash() {
    checkGC();
    int[] result = null;
    if( lineDashes != null ) {
      result = new int[ lineDashes.length ];
      for( int i = 0; i < result.length; i++ ) {
        result[ i ] = ( int )lineDashes[ i ];
      }
    }
    return result;
  }

  public void setLineAttributes( final LineAttributes attributes ) {
    checkGC();
    if( attributes == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    boolean changed = false;
    float lineWidth = attributes.width;
    if( lineWidth != this.lineWidth ) {
      changed = true;
    }
    int lineStyle = attributes.style;
    if( lineStyle != this.lineStyle ) {
      changed = true;
      switch( lineStyle ) {
        case SWT.LINE_SOLID:
        case SWT.LINE_DASH:
        case SWT.LINE_DOT:
        case SWT.LINE_DASHDOT:
        case SWT.LINE_DASHDOTDOT:
        break;
        case SWT.LINE_CUSTOM:
          if( attributes.dash == null )
            lineStyle = SWT.LINE_SOLID;
        break;
        default:
          SWT.error( SWT.ERROR_INVALID_ARGUMENT );
      }
    }
    int join = attributes.join;
    if( join != this.lineJoin ) {
      changed = true;
      switch( join ) {
        case SWT.CAP_ROUND:
        case SWT.CAP_FLAT:
        case SWT.CAP_SQUARE:
        break;
        default:
          SWT.error( SWT.ERROR_INVALID_ARGUMENT );
      }
    }
    int cap = attributes.join;
    if( cap != this.lineCap ) {
      changed = true;
      switch( cap ) {
        case SWT.JOIN_MITER:
        case SWT.JOIN_ROUND:
        case SWT.JOIN_BEVEL:
        break;
        default:
          SWT.error( SWT.ERROR_INVALID_ARGUMENT );
      }
    }
    float[] dashes = attributes.dash;
    float[] lineDashes = this.lineDashes;
    if( dashes != null && dashes.length > 0 ) {
      boolean dashesChanged = lineDashes == null
                              || lineDashes.length != dashes.length;
      for( int i = 0; i < dashes.length; i++ ) {
        float dash = dashes[ i ];
        if( dash <= 0 )
          SWT.error( SWT.ERROR_INVALID_ARGUMENT );
        if( !dashesChanged && lineDashes[ i ] != dash )
          dashesChanged = true;
      }
      if( dashesChanged ) {
        float[] newDashes = new float[ dashes.length ];
        System.arraycopy( dashes, 0, newDashes, 0, dashes.length );
        dashes = newDashes;
        changed = true;
      } else {
        dashes = lineDashes;
      }
    } else {
      if( lineDashes != null && lineDashes.length > 0 ) {
        changed = true;
      } else {
        dashes = lineDashes;
      }
    }
    float dashOffset = attributes.dashOffset;
    if( dashOffset != this.lineDashesOffset ) {
      changed = true;
    }
    float miterLimit = attributes.miterLimit;
    if( miterLimit != this.lineMiterLimit ) {
      changed = true;
    }
    if( changed ) {
      this.lineWidth = lineWidth;
      this.lineStyle = lineStyle;
      this.lineCap = cap;
      this.lineJoin = join;
      this.lineDashes = dashes;
      this.lineDashesOffset = dashOffset;
      this.lineMiterLimit = miterLimit;
      lineStroke = null;
    }
  }

  public LineAttributes getLineAttributes() {
    checkGC();
    float[] dashes = null;
    if( lineDashes != null ) {
      dashes = new float[ lineDashes.length ];
      System.arraycopy( lineDashes, 0, dashes, 0, dashes.length );
    }
    return new LineAttributes( lineWidth,
                               lineCap,
                               lineJoin,
                               lineStyle,
                               dashes,
                               lineDashesOffset,
                               lineMiterLimit );
  }

  // ////////////////
  // Drawing methods
  public void drawLine( final int x1, final int y1, final int x2, final int y2 )
  {
    checkGC();
    applyColor( foreground );
    applyLineStroke();
    Line2D.Float line = new Line2D.Float( x1, y1, x2, y2 );
    graphics.draw( line );
  }

  public void drawRectangle( final Rectangle rect ) {
    if( rect == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    drawRectangle( rect.x, rect.y, rect.width, rect.height );
  }

  public void drawRectangle( final int x,
                             final int y,
                             final int width,
                             final int height )
  {
    checkGC();
    applyColor( foreground );
    graphics.drawRect( x, y, width - 1, height - 1 );
  }

  public void drawRoundRectangle( final int x,
                                  final int y,
                                  final int width,
                                  final int height,
                                  final int arcWidth,
                                  final int arcHeight )
  {
    checkGC();
    applyColor( foreground );
    graphics.drawRoundRect( x, y, width - 1, height - 1, arcWidth, arcHeight );
  }

  public void fillPolygon( int[] shapeArray ) {
    applyColor( background );
    graphics.fillPolygon( getPolygon( shapeArray ) );
  }

  public void drawPolygon( int[] shapeArray ) {
    applyColor( foreground );
    graphics.drawPolygon( getPolygon( shapeArray ) );
  }

  private Polygon getPolygon( int[] shapeArray ) {
    Polygon result = new Polygon();
    int numberOfPoints = shapeArray.length / 2;
    int[] xcoords = new int[ numberOfPoints ];
    int[] ycoords = new int[ numberOfPoints ];
    for( int i = 0; i <= numberOfPoints; i++ ) {
      xcoords[ i ] = shapeArray[ i ];
      xcoords[ i ] = shapeArray[ i + 1 ];
    }
    result.npoints = numberOfPoints;
    result.xpoints = xcoords;
    result.ypoints = ycoords;
    return result;
  }

  public void drawImage( final Image image, final int x, final int y ) {
    checkGC();
    if( image == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    BufferedImage awtImage = toAWTImage( image );
    graphics.drawImage( awtImage, x, y, null );
  }

  // ///////////////////////
  // dispose and isDisposed
  public void dispose() {
    writeImage();
    graphics.dispose();
    graphics = null;
    bufferedImage = null;
    image = null;
  }

  public boolean isDisposed() {
    return bufferedImage == null;
  }

  public String toString() {
    String result;
    if( isDisposed() ) {
      result = "GC {*DISPOSED*}";
    } else {
      result = "GC {" + bufferedImage + "}";
    }
    return result;
  }

  // ///////////////////////////////
  // Helping methods to write image
  private void writeImage() {
    ImageWriter writer = getImageWriter();
    File location = getImageResourceLocation( image );
    try {
      ImageOutputStream stream = ImageIO.createImageOutputStream( location );
      try {
        writer.setOutput( stream );
        writer.write( bufferedImage );
      } finally {
        stream.close();
      }
    } catch( IOException e ) {
      SWT.error( SWT.ERROR_IO, e );
    } finally {
      writer.dispose();
    }
  }

  private static ImageWriter getImageWriter() {
    ImageWriter result = null;
    Iterator imageWriters = ImageIO.getImageWritersByFormatName( IMAGE_FORMAT );
    if( imageWriters.hasNext() ) {
      result = ( ImageWriter )imageWriters.next();
    }
    if( result == null ) {
      SWT.error( SWT.ERROR_UNSUPPORTED_FORMAT, null, IMAGE_FORMAT );
    }
    return result;
  }

  private static File getImageResourceLocation( final Image image ) {
    IEngineConfig engineConfig = ConfigurationReader.getEngineConfig();
    File serverContextDir = engineConfig.getServerContextDir();
    String filename = "gen_" + image.hashCode();
    ResourceFactory.images.put( filename, image );
    return new File( serverContextDir, filename );
  }

  // /////////////////////////////
  // AWT Graphics Helping Methods
  private void applyColor( final Color color ) {
    graphics.setColor( toAWTColor( color ) );
  }

  private void applyLineStroke() {
    if( lineStroke == null ) {
      int awtLineCap = -1;
      switch( lineCap ) {
        case SWT.CAP_FLAT:
          awtLineCap = BasicStroke.CAP_BUTT;
        break;
        case SWT.CAP_ROUND:
          awtLineCap = BasicStroke.CAP_ROUND;
        break;
        case SWT.CAP_SQUARE:
          awtLineCap = BasicStroke.CAP_SQUARE;
        break;
      }
      int awtLineJoin = -1;
      switch( lineJoin ) {
        case SWT.JOIN_BEVEL:
          awtLineJoin = BasicStroke.JOIN_BEVEL;
        break;
        case SWT.JOIN_ROUND:
          awtLineJoin = BasicStroke.JOIN_ROUND;
        break;
        case SWT.JOIN_MITER:
          awtLineJoin = BasicStroke.JOIN_MITER;
        break;
      }
      float[] awtDashes = null;
      switch( lineStyle ) {
        case SWT.LINE_SOLID:
        break;
        case SWT.LINE_DASH:
          new UnsupportedOperationException( "dashes not yet implemented" );
        break;
        case SWT.LINE_DASHDOT:
          new UnsupportedOperationException( "dashes not yet implemented" );
        break;
        case SWT.LINE_DASHDOTDOT:
          new UnsupportedOperationException( "dashes not yet implemented" );
        break;
        case SWT.LINE_DOT:
          new UnsupportedOperationException( "dashes not yet implemented" );
        break;
        case SWT.LINE_CUSTOM:
          awtDashes = new float[ lineDashes.length ];
          for( int i = 0; i < lineDashes.length; i++ ) {
            awtDashes[ i ] = lineDashes[ i ];
          }
        break;
      }
      lineStroke = new BasicStroke( lineWidth,
                                    awtLineCap,
                                    awtLineJoin,
                                    lineMiterLimit,
                                    awtDashes,
                                    lineDashesOffset );
      graphics.setStroke( lineStroke );
    }
  }

  // //////////////////////
  // SWT to AWT conversion
  private static java.awt.Color toAWTColor( final Color color ) {
    return new java.awt.Color( color.getRed(),
                               color.getGreen(),
                               color.getBlue() );
  }

  private BufferedImage toAWTImage( final Image image ) {
    BufferedImage result = null;
    IResourceManager manager = ResourceManager.getInstance();
    String imagePath = ResourceFactory.getImagePath( image );
    try {
      InputStream inputStream = manager.getRegisteredContent( imagePath );
      try {
        boolean saveUseCache = ImageIO.getUseCache();
        ImageIO.setUseCache( false );
        try {
          result = ImageIO.read( inputStream );
        } finally {
          ImageIO.setUseCache( saveUseCache );
        }
      } finally {
        inputStream.close();
      }
    } catch( IOException e ) {
      String msg = "Failed to read input stream of image: " + imagePath;
      SWT.error( SWT.ERROR_IO, e, msg );
    }
    return result;
  }

  // ////////////////
  // Helping methods
  private void initDefaults() {
    // white background
    background = org.eclipse.rwt.graphics.Graphics.getColor( 255, 255, 255 );
    // black foreground
    foreground = org.eclipse.rwt.graphics.Graphics.getColor( 0, 0, 0 );
    // line attributes
    lineWidth = 0;
    lineCap = SWT.CAP_FLAT;
    lineJoin = SWT.JOIN_MITER;
    lineStyle = SWT.LINE_SOLID;
    lineDashes = null;
    lineDashesOffset = 0;
    lineMiterLimit = 10;
    // fill with default GC bg as the default Graphics bg is different
    Rectangle bounds = image.getBounds();
    applyColor( background );
    graphics.fillRect( 0, 0, bounds.width, image.getBounds().height );
    // set antialias to default
    graphics.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                               RenderingHints.VALUE_ANTIALIAS_DEFAULT );
  }

  private void checkGC() {
    if( bufferedImage == null ) {
      SWT.error( SWT.ERROR_GRAPHIC_DISPOSED );
    }
  }

  private static BufferedImage createBufferedImage( final Image image ) {
    Rectangle bounds = image.getBounds();
    int type = BufferedImage.TYPE_INT_RGB;
    return new BufferedImage( bounds.width, bounds.height, type );
  }

  private static int checkStyle( final int style ) {
    // if ((style & SWT.LEFT_TO_RIGHT) != 0) style &= ~SWT.RIGHT_TO_LEFT;
    // return style & (SWT.LEFT_TO_RIGHT | SWT.RIGHT_TO_LEFT);
    return style;
  }

  public Device getDevice() {
    return Display.getCurrent();
  }

  public void fillRoundRectangle( int x,
                                  int y,
                                  int width,
                                  int height,
                                  int arcWidth,
                                  int arcHeight )
  {
    checkGC();
    applyColor( background );
    graphics.fillRoundRect( x, y, width, height, arcWidth, arcHeight );
  }
}
