/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.expanditemkit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ExpandItemOperationHandler_Test {

  private ExpandItem item;
  private ExpandItemOperationHandler handler;

  @Before
  public void setUp() {
    Fixture.setUp();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    ExpandBar expandBar = new ExpandBar( shell, SWT.V_SCROLL );
    item = new ExpandItem( expandBar, SWT.NONE );
    handler = new ExpandItemOperationHandler( item );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testHandleSetЕxpanded_expand() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );

    handler.handleSet( new JsonObject().add( "expanded", true ) );

    assertTrue( item.getExpanded() );
  }

  @Test
  public void testHandleSetЕxpanded_collaps() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    item.setExpanded( true );

    handler.handleSet( new JsonObject().add( "expanded", false ) );

    assertFalse( item.getExpanded() );
  }

}
