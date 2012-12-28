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
package org.eclipse.swt.events;

import static org.mockito.Mockito.mock;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;
import org.junit.Test;


public class TraverseEvent_Test {

  @Test
  public void testUntypedEventConstructor() {
    Event event = new Event();
    event.display = mock( Display.class );
    event.widget = mock( Widget.class );
    event.time = 323;
    event.character = 'x';
    event.keyCode = 43;
    event.stateMask = 5;
    event.doit = true;
    event.detail = 6;
    event.data = new Object();

    TraverseEvent traverseEvent = new TraverseEvent( event );

    EventTestHelper.assertFieldsEqual( traverseEvent, event );
  }

}
