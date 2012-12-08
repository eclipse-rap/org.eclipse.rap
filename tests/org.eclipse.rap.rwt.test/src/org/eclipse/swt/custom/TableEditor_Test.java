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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;


public class TableEditor_Test extends TestCase {

  private Display display;
  private Shell shell;

  public void testIsSerializable() throws Exception {
    String itemText = "item0";
    Table table = new Table( shell, SWT.NONE );
    new TableColumn( table, SWT.NONE );
    new TableColumn( table, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );
    item.setText( itemText );
    TableEditor tableEditor = new TableEditor( table );
    tableEditor.setColumn( 1 );
    tableEditor.setItem( item );
    
    TableEditor deserializedTableEditor = Fixture.serializeAndDeserialize( tableEditor );
    
    attachThread( deserializedTableEditor.getItem().getDisplay() );
    assertEquals( 1, deserializedTableEditor.getColumn() );
    assertEquals( itemText, deserializedTableEditor.getItem().getText() );
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
