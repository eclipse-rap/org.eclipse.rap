/*******************************************************************************
 * Copyright (c) 2002-2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.branding;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.rwt.branding.Header;
import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.internal.service.BrowserSurvey;


public final class BrandingUtil {

  private static final Pattern DOUBLE_QUOTE_PATTERN 
    = Pattern.compile( "(\"|\\\\)" );
  private static final Pattern NEWLINE_PATTERN 
    = Pattern.compile( "\\r\\n|\\r|\\n" );
  private static final String NEWLINE_ESCAPE 
    = "\\\\n";

  public static void replacePlaceholder( final StringBuffer buffer, 
                                         final String placeHolder, 
                                         final String replacement ) 
  {
    String safeReplacement = replacement == null ? "" : replacement;
    BrowserSurvey.replacePlaceholder( buffer, placeHolder, safeReplacement );
  }

  public static String headerMarkup( final AbstractBranding branding ) {
    Header[] headers = branding.getHeaders();
    StringBuffer buffer = new StringBuffer();
    appendFavIconMarkup( buffer, branding );
    if( headers != null ) {
      for( int i = 0; i < headers.length; i++ ) {
        Header header = headers[ i ];
        appendHeaderMarkup( buffer, header );
        buffer.append( "\n" );
      }
    }
    return buffer.toString();
  }
  
  public static String exitMessageScript( final AbstractBranding branding ) {
    String result = "";
    String exitMessage = branding.getExitMessage();
    if( exitMessage != null && exitMessage != "" ) {
      exitMessage = escapeString( exitMessage );
      result = "app.setExitConfirmation( \"" + exitMessage + "\" );";
    }
    return result;
  }

  //////////////////
  // Helping methods
  
  private static void appendFavIconMarkup( final StringBuffer buffer,
                                           final AbstractBranding branding )
  {
    String favIcon = branding.getFavIcon();
    if( favIcon != null && !"".equals( favIcon ) ) {
      Map attributes = new HashMap();
      attributes.put( "rel", "shortcut icon" );
      attributes.put( "type", "image/x-icon" );
      attributes.put( "href", favIcon );
      Header header = new Header( "link", attributes );
      appendHeaderMarkup( buffer, header );
      buffer.append( "\n" );
    }
  }

  private static String appendHeaderMarkup( final StringBuffer buffer,
                                            final Header header ) 
  {
    buffer.append( "<" );
    buffer.append( header.getTagName() );
    buffer.append( " " );
    String[] names = header.getNames();
    String[] values = header.getValues();
    for( int i = 0; i < names.length; i++ ) {
      String name = names[ i ];
      String value = values[ i ];
      if( name != null && value != null ) {
        buffer.append( name );
        buffer.append( "=\"" );
        buffer.append( value );
        buffer.append( "\" " );
      }
    }
    buffer.append( "/>" );
    return buffer.toString();
  }

  private static String escapeString( final String input ) {
    Matcher matcher = BrandingUtil.DOUBLE_QUOTE_PATTERN.matcher( input );
    String result = matcher.replaceAll( "\\\\$1" );
    matcher = BrandingUtil.NEWLINE_PATTERN.matcher( result );
    result = matcher.replaceAll( BrandingUtil.NEWLINE_ESCAPE );
    return result;
  }
  
  private BrandingUtil() {
    // prevent instantiation
  }
}
