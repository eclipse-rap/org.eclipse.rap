/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.events;

import static org.mockito.Mockito.mock;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;
import org.junit.Test;


public class TreeEvent_Test {

  @Test
  public void testUntypedEventConstructor() {
    Event event = new Event();
    event.display = mock( Display.class );
    event.widget = mock( Widget.class );
    event.time = 7;
    event.data = new Object();

    TreeEvent treeEvent = new TreeEvent( event );

    EventTestHelper.assertFieldsEqual( treeEvent, event );
  }

}
