/*******************************************************************************
 * Copyright (c) 2002, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.service;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rwt.internal.lifecycle.HtmlResponseWriter;


/**
 * <p>The <code>ServiceStateInfo</code> keeps state information needed by the
 * service handlers for proper execution.</p>
 */
public final class ServiceStateInfo implements IServiceStateInfo {

  private HtmlResponseWriter responseWriter;
  private final Map attributes = new HashMap();
  
  /** <p>Sets the given <code>responseWriter</code> for the current request.
   * </p> */
  public void setResponseWriter( final HtmlResponseWriter responseWriter ) {
    this.responseWriter = responseWriter;
  }

  /** <p>Returns the currently set responseWriter.</p> */
  public HtmlResponseWriter getResponseWriter() {
    return responseWriter;
  }
  
  public Object getAttribute( final String key ) {
    return attributes.get( key );
  }

  public void setAttribute( final String key, final Object value ) {
    attributes.put( key, value );
  }
}