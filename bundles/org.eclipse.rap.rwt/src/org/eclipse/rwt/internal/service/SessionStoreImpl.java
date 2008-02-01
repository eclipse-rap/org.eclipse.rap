/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.service;

import java.util.*;

import javax.servlet.http.*;

import org.eclipse.rwt.internal.lifecycle.UICallBackServiceHandler;
import org.eclipse.rwt.internal.util.ParamCheck;
import org.eclipse.rwt.service.*;

public final class SessionStoreImpl 
  implements ISessionStore, HttpSessionBindingListener 
{

  public static final String ID_SESSION_STORE = SessionStoreImpl.class.getName();
  
  private final Map attributes = new HashMap();
  private final Set listeners = new HashSet();
  private final HttpSession session;
  private boolean bound;
  private boolean aboutUnbound;

  
  public SessionStoreImpl( final HttpSession session ) {
    ParamCheck.notNull( session, "session" );
    this.session = session;
    this.session.setAttribute( ID_SESSION_STORE, this );
    bound = true;
  }
  
  public Object getAttribute( final String name ) {
    checkBound();
    synchronized( attributes ) {
      return attributes.get( name );
    }
  }

  public void setAttribute( final String name, final Object value ) {
    checkBound();
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
  
  public void removeAttribute( final String name ) {
    checkBound();
    fireValueUnbound( name, removeAttributeInternal( name ) );
  }

  public Enumeration getAttributeNames() {
    checkBound();
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
  
  public String getId() {
    return session.getId();
  }
  
  public HttpSession getHttpSession() {
    return session;
  }
  
  public boolean isBound() {
    return bound;
  }

  public void addSessionStoreListener( final SessionStoreListener lsnr ) {
    checkAboutUnbound();
    checkBound();
    synchronized( listeners ) {
      listeners.add( lsnr );
    }
  }

  public void removeSessionStoreListener( final SessionStoreListener lsnr ) {
    checkAboutUnbound();
    checkBound();
    synchronized( listeners ) {
      listeners.remove( lsnr );
    }
  }

  
  ///////////////////////////////////////
  // interface HttpSessionBindingListener
  
  public void valueBound( final HttpSessionBindingEvent event ) {
    bound = true;
    aboutUnbound = false;
  }
  
  public void valueUnbound( final HttpSessionBindingEvent event ) {
    boolean fakeContext = false;
    if( !ContextProvider.hasContext() ) {
      fakeContext = true;
      ServiceContext context = UICallBackServiceHandler.getFakeContext( this );
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
      ( ( SessionStoreListener )lsnrs[ i ] ).beforeDestroy( evt );
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
        fireValueUnbound( name, attribute );
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
  
  private void checkBound() {
    if( !bound ) {
      throw new IllegalStateException( "The session store has been unbound." );
    }
  }
  
  private void checkAboutUnbound() {
    if( aboutUnbound ) {
      String msg = "The session store is about to be unbound.";
      throw new IllegalStateException( msg );
    }
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
}
