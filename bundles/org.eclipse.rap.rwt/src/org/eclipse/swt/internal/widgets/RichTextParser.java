/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets;

import java.io.StringReader;

import javax.xml.parsers.*;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;


public class RichTextParser {
  
  public static boolean isRichText( String text ) {
    return text.startsWith( "<html>" );
  }
  
  private final IRichTextParserCallback callback;
  private final SAXParser saxParser;
  
  public RichTextParser( IRichTextParserCallback callback ) {
    this.callback = callback;
    this.saxParser = createSaxParser();
  }

  private SAXParser createSaxParser() {
    SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
    try {
      return saxParserFactory.newSAXParser();
    } catch( Exception e ) {
      throw new RichTextParserException( "Failed to create SAX parser.", e );
    } 
  }
  
  public void parse( String richText ) {
    InputSource inputSource = new InputSource( new StringReader( richText ) );
    try {
      saxParser.parse( inputSource, new RichTextHandler( callback ) );
    } catch( RuntimeException re ) {
      throw re;
    } catch( Exception e ) {
      throw new RichTextParserException( "Failed to parse rich text.", e );
    }
  }

  private static class RichTextHandler extends DefaultHandler {
    private static final String ELEM_HTML = "html";
    private static final String ELEM_LINE_BREAK = "br";
    private static final String ELEM_FONT = "font";
    private static final String ATTR_FONT_NAME = "name";
    private static final String ATTR_FONT_HEIGHT = "height";
    private static final String ATTR_IMAGE_SRC = "src";
    private static final String ELEM_IMAGE = "img";

    private final IRichTextParserCallback callback;
    
    RichTextHandler( IRichTextParserCallback callback ) {
      this.callback = callback;
    }
    
    public void startElement( String uri, String localName, String qName, Attributes attributes ) {
      if( ELEM_HTML.equals( qName ) ) {
        callback.beginHtml();
      } else if( ELEM_FONT.equals( qName ) ) {
        String fontName = getFontName( attributes );
        int fontHeight = getFontHeight( attributes );
        callback.beginFont( fontName, fontHeight );
      } else if( ELEM_IMAGE.equals( qName ) ) {
        callback.image( getImageSrc( attributes ) );
      } else if ( ELEM_LINE_BREAK.equals( qName ) ) {
      } else {
        throw new RichTextParserException( "Unsupported element: " + qName );
      }
    }
    
    public void endElement( String uri, String localName, String qName ) {
      if( ELEM_HTML.equals( qName ) ) {
        callback.endHtml();
      } else if( ELEM_LINE_BREAK.equals( qName ) ) {
        callback.lineBreak();
      } else if( ELEM_FONT.equals( qName ) ) {
        callback.endFont();
      }
    }
    
    public void characters( char[] ch, int start, int length ) {
      callback.text( new String( ch, start, length ) );
    }
    
    private static String getImageSrc( Attributes attributes ) {
      return attributes.getValue( ATTR_IMAGE_SRC );
    }

    private static String getFontName( Attributes attributes ) {
      return attributes.getValue( ATTR_FONT_NAME );
    }

    private static int getFontHeight( Attributes attributes ) {
      String value = attributes.getValue( ATTR_FONT_HEIGHT );
      try {
        return Integer.parseInt( value );
      } catch( NumberFormatException nfe ) {
        throw new RichTextParserException( "Invalid font height: " + value, nfe );
      }
    }
  }
}
