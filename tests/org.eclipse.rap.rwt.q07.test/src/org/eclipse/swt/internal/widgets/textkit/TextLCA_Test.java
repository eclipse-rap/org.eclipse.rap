/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.textkit;

import java.io.IOException;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;

public class TextLCA_Test extends TestCase {
  
  private Display display;
  private Shell shell;

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Fixture.fakeResponseWriter();
    display = new Display();
    shell = new Shell( display );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testMultiPreserveValues() {
    Text text = new Text( shell, SWT.MULTI );
    testPreserveValues( text );
  }

  public void testPasswordPreserveValues() {
    Text text = new Text( shell, SWT.PASSWORD );
    testPreserveValues( text );
  }

  public void testSinglePreserveValues() {
    Text text = new Text( shell, SWT.SINGLE );
    testPreserveValues( text );
    //Selection_Listener
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( text );
    String propSelectionLsnr = TextLCAUtil.PROP_SELECTION_LISTENER;
    Boolean hasListeners = ( Boolean )adapter.getPreserved( propSelectionLsnr );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    SelectionListener selectionListener = new SelectionAdapter() {
    };
    text.addSelectionListener( selectionListener );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( text );
    hasListeners = ( Boolean )adapter.getPreserved( propSelectionLsnr );
    assertEquals( Boolean.TRUE, hasListeners );
  }

  public void testReadData() {
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
    Text text = new Text( shell, SWT.NONE );
    shell.open();
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    TextLCA textLCA = new TextLCA();
    text.setText( "hello" );
    textLCA.renderChanges( text );
    assertTrue( Fixture.getAllMarkup().endsWith( "setValue( \"hello\" );" ) );
    Fixture.fakeResponseWriter();
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    textLCA.renderChanges( text );
    assertEquals( "", Fixture.getAllMarkup() );
  }
  
  public void testRenderText_ZeroChar() throws IOException {
    Text text = new Text( shell, SWT.NONE );
    shell.open();
    Fixture.markInitialized( display );
    Fixture.markInitialized( text );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    TextLCA textLCA = new TextLCA();
    char[] value = new char[] { 'h', 'e', 'l', 0, 'l', 'o' };
    text.setText( String.valueOf( value ) );
    textLCA.renderChanges( text );
    assertTrue( Fixture.getAllMarkup().endsWith( "setValue( \"hel\" );" ) );
  }

  public void testModifyEvent() {
    final StringBuffer log = new StringBuffer();
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
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( textId + ".text", "new text" );
    Fixture.fakeRequestParam( JSConst.EVENT_MODIFY_TEXT, textId );
    Fixture.executeLifeCycleFromServerThread( );
    assertEquals( "modifyText", log.toString() );
  }

  public void testVerifyEvent() {
    final StringBuffer log = new StringBuffer();
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
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( textId + ".text", "verify me" );
    Fixture.fakeRequestParam( JSConst.EVENT_MODIFY_TEXT, textId );
    Fixture.executeLifeCycleFromServerThread( );
    assertEquals( "verifyText", log.toString() );
  }
  
  public void testSelectionWithVerifyEvent() {
    final java.util.List log = new ArrayList();
    // register preserve-values phase-listener
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    lifeCycle.addPhaseListener( new PreserveWidgetsPhaseListener() );
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
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.markInitialized( text );
    log.clear();
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( textId + ".text", "verify me" );
    Fixture.fakeRequestParam( textId + ".selectionStart", "1" );
    Fixture.fakeRequestParam( textId + ".selectionLength", "0" );
    Fixture.fakeRequestParam( JSConst.EVENT_MODIFY_TEXT, textId );
    Fixture.executeLifeCycleFromServerThread();
    // ensure that an empty verify listener does not lead to sending the
    // original text and selection values back to the client
    String markup = Fixture.getAllMarkup();
    assertEquals( -1, markup.indexOf( "w.setValue(" ) );
    assertEquals( -1, markup.indexOf( ".setSelection( w," ) );
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
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( textId + ".text", "verify me" );
    Fixture.fakeRequestParam( textId + ".selectionStart", "1" );
    Fixture.fakeRequestParam( textId + ".selectionLength", "0" );
    Fixture.fakeRequestParam( JSConst.EVENT_MODIFY_TEXT, textId );
    Fixture.executeLifeCycleFromServerThread( );
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
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( textId + ".text", "verify me" );
    Fixture.fakeRequestParam( textId + ".selectionStart", "1" );
    Fixture.fakeRequestParam( textId + ".selectionLength", "0" );
    Fixture.fakeRequestParam( JSConst.EVENT_MODIFY_TEXT, textId );
    Fixture.executeLifeCycleFromServerThread( );
    assertEquals( 1, log.size() );
    assertEquals( new Point( 0, 0 ), text.getSelection() );
    assertEquals( "", text.getText() );
    text.removeVerifyListener( alteringVerifyListener );
  }

  public void testPreserveText() {
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    lifeCycle.addPhaseListener( new PreserveWidgetsPhaseListener() );
    Text text = new Text( shell, SWT.SINGLE );
    shell.open();
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.markInitialized( text );
    Fixture.fakeNewRequest();
    String textId = WidgetUtil.getId( text );
    String displayId = DisplayUtil.getId( display );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( textId + ".text", "verify me" );
    Fixture.fakeRequestParam( textId + ".selectionStart", "1" );
    Fixture.fakeRequestParam( textId + ".selectionLength", "0" );
    Fixture.executeLifeCycleFromServerThread();
    // ensure that no text and selection values are sent back to the client
    String markup = Fixture.getAllMarkup();
    assertEquals( -1, markup.indexOf( "w.setValue(" ) );
    assertEquals( -1, markup.indexOf( ".setSelection( w," ) );
  }

  public void testVerifyAndModifyEvent() {
    final java.util.List log = new ArrayList();
    // register preserve-values phase-listener
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    lifeCycle.addPhaseListener( new PreserveWidgetsPhaseListener() );
    // set up widgets to be tested
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
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( textId + ".text", "verify me" );
    Fixture.fakeRequestParam( textId + ".selectionStart", "1" );
    Fixture.fakeRequestParam( textId + ".selectionLength", "0" );
    Fixture.fakeRequestParam( JSConst.EVENT_MODIFY_TEXT, textId );
    Fixture.executeLifeCycleFromServerThread( );
    assertEquals( 2, log.size() );
    assertTrue( log.get( 0 ) instanceof VerifyEvent );
    assertTrue( log.get( 1 ) instanceof ModifyEvent );
  }

  public void testTextLimit() throws IOException {
    Text text = new Text( shell, SWT.NONE );
    TextLCA lca = new TextLCA();
    // run LCA one to dump the here uninteresting prolog
    lca.renderChanges( text );
    // Initially no textLimit must be rendered if the initial value is untouched
    Fixture.fakeResponseWriter();
    lca.renderChanges( text );
    assertEquals( -1, Fixture.getAllMarkup().indexOf( "setMaxLength" ) );
    // Positive textLimit is written as setMaxLength( ... )
    Fixture.fakeResponseWriter();
    Fixture.markInitialized( text );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    text.setTextLimit( 12 );
    lca.renderChanges( text );
    String expected = "setMaxLength( 12 );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
    // Negative textLimit is tread as 'no limit'
    Fixture.fakeResponseWriter();
    Fixture.markInitialized( text );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    text.setTextLimit( -50 );
    lca.renderChanges( text );
    expected = "setMaxLength( null );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
  }
  
  public void testEchoCharMultiLine() {
    String displayId = DisplayUtil.getId( display );
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Text text = new Text( shell, SWT.MULTI );
    Fixture.executeLifeCycleFromServerThread();
    String markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( "setPasswordMode" ) == -1 );
    text.setEchoChar( ( char )27 );
    Fixture.executeLifeCycleFromServerThread();
    assertTrue( markup.indexOf( "setPasswordMode" ) == -1 );
  }

  public void testEchoCharSingleLine() {
    String displayId = DisplayUtil.getId( display );
    Text text = new Text( shell, SWT.SINGLE );
    Fixture.markInitialized( display );
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.executeLifeCycleFromServerThread();
    String markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( "setPasswordMode" ) == -1 );
    text.setEchoChar( ( char )27 );
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );    
    Fixture.executeLifeCycleFromServerThread();
    markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( "setPasswordMode( true )" ) != -1 );
    text.setEchoChar( ( char )0 );
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );    
    Fixture.executeLifeCycleFromServerThread();
    markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( "setPasswordMode( false )" ) != -1 );
  }
  
  public void testEchoCharPassword() {
    String displayId = DisplayUtil.getId( display );
    Text text = new Text( shell, SWT.PASSWORD );
    Fixture.markInitialized( display );
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.executeLifeCycleFromServerThread();
    String markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( "setPasswordMode( true )" ) != -1 );
    text.setEchoChar( (char) 0 );
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.executeLifeCycleFromServerThread();
    markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( "setPasswordMode( false )" ) != -1 );
    text.setEchoChar( (char) 27 );
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.executeLifeCycleFromServerThread();
    markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( "setPasswordMode( true )" ) != -1 );
  }
  
  private static void testPreserveValues( final Text text ) {
    //text
    text.setText( "some text" );
    Fixture.markInitialized( text.getDisplay() );
    Fixture.preserveWidgets();
    assertEquals( text.getText(), getPreserved( text, Props.TEXT ) );
    Fixture.clearPreserved();
    //text-limit
    Fixture.preserveWidgets();
    Integer textLimit
     = ( Integer )( getPreserved( text, TextLCAUtil.PROP_TEXT_LIMIT ) );
    assertEquals( Integer.MAX_VALUE, textLimit.intValue() );
    text.setTextLimit( 30 );
    Fixture.preserveWidgets();
    textLimit
     = ( Integer )( getPreserved( text, TextLCAUtil.PROP_TEXT_LIMIT ) );
    assertEquals( 30, textLimit.intValue() );
    Fixture.clearPreserved();
    //selection
    Fixture.preserveWidgets();
    Point point = new Point( 0, 0 );
    assertEquals( point, getPreserved( text, TextLCAUtil.PROP_SELECTION ) );
    Fixture.clearPreserved();
    point = new Point( 3, 6 );
    text.setSelection( point );
    text.getSelection();
    Fixture.preserveWidgets();
    assertEquals( point, getPreserved( text, TextLCAUtil.PROP_SELECTION ) );
    Fixture.clearPreserved();
    //readonly
    Fixture.preserveWidgets();
    Boolean readonly
     = ( Boolean )getPreserved( text, TextLCAUtil.PROP_READ_ONLY );
    assertEquals( Boolean.FALSE, readonly );
    Fixture.clearPreserved();
    text.setEditable( false );
    Fixture.preserveWidgets();
    readonly = ( Boolean )getPreserved( text, TextLCAUtil.PROP_READ_ONLY );
    assertEquals( Boolean.TRUE, readonly );
    Fixture.clearPreserved();
    //verifymodify-Listeners
    Fixture.preserveWidgets();
    Boolean hasVerifyModifyListener
     = ( Boolean )getPreserved( text, TextLCAUtil.PROP_VERIFY_MODIFY_LISTENER );
    assertEquals( Boolean.FALSE, hasVerifyModifyListener );
    text.addVerifyListener( new VerifyListener() {
      public void verifyText( final VerifyEvent event ) {
      }
    } );
    Fixture.clearPreserved();
    //Bounds
    Rectangle rectangle = new Rectangle( 10, 10, 200, 100 );
    text.setBounds( rectangle );
    Fixture.preserveWidgets();
    assertEquals( rectangle, getPreserved( text, Props.BOUNDS ) );
    Fixture.clearPreserved();
    //control_listeners
    Fixture.preserveWidgets();
    assertEquals( Boolean.FALSE, 
                  getPreserved( text, Props.CONTROL_LISTENERS ) );
    Fixture.clearPreserved();
    text.addControlListener( new ControlAdapter() { } );
    Fixture.preserveWidgets();
    assertEquals( Boolean.TRUE, getPreserved( text, Props.CONTROL_LISTENERS ) );
    Fixture.clearPreserved();
    //enabled
    Fixture.preserveWidgets();
    assertEquals( Boolean.TRUE, getPreserved( text, Props.ENABLED ) );
    Fixture.clearPreserved();
    text.setEnabled( false );
    Fixture.preserveWidgets();
    assertEquals( Boolean.FALSE, getPreserved( text, Props.ENABLED ) );
    Fixture.clearPreserved();
    //menu
    Fixture.preserveWidgets();
    assertEquals( null, getPreserved( text, Props.MENU ) );
    Fixture.clearPreserved();
    Menu menu = new Menu( text );
    MenuItem item = new MenuItem( menu, SWT.NONE );
    item.setText( "1 Item" );
    text.setMenu( menu );
    Fixture.preserveWidgets();
    assertEquals( menu, getPreserved( text, Props.MENU ) );
    Fixture.clearPreserved();
    //visible
    Fixture.preserveWidgets();
    assertEquals( Boolean.TRUE, getPreserved( text, Props.VISIBLE ) );
    Fixture.clearPreserved();
    text.setVisible( false );
    Fixture.preserveWidgets();
    assertEquals( Boolean.FALSE, getPreserved( text, Props.VISIBLE ) );
    Fixture.clearPreserved();
    // z-index
    Fixture.preserveWidgets();
    assertTrue( getPreserved( text, Props.Z_INDEX ) != null );
    Fixture.clearPreserved();
  }
  
  public void testPreserveModifyListener() {
    Fixture.markInitialized( display );
    Text text = new Text( shell, SWT.SINGLE );
    text.addModifyListener( createModifyListener() );
    Fixture.preserveWidgets();
    Object preserved 
      = getPreserved( text, TextLCAUtil.PROP_VERIFY_MODIFY_LISTENER );
    assertEquals( Boolean.TRUE, preserved );
  }

  public void testPreserveModifyListenerWhenReadOnly() {
    Fixture.markInitialized( display );
    Text text = new Text( shell, SWT.READ_ONLY );
    text.addModifyListener( createModifyListener() );
    Fixture.preserveWidgets();
    Object preserved 
      = getPreserved( text, TextLCAUtil.PROP_VERIFY_MODIFY_LISTENER );
    assertEquals( Boolean.TRUE, preserved );
  }
  
  public void testWriteModifyListenerWhenReadOnly() throws IOException {
    String setHasModifyListener
      = "org.eclipse.swt.TextUtil.setHasVerifyOrModifyListener( w, true )";
    Text text = new Text( shell, SWT.READ_ONLY );
    text.addModifyListener( createModifyListener() );
    new TextLCA().renderChanges( text );
    assertTrue( Fixture.getAllMarkup().indexOf( setHasModifyListener ) != -1 );
  }
  
  // bug 337130
  public void testWriteModifyListenerAfterBecomingEditable() throws IOException 
  {
    String setHasModifyListener
      = "org.eclipse.swt.TextUtil.setHasVerifyOrModifyListener( w, true )";
    Text text = new Text( shell, SWT.READ_ONLY );
    text.addModifyListener( createModifyListener() );
    Fixture.markInitialized( text );
    Fixture.preserveWidgets();
    text.setEditable( true );
    new TextLCA().renderChanges( text );
    assertTrue( Fixture.getAllMarkup().indexOf( setHasModifyListener ) != -1 );
  }

  private static Object getPreserved( final Text text, final String property ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( text );
    return adapter.getPreserved( property );
  }

  private static ModifyListener createModifyListener() {
    return new ModifyListener() {
      public void modifyText( final ModifyEvent event ) {
      }
    };
  }
}
