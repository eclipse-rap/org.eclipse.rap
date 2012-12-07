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
import org.eclipse.rap.rwt.RWT;
import org.osgi.framework.Bundle;


public final class Branding extends AbstractBranding {

  private static final Header[] EMPTY_HEADERS = new Header[ 0 ];

  private final String contributor;
  private String title;
  private String favIcon;
  private List<Header> headers;
  private String body;
  private String themeId;
  private String brandingId;

  public Branding( final String contributor ) {
    this.contributor = contributor;
  }

  /////////////////
  // Setter methods

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

  public void setThemeId( final String themeId ) {
    this.themeId = themeId;
  }

  void setId( final String brandingId ) {
    this.brandingId = brandingId;
  }

  ///////////////////////////
  // AbstractBranding implementation

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
