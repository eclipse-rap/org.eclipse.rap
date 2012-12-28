/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.dnd;

import static org.mockito.Mockito.mock;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.EventTestHelper;
import org.eclipse.swt.internal.dnd.DNDEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class DragSourceEvent_Test {

  private Display display;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testUntypedEventConstructor() {
    DNDEvent event = new DNDEvent();
    event.display = display;
    event.widget = mock( Widget.class );
    event.time = 4325;
    event.x = 1;
    event.y = 2;
    event.dataType = mock( TransferData.class );
    event.image = display.getSystemImage( SWT.ICON_QUESTION );
    event.doit = true;
    event.detail = 6;
    event.data = new Object();
    event.offsetX = 11;
    event.offsetY = 12;

    DragSourceEvent dragSourceEvent = new DragSourceEvent( event );

    EventTestHelper.assertFieldsEqual( dragSourceEvent, event );
  }

}
