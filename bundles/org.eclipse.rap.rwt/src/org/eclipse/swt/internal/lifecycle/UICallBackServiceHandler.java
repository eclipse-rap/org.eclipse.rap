/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.lifecycle;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.eclipse.swt.SWT;
import com.w4t.HtmlResponseWriter;
import com.w4t.W4TContext;
import com.w4t.engine.lifecycle.*;
import com.w4t.engine.service.*;


public class UICallBackServiceHandler implements IServiceHandler {
  
  private static final String ACTIVATION_IDS
    = UICallBackServiceHandler.class.getName() + "ActivationIds";

  public final static String HANDLER_ID
    = UICallBackServiceHandler.class.getName();
  
  private static String jsUICallBack;

  public void service() throws IOException, ServletException {
    UICallBackManager.getInstance().blockCallBackRequest();
    if( ContextProvider.hasContext() ) {
      HttpServletResponse response = ContextProvider.getResponse();
      PrintWriter writer = response.getWriter();
      writer.print( jsUICallBack() );
      writer.flush();
    }
  }

  private static String jsUICallBack() {
    if( jsUICallBack == null ) {
      StringBuffer code = new StringBuffer();
      code.append( "org.eclipse.swt.Request.getInstance().send();" );
      code.append( jsEnableUICallBack() );
      jsUICallBack = code.toString();      
    }
    return jsUICallBack;
  }

  public static String jsEnableUICallBack() {
    String result = "";
    if( isUICallBackActive() ) {
      Object[] param = new Object[] { 
        ContextProvider.getRequest().getServletPath().substring( 1 ),
        IServiceHandler.REQUEST_PARAM,
        HANDLER_ID
      };
      String callBackPattern =   "org.eclipse.swt.Request.getInstance()." 
                               + "enableUICallBack( \"{0}\",\"{1}\",\"{2}\" );";
      result = MessageFormat.format( callBackPattern, param );
    }
    return result;
  }

  private static boolean isUICallBackActive() {
    return !getActivationIds().isEmpty();
  }

  public static void activateUICallBacksFor( final String id ) {
    if( id == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    getActivationIds().add( id );
    if( getActivationIds().size() == 1 ) {
      registerUICallBackActivator();
    }
  }

  private static void registerUICallBackActivator() {
    final String id = ContextProvider.getSession().getId();
    W4TContext.getLifeCycle().addPhaseListener( new PhaseListener() {
      private static final long serialVersionUID = 1L;

      public void beforePhase( final PhaseEvent event ) {
      }

      public void afterPhase( final PhaseEvent event ) {
        if( id == ContextProvider.getSession().getId() ) {
          IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
          HtmlResponseWriter writer = stateInfo.getResponseWriter();
          try {
            writer.write( jsEnableUICallBack() );
          } catch( IOException e ) {
            // [rh] exception handling
            e.printStackTrace();
          } finally {
            W4TContext.getLifeCycle().removePhaseListener( this );
          }
        }
      }
      
      public PhaseId getPhaseId() {
        return PhaseId.RENDER;
      }
    } );
  }

  public static void deactivateUICallBacksFor( final String id ) {
    if( id == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    getActivationIds().remove( id );
    // release blocked callback handler request
    if( getActivationIds().isEmpty() ) {
      UICallBackManager.getInstance().sendUICallBack();
    }
  }
  
  private static Set getActivationIds() {
    HttpSession session = ContextProvider.getSession();
    Set result = ( Set )session.getAttribute( ACTIVATION_IDS );
    if( result == null ) {
      result = new HashSet();
      session.setAttribute( ACTIVATION_IDS, result );
    }
    return result;
  }
}
