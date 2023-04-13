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
package org.eclipse.swt.internal.widgets.expandbarkit;

import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_COLLAPSE;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_EXPAND;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


public class ExpandBarOperationHandler_Test {

  private ExpandBar expandBar;
  private ExpandBarOperationHandler handler;

  @Before
  public void setUp() {
    Fixture.setUp();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    expandBar = spy( new ExpandBar( shell, SWT.V_SCROLL ) );
    handler = new ExpandBarOperationHandler( expandBar );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testHandleNotifyExpand() {
    ExpandItem item = new ExpandItem( expandBar, SWT.NONE );
    JsonObject properties = new JsonObject().add( "item", getId( item ) );

    handler.handleNotify( EVENT_EXPAND, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( expandBar ).notifyListeners( eq( SWT.Expand ), captor.capture() );
    assertEquals( item, captor.getValue().item );
  }

  @Test
  public void testHandleNotifyCollapse() {
    ExpandItem item = new ExpandItem( expandBar, SWT.NONE );
    JsonObject properties = new JsonObject().add( "item", getId( item ) );

    handler.handleNotify( EVENT_COLLAPSE, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( expandBar ).notifyListeners( eq( SWT.Collapse ), captor.capture() );
    assertEquals( item, captor.getValue().item );
  }

}
