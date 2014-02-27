/*******************************************************************************
 * Copyright (c) 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.dnd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.dnd.HTMLTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ClientFileTransfer_Test {

  private ClientFileTransfer transfer;

  @Before
  public void setUp() {
    Fixture.createApplicationContext();
    Fixture.createServiceContext();
    transfer = ClientFileTransfer.getInstance();
  }

  @After
  public void tearDown() {
    Fixture.disposeOfServiceContext();
    Fixture.disposeOfApplicationContext();
  }

  @Test
  public void testGetInstance_returnsSameInstance() {
    ClientFileTransfer transfer2 = ClientFileTransfer.getInstance();

    assertNotNull( transfer );
    assertSame( transfer, transfer2 );
  }

  @Test
  public void testGetTypeNames() {
    String[] typeNames = transfer.getTypeNames();

    assertEquals( 1, typeNames.length );
    assertEquals( "ClientFile", typeNames[ 0 ] );
  }

  @Test
  public void testGetTypeIds() {
    int[] typeIds = transfer.getTypeIds();

    assertEquals( 1, typeIds.length );
    assertEquals( "ClientFile".hashCode(), typeIds[ 0 ] );
  }

  @Test
  public void testGetSupportedTypes() {
    TransferData[] types = transfer.getSupportedTypes();

    assertEquals( 1, types.length );
    assertEquals( transfer.getTypeIds()[ 0 ], types[ 0 ].type );
  }

  @Test
  public void testIsSupportedType() {
    TransferData[] types = transfer.getSupportedTypes();

    assertTrue( transfer.isSupportedType( types[ 0 ] ) );
  }

  @Test
  public void testIsSupportedType_returnsFalseForNullArgument() {
    assertFalse( transfer.isSupportedType( null ) );
  }

  @Test
  public void testIsSupportedType_returnsFalseForUnsupported() {
    TransferData[] types = HTMLTransfer.getInstance().getSupportedTypes();

    assertFalse( transfer.isSupportedType( types[ 0 ] ) );
  }

}
