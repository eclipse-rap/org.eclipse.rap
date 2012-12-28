/*******************************************************************************
 * Copyright (c) 2010, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.dnd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class ByteArrayTransfer_Test {

  @Test
  public void testJavaToNative() {
    ByteArrayTransfer transfer = new ByteArrayTransfer() {
      @Override
      protected String[] getTypeNames() {
        return new String[] { "test-byte-transfer" };
      }
      @Override
      protected int[] getTypeIds() {
        return new int[] { Transfer.registerType( getTypeNames()[ 0 ] ) };
      }
    };
    byte[] object = { 1, 2, 3 };
    TransferData transferData = new TransferData();
    transferData.type = transfer.getTypeIds()[ 0 ];
    transfer.javaToNative( object, transferData );
    byte[] nativeToJava = ( byte[] )transfer.nativeToJava( transferData );
    assertEquals( 1, nativeToJava[ 0 ] );
    assertEquals( 2, nativeToJava[ 1 ] );
    assertEquals( 3, nativeToJava[ 2 ] );
  }

  @Test
  public void testNativeToJavaWithIllegalTransferData() {
    ByteArrayTransfer transfer = new ByteArrayTransfer() {
      @Override
      protected String[] getTypeNames() {
        return new String[] { "test-byte-transfer" };
      }
      @Override
      protected int[] getTypeIds() {
        return new int[] { Transfer.registerType( getTypeNames()[ 0 ] ) };
      }
    };
    TransferData transferData = new TransferData();
    assertNull( transfer.nativeToJava( transferData ) );
    transferData.type = transfer.getTypeIds()[ 0 ];
    assertNull( transfer.nativeToJava( transferData ) );
  }

  @Test
  public void testIsSupportedType() {
    ByteArrayTransfer transfer = new ByteArrayTransfer() {
      @Override
      protected String[] getTypeNames() {
        return new String[] { "test-byte-transfer" };
      }
      @Override
      protected int[] getTypeIds() {
        return new int[] { Transfer.registerType( getTypeNames()[ 0 ] ) };
      }
    };
    assertFalse( transfer.isSupportedType( null ) );
    assertFalse( transfer.isSupportedType( new TransferData() ) );
    TransferData transferData = new TransferData();
    transferData.type = transfer.getTypeIds()[ 0 ];
    assertTrue( transfer.isSupportedType( transferData ) );
  }

}
