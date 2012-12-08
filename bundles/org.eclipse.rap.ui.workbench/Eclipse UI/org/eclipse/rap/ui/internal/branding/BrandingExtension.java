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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.service.ResourceLoader;
import org.osgi.framework.Bundle;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;


public final class BrandingExtension {

  private static final String EP_BRANDING = "org.eclipse.rap.ui.branding"; //$NON-NLS-1$
  private static final String ATT_ID = "id"; //$NON-NLS-1$
  private static final String ATT_THEME_ID = "themeId"; //$NON-NLS-1$
  private static final String ATT_FAVICON = "favicon"; //$NON-NLS-1$
  private static final String ATT_TITLE = "title"; //$NON-NLS-1$
  private static final String ATT_BODY = "body"; //$NON-NLS-1$
  private static final String ELEM_ADITIONAL_HEADERS = "additionalHeaders"; //$NON-NLS-1$
  private static final String TAG_META = "meta"; //$NON-NLS-1$
  private static final String TAG_LINK = "link"; //$NON-NLS-1$
  private static final String ELEM_ATTRIBUTE = "attribute"; //$NON-NLS-1$
  private static final String ATT_NAME = "name"; //$NON-NLS-1$
  private static final String ATT_CONTENT = "content"; //$NON-NLS-1$
  private static final String ATT_REL = "rel"; //$NON-NLS-1$
  private static final String ATT_HREF = "href"; //$NON-NLS-1$
  private static final String ATT_VALUE = "value"; //$NON-NLS-1$
  private static final String ELEM_SERVICE_SELECTOR = "httpServiceFilter"; //$NON-NLS-1$
  private static final String ATT_CLASS = "class"; //$NON-NLS-1$

  private final Application application;
  private final ServiceReference<HttpService> httpServiceReference;

  public BrandingExtension( Application configuration,
                            ServiceReference<HttpService> httpServiceReference )
  {
    this.application = configuration;
    this.httpServiceReference = httpServiceReference;
  }

  public void read() throws IOException {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IExtensionPoint ep = registry.getExtensionPoint( EP_BRANDING );
    IConfigurationElement[] brandings = ep.getConfigurationElements();
    for( int i = 0; i < brandings.length; i++ ) {
      IConfigurationElement configElement = brandings[ i ];
      readBranding( configElement );
    }
  }

  //////////////////
  // Helping methods

  private void readBranding( IConfigurationElement element ) throws IOException {
    String contributor = element.getContributor().getName();
    String id = element.getAttribute( ATT_ID );
    String body = element.getAttribute( ATT_BODY );
    String title = element.getAttribute( ATT_TITLE );
    String favIcon = element.getAttribute( ATT_FAVICON );
    String themeId = element.getAttribute( ATT_THEME_ID );
    Branding branding = new Branding( contributor );
    branding.setId( id );
    branding.setBody( readBody( contributor, body ) );
    branding.setTitle( title );
    branding.setThemeId( themeId );
    branding.setFavIcon( favIcon );
    // loop through all additional headers
    IConfigurationElement[] additionalHeaders = element.getChildren( ELEM_ADITIONAL_HEADERS );
    if( additionalHeaders.length > 0 ) {
      IConfigurationElement additionalHeader = additionalHeaders[ 0 ];
      readAdditionalHeader( branding, additionalHeader );
    }
    if( !isFiltered( element ) ) {
      register( branding );
      registerFavIcon( element, favIcon );
    }
  }

  private boolean isFiltered( IConfigurationElement element ) {
    boolean result = false;
    if( httpServiceReference != null ) {
      Filter serviceFilter = readServiceFilter( element );
      result = serviceFilter != null && !serviceFilter.match( httpServiceReference );
    }
    return result;
  }

  // EXPERIMENTAL, see bug 241210
  private Filter readServiceFilter( IConfigurationElement element ) {
    Filter result = null;
    IConfigurationElement[] serviceFilterElements = element.getChildren( ELEM_SERVICE_SELECTOR );
    if( serviceFilterElements.length > 0 ) {
      IConfigurationElement serviceFilterElement = serviceFilterElements[ 0 ];
      String filterClass = serviceFilterElement.getAttribute( ATT_CLASS );
      if( filterClass != null ) {
        try {
          result = ( Filter )serviceFilterElement.createExecutableExtension( ATT_CLASS );
        } catch( CoreException exception ) {
          String message = "Could not instantiate http service filter for branding: "
                         + filterClass;
          throw new IllegalArgumentException( message, exception );
        }
      }
    }
    return result;
  }

  private void register( AbstractBranding branding ) {
    BrandingManager.getInstance().register( branding );
  }

  private void registerFavIcon( IConfigurationElement element, final String favIcon ) {
    if( favIcon != null ) {
      final Bundle bundle = Platform.getBundle( element.getContributor().getName() );
      application.addResource( favIcon, new ResourceLoader() {
        public InputStream getResourceAsStream( String resourceName ) throws IOException {
          return FileLocator.openStream( bundle, new Path( favIcon ), false );
        }
      } );
    }
  }

  private void readAdditionalHeader( Branding branding, IConfigurationElement elem ) {
    IConfigurationElement[] headers = elem.getChildren();
    for( int i = 0; i < headers.length; i++ ) {
      IConfigurationElement header = headers[ i ];
      Map<String, String> attributes = new HashMap<String, String>();
      // add predefined attributes
      String tagName = header.getName();
      if( TAG_META.equals( tagName ) ) {
        attributes.put( ATT_NAME, header.getAttribute( ATT_NAME ) );
        attributes.put( ATT_CONTENT, header.getAttribute( ATT_CONTENT ) );
      } else if( TAG_LINK.equals( tagName ) ) {
        attributes.put( ATT_REL, header.getAttribute( ATT_REL ) );
        attributes.put( ATT_HREF, header.getAttribute( ATT_HREF ) );
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

  private String readBody( String contributor, String path ) throws IOException {
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
}
