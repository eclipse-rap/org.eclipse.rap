/*******************************************************************************
 * Copyright (c) 2009, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rap.rwt.testfixture.internal;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionBindingEvent;
import jakarta.servlet.http.HttpSessionBindingListener;


/**
 * <p>
 * <strong>IMPORTANT:</strong> This class is <em>not</em> part the public RAP
 * API. It may change or disappear without further notice. Use this class at
 * your own risk.
 * </p>
 */
@SuppressWarnings( "deprecation" )
public class TestHttpSession implements HttpSession {

  private final Map<String,Object> attributes;
  private String id;
  private ServletContext servletContext;
  private boolean isInvalidated;
  private boolean newSession;
  private int maxInactiveInterval;

  public TestHttpSession() {
    attributes = new HashMap<String,Object>();
    servletContext = new TestServletContext();
    id = String.valueOf( hashCode() );
  }

  @Override
  public long getCreationTime() {
    return 0;
  }

  public void setId( String id ) {
    this.id = id;
  }

  @Override
  public String getId() {
    if( isInvalidated ) {
      String text = "Unable to obtain session id. Session already invalidated.";
      throw new IllegalStateException( text );
    }
    return id;
  }

  @Override
  public long getLastAccessedTime() {
    return 0;
  }

  @Override
  public ServletContext getServletContext() {
    return servletContext ;
  }

  public void setServletContext( ServletContext servletContext ) {
    this.servletContext = servletContext;
  }

  @Override
  public void setMaxInactiveInterval( int maxInactiveInterval ) {
    this.maxInactiveInterval = maxInactiveInterval;
  }

  @Override
  public int getMaxInactiveInterval() {
    return maxInactiveInterval;
  }

  @Override
  public Object getAttribute( String arg0 ) {
    return attributes.get( arg0 );
  }

  public Object getValue( String arg0 ) {
    return null;
  }

  @Override
  public Enumeration<String> getAttributeNames() {
    final Iterator iterator = attributes.keySet().iterator();
    return new Enumeration<String>() {
      @Override
      public boolean hasMoreElements() {
        return iterator.hasNext();
      }
      @Override
      public String nextElement() {
        return ( String )iterator.next();
      }
    };
  }

  public String[] getValueNames() {
    return null;
  }

  @Override
  public void setAttribute( String arg0, Object arg1 ) {
    if( arg1 instanceof HttpSessionBindingListener ) {
      HttpSessionBindingListener listener = ( HttpSessionBindingListener )arg1;
      listener.valueBound( new HttpSessionBindingEvent( this, arg0, arg1 ) );
    }
    attributes.put( arg0, arg1 );
  }

  public void putValue( String arg0, Object arg1 ) {
  }

  @Override
  public void removeAttribute( String arg0 ) {
    Object removed = attributes.remove( arg0 );
    if( removed instanceof HttpSessionBindingListener ) {
      HttpSessionBindingListener listener = ( HttpSessionBindingListener )removed;
      HttpSessionBindingEvent evt = new HttpSessionBindingEvent( this, arg0, removed );
      listener.valueUnbound( evt );
    }
  }

  public void removeValue( String arg0 ) {
  }

  @Override
  public void invalidate() {
    Object[] keys = attributes.keySet().toArray();
    for( int i = 0; i < keys.length; i++ ) {
      String key = ( String )keys[ i ];
      Object value = attributes.get( key );
      if( value instanceof HttpSessionBindingListener ) {
        HttpSessionBindingListener listener = ( HttpSessionBindingListener )value;
        listener.valueUnbound( new HttpSessionBindingEvent( this, key, value ) );
      }
    }
    attributes.clear();
    isInvalidated = true;
  }

  public boolean isInvalidated() {
    return isInvalidated;
  }

  @Override
  public boolean isNew() {
    return newSession;
  }

  public void setNew( boolean newSession ) {
    this.newSession = newSession;
  }
}