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

package org.eclipse.swt.internal.widgets.textkit;

import java.io.IOException;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;

public class TextLCA_Test extends TestCase {

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }

  public void testMultiPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Text text = new Text( shell, SWT.MULTI );
    testPreserveValues( display, text );
    display.dispose();
  }

  public void testPasswordPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Text text = new Text( shell, SWT.PASSWORD );
    testPreserveValues( display, text );
    display.dispose();
  }

  public void testSinglePreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Text text = new Text( shell, SWT.SINGLE );
    testPreserveValues( display, text );
    //Selection_Listener
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( text );
    String propSelectionLsnr = TextLCAUtil.PROP_SELECTION_LISTENER;
    Boolean hasListeners = ( Boolean )adapter.getPreserved( propSelectionLsnr );
    assertEquals( Boolean.FALSE, hasListeners );
    RWTFixture.clearPreserved();
    SelectionListener selectionListener = new SelectionAdapter() {
    };
    text.addSelectionListener( selectionListener );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( text );
    hasListeners = ( Boolean )adapter.getPreserved( propSelectionLsnr );
    assertEquals( Boolean.TRUE, hasListeners );
    RWTFixture.clearPreserved();
    display.dispose();
  }

  public void testReadData() {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Text text = new Text( shell, SWT.NONE );
    String textId = WidgetUtil.getId( text );
    // read changed text
    Fixture.fakeRequestParam( textId + ".text", "abc" );
    WidgetUtil.getLCA( text ).readData( text );
    assertEquals( "abc", text.getText() );
    // read changed selection
    Fixture.fakeRequestParam( textId + ".text", "abc" );
    Fixture.fakeRequestParam( textId + ".selectionStart", "1" );
    Fixture.fakeRequestParam( textId + ".selectionLength", "1" );
    WidgetUtil.getLCA( text ).readData( text );
    assertEquals( new Point( 1, 2 ), text.getSelection() );
  }

  public void testRenderChanges() throws IOException {
    Fixture.fakeResponseWriter();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Text text = new Text( shell, SWT.NONE );
    shell.open();
    RWTFixture.markInitialized( display );
    RWTFixture.markInitialized( text );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    TextLCA textLCA = new TextLCA();
    text.setText( "hello" );
    textLCA.renderChanges( text );
    assertTrue( Fixture.getAllMarkup().endsWith( "setValue( \"hello\" );" ) );
    Fixture.fakeResponseWriter();
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    textLCA.renderChanges( text );
    assertEquals( "", Fixture.getAllMarkup() );
  }

  public void testModifyEvent() {
    final StringBuffer log = new StringBuffer();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    final Text text = new Text( shell, SWT.NONE );
    text.addModifyListener( new ModifyListener() {

      public void modifyText( final ModifyEvent event ) {
        assertEquals( text, event.getSource() );
        log.append( "modifyText" );
      }
    } );
    shell.open();
    String displayId = DisplayUtil.getId( display );
    String textId = WidgetUtil.getId( text );
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( textId + ".text", "new text" );
    Fixture.fakeRequestParam( JSConst.EVENT_MODIFY_TEXT, textId );
    RWTFixture.executeLifeCycleFromServerThread( );
    assertEquals( "modifyText", log.toString() );
  }

  public void testVerifyEvent() {
    final StringBuffer log = new StringBuffer();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    final Text text = new Text( shell, SWT.NONE );
    text.addVerifyListener( new VerifyListener() {
      public void verifyText( final VerifyEvent event ) {
        assertEquals( text, event.getSource() );
        assertEquals( text, event.widget );
        assertTrue( event.doit );
        log.append( "verifyText" );
      }
    } );
    shell.open();
    String displayId = DisplayUtil.getId( display );
    String textId = WidgetUtil.getId( text );
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( textId + ".text", "verify me" );
    Fixture.fakeRequestParam( JSConst.EVENT_MODIFY_TEXT, textId );
    RWTFixture.executeLifeCycleFromServerThread( );
    assertEquals( "verifyText", log.toString() );
  }
  
  public void testSelectionWithVerifyEvent() {
    final java.util.List log = new ArrayList();
    // register preserve-values phase-listener
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    lifeCycle.addPhaseListener( new PreserveWidgetsPhaseListener() );
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    final Text text = new Text( shell, SWT.NONE );
    shell.open();
    String displayId = DisplayUtil.getId( display );
    String textId = WidgetUtil.getId( text );
    // ensure that selection is unchanged in case a verify-listener is 
    // registered that does not change the text
    VerifyListener emptyVerifyListener = new VerifyListener() {
      public void verifyText( final VerifyEvent event ) {
        log.add( event );
      }
    };
    text.addVerifyListener( emptyVerifyListener );
    log.clear();
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( textId + ".text", "verify me" );
    Fixture.fakeRequestParam( textId + ".selectionStart", "1" );
    Fixture.fakeRequestParam( textId + ".selectionLength", "0" );
    Fixture.fakeRequestParam( JSConst.EVENT_MODIFY_TEXT, textId );
    RWTFixture.executeLifeCycleFromServerThread( );
    assertEquals( 1, log.size() );
    assertEquals( new Point( 1, 1 ), text.getSelection() );
    assertEquals( "verify me", text.getText() );
    text.removeVerifyListener( emptyVerifyListener );
    // ensure that selection is unchanged in case a verify-listener changes 
    // the incoming text within the limits of the selection
    text.setText( "" );
    VerifyListener alteringVerifyListener = new VerifyListener() {
      public void verifyText( final VerifyEvent event ) {
        log.add( event );
        event.text = "verified";
      }
    };
    text.addVerifyListener( alteringVerifyListener );
    log.clear();
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( textId + ".text", "verify me" );
    Fixture.fakeRequestParam( textId + ".selectionStart", "1" );
    Fixture.fakeRequestParam( textId + ".selectionLength", "0" );
    Fixture.fakeRequestParam( JSConst.EVENT_MODIFY_TEXT, textId );
    RWTFixture.executeLifeCycleFromServerThread( );
    assertEquals( 1, log.size() );
    assertEquals( new Point( 1, 1 ), text.getSelection() );
    assertEquals( "verified", text.getText() );
    text.removeVerifyListener( alteringVerifyListener );
    // ensure that selection is adjusted in case a verify-listener changes 
    // the incoming text in a way that would result in an invalid selection
    text.setText( "" );
    alteringVerifyListener = new VerifyListener() {
      public void verifyText( final VerifyEvent event ) {
        log.add( event );
        event.text = "";
      }
    };
    text.addVerifyListener( alteringVerifyListener );
    log.clear();
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( textId + ".text", "verify me" );
    Fixture.fakeRequestParam( textId + ".selectionStart", "1" );
    Fixture.fakeRequestParam( textId + ".selectionLength", "0" );
    Fixture.fakeRequestParam( JSConst.EVENT_MODIFY_TEXT, textId );
    RWTFixture.executeLifeCycleFromServerThread( );
    assertEquals( 1, log.size() );
    assertEquals( new Point( 0, 0 ), text.getSelection() );
    assertEquals( "", text.getText() );
    text.removeVerifyListener( alteringVerifyListener );
  }
  
  public void testVerifyAndModifyEvent() {
    final java.util.List log = new ArrayList();
    // register preserve-values phase-listener
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    lifeCycle.addPhaseListener( new PreserveWidgetsPhaseListener() );
    // set up widgets to be tested
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    final Text text = new Text( shell, SWT.NONE );
    shell.open();
    String displayId = DisplayUtil.getId( display );
    String textId = WidgetUtil.getId( text );
    // ensure that modify *and* verify event is fired
    text.setText( "" );
    text.addVerifyListener( new VerifyListener() {
      public void verifyText( final VerifyEvent event ) {
        log.add( event );
      }
    } );
    text.addModifyListener( new ModifyListener() {
      public void modifyText( final ModifyEvent event ) {
        log.add( event );
      }
    } );
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( textId + ".text", "verify me" );
    Fixture.fakeRequestParam( textId + ".selectionStart", "1" );
    Fixture.fakeRequestParam( textId + ".selectionLength", "0" );
    Fixture.fakeRequestParam( JSConst.EVENT_MODIFY_TEXT, textId );
    RWTFixture.executeLifeCycleFromServerThread( );
    assertEquals( 2, log.size() );
    assertTrue( log.get( 0 ) instanceof VerifyEvent );
    assertTrue( log.get( 1 ) instanceof ModifyEvent );
  }

  public void testTextLimit() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Text text = new Text( shell, SWT.NONE );
    TextLCA lca = new TextLCA();
    // run LCA one to dump the here uninteresting prolog
    Fixture.fakeResponseWriter();
    lca.renderChanges( text );
    // Initially no textLimit must be rendered if the initial value is untouched
    Fixture.fakeResponseWriter();
    lca.renderChanges( text );
    assertEquals( -1, Fixture.getAllMarkup().indexOf( "setMaxLength" ) );
    // Positive textLimit is written as setMaxLength( ... )
    Fixture.fakeResponseWriter();
    RWTFixture.markInitialized( text );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    text.setTextLimit( 12 );
    lca.renderChanges( text );
    String expected = "setMaxLength( 12 );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
    // Negative textLimit is tread as 'no limit'
    Fixture.fakeResponseWriter();
    RWTFixture.markInitialized( text );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    text.setTextLimit( -50 );
    lca.renderChanges( text );
    expected = "setMaxLength( null );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
  }

  private void testPreserveValues( final Display display, final Text text ) {
    Boolean hasListeners;
    //text
    text.setText( "some text" );
    RWTFixture.markInitialized( display );
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( text );
    assertEquals( text.getText(), adapter.getPreserved( Props.TEXT ) );
    RWTFixture.clearPreserved();
    //text-limit
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( text );
    Integer textLimit
     = ( Integer )( adapter.getPreserved( TextLCAUtil.PROP_TEXT_LIMIT ) );
    assertEquals( Integer.MAX_VALUE, textLimit.intValue() );
    text.setTextLimit( 30 );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( text );
    textLimit
     = ( Integer )( adapter.getPreserved( TextLCAUtil.PROP_TEXT_LIMIT ) );
    assertEquals( 30, textLimit.intValue() );
    RWTFixture.clearPreserved();
    //selection
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( text );
    Point point = new Point( 0, 0 );
    assertEquals( point, adapter.getPreserved( TextLCAUtil.PROP_SELECTION ) );
    RWTFixture.clearPreserved();
    point = new Point( 3, 6 );
    text.setSelection( point );
    text.getSelection();
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( text );
    assertEquals( point, adapter.getPreserved( TextLCAUtil.PROP_SELECTION ) );
    RWTFixture.clearPreserved();
    //readonly
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( text );
    Boolean readonly
     = ( Boolean )adapter.getPreserved( TextLCAUtil.PROP_READONLY );
    assertEquals( Boolean.FALSE, readonly );
    RWTFixture.clearPreserved();
    text.setEditable( false );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( text );
    readonly = ( Boolean )adapter.getPreserved( TextLCAUtil.PROP_READONLY );
    assertEquals( Boolean.TRUE, readonly );
    RWTFixture.clearPreserved();
    //verifymodify-Listeners
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( text );
    Boolean hasVerifyModifyListener
     = ( Boolean )adapter.getPreserved( TextLCAUtil.PROP_VERIFY_MODIFY_LISTENER );
    assertEquals( Boolean.FALSE, hasVerifyModifyListener );
    text.addVerifyListener( new VerifyListener() {

      public void verifyText( final VerifyEvent event ) {
      }
    } );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( text );
    hasVerifyModifyListener
     = ( Boolean )adapter.getPreserved( TextLCAUtil.PROP_VERIFY_MODIFY_LISTENER );
    assertEquals( Boolean.TRUE, hasVerifyModifyListener );
    RWTFixture.clearPreserved();
    //Bounds
    Rectangle rectangle = new Rectangle( 10, 10, 200, 100 );
    text.setBounds( rectangle );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( text );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    RWTFixture.clearPreserved();
    //control_listeners
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( text );
    hasListeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    RWTFixture.clearPreserved();
    text.addControlListener( new ControlListener() {

      public void controlMoved( final ControlEvent e ) {
      }

      public void controlResized( final ControlEvent e ) {
      }
    } );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( text );
    hasListeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    RWTFixture.clearPreserved();
    //enabled
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( text );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    RWTFixture.clearPreserved();
    text.setEnabled( false );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( text );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    RWTFixture.clearPreserved();
    //menu
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( text );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    RWTFixture.clearPreserved();
    Menu menu = new Menu( text );
    MenuItem item = new MenuItem( menu, SWT.NONE );
    item.setText( "1 Item" );
    text.setMenu( menu );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( text );
    assertEquals( menu, adapter.getPreserved( Props.MENU ) );
    RWTFixture.clearPreserved();
    //visible
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( text );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    RWTFixture.clearPreserved();
    text.setVisible( false );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( text );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    RWTFixture.clearPreserved();
    //z-index
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( text );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    RWTFixture.clearPreserved();
  }
}
