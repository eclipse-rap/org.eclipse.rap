/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.service;

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.eclipse.rap.rwt.internal.lifecycle.ContextUtil;
import org.eclipse.rap.rwt.internal.lifecycle.ISessionShutdownAdapter;
import org.eclipse.rap.rwt.internal.util.ParamCheck;
import org.eclipse.rap.rwt.internal.util.SerializableLock;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.rap.rwt.service.UISessionEvent;
import org.eclipse.rap.rwt.service.UISessionListener;
import org.eclipse.swt.internal.SerializableCompatibility;

public final class UISessionImpl
  implements UISession, HttpSessionBindingListener, SerializableCompatibility
{

  public static final String ATTR_SESSION_STORE = UISessionImpl.class.getName();

  public static UISessionImpl getInstanceFromSession( HttpSession httpSession ) {
    return ( UISessionImpl )httpSession.getAttribute( ATTR_SESSION_STORE );
  }

  public static void attachInstanceToSession( HttpSession httpSession, UISession uiSession ) {
    httpSession.setAttribute( ATTR_SESSION_STORE, uiSession );
  }

  private final SerializableLock requestLock;
  private final SerializableLock lock;
  private final Map<String, Object> attributes;
  private final Set<UISessionListener> listeners;
  private final String id;
  private transient HttpSession httpSession;
  private boolean bound;
  private boolean inDestroy;
  private transient ISessionShutdownAdapter shutdownAdapter;


  public UISessionImpl( HttpSession httpSession ) {
    ParamCheck.notNull( httpSession, "httpSession" );
    requestLock = new SerializableLock();
    lock = new SerializableLock();
    attributes = new HashMap<String, Object>();
    listeners = new HashSet<UISessionListener>();
    id = httpSession.getId();
    bound = true;
    this.httpSession = httpSession;
  }

  public void setShutdownAdapter( ISessionShutdownAdapter adapter ) {
    shutdownAdapter = adapter;
    if( shutdownAdapter != null ) {
      shutdownAdapter.setUISession( this );
      shutdownAdapter.setShutdownCallback( new Runnable() {
        public void run() {
          destroy();
        }
      } );
    }
  }

  public ISessionShutdownAdapter getShutdownAdapter() {
    return shutdownAdapter;
  }

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

  public Enumeration<String> getAttributeNames() {
    return createAttributeNameEnumeration();
  }

  public String getId() {
    return id;
  }

  public HttpSession getHttpSession() {
    synchronized( lock ) {
      return httpSession;
    }
  }

  public void attachHttpSession( HttpSession httpSession ) {
    ParamCheck.notNull( httpSession, "httpSession" );
    synchronized( lock ) {
      this.httpSession = httpSession;
    }
  }

  public boolean isBound() {
    synchronized( lock ) {
      return bound;
    }
  }

  public void exec( Runnable runnable ) {
    ParamCheck.notNull( runnable, "runnable" );
    ContextUtil.runNonUIThreadWithFakeContext( this, runnable );
  }

  public boolean addSessionStoreListener( UISessionListener listener ) {
    return addUISessionListener( listener );
  }

  public boolean addUISessionListener( UISessionListener listener ) {
    ParamCheck.notNull( listener, "listener" );
    boolean result = false;
    synchronized( lock ) {
      if( bound && !inDestroy ) {
        result = true;
        listeners.add( listener );
      }
    }
    return result;
  }

  public boolean removeSessionStoreListener( UISessionListener listener ) {
    return removeUISessionListener( listener );
  }

  public boolean removeUISessionListener( UISessionListener listener ) {
    ParamCheck.notNull( listener, "listener" );
    boolean result = false;
    synchronized( lock ) {
      if( bound && !inDestroy ) {
        result = true;
        listeners.remove( listener );
      }
    }
    return result;
  }

  public void valueBound( HttpSessionBindingEvent event ) {
    synchronized( lock ) {
      bound = true;
      inDestroy = false;
    }
  }

  public void valueUnbound( HttpSessionBindingEvent event ) {
    if( shutdownAdapter != null ) {
      shutdownAdapter.interceptShutdown();
    } else {
      boolean fakeContext = false;
      if( !ContextProvider.hasContext() ) {
        fakeContext = true;
        ServiceContext context = ContextUtil.createFakeContext( this );
        ContextProvider.setContext( context );
      }
      try {
        destroy();
      } finally {
        if( fakeContext ) {
          ContextProvider.releaseContextHolder();
        }
      }
    }
  }

  Object getRequestLock() {
    return requestLock;
  }

  private void removeAttributeInternal( String name ) {
    attributes.remove( name );
  }

  private void destroy() {
    synchronized( lock ) {
      inDestroy = true;
    }
    fireBeforeDestroy();
    synchronized( lock ) {
      attributes.clear();
      listeners.clear();
      bound = false;
      inDestroy = false;
    }
  }

  private void fireBeforeDestroy() {
    UISessionListener[] listenersCopy;
    synchronized( lock ) {
      int size = listeners.size();
      listenersCopy = listeners.toArray( new UISessionListener[ size ] );
    }
    UISessionEvent event = new UISessionEvent( this );
    for( UISessionListener listener : listenersCopy ) {
      try {
        listener.beforeDestroy( event );
      } catch( RuntimeException exception ) {
        handleBeforeDestroyException( listener, exception );
      }
    }
  }

  private void handleBeforeDestroyException( UISessionListener listener,
                                             RuntimeException exception )
  {
    String txt = "Could not execute {0}.beforeDestroy(UISessionEvent).";
    Object[] param = new Object[] { listener.getClass().getName() };
    String msg = MessageFormat.format( txt, param );
    httpSession.getServletContext().log( msg, exception );
  }

  private Enumeration<String> createAttributeNameEnumeration() {
    Set<String> names;
    synchronized( lock ) {
      names = new HashSet<String>( attributes.keySet() );
    }
    final Iterator<String> iterator = names.iterator();
    return new Enumeration<String>() {
      public boolean hasMoreElements() {
        return iterator.hasNext();
      }
      public String nextElement() {
        return iterator.next();
      }
    };
  }

}
