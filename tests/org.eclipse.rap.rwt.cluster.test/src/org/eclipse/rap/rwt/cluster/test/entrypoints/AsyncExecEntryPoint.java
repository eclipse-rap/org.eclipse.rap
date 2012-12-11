/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.cluster.test.entrypoints;

import java.io.Serializable;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.rap.rwt.lifecycle.UICallBack;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.rap.rwt.widgets.ClusteredSynchronizer;
import org.eclipse.swt.widgets.Display;


public class AsyncExecEntryPoint implements EntryPoint {

  private static final String ATTRIBUTE_NAME = "foo";
  private static final Boolean ATTRIBUTE_VALUE = Boolean.TRUE;

  public static void scheduleAsyncRunnable( Display display ) {
    display.asyncExec( new AsyncExecRunnable( display ) );
  }
  
  public static void scheduleSyncRunnable( final Display display ) throws InterruptedException {
    Thread thread = new Thread( new Runnable() {
      public void run() {
        display.syncExec( new AsyncExecRunnable( display ) );
      }
    } );
    thread.setDaemon( true );
    thread.start();
    Thread.sleep( 400 );
  }

  public static boolean wasRunnableExecuted( UISession uiSession ) {
    return ATTRIBUTE_VALUE.equals( uiSession.getAttribute( ATTRIBUTE_NAME ) ); 
  }

  public int createUI() {
    Display display = new Display();
    display.setSynchronizer( new ClusteredSynchronizer( display ) );
    return 0;
  }
  
  private static class AsyncExecRunnable implements Runnable, Serializable {
    private final Display display;

    AsyncExecRunnable( Display display ) {
      this.display = display;
    }

    public void run() {
      UICallBack.runNonUIThreadWithFakeContext( display, new Runnable() {
        public void run() {
          RWT.getUISession().setAttribute( ATTRIBUTE_NAME, ATTRIBUTE_VALUE );
        }
      } );
    }
  }
}
