/*******************************************************************************
 * Copyright (c) 2007, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.ui.internal.branding;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.*;
import org.eclipse.rap.ui.branding.IExitConfirmation;
import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.engine.Context;
import org.eclipse.rwt.internal.branding.BrandingManager;
import org.osgi.framework.*;


public final class BrandingExtension {
  private static final String EP_BRANDING = "org.eclipse.rap.ui.branding"; //$NON-NLS-1$
  private static final String ATT_ID = "id"; //$NON-NLS-1$
  private static final String ATT_DEFAULT_ENTRYPOINT_ID = "defaultEntrypointId"; //$NON-NLS-1$
  private static final String ATT_EXIT_CONFIRMATION_CLASS = "exitConfirmationClass"; //$NON-NLS-$
  private static final String ATT_THEME_ID = "themeId"; //$NON-NLS-1$
  private static final String ATT_FAVICON = "favicon"; //$NON-NLS-1$
  private static final String ATT_SERVLET_NAME = "servletName"; //$NON-NLS-1$
  private static final String ATT_TITLE = "title"; //$NON-NLS-1$
  private static final String ATT_BODY = "body"; //$NON-NLS-1$
  private static final String ELEM_ADITIONAL_HEADERS = "additionalHeaders"; //$NON-NLS-1$
  private static final String ELEM_ENTRYPOINTS = "associatedEntrypoints"; //$NON-NLS-1$
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
  
  private final Context context;
  private final ServiceReference httpServiceReference;

  public BrandingExtension( Context context, ServiceReference httpServiceReference ) {
    this.context = context;
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
    registerDefaultServletName();
  }

  //////////////////
  // Helping methods

  private void readBranding( IConfigurationElement element ) throws IOException {
    String contributor = element.getContributor().getName();
    String defEntryPointId = element.getAttribute( ATT_DEFAULT_ENTRYPOINT_ID );
    String id = element.getAttribute( ATT_ID );
    String body = element.getAttribute( ATT_BODY );
    String title = element.getAttribute( ATT_TITLE );
    String servletName = element.getAttribute( ATT_SERVLET_NAME );
    String favIcon = element.getAttribute( ATT_FAVICON );
    String themeId = element.getAttribute( ATT_THEME_ID );
    IExitConfirmation exitConfirmation = findExitConfirmationImpl( element );
    Branding branding = new Branding( contributor );
    branding.setId( id );
    branding.setBody( readBody( contributor, body ) );
    branding.setTitle( title );
    branding.setThemeId( themeId );
    branding.setFavIcon( favIcon );
    branding.setServletName( servletName );
    branding.setExitConfirmation( exitConfirmation );
    branding.setDefaultEntryPointId( defEntryPointId );
    // loop through all additional headers
    IConfigurationElement[] additionalHeaders = element.getChildren( ELEM_ADITIONAL_HEADERS );
    if( additionalHeaders.length > 0 ) {
      IConfigurationElement additionalHeader = additionalHeaders[ 0 ];
      readAdditionalHeader( branding, additionalHeader );
    }
    // loop through all whitelisted entrypoints
    IConfigurationElement[] entryPoints = element.getChildren( ELEM_ENTRYPOINTS );
    if( entryPoints.length > 0 ) {
      entryPoints = entryPoints[ 0 ].getChildren();
      for( int i = 0; i < entryPoints.length; i++ ) {
        String entryPointId = entryPoints[ i ].getAttribute( ATT_ID );
        branding.addEntryPointId( entryPointId );
      }
    }
    Filter serviceFilter = readServiceFilter( element, branding );
    if( ( serviceFilter == null || serviceFilter.match( httpServiceReference ) ) ) {
      context.addBranding( branding );
    }
  }

  // EXPERIMENTAL, see bug 241210
  private Filter readServiceFilter( IConfigurationElement element, Branding branding ) {
    Filter result = null;
    IConfigurationElement[] serviceFilterElements = element.getChildren( ELEM_SERVICE_SELECTOR );
    if( serviceFilterElements.length > 0 ) {
      IConfigurationElement serviceFilterElement = serviceFilterElements[ 0 ];
      String filterClass = serviceFilterElement.getAttribute( ATT_CLASS );
      if( filterClass != null ) {
        try {
          result = ( Filter )serviceFilterElement.createExecutableExtension( ATT_CLASS );
        } catch( CoreException exception ) {
          String text = "Could not instantiate http service filter for branding ''{0}'': ''{1}''";
          Object[] param = new Object[] { branding.getId(), exception.getMessage() };
          String message = MessageFormat.format( text, param );
          throw new IllegalArgumentException( message );
        }
      }
    }
    return result;
  }

  private IExitConfirmation findExitConfirmationImpl( IConfigurationElement element ) {
    IExitConfirmation result = null;
    String className = element.getAttribute( ATT_EXIT_CONFIRMATION_CLASS );
    if( className != null ) {
      try {
        String contributorName = element.getContributor().getName();
        Bundle bundle = Platform.getBundle( contributorName );
        Class clazz = bundle.loadClass( className );
        if( !IExitConfirmation.class.isAssignableFrom( clazz ) ) {
          String text = "The argument ''{0}'' must implement {1}.";
          Object[] args = new Object[] {
            ATT_EXIT_CONFIRMATION_CLASS,
            IExitConfirmation.class.getName()
          };
          String msg = MessageFormat.format( text, args );
          throw new IllegalArgumentException( msg );
        }
        try {
          result = ( IExitConfirmation )clazz.newInstance();
        } catch( Exception e ) {
          String pattern = "Can not instantiate class {0}.";
          Object[] args = new Object[] { clazz.getName() };
          String msg = MessageFormat.format( pattern, args );
          throw new IllegalArgumentException( msg );
        }
      } catch( ClassNotFoundException e ) {
        String pattern = "Class ''{0}'' not found.";
        Object[] args = new Object[] { className };
        String msg = MessageFormat.format( pattern, args );
        throw new IllegalArgumentException( msg );
      }
    }
    return result;
  }

  private void registerDefaultServletName() {
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
      context.addBranding( new AbstractBranding() {
        public String getServletName() {
          return BrandingManager.DEFAULT_SERVLET_NAME;
        }
        public String getTitle() {
          return "RAP Application";
        }
      } );
    }
  }

  private void readAdditionalHeader( Branding branding, IConfigurationElement elem ) {
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
