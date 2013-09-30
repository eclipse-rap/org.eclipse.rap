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
package org.eclipse.rap.rwt.internal.protocol;

import static org.eclipse.rap.rwt.internal.protocol.WidgetOperationHandler.createSelectionEvent;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;
import org.junit.Before;
import org.junit.Test;


public class WidgetOperationHandler_Test {

  private Widget widget;
  private WidgetOperationHandler<Widget> handler;

  @Before
  public void setUp() {
    widget = mock( Widget.class );
    handler = new WidgetOperationHandler<Widget>( widget ) {};
  }

  @Test
  public void testCreateSelectionEvent_withoutProperties() {
    Event event = createSelectionEvent( SWT.Selection, new JsonObject() );

    assertEquals( SWT.Selection, event.type );
  }

  @Test
  public void testCreateSelectionEvent_withStateMask() {
    JsonObject properties = new JsonObject().add( "altKey", true ).add( "ctrlKey", true );

    Event event = createSelectionEvent( SWT.Selection, properties );

    assertEquals( SWT.ALT | SWT.CTRL, event.stateMask );
  }

  @Test
  public void testCreateSelectionEvent_withDetail_check() {
    JsonObject properties = new JsonObject().add( "detail", "check" );

    Event event = createSelectionEvent( SWT.Selection, properties );

    assertEquals( SWT.CHECK, event.detail );
  }

  @Test
  public void testCreateSelectionEvent_withDetail_search() {
    JsonObject properties = new JsonObject().add( "detail", "search" );

    Event event = createSelectionEvent( SWT.Selection, properties );

    assertEquals( SWT.ICON_SEARCH, event.detail );
  }

  @Test
  public void testCreateSelectionEvent_withDetail_cancel() {
    JsonObject properties = new JsonObject().add( "detail", "cancel" );

    Event event = createSelectionEvent( SWT.Selection, properties );

    assertEquals( SWT.ICON_CANCEL, event.detail );
  }

  @Test
  public void testCreateSelectionEvent_withDetail_hyperlink() {
    JsonObject properties = new JsonObject().add( "detail", "hyperlink" );

    Event event = createSelectionEvent( SWT.Selection, properties );

    assertEquals( RWT.HYPERLINK, event.detail );
  }

  @Test
  public void testCreateSelectionEvent_withDetail_drag() {
    JsonObject properties = new JsonObject().add( "detail", "drag" );

    Event event = createSelectionEvent( SWT.Selection, properties );

    assertEquals( SWT.DRAG, event.detail );
  }

  @Test
  public void testCreateSelectionEvent_withDetail_arrow() {
    JsonObject properties = new JsonObject().add( "detail", "arrow" );

    Event event = createSelectionEvent( SWT.Selection, properties );

    assertEquals( SWT.ARROW, event.detail );
  }

  @Test
  public void testCreateSelectionEvent_withText() {
    JsonObject properties = new JsonObject().add( "text", "foo" );

    Event event = createSelectionEvent( SWT.Selection, properties );

    assertEquals( "foo", event.text );
  }

  @Test
  public void testCreateSelectionEvent_withBounds() {
    JsonObject properties = new JsonObject()
      .add( "x", 1 )
      .add( "y", 2 )
      .add( "width", 3 )
      .add( "height", 4 );

    Event event = createSelectionEvent( SWT.Selection, properties );

    assertEquals( new Rectangle( 1, 2, 3, 4 ), event.getBounds() );
  }

  @Test
  public void testHandleSet_delegatesToHandleSetWithWidget() {
    JsonObject properties = new JsonObject();
    WidgetOperationHandler<Widget> handler = spy( new TestWidgetOperationHandler( widget ) );

    handler.handleSet( properties );

    verify( handler ).handleSet( widget, properties );
  }

  @Test( expected = UnsupportedOperationException.class )
  public void testHandleSet_throwsExceptionIfNotSupported() {
    JsonObject properties = new JsonObject();

    handler.handleSet( properties );
  }

  @Test
  public void testHandleCall_delegatesToHandleCallWithWidget() {
    JsonObject properties = new JsonObject();
    WidgetOperationHandler<Widget> handler = spy( new TestWidgetOperationHandler( widget ) );

    handler.handleCall( "foo", properties );

    verify( handler ).handleCall( widget, "foo", properties );
  }

  @Test( expected = UnsupportedOperationException.class )
  public void testHandleCall_throwsExceptionIfNotSupported() {
    JsonObject properties = new JsonObject();

    handler.handleCall( "foo", properties );
  }

  @Test
  public void testHandleNotify_delegatesToHandleCallWithWidget() {
    Widget widget = mock( Widget.class );
    JsonObject properties = new JsonObject();
    WidgetOperationHandler<Widget> handler = spy( new TestWidgetOperationHandler( widget ) );

    handler.handleNotify( "foo", properties );

    verify( handler ).handleNotify( widget, "foo", properties );
  }

  @Test( expected = UnsupportedOperationException.class )
  public void testHandleNotify_throwsExceptionIfNotSupported() {
    JsonObject properties = new JsonObject();

    handler.handleNotify( "foo", properties );
  }

  private static class TestWidgetOperationHandler extends WidgetOperationHandler<Widget> {

    private TestWidgetOperationHandler( Widget widget ) {
      super( widget );
    }

    @Override
    public void handleSet( Widget widget, JsonObject properties ) {
    }

    @Override
    public void handleCall( Widget widget, String method, JsonObject parameters ) {
    }

    @Override
    public void handleNotify( Widget widget, String eventType, JsonObject parameters ) {
    }

  }

}
