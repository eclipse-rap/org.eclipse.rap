/*******************************************************************************
 * Copyright (c) 2007, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.uicallback;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.rwt.internal.lifecycle.JavaScriptResponseWriter;
import org.eclipse.rwt.internal.protocol.ProtocolMessageWriter;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.service.IServiceHandler;
import org.eclipse.rwt.service.ISessionStore;


public class UICallBackServiceHandler implements IServiceHandler {

  private final static String UI_CALLBACK_ID = "uicb";
  private final static String PROP_ACTIVE = "active";
  private final static String METHOD_SEND_UI_REQUEST = "sendUIRequest";

  public final static String HANDLER_ID = UICallBackServiceHandler.class.getName();

  private static final String ATTR_NEEDS_UICALLBACK
    = UICallBackServiceHandler.class.getName() + ".needsUICallback";

  public void service() throws IOException {
    HttpServletResponse response = ContextProvider.getResponse();
    ISessionStore sessionStore = ContextProvider.getSession();
    boolean success = UICallBackManager.getInstance().processRequest( response );
    if( success && sessionStore.isBound() ) {
      JavaScriptResponseWriter writer = new JavaScriptResponseWriter( response );
      writeUICallBackDeactivation( writer );
      writeUIRequestNeeded( writer );
      writer.finish();
    }
  }

  public static void writeUICallBackActivation( JavaScriptResponseWriter writer ) {
    boolean actual = UICallBackManager.getInstance().needsActivation();
    boolean preserved = getPreservedUICallBackActivation();
    if( preserved != actual && actual ) {
      writeUICallBackActivation( writer, actual );
      ISessionStore sessionStore = ContextProvider.getSession();
      sessionStore.setAttribute( ATTR_NEEDS_UICALLBACK, Boolean.valueOf( actual ) );
    }
  }

  public static void writeUICallBackDeactivation( JavaScriptResponseWriter writer ) {
    boolean actual = UICallBackManager.getInstance().needsActivation();
    boolean preserved = getPreservedUICallBackActivation();
    if( preserved != actual && !actual ) {
      writeUICallBackActivation( writer, actual );
      ISessionStore sessionStore = ContextProvider.getSession();
      sessionStore.setAttribute( ATTR_NEEDS_UICALLBACK, Boolean.valueOf( actual ) );
    }
  }

  private static void writeUICallBackActivation( JavaScriptResponseWriter writer, boolean value ) {
    ProtocolMessageWriter protocolWriter = writer.getProtocolWriter();
    protocolWriter.appendSet( UI_CALLBACK_ID, PROP_ACTIVE, value );
  }

  private static boolean getPreservedUICallBackActivation() {
    boolean result = false;
    ISessionStore sessionStore = ContextProvider.getSession();
    Boolean preserved = ( Boolean )sessionStore.getAttribute( ATTR_NEEDS_UICALLBACK );
    if( preserved != null ) {
      result = preserved.booleanValue();
    }
    return result;
  }

  static void writeUIRequestNeeded( JavaScriptResponseWriter writer ) {
    if( UICallBackManager.getInstance().hasRunnables() ) {
      ProtocolMessageWriter protocolWriter = writer.getProtocolWriter();
      protocolWriter.appendCall( UI_CALLBACK_ID, METHOD_SEND_UI_REQUEST, null );
    }
  }
}
