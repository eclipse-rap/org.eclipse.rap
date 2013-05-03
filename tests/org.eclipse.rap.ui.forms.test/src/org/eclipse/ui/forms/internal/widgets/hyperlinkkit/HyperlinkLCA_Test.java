/*******************************************************************************
 * Copyright (c) 2009, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.ui.forms.internal.widgets.hyperlinkkit;

import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;

import java.io.IOException;
import java.util.Arrays;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.*;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.HyperlinkSettings;
import org.eclipse.ui.forms.internal.widgets.FormsControlLCA_AbstractTest;
import org.eclipse.ui.forms.internal.widgets.IHyperlinkAdapter;
import org.eclipse.ui.forms.widgets.Hyperlink;

@SuppressWarnings("restriction")
public class HyperlinkLCA_Test extends FormsControlLCA_AbstractTest {

  private Hyperlink hyperlink;
  private HyperlinkLCA lca;

  @Override
  protected void setUp() {
    super.setUp();
    hyperlink = new Hyperlink( shell, SWT.NONE );
    lca = new HyperlinkLCA();
    Fixture.fakeNewRequest();
  }

  public void testSelectionEvent() {
    Hyperlink hyperlink = new Hyperlink( shell, SWT.NONE );
    testDefaultSelectionEvent( hyperlink );
  }

  @SuppressWarnings("serial")
  private void testDefaultSelectionEvent( final Hyperlink hyperlink ) {
    final StringBuffer log = new StringBuffer();
    Listener listener = new Listener() {
      public void handleEvent( Event event ) {
        assertEquals( hyperlink, event.widget );
        assertEquals( null, event.item );
        assertEquals( SWT.NONE, event.detail );
        assertEquals( 0, event.x );
        assertEquals( 0, event.y );
        assertEquals( 0, event.width );
        assertEquals( 0, event.height );
        assertEquals( true, event.doit );
        log.append( "widgetDefaultSelected" );
      }
    };
    hyperlink.addListener( SWT.DefaultSelection, listener );
    Fixture.fakeNotifyOperation( getId( hyperlink ), ClientMessageConst.EVENT_DEFAULT_SELECTION, null );
    Fixture.readDataAndProcessAction( hyperlink );
    assertEquals( "widgetDefaultSelected", log.toString() );
  }

  public void testRenderCreate() throws IOException {
    lca.renderInitialization( hyperlink );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( hyperlink );
    assertEquals( "forms.widgets.Hyperlink", operation.getType() );
  }

  public void testRenderCreateWithWrap() throws IOException {
    hyperlink = new Hyperlink( shell, SWT.WRAP );

    lca.renderInitialization( hyperlink );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( hyperlink );
    assertEquals( "forms.widgets.Hyperlink", operation.getType() );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "WRAP" ) );
  }

  public void testRenderParent() throws IOException {
    lca.renderInitialization( hyperlink );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( hyperlink );
    assertEquals( WidgetUtil.getId( hyperlink.getParent() ), operation.getParent() );
  }

  public void testRenderInitialText() throws IOException {
    lca.renderChanges( hyperlink );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( hyperlink, "text" ) );
  }

  public void testRenderText() throws IOException {
    hyperlink.setText( "test" );
    lca.renderChanges( hyperlink );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "test", message.findSetProperty( hyperlink, "text" ).asString() );
  }

  public void testRenderTextUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( hyperlink );

    hyperlink.setText( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( hyperlink );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( hyperlink, "text" ) );
  }

  public void testRenderInitialUnderlined() throws IOException {
    lca.renderChanges( hyperlink );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( hyperlink, "underlined" ) );
  }

  public void testRenderUnderlined() throws IOException {
    hyperlink.setUnderlined( true );
    lca.renderChanges( hyperlink );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findSetProperty( hyperlink, "underlined" ) );
  }

  public void testRenderUnderlinedUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( hyperlink );

    hyperlink.setUnderlined( true );
    Fixture.preserveWidgets();
    lca.renderChanges( hyperlink );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( hyperlink, "underlined" ) );
  }

  public void testRenderInitialUnderlineMode() throws IOException {
    lca.renderChanges( hyperlink );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( hyperlink, "underlineMode" ) );
  }

  public void testRenderUnderlineMode() throws IOException {
    getAdapter( hyperlink ).setUnderlineMode( HyperlinkSettings.UNDERLINE_HOVER );
    lca.renderChanges( hyperlink );

    Message message = Fixture.getProtocolMessage();
    assertEquals( 2, message.findSetProperty( hyperlink, "underlineMode" ).asInt() );
  }

  public void testRenderUnderlineModeUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( hyperlink );

    getAdapter( hyperlink ).setUnderlineMode( HyperlinkSettings.UNDERLINE_HOVER );
    Fixture.preserveWidgets();
    lca.renderChanges( hyperlink );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( hyperlink, "underlineMode" ) );
  }

  public void testRenderInitialActiveBackground() throws IOException {
    lca.render( hyperlink );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( hyperlink );
    assertTrue( operation.getPropertyNames().indexOf( "activeBackground" ) == -1 );
  }

  public void testRenderActiveBackground() throws IOException {
    getAdapter( hyperlink ).setActiveBackground( display.getSystemColor( SWT.COLOR_GREEN ) );
    lca.renderChanges( hyperlink );

    Message message = Fixture.getProtocolMessage();
    JsonArray actual = ( JsonArray )message.findSetProperty( hyperlink, "activeBackground" );
    assertEquals( 0, actual.get( 0 ).asInt() );
    assertEquals( 255, actual.get( 1 ).asInt() );
    assertEquals( 0, actual.get( 2 ).asInt() );
    assertEquals( 255, actual.get( 3 ).asInt() );
  }

  public void testRenderActiveBackgroundUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( hyperlink );

    getAdapter( hyperlink ).setActiveBackground( display.getSystemColor( SWT.COLOR_GREEN ) );
    Fixture.preserveWidgets();
    lca.renderChanges( hyperlink );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( hyperlink, "activeBackground" ) );
  }

  public void testRenderInitialActiveForeground() throws IOException {
    lca.render( hyperlink );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( hyperlink );
    assertTrue( operation.getPropertyNames().indexOf( "activeForeground" ) == -1 );
  }

  public void testRenderActiveForeground() throws IOException {
    getAdapter( hyperlink ).setActiveForeground( display.getSystemColor( SWT.COLOR_GREEN ) );
    lca.renderChanges( hyperlink );

    Message message = Fixture.getProtocolMessage();
    JsonArray actual = ( JsonArray )message.findSetProperty( hyperlink, "activeForeground" );
    assertEquals( 0, actual.get( 0 ).asInt() );
    assertEquals( 255, actual.get( 1 ).asInt() );
    assertEquals( 0, actual.get( 2 ).asInt() );
    assertEquals( 255, actual.get( 3 ).asInt() );
  }

  public void testRenderActiveForegroundUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( hyperlink );

    getAdapter( hyperlink ).setActiveForeground( display.getSystemColor( SWT.COLOR_GREEN ) );
    Fixture.preserveWidgets();
    lca.renderChanges( hyperlink );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( hyperlink, "activeForeground" ) );
  }

  public void testRenderAddSelectionListener() throws Exception {
    lca.renderChanges( hyperlink );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( hyperlink, "DefaultSelection" ) );
  }

  @SuppressWarnings("serial")
  public void testRenderSelectionListenerUnchanged() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( hyperlink );
    Fixture.preserveWidgets();

    hyperlink.addListener( SWT.DefaultSelection, new Listener() {
      public void handleEvent( Event event ) {
      }
    } );
    Fixture.preserveWidgets();
    lca.renderChanges( hyperlink );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( hyperlink, "DefaultSelection" ) );
  }

  private IHyperlinkAdapter getAdapter( Hyperlink hyperlink ) {
    return hyperlink.getAdapter( IHyperlinkAdapter.class );
  }

}
