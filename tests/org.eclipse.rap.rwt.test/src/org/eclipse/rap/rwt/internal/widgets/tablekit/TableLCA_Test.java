/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.rwt.internal.widgets.tablekit;

import junit.framework.TestCase;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.RWTFixture;
import org.eclipse.rap.rwt.widgets.*;
import com.w4t.Fixture;
import com.w4t.ParamCheck;

public class TableLCA_Test extends TestCase {

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }

  public void testTableContentCreation() {
    // TODO: [fappel] test content creation without columns
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    Table table = new Table( shell, RWT.NONE );
    TableColumn column0 = new TableColumn( table, RWT.NONE );
    column0.setText( "Column0" );
    TableColumn column1 = new TableColumn( table, RWT.NONE );
    column1.setText( "Column1" );
    TableItem tableItem0 = new TableItem( table, RWT.NONE );
    String[] texts = new String[] { "0-0", "0-1" };
    for( int i = 0; i < texts.length; i++ ) {
      tableItem0.setText( i, texts[ i ] );
    }
    TableItem tableItem1 = new TableItem( table, RWT.NONE );
    String[] texts1 = new String[] { "1-0", "1-1" };
    ParamCheck.notNull( texts1, "texts" );
    for( int i = 0; i < texts1.length; i++ ) {
      tableItem1.setText( i, texts1[ i ] );
    }
    Fixture.fakeResponseWriter();
    TableLCA.createTableContent( table );
    String allMarkup = Fixture.getAllMarkup();
    String expected =   "var tableData=[];"
                      + "tableData.push([\"0-0\",\"0-1\"]);"
                      + "tableData.push([\"1-0\",\"1-1\"]);";
    assertEquals( expected, allMarkup );
  }

  public void testTableColumnCreation() {
    // TODO: [fappel] table without header
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    Table table = new Table( shell, RWT.NONE );
    TableColumn column0 = new TableColumn( table, RWT.NONE );
    column0.setText( "Column0" );
    column0.setWidth( 10 );
    TableColumn column1 = new TableColumn( table, RWT.NONE );
    column1.setText( "Column1" );
    column1.setWidth( 20 );
    Fixture.fakeResponseWriter();
    TableLCA.createTableColumns( table );
    String allMarkup = Fixture.getAllMarkup();
    String expected = "var tableColumns=[\"Column0\",\"Column1\"];";
    assertEquals( expected, allMarkup );
  }
}
