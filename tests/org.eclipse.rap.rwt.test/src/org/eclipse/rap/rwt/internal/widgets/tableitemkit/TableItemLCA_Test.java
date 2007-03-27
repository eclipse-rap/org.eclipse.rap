/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.rwt.internal.widgets.tableitemkit;

import junit.framework.TestCase;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.RWTFixture;
import org.eclipse.rap.rwt.widgets.*;


public class TableItemLCA_Test extends TestCase {

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
  
  public void testContentChanged() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, RWT.NONE );
    TableColumn column0 = new TableColumn( table, RWT.NONE );
    TableItem item = new TableItem( table, RWT.NONE );
    
    // A non-initialized item returns true for itemContentChanged
    assertTrue( TableItemLCA.itemContentChanged( item ) );
    
    RWTFixture.markInitialized( item );
    TableItemLCA tableItemLCA = new TableItemLCA();
    // Adding a column -> itemContentChanged == true 
    tableItemLCA.preserveValues( item );
    new TableColumn( table, RWT.NONE );
    assertTrue( TableItemLCA.itemContentChanged( item ) );
    // Changing a text -> itemContentChanged == true 
    tableItemLCA.preserveValues( item );
    item.setText( 1, "def" );
    assertTrue( TableItemLCA.itemContentChanged( item ) );
    // Changing a text -> itemContentChanged == true 
    tableItemLCA.preserveValues( item );
    column0.setWidth( column0.getWidth() + 2 );
    assertTrue( TableItemLCA.itemContentChanged( item ) );
    // Changing nothing -> itemContentChanged == false 
    tableItemLCA.preserveValues( item );
    assertTrue( TableItemLCA.itemContentChanged( item ) );
  }
}
