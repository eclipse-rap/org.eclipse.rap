/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.osgi.internal;

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;


@SuppressWarnings( "deprecation" )
public class HttpSessionWrapper implements HttpSession {
  private final HttpSession session;
  private final ServletContext servletContext;

  public HttpSessionWrapper( HttpSession session, ServletContext servletContext ) {
    this.session = session;
    this.servletContext = servletContext;
  }

  public long getCreationTime() {
    return session.getCreationTime();
  }

  public String getId() {
    return session.getId();
  }

  public long getLastAccessedTime() {
    return session.getLastAccessedTime();
  }

  public ServletContext getServletContext() {
    return servletContext;
  }

  public void setMaxInactiveInterval( int interval ) {
    session.setMaxInactiveInterval( interval );
  }

  public int getMaxInactiveInterval() {
    return session.getMaxInactiveInterval();
  }

  @Deprecated
  public HttpSessionContext getSessionContext() {
    return session.getSessionContext();
  }

  public Object getAttribute( String name ) {
    return session.getAttribute( name );
  }

  @Deprecated
  public Object getValue( String name ) {
    return session.getValue( name );
  }

  public Enumeration getAttributeNames() {
    return session.getAttributeNames();
  }

  @Deprecated
  public String[] getValueNames() {
    return session.getValueNames();
  }

  public void setAttribute( String name, Object value ) {
    session.setAttribute( name, value );
  }

  @Deprecated
  public void putValue( String name, Object value ) {
    session.putValue( name, value );
  }

  public void removeAttribute( String name ) {
    session.removeAttribute( name );
  }

  @Deprecated
  public void removeValue( String name ) {
    session.removeValue( name );
  }

  public void invalidate() {
    session.invalidate();
  }

  public boolean isNew() {
    return session.isNew();
  }
}