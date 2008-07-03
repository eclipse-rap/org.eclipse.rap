/*******************************************************************************
 * Copyright (c) 2007, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.branding;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.branding.Header;
import org.eclipse.rwt.internal.lifecycle.CommonPatterns;
import org.eclipse.rwt.internal.service.*;


public final class BrandingUtil {

  private static final String ATTR_BRANDING_ID 
    = BrandingUtil.class.getName() + "#brandingId";

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
    if( branding.showExitConfirmation() ) {
      String exitMessage = branding.getExitConfirmationText();
      if( exitMessage != null && exitMessage != "" ) {
        exitMessage = escapeString( exitMessage );
        result = "app.setExitConfirmation( \"" + exitMessage + "\" );";
      }
    }
    return result;
  }

  public static AbstractBranding findBranding() {
    HttpServletRequest request = ContextProvider.getRequest();
    String servletName = BrowserSurvey.getSerlvetName();
    String entryPoint = request.getParameter( RequestParams.STARTUP );
    AbstractBranding branding = BrandingManager.get( servletName, entryPoint );
    RWT.getSessionStore().setAttribute( ATTR_BRANDING_ID, branding.getId() );
    return branding;
  }
  
  /**
   * Return the id of the current branding. This is only available after 
   * {@link #findBranding()} has been called.
   * @return the id of the current branding or <code>null</code>.
   */
  public static String getCurrentBrandingId() {
    return ( String ) RWT.getSessionStore().getAttribute( ATTR_BRANDING_ID );
  }
  
  //////////////////
  // Helping methods
  
  private static void appendFavIconMarkup( final StringBuffer buffer,
                                           final AbstractBranding branding )
  {
    String favIcon = branding.getFavIcon();
    if( favIcon != null && !"".equals( favIcon ) ) {
      String[] names = new String[] { 
        "rel", 
        "type", 
        "href" 
      };
      String[] values = new String[] { 
        "shortcut icon", 
        "image/x-icon", 
        favIcon 
      };
      Header header = new Header( "link", names, values );
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
    String result = CommonPatterns.escapeDoubleQuoted( input );
    return CommonPatterns.replaceNewLines( result );
  }

  private BrandingUtil() {
    // prevent instantiation
  }
}
