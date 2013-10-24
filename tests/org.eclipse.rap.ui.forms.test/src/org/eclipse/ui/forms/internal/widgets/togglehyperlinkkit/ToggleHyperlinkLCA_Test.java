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

import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.eclipse.rap.json.*;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectRegistry;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.testfixture.*;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.internal.widgets.FormsControlLCA_AbstractTest;
import org.eclipse.ui.forms.widgets.Twistie;
import org.junit.Test;

@SuppressWarnings("restriction")
public class ToggleHyperlinkLCA_Test extends FormsControlLCA_AbstractTest {

  private Twistie twistie;
  private ToggleHyperlinkLCA lca;

  @Override
  protected void setUp() {
    super.setUp();
    twistie = new Twistie( shell, SWT.NONE );
    lca = new ToggleHyperlinkLCA();
    Fixture.fakeNewRequest();
  }

  public void testRenderCreate() throws IOException {
    lca.renderInitialization( twistie );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( twistie );
    assertEquals( "forms.widgets.ToggleHyperlink", operation.getType() );
  }

  @Test
  public void testRenderInitialization_setsOperationHandler() throws IOException {
    String id = getId( twistie );
    lca.renderInitialization( twistie );

    OperationHandler handler = RemoteObjectRegistry.getInstance().get( id ).getHandler();
    assertTrue( handler instanceof ToggleHyperlinkOperationHandler );
  }

  @Test
  public void testReadData_usesOperationHandler() {
    ToggleHyperlinkOperationHandler handler = spy( new ToggleHyperlinkOperationHandler( twistie ) );
    getRemoteObject( getId( twistie ) ).setHandler( handler );

    Fixture.fakeNotifyOperation( getId( twistie ), "Help", new JsonObject() );
    lca.readData( twistie );

    verify( handler ).handleNotifyHelp( twistie, new JsonObject() );
  }

  public void testRenderParent() throws IOException {
    lca.renderInitialization( twistie );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( twistie );
    assertEquals( WidgetUtil.getId( twistie.getParent() ), operation.getParent() );
  }

  public void testRenderImages() throws IOException {
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
    lca.renderChanges( twistie );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( twistie, "DefaultSelection" ) );
  }

  public void testRenderSelectionListenerUnchanged() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( twistie );
    Fixture.preserveWidgets();

    twistie.addListener( SWT.DefaultSelection, mock( Listener.class ) );
    Fixture.preserveWidgets();
    lca.renderChanges( twistie );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( twistie, "DefaultSelection" ) );
  }

  public void testRenderInitialExpanded() throws IOException {
    lca.renderChanges( twistie );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( twistie, "expanded" ) );
  }

  public void testRenderExpanded() throws IOException {
    twistie.setExpanded( true );
    lca.renderChanges( twistie );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findSetProperty( twistie, "expanded" ) );
  }

  public void testRenderExpandedUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( twistie );

    twistie.setExpanded( true );
    Fixture.preserveWidgets();
    lca.renderChanges( twistie );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( twistie, "expanded" ) );
  }

}
