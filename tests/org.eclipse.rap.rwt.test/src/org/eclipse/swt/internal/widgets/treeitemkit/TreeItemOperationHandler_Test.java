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
package org.eclipse.swt.internal.widgets.treeitemkit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TreeItemOperationHandler_Test {

  private TreeItem item;
  private TreeItemOperationHandler handler;

  @Before
  public void setUp() {
    Fixture.setUp();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Tree tree = new Tree( shell, SWT.CHECK );
    tree.setBounds( 0, 0, 100, 100 );
    item = new TreeItem( tree, SWT.NONE );
    new TreeItem( item, SWT.NONE );
    handler = new TreeItemOperationHandler( item );
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

  @Test
  public void testHandleSetЕxpanded_expand() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );

    handler.handleSet( new JsonObject().add( "expanded", true ) );

    assertTrue( item.getExpanded() );
  }

  @Test
  public void testHandleSetЕxpanded_collaps() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    item.setExpanded( true );

    handler.handleSet( new JsonObject().add( "expanded", false ) );

    assertFalse( item.getExpanded() );
  }

}
