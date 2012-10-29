/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.uicallback;

import java.io.IOException;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rap.rwt.internal.protocol.ProtocolMessageWriter;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.util.HTTP;
import org.eclipse.rap.rwt.service.IServiceHandler;
import org.eclipse.swt.internal.widgets.displaykit.UICallBackRenderer;


public class UICallBackServiceHandler implements IServiceHandler {

  public final static String HANDLER_ID = "org.eclipse.rap.uicallback";

  private final static String METHOD_SEND_UI_REQUEST = "sendUIRequest";

  public void service() throws IOException {
    HttpServletResponse response = ContextProvider.getResponse();
    configureResponseHeaders( response );
    ProtocolMessageWriter writer = new ProtocolMessageWriter();
    UICallBackManager.getInstance().processRequest( response );
    writeUIRequestNeeded( writer );
    String message = writer.createMessage();
    response.getWriter().write( message );
  }

  private static void configureResponseHeaders( ServletResponse response ) {
    response.setContentType( HTTP.CONTENT_TYPE_JSON );
    response.setCharacterEncoding( HTTP.CHARSET_UTF_8 );
  }

  private static void writeUIRequestNeeded( ProtocolMessageWriter writer ) {
    writer.appendCall( UICallBackRenderer.UI_CALLBACK_ID, METHOD_SEND_UI_REQUEST, null );
  }

}
