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
package org.eclipse.swt.internal.widgets.buttonkit;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.lifecycle.DisplayUtil;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.events.ActivateAdapter;
import org.eclipse.swt.internal.events.ActivateEvent;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.internal.widgets.IShellAdapter;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;


// TODO [rst] Split into different test classes for button types
public class ButtonLCA_Test extends TestCase {

  private Display display;
  private Shell shell;

  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
  }

  protected void tearDown() throws Exception {
    display.dispose();
    Fixture.tearDown();
  }

  public void testPushPreserveValues() {
    Button button = new Button( shell, SWT.PUSH );
    Fixture.markInitialized( display );
    testPreserveValues( display, button );
    //default
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( button );
    Boolean isDefault
      = ( Boolean )adapter.getPreserved( PushButtonDelegateLCA.PROP_DEFAULT );
    assertEquals( Boolean.FALSE, isDefault );
    button.getShell().setDefaultButton( button );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    isDefault
      = ( Boolean )adapter.getPreserved( PushButtonDelegateLCA.PROP_DEFAULT );
    assertEquals( Boolean.TRUE, isDefault );
    Fixture.clearPreserved();
    display.dispose();
  }

  public void testRadioPreserveValues() {
    Button button = new Button( shell, SWT.RADIO );
    Fixture.markInitialized( display );
    testPreserveValues( display, button );
    button.setSelection( true );
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( button );
    assertEquals( Boolean.TRUE,
                  adapter.getPreserved( ButtonLCAUtil.PROP_SELECTION ) );
    Fixture.clearPreserved();
    display.dispose();
  }

  public void testCheckPreserveValues() {
    Button button = new Button( shell, SWT.CHECK );
    Fixture.markInitialized( display );
    testPreserveValues( display, button );
    button.setSelection( true );
    button.setGrayed( true );
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( button );
    assertEquals( Boolean.TRUE,
                  adapter.getPreserved( ButtonLCAUtil.PROP_SELECTION ) );
    assertEquals( Boolean.TRUE,
                  adapter.getPreserved( CheckButtonDelegateLCA.PROP_GRAYED ) );
    Fixture.clearPreserved();
    display.dispose();
  }

  public void testArrowPreserveValues() {
    Button button = new Button( shell, SWT.ARROW );
    Fixture.markInitialized( display );
    testPreserveValues( display, button );
    //alignment
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( button );
    Integer alignment
     = ( Integer )adapter.getPreserved( ButtonLCAUtil.PROP_ALIGNMENT );
    assertEquals( SWT.UP, alignment.intValue() );
    button.setAlignment( SWT.LEFT );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    alignment = ( Integer )adapter.getPreserved( ButtonLCAUtil.PROP_ALIGNMENT );
    assertEquals( SWT.LEFT, alignment.intValue() );
    Fixture.clearPreserved();
    button.setAlignment( SWT.RIGHT );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    alignment = ( Integer )adapter.getPreserved( ButtonLCAUtil.PROP_ALIGNMENT );
    assertEquals( SWT.RIGHT, alignment.intValue() );
    Fixture.clearPreserved();
    button.setAlignment( SWT.UP );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    alignment = ( Integer )adapter.getPreserved( ButtonLCAUtil.PROP_ALIGNMENT );
    assertEquals( SWT.UP, alignment.intValue() );
    Fixture.clearPreserved();
    button.setAlignment( SWT.DOWN );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    alignment = ( Integer )adapter.getPreserved( ButtonLCAUtil.PROP_ALIGNMENT );
    assertEquals( SWT.DOWN, alignment.intValue() );
    Fixture.clearPreserved();
    display.dispose();
  }

  public void testTogglePreserveValues() {
    Button button = new Button( shell, SWT.TOGGLE );
    Fixture.markInitialized( display );
    testPreserveValues( display, button );
    button.setSelection( true );
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( button );
    assertEquals( Boolean.TRUE,
                  adapter.getPreserved( ButtonLCAUtil.PROP_SELECTION ) );
    Fixture.clearPreserved();
    display.dispose();
  }

  private void testPreserveValues( final Display display, final Button button ) {
    Boolean hasListeners;
    // Text,Image
    IWidgetAdapter adapter = WidgetUtil.getAdapter( button );
    if( ( button.getStyle() & SWT.ARROW ) == 0 ) {
      button.setText( "abc" );
      Fixture.preserveWidgets();
      adapter = WidgetUtil.getAdapter( button );
      Object object = adapter.getPreserved( Props.TEXT );
      assertEquals( "abc", ( String )object );
      Fixture.clearPreserved();
      Image image = Graphics.getImage( Fixture.IMAGE1 );
      button.setImage( image );
      Fixture.preserveWidgets();
      adapter = WidgetUtil.getAdapter( button );
      assertSame( image, adapter.getPreserved( Props.IMAGE ) );
      Fixture.clearPreserved();
      Fixture.preserveWidgets();
      adapter = WidgetUtil.getAdapter( button );
      hasListeners = ( Boolean )adapter.getPreserved( Props.FOCUS_LISTENER );
      assertEquals( Boolean.FALSE, hasListeners );
      Fixture.clearPreserved();
      button.addFocusListener( new FocusAdapter() { } );
      Fixture.preserveWidgets();
      adapter = WidgetUtil.getAdapter( button );
      hasListeners = ( Boolean )adapter.getPreserved( Props.FOCUS_LISTENER );
      assertEquals( Boolean.TRUE, hasListeners );
      Fixture.clearPreserved();
    }
    //Selection_Listener
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    hasListeners = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    SelectionListener selectionListener = new SelectionAdapter() {
    };
    button.addSelectionListener( selectionListener );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    hasListeners = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    Fixture.clearPreserved();
    //bound
    Rectangle rectangle = new Rectangle( 10, 10, 10, 10 );
    button.setBounds( rectangle );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    Fixture.clearPreserved();
    //z-index
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    Fixture.clearPreserved();
    //menu
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    Menu menu = new Menu( button );
    MenuItem item = new MenuItem( menu, SWT.NONE );
    item.setText( "1 Item" );
    button.setMenu( menu );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    assertEquals( menu, adapter.getPreserved( Props.MENU ) );
    //visible
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    button.setVisible( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    //enabled
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    button.setEnabled( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    button.setEnabled( true );
    //control_listeners
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    hasListeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    button.addControlListener( new ControlListener() {
      public void controlMoved( final ControlEvent e ) {
      }
      public void controlResized( final ControlEvent e ) {
      }
    } );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    hasListeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    Fixture.clearPreserved();
    //foreground background font
    Color background = Graphics.getColor( 122, 33, 203 );
    button.setBackground( background );
    Color foreground = Graphics.getColor( 211, 178, 211 );
    button.setForeground( foreground );
    Font font = Graphics.getFont( "font", 12, SWT.BOLD );
    button.setFont( font );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    Fixture.clearPreserved();
    //tab_index
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    Fixture.clearPreserved();
    //tooltiptext
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    assertEquals( null, button.getToolTipText() );
    Fixture.clearPreserved();
    button.setToolTipText( "some text" );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    assertEquals( "some text", button.getToolTipText() );
    Fixture.clearPreserved();
    //activate_listeners
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    ActivateEvent.addListener( button, new ActivateAdapter() {
    } );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    Fixture.clearPreserved();
  }

  public void testDisabledButtonSelection() {
    final StringBuffer log = new StringBuffer();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    final Button button = new Button( shell, SWT.NONE );
    Label label = new Label( shell, SWT.NONE );
    ActivateEvent.addListener( button, new ActivateAdapter() {
      public void activated( final ActivateEvent event ) {
        log.append( "widgetActivated|" );
        button.setEnabled( false );
      }
    } );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        log.append( "widgetSelected|" );
      }
    } );
    Object adapter = shell.getAdapter( IShellAdapter.class );
    IShellAdapter shellAdapter = ( IShellAdapter )adapter;
    shellAdapter.setActiveControl( label );
    String displayId = DisplayUtil.getId( display );
    String buttonId = WidgetUtil.getId( button );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_ACTIVATED, buttonId );
    Fixture.readDataAndProcessAction( display );
    assertEquals( "widgetActivated|", log.toString() );
  }

  public void testSelectionEvent() {
    final StringBuffer log = new StringBuffer();
    final Button button = new Button( shell, SWT.PUSH );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        assertEquals( 0, event.x );
        assertEquals( 0, event.y );
        assertEquals( 0, event.width );
        assertEquals( 0, event.height );
        assertSame( button, event.getSource() );
        assertEquals( 0, event.detail );
        log.append( "widgetSelected" );
      }
    } );
    String displayId = DisplayUtil.getId( display );
    String buttonId = WidgetUtil.getId( button );
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( "org.eclipse.swt.events.widgetSelected", buttonId );
    Fixture.readDataAndProcessAction( display );
    assertEquals( "widgetSelected", log.toString() );
  }

  public void testEscape() throws Exception {
    Button pushButton = new Button( shell, SWT.PUSH );
    pushButton.setText( "PUSH &E<s>ca'pe\" && me" );
    Button checkButton = new Button( shell, SWT.CHECK );
    checkButton.setText( "CHECK &E<s>ca'pe\" && me" );
    Button radioButton = new Button( shell, SWT.RADIO );
    radioButton.setText( "RADIO &E<s>ca'pe\" && me" );
    Fixture.fakeResponseWriter();
    ButtonDelegateLCA pushLCA = new PushButtonDelegateLCA();
    pushLCA.renderChanges( pushButton );
    String expected = "\"PUSH E&lt;s&gt;ca'pe&quot; &amp; me\"";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
    ButtonDelegateLCA checkLCA = new CheckButtonDelegateLCA();
    checkLCA.renderChanges( checkButton );
    expected = "\"CHECK E&lt;s&gt;ca'pe&quot; &amp; me\"";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
    ButtonDelegateLCA radioLCA = new RadioButtonDelegateLCA();
    radioLCA.renderChanges( radioButton );
    expected = "\"RADIO E&lt;s&gt;ca'pe&quot; &amp; me\"";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
  }

  public void testReplaceLinkeBreaks() throws Exception {
    Button normalButton = new Button( shell, SWT.PUSH );
    normalButton.setText( "Some\nText" );
    Button wrapButton = new Button( shell, SWT.WRAP );
    wrapButton.setText( "Some\nText" );
    Fixture.fakeResponseWriter();
    ButtonDelegateLCA pushLCA = new PushButtonDelegateLCA();
    pushLCA.renderChanges( normalButton );
    String expected = "\"Some\\nText\"";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
    Fixture.fakeResponseWriter();
    pushLCA.renderChanges( wrapButton );
    expected = "\"Some<br/>Text\"";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
  }
  
  public void testDefaultButton() {
    Button button = new Button( shell, SWT.PUSH );
    assertFalse( PushButtonDelegateLCA.isDefaultButton( button ) );
    shell.setDefaultButton( button );
    assertTrue( PushButtonDelegateLCA.isDefaultButton( button ) );
  }

  // https://bugs.eclipse.org/bugs/show_bug.cgi?id=224872
  public void testRadioSelectionEvent() {
    final StringBuffer log = new StringBuffer();
    final Button button1 = new Button( shell, SWT.RADIO );
    button1.setText( "1" );
    final Button button2 = new Button( shell, SWT.RADIO );
    button2.setText( "2" );
    final Button button3 = new Button( shell, SWT.RADIO );
    button3.setText( "3" );
    SelectionAdapter listener = new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        Button button = ( Button )event.getSource();
        log.append( button.getText() );
        log.append( ":" );
        log.append( button.getSelection() );
        log.append( "|" );
      }
    };
    button1.addSelectionListener( listener );
    button2.addSelectionListener( listener );
    button3.addSelectionListener( listener );
    String displayId = DisplayUtil.getId( display );
    String button1Id = WidgetUtil.getId( button1 );
    String button2Id = WidgetUtil.getId( button2 );
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( button1Id + ".selection", "true" );
    Fixture.readDataAndProcessAction( display );
    assertTrue( log.indexOf( "1:true" ) != -1 );
    assertTrue( log.indexOf( "2:" ) == -1 );
    assertTrue( log.indexOf( "3:" ) == -1 );

    log.delete( 0, log.length() );

    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( button1Id + ".selection", "false" );
    Fixture.fakeRequestParam( button2Id + ".selection", "true" );
    Fixture.readDataAndProcessAction( display );
    assertTrue( log.indexOf( "1:false" ) != -1 );
    assertTrue( log.indexOf( "2:true" ) != -1 );
    assertTrue( log.indexOf( "3:" ) == -1 );
  }

  public void testRadioTypedSelectionEventOrder() {
    final java.util.List log = new ArrayList();
    Button button1 = new Button( shell, SWT.RADIO );
    button1.setText( "1" );
    Button button2 = new Button( shell, SWT.RADIO );
    button2.setText( "2" );
    SelectionAdapter listener = new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        log.add( event );
      }
    };
    button1.addSelectionListener( listener );
    button2.addSelectionListener( listener );
    button2.setSelection( true );
    String displayId = DisplayUtil.getId( display );
    String button1Id = WidgetUtil.getId( button1 );
    String button2Id = WidgetUtil.getId( button2 );
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( button1Id + ".selection", "true" );
    Fixture.fakeRequestParam( button2Id + ".selection", "false" );
    Fixture.readDataAndProcessAction( display );
    assertEquals( 2, log.size() );
    SelectionEvent event = ( SelectionEvent )log.get( 0 );
    assertSame( button2, event.widget );
    event = ( SelectionEvent )log.get( 1 );
    assertSame( button1, event.widget );
  }

  public void testRadioUntypedSelectionEventOrder() {
    final java.util.List log = new ArrayList();
    Button button1 = new Button( shell, SWT.RADIO );
    button1.setText( "1" );
    Button button2 = new Button( shell, SWT.RADIO );
    button2.setText( "2" );
    Listener listener = new Listener() {
      public void handleEvent( final Event event ) {
        log.add( event );
      }
    };
    button1.addListener( SWT.Selection, listener );
    button2.addListener( SWT.Selection, listener );
    button2.setSelection( true );
    String displayId = DisplayUtil.getId( display );
    String button1Id = WidgetUtil.getId( button1 );
    String button2Id = WidgetUtil.getId( button2 );
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( button1Id + ".selection", "true" );
    Fixture.fakeRequestParam( button2Id + ".selection", "false" );
    Fixture.readDataAndProcessAction( display );
    assertEquals( 2, log.size() );
    Event event = ( Event )log.get( 0 );
    assertSame( button2, event.widget );
    event = ( Event )log.get( 1 );
    assertSame( button1, event.widget );
  }

  public void testRenderTextAndImageForPushButton() throws Exception {
    Button button = new Button( shell, SWT.PUSH );
    button.setText( "Test" );
    Image image = Graphics.getImage( Fixture.IMAGE1 );
    button.setImage( image );
    Fixture.markInitialized( button );
    Fixture.preserveWidgets();
    Fixture.fakeResponseWriter();
    PushButtonDelegateLCA lca = new PushButtonDelegateLCA();
    lca.renderChanges( button );
    String allMarkup = Fixture.getAllMarkup();
    assertTrue( allMarkup.indexOf( "w.setText( \"Test\" );" ) != -1 );
    String imageLocation = ImageFactory.getImagePath( image );
    String expected = "w.setImage( \"" + imageLocation + "\", 58, 12 );";
    assertTrue( allMarkup.indexOf( expected ) != -1 );
    Fixture.fakeResponseWriter();
    lca.preserveValues( button );
    button.setImage( null );
    lca.renderChanges( button );
    allMarkup = Fixture.getAllMarkup();
    expected = "w.setImage( null, 0, 0 );";
    assertTrue( allMarkup.indexOf( expected ) != -1 );
  }
  
  public void testRenderWrap() throws Exception {
    Button button = new Button( shell, SWT.PUSH | SWT.WRAP );
    Fixture.fakeResponseWriter();
    PushButtonDelegateLCA lca = new PushButtonDelegateLCA();
    lca.renderInitialization( button );
    String allMarkup = Fixture.getAllMarkup();
    assertTrue( allMarkup.indexOf( "w.setWrap( true );" ) != -1 );
  }

  public void testRenderTextAndImageForCheckAndRadioButton() throws Exception {
    Button checkButton = new Button( shell, SWT.CHECK );
    Button radioButton = new Button( shell, SWT.RADIO );
    checkButton.setText( "Test" );
    checkButton.setImage( Graphics.getImage( Fixture.IMAGE1 ) );
    radioButton.setText( "Test" );
    radioButton.setImage( Graphics.getImage( Fixture.IMAGE1 ) );
    Fixture.markInitialized( radioButton );
    Fixture.preserveWidgets();
    Fixture.fakeResponseWriter();
    CheckButtonDelegateLCA checkLCA = new CheckButtonDelegateLCA();
    RadioButtonDelegateLCA radioLCA = new RadioButtonDelegateLCA();
    checkLCA.renderChanges( checkButton );
    String allMarkup = Fixture.getAllMarkup();
    assertTrue( allMarkup.indexOf( "w.setText( \"Test\" );" ) != -1 );
    assertTrue( allMarkup.indexOf( "w.setImage(" ) != -1 );
    Fixture.fakeResponseWriter();
    radioLCA.renderChanges( radioButton );
    allMarkup = Fixture.getAllMarkup();
    assertTrue( allMarkup.indexOf( "w.setText( \"Test\" );" ) != -1 );
    assertTrue( allMarkup.indexOf( "w.setImage(" ) != -1 );
  }
  
  public void testRenderNoRadioGroupForRadioButton() throws Exception {
    Composite composite = new Composite( shell, SWT.NO_RADIO_GROUP );
    Button radioButton = new Button( composite, SWT.RADIO );
    Fixture.fakeResponseWriter();
    RadioButtonDelegateLCA radioLCA = new RadioButtonDelegateLCA();
    radioLCA.renderInitialization( radioButton );
    String allMarkup = Fixture.getAllMarkup();
    assertTrue( allMarkup.indexOf( "w.setNoRadioGroup( true );" ) != -1 );
  }
}
