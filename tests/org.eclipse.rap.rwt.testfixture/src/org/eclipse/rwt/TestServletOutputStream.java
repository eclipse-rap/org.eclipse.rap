/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;

public class TestServletOutputStream extends ServletOutputStream {

  private ByteArrayOutputStream stream = new ByteArrayOutputStream();
  
  public void write( final int b ) throws IOException {
    stream.write( b );
  }
  
  public ByteArrayOutputStream getContent() {
    return stream;
  }
}