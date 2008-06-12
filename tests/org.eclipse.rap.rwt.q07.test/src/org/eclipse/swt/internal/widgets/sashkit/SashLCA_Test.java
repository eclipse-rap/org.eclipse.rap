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

package org.eclipse.swt.internal.widgets.sashkit;

import java.io.IOException;
import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.events.ActivateAdapter;
import org.eclipse.swt.internal.events.ActivateEvent;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;

public class SashLCA_Test extends TestCase {

  public void testPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Sash sash = new Sash( shell, SWT.NONE );
    RWTFixture.markInitialized( display );
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( sash );
    Object[] Listeners;
    Listeners = ( Object[] )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( 0, Listeners.length );
    SelectionListener listener = new SelectionAdapter() {

      public void widgetSelected( SelectionEvent event ) {
      }
    };
    sash.addSelectionListener( listener );
    RWTFixture.preserveWidgets();
    Listeners = ( Object[] )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( 1, Listeners.length );
    assertEquals( listener, Listeners[ 0 ] );
    //control: enabled
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    RWTFixture.clearPreserved();
    sash.setEnabled( false );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    RWTFixture.clearPreserved();
    //visible
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    RWTFixture.clearPreserved();
    sash.setVisible( false );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    RWTFixture.clearPreserved();
    //menu
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    RWTFixture.clearPreserved();
    Menu menu = new Menu( sash );
    MenuItem item = new MenuItem( menu, SWT.NONE );
    item.setText( "1 Item" );
    sash.setMenu( menu );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    assertEquals( menu, adapter.getPreserved( Props.MENU ) );
    RWTFixture.clearPreserved();
    //bound
    Rectangle rectangle = new Rectangle( 10, 10, 30, 50 );
    sash.setBounds( rectangle );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    RWTFixture.clearPreserved();
    //control_listeners
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    Boolean hasListeners
     = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    RWTFixture.clearPreserved();
    sash.addControlListener( new ControlListener() {

      public void controlMoved( final ControlEvent e ) {
      }

      public void controlResized( final ControlEvent e ) {
      }
    } );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    hasListeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    RWTFixture.clearPreserved();
    //z-index
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    RWTFixture.clearPreserved();
    //foreground background font
    Color background = Graphics.getColor( 122, 33, 203 );
    sash.setBackground( background );
    Color foreground = Graphics.getColor( 211, 178, 211 );
    sash.setForeground( foreground );
    Font font = Graphics.getFont( "font", 12, SWT.BOLD );
    sash.setFont( font );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    RWTFixture.clearPreserved();
    //tab_index
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    RWTFixture.clearPreserved();
    //tooltiptext
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    assertEquals( null, sash.getToolTipText() );
    RWTFixture.clearPreserved();
    sash.setToolTipText( "some text" );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    assertEquals( "some text", sash.getToolTipText() );
    RWTFixture.clearPreserved();
    //activate_listeners   Focus_listeners
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    hasListeners = ( Boolean )adapter.getPreserved( Props.FOCUS_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    RWTFixture.clearPreserved();
    sash.addFocusListener( new FocusListener() {

      public void focusGained( final FocusEvent event ) {
      }

      public void focusLost( final FocusEvent event ) {
      }
    } );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    hasListeners = ( Boolean )adapter.getPreserved( Props.FOCUS_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    RWTFixture.clearPreserved();
    ActivateEvent.addListener( sash, new ActivateAdapter() {
    } );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    RWTFixture.clearPreserved();
    display.dispose();
  }

  public void testRenderChanges() throws IOException {
    Fixture.fakeResponseWriter();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Sash sash = new Sash( shell, SWT.NONE );
    shell.open();
    RWTFixture.markInitialized( display );
    RWTFixture.markInitialized( sash );
    RWTFixture.preserveWidgets();
    sash.setBounds( new Rectangle( 20, 100, 50, 60 ) );
    SashLCA sashLCA = new SashLCA();
    sashLCA.renderChanges( sash );
    assertTrue( Fixture.getAllMarkup()
      .endsWith( "setSpace( 20, 50, 100, 60 );" ) );
    RWTFixture.clearPreserved();
    Fixture.fakeResponseWriter();
    RWTFixture.preserveWidgets();
    sashLCA.renderChanges( sash );
    assertEquals( "", Fixture.getAllMarkup() );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
