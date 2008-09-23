/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.events;

import junit.framework.TestCase;

import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

public class TreeEvent_Test extends TestCase {

  private static final String TREE_EXPANDED = "treeExpanded";
  private static final String TREE_COLLAPSED = "treeCollapsed";
  private String log = "";

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }

  public void testAddRemoveClosedListener() {
    TreeListener listener = new TreeListener() {

      public void treeCollapsed( final TreeEvent e ) {
        log += TREE_COLLAPSED;
      }

      public void treeExpanded( final TreeEvent e ) {
        log += TREE_EXPANDED;
      }
    };
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Tree tree = new Tree( shell, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    tree.addTreeListener( listener );
    log = "";
    TreeEvent event = new TreeEvent( tree, item, TreeEvent.TREE_COLLAPSED );
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    event.processEvent();
    assertEquals( TREE_COLLAPSED, log );
    log = "";
    event = new TreeEvent( tree, item, TreeEvent.TREE_EXPANDED );
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    event.processEvent();
    assertEquals( TREE_EXPANDED, log );
  }
}
