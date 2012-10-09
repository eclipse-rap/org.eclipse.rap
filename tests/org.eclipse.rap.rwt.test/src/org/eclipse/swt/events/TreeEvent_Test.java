/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.events;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

public class TreeEvent_Test extends TestCase {

  private static final String TREE_EXPANDED = "treeExpanded";
  private static final String TREE_COLLAPSED = "treeCollapsed";
  
  private String log = "";
  private Display display;

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testAddRemoveListener() {
    TreeListener listener = new TreeListener() {
      public void treeCollapsed( TreeEvent e ) {
        log += TREE_COLLAPSED;
      }
      public void treeExpanded( TreeEvent e ) {
        log += TREE_EXPANDED;
      }
    };
    Shell shell = new Shell( display, SWT.NONE );
    Tree tree = new Tree( shell, SWT.NONE );
    tree.addTreeListener( listener );
    log = "";
    tree.notifyListeners( SWT.Collapse, new Event() );
    assertEquals( TREE_COLLAPSED, log );

    log = "";
    tree.notifyListeners( SWT.Expand, new Event() );
    assertEquals( TREE_EXPANDED, log );
  }
}
