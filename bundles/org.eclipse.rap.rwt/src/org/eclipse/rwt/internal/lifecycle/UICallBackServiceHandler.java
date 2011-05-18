/*******************************************************************************
 * Copyright (c) 2007, 2011 Innoopract Informationssysteme GmbH.
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

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.IServiceStateInfo;
import org.eclipse.rwt.internal.util.HTTP;
import org.eclipse.rwt.service.IServiceHandler;
import org.eclipse.rwt.service.ISessionStore;


public class UICallBackServiceHandler implements IServiceHandler {

  // keep in sync with function enableUICallBack() in Request.js
  public final static String HANDLER_ID = UICallBackServiceHandler.class.getName();

  static final String JS_SEND_CALLBACK_REQUEST
    = "org.eclipse.swt.Request.getInstance().enableUICallBack();";
  private static final String JS_SEND_UI_REQUEST
    = "org.eclipse.swt.Request.getInstance().send();";

  private static final String BUFFERED_SEND_CALLBACK_REQUEST
    = UICallBackServiceHandler.class.getName() + "#jsUICallback";

  private static final String NEED_UI_CALLBACK_ACTIVATOR
    = UICallBackServiceHandler.class.getName() + "#needUICallBackActivator";

  public void service() throws IOException {
    ISessionStore sessionStore = RWT.getSessionStore();
    if(    !UICallBackManager.getInstance().blockCallBackRequest()
        && ContextProvider.hasContext()
        && sessionStore.isBound() )
    {
      writeResponse();
    }
  }

  static void registerUICallBackActivator() {
    ISessionStore session = ContextProvider.getSession();
    session.setAttribute( NEED_UI_CALLBACK_ACTIVATOR, Boolean.TRUE );
  }

  public static void writeActivation() {
    if( needsActivation() ) {
      ISessionStore session = ContextProvider.getSession();
      session.setAttribute( NEED_UI_CALLBACK_ACTIVATOR, Boolean.FALSE );
      IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
      JavaScriptResponseWriter writer = stateInfo.getResponseWriter();
      writer.write( JS_SEND_CALLBACK_REQUEST );
    }
  }

  private static boolean needsActivation() {
    ISessionStore session = ContextProvider.getSession();
    UICallBackManager uiCallbackManager = UICallBackManager.getInstance();
    return    uiCallbackManager.isUICallBackActive()
           && Boolean.TRUE == session.getAttribute( NEED_UI_CALLBACK_ACTIVATOR )
           && !uiCallbackManager.isCallBackRequestBlocked();
  }

  //////////////////////////
  // Service helping methods

  static void writeResponse() throws IOException {
    HttpServletResponse response = ContextProvider.getResponse();
    response.setContentType( HTTP.CONTENT_TEXT_JAVASCRIPT );
    response.setCharacterEncoding( HTTP.CHARSET_UTF_8 );
    PrintWriter writer = response.getWriter();
    writer.print( jsUICallBack() );
    writer.flush();
  }

  private static String jsUICallBack() {
    String result;
    UICallBackManager uiCallbackManager = UICallBackManager.getInstance();
    if(     uiCallbackManager.isUICallBackActive()
        && !uiCallbackManager.isCallBackRequestBlocked() )
    {
      ISessionStore session = ContextProvider.getSession();
      String bufferedCode
        = ( String )session.getAttribute( BUFFERED_SEND_CALLBACK_REQUEST );
      if( bufferedCode == null ) {
        StringBuffer code = new StringBuffer();
        code.append( JS_SEND_UI_REQUEST );
        code.append( JS_SEND_CALLBACK_REQUEST );
        bufferedCode = code.toString();
        session.setAttribute( BUFFERED_SEND_CALLBACK_REQUEST, bufferedCode );
      }
      result = bufferedCode;
    } else {
      result = JS_SEND_UI_REQUEST;
    }
    return result;
  }
}
