/*******************************************************************************
 * Copyright (c) 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.scrollbarkit;

import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_SELECTION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ScrollBar;
import org.junit.Before;
import org.junit.Test;


public class ScrollBarOperationHandler_Test {

  private ScrollBar scrollBar;
  private ScrollBarOperationHandler handler;

  @Before
  public void setUp() {
    scrollBar = mock( ScrollBar.class );
    handler = new ScrollBarOperationHandler( scrollBar );
  }

  @Test
  public void testHandleNotifySelection() {
    JsonObject properties = new JsonObject().add( "altKey", true ).add( "shiftKey", true );
    handler.handleNotify( EVENT_SELECTION, properties );

    verify( scrollBar ).notifyListeners( eq( SWT.Selection ), any( Event.class ) );
  }

}
