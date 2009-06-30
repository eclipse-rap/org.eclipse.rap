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

package org.eclipse.swt.internal.widgets.linkkit;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.lifecycle.JSConst;
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

public class LinkLCA_Test extends TestCase {

  public void testPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Link link = new Link( shell, SWT.NONE );
    RWTFixture.markInitialized( display );
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( link );
    assertEquals( "", adapter.getPreserved( Props.TEXT ) );
    Boolean hasListeners
      = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    RWTFixture.clearPreserved();
    link.setText( "some text" );
    link.addSelectionListener( new SelectionListener() {

      public void widgetDefaultSelected( final SelectionEvent e ) {
      }

      public void widgetSelected( final SelectionEvent e ) {
      }
    } );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( link );
    assertEquals( "some text", adapter.getPreserved( Props.TEXT ) );
    hasListeners
      = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    RWTFixture.clearPreserved();
    //control: enabled
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( link );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    RWTFixture.clearPreserved();
    link.setEnabled( false );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( link );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    RWTFixture.clearPreserved();
    //visible
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( link );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    RWTFixture.clearPreserved();
    link.setVisible( false );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( link );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    RWTFixture.clearPreserved();
    //menu
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( link );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    RWTFixture.clearPreserved();
    Menu menu = new Menu( link );
    MenuItem item = new MenuItem( menu, SWT.NONE );
    item.setText( "1 Item" );
    link.setMenu( menu );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( link );
    assertEquals( menu, adapter.getPreserved( Props.MENU ) );
    RWTFixture.clearPreserved();
    //bound
    Rectangle rectangle = new Rectangle( 10, 10, 30, 50 );
    link.setBounds( rectangle );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( link );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    RWTFixture.clearPreserved();
    //control_listeners
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( link );
    hasListeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    RWTFixture.clearPreserved();
    link.addControlListener( new ControlListener() {

      public void controlMoved( final ControlEvent e ) {
      }

      public void controlResized( final ControlEvent e ) {
      }
    } );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( link );
    hasListeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    RWTFixture.clearPreserved();
    //z-index
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( link );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    RWTFixture.clearPreserved();
    //foreground background font
    Color background = Graphics.getColor( 122, 33, 203 );
    link.setBackground( background );
    Color foreground = Graphics.getColor( 211, 178, 211 );
    link.setForeground( foreground );
    Font font = Graphics.getFont( "font", 12, SWT.BOLD );
    link.setFont( font );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( link );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    RWTFixture.clearPreserved();
    //tab_index
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( link );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    RWTFixture.clearPreserved();
    //tooltiptext
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( link );
    assertEquals( null, link.getToolTipText() );
    RWTFixture.clearPreserved();
    link.setToolTipText( "some text" );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( link );
    assertEquals( "some text", link.getToolTipText() );
    RWTFixture.clearPreserved();
    //activate_listeners   Focus_listeners
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( link );
    hasListeners = ( Boolean )adapter.getPreserved( Props.FOCUS_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    RWTFixture.clearPreserved();
    link.addFocusListener( new FocusListener() {

      public void focusGained( final FocusEvent event ) {
      }

      public void focusLost( final FocusEvent event ) {
      }
    } );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( link );
    hasListeners = ( Boolean )adapter.getPreserved( Props.FOCUS_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( link );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    RWTFixture.clearPreserved();
    ActivateEvent.addListener( link, new ActivateAdapter() {
    } );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( link );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    RWTFixture.clearPreserved();
    display.dispose();
  }

  public void testSelectionEvent() {
    final StringBuffer log = new StringBuffer();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    final Link link = new Link( shell, SWT.NONE );
    link.setText( "Big <a>Bang</a>" );
    link.addSelectionListener( new SelectionAdapter() {

      public void widgetSelected( final SelectionEvent event ) {
        log.append( "selectionEvent" );
        assertSame( link, event.getSource() );
        assertEquals( 0, event.detail );
        assertEquals( 0, event.x );
        assertEquals( 0, event.y );
        assertEquals( 0, event.width );
        assertEquals( 0, event.height );
        assertEquals( null, event.item );
        assertEquals( true, event.doit );
      }
    } );
    String linkId = WidgetUtil.getId( link );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, linkId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED + ".index", "0" );
    RWTFixture.readDataAndProcessAction( link );
    assertEquals( "selectionEvent", log.toString() );
  }

  public void testIllegalSelectionEvent() {
    // Selection event should not fire if index out of bounds (see bug 252354)
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Link link = new Link( shell, SWT.NONE );
    link.setText( "No Link" );
    link.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        fail( "Should not be fired" );
      }
    } );
    String linkId = WidgetUtil.getId( link );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, linkId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED + ".index", "0" );
    RWTFixture.readDataAndProcessAction( link );
  }

  public void testRender() throws Exception {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    final Link link = new Link( shell, SWT.NONE );
    link.setText( "Big <a><b>Bang</b></a>" );
    Fixture.fakeResponseWriter();
    LinkLCA lca = new LinkLCA();
    lca.renderChanges( link );
    String markup = Fixture.getAllMarkup();
    assertContains( "clear()", markup );
    assertContains( "addText( \"Big \" )", markup );
    assertContains( "addLink( \"&lt;b&gt;Bang&lt;/b&gt;\", 0 )", markup );
    assertContains( "applyText()", markup );
  }

  public void testEscape() throws Exception {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Link link1 = new Link( shell, SWT.NONE );
    Link link2 = new Link( shell, SWT.NONE );
    link1.setText( "test.<" );
    link2.setText( "&E<s>ca'pe\" && me" );
    Fixture.fakeResponseWriter();
    LinkLCA lca = new LinkLCA();
    lca.renderChanges( link1 );
    lca.renderChanges( link2 );
    String actual = Fixture.getAllMarkup();
    String expected1 = "\"test.&lt;\"";
    assertTrue( actual.indexOf( expected1 ) != -1 );
    String expected2 = "\"E&lt;s&gt;ca'pe&quot; &amp; me\"";
    assertTrue( actual.indexOf( expected2 ) != -1 );
  }

  private void assertContains( final String expected, final String string ) {
    String message = "'" + expected + "' not contained in '" + string + "'";
    assertTrue( message, string.indexOf( expected ) != -1 );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
