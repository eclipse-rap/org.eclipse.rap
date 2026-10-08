/*******************************************************************************
 * Copyright (c) 2010, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.events;

import static org.mockito.Mockito.mock;

import org.eclipse.rap.rwt.internal.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class MenuDetectEvent_Test {

  private Display display;

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testUntypedEventConstructor() {
    Event event = new Event();
    event.display = display;
    event.widget = mock( Widget.class );
    event.time = 4325;
    event.x = 10;
    event.y = 20;
    event.doit = true;
    event.data = new Object();

    MenuDetectEvent menuDeteectEvent = new MenuDetectEvent( event );

    EventTestHelper.assertFieldsEqual( menuDeteectEvent, event );
  }

}
