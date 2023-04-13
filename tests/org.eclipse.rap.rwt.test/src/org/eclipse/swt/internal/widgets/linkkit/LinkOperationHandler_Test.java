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
package org.eclipse.swt.internal.widgets.linkkit;

import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_SELECTION;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


public class LinkOperationHandler_Test {

  private Link link;
  private LinkOperationHandler handler;

  @Before
  public void setUp() {
    Fixture.setUp();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    link = spy( new Link( shell, SWT.NONE ) );
    handler = new LinkOperationHandler( link );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testHandleNotifySelection() {
    link.setText( "foo <a>bar</a>" );

    JsonObject properties = new JsonObject()
      .add( "altKey", true )
      .add( "shiftKey", true )
      .add( "index", 0 );
    handler.handleNotify( EVENT_SELECTION, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( link ).notifyListeners( eq( SWT.Selection ), captor.capture() );
    Event event = captor.getValue();
    assertEquals( SWT.ALT | SWT.SHIFT, event.stateMask );
    assertEquals( "bar", event.text );
  }

  @Test
  public void testHandleNotifySelection_invalidIndex() {
    link.setText( "foo bar" );

    JsonObject properties = new JsonObject().add( "index", 0 );
    handler.handleNotify( EVENT_SELECTION, properties );

    verify( link, never() ).notifyListeners( eq( SWT.Selection ), any( Event.class ) );
  }

}
