/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.resources;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.rwt.internal.util.HTTP;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.rwt.resources.IResourceManager.RegisterOptions;


public class JSLibraryConcatenator {
  private final IResourceManager resourceManager;
  private ContentBuffer jsBuffer;
  private String location;

  public JSLibraryConcatenator( IResourceManager resourceManager ) {
    this.resourceManager = resourceManager;
  }

  public void startJSConcatenation() {
    jsBuffer = new ContentBuffer();
  }

  public void appendJSLibrary( byte[] content ) {
    if( jsBuffer != null ) {
      jsBuffer.append( content );
    }
  }

  public String getLocation() {
    return location;
  }

  public void activate() {
    synchronized( JSLibraryConcatenator.class ) {
      if( location == null ) {
        byte[] content = readContent();
        if( content.length > 0 ) {
          location = register( content, "resources.js" );
        }
      }
    }
  }

  public void deactivate() {
    resourceManager.unregister( "resources.js" );
    jsBuffer = null;
    location = null;
  }

  byte[] readContent() {
    byte[] content;
    if( jsBuffer != null ) {
      content = jsBuffer.getContent();
      jsBuffer = null;
    } else {
      content = new byte[ 0 ];
    }
    return content;
  }

  private String register( byte[] content, String name ) {
    InputStream inputStream = new ByteArrayInputStream( content );
    resourceManager.register( name, inputStream, HTTP.CHARSET_UTF_8, RegisterOptions.VERSION );
    return resourceManager.getLocation( name );
  }

}
