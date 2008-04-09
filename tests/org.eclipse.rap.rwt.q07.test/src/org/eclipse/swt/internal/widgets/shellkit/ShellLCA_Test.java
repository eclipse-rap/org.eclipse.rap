/*******************************************************************************
 * Copyright (c) 2002-2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.shellkit;

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
import org.eclipse.swt.internal.events.*;
import org.eclipse.swt.internal.widgets.IShellAdapter;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;

public class ShellLCA_Test extends TestCase {

  public void testPreserveValues() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Button button = new Button( shell, SWT.PUSH );
    Boolean hasListeners;
    RWTFixture.markInitialized( display );
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( shell );
    hasListeners
     = ( Boolean )adapter.getPreserved( ShellLCA.PROP_SHELL_LISTENER );
    assertEquals( "", adapter.getPreserved( Props.TEXT ) );
    assertEquals( null, adapter.getPreserved( Props.IMAGE ) );
    assertEquals( Boolean.FALSE, hasListeners );
    assertEquals( null, adapter.getPreserved( ShellLCA.PROP_ACTIVE_CONTROL ) );
    assertEquals( null, adapter.getPreserved( ShellLCA.PROP_ACTIVE_SHELL ) );
    assertEquals( null, adapter.getPreserved( ShellLCA.PROP_MODE ) );
    RWTFixture.clearPreserved();
    shell.setText( "some text" );
    shell.open();
    shell.setActive();
    IShellAdapter shellAdapter
     = ( IShellAdapter )shell.getAdapter( IShellAdapter.class );
    shellAdapter.setActiveControl( button );
    shell.addShellListener( new ShellListener() {

      public void shellActivated( final ShellEvent e ) {
      }

      public void shellClosed( final ShellEvent e ) {
      }

      public void shellDeactivated( final ShellEvent e ) {
      }
    } );
    shell.setMaximized( true );
    Image image = Graphics.getImage( RWTFixture.IMAGE1 );
    shell.setImage( image );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( shell );
    hasListeners = ( Boolean )adapter.getPreserved( ShellLCA.PROP_SHELL_LISTENER );
    assertEquals( "some text", adapter.getPreserved( Props.TEXT ) );
    assertEquals( image, adapter.getPreserved( Props.IMAGE ) );
    assertEquals( Boolean.TRUE, hasListeners );
    assertEquals( button, adapter.getPreserved( ShellLCA.PROP_ACTIVE_CONTROL ) );
    assertEquals( shell, adapter.getPreserved( ShellLCA.PROP_ACTIVE_SHELL ) );
    assertEquals( "maximized", adapter.getPreserved( ShellLCA.PROP_MODE ) );
    RWTFixture.clearPreserved();
    //control: enabled
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( shell );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    RWTFixture.clearPreserved();
    shell.setEnabled( false );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( shell );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    RWTFixture.clearPreserved();
    //visible
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( shell );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    RWTFixture.clearPreserved();
    shell.setVisible( false );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( shell );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    RWTFixture.clearPreserved();
    //menu
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( shell );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    RWTFixture.clearPreserved();
    Menu menu = new Menu( shell );
    MenuItem item = new MenuItem( menu, SWT.NONE );
    item.setText( "1 Item" );
    shell.setMenu( menu );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( shell );
    assertEquals( menu, adapter.getPreserved( Props.MENU ) );
    RWTFixture.clearPreserved();
    //bound
    Rectangle rectangle = new Rectangle( 10, 10, 30, 50 );
    shell.setBounds( rectangle );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( shell );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    RWTFixture.clearPreserved();
    //control_listeners
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( shell );
    hasListeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    RWTFixture.clearPreserved();
    shell.addControlListener( new ControlListener() {

      public void controlMoved( final ControlEvent e ) {
      }

      public void controlResized( final ControlEvent e ) {
      }
    } );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( shell );
    hasListeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    RWTFixture.clearPreserved();
    //foreground background font
    Color background = Graphics.getColor( 122, 33, 203 );
    shell.setBackground( background );
    Color foreground = Graphics.getColor( 211, 178, 211 );
    shell.setForeground( foreground );
    Font font = Graphics.getFont( "font", 12, SWT.BOLD );
    shell.setFont( font );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( shell );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    RWTFixture.clearPreserved();
    //tooltiptext
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( shell );
    assertEquals( null, shell.getToolTipText() );
    RWTFixture.clearPreserved();
    shell.setToolTipText( "some text" );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( shell );
    assertEquals( "some text", shell.getToolTipText() );
    RWTFixture.clearPreserved();
    //activate_listeners   Focus_listeners
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( shell );
    hasListeners = ( Boolean )adapter.getPreserved( Props.FOCUS_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    RWTFixture.clearPreserved();
    shell.addFocusListener( new FocusListener() {

      public void focusGained( final FocusEvent event ) {
      }

      public void focusLost( final FocusEvent event ) {
      }
    } );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( shell );
    hasListeners = ( Boolean )adapter.getPreserved( Props.FOCUS_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( shell );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    RWTFixture.clearPreserved();
    ActivateEvent.addListener( shell, new ActivateAdapter() {
    } );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( shell );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    RWTFixture.clearPreserved();
    display.dispose();
  }

  public void testReadDataForClosed() {
    final StringBuffer log = new StringBuffer();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    shell.open();
    shell.addShellListener( new ShellAdapter() {

      public void shellClosed( final ShellEvent event ) {
        log.append( "closed" );
      }
    } );
    String shellId = WidgetUtil.getId( shell );
    Fixture.fakeRequestParam( JSConst.EVENT_SHELL_CLOSED, shellId );
    RWTFixture.readDataAndProcessAction( shell );
    assertEquals( "closed", log.toString() );
  }

  public void testReadDataForActiveControl() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Label label = new Label( shell, SWT.NONE );
    Label otherLabel = new Label( shell, SWT.NONE );
    String displayId = DisplayUtil.getId( display );
    String shellId = WidgetUtil.getId( shell );
    String labelId = WidgetUtil.getId( label );
    String otherLabelId = WidgetUtil.getId( otherLabel );
    setActiveControl( shell, otherLabel );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( shellId + ".activeControl", labelId );
    RWTFixture.readDataAndProcessAction( display );
    assertSame( label, getActiveControl( shell ) );
    // Ensure that if there is both, an avtiveControl parameter and a
    // controlActivated event, the activeControl parameter is ignored
    setActiveControl( shell, otherLabel );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( shellId + ".activeControl", otherLabelId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_ACTIVATED, labelId );
    RWTFixture.readDataAndProcessAction( display );
    assertSame( label, getActiveControl( shell ) );
  }

  public void testShellActivate() {
    final StringBuffer activateEventLog = new StringBuffer();
    ActivateListener activateListener = new ActivateListener() {

      public void activated( final ActivateEvent event ) {
        Shell shell = ( Shell )event.getSource();
        activateEventLog.append( "activated:" + shell.getData() + "|" );
      }

      public void deactivated( final ActivateEvent event ) {
        Shell shell = ( Shell )event.getSource();
        activateEventLog.append( "deactivated:" + shell.getData() + "|" );
      }
    };
    final StringBuffer shellEventLog = new StringBuffer();
    ShellListener shellListener = new ShellAdapter() {

      public void shellActivated( ShellEvent event ) {
        Shell shell = ( Shell )event.getSource();
        shellEventLog.append( "activated:" + shell.getData() + "|" );
      }

      public void shellDeactivated( ShellEvent event ) {
        Shell shell = ( Shell )event.getSource();
        shellEventLog.append( "deactivated:" + shell.getData() + "|" );
      }
    };
    Display display = new Display();
    Shell shellToActivate = new Shell( display, SWT.NONE );
    shellToActivate.setData( "shellToActivate" );
    shellToActivate.open();
    Shell activeShell = new Shell( display, SWT.NONE );
    activeShell.setData( "activeShell" );
    activeShell.open();
    String displayId = DisplayUtil.getId( display );
    String shellToActivateId = WidgetUtil.getId( shellToActivate );
    // Set precondition and assert it
    PhaseListenerRegistry.add( new PreserveWidgetsPhaseListener() );
    activeShell.setActive();
    assertSame( activeShell, display.getActiveShell() );
    // Simulate shell activation without event listeners
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( displayId + ".activeShell", shellToActivateId );
    RWTFixture.executeLifeCycleFromServerThread( );
    assertSame( shellToActivate, display.getActiveShell() );
    // Set precondition and assert it
    RWTFixture.markInitialized( activeShell );
    RWTFixture.markInitialized( shellToActivate );
    activeShell.setActive();
    assertSame( activeShell, display.getActiveShell() );

    // Simulate shell activation with event listeners
    ActivateEvent.addListener( shellToActivate, activateListener );
    ActivateEvent.addListener( activeShell, activateListener );
    shellToActivate.addShellListener( shellListener );
    activeShell.addShellListener( shellListener );
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId  );
    Fixture.fakeRequestParam( JSConst.EVENT_SHELL_ACTIVATED,
                              shellToActivateId );
    RWTFixture.executeLifeCycleFromServerThread( );
    assertSame( shellToActivate, display.getActiveShell() );
    String expected = "deactivated:activeShell|activated:shellToActivate|";
    assertEquals( expected, activateEventLog.toString() );
    assertEquals( expected, shellEventLog.toString() );
    // Ensure that no setActive javaScript code is rendered for client-side
    // activated Shell
    assertEquals( -1, Fixture.getAllMarkup().indexOf( "setActive" ) );
  }

  public void testDisposeSingleShell() {
    Display display = new Display();
    Shell shell = new Shell( display );
    shell.open();
    String displayId = DisplayUtil.getId( display );
    String shellId = WidgetUtil.getId( shell );
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_SHELL_CLOSED, shellId );
    RWTFixture.executeLifeCycleFromServerThread( );
    assertEquals( 0, display.getShells().length );
    assertEquals( null, display.getActiveShell() );
    assertEquals( true, shell.isDisposed() );
  }

  public void testAlpha() throws Exception {
    Display display = new Display();
    Shell shell = new Shell( display );
    shell.setAlpha( 23 );
    shell.open();
    AbstractWidgetLCA lca = WidgetUtil.getLCA( shell );
    Fixture.fakeResponseWriter();
    lca.renderInitialization( shell );
    lca.renderChanges( shell );
    String expected = "w.setOpacity( 0.09 );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
    //
    Fixture.fakeResponseWriter();
    shell.setAlpha( 250 );
    lca.renderChanges( shell );
    expected = "w.setOpacity( 0.98 );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }

  private static Control getActiveControl( final Shell shell ) {
    Object adapter = shell.getAdapter( IShellAdapter.class );
    IShellAdapter shellAdapter = ( IShellAdapter )adapter;
    return shellAdapter.getActiveControl();
  }

  private static void setActiveControl( final Shell shell, final Control control )
  {
    Object adapter = shell.getAdapter( IShellAdapter.class );
    IShellAdapter shellAdapter = ( IShellAdapter )adapter;
    shellAdapter.setActiveControl( control );
  }
}
