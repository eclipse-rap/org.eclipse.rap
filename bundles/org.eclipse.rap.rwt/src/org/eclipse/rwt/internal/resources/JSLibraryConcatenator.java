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

import java.io.ByteArrayOutputStream;


public class JSLibraryConcatenator {
  private ByteArrayOutputStream jsConcatenator;
  private byte[] content;
  private String hashCode;

  public void startJSConcatenation() {
    jsConcatenator = new ByteArrayOutputStream();
  }

  public void appendJSLibrary( byte[] content ) {
    if( jsConcatenator != null && content.length > 0 ) {
      jsConcatenator.write( content, 0, content.length );
      jsConcatenator.write( '\n' );
    }
  }

  public byte[] getContent() {
    return content;
  }

  public String getHashCode() {
    return hashCode;
  }

  public void activate() {
    synchronized( JSLibraryServiceHandler.class ) {
      if( content == null ) {
        content = readContent();
        hashCode = "H" + new String( content ).hashCode();
      }
    }
  }

  public void deactivate() {
    jsConcatenator = null;
    content = null;
    hashCode = null;
  }

  byte[] readContent() {
    byte[] content;
    if( jsConcatenator != null ) {
      content = jsConcatenator.toByteArray();
      jsConcatenator = null;
    } else {
      content = new byte[ 0 ];
    }
    return content;
  }

}
