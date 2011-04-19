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
package org.eclipse.rwt.internal.textsize;

import java.util.*;

import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import org.eclipse.rwt.lifecycle.ILifeCycle;
import org.eclipse.rwt.lifecycle.PhaseListener;
import org.eclipse.rwt.service.ISessionStore;
import org.eclipse.rwt.service.SessionStoreListener;


public class MeasurementHandlerRegistrar_Test extends TestCase {

  private Map attributes;
  private ISessionStore session;
  private Set phaseListeners;
  private ILifeCycle lifeCycle;
  private MeasurementHandlerRegistrar registrar;

  
  private final class SessionStore implements ISessionStore {
    private final Map attributes;

    private SessionStore( Map attributes ) {
      this.attributes = attributes;
    }

    public boolean setAttribute( String name, Object value ) {
      attributes.put( name, value );
      return true;
    }

    public boolean removeSessionStoreListener( SessionStoreListener listener ) {
      return false;
    }

    public boolean removeAttribute( String name ) {
      attributes.remove( name );
      return true;
    }

    public boolean isBound() {
      return false;
    }

    public String getId() {
      return null;
    }

    public HttpSession getHttpSession() {
      return null;
    }

    public Enumeration getAttributeNames() {
      return null;
    }

    public Object getAttribute( String name ) {
      return attributes.get( name );
    }

    public boolean addSessionStoreListener( SessionStoreListener listener ) {
      return false;
    }
  }

  private final class LifeCycle implements ILifeCycle {
    private final Set set;

    private LifeCycle( Set set ) {
      this.set = set;
    }

    public void removePhaseListener( PhaseListener listener ) {
      set.remove( listener );
    }

    public void addPhaseListener( PhaseListener listener ) {
      set.add( listener );
    }
  }

  public void testRegister() {
    registrar.register();
    
    assertEquals( 1, attributes.size() );
    assertEquals( 1, phaseListeners.size() );
    assertTrue( registrar.isRegistered() );
  }

  public void testRegistrationIsIdempotent() {
    registrar.register();
    MeasurementHandler handler = getRegisteredMeasurementHandler();
    PhaseListener listener = getRegisteredPhaseListener();

    registrar.register();
    
    assertEquals( 1, attributes.size() );
    assertEquals( 1, phaseListeners.size() );
    assertSame( handler, getRegisteredMeasurementHandler() );
    assertSame( listener, getRegisteredPhaseListener() );
  }
  
  public void testDeregister() {
    registrar.register();
    registrar.deregister();
    
    assertEquals( 0, attributes.size() );
    assertEquals( 0, phaseListeners.size() );
    assertFalse( registrar.isRegistered() );
  }
  
  protected void setUp() throws Exception {
    attributes = new HashMap();
    session = new SessionStore( attributes );
    phaseListeners = new HashSet();
    lifeCycle = new LifeCycle( phaseListeners );
    registrar = new MeasurementHandlerRegistrar( session, lifeCycle );
  }

  private PhaseListener getRegisteredPhaseListener() {
    return ( PhaseListener )phaseListeners.toArray()[ 0 ];
  }

  private MeasurementHandler getRegisteredMeasurementHandler() {
    return ( MeasurementHandler )attributes.values().toArray()[ 0 ];
  }
}