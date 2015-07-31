/*******************************************************************************
 * Copyright (c) 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.lifecycle;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class DisplayUtil_Test {

  private Display display;

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakeResponseWriter();
    display = new Display();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testGetLCA() {
    assertNotNull( DisplayUtil.getLCA( display ) );
  }

  @Test
  public void testGetLCA_returnsSameAdapterForEachInvocation() {
    Object adapter1 = DisplayUtil.getLCA( display );
    Object adapter2 = DisplayUtil.getLCA( display );

    assertSame( adapter1, adapter2 );
  }

  @Test
  public void testGetLCA_returnsSameAdapterForDifferentDisplays() {
    Object adapter1 = DisplayUtil.getLCA( display );
    display.dispose();
    Display display2 = new Display();

    Object adapter2 = DisplayUtil.getLCA( display2 );

    assertSame( adapter1, adapter2 );
  }

  @Test
  public void testGetLCA_isApplicationScoped() {
    Object adapter1 = display.getAdapter( DisplayLifeCycleAdapter.class );
    newSession();
    Display display2 = new Display();

    Object adapter2 = display2.getAdapter( DisplayLifeCycleAdapter.class );

    assertSame( adapter1, adapter2 );
  }

  private static void newSession() {
    ContextProvider.disposeContext();
    Fixture.createServiceContext();
  }

}
