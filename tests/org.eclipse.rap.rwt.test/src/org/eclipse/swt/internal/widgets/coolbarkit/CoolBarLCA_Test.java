/*******************************************************************************
 * Copyright (c) 2002, 2014 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.coolbarkit;

import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectRegistry;
import org.eclipse.rap.rwt.internal.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetAdapter;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rap.rwt.testfixture.Message.SetOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public final class CoolBarLCA_Test {

  private Display display;
  private Shell shell;
  private CoolBar bar;
  private CoolBarLCA lca;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    bar = new CoolBar( shell, SWT.FLAT );
    lca = new CoolBarLCA();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testControlListeners() throws IOException {
    ControlLCATestUtil.testActivateListener( bar );
    ControlLCATestUtil.testMouseListener( bar );
    ControlLCATestUtil.testKeyListener( bar );
    ControlLCATestUtil.testTraverseListener( bar );
    ControlLCATestUtil.testMenuDetectListener( bar );
    ControlLCATestUtil.testHelpListener( bar );
  }

  @Test
  public void testPreserveValues() {
    Fixture.markInitialized( bar );
    AbstractWidgetLCA lca = WidgetUtil.getLCA( bar );
    lca.preserveValues( bar );
    WidgetAdapter adapter = WidgetUtil.getAdapter( bar );
    assertEquals( Boolean.FALSE, adapter.getPreserved( CoolBarLCA.PROP_LOCKED ) );
    Fixture.clearPreserved();
    bar.setLocked( true );
    lca.preserveValues( bar );
    adapter = WidgetUtil.getAdapter( bar );
    assertEquals( Boolean.TRUE, adapter.getPreserved( CoolBarLCA.PROP_LOCKED ) );
    Fixture.clearPreserved();
    // control: enabled
    lca.preserveValues( bar );
    adapter = WidgetUtil.getAdapter( bar );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    bar.setEnabled( false );
    lca.preserveValues( bar );
    adapter = WidgetUtil.getAdapter( bar );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    bar.setEnabled( true );
    // visible
    bar.setSize( 10, 10 );
    lca.preserveValues( bar );
    adapter = WidgetUtil.getAdapter( bar );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    bar.setVisible( false );
    lca.preserveValues( bar );
    adapter = WidgetUtil.getAdapter( bar );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    // menu
    lca.preserveValues( bar );
    adapter = WidgetUtil.getAdapter( bar );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    Menu menu = new Menu( bar );
    MenuItem item = new MenuItem( menu, SWT.NONE );
    item.setText( "1 Item" );
    bar.setMenu( menu );
    lca.preserveValues( bar );
    adapter = WidgetUtil.getAdapter( bar );
    assertEquals( menu, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    // bound
    Rectangle rectangle = new Rectangle( 10, 10, 30, 50 );
    bar.setBounds( rectangle );
    lca.preserveValues( bar );
    adapter = WidgetUtil.getAdapter( bar );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    Fixture.clearPreserved();
    // foreground background font
    Color background = new Color( display, 122, 33, 203 );
    bar.setBackground( background );
    Color foreground = new Color( display, 211, 178, 211 );
    bar.setForeground( foreground );
    Font font = new Font( display, "font", 12, SWT.BOLD );
    bar.setFont( font );
    lca.preserveValues( bar );
    adapter = WidgetUtil.getAdapter( bar );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    Fixture.clearPreserved();
    // tooltiptext
    lca.preserveValues( bar );
    adapter = WidgetUtil.getAdapter( bar );
    assertEquals( null, bar.getToolTipText() );
    Fixture.clearPreserved();
    bar.setToolTipText( "some text" );
    lca.preserveValues( bar );
    adapter = WidgetUtil.getAdapter( bar );
    assertEquals( "some text", bar.getToolTipText() );
  }

  @Test
  public void testRenderCreate() throws IOException {
    lca.renderInitialization( bar );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( bar );
    assertEquals( "rwt.widgets.CoolBar", operation.getType() );
  }

  @Test
  public void testRenderInitialization_setsOperationHandler() throws IOException {
    String id = getId( bar );
    lca.renderInitialization( bar );

    OperationHandler handler = RemoteObjectRegistry.getInstance().get( id ).getHandler();
    assertTrue( handler instanceof CoolBarOperationHandler );
  }

  @Test
  public void testReadData_usesOperationHandler() {
    CoolBarOperationHandler handler = spy( new CoolBarOperationHandler( bar ) );
    getRemoteObject( getId( bar ) ).setHandler( handler );

    Fixture.fakeNotifyOperation( getId( bar ), "Help", new JsonObject() );
    lca.readData( bar );

    verify( handler ).handleNotifyHelp( bar, new JsonObject() );
  }

  @Test
  public void testRenderParent() throws IOException {
    lca.renderInitialization( bar );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( bar );
    assertEquals( WidgetUtil.getId( bar.getParent() ), operation.getParent() );
  }

  @Test
  public void testRenderLocked() throws IOException {
    lca.preserveValues( bar );
    bar.setLocked( true );
    lca.renderChanges( bar );

    Message message = Fixture.getProtocolMessage();
    SetOperation operation = message.findSetOperation( bar, "locked" );
    assertEquals( JsonValue.TRUE, operation.getProperty( "locked" ) );
  }

}
