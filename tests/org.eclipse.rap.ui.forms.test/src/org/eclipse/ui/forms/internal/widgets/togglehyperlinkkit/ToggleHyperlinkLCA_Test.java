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
package org.eclipse.ui.forms.internal.widgets.togglehyperlinkkit;

import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;

import java.io.IOException;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.*;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.internal.widgets.FormsControlLCA_AbstractTest;
import org.eclipse.ui.forms.widgets.ToggleHyperlink;
import org.eclipse.ui.forms.widgets.Twistie;

@SuppressWarnings("restriction")
public class ToggleHyperlinkLCA_Test extends FormsControlLCA_AbstractTest {

  private ToggleHyperlinkLCA lca;

  @Override
  protected void setUp() {
    super.setUp();
    lca = new ToggleHyperlinkLCA();
    Fixture.fakeNewRequest();
  }

  public void testSelectionEvent() {
    Twistie twistie = new Twistie( shell, SWT.NONE );
    testDefaultSelectionEvent( twistie );
  }

  @SuppressWarnings("serial")
  private void testDefaultSelectionEvent( final ToggleHyperlink hyperlink ) {
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
    Twistie twistie = new Twistie( shell, SWT.NONE );

    lca.renderInitialization( twistie );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( twistie );
    assertEquals( "forms.widgets.ToggleHyperlink", operation.getType() );
  }

  public void testRenderParent() throws IOException {
    Twistie twistie = new Twistie( shell, SWT.NONE );

    lca.renderInitialization( twistie );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( twistie );
    assertEquals( WidgetUtil.getId( twistie.getParent() ), operation.getParent() );
  }

  public void testRenderImages() throws IOException {
    Twistie twistie = new Twistie( shell, SWT.NONE );

    lca.renderInitialization( twistie );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( twistie );
    JsonArray images = operation.getProperty( "images" ).asArray();
    assertNotNull( images.get( 0 ) );
    assertNotNull( images.get( 1 ) );
    assertNotNull( images.get( 2 ) );
    assertNotNull( images.get( 3 ) );
  }

  public void testRenderAddSelectionListener() throws Exception {
    Twistie twistie = new Twistie( shell, SWT.NONE );

    lca.renderChanges( twistie );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( twistie, "DefaultSelection" ) );
  }

  @SuppressWarnings("serial")
  public void testRenderSelectionListenerUnchanged() throws Exception {
    Twistie twistie = new Twistie( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( twistie );
    Fixture.preserveWidgets();

    twistie.addListener( SWT.DefaultSelection, new Listener() {
      public void handleEvent( Event event ) {
      }
    } );
    Fixture.preserveWidgets();
    lca.renderChanges( twistie );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( twistie, "DefaultSelection" ) );
  }

  public void testRenderInitialExpanded() throws IOException {
    Twistie twistie = new Twistie( shell, SWT.NONE );

    lca.renderChanges( twistie );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( twistie, "expanded" ) );
  }

  public void testRenderExpanded() throws IOException {
    Twistie twistie = new Twistie( shell, SWT.NONE );

    twistie.setExpanded( true );
    lca.renderChanges( twistie );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findSetProperty( twistie, "expanded" ) );
  }

  public void testRenderExpandedUnchanged() throws IOException {
    Twistie twistie = new Twistie( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( twistie );

    twistie.setExpanded( true );
    Fixture.preserveWidgets();
    lca.renderChanges( twistie );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( twistie, "expanded" ) );
  }

}
