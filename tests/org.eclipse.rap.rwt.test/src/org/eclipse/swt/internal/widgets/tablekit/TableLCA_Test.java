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

package org.eclipse.swt.internal.widgets.tablekit;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.lifecycle.RWTLifeCycle;
import org.eclipse.swt.internal.widgets.ItemHolder;
import org.eclipse.swt.lifecycle.*;
import org.eclipse.swt.widgets.*;

import com.w4t.Fixture;
import com.w4t.engine.requests.RequestParams;


public class TableLCA_Test extends TestCase {

  public void testSetDataEvent() throws IOException {
    final StringBuffer log = new StringBuffer();
    Display display = new Display();
    Shell shell = new Shell( display );
    final Table table = new Table( shell, SWT.VIRTUAL );
    table.setItemCount( 10 );
    table.addListener( SWT.SetData, new Listener() {
      public void handleEvent( final Event event ) {
        assertSame( table.getItem( 1 ), event.item );
        assertEquals( 1, event.index );
        log.append( "SetDataEvent" );
      }
    } );
    String displayId = DisplayUtil.getId( display );
    String tableId = WidgetUtil.getId( table );
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_SET_DATA, tableId );
    Fixture.fakeRequestParam( JSConst.EVENT_SET_DATA_INDEX, "1" );
    RWTLifeCycle lifeCycle = new RWTLifeCycle();
    lifeCycle.execute();
    
    assertEquals( 1, ItemHolder.getItems( table ).length );
    assertEquals( "SetDataEvent", log.toString() );
    String tableItemCtor = "org.eclipse.swt.widgets.TableItem";
    assertTrue( Fixture.getAllMarkup().indexOf( tableItemCtor ) != -1 );
  }
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
