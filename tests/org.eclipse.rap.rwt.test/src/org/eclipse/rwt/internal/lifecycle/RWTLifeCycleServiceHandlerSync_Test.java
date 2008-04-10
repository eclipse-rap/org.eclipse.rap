/*******************************************************************************
 * Copyright (c) 2002-2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.lifecycle;

import java.io.IOException;

import javax.servlet.ServletException;

import junit.framework.TestCase;

import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.ServiceContext;
import org.eclipse.swt.RWTFixture;


public class RWTLifeCycleServiceHandlerSync_Test extends TestCase {
  private static final String BEFORE_SYNCHRONIZE = "before synchronize";

  private Object lock = new Object();
  private StringBuffer log = new StringBuffer();

  private class TestHandler extends RWTLifeCycleServiceHandlerSync {
    void serviceInternal() throws ServletException, IOException {
      log.append( BEFORE_SYNCHRONIZE );
      synchronized( lock ) {
        try {
          lock.notify();
          lock.wait();
        } catch( InterruptedException e ) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      super.serviceInternal();
    }
  }

  private class Worker implements Runnable {
    private final ServiceContext context;
    private final RWTLifeCycleServiceHandlerSync syncHandler;

    private Worker( final ServiceContext context,
                    final RWTLifeCycleServiceHandlerSync syncHandler )
    {
      this.context = context;
      this.syncHandler = syncHandler;
    }

    public void run() {
      ContextProvider.setContext( context );
      try {
        syncHandler.service();
      } catch( ServletException e ) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch( IOException e ) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } finally {
        ContextProvider.releaseContextHolder();
      }
    }
  }
  
  public void testSessionSynchronizationInRWTLifeCycleServiceHandlerSync()
    throws InterruptedException
  {
    RWTLifeCycleServiceHandlerSync syncHandler = new TestHandler();
    ServiceContext context = ContextProvider.getContext();
    synchronized( lock ) {      
      Thread thread1 = new Thread( new Worker( context, syncHandler ) );
      thread1.setDaemon( true );
      thread1.start();
      lock.wait();
    }

    synchronized( lock ) {      
      Thread thread2 = new Thread( new Worker( context, syncHandler ) );
      thread2.setDaemon( true );
      thread2.start();
      // 100 ms should be enough for thread2 to reach the synchronization block
      // of the TestHandler if synchronization on session store in 
      // RWTLifeCycleServiceHandlerSync fails
      lock.wait( 100 );
    }
    
    assertEquals( BEFORE_SYNCHRONIZE, log.toString() );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }
  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
