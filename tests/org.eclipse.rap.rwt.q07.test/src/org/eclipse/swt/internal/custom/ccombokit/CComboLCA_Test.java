/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     EclipseSource - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.custom.ccombokit;

import java.io.IOException;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.*;
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
    Fixture.markInitialized( display );
    // Test preserving a CCombo with no items and (naturally) no selection
    Fixture.preserveWidgets();
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
    Fixture.clearPreserved();
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
    Fixture.preserveWidgets();

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
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( ccombo );
    hasListeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    ccombo.addControlListener( new ControlListener (){

      public void controlMoved( final ControlEvent e ) {
      }

      public void controlResized( final ControlEvent e ) {
      }});
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( ccombo );
    hasListeners = ( Boolean ) adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    Fixture.clearPreserved();
    //foreground background font
    Color background = Graphics.getColor( 122, 33, 203 );
    ccombo.setBackground( background );
    Color foreground = Graphics.getColor( 211, 178, 211 );
    ccombo.setForeground( foreground );
    Font font = Graphics.getFont( "font", 12, SWT.BOLD );
    ccombo.setFont( font );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( ccombo );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    Fixture.clearPreserved();
    //tooltiptext
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( ccombo );
    assertEquals( null, ccombo.getToolTipText() );
    Fixture.clearPreserved();
    ccombo.setToolTipText( "some text" );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( ccombo );
    assertEquals( "some text", ccombo.getToolTipText() );
    Fixture.clearPreserved();
    //tab_index
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( ccombo );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    Fixture.clearPreserved();
    //activateListener
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( ccombo );
    hasListeners = (Boolean)adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    ActivateEvent.addListener( ccombo, new ActivateAdapter() {
    } );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( ccombo );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
  }

  public void testEditablePreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    CCombo ccombo = new CCombo( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( ccombo );
    assertEquals( Boolean.TRUE , adapter.getPreserved( CComboLCA.PROP_EDITABLE ) );
    // activateListeners, focusListeners
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( ccombo );
    Boolean focusListener
     = ( Boolean )adapter.getPreserved( Props.FOCUS_LISTENER );
    assertEquals( Boolean.FALSE, focusListener );
    Fixture.clearPreserved();
    ccombo.addFocusListener( new FocusListener (){
      public void focusGained( final FocusEvent event ) {
      }

      public void focusLost( final FocusEvent event ) {
      }} );
    Fixture.preserveWidgets();
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
    Fixture.markInitialized( display );
    Fixture.markInitialized( ccombo );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    CComboLCA ccomboLCA = new CComboLCA();
    ccombo.add( "item 1" );
    ccombo.add( "item 2" );
    ccomboLCA.renderChanges( ccombo );
    String expected;
    expected = "w.setItems( [ \"item 1\", \"item 2\" ] );";
    assertTrue( Fixture.getAllMarkup().endsWith( expected ) );
    Fixture.fakeResponseWriter();
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    ccombo.select( 1 );
    ccomboLCA.renderChanges( ccombo );
    expected = "w.select( 1 );";
    assertTrue( Fixture.getAllMarkup().endsWith( expected ) );
    Fixture.fakeResponseWriter();
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
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
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Fixture.fakeRequestParam( ccomboId + ".selectedItem", "0" );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, ccomboId );
    WidgetUtil.getLCA( ccombo ).readData( ccombo );
    assertEquals( 0, ccombo.getSelectionIndex() );
    assertEquals( "widgetSelected", log.toString() );
    // read changed text
    Fixture.fakeRequestParam( ccomboId + ".text", "abc" );
    WidgetUtil.getLCA( ccombo ).readData( ccombo );
    assertEquals( "abc", ccombo.getText() );
    // read changed selection
    Fixture.fakeRequestParam( ccomboId + ".text", "abc" );
    Fixture.fakeRequestParam( ccomboId + ".selectionStart", "1" );
    Fixture.fakeRequestParam( ccomboId + ".selectionLength", "1" );
    WidgetUtil.getLCA( ccombo ).readData( ccombo );
    assertEquals( new Point( 1, 2 ), ccombo.getSelection() );
  }

  public void testReadText() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    final CCombo ccombo = new CCombo( shell, SWT.NONE );
    shell.open();
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.markInitialized( ccombo );
    // test without verify listener
    Fixture.fakeNewRequest( display );
    String textId = WidgetUtil.getId( ccombo );
    Fixture.fakeRequestParam( textId + ".text", "some text" );
    Fixture.executeLifeCycleFromServerThread();
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
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( textId + ".text", "verify me" );
    Fixture.executeLifeCycleFromServerThread();
    // ensure that no text and selection values are sent back to the client
    markup = Fixture.getAllMarkup();
    assertEquals( -1, markup.indexOf( "w.setValue(" ) );
    assertEquals( "verify me", ccombo.getText() );
    assertEquals( "verify me", log.toString() );
  }

  public void testTextSelectionWithVerifyEvent() {
    final java.util.List<VerifyEvent> log = new ArrayList<VerifyEvent>();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    final CCombo ccombo = new CCombo( shell, SWT.NONE );
    shell.open();
    String ccomboId = WidgetUtil.getId( ccombo );
    // ensure that selection is unchanged in case a verify-listener is
    // registered that does not change the text
    VerifyListener emptyVerifyListener = new VerifyListener() {
      public void verifyText( final VerifyEvent event ) {
        log.add( event );
      }
    };
    ccombo.addVerifyListener( emptyVerifyListener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.markInitialized( ccombo );
    log.clear();
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( ccomboId + ".text", "verify me" );
    Fixture.fakeRequestParam( ccomboId + ".selectionStart", "1" );
    Fixture.fakeRequestParam( ccomboId + ".selectionLength", "0" );
    Fixture.fakeRequestParam( JSConst.EVENT_MODIFY_TEXT, ccomboId );
    Fixture.executeLifeCycleFromServerThread();
    // ensure that an empty verify listener does not lead to sending the
    // original text and selection values back to the client
    String markup = Fixture.getAllMarkup();
    assertEquals( -1, markup.indexOf( "w.setValue(" ) );
    assertEquals( -1, markup.indexOf( ".setSelection( w," ) );
    assertEquals( 1, log.size() );
    assertEquals( new Point( 1, 1 ), ccombo.getSelection() );
    assertEquals( "verify me", ccombo.getText() );
    ccombo.removeVerifyListener( emptyVerifyListener );
    // ensure that selection is unchanged in case a verify-listener changes
    // the incoming text within the limits of the selection
    ccombo.setText( "" );
    VerifyListener alteringVerifyListener = new VerifyListener() {
      public void verifyText( final VerifyEvent event ) {
        log.add( event );
        event.text = "verified";
      }
    };
    ccombo.addVerifyListener( alteringVerifyListener );
    log.clear();
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( ccomboId + ".text", "verify me" );
    Fixture.fakeRequestParam( ccomboId + ".selectionStart", "1" );
    Fixture.fakeRequestParam( ccomboId + ".selectionLength", "0" );
    Fixture.fakeRequestParam( JSConst.EVENT_MODIFY_TEXT, ccomboId );
    Fixture.executeLifeCycleFromServerThread( );
    assertEquals( 1, log.size() );
    assertEquals( new Point( 1, 1 ), ccombo.getSelection() );
    assertEquals( "verified", ccombo.getText() );
    ccombo.removeVerifyListener( alteringVerifyListener );
    // ensure that selection is adjusted in case a verify-listener changes
    // the incoming text in a way that would result in an invalid selection
    ccombo.setText( "" );
    alteringVerifyListener = new VerifyListener() {
      public void verifyText( final VerifyEvent event ) {
        log.add( event );
        event.text = "";
      }
    };
    ccombo.addVerifyListener( alteringVerifyListener );
    log.clear();
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( ccomboId + ".text", "verify me" );
    Fixture.fakeRequestParam( ccomboId + ".selectionStart", "1" );
    Fixture.fakeRequestParam( ccomboId + ".selectionLength", "0" );
    Fixture.fakeRequestParam( JSConst.EVENT_MODIFY_TEXT, ccomboId );
    Fixture.executeLifeCycleFromServerThread( );
    assertEquals( 1, log.size() );
    assertEquals( new Point( 0, 0 ), ccombo.getSelection() );
    assertEquals( "", ccombo.getText() );
    ccombo.removeVerifyListener( alteringVerifyListener );
  }

  public void testTextLimit() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    final CCombo ccombo = new CCombo( shell, SWT.BORDER );
    CComboLCA lca = new CComboLCA();
    // run LCA one to dump the here uninteresting prolog
    Fixture.fakeResponseWriter();
    lca.renderChanges( ccombo );
    // Initially no textLimit must be rendered if the initial value is untouched
    Fixture.fakeResponseWriter();
    lca.renderChanges( ccombo );
    assertEquals( -1, Fixture.getAllMarkup().indexOf( "setTextLimit" ) );
    // Positive textLimit is written as setMaxLength( ... )
    Fixture.fakeResponseWriter();
    Fixture.markInitialized( ccombo );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    ccombo.setTextLimit( 12 );
    lca.renderChanges( ccombo );
    String expected = "setTextLimit( 12 );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
    // textLimit = CCombo.LIMIT is tread as 'no limit'
    Fixture.fakeResponseWriter();
    Fixture.markInitialized( ccombo );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    ccombo.setTextLimit( CCombo.LIMIT );
    lca.renderChanges( ccombo );
    expected = "setTextLimit( null );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
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

    // Execute life cycle once to simulate startup request
    Fixture.fakeNewRequest( display );
    Fixture.executeLifeCycleFromServerThread();

    // Simulate button click that executes widgetSelected
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId );
    Fixture.executeLifeCycleFromServerThread();
    String expected = "w.select( 0 )";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
