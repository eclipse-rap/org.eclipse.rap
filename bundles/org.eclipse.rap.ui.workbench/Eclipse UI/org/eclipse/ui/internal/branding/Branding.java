/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
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

import org.eclipse.core.runtime.Platform;
import org.eclipse.rwt.branding.Header;
import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.internal.resources.ResourceManager;
import org.eclipse.ui.internal.servlet.EntryPointExtension;

public final class Branding extends AbstractBranding {

  private static final String[] EMPTY_STRINGS = new String[ 0 ];
  private static final Header[] EMPTY_HEADERS = new Header[ 0 ];
  
  private final String contributor;
  private String servletName;
  private String defaultEntryPointId;
  private List entryPointIds;
  private String title;
  private String favIcon;
  private List headers;
  private String body;
  private String exitMessage;
  private String themeId;
  private String brandingId;
  
  public Branding( final String contributor ) {
    this.contributor = contributor;
  }

  /////////////////
  // Setter methods
  
  public void setServletName( final String servletName ) {
    this.servletName = servletName;
  }

  public void addEntryPointId( final String entryPointId ) {
    if( entryPointIds == null ) {
      entryPointIds = new ArrayList();
    }
    entryPointIds.add( entryPointId );
  }

  public void setDefaultEntryPointId( final String defaultEntryPointId ) {
    this.defaultEntryPointId = defaultEntryPointId;
  }
  
  public void setTitle( final String title ) {
    this.title = title;
  }

  public void setFavIcon( final String favIcon ) {
    this.favIcon = favIcon;
  }
  
  public void setBody( final String body ) {
    this.body = body;
  }

  public void addHeader( final String tagName, final Map attributes ) {
    if( headers == null ) {
      headers = new ArrayList();
    }
    Header header = new Header( tagName, attributes );
    headers.add( header );
  }

  public void setExitMessage( final String exitMessage ) {
    this.exitMessage = exitMessage;
  }

  public void setThemeId( final String themeId ) {
    this.themeId = themeId;
  }
  
  void setId( final String brandingId ) {
    this.brandingId = brandingId;
  }
  
  ///////////////////////////
  // AbstractBranding implementation
  
  public String getServletName() {
    return servletName;
  }

  public String getDefaultEntryPoint() {
    return EntryPointExtension.getById( defaultEntryPointId );
  }
  
  public String[] getEntryPoints() {
    String[] result;
    if( entryPointIds == null ) {
      result = EMPTY_STRINGS;
    } else {
      result = new String[ entryPointIds.size() ];
      for( int i = 0; i < result.length; i++ ) {
        String entryPointId = ( String )entryPointIds.get( i );
        result[ i ] = EntryPointExtension.getById( entryPointId );
      }
    }
    return result;
  }
  
  public String getTitle() {
    return title;
  }

  public String getFavIcon() {
    return favIcon;
  }
  
  public Header[] getHeaders() {
    Header[] result;
    if( headers == null ) {
      result = EMPTY_HEADERS;
    } else {
      result = new Header[ headers.size() ];
      headers.toArray( result );
    }
    return result;
  }
  
  public String getBody() {
    return body;
  }

  public String getExitMessage() {
    return exitMessage;
  }
  
  public String getThemeId() {
    return themeId;
  }
  
  public String getId() {
    return brandingId;
  }

  public void registerResources() throws IOException {
    if( favIcon != null && !"".equals( favIcon ) ) { //$NON-NLS-1$
      URL url = Platform.getBundle( contributor ).getEntry( favIcon );
      InputStream stream = url.openStream();
      if( stream != null ) {
        try {
          ResourceManager.getInstance().register( favIcon, stream );
        } finally {
          stream.close();
        }
      }
    }
  }
}
