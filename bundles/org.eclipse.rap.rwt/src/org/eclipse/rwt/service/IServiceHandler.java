/*******************************************************************************
 * Copyright (c) 2002, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.service;

import java.io.IOException;
import javax.servlet.ServletException;


/**
 * <p>
 * A service handler can be used to process requests that bypass the standard
 * request lifecycle. Clients are free to implement custom service handlers to
 * deliver custom content. Implementing a custom service handler involves three
 * steps:
 * </p>
 * <ul>
 * <li>Implementing the IServiceHandler interface, e.g.
 * <pre>
 * public class MyServiceHandler implements IServiceHandler {
 * 
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
 * StringBuffer url = new StringBuffer();
 * url.append( RWT.getRequest().getContextPath() );
 * url.append( RWT.getRequest().getServletPath() );
 * url.append( &quot;?&quot; );
 * url.append( IServiceHandler.REQUEST_PARAM );
 * url.append( &quot;=myServiceHandler&quot; );
 * String encodedURL = RWT.getResponse().encodeURL( url.toString() );
 * </pre>
 * </li>
 * 
 * @since 1.0
 */
public interface IServiceHandler {
  
  /**
   * <p>The request parameter name to hold the service handlers name as its 
   * value (value is custom_service_handler).</p>
   */
  static final String REQUEST_PARAM = "custom_service_handler";

  /**
   * <p>This method is called by the request lifecycle to allow the service
   * handler to respond to a request.</p>
   * 
   * @throws IOException
   * @throws ServletException
   */
  void service() throws IOException, ServletException; 

}
