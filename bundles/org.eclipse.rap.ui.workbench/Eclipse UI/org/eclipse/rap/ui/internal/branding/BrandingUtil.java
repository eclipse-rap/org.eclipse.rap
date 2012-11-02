/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.ui.internal.branding;

import java.util.Map;

import org.eclipse.rap.rwt.internal.lifecycle.EntryPointUtil;


public final class BrandingUtil {

  public static final String ENTRY_POINT_BRANDING = "branding";

  public static String getCurrentBrandingId() {
    Map<String, String> properties = EntryPointUtil.getCurrentEntryPointProperties();
    return properties.get( BrandingUtil.ENTRY_POINT_BRANDING );
  }

  public static String headerMarkup( AbstractBranding branding ) {
    StringBuilder buffer = new StringBuilder();
    appendHeaderMarkup( buffer, branding );
    return buffer.toString();
  }

  private static void appendHeaderMarkup( StringBuilder buffer, AbstractBranding branding ) {
    Header[] headers = branding.getHeaders();
    if( headers != null ) {
      buffer.append( createMarkupForHeaders( headers ) );
    }
  }

  private static String createMarkupForHeaders( Header... headers ) {
    StringBuilder buffer = new StringBuilder();
    for( Header header : headers ) {
      appendHeaderMarkup( buffer, header );
    }
    return buffer.toString();
  }

  private static String appendHeaderMarkup( StringBuilder buffer, Header header ) {
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
    buffer.append( "/>\n" );
    return buffer.toString();
  }

  private BrandingUtil() {
    // prevent instantiation
  }
}
