/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.custom;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;


public class TreeEditor_Test extends TestCase {

  private Display display;
  private Shell shell;

  public void testIsSerializable() throws Exception {
    String itemText = "item0";
    Tree tree = new Tree( shell, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );
    new TreeColumn( tree, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    item.setText( itemText );
    TreeEditor treeEditor = new TreeEditor( tree );
    treeEditor.setColumn( 1 );
    treeEditor.setItem( item );
    
    TreeEditor deserializedTreeEditor = Fixture.serializeAndDeserialize( treeEditor );
    
    attachThread( deserializedTreeEditor.getItem().getDisplay() );
    assertEquals( 1, deserializedTreeEditor.getColumn() );
    assertEquals( itemText, deserializedTreeEditor.getItem().getText() );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  private static void attachThread( Display display ) {
    IDisplayAdapter adapter = display.getAdapter( IDisplayAdapter.class );
    adapter.attachThread();
  }
}
