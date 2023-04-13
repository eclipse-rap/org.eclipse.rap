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
package org.eclipse.swt.internal.widgets.menukit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.internal.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ArmEvent;
import org.eclipse.swt.events.ArmListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class MenuOperationHandler_Test {

  private Shell shell;
  private Menu menu;
  private MenuOperationHandler handler;

  @Before
  public void setUp() {
    Fixture.setUp();
    Display display = new Display();
    shell = new Shell( display, SWT.NONE );
    menu = mock( Menu.class );
    when( menu.getItems() ).thenReturn( new MenuItem[ 0 ] );
    handler = new MenuOperationHandler( menu );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testHandleNotifyShow() {
    JsonObject properties = new JsonObject();

    handler.handleNotify( "Show", properties );

    verify( menu ).notifyListeners( eq( SWT.Show ), any( Event.class ) );
  }

  @Test
  public void testHandleNotifyShow_fireArmEvents() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    menu = new Menu( shell, SWT.POP_UP );
    handler = new MenuOperationHandler( menu );
    MenuItem pushItem = new MenuItem( menu, SWT.PUSH );
    MenuItem radioItem = new MenuItem( menu, SWT.RADIO );
    MenuItem checkItem = new MenuItem( menu, SWT.CHECK );
    ArmListener pushArmListener = mock( ArmListener.class );
    pushItem.addArmListener( pushArmListener );
    ArmListener radioArmListener = mock( ArmListener.class );
    radioItem.addArmListener( radioArmListener );
    ArmListener checkArmListener = mock( ArmListener.class );
    checkItem.addArmListener( checkArmListener );
    JsonObject properties = new JsonObject();

    handler.handleNotify( "Show", properties );

    verify( pushArmListener ).widgetArmed( any( ArmEvent.class ) );
    verify( radioArmListener ).widgetArmed( any( ArmEvent.class ) );
    verify( checkArmListener ).widgetArmed( any( ArmEvent.class ) );
  }

  @Test
  public void testHandleNotifyHide() {
    JsonObject properties = new JsonObject();

    handler.handleNotify( "Hide", properties );

    verify( menu ).notifyListeners( eq( SWT.Hide ), any( Event.class ) );
  }

  @Test
  public void testHandleNotifyHelp() {
    JsonObject properties = new JsonObject();

    handler.handleNotify( "Help", properties );

    verify( menu ).notifyListeners( eq( SWT.Help ), any( Event.class ) );
  }

}
