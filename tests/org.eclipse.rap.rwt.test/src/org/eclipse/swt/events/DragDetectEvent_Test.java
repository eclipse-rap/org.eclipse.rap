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
import junit.framework.TestCase;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;


public class DragDetectEvent_Test extends TestCase {

  public void testUntypedEventConstructor() throws Exception {
    Event event = new Event();
    event.display = mock( Display.class );
    event.widget = mock( Widget.class );
    event.time = 323;
    event.x = 1;
    event.y = 2;
    event.button = 3;
    event.stateMask = 4;
    event.doit = true;
    event.count = 5;
    event.data = new Object();
    
    DragDetectEvent dragDetectEvent = new DragDetectEvent( event );
    
    EventTestHelper.assertFieldsEqual( dragDetectEvent, event );
  }

}
