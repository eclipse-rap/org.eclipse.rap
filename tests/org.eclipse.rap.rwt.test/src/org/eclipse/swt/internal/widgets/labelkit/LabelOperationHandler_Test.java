/*******************************************************************************
 * Copyright (c) 2013, 2016 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.labelkit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.swt.internal.widgets.IControlAdapter;
import org.eclipse.swt.widgets.Label;
import org.junit.Before;
import org.junit.Test;


public class LabelOperationHandler_Test {

  private Label label;
  private IControlAdapter controlAdapter;
  private LabelOperationHandler handler;

  @Before
  public void setUp() {
    label = mock( Label.class );
    controlAdapter = mock( IControlAdapter.class );
    when( label.getAdapter( IControlAdapter.class ) ).thenReturn( controlAdapter );
    handler = new LabelOperationHandler( label );
  }

  @Test
  public void testHandleSetText() {
    handler.handleSet( new JsonObject().add( "text", "foo" ) );

    verify( label ).setText( "foo" );
  }

  @Test
  public void testHandleSetVisibility() {
    handler.handleSet( new JsonObject().add( "visibility", false ) );

    verify( controlAdapter ).setVisible( false );
  }

}
