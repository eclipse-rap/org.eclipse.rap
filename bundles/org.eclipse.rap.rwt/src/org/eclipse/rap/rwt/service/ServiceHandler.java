/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.service;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * <p>
 * A service handler can be used to process requests that bypass the standard
 * request lifecycle. Clients are free to implement custom service handlers to
 * deliver custom content. Implementing a custom service handler involves three
 * steps:
 * </p>
 * <ul>
 * <li>Implementing the ServiceHandler interface, e.g.
 * <pre>
 * public class MyServiceHandler implements ServiceHandler {
 *   public void service() throws IOException, ServletException {
 *     HttpServletResponse response = RWT.getResponse();
 *     response.getWriter().write( &quot;Hello World&quot; );
 *   }
 * }
 * </pre>
 * </li>
 * <li>Registering the service handler and associating it with a request
 * parameter value.
 * <pre>
 * RWT.getServiceManager().registerServiceHandler( &quot;myServiceHandler&quot;,
 *                                                 new MyServiceHandler() );
 * </pre>
 * </li>
 * <li>Constructing the URL to invoke the service handler. The URL must contain
 * the agreed parameter value like this:
 * <code>http://localhost:9090/rap?custom_service_handler=myServiceHandler</code>.
 * The following example code snippet achieves this
 * <pre>
 * StringBuilder url = new StringBuilder();
 * url.append( RWT.getRequest().getContextPath() );
 * url.append( RWT.getRequest().getServletPath() );
 * url.append( &quot;?&quot; );
 * url.append( ServiceHandler.REQUEST_PARAM );
 * url.append( &quot;=myServiceHandler&quot; );
 * String encodedURL = RWT.getResponse().encodeURL( url.toString() );
 * </pre>
 * </li>
 *
 * @since 2.0
 */
public interface ServiceHandler {

  /**
   * <p>This method is called by the request lifecycle to allow the service
   * handler to respond to a request.</p>
   *
   * @throws IOException
   * @throws ServletException
   */
  void service( HttpServletRequest request, HttpServletResponse response )
    throws IOException, ServletException;

}
