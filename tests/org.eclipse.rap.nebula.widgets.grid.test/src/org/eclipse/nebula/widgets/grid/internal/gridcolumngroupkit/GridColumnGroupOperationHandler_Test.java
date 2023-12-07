/*******************************************************************************
 * Copyright (c) 2013, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.grid.internal.gridcolumngroupkit;

import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_COLLAPSE;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_EXPAND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumnGroup;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.internal.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings( {
  "restriction", "deprecation"
} )
public class GridColumnGroupOperationHandler_Test {

  private GridColumnGroup group;
  private GridColumnGroupOperationHandler handler;

  @Before
  public void setUp() {
    Fixture.setUp();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Grid grid = new Grid( shell, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL );
    grid.setBounds( 0, 0, 100, 100 );
    group = mock( GridColumnGroup.class );
    handler = new GridColumnGroupOperationHandler( group );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testHandleSetЕxpanded_expand() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );

    handler.handleSet( new JsonObject().add( "expanded", true ) );

    verify( group ).setExpanded( true );
  }

  @Test
  public void testHandleSetЕxpanded_collaps() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );

    handler.handleSet( new JsonObject().add( "expanded", false ) );

    verify( group ).setExpanded( false );
  }

  @Test
  public void testHandleNotifyExpand() {
    handler.handleNotify( EVENT_EXPAND, new JsonObject() );

    verify( group ).notifyListeners( eq( SWT.Expand ), any( Event.class ) );
  }

  @Test
  public void testHandleNotifyCollapse() {
    handler.handleNotify( EVENT_COLLAPSE, new JsonObject() );

    verify( group ).notifyListeners( eq( SWT.Collapse ), any( Event.class ) );
  }

}
