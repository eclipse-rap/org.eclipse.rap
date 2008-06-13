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
package org.eclipse.ui.internal.branding;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

import org.eclipse.core.runtime.*;
import org.eclipse.rwt.internal.branding.BrandingManager;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.servlet.HttpServiceTracker;

public final class BrandingExtension {

  private static final String EP_BRANDING 
    = "org.eclipse.rap.ui.branding"; //$NON-NLS-1$
  private static final String ATT_ID 
    = "id"; //$NON-NLS-1$
  private static final String ATT_DEFAULT_ENTRYPOINT_ID 
    = "defaultEntrypointId"; //$NON-NLS-1$
  private static final String ATT_EXIT_CONFIRMATION 
    = "exitConfirmation"; //$NON-NLS-1$
  private static final String ATT_THEME_ID 
    = "themeId"; //$NON-NLS-1$
  private static final String ATT_FAVICON 
    = "favicon"; //$NON-NLS-1$
  private static final String ATT_SERVLET_NAME 
    = "servletName"; //$NON-NLS-1$
  private static final String ATT_TITLE 
    = "title"; //$NON-NLS-1$
  private static final String ATT_BODY 
    = "body"; //$NON-NLS-1$
  private static final String ELEM_ADITIONAL_HEADERS 
    = "additionalHeaders"; //$NON-NLS-1$
  private static final String ELEM_ENTRYPOINTS 
    = "entrypoints"; //$NON-NLS-1$
  private static final String TAG_META 
    = "meta"; //$NON-NLS-1$
  private static final String TAG_LINK 
    = "link"; //$NON-NLS-1$
  private static final String ELEM_ATTRIBUTE 
    = "attribute"; //$NON-NLS-1$
  private static final String ATT_NAME 
    = "name"; //$NON-NLS-1$
  private static final String ATT_CONTENT 
    = "content"; //$NON-NLS-1$
  private static final String ATT_REL 
    = "rel"; //$NON-NLS-1$
  private static final String ATT_VALUE 
    = "value"; //$NON-NLS-1$
  
  public static void read() throws IOException {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IExtensionPoint ep = registry.getExtensionPoint( EP_BRANDING );
    IConfigurationElement[] brandings = ep.getConfigurationElements();
    for( int i = 0; i < brandings.length; i++ ) {
      IConfigurationElement configElement = brandings[ i ];
      readBranding( configElement );
    }
    registerDefaultServletName();
  }

  //////////////////
  // Helping methods
  
  private static void readBranding( final IConfigurationElement element )
    throws IOException
  {
    String contributor = element.getContributor().getName();
    String defEntryPointId = element.getAttribute( ATT_DEFAULT_ENTRYPOINT_ID );
    String id = element.getAttribute( ATT_ID );
    String body = element.getAttribute( ATT_BODY );
    String title = element.getAttribute( ATT_TITLE );
    String servletName = element.getAttribute( ATT_SERVLET_NAME );
    String favIcon = element.getAttribute( ATT_FAVICON );
    String themeId = element.getAttribute( ATT_THEME_ID );
    String exitMessage = element.getAttribute( ATT_EXIT_CONFIRMATION );
    Branding branding = new Branding( contributor );
    branding.setId( id );
    branding.setBody( readBody( contributor, body ) );
    branding.setTitle( title );
    branding.setThemeId( themeId );
    branding.setFavIcon( favIcon );
    branding.setServletName( servletName );
    branding.setExitMessage( exitMessage );
    branding.setDefaultEntryPointId( defEntryPointId );
    // loop through all additional headers
    IConfigurationElement[] additionalHeaders 
      = element.getChildren( ELEM_ADITIONAL_HEADERS );
    if( additionalHeaders.length > 0 ) {
      IConfigurationElement additionalHeader = additionalHeaders[ 0 ];
      readAdditionalHeader( branding, additionalHeader );
    }
    // loop through all whitelisted entrypoints
    IConfigurationElement[] entryPoints 
      = element.getChildren( ELEM_ENTRYPOINTS );
    if( entryPoints.length > 0 ) {
      entryPoints = entryPoints[ 0 ].getChildren();
      for( int i = 0; i < entryPoints.length; i++ ) {
        String entryPointId = entryPoints[ i ].getAttribute( ATT_ID );
        branding.addEntryPointId( entryPointId );
      }
    }
    registerServletName( servletName );
    BrandingManager.register( branding );
  }

  private static void registerServletName( final String servletName ) {
    WorkbenchPlugin workbench = WorkbenchPlugin.getDefault();
    HttpServiceTracker httpServiceTracker = workbench.getHttpServiceTracker();
    httpServiceTracker.addServletAlias( servletName );
  }
  
  private static void registerDefaultServletName() {
    boolean found = false;
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IExtensionPoint ep = registry.getExtensionPoint( EP_BRANDING );
    IConfigurationElement[] brandings = ep.getConfigurationElements();
    for( int i = 0; !found && i < brandings.length; i++ ) {
      String servletName = brandings[ i ].getAttribute( ATT_SERVLET_NAME );
      if( BrandingManager.DEFAULT_SERVLET_NAME.equals( servletName ) ) {
        found = true;
      }
    }
    if( !found ) {
      registerServletName( BrandingManager.DEFAULT_SERVLET_NAME );
    }
  }

  private static void readAdditionalHeader( final Branding branding,
                                            final IConfigurationElement elem )
  {
    IConfigurationElement[] headers = elem.getChildren();
    for( int i = 0; i < headers.length; i++ ) {
      IConfigurationElement header = headers[ i ];
      Map attributes = new HashMap();
      // add predefined attributes
      String tagName = header.getName();
      if( TAG_META.equals( tagName ) ) {
        attributes.put( ATT_NAME, header.getAttribute( ATT_NAME ) );
        attributes.put( ATT_CONTENT, header.getAttribute( ATT_CONTENT ) );
      } else if( TAG_LINK.equals( tagName ) ) {
        attributes.put( ATT_REL, header.getAttribute( ATT_REL ) );
      }
      // add additional attributes
      IConfigurationElement[] addAttrs = header.getChildren( ELEM_ATTRIBUTE );
      for( int k = 0; k < addAttrs.length; k++ ) {
        String name = addAttrs[ k ].getAttribute( ATT_NAME );
        String value = addAttrs[ k ].getAttribute( ATT_VALUE );
        attributes.put( name, value );
      }
      branding.addHeader( tagName, attributes );
    }
  }

  private static String readBody( final String contributor, final String path ) 
    throws IOException 
  {
    String result = null;
    if( path != null ) {
      URL url = Platform.getBundle( contributor ).getResource( path );
      InputStream inputStream = url.openStream();
      if( inputStream != null ) {
        try {
          StringBuffer buffer = new StringBuffer();
          byte[] bytes = new byte[ 512 ];
          int bytesRead = inputStream.read( bytes );
          while( bytesRead != -1 ) {
            buffer.append( new String( bytes, 0, bytesRead ) );
            bytesRead = inputStream.read( bytes );
          }
          result = buffer.toString();
        } finally {
          inputStream.close();
        }
      }
    }
    return result;
  }

  private BrandingExtension() {
    // prevent instantiation from outside
  }
}
