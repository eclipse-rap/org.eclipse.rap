/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.servlet;

import java.io.InputStream;
import java.util.List;

import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.application.ApplicationImpl;
import org.eclipse.rap.rwt.internal.resources.ContentBuffer;
import org.eclipse.rap.rwt.service.ResourceLoader;
import org.eclipse.rap.ui.resources.IResource;


class ResourceRegisterer {

  private final Application application;
  private final ApplicationContextImpl applicationContext;
  private final ContentBuffer concatenatedScript;

  public ResourceRegisterer( Application application ) {
    this.application = application;
    applicationContext = ( ( ApplicationImpl )application ).getApplicationContext();
    concatenatedScript = new ContentBuffer();
  }

  public void registerResources( List<IResource> resources ) {
    for( IResource resource : resources ) {
      if( resource != null ) {
        registerResource( resource );
      }
    }
    if( concatenatedScript.getContent().length > 0 ) {
      registerConcatenatedScript();
    }
  }

  private void registerResource( IResource resource ) {
    if( resource.isExternal() ) {
      applicationContext.getStartupPage().addJsLibrary( resource.getLocation() );
    } else if( resource.isJSLibrary() ) {
      appendToConcatenatedScript( resource );
    } else {
      String location = resource.getLocation();
      application.addResource( location, new WorkbenchResourceLoader( resource ) );
    }
  }

  private void appendToConcatenatedScript( IResource resource ) {
    try {
      concatenatedScript.append( resource.getLoader().getResourceAsStream( resource.getLocation() ) );
    } catch( Exception exception ) {
      String message = "Failed to load resource: " + resource.getLocation();
      throw new IllegalArgumentException( message, exception );
    }
  }

  private void registerConcatenatedScript() {
    application.addResource( "resources.js", new ResourceLoader() {
      public InputStream getResourceAsStream( String resourceName ) {
        return concatenatedScript.getContentAsStream();
      }
    } );
    applicationContext.getStartupPage().addJsLibrary( "rwt-resources/resources.js" );
  }

  private static class WorkbenchResourceLoader implements ResourceLoader {

    private final IResource resource;

    private WorkbenchResourceLoader( IResource resource ) {
      this.resource = resource;
    }

    public InputStream getResourceAsStream( String resourceName ) {
      return resource.getLoader().getResourceAsStream( resource.getLocation() );
    }
  }

}
