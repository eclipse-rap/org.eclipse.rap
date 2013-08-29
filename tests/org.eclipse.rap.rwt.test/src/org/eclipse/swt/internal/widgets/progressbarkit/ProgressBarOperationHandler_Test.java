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
package org.eclipse.swt.internal.widgets.progressbarkit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.swt.widgets.ProgressBar;
import org.junit.Before;
import org.junit.Test;


public class ProgressBarOperationHandler_Test {

  private ProgressBar progressBar;
  private ProgressBarOperationHandler handler;

  @Before
  public void setUp() {
    progressBar = mock( ProgressBar.class );
    handler = new ProgressBarOperationHandler( progressBar );
  }

  @Test
  public void testHandleSetText() {
    handler.handleSet( new JsonObject().add( "selection", 10 ) );

    verify( progressBar ).setSelection( 10 );
  }

  @Test
  public void testHandleSetVisibility() {
    handler.handleSet( new JsonObject().add( "visibility", false ) );

    verify( progressBar ).setVisible( false );
  }

}
