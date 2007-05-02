/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.combokit;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.lifecycle.*;
import org.eclipse.swt.widgets.*;

import com.w4t.Fixture;
import com.w4t.engine.lifecycle.PhaseId;

public class ComboLCA_Test extends TestCase {

  private static final String PROP_ITEMS = "items";
  private static final String PROP_SELECTION = "selection";
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
  
  public void testPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Combo combo = new Combo( shell, SWT.DEFAULT );
    RWTFixture.markInitialized( display );
    
    // Test preserving a combo with no items and (naturally) no selection  
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( combo );
    String[] items = ( ( String[] )adapter.getPreserved( PROP_ITEMS ) );
    assertEquals( 0, items.length );
    assertEquals( new Integer( -1 ), adapter.getPreserved( PROP_SELECTION ) );
    Boolean hasListeners;
    hasListeners = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    
    // Test preserving combo with items were one is selected
    RWTFixture.clearPreserved();
    combo.add( "item 1" );
    combo.add( "item 2" );
    combo.select( 1 );
    SelectionListener selectionListener = new SelectionAdapter() {
    };
    combo.addSelectionListener( selectionListener );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( combo );
    items = ( ( String[] )adapter.getPreserved( PROP_ITEMS ) );
    assertEquals( 2, items.length );
    assertEquals( "item 1", items[ 0 ] );
    assertEquals( "item 2", items[ 1 ] );
    assertEquals( new Integer( 1 ), adapter.getPreserved( PROP_SELECTION ) );
    hasListeners = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
  }

  public void testRenderChanges() throws IOException {
    Fixture.fakeResponseWriter();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Combo combo = new Combo( shell, SWT.DEFAULT );
    shell.open();
    RWTFixture.markInitialized( display );
    RWTFixture.markInitialized( combo );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    ComboLCA comboLCA = new ComboLCA();
    combo.add( "item 1" );
    combo.add( "item 2" );
    comboLCA.renderChanges( combo );
    String expected;
    expected 
      = "ComboUtil.createComboBoxItems( \""
      + WidgetUtil.getId( combo )
      + "\", [ \"item 1\", \"item 2\" ] );";
    assertTrue( Fixture.getAllMarkup().endsWith( expected ) );
    Fixture.fakeResponseWriter();
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    combo.select( 1 );
    comboLCA.renderChanges( combo );
    expected 
      = "ComboUtil.selectComboBoxItem( \""
      + WidgetUtil.getId( combo )
      + "\", 1 );";
    assertTrue( Fixture.getAllMarkup().endsWith( expected ) );
    Fixture.fakeResponseWriter();
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    comboLCA.renderChanges( combo );
    assertEquals( "", Fixture.getAllMarkup() );
  }

  public void testReadData() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    final Combo combo = new Combo( shell, SWT.NONE );
    String comboId = WidgetUtil.getId( combo );
    // init combo items
    combo.add( "item 1" );
    combo.add( "item 2" );
    // read changed selection
    Fixture.fakeRequestParam( comboId + ".selectedItem", "1" );
    WidgetUtil.getLCA( combo ).readData( combo );
    assertEquals( 1, combo.getSelectionIndex() );
    // read changed selection and ensure that SelectionListener gets called
    final StringBuffer log = new StringBuffer();
    combo.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        assertSame( combo, event.getSource() );
        assertEquals( 0, event.detail );
        assertEquals( null, event.item );
        assertEquals( true, event.doit );
        log.append( "widgetSelected" );
      }
    } );
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    Fixture.fakeRequestParam( comboId + ".selectedItem", "0" );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, comboId );
    WidgetUtil.getLCA( combo ).readData( combo );
    assertEquals( 0, combo.getSelectionIndex() );
    assertEquals( "widgetSelected", log.toString() );
  }
}