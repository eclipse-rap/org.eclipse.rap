/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.compositekit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class CompositeLCA_Test {

  private Display display;
  private Shell shell;
  private CompositeLCA lca;
  private Composite composite;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    lca = new CompositeLCA();
    Fixture.fakeNewRequest();
    composite = new Composite( shell, SWT.BORDER );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testControlListeners() throws IOException {
    ControlLCATestUtil.testActivateListener( composite );
    ControlLCATestUtil.testFocusListener( composite );
    ControlLCATestUtil.testMouseListener( composite );
    ControlLCATestUtil.testKeyListener( composite );
    ControlLCATestUtil.testTraverseListener( composite );
    ControlLCATestUtil.testMenuDetectListener( composite );
    ControlLCATestUtil.testHelpListener( composite );
  }

  @Test
  public void testRenderClientArea() throws JSONException {
    composite.setSize( 110, 120 );

    lca.renderClientArea( composite );

    Message message = Fixture.getProtocolMessage();
    Rectangle clientArea = composite.getClientArea();
    assertEquals( clientArea, toRectangle( message.findSetProperty( composite, "clientArea" ) ) );
  }

  @Test
  public void testRenderClientArea_SizeZero() throws JSONException {
    composite.setSize( 0, 0 );

    lca.renderClientArea( composite );

    Message message = Fixture.getProtocolMessage();
    Rectangle clientArea = new Rectangle( 0, 0, 0, 0 );
    assertEquals( clientArea, toRectangle( message.findSetProperty( composite, "clientArea" ) ) );
  }

  @Test
  public void testRenderClientArea_SizeUnchanged() {
    Fixture.markInitialized( composite );
    composite.setSize( 110, 120 );

    lca.preserveValues( composite );
    lca.renderClientArea( composite );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( composite, "clientArea" ) );
  }

  private Rectangle toRectangle( Object property ) throws JSONException {
    JSONArray jsonArray = ( JSONArray )property;
    Rectangle result = new Rectangle(
      jsonArray.getInt( 0 ),
      jsonArray.getInt( 1 ),
      jsonArray.getInt( 2 ),
      jsonArray.getInt( 3 )
    );
    return result;
  }

}
