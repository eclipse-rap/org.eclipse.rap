/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.buttonkit;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.events.ActivateAdapter;
import org.eclipse.swt.internal.events.ActivateEvent;
import org.eclipse.swt.internal.widgets.IShellAdapter;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;

public class ButtonLCA_Test extends TestCase {

  public void testPushPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Button button = new Button( shell, SWT.PUSH );
    RWTFixture.markInitialized( display );
    testPreserveValues( display, button );
    //default
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( button );
    Boolean isDefault = ( Boolean )adapter.getPreserved( ButtonLCAUtil.PROP_DEFAULT );
    assertEquals( Boolean.FALSE, isDefault );
    button.getShell().setDefaultButton( button );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    isDefault = ( Boolean )adapter.getPreserved( ButtonLCAUtil.PROP_DEFAULT );
    assertEquals( Boolean.TRUE, isDefault );
    RWTFixture.clearPreserved();
    display.dispose();
  }

  public void testRadioPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Button button = new Button( shell, SWT.RADIO );
    RWTFixture.markInitialized( display );
    testPreserveValues( display, button );
    button.setSelection( true );
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( button );
    assertEquals( Boolean.TRUE,
                  adapter.getPreserved( ButtonLCAUtil.PROP_SELECTION ) );
    RWTFixture.clearPreserved();
    display.dispose();
  }

  public void testCheckPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Button button = new Button( shell, SWT.CHECK );
    RWTFixture.markInitialized( display );
    testPreserveValues( display, button );
    button.setSelection( true );
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( button );
    assertEquals( Boolean.TRUE,
                  adapter.getPreserved( ButtonLCAUtil.PROP_SELECTION ) );
    RWTFixture.clearPreserved();
    display.dispose();
  }

  public void testArrowPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Button button = new Button( shell, SWT.ARROW );
    RWTFixture.markInitialized( display );
    testPreserveValues( display, button );
    //alignment
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( button );
    Integer alignment
     = ( Integer )adapter.getPreserved( ButtonLCAUtil.PROP_ALIGNMENT );
    assertEquals( SWT.UP, alignment.intValue() );
    button.setAlignment( SWT.LEFT );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    alignment = ( Integer )adapter.getPreserved( ButtonLCAUtil.PROP_ALIGNMENT );
    assertEquals( SWT.LEFT, alignment.intValue() );
    RWTFixture.clearPreserved();
    button.setAlignment( SWT.RIGHT );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    alignment = ( Integer )adapter.getPreserved( ButtonLCAUtil.PROP_ALIGNMENT );
    assertEquals( SWT.RIGHT, alignment.intValue() );
    RWTFixture.clearPreserved();
    button.setAlignment( SWT.UP );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    alignment = ( Integer )adapter.getPreserved( ButtonLCAUtil.PROP_ALIGNMENT );
    assertEquals( SWT.UP, alignment.intValue() );
    RWTFixture.clearPreserved();
    button.setAlignment( SWT.DOWN );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    alignment = ( Integer )adapter.getPreserved( ButtonLCAUtil.PROP_ALIGNMENT );
    assertEquals( SWT.DOWN, alignment.intValue() );
    RWTFixture.clearPreserved();
    display.dispose();
  }

  public void testTogglePreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Button button = new Button( shell, SWT.TOGGLE );
    RWTFixture.markInitialized( display );
    testPreserveValues( display, button );
    button.setSelection( true );
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( button );
    assertEquals( Boolean.TRUE,
                  adapter.getPreserved( ButtonLCAUtil.PROP_SELECTION ) );
    RWTFixture.clearPreserved();
    display.dispose();
  }

  private void testPreserveValues( final Display display, final Button button ) {
    Boolean hasListeners;
    // Text,Image
    IWidgetAdapter adapter = WidgetUtil.getAdapter( button );
    if( ( button.getStyle() & SWT.ARROW ) == 0 ) {
      button.setText( "abc" );
      RWTFixture.preserveWidgets();
      adapter = WidgetUtil.getAdapter( button );
      Object object = adapter.getPreserved( Props.TEXT );
      assertEquals( "abc", ( String )object );
      RWTFixture.clearPreserved();
      Image image = Graphics.getImage( RWTFixture.IMAGE1 );
      button.setImage( image );
      RWTFixture.preserveWidgets();
      adapter = WidgetUtil.getAdapter( button );
      assertSame( image, adapter.getPreserved( Props.IMAGE ) );
      RWTFixture.clearPreserved();
      RWTFixture.preserveWidgets();
      adapter = WidgetUtil.getAdapter( button );
      hasListeners = ( Boolean )adapter.getPreserved( Props.FOCUS_LISTENER );
      assertEquals( Boolean.FALSE, hasListeners );
      RWTFixture.clearPreserved();
      button.addFocusListener( new FocusListener() {

        public void focusGained( final FocusEvent event ) {
        }

        public void focusLost( final FocusEvent event ) {
        }
      } );
      RWTFixture.preserveWidgets();
      adapter = WidgetUtil.getAdapter( button );
      hasListeners = ( Boolean )adapter.getPreserved( Props.FOCUS_LISTENER );
      assertEquals( Boolean.TRUE, hasListeners );
      RWTFixture.clearPreserved();
    }
    //Selection_Listener
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    hasListeners = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    RWTFixture.clearPreserved();
    SelectionListener selectionListener = new SelectionAdapter() {
    };
    button.addSelectionListener( selectionListener );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    hasListeners = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    RWTFixture.clearPreserved();
    //bound
    Rectangle rectangle = new Rectangle( 10, 10, 10, 10 );
    button.setBounds( rectangle );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    RWTFixture.clearPreserved();
    //z-index
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    RWTFixture.clearPreserved();
    //menu
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    RWTFixture.clearPreserved();
    Menu menu = new Menu( button );
    MenuItem item = new MenuItem( menu, SWT.NONE );
    item.setText( "1 Item" );
    button.setMenu( menu );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    assertEquals( menu, adapter.getPreserved( Props.MENU ) );
    //visible
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    RWTFixture.clearPreserved();
    button.setVisible( false );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    RWTFixture.clearPreserved();
    //enabled
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    RWTFixture.clearPreserved();
    button.setEnabled( false );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    RWTFixture.clearPreserved();
    //control_listeners
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    hasListeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    RWTFixture.clearPreserved();
    button.addControlListener( new ControlListener() {
      public void controlMoved( final ControlEvent e ) {
      }
      public void controlResized( final ControlEvent e ) {
      }
    } );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    hasListeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    RWTFixture.clearPreserved();
    //foreground background font
    Color background = Graphics.getColor( 122, 33, 203 );
    button.setBackground( background );
    Color foreground = Graphics.getColor( 211, 178, 211 );
    button.setForeground( foreground );
    Font font = Graphics.getFont( "font", 12, SWT.BOLD );
    button.setFont( font );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    RWTFixture.clearPreserved();
    //tab_index
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    RWTFixture.clearPreserved();
    //tooltiptext
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    assertEquals( null, button.getToolTipText() );
    RWTFixture.clearPreserved();
    button.setToolTipText( "some text" );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    assertEquals( "some text", button.getToolTipText() );
    RWTFixture.clearPreserved();
    //activate_listeners
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    RWTFixture.clearPreserved();
    ActivateEvent.addListener( button, new ActivateAdapter() {
    } );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    RWTFixture.clearPreserved();
  }

  public void testDisabledButtonSelection() {
    final StringBuffer log = new StringBuffer();
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
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
    RWTFixture.readDataAndProcessAction( display );
    assertEquals( "widgetActivated|", log.toString() );
  }
  
  public void testSelectionEvent() {
    final StringBuffer log = new StringBuffer();
    Display display = new Display();
    Shell shell = new Shell( display );
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
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( "org.eclipse.swt.events.widgetSelected",
                              buttonId );
    RWTFixture.executeLifeCycleFromServerThread( );
    assertEquals( "widgetSelected", log.toString() );
  }

  public void testEscape() throws Exception {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
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
  
  public void testDefaultButton() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Button button = new Button( shell, SWT.PUSH );
    assertFalse( ButtonLCAUtil.isDefaultButton( button ) );
    shell.setDefaultButton( button );
    assertTrue( ButtonLCAUtil.isDefaultButton( button ) );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
