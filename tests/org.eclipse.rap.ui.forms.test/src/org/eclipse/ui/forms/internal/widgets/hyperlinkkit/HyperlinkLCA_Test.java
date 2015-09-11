/*******************************************************************************
 * Copyright (c) 2009, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.ui.forms.internal.widgets.hyperlinkkit;

import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;
import static org.eclipse.rap.rwt.testfixture.internal.TestMessage.getParent;
import static org.eclipse.rap.rwt.testfixture.internal.TestMessage.getStyles;
import static org.eclipse.rap.rwt.widgets.WidgetUtil.getId;
import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.eclipse.rap.json.*;
import org.eclipse.rap.rwt.internal.protocol.Operation.CreateOperation;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectRegistry;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.TestMessage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.forms.HyperlinkSettings;
import org.eclipse.ui.forms.internal.widgets.IHyperlinkAdapter;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.junit.*;


@SuppressWarnings("restriction")
public class HyperlinkLCA_Test {

  @Rule
  public TestContext context = new TestContext();

  private Display display;
  private Shell shell;
  private Hyperlink hyperlink;
  private HyperlinkLCA lca;

  @Before
  public void setUp() {
    display = new Display();
    shell = new Shell( display );
    hyperlink = new Hyperlink( shell, SWT.NONE );
    lca = new HyperlinkLCA();
  }

  @Test
  public void testRenderCreate() throws IOException {
    lca.renderInitialization( hyperlink );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( hyperlink );
    assertEquals( "forms.widgets.Hyperlink", operation.getType() );
  }

  @Test
  public void testRenderCreateWithWrap() throws IOException {
    hyperlink = new Hyperlink( shell, SWT.WRAP );

    lca.renderInitialization( hyperlink );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( hyperlink );
    assertEquals( "forms.widgets.Hyperlink", operation.getType() );
    assertTrue( getStyles( operation ).contains( "WRAP" ) );
  }

  @Test
  public void testRenderInitialization_setsOperationHandler() throws IOException {
    String id = getId( hyperlink );
    lca.renderInitialization( hyperlink );

    OperationHandler handler = RemoteObjectRegistry.getInstance().get( id ).getHandler();
    assertTrue( handler instanceof HyperlinkOperationHandler );
  }

  @Test
  public void testReadData_usesOperationHandler() {
    HyperlinkOperationHandler handler = spy( new HyperlinkOperationHandler( hyperlink ) );
    getRemoteObject( getId( hyperlink ) ).setHandler( handler );

    Fixture.fakeNotifyOperation( getId( hyperlink ), "Help", new JsonObject() );
    lca.readData( hyperlink );

    verify( handler ).handleNotifyHelp( hyperlink, new JsonObject() );
  }

  @Test
  public void testRenderParent() throws IOException {
    lca.renderInitialization( hyperlink );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( hyperlink );
    assertEquals( getId( hyperlink.getParent() ), getParent( operation ) );
  }

  @Test
  public void testRenderInitialText() throws IOException {
    lca.renderChanges( hyperlink );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( hyperlink, "text" ) );
  }

  @Test
  public void testRenderText() throws IOException {
    hyperlink.setText( "test" );
    lca.renderChanges( hyperlink );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "test", message.findSetProperty( hyperlink, "text" ).asString() );
  }

  @Test
  public void testRenderTextUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( hyperlink );

    hyperlink.setText( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( hyperlink );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( hyperlink, "text" ) );
  }

  @Test
  public void testRenderInitialUnderlined() throws IOException {
    lca.renderChanges( hyperlink );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( hyperlink, "underlined" ) );
  }

  @Test
  public void testRenderUnderlined() throws IOException {
    hyperlink.setUnderlined( true );
    lca.renderChanges( hyperlink );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findSetProperty( hyperlink, "underlined" ) );
  }

  @Test
  public void testRenderUnderlinedUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( hyperlink );

    hyperlink.setUnderlined( true );
    Fixture.preserveWidgets();
    lca.renderChanges( hyperlink );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( hyperlink, "underlined" ) );
  }

  @Test
  public void testRenderInitialUnderlineMode() throws IOException {
    lca.renderChanges( hyperlink );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( hyperlink, "underlineMode" ) );
  }

  @Test
  public void testRenderUnderlineMode() throws IOException {
    getAdapter( hyperlink ).setUnderlineMode( HyperlinkSettings.UNDERLINE_HOVER );
    lca.renderChanges( hyperlink );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 2, message.findSetProperty( hyperlink, "underlineMode" ).asInt() );
  }

  @Test
  public void testRenderUnderlineModeUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( hyperlink );

    getAdapter( hyperlink ).setUnderlineMode( HyperlinkSettings.UNDERLINE_HOVER );
    Fixture.preserveWidgets();
    lca.renderChanges( hyperlink );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( hyperlink, "underlineMode" ) );
  }

  @Test
  public void testRenderInitialActiveBackground() throws IOException {
    lca.render( hyperlink );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( hyperlink );
    assertFalse( operation.getProperties().names().contains( "activeBackground" ) );
  }

  @Test
  public void testRenderActiveBackground() throws IOException {
    getAdapter( hyperlink ).setActiveBackground( display.getSystemColor( SWT.COLOR_GREEN ) );
    lca.renderChanges( hyperlink );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray actual = ( JsonArray )message.findSetProperty( hyperlink, "activeBackground" );
    assertEquals( 0, actual.get( 0 ).asInt() );
    assertEquals( 255, actual.get( 1 ).asInt() );
    assertEquals( 0, actual.get( 2 ).asInt() );
    assertEquals( 255, actual.get( 3 ).asInt() );
  }

  @Test
  public void testRenderActiveBackgroundUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( hyperlink );

    getAdapter( hyperlink ).setActiveBackground( display.getSystemColor( SWT.COLOR_GREEN ) );
    Fixture.preserveWidgets();
    lca.renderChanges( hyperlink );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( hyperlink, "activeBackground" ) );
  }

  @Test
  public void testRenderInitialActiveForeground() throws IOException {
    lca.render( hyperlink );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( hyperlink );
    assertFalse( operation.getProperties().names().contains( "activeForeground" ) );
  }

  @Test
  public void testRenderActiveForeground() throws IOException {
    getAdapter( hyperlink ).setActiveForeground( display.getSystemColor( SWT.COLOR_GREEN ) );
    lca.renderChanges( hyperlink );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray actual = ( JsonArray )message.findSetProperty( hyperlink, "activeForeground" );
    assertEquals( 0, actual.get( 0 ).asInt() );
    assertEquals( 255, actual.get( 1 ).asInt() );
    assertEquals( 0, actual.get( 2 ).asInt() );
    assertEquals( 255, actual.get( 3 ).asInt() );
  }

  @Test
  public void testRenderActiveForegroundUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( hyperlink );

    getAdapter( hyperlink ).setActiveForeground( display.getSystemColor( SWT.COLOR_GREEN ) );
    Fixture.preserveWidgets();
    lca.renderChanges( hyperlink );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( hyperlink, "activeForeground" ) );
  }

  @Test
  public void testRenderAddSelectionListener() throws Exception {
    lca.renderChanges( hyperlink );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( hyperlink, "DefaultSelection" ) );
  }

  @Test
  @SuppressWarnings("serial")
  public void testRenderSelectionListenerUnchanged() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( hyperlink );
    Fixture.preserveWidgets();

    hyperlink.addListener( SWT.DefaultSelection, new Listener() {
      @Override
      public void handleEvent( Event event ) {
      }
    } );
    Fixture.preserveWidgets();
    lca.renderChanges( hyperlink );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( hyperlink, "DefaultSelection" ) );
  }

  private IHyperlinkAdapter getAdapter( Hyperlink hyperlink ) {
    return hyperlink.getAdapter( IHyperlinkAdapter.class );
  }

}
