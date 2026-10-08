/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.cluster.testfixture.internal.util;

import java.io.IOException;
import java.net.ServerSocket;


public class SocketUtil {
  
  public static int getFreePort() {
    int result;
    try {
      ServerSocket socket = new ServerSocket( 0 );
      result = socket.getLocalPort();
      socket.close();
    } catch( IOException ioe ) {
      throw new RuntimeException( "Failed to find free port.", ioe );
    } 
    return result;
  }
  
  private SocketUtil() {
    // prevent instantiation
  }
}
