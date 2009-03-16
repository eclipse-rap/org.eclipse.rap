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

package org.eclipse.ui.forms.internal.widgets.formtextkit;

import java.io.IOException;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.graphics.ResourceFactory;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.HyperlinkSettings;
import org.eclipse.ui.forms.internal.widgets.*;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.internal.forms.widgets.*;

public class FormTextLCA extends AbstractWidgetLCA {

  private static final Pattern FONT_NAME_FILTER_PATTERN
    = Pattern.compile( "\"|\\\\" ); //$NON-NLS-1$
  private static final String PREFIX
    = "resource/widget/rap/formtext/"; //$NON-NLS-1$
  private static final String BULLET_CIRCLE_GIF
    = PREFIX + "bullet_circle.gif"; //$NON-NLS-1$

  // Property names for preserveValues
  private static final String PROP_PARAGRAPHS = "paragraphs"; //$NON-NLS-1$
  private static final String PROP_HYPERLINK_SETTINGS
    = "hyperlinkSettings"; //$NON-NLS-1$

  // Default values
  private static final Paragraph[] DEFAULT_PARAGRAPHS = new Paragraph[ 0 ];

  public void preserveValues( final Widget widget ) {
    FormText formText = ( FormText )widget;
    ControlLCAUtil.preserveValues( formText );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( formText );
    adapter.preserve( PROP_PARAGRAPHS, getParagraphs( formText ) );
    adapter.preserve( PROP_HYPERLINK_SETTINGS, formText.getHyperlinkSettings() );
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    FormText formText = ( FormText )widget;
    JSWriter writer = JSWriter.getWriterFor( formText );
    writer.newWidget( "org.eclipse.ui.forms.widgets.FormText" ); //$NON-NLS-1$
    ControlLCAUtil.writeStyleFlags( formText );
  }

  public void readData( final Widget widget ) {
    FormText formText = ( FormText )widget;
    ControlLCAUtil.processSelection( formText, null, false );
    ControlLCAUtil.processMouseEvents( formText );
    ControlLCAUtil.processKeyEvents( formText );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    FormText formText = ( FormText )widget;
    ControlLCAUtil.writeChanges( formText );
    writeHyperlinkSettings( formText );
    writeParagraphs( formText );
    WidgetLCAUtil.writeCustomVariant( formText );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }

  public void createResetHandlerCalls( final String typePoolId )
    throws IOException
  {
  }

  public String getTypePoolId( final Widget widget ) {
    return null;
  }

  ////////////////
  // Write changes
  private void writeParagraphs( final FormText formText ) throws IOException {
    Paragraph[] paragraphs = getParagraphs( formText );
    String prop = PROP_PARAGRAPHS;
    Paragraph[] defValue = DEFAULT_PARAGRAPHS;
    if(    WidgetLCAUtil.hasChanged( formText, prop, paragraphs, defValue )
        || hasLayoutChanged( formText ) ) {
      clearContent( formText );
      for( int i = 0; i < paragraphs.length; i++ ) {
        Paragraph paragraph = paragraphs[ i ];
        if( paragraph instanceof BulletParagraph ) {
          BulletParagraph bullet = ( BulletParagraph )paragraph;
          writeBullet( formText, bullet );
        }
        ParagraphSegment[] segments = paragraph.getSegments();
        writeSegments( formText, segments );
      }
      updateHyperlinks( formText );
    }
  }

  private void writeBullet( final FormText formText,
                            final BulletParagraph bullet )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( formText );
    int style = bullet.getBulletStyle();
    Image image = getBulletImage( formText, bullet );
    String imageName = ResourceFactory.getImagePath( image );
    String text = bullet.getBulletText();
    Rectangle bounds = getBulletBounds( bullet );
    Object[] args = new Object[] {
      new Integer( style ),
      imageName,
      text,
      new Integer( bounds.x ),
      new Integer( bounds.y ),
      new Integer( bounds.width ),
      new Integer( bounds.height )
    };
    writer.call( "createBullet", args ); //$NON-NLS-1$
  }

  private void writeSegments( final FormText formText,
                              final ParagraphSegment[] segments )
    throws IOException
  {
    for( int i = 0; i < segments.length; i++ ) {
      ParagraphSegment segment = segments[ i ];
      if( segment instanceof TextHyperlinkSegment ) {
         writeTextHyperlinkSegment( formText, ( TextHyperlinkSegment )segment );
      } else if( segment instanceof TextSegment ) {
         writeTextSegment( formText, ( TextSegment )segment );
      } else if( segment instanceof ImageHyperlinkSegment ) {
         writeImageHyperlinkSegment( formText, ( ImageHyperlinkSegment )segment );
      } else if( segment instanceof ImageSegment ) {
        writeImageSegment( formText, ( ImageSegment )segment );
      } else if( segment instanceof ControlSegment ) {
        writeControlSegment( formText, ( ControlSegment )segment );
      }
    }
  }

  private void writeTextSegment( final FormText formText,
                                 final TextSegment segment )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( formText );
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
        textFragments[ i ],
        new Integer( textFragmentsBounds[ i ].x ),
        new Integer( textFragmentsBounds[ i ].y ),
        new Integer( textFragmentsBounds[ i ].width ),
        new Integer( textFragmentsBounds[ i ].height ),
        getFontName( font ),
        getFontSize( font ),
        getFontStyle( font, SWT.BOLD ),
        getFontStyle( font, SWT.ITALIC ),
        colorToHtmlString( color )
      };
      writer.call( "createTextFragment", args ); //$NON-NLS-1$
    }
  }

  private void writeTextHyperlinkSegment( final FormText formText,
                                          final TextHyperlinkSegment segment )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( formText );
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
        textFragments[ i ],
        tooltipText,
        new Integer( textFragmentsBounds[ i ].x ),
        new Integer( textFragmentsBounds[ i ].y ),
        new Integer( textFragmentsBounds[ i ].width ),
        new Integer( textFragmentsBounds[ i ].height ),
        getFontName( font ),
        getFontSize( font ),
        getFontStyle( font, SWT.BOLD ),
        getFontStyle( font, SWT.ITALIC )
      };
      writer.call( "createTextHyperlinkSegment", args ); //$NON-NLS-1$
    }
  }

  private void writeImageSegment( final FormText formText,
                                  final ImageSegment segment )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( formText );
    Rectangle bounds = segment.getBounds();
    Image image = segment.getImage( getResourceTable( formText ) );
    String imageName = ResourceFactory.getImagePath( image );
    Object[] args = new Object[] {
      imageName,
      new Integer( bounds.x ),
      new Integer( bounds.y ),
      new Integer( bounds.width ),
      new Integer( bounds.height )
    };
    writer.call( "createImageSegment", args ); //$NON-NLS-1$
  }

  private void writeImageHyperlinkSegment( final FormText formText,
                                           final ImageHyperlinkSegment segment )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( formText );
    String tooltipText = segment.getTooltipText();
    Rectangle bounds = segment.getBounds();
    Image image = segment.getImage( getResourceTable( formText ) );
    String imageName = ResourceFactory.getImagePath( image );
    Object[] args = new Object[] {
      imageName,
      tooltipText,
      new Integer( bounds.x ),
      new Integer( bounds.y ),
      new Integer( bounds.width ),
      new Integer( bounds.height )
    };
    writer.call( "createImageHyperlinkSegment", args ); //$NON-NLS-1$
  }

  private void writeControlSegment( final FormText formText,
                                    final ControlSegment segment )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( formText );
    Control control = segment.getControl( getResourceTable( formText ) );
    Object[] args = new Object[] {
      WidgetUtil.getId( control )
    };
    writer.call( "createControlSegment", args ); //$NON-NLS-1$
  }

  private void writeHyperlinkSettings( final FormText formText )
    throws IOException
  {
    HyperlinkSettings newValue = formText.getHyperlinkSettings();
    String prop = PROP_HYPERLINK_SETTINGS;
    if( WidgetLCAUtil.hasChanged( formText, prop, newValue ) ) {
      int underlineMode = newValue.getHyperlinkUnderlineMode();
      Color foreground = newValue.getForeground();
      Color activeForeground = newValue.getActiveForeground();
      Object[] args = new Object[] {
        new Integer( underlineMode ),
        colorToHtmlString( foreground ),
        colorToHtmlString( activeForeground )
      };
      JSWriter writer = JSWriter.getWriterFor( formText );
      writer.call( "setHyperlinkSettings", args ); //$NON-NLS-1$
    }
  }

  private void clearContent( final FormText formText ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( formText );
    writer.call( "clearContent", new Object[ 0 ] ); //$NON-NLS-1$
  }

  private void updateHyperlinks( final FormText formText ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( formText );
    writer.call( "updateHyperlinks", new Object[ 0 ] ); //$NON-NLS-1$
  }

  //////////////////
  // Helping methods
  private IFormTextAdapter getAdapter( final FormText formText ) {
    Object adapter = formText.getAdapter( IFormTextAdapter.class );
    IFormTextAdapter formTextAdapter = ( IFormTextAdapter )adapter;
    return formTextAdapter;
  }

  private ITextSegmentAdapter getAdapter( final TextSegment segment ) {
    Object adapter = segment.getAdapter( ITextSegmentAdapter.class );
    ITextSegmentAdapter textSegmentAdapter = ( ITextSegmentAdapter )adapter;
    return textSegmentAdapter;
  }

  private IBulletParagraphAdapter getAdapter( final BulletParagraph bullet ) {
    Object adapter = bullet.getAdapter( IBulletParagraphAdapter.class );
    IBulletParagraphAdapter bulletParagraphAdapter
      = ( IBulletParagraphAdapter )adapter;
    return bulletParagraphAdapter;
  }

  private Image getBulletImage( final FormText formText,
                                final BulletParagraph bullet)
  {
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

  private Rectangle getBulletBounds( final BulletParagraph bullet ) {
    IBulletParagraphAdapter bulletParagraphAdapter = getAdapter( bullet );
    return bulletParagraphAdapter.getBulletBounds();
  }

  private Paragraph[] getParagraphs( final FormText formText ) {
    IFormTextAdapter adapter = getAdapter( formText );
    return adapter.getParagraphs();
  }

  private Hashtable getResourceTable( final FormText formText ) {
    IFormTextAdapter adapter = getAdapter( formText );
    return adapter.getResourceTable();
  }

  private boolean hasLayoutChanged( final FormText formText ) {
    IFormTextAdapter adapter = getAdapter( formText );
    return adapter.hasLayoutChanged();
  }

  private String[] getTextFragments( final TextSegment segment ) {
    ITextSegmentAdapter textSegmentAdapter = getAdapter( segment );
    return textSegmentAdapter.getTextFragments();
  }

  private Rectangle[] getTextFragmentsBounds( final TextSegment segment ) {
    ITextSegmentAdapter textSegmentAdapter = getAdapter( segment );
    return textSegmentAdapter.getTextFragmentsBounds();
  }

  private String getFontId( final TextSegment segment ) {
    ITextSegmentAdapter textSegmentAdapter = getAdapter( segment );
    return textSegmentAdapter.getFontId();
  }

  private String colorToHtmlString( final Color color ) {
    String result = null;
    if( color != null ) {
      int red = color.getRed();
      int green = color.getGreen();
      int blue = color.getBlue();
      StringBuffer sb = new StringBuffer();
      sb.append( "#" ); //$NON-NLS-1$
      sb.append( getHexStr( red ) );
      sb.append( getHexStr( green ) );
      sb.append( getHexStr( blue ) );
      result = sb.toString();
    }
    return result;
  }

  private String getHexStr( final int value ) {
    String hex = Integer.toHexString( value );
    return hex.length() == 1 ? "0" + hex : hex; //$NON-NLS-1$
  }

  private String[] getFontName( final Font font ) {
    String[] result = null;
    if( font != null ) {
      FontData fontData = font.getFontData()[ 0 ];
      String fontName = fontData.getName();
      result = fontName.split( "," ); //$NON-NLS-1$
      for( int i = 0; i < result.length; i++ ) {
        result[ i ] = result[ i ].trim();
        Matcher matcher = FONT_NAME_FILTER_PATTERN.matcher( result[ i ] );
        result[ i ] = matcher.replaceAll( "" ); //$NON-NLS-1$
      }
    }
    return result;
  }

  private Integer getFontSize( final Font font ) {
    Integer result = null;
    if( font != null ) {
      FontData fontData = font.getFontData()[ 0 ];
      result = new Integer( fontData.getHeight() );
    }
    return result;
  }

  private Boolean getFontStyle( final Font font, final int style ) {
    Boolean result = null;
    if( font != null ) {
      FontData fontData = font.getFontData()[ 0 ];
      result = Boolean.valueOf( ( fontData.getStyle() & style ) != 0 );
    }
    return result;
  }
}
