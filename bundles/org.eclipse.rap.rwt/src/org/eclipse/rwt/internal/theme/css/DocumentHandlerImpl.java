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

import org.eclipse.rwt.internal.theme.*;
import org.w3c.css.sac.*;


public class DocumentHandlerImpl implements DocumentHandler {

  private String uri;
  private final CssFileReader reader;
  private final ResourceLoader loader;
  private final StyleSheetBuilder styleSheetBuilder;
  private StylePropertyMap currentStyleProperties;

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

  public void startSelector( final SelectorList selectors ) throws CSSException
  {
    log( "startSelector " + toString( selectors ) );
    currentStyleProperties = new StylePropertyMap();
  }

  public void endSelector( final SelectorList selectors ) throws CSSException {
    log( "endSelector " + toString( selectors ) );
    StyleRule styleRule = new StyleRule( selectors, currentStyleProperties );
    styleSheetBuilder.addStyleRule( styleRule );
    currentStyleProperties = null;
  }

  public void property( final String name,
                        final LexicalUnit value,
                        final boolean important )
    throws CSSException
  {
    log( "  property "
         + name
         + " := "
         + PropertyResolver.toString( value )
         + ( important? " !" : "" ) );
    if( important ) {
      reader.addProblem( new CSSException( "Important rules not supported"
                                           + " - ignored" ) );
    }
    if( currentStyleProperties != null ) {
      try {
        QxType resolved = PropertyResolver.resolveProperty( name, value, loader );
        currentStyleProperties.setProperty( name, resolved );
      } catch( IllegalArgumentException e ) {
        reader.addProblem( new CSSException( "Failed to read property "
                                             + name
                                             + ": "
                                             + e.getMessage() ) );
      }
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
    reader.addProblem( new CSSException( "font face rules not supported"
                                         + " - ignored" ) );
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
}
