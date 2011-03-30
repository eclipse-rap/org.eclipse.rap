/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.service;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rwt.internal.lifecycle.JavaScriptResponseWriter;


/**
 * The <code>ServiceStateInfo</code> keeps state information needed by the
 * service handlers for proper execution.
 */
public final class ServiceStateInfo implements IServiceStateInfo {

  private JavaScriptResponseWriter responseWriter;
  private final Map attributes;
  
  public ServiceStateInfo() {
    attributes = new HashMap();
  }
  
  /**
   * Sets the given <code>responseWriter</code> for the current request.
   */
  public void setResponseWriter( JavaScriptResponseWriter responseWriter ) {
    this.responseWriter = responseWriter;
  }

  /**
   * Returns the currently set responseWriter.
   */
  public JavaScriptResponseWriter getResponseWriter() {
    return responseWriter;
  }

  public Object getAttribute( final String key ) {
    return attributes.get( key );
  }

  public void setAttribute( final String key, final Object value ) {
    attributes.put( key, value );
  }
}
