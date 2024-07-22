/*******************************************************************************
 * Copyright (c) 2011, 2024 Frank Appel and others.
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

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;


@SuppressWarnings( "deprecation" )
public class HttpSessionWrapper implements HttpSession {

  private final HttpSession session;
  private final ServletContext servletContext;

  public HttpSessionWrapper( HttpSession session, ServletContext servletContext ) {
    this.session = session;
    this.servletContext = servletContext;
  }

  @Override
  public long getCreationTime() {
    return session.getCreationTime();
  }

  @Override
  public String getId() {
    return session.getId();
  }

  @Override
  public long getLastAccessedTime() {
    return session.getLastAccessedTime();
  }

  @Override
  public ServletContext getServletContext() {
    return servletContext;
  }

  @Override
  public void setMaxInactiveInterval( int interval ) {
    session.setMaxInactiveInterval( interval );
  }

  @Override
  public int getMaxInactiveInterval() {
    return session.getMaxInactiveInterval();
  }

  @Override
  public Object getAttribute( String name ) {
    return session.getAttribute( name );
  }

  @Override
  public Enumeration<String> getAttributeNames() {
    return session.getAttributeNames();
  }

  @Override
  public void setAttribute( String name, Object value ) {
    session.setAttribute( name, value );
  }

  @Override
  public void removeAttribute( String name ) {
    session.removeAttribute( name );
  }

  @Override
  public void invalidate() {
    session.invalidate();
  }

  @Override
  public boolean isNew() {
    return session.isNew();
  }

}
