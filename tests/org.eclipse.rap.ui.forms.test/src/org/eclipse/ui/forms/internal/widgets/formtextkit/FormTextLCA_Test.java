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
package org.eclipse.ui.forms.internal.widgets.formtextkit;

import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;
import static org.eclipse.rap.rwt.testfixture.internal.TestMessage.getParent;
import static org.eclipse.rap.rwt.widgets.WidgetUtil.getId;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.internal.protocol.Operation.CreateOperation;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectRegistry;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.TestMessage;
import org.eclipse.swt.SWT;
import org.eclipse.ui.forms.HyperlinkSettings;
import org.eclipse.ui.forms.internal.widgets.FormsControlLCA_AbstractTest;
import org.eclipse.ui.forms.widgets.FormText;
import org.junit.Test;


@SuppressWarnings( "restriction" )
public class FormTextLCA_Test extends FormsControlLCA_AbstractTest {

  private FormText formText;
  private FormTextLCA lca;

  @Override
  protected void setUp() {
    super.setUp();
    formText = new FormText( shell, SWT.NONE );
    lca = new FormTextLCA();
    Fixture.fakeNewRequest();
  }

  public void testRenderCreate() throws IOException {
    lca.renderInitialization( formText );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( formText );
    assertEquals( "forms.widgets.FormText", operation.getType() );
  }

  @Test
  public void testRenderInitialization_setsOperationHandler() throws IOException {
    String id = getId( formText );
    lca.renderInitialization( formText );

    OperationHandler handler = RemoteObjectRegistry.getInstance().get( id ).getHandler();
    assertTrue( handler instanceof FormTextOperationHandler );
  }

  @Test
  public void testReadData_usesOperationHandler() {
    FormTextOperationHandler handler = spy( new FormTextOperationHandler( formText ) );
    getRemoteObject( getId( formText ) ).setHandler( handler );

    Fixture.fakeNotifyOperation( getId( formText ), "Help", new JsonObject() );
    lca.readData( formText );

    verify( handler ).handleNotifyHelp( formText, new JsonObject() );
  }

  public void testRenderParent() throws IOException {
    lca.renderInitialization( formText );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( formText );
    assertEquals( getId( formText.getParent() ), getParent( operation ) );
  }

  public void testRenderHyperlinkSettings() throws IOException {
    HyperlinkSettings settings = new HyperlinkSettings( display );
    settings.setForeground( display.getSystemColor( SWT.COLOR_BLUE ) );
    settings.setActiveForeground( display.getSystemColor( SWT.COLOR_CYAN ) );
    formText.setHyperlinkSettings( settings );

    lca.renderChanges( formText );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray actual = ( JsonArray )message.findSetProperty( formText, "hyperlinkSettings" );
    assertEquals( 3, actual.get( 0 ).asInt() );
    JsonArray foreground = actual.get( 1 ).asArray();
    assertEquals( 0, foreground.get( 0 ).asInt() );
    assertEquals( 0, foreground.get( 1 ).asInt() );
    assertEquals( 255, foreground.get( 2 ).asInt() );
    assertEquals( 255, foreground.get( 3 ).asInt() );
    JsonArray activeForeground = actual.get( 2 ).asArray();
    assertEquals( 0, activeForeground.get( 0 ).asInt() );
    assertEquals( 255, activeForeground.get( 1 ).asInt() );
    assertEquals( 255, activeForeground.get( 2 ).asInt() );
    assertEquals( 255, activeForeground.get( 3 ).asInt() );
  }

  public void testRenderHyperlinkSettingsUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( formText );
    HyperlinkSettings settings = new HyperlinkSettings( display );
    settings.setForeground( display.getSystemColor( SWT.COLOR_BLUE ) );
    settings.setActiveForeground( display.getSystemColor( SWT.COLOR_CYAN ) );
    formText.setHyperlinkSettings( settings );

    Fixture.preserveWidgets();
    lca.renderChanges( formText );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( formText, "hyperlinkSettings" ) );
  }

  public void testRenderText() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( formText );
    String text = "<form>"
                + "<p>First paragraph</p>"
                + "<li>First bullet</li>"
                + "<li>Second bullet</li>"
                + "<li>Third bullet</li>"
                + "<p>Second paragraph</p>"
                + "</form>";
    formText.setSize( 300, 300 );
    formText.setText( text, true, false );

    lca.renderChanges( formText );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray actual = ( JsonArray )message.findSetProperty( formText, "text" );
    assertEquals( "text", actual.get( 0 ).asArray().get( 0 ).asString() );
    assertEquals( "First paragraph", actual.get( 0 ).asArray().get( 1 ).asString() );
    assertEquals( "bullet", actual.get( 1 ).asArray().get( 0 ).asString() );
    assertEquals( "text", actual.get( 2 ).asArray().get( 0 ).asString() );
    assertEquals( "First bullet", actual.get( 2 ).asArray().get( 1 ).asString() );
    assertEquals( "bullet", actual.get( 3 ).asArray().get( 0 ).asString() );
    assertEquals( "text", actual.get( 4 ).asArray().get( 0 ).asString() );
    assertEquals( "Second bullet", actual.get( 4 ).asArray().get( 1 ).asString() );
    assertEquals( "bullet", actual.get( 5 ).asArray().get( 0 ).asString() );
    assertEquals( "text", actual.get( 6 ).asArray().get( 0 ).asString() );
    assertEquals( "Third bullet", actual.get( 6 ).asArray().get( 1 ).asString() );
    assertEquals( "text", actual.get( 7 ).asArray().get( 0 ).asString() );
    assertEquals( "Second paragraph", actual.get( 7 ).asArray().get( 1 ).asString() );
  }

  public void testRenderTextWithChangedResourceTable() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( formText );
    String text = "<form>"
                + "<p><span color=\"foo\">First paragraph</span></p>"
                + "</form>";
    formText.setSize( 300, 300 );
    formText.setColor( "foo", display.getSystemColor( SWT.COLOR_RED ) );
    formText.setText( text, true, false );
    Fixture.executeLifeCycleFromServerThread();
    Fixture.fakeNewRequest();
    Fixture.preserveWidgets();

    formText.setColor( "foo", display.getSystemColor( SWT.COLOR_BLUE ) );
    lca.renderChanges( formText );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray actual = ( JsonArray )message.findSetProperty( formText, "text" );
    assertEquals( "text", actual.get( 0 ).asArray().get( 0 ).asString() );
    assertEquals( "First paragraph", actual.get( 0 ).asArray().get( 1 ).asString() );
    assertEquals( "[0,0,255,255]", actual.get( 0 ).asArray().get( 4 ).toString() );
  }

  public void testRenderTextWithChangedBounds() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( formText );
    String text = "<form>"
                + "<p>First paragraph</p>"
                + "</form>";
    formText.setSize( 300, 300 );
    formText.setText( text, true, false );
    Fixture.executeLifeCycleFromServerThread();
    Fixture.fakeNewRequest();
    Fixture.preserveWidgets();

    formText.setSize( 200, 400 );
    lca.renderChanges( formText );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray actual = ( JsonArray )message.findSetProperty( formText, "text" );
    assertEquals( "text", actual.get( 0 ).asArray().get( 0 ).asString() );
    assertEquals( "First paragraph", actual.get( 0 ).asArray().get( 1 ).asString() );
  }

}
