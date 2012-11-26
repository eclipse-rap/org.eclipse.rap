/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.ui.forms.internal.widgets.formtextkit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.rap.rwt.graphics.Graphics;
import org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rap.rwt.internal.protocol.IClientObject;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.HyperlinkSettings;
import org.eclipse.ui.forms.internal.widgets.*;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.internal.forms.widgets.*;


public class FormTextLCA extends AbstractWidgetLCA {

  private static final String TYPE = "forms.widgets.FormText"; //$NON-NLS-1$

  private static final Pattern FONT_NAME_FILTER_PATTERN
    = Pattern.compile( "\"|\\\\" ); //$NON-NLS-1$
  private static final String PREFIX = "resource/widget/rap/formtext/"; //$NON-NLS-1$
  private static final String BULLET_CIRCLE_GIF = PREFIX + "bullet_circle.gif"; //$NON-NLS-1$

  // Property names for preserveValues
  private static final String PROP_BOUNDS = "bounds"; //$NON-NLS-1$
  private static final String PROP_TEXT = "text"; //$NON-NLS-1$
  private static final String PROP_HYPERLINK_SETTINGS = "hyperlinkSettings"; //$NON-NLS-1$
  private static final String PROP_HYPERLINK_UNDERLINE_MODE = "hyperlinkUnderlineMode"; //$NON-NLS-1$
  private static final String PROP_HYPERLINK_FOREGROUND = "hyperlinkForeground"; //$NON-NLS-1$
  private static final String PROP_HYPERLINK_ACTIVE_FOREGROUND = "hyperlinkActiveForeground"; //$NON-NLS-1$
  private static final String PROP_RESOURCE_TABLE = "resourceTable"; //$NON-NLS-1$

  public void preserveValues( Widget widget ) {
    FormText formText = ( FormText )widget;
    ControlLCAUtil.preserveValues( formText );
    WidgetLCAUtil.preserveCustomVariant( formText );
    HyperlinkSettings settings = formText.getHyperlinkSettings();
    WidgetLCAUtil.preserveProperty( formText,
                                    PROP_HYPERLINK_UNDERLINE_MODE,
                                    settings.getHyperlinkUnderlineMode() );
    WidgetLCAUtil.preserveProperty( formText,
                                    PROP_HYPERLINK_FOREGROUND,
                                    settings.getForeground() );
    WidgetLCAUtil.preserveProperty( formText,
                                    PROP_HYPERLINK_ACTIVE_FOREGROUND,
                                    settings.getActiveForeground() );
    WidgetLCAUtil.preserveProperty( widget, PROP_RESOURCE_TABLE, getResourceTable( formText ) );
  }

  public void renderInitialization( Widget widget ) throws IOException {
    FormText formText = ( FormText )widget;
    IClientObject clientObject = ClientObjectFactory.getClientObject( formText );
    clientObject.create( TYPE );
    clientObject.set( "parent", WidgetUtil.getId( formText.getParent() ) ); //$NON-NLS-1$
  }

  public void readData( Widget widget ) {
    FormText formText = ( FormText )widget;
    ControlLCAUtil.processSelection( formText, null, false );
    ControlLCAUtil.processEvents( formText );
    ControlLCAUtil.processKeyEvents( formText );
  }

  public void renderChanges( Widget widget ) throws IOException {
    FormText formText = ( FormText )widget;
    ControlLCAUtil.renderChanges( formText );
    WidgetLCAUtil.renderCustomVariant( formText );
    renderHyperlinkSettings( formText );
    renderText( formText );
  }

  ////////////////////////////
  // Render changed properties

  private static void renderHyperlinkSettings( FormText formText ) {
    if( hasHyperlinkSettingsChanged( formText ) ) {
      HyperlinkSettings newValue = formText.getHyperlinkSettings();
      int underlineMode = newValue.getHyperlinkUnderlineMode();
      Color foreground = newValue.getForeground();
      Color activeForeground = newValue.getActiveForeground();
      Object[] args = new Object[] {
        new Integer( underlineMode ),
        getColorAsArray( foreground ),
        getColorAsArray( activeForeground )
      };
      IClientObject clientObject = ClientObjectFactory.getClientObject( formText );
      clientObject.set( PROP_HYPERLINK_SETTINGS, args );
    }
  }

  private static void renderText( FormText formText ) {
    if(    hasLayoutChanged( formText )
        || hasResourceTableChanged( formText )
        || hasBoundsChanged( formText ) )
    {
      Paragraph[] paragraphs = getParagraphs( formText );
      ArrayList buffer = new ArrayList();
      for( int i = 0; i < paragraphs.length; i++ ) {
        Paragraph paragraph = paragraphs[ i ];
        if( paragraph instanceof BulletParagraph ) {
          BulletParagraph bullet = ( BulletParagraph )paragraph;
          appendBullet( formText, bullet, buffer );
        }
        ParagraphSegment[] segments = paragraph.getSegments();
        appendSegments( formText, segments, buffer );
      }
      IClientObject clientObject = ClientObjectFactory.getClientObject( formText );
      clientObject.set( PROP_TEXT, buffer.toArray( new Object[ 0 ] ) );
    }
  }

  private static void appendBullet( FormText formText, BulletParagraph bullet, ArrayList buffer ) {
    int style = bullet.getBulletStyle();
    Image image = getBulletImage( formText, bullet );
    String imageName = ImageFactory.getImagePath( image );
    String text = bullet.getBulletText();
    Rectangle bounds = getBulletBounds( bullet );
    // [if] If <li> "style" attribute is set to "text" and there is no text set
    // ( no "value" attribute ) the bullet bounds are null
    if( bounds != null ) {
      Object[] args = new Object[] {
        "bullet", //$NON-NLS-1$
        new Integer( style ),
        imageName,
        text,
        getBoundsAsArray( bounds )
      };
      buffer.add( args );
    }
  }

  private static void appendSegments( FormText formText,
                                      ParagraphSegment[] segments,
                                      ArrayList buffer )
  {
    for( int i = 0; i < segments.length; i++ ) {
      ParagraphSegment segment = segments[ i ];
      if( segment instanceof TextHyperlinkSegment ) {
        appendTextHyperlinkSegment( formText, ( TextHyperlinkSegment )segment, buffer );
      } else if( segment instanceof TextSegment ) {
        appendTextSegment( formText, ( TextSegment )segment, buffer );
      } else if( segment instanceof ImageHyperlinkSegment ) {
        appendImageHyperlinkSegment( formText, ( ImageHyperlinkSegment )segment, buffer );
      } else if( segment instanceof ImageSegment ) {
        appendImageSegment( formText, ( ImageSegment )segment, buffer );
      } else if( segment instanceof AggregateHyperlinkSegment ) {
        appendAggregateHyperlinkSegment( formText, ( AggregateHyperlinkSegment )segment, buffer );
      }
    }
  }

  private static void appendTextHyperlinkSegment( FormText formText,
                                                  TextHyperlinkSegment segment,
                                                  ArrayList buffer )
  {
    String[] textFragments = getTextFragments( segment );
    String tooltipText = segment.getTooltipText();
    Rectangle[] textFragmentsBounds = getTextFragmentsBounds( segment );
    String fontId = getFontId( segment );
    Font font = null;
    if( fontId != null ) {
      font = ( Font )getResourceTable( formText ).get( fontId );
    }
    for( int i = 0; i < textFragments.length; i++ ) {
      Object[] args = new Object[] {
        "textHyperlink", //$NON-NLS-1$
        textFragments[ i ],
        tooltipText,
        getBoundsAsArray( textFragmentsBounds[ i ] ),
        getFontAsArray( font ),
      };
      buffer.add( args );
    }
  }

  private static void appendTextSegment( FormText formText,
                                         TextSegment segment,
                                         ArrayList buffer )
  {
    String[] textFragments = getTextFragments( segment );
    Rectangle[] textFragmentsBounds = getTextFragmentsBounds( segment );
    String fontId = getFontId( segment );
    Font font = null;
    if( fontId != null ) {
      font = ( Font )getResourceTable( formText ).get( fontId );
    }
    String colorId = segment.getColorId();
    Color color = null;
    if( colorId != null ) {
      color = ( Color )getResourceTable( formText ).get( colorId );
    }
    for( int i = 0; i < textFragments.length; i++ ) {
      Object[] args = new Object[] {
        "text", //$NON-NLS-1$
        textFragments[ i ],
        getBoundsAsArray( textFragmentsBounds[ i ] ),
        getFontAsArray( font ),
        getColorAsArray( color )
      };
      buffer.add( args );
    }
  }

  private static void appendImageHyperlinkSegment( FormText formText,
                                                   ImageHyperlinkSegment segment,
                                                   ArrayList buffer )
  {
    String tooltipText = segment.getTooltipText();
    Rectangle bounds = segment.getBounds();
    Image image = segment.getImage( getResourceTable( formText ) );
    String imageName = ImageFactory.getImagePath( image );
    Object[] args = new Object[] {
      "imageHyperlink", //$NON-NLS-1$
      imageName,
      tooltipText,
      getBoundsAsArray( bounds )
    };
    buffer.add( args );
  }

  private static void appendImageSegment( FormText formText,
                                          ImageSegment segment,
                                          ArrayList buffer )
  {
    Rectangle bounds = segment.getBounds();
    Image image = segment.getImage( getResourceTable( formText ) );
    String imageName = ImageFactory.getImagePath( image );
    Object[] args = new Object[] {
      "image", //$NON-NLS-1$
      imageName,
      getBoundsAsArray( bounds )
    };
    buffer.add( args );
  }

  private static void appendAggregateHyperlinkSegment( FormText formText,
                                                       AggregateHyperlinkSegment segment,
                                                       ArrayList buffer )
  {
    Object[] segments = getHyperlinkSegments( segment );
    for( int i = 0; i < segments.length; i++ ) {
      Object hyperlinkSegment = segments[ i ];
      if( hyperlinkSegment instanceof TextHyperlinkSegment ) {
        appendTextHyperlinkSegment( formText,
                                   ( TextHyperlinkSegment )hyperlinkSegment,
                                   buffer );
      } else if( hyperlinkSegment instanceof ImageHyperlinkSegment ) {
        appendImageHyperlinkSegment( formText,
                                    ( ImageHyperlinkSegment )hyperlinkSegment,
                                    buffer );
      }
    }
  }

  //////////////////
  // Helping methods

  private static boolean hasHyperlinkSettingsChanged( FormText formText ) {
    HyperlinkSettings newValue = formText.getHyperlinkSettings();
    Integer underlineMode = new Integer( newValue.getHyperlinkUnderlineMode() );
    Color foreground = newValue.getForeground();
    Color actForeground = newValue.getActiveForeground();
    return    WidgetLCAUtil.hasChanged( formText, PROP_HYPERLINK_UNDERLINE_MODE, underlineMode )
           || WidgetLCAUtil.hasChanged( formText, PROP_HYPERLINK_FOREGROUND, foreground )
           || WidgetLCAUtil.hasChanged( formText, PROP_HYPERLINK_ACTIVE_FOREGROUND, actForeground );
  }

  private static Paragraph[] getParagraphs( FormText formText ) {
    IFormTextAdapter adapter = getAdapter( formText );
    return adapter.getParagraphs();
  }

  private static boolean hasLayoutChanged( FormText formText ) {
    IFormTextAdapter adapter = getAdapter( formText );
    return adapter.hasLayoutChanged();
  }

  private static boolean hasResourceTableChanged( FormText formText ) {
    Hashtable resourceTable = getResourceTable( formText );
    return WidgetLCAUtil.hasChanged( formText, PROP_RESOURCE_TABLE, resourceTable );
  }

  private static boolean hasBoundsChanged( FormText formText ) {
    return WidgetLCAUtil.hasChanged( formText, PROP_BOUNDS, formText.getBounds() );
  }

  private static IFormTextAdapter getAdapter( FormText formText ) {
    Object adapter = formText.getAdapter( IFormTextAdapter.class );
    return ( IFormTextAdapter )adapter;
  }

  private static ITextSegmentAdapter getAdapter( TextSegment segment ) {
    Object adapter = segment.getAdapter( ITextSegmentAdapter.class );
    return ( ITextSegmentAdapter )adapter;
  }

  private static IBulletParagraphAdapter getAdapter( BulletParagraph bullet ) {
    Object adapter = bullet.getAdapter( IBulletParagraphAdapter.class );
    return ( IBulletParagraphAdapter )adapter;
  }

  private static IAggregateHyperlinkSegmentAdapter getAdapter( AggregateHyperlinkSegment segment ) {
    Object adapter = segment.getAdapter( IAggregateHyperlinkSegmentAdapter.class );
    return ( IAggregateHyperlinkSegmentAdapter )adapter;
  }

  private static Image getBulletImage( FormText formText, BulletParagraph bullet ) {
    ClassLoader classLoader = FormTextLCA.class.getClassLoader();
    Image bulletImage = Graphics.getImage( BULLET_CIRCLE_GIF, classLoader );
    if( bullet.getBulletStyle() == BulletParagraph.IMAGE ) {
      String text = bullet.getBulletText();
      if( text != null ) {
        Image img = ( Image )getResourceTable( formText ).get( text );
        if( img != null ) {
          bulletImage = img;
        }
      }
    }
    return bulletImage;
  }

  private static Rectangle getBulletBounds( BulletParagraph bullet ) {
    IBulletParagraphAdapter bulletParagraphAdapter = getAdapter( bullet );
    return bulletParagraphAdapter.getBulletBounds();
  }

  private static Hashtable getResourceTable( FormText formText ) {
    IFormTextAdapter adapter = getAdapter( formText );
    return adapter.getResourceTable();
  }

  private static String[] getTextFragments( TextSegment segment ) {
    ITextSegmentAdapter textSegmentAdapter = getAdapter( segment );
    return textSegmentAdapter.getTextFragments();
  }

  private static Rectangle[] getTextFragmentsBounds( TextSegment segment ) {
    ITextSegmentAdapter textSegmentAdapter = getAdapter( segment );
    return textSegmentAdapter.getTextFragmentsBounds();
  }

  private static String getFontId( TextSegment segment ) {
    ITextSegmentAdapter textSegmentAdapter = getAdapter( segment );
    return textSegmentAdapter.getFontId();
  }

  private static Object[] getHyperlinkSegments( AggregateHyperlinkSegment segment ) {
    IAggregateHyperlinkSegmentAdapter hyperlinkSegmentAdapter = getAdapter( segment );
    return hyperlinkSegmentAdapter.getHyperlinkSegments();
  }

  private static int[] getBoundsAsArray( Rectangle bounds ) {
    return new int[] { bounds.x, bounds.y, bounds.width, bounds.height };
  }

  private static Object[] getFontAsArray( Font font ) {
    Object[] result = null;
    if( font != null ) {
      result = new Object[] {
        getFontName( font ),
        getFontSize( font ),
        getFontStyle( font, SWT.BOLD ),
        getFontStyle( font, SWT.ITALIC )
      };
    }
    return result;
  }

  private static String[] getFontName( Font font ) {
    FontData fontData = font.getFontData()[ 0 ];
    String fontName = fontData.getName();
    String[] result = fontName.split( "," ); //$NON-NLS-1$
    for( int i = 0; i < result.length; i++ ) {
      result[ i ] = result[ i ].trim();
      Matcher matcher = FONT_NAME_FILTER_PATTERN.matcher( result[ i ] );
      result[ i ] = matcher.replaceAll( "" ); //$NON-NLS-1$
    }
    return result;
  }

  private static Integer getFontSize( Font font ) {
    FontData fontData = font.getFontData()[ 0 ];
    return new Integer( fontData.getHeight() );
  }

  private static Boolean getFontStyle( Font font, int style ) {
    FontData fontData = font.getFontData()[ 0 ];
    return Boolean.valueOf( ( fontData.getStyle() & style ) != 0 );
  }

  private static int[] getColorAsArray( Color color ) {
    int[] result = null;
    if( color != null ) {
      RGB rgb = color.getRGB();
      result = new int[] { rgb.red, rgb.green, rgb.blue, 255 };
    }
    return result;
  }
}
