/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
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

import javax.servlet.http.*;

import org.eclipse.rwt.internal.lifecycle.ISessionShutdownAdapter;
import org.eclipse.rwt.internal.lifecycle.UICallBackServiceHandler;
import org.eclipse.rwt.internal.util.ParamCheck;
import org.eclipse.rwt.service.*;

public final class SessionStoreImpl 
  implements ISessionStore, HttpSessionBindingListener 
{
  public static final String ID_SESSION_STORE
    = SessionStoreImpl.class.getName();
  
  private final Map attributes;
  private final Set listeners;
  private final HttpSession session;
  private final String id;
  private boolean bound;
  private boolean aboutUnbound;
  private ISessionShutdownAdapter shutdownAdapter;

  
  public SessionStoreImpl( final HttpSession session ) {
    ParamCheck.notNull( session, "session" );
    attributes = new HashMap();
    listeners = new HashSet();
    this.id = session.getId();
    this.session = session;
    this.session.setAttribute( ID_SESSION_STORE, this );
    bound = true;
  }
  
  public void setShutdownAdapter( final ISessionShutdownAdapter adapter ) {
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
  
  public Object getAttribute( final String name ) {
    Object result = null;
    if( bound ) {
      synchronized( attributes ) {
        result = attributes.get( name );
      }
    }
    return result;
  }

  public boolean setAttribute( final String name, final Object value ) {
    boolean result = false;
    if( bound ) {
      result = true;
      if( value == null ) {
        removeAttribute( name );
      } else {
        Object removed = null;
        synchronized( attributes ) {
          if( attributes.containsKey( name ) ) {
            removed = removeAttributeInternal( name );
          }
          attributes.put( name, value );
        }
        if( removed != null ) {
          fireValueUnbound( name, removed );
        }
        fireValueBound( name, value );
      }
    }
    return result;
  }
  
  public boolean removeAttribute( final String name ) {
    boolean result = false;
    if( bound ) {
      result = true;
      fireValueUnbound( name, removeAttributeInternal( name ) );
    }
    return result;
  }

  public Enumeration getAttributeNames() {
    Enumeration result;
    if( bound ) {
      result = createAttributeNameEnumeration();
    } else {
      result = createEmptyEnumeration();
    }
    return result;
  }

  public String getId() {
    return id;
  }
  
  public HttpSession getHttpSession() {
    return session;
  }
  
  public boolean isBound() {
    return bound;
  }

  public boolean addSessionStoreListener( final SessionStoreListener lsnr ) {
    boolean result = false;
    if( bound && !aboutUnbound ) {
      result = true;
      synchronized( listeners ) {
        listeners.add( lsnr );
      }
    }
    return result;
  }

  public boolean removeSessionStoreListener( final SessionStoreListener lsnr ) {
    boolean result = false;
    if( bound && !aboutUnbound ) {
      result = true;
      synchronized( listeners ) {
        listeners.remove( lsnr );
      }
    }
    return result;
  }

  
  ///////////////////////////////////////
  // interface HttpSessionBindingListener
  
  public void valueBound( final HttpSessionBindingEvent event ) {
    bound = true;
    aboutUnbound = false;
  }
  
  public void valueUnbound( final HttpSessionBindingEvent event ) {
    if( shutdownAdapter != null ) {
      shutdownAdapter.interceptShutdown();
    } else {
      boolean fakeContext = false;
      if( !ContextProvider.hasContext() ) {
        fakeContext = true;
        ServiceContext ctx = UICallBackServiceHandler.getFakeContext( this );
        ContextProvider.setContext( ctx );
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
  
  private void doValueUnbound() {
    aboutUnbound = true;
    Object[] lsnrs;
    synchronized( listeners ) {      
      lsnrs = listeners.toArray();
    }
    SessionStoreEvent evt = new SessionStoreEvent( this );
    for( int i = 0; i < lsnrs.length; i++ ) {
      try {
        ( ( SessionStoreListener )lsnrs[ i ] ).beforeDestroy( evt );
      } catch( final RuntimeException re ) {
        String txt = "Could not execute {0}.beforeDestroy(SessionStoreEvent).";
        Object[] param = new Object[] { lsnrs[ i ].getClass().getName() };
        String msg = MessageFormat.format( txt, param );
        ServletLog.log( msg, re );
      }
    }
    Object[] names;
    synchronized( attributes ) {      
      names = attributes.keySet().toArray();
    }
    
    ///////////////////////////////////////////////////////////////
    // we remove the attributes from the datastructure as
    // late as possible to allow a defined shutdown of
    // the application
    Set removedAttributes = new HashSet();
    try {
      for( int i = 0; i < names.length; i++ ) {
        String name = ( String )names[ i ];
        Object attribute = null;
        synchronized( attributes ) {
          attribute = attributes.get( name );
        }
        removedAttributes.add( name );
        try {
          fireValueUnbound( name, attribute );
        } catch( final RuntimeException re ) {
          String txt
            = "Could not execute {0}.valueUnbound(HttpSessionBindingEvent).";
          Object[] param = new Object[] { attribute.getClass().getName() };
          String msg = MessageFormat.format( txt, param );
          ServletLog.log( msg, re );
        }
      }
    } finally {
      synchronized( attributes ) {
        Iterator iterator = removedAttributes.iterator();
        while( iterator.hasNext() ) {
          attributes.remove( iterator.next() );
        }
      }      
    }
    
    listeners.clear();
    bound = false;
    aboutUnbound = false;
  }

  private Object removeAttributeInternal( final String name ) {
    Object result;
    synchronized( attributes ) {      
      result = attributes.remove( name );
    }
    return result;
  }
  
  private void fireValueBound( final String name, final Object value ) {
    if( value instanceof HttpSessionBindingListener ) {
      HttpSessionBindingListener listener
        = ( HttpSessionBindingListener )value;
      HttpSessionBindingEvent evt 
        = new HttpSessionBindingEvent( session, name, value );
      listener.valueBound( evt );
    }
  }
  
  private void fireValueUnbound( final String name, Object removed ) {
    if( removed instanceof HttpSessionBindingListener ) {
      HttpSessionBindingListener listener
        = ( HttpSessionBindingListener )removed;
      HttpSessionBindingEvent evt 
        = new HttpSessionBindingEvent( session, name, removed );
      listener.valueUnbound( evt );
    }
  }

  private Enumeration createEmptyEnumeration() {
    return new Enumeration() {
      public boolean hasMoreElements() {
        return false;
      }
      public Object nextElement() {
        throw new NoSuchElementException();
      }
    };
  }

  private Enumeration createAttributeNameEnumeration() {
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
}
