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
package org.eclipse.swt.internal.widgets.tableitemkit;

import static org.junit.Assert.assertTrue;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TableItemOperationHandler_Test {

  private TableItem item;
  private TableItemOperationHandler handler;

  @Before
  public void setUp() {
    Fixture.setUp();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Table table = new Table( shell, SWT.CHECK );
    item = new TableItem( table, SWT.NONE );
    handler = new TableItemOperationHandler( item );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testHandleSetChecked() {
    handler.handleSet( new JsonObject().add( "checked", true ) );

    assertTrue( item.getChecked() );
  }

}
