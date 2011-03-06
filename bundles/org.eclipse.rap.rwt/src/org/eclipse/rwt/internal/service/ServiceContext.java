/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.service;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rwt.internal.engine.RWTContext;
import org.eclipse.rwt.internal.engine.RWTContextUtil;
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
  
  private HttpServletRequest request;
  private HttpServletResponse response;
  private IServiceStateInfo stateInfo;
  private boolean disposed;
  private ISessionStore sessionStore;
  private RWTContext rwtContext;
  
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
  
  public boolean isDisposed() {
    return disposed;
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
    rwtContext = null;
  }
  
  public RWTContext getRWTContext() {
    checkState();
    // TODO [RWTContext]: Revise performance improvement with buffering
    //                    mechanism in place.
    if( !isBuffered() ) {
      getRWTContextFromSession();
      if( !isBuffered() ) {
        getRWTContextFromServletContext();
        bufferRWTContextInSession();
      }
    }
    return rwtContext;
  }

  
  //////////////////
  // helping methods

  private boolean isBuffered() {
    return rwtContext != null;
  }
  
  private void bufferRWTContextInSession() {
    if( sessionStore != null ) {
      RWTContextUtil.registerRWTContext( sessionStore, rwtContext );
    }
  }

  private void getRWTContextFromServletContext() {
    // Note [fappel]: Yourkit analysis showed that the following line is
    //                expensive. Because of this the RWTContext is 
    //                buffered in a field.
    ServletContext servletContext = request.getSession().getServletContext();
    rwtContext = RWTContextUtil.getRWTContext( servletContext );
  }

  private void getRWTContextFromSession() {
    if( sessionStore != null ) {
      rwtContext = RWTContextUtil.getRWTContext( sessionStore );
    }
  }
  
  private void checkState() {
    if( disposed ) {
      throw new IllegalStateException( "The context has been disposed." );
    }
  }
}