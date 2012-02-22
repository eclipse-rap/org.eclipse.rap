/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH.
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.rap.ui.branding.IExitConfirmation;
import org.eclipse.rap.ui.internal.servlet.EntryPointParameters;
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.branding.Header;
import org.osgi.framework.Bundle;


public final class Branding extends AbstractBranding {

  private static final String[] EMPTY_STRINGS = new String[ 0 ];
  private static final Header[] EMPTY_HEADERS = new Header[ 0 ];

  private final String contributor;
  private String servletName;
  private String defaultEntryPointId;
  private List<String> entryPointIds;
  private String title;
  private String favIcon;
  private List<Header> headers;
  private String body;
  private IExitConfirmation exitConfirmation;
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
      entryPointIds = new ArrayList<String>();
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

  public void addHeader( final String tagName, final Map<String, String> attributes ) {
    if( headers == null ) {
      headers = new ArrayList<Header>();
    }
    Header header = new Header( tagName, attributes );
    headers.add( header );
  }

  public void setExitConfirmation( final IExitConfirmation exitConfirmation ) {
    this.exitConfirmation = exitConfirmation;
  }

  public void setThemeId( final String themeId ) {
    this.themeId = themeId;
  }

  void setId( final String brandingId ) {
    this.brandingId = brandingId;
  }

  ///////////////////////////
  // AbstractBranding implementation

  @Override
  public String getServletName() {
    return servletName;
  }

  @Override
  public String getDefaultEntryPoint() {
    return EntryPointParameters.getById( defaultEntryPointId );
  }

  @Override
  public String[] getEntryPoints() {
    String[] result;
    if( entryPointIds == null ) {
      result = EMPTY_STRINGS;
    } else {
      result = new String[ entryPointIds.size() ];
      for( int i = 0; i < result.length; i++ ) {
        String entryPointId = entryPointIds.get( i );
        result[ i ] = EntryPointParameters.getById( entryPointId );
      }
    }
    return result;
  }

  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public String getFavIcon() {
    return favIcon;
  }

  @Override
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

  @Override
  public String getBody() {
    return body;
  }

  @Override
  public boolean showExitConfirmation() {
    boolean result = false;
    if( exitConfirmation != null ) {
      result = exitConfirmation.showExitConfirmation();
    }
    return result;
  }

  @Override
  public String getExitConfirmationText() {
    String result = null;
    if( exitConfirmation != null ) {
      result  = exitConfirmation.getExitConfirmationText();
    }
    return result;
  }

  @Override
  public String getThemeId() {
    return themeId;
  }

  @Override
  public String getId() {
    return brandingId;
  }

  @Override
  public void registerResources() throws IOException {
    if( favIcon != null && !"".equals( favIcon ) ) {
      Bundle bundle = Platform.getBundle( contributor );
      Path file = new Path( favIcon );
      InputStream stream = FileLocator.openStream( bundle, file, false );
      if( stream != null ) {
        try {
          RWT.getResourceManager().register( favIcon, stream );
        } finally {
          stream.close();
        }
      }
    }
  }
}
