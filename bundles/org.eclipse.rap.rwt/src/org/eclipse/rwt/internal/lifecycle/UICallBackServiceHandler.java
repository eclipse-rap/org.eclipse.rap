/*******************************************************************************
 * Copyright (c) 2007, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.SessionSingletonBase;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.IServiceStateInfo;
import org.eclipse.rwt.internal.util.HTML;
import org.eclipse.rwt.service.IServiceHandler;
import org.eclipse.rwt.service.ISessionStore;


public class UICallBackServiceHandler implements IServiceHandler {

  // keep in sync with function enableUICallBack() in Request.js
  public final static String HANDLER_ID
    = UICallBackServiceHandler.class.getName();

  static final String JS_SEND_CALLBACK_REQUEST
    = "org.eclipse.swt.Request.getInstance().enableUICallBack();";
  private static final String JS_SEND_UI_REQUEST
    = "org.eclipse.swt.Request.getInstance().send();";

  private static final String BUFFERED_SEND_CALLBACK_REQUEST
    = UICallBackServiceHandler.class.getName() + "#jsUICallback";

  private static final String NEED_UI_CALLBACK_ACTIVATOR
    = UICallBackServiceHandler.class.getName() + "#needUICallBackActivator";

  ////////////////
  // inner classes

  private static final class IdManager {
    
    static IdManager getInstance() {
      return ( IdManager )SessionSingletonBase.getInstance( IdManager.class );
    }
    
    private final Set ids;
    private final Object lock;
    
    private IdManager() {
      ids = new HashSet();
      lock = new Object();
    }
    
    int add( final String id ) {
      synchronized( lock ) {
        ids.add( id );
        return ids.size();
      }
    }
    
    int remove( final String id ) {
      synchronized( lock ) {
        ids.remove( id );
        return ids.size();
      }
    }
    
    boolean isEmpty() {
      synchronized( lock ) {
        return ids.isEmpty();
      }
    }
  }

  public void service() throws IOException, ServletException {
    ISessionStore sessionStore = RWT.getSessionStore();
    if(    !UICallBackManager.getInstance().blockCallBackRequest()
        && ContextProvider.hasContext() 
        && sessionStore.isBound() )
    {
      writeResponse();
    }
  }

  public static void activateUICallBacksFor( final String id ) {
    int size = IdManager.getInstance().add( id );
    if( size == 1 ) {
      registerUICallBackActivator();
    }
  }

  private static void registerUICallBackActivator() {
    ISessionStore session = ContextProvider.getSession();
    session.setAttribute( NEED_UI_CALLBACK_ACTIVATOR, Boolean.TRUE );
  }

  public static void deactivateUICallBacksFor( final String id ) {
    // release blocked callback handler request
    int size = IdManager.getInstance().remove( id );
    if( size == 0 ) {
      UICallBackManager instance = UICallBackManager.getInstance();
      instance.setActive( false );
      instance.sendUICallBack();
    }
  }

  public static void writeActivation() throws IOException {
    if( needsActivation() ) {
      ISessionStore session = ContextProvider.getSession();
      session.setAttribute( NEED_UI_CALLBACK_ACTIVATOR, Boolean.FALSE );
      UICallBackManager.getInstance().setActive( true );
      IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
      HtmlResponseWriter writer = stateInfo.getResponseWriter();
      writer.write( UICallBackServiceHandler.JS_SEND_CALLBACK_REQUEST );
    }
  }

  private static boolean needsActivation() {
    ISessionStore session = ContextProvider.getSession();
    return    UICallBackServiceHandler.isUICallBackActive()
           && Boolean.TRUE == session.getAttribute( NEED_UI_CALLBACK_ACTIVATOR )
           && !UICallBackManager.getInstance().isCallBackRequestBlocked();
  }

  //////////////////////////
  // Service helping methods

  static void writeResponse() throws IOException {
    HttpServletResponse response = ContextProvider.getResponse();
    response.setHeader( HTML.CONTENT_TYPE, HTML.CONTENT_TEXT_JAVASCRIPT_UTF_8 );
    PrintWriter writer = response.getWriter();
    writer.print( jsUICallBack() );
    writer.flush();
  }

  private static String jsUICallBack() {
    String result;
    if(    isUICallBackActive()
        && !UICallBackManager.getInstance().isCallBackRequestBlocked() )
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

  static boolean isUICallBackActive() {
    boolean result = !IdManager.getInstance().isEmpty();
    if( !result ) {
      result = UICallBackManager.getInstance().hasRunnables();
    }
    return result;
  }
}
