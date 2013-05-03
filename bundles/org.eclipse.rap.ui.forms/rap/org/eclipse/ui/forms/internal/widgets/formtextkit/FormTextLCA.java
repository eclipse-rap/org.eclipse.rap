/*******************************************************************************
 * Copyright (c) 2009, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.ui.forms.internal.widgets.formtextkit;

import static org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory.getClientObject;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.rap.json.*;
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


@SuppressWarnings("restriction")
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

  @Override
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

  @Override
  public void renderInitialization( Widget widget ) throws IOException {
    FormText formText = ( FormText )widget;
    IClientObject clientObject = getClientObject( formText );
    clientObject.create( TYPE );
    clientObject.set( "parent", getId( formText.getParent() ) ); //$NON-NLS-1$
  }

  public void readData( Widget widget ) {
    FormText formText = ( FormText )widget;
    ControlLCAUtil.processSelection( formText, null, false );
    ControlLCAUtil.processEvents( formText );
    ControlLCAUtil.processKeyEvents( formText );
  }

  @Override
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
      JsonArray args = new JsonArray()
        .add( underlineMode )
        .add( getColorAsArray( foreground ) )
        .add( getColorAsArray( activeForeground ) );
      getClientObject( formText ).set( PROP_HYPERLINK_SETTINGS, args );
    }
  }

  private static void renderText( FormText formText ) {
    if(    hasLayoutChanged( formText )
        || hasResourceTableChanged( formText )
        || hasBoundsChanged( formText ) )
    {
      Paragraph[] paragraphs = getParagraphs( formText );
      JsonArray buffer = new JsonArray();
      for( int i = 0; i < paragraphs.length; i++ ) {
        Paragraph paragraph = paragraphs[ i ];
        if( paragraph instanceof BulletParagraph ) {
          BulletParagraph bullet = ( BulletParagraph )paragraph;
          appendBullet( formText, bullet, buffer );
        }
        ParagraphSegment[] segments = paragraph.getSegments();
        appendSegments( formText, segments, buffer );
      }
      getClientObject( formText ).set( PROP_TEXT, buffer );
    }
  }

  private static void appendBullet( FormText formText,
                                    BulletParagraph bullet,
                                    JsonArray buffer )
  {
    int style = bullet.getBulletStyle();
    Image image = getBulletImage( formText, bullet );
    String imageName = ImageFactory.getImagePath( image );
    String text = bullet.getBulletText();
    Rectangle bounds = getBulletBounds( bullet );
    // [if] If <li> "style" attribute is set to "text" and there is no text set
    // ( no "value" attribute ) the bullet bounds are null
    if( bounds != null ) {
      JsonArray args = new JsonArray()
        .add( "bullet" ) //$NON-NLS-1$
        .add( style )
        .add( imageName )
        .add( text )
        .add( getBoundsAsArray( bounds ) );
      buffer.add( args );
    }
  }

  private static void appendSegments( FormText formText,
                                      ParagraphSegment[] segments,
                                      JsonArray buffer )
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
                                                  JsonArray buffer )
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
      JsonArray args = new JsonArray()
        .add( "textHyperlink" ) //$NON-NLS-1$
        .add( textFragments[ i ] )
        .add( tooltipText )
        .add( getBoundsAsArray( textFragmentsBounds[ i ] ) )
        .add( getFontAsArray( font ) );
      buffer.add( args );
    }
  }

  private static void appendTextSegment( FormText formText,
                                         TextSegment segment,
                                         JsonArray buffer )
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
      JsonArray args = new JsonArray()
        .add( "text" ) //$NON-NLS-1$
        .add( textFragments[ i ] )
        .add( getBoundsAsArray( textFragmentsBounds[ i ] ) )
        .add( getFontAsArray( font ) )
        .add( getColorAsArray( color ) );
      buffer.add( args );
    }
  }

  private static void appendImageHyperlinkSegment( FormText formText,
                                                   ImageHyperlinkSegment segment,
                                                   JsonArray buffer )
  {
    String tooltipText = segment.getTooltipText();
    Rectangle bounds = segment.getBounds();
    Image image = segment.getImage( getResourceTable( formText ) );
    String imageName = ImageFactory.getImagePath( image );
    JsonArray args = new JsonArray()
      .add( "imageHyperlink" ) //$NON-NLS-1$
      .add( imageName )
      .add( tooltipText )
      .add( getBoundsAsArray( bounds ) );
    buffer.add( args );
  }

  private static void appendImageSegment( FormText formText,
                                          ImageSegment segment,
                                          JsonArray buffer )
  {
    Rectangle bounds = segment.getBounds();
    Image image = segment.getImage( getResourceTable( formText ) );
    String imageName = ImageFactory.getImagePath( image );
    JsonArray args = new JsonArray()
      .add( "image" ) //$NON-NLS-1$
      .add( imageName )
      .add( getBoundsAsArray( bounds ) );
    buffer.add( args );
  }

  private static void appendAggregateHyperlinkSegment( FormText formText,
                                                       AggregateHyperlinkSegment segment,
                                                       JsonArray buffer )
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
    Image bulletImage = getImage( formText.getDisplay(), BULLET_CIRCLE_GIF );
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
    return getAdapter( bullet ).getBulletBounds();
  }

  private static Hashtable getResourceTable( FormText formText ) {
    return getAdapter( formText ).getResourceTable();
  }

  private static String[] getTextFragments( TextSegment segment ) {
    return getAdapter( segment ).getTextFragments();
  }

  private static Rectangle[] getTextFragmentsBounds( TextSegment segment ) {
    return getAdapter( segment ).getTextFragmentsBounds();
  }

  private static String getFontId( TextSegment segment ) {
    return getAdapter( segment ).getFontId();
  }

  private static Object[] getHyperlinkSegments( AggregateHyperlinkSegment segment ) {
    return getAdapter( segment ).getHyperlinkSegments();
  }

  private static JsonArray getBoundsAsArray( Rectangle bounds ) {
    return new JsonArray().add( bounds.x ).add( bounds.y ).add( bounds.width ).add( bounds.height );
  }

  private static JsonValue getFontAsArray( Font font ) {
    JsonValue result = JsonObject.NULL;
    if( font != null ) {
      result = new JsonArray()
        .add( getFontName( font ) )
        .add( getFontSize( font ) )
        .add( getFontStyle( font, SWT.BOLD ) )
        .add( getFontStyle( font, SWT.ITALIC ) );
    }
    return result;
  }

  private static JsonArray getFontName( Font font ) {
    JsonArray resutl = new JsonArray();
    FontData fontData = font.getFontData()[ 0 ];
    String fontName = fontData.getName();
    String[] names = fontName.split( "," ); //$NON-NLS-1$
    for( int i = 0; i < names.length; i++ ) {
      names[ i ] = names[ i ].trim();
      Matcher matcher = FONT_NAME_FILTER_PATTERN.matcher( names[ i ] );
      names[ i ] = matcher.replaceAll( "" ); //$NON-NLS-1$
      resutl.add( names[ i ] );
    }
    return resutl;
  }

  private static int getFontSize( Font font ) {
    return font.getFontData()[ 0 ].getHeight();
  }

  private static boolean getFontStyle( Font font, int style ) {
    return ( font.getFontData()[ 0 ].getStyle() & style ) != 0;
  }

  private static JsonValue getColorAsArray( Color color ) {
    JsonValue result = JsonObject.NULL;
    if( color != null ) {
      RGB rgb = color.getRGB();
      result = new JsonArray().add( rgb.red ).add( rgb.green ).add( rgb.blue ).add( 255 );
    }
    return result;
  }

  public static Image getImage( Device device, String path ) {
    ClassLoader classLoader = FormTextLCA.class.getClassLoader();
    InputStream inputStream = classLoader.getResourceAsStream( path );
    Image result = null;
    if( inputStream != null ) {
      try {
        result = new Image( device, inputStream );
      } finally {
        try {
          inputStream.close();
        } catch( IOException e ) {
          // ignore
        }
      }
    }
    return result;
  }

}
