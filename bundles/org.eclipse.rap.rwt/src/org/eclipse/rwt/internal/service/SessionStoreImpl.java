/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
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

import java.text.MessageFormat;
import java.util.*;
import java.util.Map.Entry;

import javax.servlet.http.*;

import org.eclipse.rwt.internal.lifecycle.FakeContextUtil;
import org.eclipse.rwt.internal.lifecycle.ISessionShutdownAdapter;
import org.eclipse.rwt.internal.util.ParamCheck;
import org.eclipse.rwt.service.*;

public final class SessionStoreImpl implements ISessionStore, HttpSessionBindingListener {
  static final String ATTR_SESSION_STORE = SessionStoreImpl.class.getName();
  
  private final Object lock;
  private final Map attributes;
  private final Set sessionStoreListeners;
  private final HttpSession httpSession;
  private final String id;
  private boolean bound;
  private boolean aboutUnbound;
  private ISessionShutdownAdapter shutdownAdapter;

  
  public SessionStoreImpl( HttpSession httpSession ) {
    ParamCheck.notNull( httpSession, "session" );
    this.lock = new Object();
    this.attributes = new HashMap();
    this.sessionStoreListeners = new HashSet();
    this.id = httpSession.getId();
    this.httpSession = httpSession;
    this.httpSession.setAttribute( ATTR_SESSION_STORE, this );
    this.bound = true;
  }
  
  public void setShutdownAdapter( ISessionShutdownAdapter adapter ) {
    shutdownAdapter = adapter;
    if( shutdownAdapter != null ) {
      shutdownAdapter.setSessionStore( this );
      shutdownAdapter.setShutdownCallback( new Runnable() {
        public void run() {
          doValueUnbound();
        }
      } );
    }
  }
  
  //////////////////////////
  // interface ISessionStore
  
  public Object getAttribute( String name ) {
    ParamCheck.notNull( name, "name" );
    Object result = null;
    synchronized( lock ) {
      result = attributes.get( name );
    }
    return result;
  }

  public boolean setAttribute( String name, Object value ) {
    ParamCheck.notNull( name, "name" );
    boolean result = false;
    synchronized( lock ) {
      if( bound ) {
        result = true;
        removeAttributeInternal( name );
        attributes.put( name, value );
        fireValueBound( name, value );
      }
    }
    return result;
  }
  
  public boolean removeAttribute( String name ) {
    ParamCheck.notNull( name, "name" );
    boolean result = false;
    synchronized( lock ) {
      if( bound ) {
        result = true;
        removeAttributeInternal( name );
      }
    }
    return result;
  }

  public Enumeration getAttributeNames() {
    return createAttributeNameEnumeration();
  }

  public String getId() {
    return id;
  }
  
  public HttpSession getHttpSession() {
    return httpSession;
  }
  
  public boolean isBound() {
    synchronized( lock ) {
      return bound;
    }
  }

  public boolean addSessionStoreListener( SessionStoreListener listener ) {
    ParamCheck.notNull( listener, "listener" );
    boolean result = false;
    synchronized( lock ) {
      if( bound && !aboutUnbound ) {
        result = true;
        sessionStoreListeners.add( listener );
      }
    }
    return result;
  }

  public boolean removeSessionStoreListener( SessionStoreListener listener ) {
    ParamCheck.notNull( listener, "listener" );
    boolean result = false;
    synchronized( lock ) {
      if( bound && !aboutUnbound ) {
        result = true;
        sessionStoreListeners.remove( listener );
      }
    }
    return result;
  }

  
  ///////////////////////////////////////
  // interface HttpSessionBindingListener
  
  public void valueBound( HttpSessionBindingEvent event ) {
    synchronized( lock ) {
      bound = true;
      aboutUnbound = false;
    }
  }
  
  public void valueUnbound( HttpSessionBindingEvent event ) {
    if( shutdownAdapter != null ) {
      shutdownAdapter.interceptShutdown();
    } else {
      boolean fakeContext = false;
      if( !ContextProvider.hasContext() ) {
        fakeContext = true;
        ServiceContext context = FakeContextUtil.createFakeContext( this );
        ContextProvider.setContext( context );
      }
      try {
        doValueUnbound();
      } finally {
        if( fakeContext ) {
          ContextProvider.releaseContextHolder();
        }
      }
    }
  }
  
  //////////////////
  // helping methods
  
  private void removeAttributeInternal( String name ) {
    Object removed = attributes.remove( name );
    fireValueUnbound( name, removed );
  }

  private void doValueUnbound() {
    HashMap attributesCopy;
    synchronized( lock ) {      
      aboutUnbound = true;
      attributesCopy = new HashMap( attributes );
    }
    fireBeforeDestroy();
    // leave all attributes in place while firing valueUnbound events to allow a defined shutdown 
    // of the application
    Iterator iterator = attributesCopy.entrySet().iterator();
    while( iterator.hasNext() ) {
      Entry entry = ( Entry )iterator.next();
      fireValueUnbound( ( String )entry.getKey(), entry.getValue() );
    }
    synchronized( lock ) {
      attributes.clear();
      sessionStoreListeners.clear();
      bound = false;
      aboutUnbound = false;
    }
  }

  private void fireBeforeDestroy() {
    SessionStoreListener[] listeners;
    synchronized( lock ) {
      listeners = new SessionStoreListener[ sessionStoreListeners.size() ];
      sessionStoreListeners.toArray( listeners );
    }
    SessionStoreEvent event = new SessionStoreEvent( this );
    for( int i = 0; i < listeners.length; i++ ) {
      try {
        listeners[ i ].beforeDestroy( event );
      } catch( RuntimeException re ) {
        handleExceptionInDestroy( listeners[ i ], re );
      }
    }
  }

  private void fireValueBound( String name, Object value ) {
    if( value instanceof HttpSessionBindingListener ) {
      HttpSessionBindingListener listener = ( HttpSessionBindingListener )value;
      HttpSessionBindingEvent event = new HttpSessionBindingEvent( httpSession, name, value );
      try {
        listener.valueBound( event );
      } catch( RuntimeException re ) {
        handleExceptionInValueBound( listener, re );
      }
    }
  }

  private void fireValueUnbound( String name, Object value ) {
    if( value instanceof HttpSessionBindingListener ) {
      HttpSessionBindingListener listener = ( HttpSessionBindingListener )value;
      HttpSessionBindingEvent event = new HttpSessionBindingEvent( httpSession, name, value );
      try {
        listener.valueUnbound( event );
      } catch( RuntimeException re ) {
        handleExceptionInValueUnbound( listener, re );
      }
    }
  }

  private void handleExceptionInDestroy( SessionStoreListener listener, RuntimeException exception )
  {
    String txt = "Could not execute {0}.beforeDestroy(SessionStoreEvent).";
    Object[] param = new Object[] { listener.getClass().getName() };
    String msg = MessageFormat.format( txt, param );
    httpSession.getServletContext().log( msg, exception );
  }

  private void handleExceptionInValueUnbound( HttpSessionBindingListener listener, 
                                              RuntimeException exception ) 
  {
    String txt = "Could not execute {0}.valueUnbound(HttpSessionBindingEvent).";
    Object[] param = new Object[] { listener.getClass().getName() };
    String msg = MessageFormat.format( txt, param );
    httpSession.getServletContext().log( msg, exception );
  }

  private void handleExceptionInValueBound( HttpSessionBindingListener listener, 
                                            RuntimeException exception ) 
  {
    String txt = "Could not execute {0}.valueBound(HttpSessionBindingEvent).";
    Object[] param = new Object[] { listener.getClass().getName() };
    String msg = MessageFormat.format( txt, param );
    httpSession.getServletContext().log( msg, exception );
  }
  
  private Enumeration createAttributeNameEnumeration() {
    Set names;
    synchronized( lock ) {
      names = new HashSet( attributes.keySet() );
    }
    final Iterator iterator = names.iterator();
    return new Enumeration() {
      public boolean hasMoreElements() {
        return iterator.hasNext();
      }
      public Object nextElement() {
        return iterator.next();
      }
    };
  }
}
