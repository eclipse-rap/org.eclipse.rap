/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.displaykit;

import static org.mockito.Mockito.mock;
import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.uicallback.UICallBackManager;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.swt.widgets.Display;


public class UICallBackRenderer_Test extends TestCase {

  private static final String UI_CALLBACK_ID = "rwt.client.UICallBack";

  private Display display;
  private UICallBackRenderer renderer;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    renderer = new UICallBackRenderer();
    Fixture.fakeNewRequest();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testDoNotCreateUICallBackClientObject() throws Exception {
    // UICallBack object is created by the client
    UICallBackManager.getInstance().activateUICallBacksFor( "id" );

    renderer.render();

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findCreateOperation( UICallBackRenderer.UI_CALLBACK_ID ) );
  }

  public void testNothingRenderedIfNotActivated() {
    renderer.render();

    Message message = Fixture.getProtocolMessage();
    assertEquals( 0, message.getOperationCount() );
  }

  public void testActivationIsRendered() {
    UICallBackManager.getInstance().activateUICallBacksFor( "id" );

    renderer.render();

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findSetProperty( UI_CALLBACK_ID, "active" ) );
  }

  public void testActivationIsPreserved() {
    UICallBackManager.getInstance().activateUICallBacksFor( "id" );
    renderer.render();

    Fixture.fakeNewRequest();
    UICallBackManager.getInstance().activateUICallBacksFor( "id" );
    renderer.render();

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( UI_CALLBACK_ID, "active" ) );
  }

  public void testDeactivationIsRendered() {
    UICallBackManager.getInstance().activateUICallBacksFor( "id" );
    renderer.render();

    Fixture.fakeNewRequest();
    UICallBackManager.getInstance().deactivateUICallBacksFor( "id" );
    renderer.render();

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findSetProperty( UI_CALLBACK_ID, "active" ) );
  }

  public void testDeactivationIsNotRenderedWhenRunnablesArePending() {
    UICallBackManager.getInstance().activateUICallBacksFor( "id" );
    renderer.render();
    display.asyncExec( mock( Runnable.class ) );

    Fixture.fakeNewRequest();
    UICallBackManager.getInstance().deactivateUICallBacksFor( "id" );
    renderer.render();

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( UI_CALLBACK_ID, "active" ) );
  }

}
