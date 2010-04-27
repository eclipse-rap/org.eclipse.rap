/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.lifecycle;

import org.eclipse.rwt.Fixture;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;

import junit.framework.TestCase;


public class UICallBack_Test extends TestCase {

  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testActivateFromBackgroundThread() throws InterruptedException {
    final Display display = new Display();
    final Throwable[] exception = { null };
    Thread bgThread = new Thread( new Runnable() {
      
      public void run() {
        UICallBack.runNonUIThreadWithFakeContext( display, new Runnable() {

          public void run() {
            try {
              UICallBack.activate( "id" );
            } catch( Throwable e ) {
              exception[ 0 ] = e;
            }
          }
        } );
      }
    } );
    bgThread.start();
    bgThread.join();
    assertTrue( exception[ 0 ] instanceof SWTException );
  }

  public void testActivateWithNullArgument() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    try {
      UICallBack.activate( null );
      fail( "Must not allow null-id" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testDeactivateWithNullArgument() {
    try {
      UICallBack.deactivate( null );
      fail( "Must not allow null-id" );
    } catch( IllegalArgumentException expected ) {
    }
  }
}
