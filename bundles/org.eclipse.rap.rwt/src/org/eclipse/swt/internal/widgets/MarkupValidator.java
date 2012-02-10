/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.rwt.internal.resources.SystemProps;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;


public class MarkupValidator {

  private static final SAXParser SAX_PARSER = createSAXParser();
  private static final List<String> SUPPORTED_ELEMENTS = createSupportedElementsList();

  private MarkupValidator() {
    // prevent instantiation
  }

  public static void validate( String text ) {
    if( !SystemProps.isMarkupValidationDisabled() ) {
      StringBuilder markup = new StringBuilder();
      markup.append( "<html>" );
      markup.append( text );
      markup.append( "</html>" );
      InputSource inputSource = new InputSource( new StringReader( markup.toString() ) );
      try {
        SAX_PARSER.parse( inputSource, new MarkupHandler() );
      } catch( RuntimeException exception ) {
        throw exception;
      } catch( Exception exception ) {
        throw new IllegalArgumentException( "Failed to parse markup text", exception );
      }
    }
  }

  private static SAXParser createSAXParser() {
    SAXParser result = null;
    SAXParserFactory parserFactory = SAXParserFactory.newInstance();
    try {
      result = parserFactory.newSAXParser();
    } catch( Exception exception ) {
      throw new RuntimeException( "Failed to create SAX parser", exception );
    }
    return result;
  }

  private static List<String> createSupportedElementsList() {
    List<String> result = new ArrayList<String>();
    result.add( "html" );
    result.add( "b" );
    result.add( "i" );
    result.add( "br" );
    return Collections.unmodifiableList( result );
  }

  private static class MarkupHandler extends DefaultHandler {

    @Override
    public void startElement( String uri, String localName, String name, Attributes attributes ) {
      if( !SUPPORTED_ELEMENTS.contains( name ) ) {
        throw new IllegalArgumentException( "Unsupported element in markup text: " + name );
      }
    }

  }

}
