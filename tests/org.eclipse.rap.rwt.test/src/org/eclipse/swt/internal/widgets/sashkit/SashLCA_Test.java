/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.sashkit;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.WidgetUtil;
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
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( sash );
    Object[] Listeners;
    Listeners = ( Object[] )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( 0, Listeners.length );
    SelectionListener listener = new SelectionAdapter() {

      public void widgetSelected( SelectionEvent event ) {
      }
    };
    sash.addSelectionListener( listener );
    Fixture.preserveWidgets();
    Listeners = ( Object[] )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( 1, Listeners.length );
    assertEquals( listener, Listeners[ 0 ] );
    //control: enabled
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    sash.setEnabled( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    sash.setEnabled( true );
    //visible
    sash.setSize( 10, 10 );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    sash.setVisible( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    //menu
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    Menu menu = new Menu( sash );
    MenuItem item = new MenuItem( menu, SWT.NONE );
    item.setText( "1 Item" );
    sash.setMenu( menu );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    assertEquals( menu, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    //bound
    Rectangle rectangle = new Rectangle( 10, 10, 30, 50 );
    sash.setBounds( rectangle );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    Fixture.clearPreserved();
    //control_listeners
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    Boolean hasListeners
     = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    sash.addControlListener( new ControlListener() {

      public void controlMoved( final ControlEvent e ) {
      }

      public void controlResized( final ControlEvent e ) {
      }
    } );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    hasListeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    Fixture.clearPreserved();
    //z-index
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    Fixture.clearPreserved();
    //foreground background font
    Color background = Graphics.getColor( 122, 33, 203 );
    sash.setBackground( background );
    Color foreground = Graphics.getColor( 211, 178, 211 );
    sash.setForeground( foreground );
    Font font = Graphics.getFont( "font", 12, SWT.BOLD );
    sash.setFont( font );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    Fixture.clearPreserved();
    //tab_index
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    Fixture.clearPreserved();
    //tooltiptext
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    assertEquals( null, sash.getToolTipText() );
    Fixture.clearPreserved();
    sash.setToolTipText( "some text" );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    assertEquals( "some text", sash.getToolTipText() );
    Fixture.clearPreserved();
    //activate_listeners   Focus_listeners
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    hasListeners = ( Boolean )adapter.getPreserved( Props.FOCUS_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    sash.addFocusListener( new FocusListener() {

      public void focusGained( final FocusEvent event ) {
      }

      public void focusLost( final FocusEvent event ) {
      }
    } );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    hasListeners = ( Boolean )adapter.getPreserved( Props.FOCUS_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    ActivateEvent.addListener( sash, new ActivateAdapter() {
    } );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
  }

  public void testRenderChanges() throws IOException {
    Fixture.fakeResponseWriter();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Sash sash = new Sash( shell, SWT.NONE );
    shell.open();
    Fixture.markInitialized( display );
    Fixture.markInitialized( sash );
    Fixture.preserveWidgets();
    sash.setBounds( new Rectangle( 20, 100, 50, 60 ) );
    SashLCA sashLCA = new SashLCA();
    sashLCA.renderChanges( sash );
    assertTrue( Fixture.getAllMarkup()
      .indexOf( "setSpace( 20, 50, 100, 60 );" ) != -1 );
    Fixture.clearPreserved();
    Fixture.fakeResponseWriter();
    Fixture.preserveWidgets();
    sashLCA.renderChanges( sash );
    assertEquals( "", Fixture.getAllMarkup() );
  }

  public void testSelectionEvent() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    final Sash sash = new Sash( shell, SWT.NONE );
    final StringBuffer log = new StringBuffer();
    SelectionListener selectionListener = new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        assertEquals( sash, event.getSource() );
        assertEquals( null, event.item );
        assertEquals( 0, event.x );
        assertEquals( 0, event.y );
        assertEquals( 0, event.width );
        assertEquals( 0, event.height );
        assertEquals( 0, event.stateMask );
        assertEquals( SWT.DRAG, event.detail );
        assertEquals( true, event.doit );
        log.append( "widgetSelected" );
      }
    };
    sash.addSelectionListener( selectionListener );
    String sashId = WidgetUtil.getId( sash );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, sashId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED + ".detail",
                              "drag" );
    Fixture.readDataAndProcessAction( sash );
    assertEquals( "widgetSelected", log.toString() );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
