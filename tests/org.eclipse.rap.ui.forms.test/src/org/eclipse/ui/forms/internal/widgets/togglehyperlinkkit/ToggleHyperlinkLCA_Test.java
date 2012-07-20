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
package org.eclipse.ui.forms.internal.widgets.togglehyperlinkkit;

import java.io.IOException;

import org.eclipse.rap.rwt.internal.lifecycle.JSConst;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.forms.internal.widgets.FormsControlLCA_AbstractTest;
import org.eclipse.ui.forms.widgets.ToggleHyperlink;
import org.eclipse.ui.forms.widgets.Twistie;
import org.json.JSONArray;
import org.json.JSONException;

@SuppressWarnings("restriction")
public class ToggleHyperlinkLCA_Test extends FormsControlLCA_AbstractTest {

  private ToggleHyperlinkLCA lca;

  protected void setUp() {
    super.setUp();
    lca = new ToggleHyperlinkLCA();
    Fixture.fakeNewRequest( display );
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
    String hyperlinkId = WidgetUtil.getId( hyperlink );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_DEFAULT_SELECTED, hyperlinkId );
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

  public void testRenderImages() throws IOException, JSONException {
    Twistie twistie = new Twistie( shell, SWT.NONE );

    lca.renderInitialization( twistie );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( twistie );
    JSONArray images = ( JSONArray )operation.getProperty( "images" );
    assertNotNull( images.get( 0 ) );
    assertNotNull( images.get( 1 ) );
    assertNotNull( images.get( 2 ) );
    assertNotNull( images.get( 3 ) );
  }

  public void testRenderAddSelectionListener() throws Exception {
    Twistie twistie = new Twistie( shell, SWT.NONE );

    lca.renderChanges( twistie );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( twistie, "selection" ) );
  }

  @SuppressWarnings("serial")
  public void testRenderRemoveSelectionListener() throws Exception {
    Twistie twistie = new Twistie( shell, SWT.NONE );
    Listener listener = new Listener() {
      public void handleEvent( Event event ) {
      }
    };
    twistie.addListener( SWT.DefaultSelection, listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( twistie );
    Fixture.preserveWidgets();

    twistie.removeListener( SWT.DefaultSelection, listener );
    lca.renderChanges( twistie );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( twistie, "selection" ) );
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
    assertNull( message.findListenOperation( twistie, "selection" ) );
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
    assertEquals( Boolean.TRUE, message.findSetProperty( twistie, "expanded" ) );
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
