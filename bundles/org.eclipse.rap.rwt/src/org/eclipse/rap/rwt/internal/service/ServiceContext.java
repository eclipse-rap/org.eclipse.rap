/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.service;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.application.ApplicationContextUtil;
import org.eclipse.rap.rwt.internal.protocol.ProtocolMessageWriter;
import org.eclipse.rap.rwt.internal.util.ParamCheck;
import org.eclipse.rap.rwt.service.IServiceStore;
import org.eclipse.rap.rwt.service.UISession;


/**
 * This encapsulates access to the currently processed request, response and
 * other helpful status information needed by the service handler
 * implementations. Note that after an requests lifecycle has expired the
 * corresponding <code>ServiceContext</code> will be disposed and throws an
 * <code>IllegalStateException</code> if any of its methods will be called.
 */
public final class ServiceContext {

  private HttpServletRequest request;
  private HttpServletResponse response;
  private IServiceStore serviceStore;
  private boolean disposed;
  private UISession uiSession;
  private ApplicationContextImpl applicationContext;
  private ProtocolMessageWriter protocolWriter;

  /**
   * creates a new instance of <code>ServiceContext</code>
   *
   * @param request the instance of the currently processed request. Must not
   *                be null.
   * @param response the corresponding response to the currently processed
   *                 request. Must not be null.
   */
  public ServiceContext( HttpServletRequest request, HttpServletResponse response ) {
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
   * @param uiSession the <code>UISession</code> that represents the
   *                     <code>HttpSession<code> instance to which the currently
   *                     processed request belongs to.
   */
  public ServiceContext( HttpServletRequest request,
                         HttpServletResponse response,
                         UISession uiSession )
  {
    this( request, response );
    this.uiSession = uiSession;
  }

  /**
   * Returns the instance of the currently processed request.
   */
  public HttpServletRequest getRequest() {
    checkState();
    return request;
  }

  public void setRequest( HttpServletRequest request ) {
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
   * Returns the corresponding {@link IServiceStore} to the currently processed request.
   */
  public IServiceStore getServiceStore() {
    checkState();
    return serviceStore;
  }

  public ProtocolMessageWriter getProtocolWriter() {
    if( protocolWriter == null ) {
      protocolWriter = new ProtocolMessageWriter();
    }
    return protocolWriter;
  }

  public void resetProtocolWriter() {
    protocolWriter = new ProtocolMessageWriter();
  }

  /**
   * Sets the corresponding {@link IServiceStore} to the currently processed request.
   */
  public void setServiceStore( IServiceStore serviceStore ) {
    checkState();
    ParamCheck.notNull( serviceStore, "serviceStore" );
    if( this.serviceStore != null ) {
      String msg = "ServiceStore is already set and must not be replaced.";
      throw new IllegalStateException( msg );
    }
    this.serviceStore = serviceStore;
  }

  public boolean isDisposed() {
    return disposed;
  }

  public UISession getUISession() {
    if( uiSession != null && !uiSession.isBound() ) {
      uiSession = null;
    }
    return uiSession;
  }

  public void setUISession( UISession uiSession ) {
    this.uiSession = uiSession;
  }

  public void dispose() {
    checkState();
    request = null;
    response = null;
    serviceStore = null;
    uiSession = null;
    applicationContext = null;
    disposed = true;
  }

  public ApplicationContextImpl getApplicationContext() {
    checkState();
    // TODO [ApplicationContextImpl]: Revise performance improvement with buffering mechanism in place.
    if( !isApplicationContextBuffered() ) {
      getApplicationContextFromSession();
      if( !isApplicationContextBuffered() ) {
        getApplicationContextFromServletContext();
        bufferApplicationContextInSession();
      }
    }
    return applicationContext;
  }


  //////////////////
  // helping methods

  private boolean isApplicationContextBuffered() {
    return applicationContext != null;
  }

  private void bufferApplicationContextInSession() {
    if( uiSession != null ) {
      ApplicationContextUtil.set( uiSession, applicationContext );
    }
  }

  private void getApplicationContextFromServletContext() {
    // Note [fappel]: Yourkit analysis showed that the following line is
    //                expensive. Because of this the ApplicationContextImpl is
    //                buffered in a field.
    ServletContext servletContext = request.getSession().getServletContext();
    applicationContext = ApplicationContextUtil.get( servletContext );
  }

  private void getApplicationContextFromSession() {
    if( uiSession != null ) {
      ApplicationContextImpl fromSession = ApplicationContextUtil.get( uiSession );
      if( fromSession != null && fromSession.isActive() ) {
        applicationContext = fromSession;
      }
    }
  }

  private void checkState() {
    if( disposed ) {
      throw new IllegalStateException( "The context has been disposed." );
    }
  }
}