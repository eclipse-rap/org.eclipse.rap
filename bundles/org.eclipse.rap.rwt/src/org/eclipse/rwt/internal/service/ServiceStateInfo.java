/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.service;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.internal.browser.Browser;
import org.eclipse.rwt.internal.lifecycle.HtmlResponseWriter;


/**
 * <p>The <code>ServiceStateInfo</code> keeps state information needed by the
 * service handlers for proper execution.</p>
 */
public final class ServiceStateInfo implements IServiceStateInfo {

  /** the WebForm posted in the request has expired */
  private boolean expired = false;
  /** the WebForm processed in the model caused an exception */
  private boolean exceptionOccured = false;
  /** whether the startup request parameter should be ignored
    * during the render phase of the requests lifecycle. */
  private boolean ignoreStartup = false;
  /** <p>The browser as it was detected at session startup.</p> */
  private Browser detectedBrowser;
  /** <p>contains the rendered page for the request for which this 
    * ServiceStateInfo collects data.</p> */
  private HtmlResponseWriter responseWriter;
  /** <p>The event queue for a request contains all WebDataEvents, i.e. 
    * events which are fired from a component when its value changes.</p> */
  private Object eventQueue;
  
  private boolean invalidated;
  private final Map attributes = new HashMap();
  
  public void setExpired( final boolean expired ) {
    this.expired = expired;
  }

  public boolean isExpired() {
    return expired;
  }

  public void setExceptionOccured( final boolean exceptionOcc ) {
    this.exceptionOccured = exceptionOcc;
  }

  public boolean isExceptionOccured() {
    return exceptionOccured;
  }
  
  /** <p>Marks the current session as invalidated.</p> */
  public void setInvalidated( final boolean invalidated ) {
    this.invalidated = invalidated;
  }

  /** <p>Returns whether the current session is marked as invalidated..</p> */
  public boolean isInvalidated() {
    return this.invalidated;
  }
  
  /** <p>Sets the given <code>responseWriter</code> for the current request.
   * </p> */
  public void setResponseWriter( final HtmlResponseWriter responseWriter ) {
    this.responseWriter = responseWriter;
  }

  /** <p>Returns the currently set responseWriter.</p> */
  public HtmlResponseWriter getResponseWriter() {
    return responseWriter;
  }
  
  public void setDetectedBrowser( final Browser detectedBrowser ) {
    this.detectedBrowser = detectedBrowser;
  }

  public Browser getDetectedBrowser() {
    return detectedBrowser;
  }
  
  
  /** <p>Returns the event queue of this ServiceStateInfo.</p>
    * <p>The event queue for a request contains all WebDataEvents, i.e. 
    * events which are fired from a component when its value changes.</p>
    */
  public Object getEventQueue() {
    return eventQueue;
  }
  
  public void setEventQueue( final Object eventQueue ) {
    this.eventQueue = eventQueue;
  }
  
  public boolean isIgnoreStartup() {
    return ignoreStartup;
  }

  public void setIgnoreStartup( final boolean ignoreStartup ) {
    this.ignoreStartup = ignoreStartup;
  }

  public boolean isFirstAccess() {
    HttpServletRequest request = ContextProvider.getRequest();
    return    request.getSession( true ).isNew()
           || request.getParameter( RequestParams.STARTUP ) != null;
  }

  public Object getAttribute( final String key ) {
    return attributes.get( key );
  }

  public void setAttribute( final String key, final Object value ) {
    attributes.put( key, value );
  }
}