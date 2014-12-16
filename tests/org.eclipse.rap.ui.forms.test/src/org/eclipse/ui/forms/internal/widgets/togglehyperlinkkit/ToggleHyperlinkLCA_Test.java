/*******************************************************************************
 * Copyright (c) 2009, 2014 EclipseSource and others.
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
import static org.eclipse.rap.rwt.testfixture.internal.TestMessage.getParent;
import static org.eclipse.rap.rwt.widgets.WidgetUtil.getId;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.eclipse.rap.json.*;
import org.eclipse.rap.rwt.internal.protocol.Operation.CreateOperation;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectRegistry;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.TestMessage;
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

    TestMessage message = Fixture.getProtocolMessage();
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

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( twistie );
    assertEquals( getId( twistie.getParent() ), getParent( operation ) );
  }

  public void testRenderImages() throws IOException {
    lca.renderInitialization( twistie );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( twistie );
    JsonArray images = operation.getProperties().get( "images" ).asArray();
    assertNotNull( images.get( 0 ) );
    assertNotNull( images.get( 1 ) );
    assertNotNull( images.get( 2 ) );
    assertNotNull( images.get( 3 ) );
  }

  public void testRenderAddSelectionListener() throws Exception {
    lca.renderChanges( twistie );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( twistie, "DefaultSelection" ) );
  }

  public void testRenderSelectionListenerUnchanged() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( twistie );
    Fixture.preserveWidgets();

    twistie.addListener( SWT.DefaultSelection, mock( Listener.class ) );
    Fixture.preserveWidgets();
    lca.renderChanges( twistie );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( twistie, "DefaultSelection" ) );
  }

  public void testRenderInitialExpanded() throws IOException {
    lca.renderChanges( twistie );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( twistie, "expanded" ) );
  }

  public void testRenderExpanded() throws IOException {
    twistie.setExpanded( true );
    lca.renderChanges( twistie );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findSetProperty( twistie, "expanded" ) );
  }

  public void testRenderExpandedUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( twistie );

    twistie.setExpanded( true );
    Fixture.preserveWidgets();
    lca.renderChanges( twistie );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( twistie, "expanded" ) );
  }

}
