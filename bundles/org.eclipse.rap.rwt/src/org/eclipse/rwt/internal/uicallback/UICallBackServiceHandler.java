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
package org.eclipse.rwt.internal.uicallback;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.rwt.internal.lifecycle.JavaScriptResponseWriter;
import org.eclipse.rwt.internal.lifecycle.LifeCycleUtil;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.service.IServiceHandler;
import org.eclipse.rwt.service.ISessionStore;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import org.eclipse.swt.widgets.Display;


public class UICallBackServiceHandler implements IServiceHandler {

  // keep in sync with function enableUICallBack() in Request.js
  public final static String HANDLER_ID = UICallBackServiceHandler.class.getName();

  private static final String JS_SEND_UI_REQUEST
    = "org.eclipse.swt.Request.getInstance()._sendImmediate( true );";

  private static final String ATTR_NEEDS_UICALLBACK
    = UICallBackServiceHandler.class.getName() + ".needsUICallback";

  public void service() throws IOException {
    HttpServletResponse response = ContextProvider.getResponse();
    ISessionStore sessionStore = ContextProvider.getSession();
    boolean isActiveRequest = UICallBackManager.getInstance().processCallBackRequest( response );
    if( sessionStore.isBound() && isActiveRequest ) {
      JavaScriptResponseWriter writer = new JavaScriptResponseWriter( response );
      writeUICallBackActivation( LifeCycleUtil.getSessionDisplay(), writer );
      writeUiRequestNeeded( writer );
    }
  }

  public static void writeUICallBackActivation( Display display, JavaScriptResponseWriter writer ) {
    if( display != null && !display.isDisposed() ) {
      boolean actual = UICallBackManager.getInstance().needsActivation();
      IDisplayAdapter adapter = ( IDisplayAdapter )display.getAdapter( IDisplayAdapter.class );
      ISessionStore sessionStore = adapter.getSessionStore();
      Boolean preserved = ( Boolean )sessionStore.getAttribute( ATTR_NEEDS_UICALLBACK );
      if( preserved == null ) {
        preserved = Boolean.FALSE;
      }
      if( preserved.booleanValue() != actual ) {
        writer.write(   "org.eclipse.swt.Request.getInstance().setUiCallBackActive( "
                      + Boolean.toString( actual )
                      + " );" );
        sessionStore.setAttribute( ATTR_NEEDS_UICALLBACK, Boolean.valueOf( actual ) );
      }
    }
  }

  static void writeUiRequestNeeded( JavaScriptResponseWriter writer ) {
    if( UICallBackManager.getInstance().hasRunnables() ) {
      writer.write( JS_SEND_UI_REQUEST );
    }
  }
}
