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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.swt.widgets.Widget;
import org.junit.Test;


public class WidgetOperationHandler_Test {

  @Test
  public void testHandleSet_delegatesToHandleSetWithWidget() {
    Widget widget = mock( Widget.class );
    JsonObject properties = new JsonObject();
    WidgetOperationHandler<Widget> handler = spy( new TestWidgetOperationHandler( widget ) );

    handler.handleSet( properties );

    verify( handler ).handleSet( widget, properties );
  }

  @Test( expected = UnsupportedOperationException.class )
  public void testHandleSet_throwsExceptionIfNotSupported() {
    Widget widget = mock( Widget.class );
    JsonObject properties = new JsonObject();
    WidgetOperationHandler<Widget> handler = spy( new WidgetOperationHandler<Widget>( widget ) {} );

    handler.handleSet( properties );
  }

  @Test
  public void testHandleCall_delegatesToHandleCallWithWidget() {
    Widget widget = mock( Widget.class );
    JsonObject properties = new JsonObject();
    WidgetOperationHandler<Widget> handler = spy( new TestWidgetOperationHandler( widget ) );

    handler.handleCall( "foo", properties );

    verify( handler ).handleCall( widget, "foo", properties );
  }

  @Test( expected = UnsupportedOperationException.class )
  public void testHandleCall_throwsExceptionIfNotSupported() {
    Widget widget = mock( Widget.class );
    JsonObject properties = new JsonObject();
    WidgetOperationHandler<Widget> handler = spy( new WidgetOperationHandler<Widget>( widget ) {} );

    handler.handleCall( "foo", properties );
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

  }

}
