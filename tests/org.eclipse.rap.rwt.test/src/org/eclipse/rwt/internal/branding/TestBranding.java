/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.branding;

import java.io.IOException;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.branding.Header;

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

  public String getId() {
    return TestBranding.class.getName();
  }

  public String[] getEntryPoints() {
    return entryPoints;
  }

  public String getServletName() {
    return servletName;
  }

  public String getDefaultEntryPoint() {
    return defaultEntryPoint;
  }

  public String getBody() {
    return null;
  }
  
  public boolean showExitConfirmation() {
    return showExitConf;
  }
  
  public String getExitConfirmationText() {
    return exitConfMessage;
  }
  
  public String getFavIcon() {
    return favIcon;
  }
  
  public Header[] getHeaders() {
    return headers;
  }

  public void registerResources() throws IOException {
    registerResourcesCallCount++;
    if( favIcon != null && !"".equals( favIcon ) ) {
      RWT.getResourceManager().register( favIcon );
    }
 }
}