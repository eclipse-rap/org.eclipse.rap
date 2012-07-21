/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.ui.forms.internal.widgets.formtextkit;

import java.io.IOException;

import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.swt.SWT;
import org.eclipse.ui.forms.HyperlinkSettings;
import org.eclipse.ui.forms.internal.widgets.FormsControlLCA_AbstractTest;
import org.eclipse.ui.forms.widgets.*;
import org.json.JSONArray;
import org.json.JSONException;

public class FormTextLCA_Test extends FormsControlLCA_AbstractTest {

  private FormText formText;
  private FormTextLCA lca;

  protected void setUp() {
    super.setUp();
    formText = new FormText( shell, SWT.NONE );
    lca = new FormTextLCA();
    Fixture.fakeNewRequest( display );
  }

  public void testRenderCreate() throws IOException {
    lca.renderInitialization( formText );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( formText );
    assertEquals( "forms.widgets.FormText", operation.getType() );
  }

  public void testRenderParent() throws IOException {
    lca.renderInitialization( formText );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( formText );
    assertEquals( WidgetUtil.getId( formText.getParent() ), operation.getParent() );
  }

  public void testRenderHyperlinkSettings() throws IOException, JSONException {
    HyperlinkSettings settings = new HyperlinkSettings( display );
    settings.setForeground( display.getSystemColor( SWT.COLOR_BLUE ) );
    settings.setActiveForeground( display.getSystemColor( SWT.COLOR_CYAN ) );
    formText.setHyperlinkSettings( settings );

    lca.renderChanges( formText );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( formText, "hyperlinkSettings" );
    assertEquals( 3, actual.getInt( 0 ) );
    JSONArray foreground = actual.getJSONArray( 1 );
    assertEquals( 0, foreground.getInt( 0 ) );
    assertEquals( 0, foreground.getInt( 1 ) );
    assertEquals( 255, foreground.getInt( 2 ) );
    assertEquals( 255, foreground.getInt( 3 ) );
    JSONArray activeForeground = actual.getJSONArray( 2 );
    assertEquals( 0, activeForeground.getInt( 0 ) );
    assertEquals( 255, activeForeground.getInt( 1 ) );
    assertEquals( 255, activeForeground.getInt( 2 ) );
    assertEquals( 255, activeForeground.getInt( 3 ) );
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

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( formText, "hyperlinkSettings" ) );
  }

  public void testRenderText() throws IOException, JSONException {
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

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( formText, "text" );
    assertEquals( "text", actual.getJSONArray( 0 ).getString( 0 ) );
    assertEquals( "First paragraph", actual.getJSONArray( 0 ).getString( 1 ) );
    assertEquals( "bullet", actual.getJSONArray( 1 ).getString( 0 ) );
    assertEquals( "text", actual.getJSONArray( 2 ).getString( 0 ) );
    assertEquals( "First bullet", actual.getJSONArray( 2 ).getString( 1 ) );
    assertEquals( "bullet", actual.getJSONArray( 3 ).getString( 0 ) );
    assertEquals( "text", actual.getJSONArray( 4 ).getString( 0 ) );
    assertEquals( "Second bullet", actual.getJSONArray( 4 ).getString( 1 ) );
    assertEquals( "bullet", actual.getJSONArray( 5 ).getString( 0 ) );
    assertEquals( "text", actual.getJSONArray( 6 ).getString( 0 ) );
    assertEquals( "Third bullet", actual.getJSONArray( 6 ).getString( 1 ) );
    assertEquals( "text", actual.getJSONArray( 7 ).getString( 0 ) );
    assertEquals( "Second paragraph", actual.getJSONArray( 7 ).getString( 1 ) );
  }

  public void testRenderTextWithChangedResourceTable() throws IOException, JSONException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( formText );
    String text = "<form>"
                + "<p><span color=\"foo\">First paragraph</span></p>"
                + "</form>";
    formText.setSize( 300, 300 );
    formText.setColor( "foo", display.getSystemColor( SWT.COLOR_RED ) );
    formText.setText( text, true, false );
    Fixture.executeLifeCycleFromServerThread();
    Fixture.fakeNewRequest( display );
    Fixture.preserveWidgets();

    formText.setColor( "foo", display.getSystemColor( SWT.COLOR_BLUE ) );
    lca.renderChanges( formText );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( formText, "text" );
    assertEquals( "text", actual.getJSONArray( 0 ).getString( 0 ) );
    assertEquals( "First paragraph", actual.getJSONArray( 0 ).getString( 1 ) );
    assertEquals( "[0,0,255,255]", actual.getJSONArray( 0 ).getString( 4 ) );
  }

  public void testRenderTextWithChangedBounds() throws IOException, JSONException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( formText );
    String text = "<form>"
                + "<p>First paragraph</p>"
                + "</form>";
    formText.setSize( 300, 300 );
    formText.setText( text, true, false );
    Fixture.executeLifeCycleFromServerThread();
    Fixture.fakeNewRequest( display );
    Fixture.preserveWidgets();

    formText.setSize( 200, 400 );
    lca.renderChanges( formText );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( formText, "text" );
    assertEquals( "text", actual.getJSONArray( 0 ).getString( 0 ) );
    assertEquals( "First paragraph", actual.getJSONArray( 0 ).getString( 1 ) );
  }

}
