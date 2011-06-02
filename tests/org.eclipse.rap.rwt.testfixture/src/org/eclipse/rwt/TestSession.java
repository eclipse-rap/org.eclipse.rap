/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rwt;

import java.util.*;

import javax.servlet.ServletContext;
import javax.servlet.http.*;


public class TestSession implements HttpSession {
  
  private final Map<String,Object> attributes;
  private String id;
  private ServletContext servletContext;
  private boolean isInvalidated;
  private boolean newSession;
  
  public TestSession() {
    attributes = new HashMap<String,Object>();
    servletContext = new TestServletContext();
    id = String.valueOf( hashCode() );
  }
  
  public long getCreationTime() {
    return 0;
  }
  
  public void setId( String id ) {
    this.id = id;
  }
  
  public String getId() {
    if( isInvalidated ) {
      String text = "Unable to obtain session id. Session already invalidated.";
      throw new IllegalStateException( text );
    }
    return id;
  }
  
  public long getLastAccessedTime() {
    return 0;
  }
  
  public ServletContext getServletContext() {
    return servletContext ;
  }
  
  public void setServletContext( ServletContext servletContext ) {
    this.servletContext = servletContext;
  }
  
  public void setMaxInactiveInterval( final int arg0 ) {
  }
  
  public int getMaxInactiveInterval() {
    return 0;
  }
  
  /**
   * @deprecated
   */
  public HttpSessionContext getSessionContext() {
    return null;
  }
  
  public Object getAttribute( final String arg0 ) {
    return attributes.get( arg0 );
  }
  
  public Object getValue( final String arg0 ) {
    return null;
  }
  
  public Enumeration getAttributeNames() {
    final Iterator iterator = attributes.keySet().iterator();
    return new Enumeration() {
      public boolean hasMoreElements() {
        return iterator.hasNext();
      }
      public Object nextElement() {
        return iterator.next();
      }
    };
  }
  
  public String[] getValueNames() {
    return null;
  }
  
  public void setAttribute( final String arg0, final Object arg1 ) {
    if( arg1 instanceof HttpSessionBindingListener ) {
      HttpSessionBindingListener listener
        = ( HttpSessionBindingListener )arg1;
      listener.valueBound( new HttpSessionBindingEvent( this, arg0, arg1 ) );
    }
    attributes.put( arg0, arg1 );
  }
  
  public void putValue( final String arg0, final Object arg1 ) {
  }
  
  public void removeAttribute( final String arg0 ) {
    Object removed = attributes.remove( arg0 );
    if( removed instanceof HttpSessionBindingListener ) {
      HttpSessionBindingListener listener
        = ( HttpSessionBindingListener )removed;
      HttpSessionBindingEvent evt
        = new HttpSessionBindingEvent( this, arg0, removed );
      listener.valueUnbound( evt );
    }
  }
  
  public void removeValue( final String arg0 ) {
  }
  
  public void invalidate() {
    Object[] keys = attributes.keySet().toArray();
    for( int i = 0; i < keys.length; i++ ) {
      String key = ( String )keys[ i ];
      Object val = attributes.get( key );
      if( val instanceof HttpSessionBindingListener ) {
        HttpSessionBindingListener lsnr = ( HttpSessionBindingListener )val;
        lsnr.valueUnbound( new HttpSessionBindingEvent( this, key, val ) );
      }
    }
    attributes.clear();
    isInvalidated = true;
  }
  
  public boolean isInvalidated() {
    return isInvalidated;
  }
  
  public boolean isNew() {
    return newSession;
  }

  public void setNew( boolean newSession ) {
    this.newSession = newSession;
  }
}