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
package org.eclipse.rwt.internal.branding;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.branding.Header;
import org.eclipse.rwt.internal.application.RWTFactory;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.internal.util.URLHelper;
import org.eclipse.rwt.service.IApplicationStore;


public final class BrandingUtil {

  private static final String ATTR_CURRENT_BRANDING_ID
    = BrandingUtil.class.getName() + "#currentBrandingId";
  private static final String ATTR_REG_BRANDINGS
    = BrandingUtil.class.getName() + "#registeredBrandings";
  private static final Object LOCK = new Object();

  public static String headerMarkup( AbstractBranding branding ) {
    StringBuilder buffer = new StringBuilder();
    appendFavIconMarkup( buffer, branding );
    appendHeaderMarkup( buffer, branding );
    return buffer.toString();
  }

  private static void appendFavIconMarkup( StringBuilder buffer, AbstractBranding branding ) {
    String favIcon = branding.getFavIcon();
    if( favIcon != null && !"".equals( favIcon ) ) {
      Header header = createHeaderForFavIcon( favIcon );
      buffer.append( createMarkupForHeaders( header ) );
    }
  }

  private static void appendHeaderMarkup( StringBuilder buffer, AbstractBranding branding ) {
    Header[] headers = branding.getHeaders();
    if( headers != null ) {
      buffer.append( createMarkupForHeaders( headers ) );
    }
  }

  public static Header createHeaderForFavIcon( String favIcon ) {
    String[] names = new String[]{ "rel", "type", "href" };
    String favIconUrl = RWT.getResourceManager().getLocation( favIcon );
    String[] values = new String[]{ "shortcut icon", "image/x-icon", favIconUrl };
    return new Header( "link", names, values );
  }

  public static String createMarkupForHeaders( Header... headers ) {
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

  public static AbstractBranding determineBranding() {
    HttpServletRequest request = ContextProvider.getRequest();
    String servletName = URLHelper.getServletName();
    String entryPoint = request.getParameter( RequestParams.STARTUP );
    AbstractBranding result = RWTFactory.getBrandingManager().find( servletName, entryPoint );
    RWT.getSessionStore().setAttribute( ATTR_CURRENT_BRANDING_ID, result.getId() );
    return result;
  }

  /**
   * Return the id of the current branding. This is only available after
   * {@link #determineBranding()} has been called.
   * @return the id of the current branding or <code>null</code>.
   */
  public static String getCurrentBrandingId() {
    return ( String )RWT.getSessionStore().getAttribute( ATTR_CURRENT_BRANDING_ID );
  }

  public static void registerResources( AbstractBranding branding ) throws IOException {
    if( needsRegistration( branding ) ) {
      branding.registerResources();
    }
  }

  private static boolean needsRegistration( AbstractBranding branding ) {
    boolean result;
    Set<AbstractBranding> registeredBrandings = getRegisteredBrandings();
    synchronized( LOCK ) {
      result = registeredBrandings.add( branding );
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  private static Set<AbstractBranding> getRegisteredBrandings() {
    Set<AbstractBranding> result;
    IApplicationStore store = RWT.getApplicationStore();
    synchronized( LOCK ) {
      result = ( Set<AbstractBranding> )store.getAttribute( ATTR_REG_BRANDINGS );
      if( result == null ) {
        result = new HashSet<AbstractBranding>();
        store.setAttribute( ATTR_REG_BRANDINGS, result );
      }
    }
    return result;
  }

  private BrandingUtil() {
    // prevent instantiation
  }
}
