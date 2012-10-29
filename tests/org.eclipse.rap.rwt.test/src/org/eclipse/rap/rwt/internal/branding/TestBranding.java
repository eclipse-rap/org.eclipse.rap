/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.branding;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.branding.AbstractBranding;
import org.eclipse.rap.rwt.branding.Header;

public class TestBranding extends AbstractBranding {

  String servletName;
  String[] entryPoints;
  String defaultEntryPoint;
  int registerResourcesCallCount;
  String favIcon;
  Header[] headers;
  String exitConfMessage;
  boolean showExitConf;

  public TestBranding() {
  }

  public TestBranding( String servletName, String[] entryPoints, String defaultEntryPoint ) {
    this.servletName = servletName;
    this.entryPoints = entryPoints;
    this.defaultEntryPoint = defaultEntryPoint;
  }

  @Override
  public String getId() {
    return TestBranding.class.getName();
  }

  @Override
  public String[] getEntryPoints() {
    return entryPoints;
  }

  @Override
  public String getServletName() {
    return servletName;
  }

  @Override
  public String getDefaultEntryPoint() {
    return defaultEntryPoint;
  }

  @Override
  public String getBody() {
    return null;
  }

  @Override
  public boolean showExitConfirmation() {
    return showExitConf;
  }

  @Override
  public String getExitConfirmationText() {
    return exitConfMessage;
  }

  public void setFavIcon( String favIcon ) {
    this.favIcon = favIcon;
  }

  @Override
  public String getFavIcon() {
    return favIcon;
  }

  @Override
  public Header[] getHeaders() {
    return headers;
  }

  @Override
  public void registerResources() throws IOException {
    registerResourcesCallCount++;
    if( favIcon != null && !"".equals( favIcon ) ) {
      RWT.getResourceManager().register( favIcon, new ByteArrayInputStream( new byte[0 ] ) );
    }
 }
}