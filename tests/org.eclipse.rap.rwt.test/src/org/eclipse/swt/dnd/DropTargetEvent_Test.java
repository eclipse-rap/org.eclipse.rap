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
import junit.framework.TestCase;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;


public class DropTargetEvent_Test extends TestCase {

  public void testUntypedEventConstructor() throws Exception {
    DNDEvent event = new DNDEvent();
    event.display = mock( Display.class );
    event.widget = mock( Widget.class );
    event.time = 989;
    event.x = 1;
    event.y = 2;
    event.dataType = mock( TransferData.class );
    event.dataTypes = new TransferData[] { mock( TransferData.class ) };
    event.doit = true;
    event.detail = 6;
    event.data = new Object();
    event.operations = 7;
    event.feedback = 9;
    event.item = mock( Widget.class );
    
    DropTargetEvent dropTargetEvent = new DropTargetEvent( event );

    assertEquals( dropTargetEvent.display, event.display );
    assertEquals( dropTargetEvent.widget, event.widget );
    assertEquals( dropTargetEvent.time, event.time );
    assertEquals( dropTargetEvent.x, event.x );
    assertEquals( dropTargetEvent.y, event.y );
    assertEquals( dropTargetEvent.currentDataType, event.dataType );
    assertEquals( dropTargetEvent.dataTypes, event.dataTypes );
    assertEquals( dropTargetEvent.detail, event.detail );
    assertEquals( dropTargetEvent.data, event.data );
    assertEquals( dropTargetEvent.operations, event.operations );
    assertEquals( dropTargetEvent.feedback, event.feedback );
    assertEquals( dropTargetEvent.item, event.item );
  }

}
