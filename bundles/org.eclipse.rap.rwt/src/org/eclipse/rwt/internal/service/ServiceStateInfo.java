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
import org.eclipse.rwt.internal.protocol.ProtocolMessageWriter;
import org.eclipse.rwt.internal.util.ParamCheck;


/**
 * The <code>ServiceStateInfo</code> keeps state information needed by the
 * service handlers for proper execution.
 */
public final class ServiceStateInfo implements IServiceStateInfo {

  private JavaScriptResponseWriter responseWriter;
  private final Map<String,Object> attributes;

  public ServiceStateInfo() {
    attributes = new HashMap<String,Object>();
  }

  public void setResponseWriter( JavaScriptResponseWriter responseWriter ) {
    this.responseWriter = responseWriter;
  }

  public JavaScriptResponseWriter getResponseWriter() {
    return responseWriter;
  }

  public ProtocolMessageWriter getProtocolWriter() {
    return responseWriter.getProtocolWriter();
  }

  public Object getAttribute( String name ) {
    ParamCheck.notNull( name, "name" );
    return attributes.get( name );
  }

  public void setAttribute( String name, Object value ) {
    ParamCheck.notNull( name, "name" );
    attributes.put( name, value );
  }

  public void removeAttribute( String name ) {
    ParamCheck.notNull( name, "name" );
    attributes.remove( name );
  }

}
