/*******************************************************************************
 * Copyright (c) 2025 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.addons.scanner;

import static org.eclipse.rap.rwt.widgets.WidgetUtil.getId;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.remote.Connection;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class BarcodeScanner_Test {

  @Rule
  public TestContext context = new TestContext();

  private Shell shell;
  private BarcodeScanner scanner;
  private Connection connection;
  private RemoteObject remoteObject;

  @Before
  public void setUp() {
    shell = new Shell( new Display() );
    remoteObject = mock( RemoteObject.class );
    connection = mock( Connection.class );
    when( connection.createRemoteObject( anyString() ) ).thenReturn( remoteObject );
    context.replaceConnection( connection );
    scanner = new BarcodeScanner( shell );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testFailsWithNullParent() {
    new BarcodeScanner( null );
  }

  @Test
  public void testContructor_createsRemoteObjectWithCorrectType() {
    verify( connection ).createRemoteObject( eq( "rwt.widgets.BarcodeScanner" ) );
  }

  @Test
  public void testContructor_setsParent() {
    verify( remoteObject ).set( "parent", getId( scanner ) );
  }

  @Test
  public void testRenderStart() {
    scanner.start( new BarcodeScanner.Formats[] { BarcodeScanner.Formats.QR_CODE } );

    verify( remoteObject ).call( "start", new JsonObject().set( "formats", new JsonArray().add( "QR_CODE" ) ) );
  }

  @Test
  public void testRenderStart_onlyOnce() {
    scanner.start( new BarcodeScanner.Formats[] { BarcodeScanner.Formats.QR_CODE } );
    scanner.start( new BarcodeScanner.Formats[] { BarcodeScanner.Formats.QR_CODE } );

    verify( remoteObject, times( 1 ) ).call( "start", new JsonObject().set( "formats", new JsonArray().add( "QR_CODE" ) ) );
  }

  @Test
  public void testRenderStop() {
    scanner.start( new BarcodeScanner.Formats[] { BarcodeScanner.Formats.QR_CODE } );
    reset( remoteObject );

    scanner.stop();

    verify( remoteObject ).call( "stop", null );
  }

  @Test
  public void testRenderStop_onlyOnce() {
    scanner.start( new BarcodeScanner.Formats[] { BarcodeScanner.Formats.QR_CODE } );
    reset( remoteObject );

    scanner.stop();
    scanner.stop();

    verify( remoteObject, times( 1 ) ).call( "stop", null );
  }

  @Test
  public void testIsRunning_returnsFalseInitially() throws Exception {
    assertFalse( scanner.isRunning() );
  }

  @Test
  public void testIsRunning_returnsTrueAfterStart() throws Exception {
    scanner.start( new BarcodeScanner.Formats[] { BarcodeScanner.Formats.QR_CODE } );

    assertTrue( scanner.isRunning() );
  }

  @Test
  public void testIsRunning_returnsTrueAfterStop() throws Exception {
    scanner.start( new BarcodeScanner.Formats[] { BarcodeScanner.Formats.QR_CODE } );

    scanner.stop();

    assertFalse( scanner.isRunning() );
  }

}
