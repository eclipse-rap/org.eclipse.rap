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
package org.eclipse.swt.widgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;


public class EventTable_Test {

  private static final int EVENT_1 = 1;
  private EventTable eventTable;

  @Before
  public void setUp() {
    eventTable = new EventTable();
  }

  @Test
  public void testHook() {
    Listener listener = mock( Listener.class );

    eventTable.hook( EVENT_1, listener );

    assertEquals( 1, eventTable.size() );
  }

  @Test
  public void testUnhook() {
    Listener listener = mock( Listener.class );
    eventTable.hook( EVENT_1, listener );

    eventTable.unhook( EVENT_1, listener );

    assertEquals( 0, eventTable.size() );
  }

  @Test
  public void testUnhookUnknownEventType() {
    Listener listener = mock( Listener.class );
    eventTable.hook( EVENT_1, listener );

    eventTable.unhook( 23, listener );

    assertEquals( 1, eventTable.size() );
  }

  @Test
  public void testUnhookUnknownListener() {
    Listener listener = mock( Listener.class );
    eventTable.hook( EVENT_1, listener );

    eventTable.unhook( EVENT_1, mock( Listener.class ) );

    assertEquals( 1, eventTable.size() );
  }

  @Test
  public void testHooksUnknownEventType() {
    boolean hooks = eventTable.hooks( 23 );

    assertFalse( hooks );
  }

  @Test
  public void testHooksKnownEventType() {
    eventTable.hook( EVENT_1, mock( Listener.class ) );

    boolean hooks = eventTable.hooks( EVENT_1 );

    assertTrue( hooks );
  }
}
