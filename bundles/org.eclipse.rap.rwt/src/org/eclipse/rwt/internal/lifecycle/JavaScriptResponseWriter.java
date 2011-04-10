/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class JavaScriptResponseWriter {

  private final List elements;

  public JavaScriptResponseWriter() {
    elements = new ArrayList();
  }

  public void write( String content ) {
    elements.add( content );
  }

  public void printContents( PrintWriter writer ) {
    for( int i = 0; i < elements.size(); i++ ) {
      writer.print( elements.get( i ).toString() );
    }
  }
}
