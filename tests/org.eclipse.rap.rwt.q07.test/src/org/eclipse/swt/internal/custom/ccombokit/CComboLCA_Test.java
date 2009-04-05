/*******************************************************************************
 * Copyright (c) 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.custom.ccombokit;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.events.ActivateAdapter;
import org.eclipse.swt.internal.events.ActivateEvent;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;

public class CComboLCA_Test extends TestCase {

  private static final String PROP_ITEMS = "items";
  private static final String PROP_SELECTION = "selection";

  public void testPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    CCombo ccombo = new CCombo( shell, SWT.DEFAULT );
    RWTFixture.markInitialized( display );
    // Test preserving a CCombo with no items and (naturally) no selection
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( ccombo );
    String[] items = ( ( String[] )adapter.getPreserved( PROP_ITEMS ) );
    assertEquals( 0, items.length );
    assertEquals( new Integer( -1 ), adapter.getPreserved( PROP_SELECTION ) );
    Object height = adapter.getPreserved( CComboLCA.PROP_MAX_LIST_HEIGHT );
    assertEquals( new Integer( CComboLCA.getMaxListHeight( ccombo ) ), height );
    assertEquals( new Integer( Text.LIMIT ), 
                  adapter.getPreserved( CComboLCA.PROP_TEXT_LIMIT ) );
    assertEquals( new Point( 0, 0 ), 
                  adapter.getPreserved( CComboLCA.PROP_TEXT_SELECTION ) );
    assertEquals( Boolean.FALSE, 
                  adapter.getPreserved( CComboLCA.PROP_LIST_VISIBLE ) );
    Boolean hasListeners;
    hasListeners = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( Boolean.FALSE, 
                  adapter.getPreserved( CComboLCA.PROP_EDITABLE ) );
    assertEquals( Boolean.FALSE, hasListeners );
    // Test preserving CCombo with items, where one is selected
    RWTFixture.clearPreserved();
    ccombo.add( "item 1" );
    ccombo.add( "item 2" );
    ccombo.select( 1 );
    ccombo.setTextLimit( 10 );
    ccombo.setListVisible( true );
    SelectionListener selectionListener = new SelectionAdapter() {
    };
    ccombo.addSelectionListener( selectionListener );
    ccombo.addModifyListener( new ModifyListener(){

      public void modifyText( final ModifyEvent event ) {
      }} );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( ccombo );
    items = ( ( String[] )adapter.getPreserved( PROP_ITEMS ) );
    assertEquals( 2, items.length );
    assertEquals( "item 1", items[ 0 ] );
    assertEquals( "item 2", items[ 1 ] );
    assertEquals( new Integer( 1 ), adapter.getPreserved( PROP_SELECTION ) );
    height = adapter.getPreserved( CComboLCA.PROP_MAX_LIST_HEIGHT );
    assertEquals( new Integer( CComboLCA.getMaxListHeight( ccombo ) ), height );
    assertEquals( "item 2", adapter.getPreserved( Props.TEXT ) );
    assertEquals( new Integer( 10 ), 
                  adapter.getPreserved( CComboLCA.PROP_TEXT_LIMIT ) );
    assertEquals( Boolean.TRUE, 
                  adapter.getPreserved( CComboLCA.PROP_LIST_VISIBLE ) );
    hasListeners = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    assertEquals( Boolean.FALSE, adapter.getPreserved( CComboLCA.PROP_EDITABLE ) );
    hasListeners
     = ( Boolean )adapter.getPreserved( CComboLCA.PROP_VERIFY_MODIFY_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    //control_listeners
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( ccombo );
    hasListeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    RWTFixture.clearPreserved();
    ccombo.addControlListener( new ControlListener (){

      public void controlMoved( final ControlEvent e ) {
      }

      public void controlResized( final ControlEvent e ) {
      }});
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( ccombo );
    hasListeners = ( Boolean ) adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    RWTFixture.clearPreserved();
    //foreground background font
    Color background = Graphics.getColor( 122, 33, 203 );
    ccombo.setBackground( background );
    Color foreground = Graphics.getColor( 211, 178, 211 );
    ccombo.setForeground( foreground );
    Font font = Graphics.getFont( "font", 12, SWT.BOLD );
    ccombo.setFont( font );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( ccombo );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    RWTFixture.clearPreserved();
    //tooltiptext
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( ccombo );
    assertEquals( null, ccombo.getToolTipText() );
    RWTFixture.clearPreserved();
    ccombo.setToolTipText( "some text" );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( ccombo );
    assertEquals( "some text", ccombo.getToolTipText() );
    RWTFixture.clearPreserved();
    //tab_index
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( ccombo );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    RWTFixture.clearPreserved();
    //activateListener
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( ccombo );
    hasListeners = (Boolean)adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    RWTFixture.clearPreserved();
    ActivateEvent.addListener( ccombo, new ActivateAdapter() {
    } );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( ccombo );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    RWTFixture.clearPreserved();
    display.dispose();
  }

  public void testEditablePreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    CCombo ccombo = new CCombo( shell, SWT.NONE );
    RWTFixture.markInitialized( display );
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( ccombo );
    assertEquals( Boolean.TRUE , adapter.getPreserved( CComboLCA.PROP_EDITABLE ) );
    // activateListeners, focusListeners
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( ccombo );
    Boolean focusListener
     = ( Boolean )adapter.getPreserved( Props.FOCUS_LISTENER );
    assertEquals( Boolean.FALSE, focusListener );
    RWTFixture.clearPreserved();
    ccombo.addFocusListener( new FocusListener (){
      public void focusGained( final FocusEvent event ) {
      }

      public void focusLost( final FocusEvent event ) {
      }} );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( ccombo );
    Boolean hasListeners
       = ( Boolean )adapter.getPreserved( Props.FOCUS_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
  }

  public void testRenderChanges() throws IOException {
    Fixture.fakeResponseWriter();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    CCombo ccombo = new CCombo( shell, SWT.READ_ONLY );
    shell.open();
    RWTFixture.markInitialized( display );
    RWTFixture.markInitialized( ccombo );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    CComboLCA ccomboLCA = new CComboLCA();
    ccombo.add( "item 1" );
    ccombo.add( "item 2" );
    ccomboLCA.renderChanges( ccombo );
    String expected;
    expected = "w.setItems( [ \"item 1\", \"item 2\" ] );";
    assertTrue( Fixture.getAllMarkup().endsWith( expected ) );
    Fixture.fakeResponseWriter();
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    ccombo.select( 1 );
    ccomboLCA.renderChanges( ccombo );
    expected = "w.select( 1 );";
    assertTrue( Fixture.getAllMarkup().endsWith( expected ) );
    Fixture.fakeResponseWriter();
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    ccomboLCA.renderChanges( ccombo );
    assertEquals( "", Fixture.getAllMarkup() );
  }

  public void testReadData() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    final CCombo ccombo = new CCombo( shell, SWT.NONE );
    String ccomboId = WidgetUtil.getId( ccombo );
    // init CCombo items
    ccombo.add( "item 1" );
    ccombo.add( "item 2" );
    // read list visibility
    Fixture.fakeRequestParam( ccomboId + ".listVisible", "true" );
    WidgetUtil.getLCA( ccombo ).readData( ccombo );
    assertEquals( true, ccombo.getListVisible() );
    // read changed selection
    Fixture.fakeRequestParam( ccomboId + ".selectedItem", "1" );
    WidgetUtil.getLCA( ccombo ).readData( ccombo );
    assertEquals( 1, ccombo.getSelectionIndex() );
    // read changed selection and ensure that SelectionListener gets called
    final StringBuffer log = new StringBuffer();
    ccombo.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        assertSame( ccombo, event.getSource() );
        assertEquals( 0, event.detail );
        assertEquals( null, event.item );
        assertEquals( true, event.doit );
        log.append( "widgetSelected" );
      }
    } );
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    Fixture.fakeRequestParam( ccomboId + ".selectedItem", "0" );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, ccomboId );
    WidgetUtil.getLCA( ccombo ).readData( ccombo );
    assertEquals( 0, ccombo.getSelectionIndex() );
    assertEquals( "widgetSelected", log.toString() );
  }
  
  public void testReadText() {
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    lifeCycle.addPhaseListener( new PreserveWidgetsPhaseListener() );
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    final CCombo ccombo = new CCombo( shell, SWT.BORDER );
    shell.open();
    RWTFixture.markInitialized( display );
    RWTFixture.markInitialized( shell );
    RWTFixture.markInitialized( ccombo );
    // test without verify listener
    RWTFixture.fakeNewRequest();
    String textId = WidgetUtil.getId( ccombo );
    String displayId = DisplayUtil.getId( display );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( textId + ".text", "some text" );
    RWTFixture.executeLifeCycleFromServerThread();
    // ensure that no text and selection values are sent back to the client
    String markup = Fixture.getAllMarkup();
    assertEquals( -1, markup.indexOf( "w.setValue(" ) );
    assertEquals( "some text", ccombo.getText() );
    // test with verify listener
    final StringBuffer log = new StringBuffer();
    ccombo.addVerifyListener( new VerifyListener() {
  
      public void verifyText( VerifyEvent event ) {
        assertEquals( ccombo, event.widget );
        assertEquals( "verify me", event.text );
        assertEquals( 0, event.start );
        assertEquals( 9, event.end );
        log.append( event.text );
      }
    } );
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( textId + ".text", "verify me" );
    RWTFixture.executeLifeCycleFromServerThread();
    // ensure that no text and selection values are sent back to the client
    markup = Fixture.getAllMarkup();
    assertEquals( -1, markup.indexOf( "w.setValue(" ) );
    assertEquals( "verify me", ccombo.getText() );
    assertEquals( "verify me", log.toString() );
  }

  public void testSelectionAfterRemoveAll() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    final CCombo ccombo = new CCombo( shell, SWT.READ_ONLY );
    ccombo.add( "item 1" );
    ccombo.select( 0 );
    Button button = new Button( shell, SWT.PUSH );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        ccombo.removeAll();
        ccombo.add( "replacement for item 1" );
        ccombo.select( 0 );
      }
    } );

    String buttonId = WidgetUtil.getId( button );
    String displayId = DisplayUtil.getId( display );
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    lifeCycle.addPhaseListener( new PreserveWidgetsPhaseListener() );

    // Execute life cycle once to simulate startup request
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    RWTFixture.executeLifeCycleFromServerThread();
    
    // Simulate button click that executes widgetSelected 
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId );
    RWTFixture.executeLifeCycleFromServerThread();
    String expected = "w.select( 0 )";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
