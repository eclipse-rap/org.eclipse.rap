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
package org.eclipse.rwt.internal.lifecycle;

import junit.framework.TestCase;

import org.eclipse.rwt.*;
import org.eclipse.rwt.internal.service.*;
import org.eclipse.rwt.lifecycle.UICallBack;
import org.eclipse.rwt.service.ISessionStore;
import org.eclipse.swt.widgets.Display;


public class LifeCycleUtil_Test extends TestCase {
  
  public void testGetEntryPointWithStartupParameter() {
    String entryPoint = "foo";
    Fixture.fakeRequestParam( RequestParams.STARTUP, entryPoint );
    
    String returnedEntryPoint = LifeCycleUtil.getEntryPoint();
    
    assertEquals( entryPoint, returnedEntryPoint );
  }

  public void testGetEntryPointWithoutStartupParameter() {
    String entryPoint = LifeCycleUtil.getEntryPoint();
    assertEquals( EntryPointManager.DEFAULT, entryPoint );
  }
  
  public void testGetEntryPointWhenDisplayWasCreated() {
    new Display();
    String entryPoint = LifeCycleUtil.getEntryPoint();
    assertNull( entryPoint );
  }
  
  public void testGetSessionDisplayBeforeAnyDisplayCreated() {
    Display sessionDisplay = LifeCycleUtil.getSessionDisplay();
    
    assertNull( sessionDisplay );
  }
  
  public void testGetSessionDisplayWithDisplayCreated() {
    Display display = new Display();

    Display sessionDisplay = LifeCycleUtil.getSessionDisplay();
    
    assertSame( display, sessionDisplay );
  }
  
  public void testGetSessionDisplayAfterDisplayDisposed() {
    Display display = new Display();
    display.dispose();
    
    Display sessionDisplay = LifeCycleUtil.getSessionDisplay();
    
    assertSame( display, sessionDisplay );
  }
  
  public void testGetSessionDisplayFromBackgroundThreadWithoutContext() throws Throwable {
    final Display[] sessionDisplay = { null };
    Runnable runnable = new Runnable() {
      public void run() {
        sessionDisplay[ 0 ] = LifeCycleUtil.getSessionDisplay();
      }
    };
    Fixture.runInThread( runnable );
    assertNull( sessionDisplay[ 0 ] );
  }

  public void testGetSessionDisplayFromBackgroundThreadWithContext() throws Throwable {
    final Display display = new Display();
    final Display[] sessionDisplay = { null };
    Runnable runnable = new Runnable() {
      public void run() {
        UICallBack.runNonUIThreadWithFakeContext( display, new Runnable() {
          public void run() {
            sessionDisplay[ 0 ] = LifeCycleUtil.getSessionDisplay();
          }
        } );
      }
    };
    Fixture.runInThread( runnable );
    assertSame( display, sessionDisplay[ 0 ] );
  }
  
  public void testGetUIThreadForNewSession() {
    IUIThreadHolder uiThread = LifeCycleUtil.getUIThread( ContextProvider.getSession() );
    
    assertNull( uiThread );
  }
  
  public void testGetUIThreadWithMultipleSessions() {
    IUIThreadHolder uiThread1 = new TestUIThreadHolder( null );
    IUIThreadHolder uiThread2 = new TestUIThreadHolder( null );
    ISessionStore session1 = new SessionStoreImpl( new TestSession() );
    ISessionStore session2 = new SessionStoreImpl( new TestSession() ); 
    
    LifeCycleUtil.setUIThread( session1, uiThread1 );
    LifeCycleUtil.setUIThread( session2, uiThread2 );
    
    assertSame( uiThread1, LifeCycleUtil.getUIThread( session1 ) );
    assertSame( uiThread2, LifeCycleUtil.getUIThread( session2 ) );
  }
  
  protected void setUp() throws Exception {
    Fixture.setUp();
  }
  
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
