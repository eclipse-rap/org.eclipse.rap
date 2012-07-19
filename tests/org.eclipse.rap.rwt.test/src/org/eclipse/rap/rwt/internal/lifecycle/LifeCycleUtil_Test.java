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
package org.eclipse.rap.rwt.internal.lifecycle;

import static org.mockito.Mockito.mock;
import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.lifecycle.IUIThreadHolder;
import org.eclipse.rap.rwt.internal.lifecycle.LifeCycleUtil;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.service.RequestParams;
import org.eclipse.rap.rwt.internal.service.SessionStoreImpl;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestSession;
import org.eclipse.rwt.lifecycle.UICallBack;
import org.eclipse.rwt.service.ISessionStore;
import org.eclipse.swt.widgets.Display;


public class LifeCycleUtil_Test extends TestCase {

  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
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
    IUIThreadHolder uiThread = LifeCycleUtil.getUIThread( ContextProvider.getSessionStore() );

    assertNull( uiThread );
  }

  public void testGetUIThreadWithMultipleSessions() {
    IUIThreadHolder uiThread1 = mock( IUIThreadHolder.class );
    IUIThreadHolder uiThread2 = mock( IUIThreadHolder.class );
    ISessionStore session1 = new SessionStoreImpl( new TestSession() );
    ISessionStore session2 = new SessionStoreImpl( new TestSession() );

    LifeCycleUtil.setUIThread( session1, uiThread1 );
    LifeCycleUtil.setUIThread( session2, uiThread2 );

    assertSame( uiThread1, LifeCycleUtil.getUIThread( session1 ) );
    assertSame( uiThread2, LifeCycleUtil.getUIThread( session2 ) );
  }

  public void testIsStartup_withoutDisplayCreated() {
    assertTrue( LifeCycleUtil.isStartup() );
  }

  public void testIsStartup_withStartupParameter() {
    Fixture.fakeRequestParam( RequestParams.STARTUP, "foo" );

    assertTrue( LifeCycleUtil.isStartup() );
  }

  public void testIsStartup_whenDisplayWasCreated() {
    new Display();

    assertFalse( LifeCycleUtil.isStartup() );
  }

}
