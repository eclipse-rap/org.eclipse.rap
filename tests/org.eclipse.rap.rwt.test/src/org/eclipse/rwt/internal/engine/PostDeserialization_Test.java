/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.engine;

import junit.framework.TestCase;

import org.eclipse.rwt.TestSession;
import org.eclipse.rwt.internal.service.SessionStoreImpl;
import org.eclipse.rwt.service.ISessionStore;


public class PostDeserialization_Test extends TestCase {
  
  private ISessionStore sessionStore;
  private boolean wasExecuted;

  public void testRunProcessor() {
    PostDeserialization.addProcessor( sessionStore, new Runnable() {
      public void run() {
        wasExecuted = true;
      }
    } );
    
    PostDeserialization.runProcessors( sessionStore );
    
    assertTrue( wasExecuted );
  }
  
  public void testRunProcessorFromDifferentSessionStore() {
    PostDeserialization.addProcessor( sessionStore, new Runnable() {
      public void run() {
        wasExecuted = true;
      }
    } );
    
    SessionStoreImpl differentSessionStore = createSessionStore();
    PostDeserialization.runProcessors( differentSessionStore );
    
    assertFalse( wasExecuted );
  }
  
  @Override
  protected void setUp() throws Exception {
    sessionStore = createSessionStore();
    wasExecuted = false;
  }

  private static SessionStoreImpl createSessionStore() {
    return new SessionStoreImpl( new TestSession() );
  }
}
