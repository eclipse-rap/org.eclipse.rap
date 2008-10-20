/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.theme.css;

import org.eclipse.rwt.internal.theme.ResourceLoader;
import org.eclipse.rwt.internal.theme.StyleSheetBuilder;
import org.w3c.css.sac.*;


public class DocumentHandlerImpl implements DocumentHandler {

  private String uri;
  private final CssFileReader reader;
  private final ResourceLoader loader;
  private final StyleSheetBuilder styleSheetBuilder;
  private StylePropertyMap currentStyleProperties = null;

  public DocumentHandlerImpl( final CssFileReader reader,
                              final ResourceLoader loader )
  {
    this.reader = reader;
    this.loader = loader;
    styleSheetBuilder = new StyleSheetBuilder();
  }

  public void startDocument( final InputSource source ) throws CSSException {
    uri = source.getURI();
    log( "=== startDocument " + uri + "===" );
  }

  public void endDocument( final InputSource source ) throws CSSException {
    log( "___ endDocument ___" );
  }

  public void startSelector( final SelectorList patterns ) throws CSSException {
    log( "startSelector " + toString( patterns ) );
    currentStyleProperties = new StylePropertyMap( loader );
  }

  public void endSelector( final SelectorList patterns ) throws CSSException {
    log( "endSelector " + toString( patterns ) );
    StyleRule styleRule = new StyleRule( patterns, currentStyleProperties );
    styleSheetBuilder.addStyleRule( styleRule );
    currentStyleProperties = null;
  }

  public void property( final String name,
                        final LexicalUnit value,
                        final boolean important ) throws CSSException
  {
    log( "  property "
         + name
         + " := "
         + toString( value )
         + ( important
                      ? ", important"
                      : "" ) );
    if( currentStyleProperties != null ) {
      currentStyleProperties.setProperty( name, value );
    }
  }

  // -- ignored --
  public void comment( final String text ) throws CSSException {
    log( "    /*" + text + "*/" );
  }

  // -- unsupported --
  public void importStyle( final String uri,
                           final SACMediaList media,
                           final String defaultNamespaceURI )
    throws CSSException
  {
    log( "importStyle " + uri + ", " + media + ", " + defaultNamespaceURI );
    reader.addProblem( new CSSException( "import rules not supported - ignored" ) );
  }

  public void namespaceDeclaration( final String prefix, final String uri )
    throws CSSException
  {
    log( "namespaceDeclaration " + prefix + ", " + uri );
    reader.addProblem( new CSSException( "unsupported namespace declaration '"
                                         + prefix
                                         + ":"
                                         + uri
                                         + "' - ignored" ) );
  }

  public void ignorableAtRule( final String atRule ) throws CSSException {
    log( "ignorableAtRule " + atRule );
    reader.addProblem( new CSSException( "unsupported at rule '"
                                         + atRule
                                         + "' - ignored" ) );
  }

  public void startPage( final String name, final String pseudo_page )
    throws CSSException
  {
    log( "startPage " + name + ", " + pseudo_page );
    reader.addProblem( new CSSException( "page rules not supported - ignored" ) );
  }

  public void endPage( final String name, final String pseudo_page )
    throws CSSException
  {
    log( "endPage " + name + ", " + pseudo_page );
  }

  public void startMedia( final SACMediaList media ) throws CSSException {
    log( "startMedia " + media );
    reader.addProblem( new CSSException( "media rules not supported - ignored" ) );
  }

  public void endMedia( final SACMediaList media ) throws CSSException {
    log( "endMedia " + media );
  }

  public void startFontFace() throws CSSException {
    log( "startFontFace" );
    reader.addProblem( new CSSException( "font face rules not supported - ignored" ) );
  }

  public void endFontFace() throws CSSException {
    log( "end FontFace" );
  }

  public StyleSheet getStyleSheet() {
    return styleSheetBuilder.getStyleSheet();
  }

  private void log( final String message ) {
//    System.out.println( message );
  }

  private static String toString( final SelectorList patterns ) {
    StringBuffer buffer = new StringBuffer();
    buffer.append( "[" );
    int length = patterns.getLength();
    for( int i = 0; i < length; i++ ) {
      buffer.append( " " );
      Selector selector = patterns.item( i );
      buffer.append( selector.toString() );
    }
    buffer.append( " ]" );
    return buffer.toString();
  }

  private static String toString( final LexicalUnit value ) {
    StringBuffer buffer = new StringBuffer();
    short type = value.getLexicalUnitType();
    if( type == LexicalUnit.SAC_ATTR ) {
      buffer.append( "ATTR " + value.getStringValue() );
    } else if( type == LexicalUnit.SAC_CENTIMETER
               || type == LexicalUnit.SAC_DEGREE
               || type == LexicalUnit.SAC_EM
               || type == LexicalUnit.SAC_EX
               || type == LexicalUnit.SAC_GRADIAN
               || type == LexicalUnit.SAC_HERTZ
               || type == LexicalUnit.SAC_INCH
               || type == LexicalUnit.SAC_KILOHERTZ
               || type == LexicalUnit.SAC_MILLIMETER
               || type == LexicalUnit.SAC_MILLISECOND
               || type == LexicalUnit.SAC_PERCENTAGE
               || type == LexicalUnit.SAC_PICA
               || type == LexicalUnit.SAC_POINT
               || type == LexicalUnit.SAC_PIXEL
               || type == LexicalUnit.SAC_RADIAN
               || type == LexicalUnit.SAC_SECOND
               || type == LexicalUnit.SAC_DIMENSION )
    {
      buffer.append( "DIM "
                     + value.getFloatValue()
                     + value.getDimensionUnitText() );
    } else if( type == LexicalUnit.SAC_RGBCOLOR ) {
      LexicalUnit parameters = value.getParameters();
      buffer.append( "RGBCOLOR " + toString( parameters ) );
    } else if( type == LexicalUnit.SAC_STRING_VALUE ) {
      buffer.append( "STRING " + value.getStringValue() );
    } else if( type == LexicalUnit.SAC_IDENT ) {
      buffer.append( "IDENT " + value.getStringValue() );
    } else if( type == LexicalUnit.SAC_PIXEL ) {
      buffer.append( "PIXEL " + value.getFloatValue() );
    } else if( type == LexicalUnit.SAC_INTEGER ) {
      buffer.append( "INT " + value.getIntegerValue() );
    } else if( type == LexicalUnit.SAC_OPERATOR_COMMA ) {
      buffer.append( "COMMA" );
    } else if( type == LexicalUnit.SAC_ATTR ) {
      buffer.append( "ATTR " + value.getStringValue() );
    } else if( type == LexicalUnit.SAC_FUNCTION ) {
      buffer.append( "UNKNOWN FUNCTION " + value.getFunctionName() );
    } else if( type == LexicalUnit.SAC_DIMENSION ) {
      buffer.append( "UNKNOWN DIMENSION " + value );
    } else {
      buffer.append( "unsupported unit " + value.getLexicalUnitType() );
    }
    LexicalUnit next = value.getNextLexicalUnit();
    if( next != null ) {
      buffer.append( ", " );
      buffer.append( toString( next ) );
    }
    return buffer.toString();
  }
}
