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
package org.eclipse.rap.rwt.cluster.testfixture.server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public interface IServletEngine {
  void start() throws Exception;
  void stop() throws Exception;
  int getPort();
  
  void addEntryPoint( Class entryPointClass );

  Map getSessions();

  HttpURLConnection createConnection( URL url ) throws IOException;
  
}
