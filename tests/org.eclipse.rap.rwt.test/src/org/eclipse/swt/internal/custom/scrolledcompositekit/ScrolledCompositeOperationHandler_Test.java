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
package org.eclipse.swt.internal.custom.scrolledcompositekit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.ScrollBar;
import org.junit.Before;
import org.junit.Test;


public class ScrolledCompositeOperationHandler_Test {

  private ScrolledComposite composite;
  private ScrolledCompositeOperationHandler handler;

  @Before
  public void setUp() {
    composite = mock( ScrolledComposite.class );
    when( composite.getOrigin() ).thenReturn( new Point( 0, 0 ) );
    handler = new ScrolledCompositeOperationHandler( composite );
  }

  @Test
  public void testHandleSetSelection_withoutScrollbars() {
    handler.handleSet( new JsonObject()
      .add( "horizontalBar.selection", 1 )
      .add( "verticalBar.selection", 2 ) );

    verify( composite ).setOrigin( new Point( 0, 0 ) );
  }

  @Test
  public void testHandleSetSelection_horizontalScrollBar() {
    when( composite.getHorizontalBar() ).thenReturn( mock( ScrollBar.class ) );
    handler.handleSet( new JsonObject().add( "horizontalBar.selection", 1 ) );

    verify( composite ).setOrigin( new Point( 1, 0 ) );
  }

  @Test
  public void testHandleSetSelection_verticalScrollBar() {
    when( composite.getVerticalBar() ).thenReturn( mock( ScrollBar.class ) );
    handler.handleSet( new JsonObject().add( "verticalBar.selection", 1 ) );

    verify( composite ).setOrigin( new Point( 0, 1 ) );
  }

  @Test
  public void testHandleSetSelection_bothScrollBars() {
    when( composite.getHorizontalBar() ).thenReturn( mock( ScrollBar.class ) );
    when( composite.getVerticalBar() ).thenReturn( mock( ScrollBar.class ) );
    handler.handleSet( new JsonObject()
      .add( "horizontalBar.selection", 1 )
      .add( "verticalBar.selection", 2 ) );

    verify( composite ).setOrigin( new Point( 1, 2 ) );
  }

}
