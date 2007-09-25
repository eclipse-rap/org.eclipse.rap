/*******************************************************************************
 * Copyright (c) 2002-2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.service;

import java.io.IOException;
import javax.servlet.ServletException;


// TODO: JavaDoc - revise this RAP/W4T
/** 
 * <p>A service handler is responsible for taking a request and sending
 * an appropriate response by bypassing the standard lifecycle. Clients
 * are free to implement custom service handlers. Implementing a custom
 * service handler involves three steps:
 * <ul><li>Implementing the IServiceHandler interface, e.g.
 * <pre>
 * public class MyServiceHandler implements IServiceHandler {
 *   public void service() throws IOException, ServletException {
 *     HttpServletResponse response = ContextProvider.getResponse();
 *     response.getWriter().write( "Hello World" );
 *   }
 * }
 * </pre>
 * </li>
 * <li>Writing an XML file which assigns the service handler to a request 
 * parameter value. In order to be found, the XML file must be located at 
 * the root of the classpath and named <code>servicehandler.xml</code>.
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;
 * &lt;servicehandler&gt;
 *   &lt;handler 
 *     class="org.demo.MyServiceHandler" 
 *     requestparameter="myServiceHandler"/&gt;
 * &lt;/servicehandler&gt;
 * </pre>
 * Each <code>servicehandler.xml</code> may contain any number of handler
 * entries.
 * <br /><br />
 *</li> 
 * <li>Constructing the URL to invoke the service handler. The URL must contain
 * the agreed on parameter value like this: 
 * <code>http://localhost:9090/rap?custom_service_handler=myServiceHandler</code>.
 * The following example code snippet achieves this
 * <pre>
 * StringBuffer url = new StringBuffer();
 * url.append( URLHelper.getURLString( false ) );
 * url.append( "?" );
 * url.append( IServiceHandler.REQUEST_PARAM ); 
 * url.append( "=myServiceHandler" );
 * String encodedURL = ContextProvider.getResponse().encodeURL( url.toString() );
 * </pre>
 * This URL could for example be passed to the HRef property of a 
 * <code>WebAnchor</code> and by this be delivered to the client.
 * </li>
 * </p>
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
   * <p>This method is called by the W4Toolkit lifecycle to allow the service
   * handler to respond to a request.</p>
   * 
   * @throws IOException
   * @throws ServletException
   */
  void service() throws IOException, ServletException; 

}
