/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rwt.internal.util.ParamCheck;
import org.eclipse.rwt.service.ISessionStore;

/** 
 * <p>This encapsulates access to the currently processed request,
 * response and other helpful status information needed by the
 * service handler implementations. Note that after an requests
 * lifecycle has expired the corresponding <code>ServiceContext</code>
 * will be disposed and throws an <code>IllegalStateException</code>
 * if any of its methods will be called.</p>
 */
public final class ServiceContext {
  
  /** 
   * The key which is used to store the {@link org.eclipse.rwt.internal.browser.Browser} instance
   * of the current session. 
   */
  public final static String DETECTED_SESSION_BROWSER 
    = "com_w4t_detected_session_browser";
  
  private HttpServletRequest request;
  private HttpServletResponse response;
  private IServiceStateInfo stateInfo;
  private boolean disposed;
  private ISessionStore sessionStore;
  
  /**
   * creates a new instance of <code>ServiceContext</code>
   * 
   * @param request the instance of the currently processed request. Must not
   *                be null.
   * @param response the corresponding response to the currently processed
   *                 request. Must not be null.
   */
  public ServiceContext( final HttpServletRequest request,
                         final HttpServletResponse response ) 
  {
    ParamCheck.notNull( request, "request" );
    ParamCheck.notNull( response, "response" );
    this.request = request;
    this.response = response;
  }

  /**
   * creates a new instance of <code>ServiceContext</code>
   * 
   * @param request the instance of the currently processed request. Must not
   *                be null.
   * @param response the corresponding response to the currently processed
   *                 request. Must not be null.
   * @param sessionStore the <code>ISessionStore</code> that represents the
   *                     <code>HttpSession<code> instance to which the currently
   *                     processed request belongs to.
   */
  public ServiceContext( final HttpServletRequest request,
                         final HttpServletResponse response,
                         final ISessionStore sessionStore )
  {
    this( request, response );
    this.sessionStore = sessionStore;
  }

  /**
   * Returns the instance of the currently processed request.
   */
  public HttpServletRequest getRequest() {
    checkState();
    return request;
  }
  
  public void setRequest( final HttpServletRequest request ) {
    this.request = request;
  }
  
  /**
   * Returns the corresponding response to the currently processed
   * request
   */
  public HttpServletResponse getResponse() {
    checkState();
    return response;
  }

  /**
   * Returns the corresponding {@link IServiceStateInfo} to the currently 
   * processed request.
   */
  public IServiceStateInfo getStateInfo() {
    checkState();
    return stateInfo;
  }

  /**
   * Sets the corresponding {@link IServiceStateInfo} to the currently 
   * processed request.
   */
  public void setStateInfo( final IServiceStateInfo stateInfo ) {
    checkState();
    ParamCheck.notNull( stateInfo, "stateInfo" );
    if( this.stateInfo != null ) {
      String msg = "StateInfo is already set and must not be replaced.";
      throw new IllegalStateException( msg );
    }
    this.stateInfo = stateInfo;
  }
  
  public ISessionStore getSessionStore() {
    if(    sessionStore != null 
        && !( ( SessionStoreImpl )sessionStore ).isBound() )
    {
      sessionStore = null;
    }
    return sessionStore;
  }
  
  public void setSessionStore( final ISessionStore sessionStore ) {
    this.sessionStore = sessionStore;
  }
  
  public void dispose() {
    checkState();
    request = null;
    response = null;
    stateInfo = null;
    disposed = true;
    sessionStore = null;
  }
  
  
  //////////////////
  // helping methods
  
  private void checkState() {
    if( disposed ) {
      throw new IllegalStateException( "The context has been disposed." );
    }
  }
}