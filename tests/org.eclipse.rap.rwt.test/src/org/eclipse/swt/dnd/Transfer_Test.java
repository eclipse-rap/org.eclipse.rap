/*******************************************************************************
 * Copyright (c) 2009, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.swt.dnd;

import static org.eclipse.rap.rwt.testfixture.internal.TestUtil.createImage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class Transfer_Test {

  private Display display;

  @Before
  public void setUp() {
    Fixture.createApplicationContext();
    Fixture.createServiceContext();
    display = new Display();
  }

  @After
  public void tearDown() {
    Fixture.disposeOfServiceContext();
    Fixture.disposeOfApplicationContext();
  }

  @Test
  public void testTransferDataInitialValues() {
    TransferData data = new TransferData();
    assertEquals( 0, data.type );
    assertEquals( 0, data.result );
    assertNull( data.data );
  }

  @Test
  public void testTextTransferTypeIds() {
    Transfer transfer = TextTransfer.getInstance();
    assertNotNull( transfer.getTypeIds() );
    assertTrue( transfer.getTypeIds().length > 0 );
  }

  @Test
  public void testTextTransferIsSupportedType() {
    Transfer transfer = TextTransfer.getInstance();
    TransferData data = new TransferData();
    data.type = transfer.getTypeIds()[ 0 ];
    assertTrue( transfer.isSupportedType( data ) );
    data = new TransferData();
    data.type = ImageTransfer.getInstance().getTypeIds()[ 0 ];
    assertFalse( transfer.isSupportedType( data ) );
    assertFalse( transfer.isSupportedType( null ) );
  }

  @Test
  public void testTextTransferConversion() {
    Transfer transfer = TextTransfer.getInstance();
    TransferData data = transfer.getSupportedTypes()[ 0 ];
    transfer.javaToNative( "foo", data );
    assertEquals( 1, data.result );
    Object java = transfer.nativeToJava( data );
    assertTrue( java instanceof String );
    assertEquals( "foo", java );
  }

  @Test
  public void testImageTransferIsSupportedType() {
    Transfer transfer = ImageTransfer.getInstance();
    TransferData data = new TransferData();
    data.type = transfer.getTypeIds()[ 0 ];
    assertTrue( transfer.isSupportedType( data ) );
    data = new TransferData();
    data.type = TextTransfer.getInstance().getTypeIds()[ 0 ];
    assertFalse( transfer.isSupportedType( data ) );
    assertFalse( transfer.isSupportedType( null ) );
  }

  @Test
  public void testImageTransferTypeIds() {
    Transfer transfer = ImageTransfer.getInstance();
    assertNotNull( transfer.getTypeIds() );
    assertTrue( transfer.getTypeIds().length > 0 );
  }

  @Test
  public void testImageTransferConversion() throws IOException {
    Fixture.useDefaultResourceManager();
    Transfer transfer = ImageTransfer.getInstance();
    TransferData data = transfer.getSupportedTypes()[ 0 ];
    Image image = createImage( display, Fixture.IMAGE1 );
    ImageData imageData = image.getImageData();
    transfer.javaToNative( imageData, data );
    assertEquals( 1, data.result );
    Object java = transfer.nativeToJava( data );
    assertTrue( java instanceof ImageData );
    assertNotSame( imageData, java );
    assertEquals( imageData.width, ( ( ImageData ) java ).width );
    assertEquals( imageData.height, ( ( ImageData ) java ).height );
  }

  @Test
  public void testURLTransferConversion() {
    Transfer transfer = URLTransfer.getInstance();
    TransferData data = transfer.getSupportedTypes()[ 0 ];
    String url = "http://eclipse.org/rap";
    transfer.javaToNative( url, data );
    assertEquals( 1, data.result );
    Object java = transfer.nativeToJava( data );
    assertTrue( java instanceof String );
    assertEquals( url, java );
  }

  @Test
  public void testFileTransferConversion() {
    Transfer transfer = FileTransfer.getInstance();
    TransferData data = transfer.getSupportedTypes()[ 0 ];
    String[] fileNames = new String[] { "c:/file1", "c:/file2", };
    transfer.javaToNative( fileNames, data );
    assertEquals( 1, data.result );
    Object java = transfer.nativeToJava( data );
    assertTrue( java instanceof String[] );
    String[] transferredFileNames = ( String[] )java;
    assertEquals( fileNames.length, transferredFileNames.length );
    assertEquals( fileNames[ 0 ], transferredFileNames[ 0 ] );
    assertEquals( fileNames[ 1 ], transferredFileNames[ 1 ] );
  }

  @Test
  public void testRTFTransferConversion() {
    Transfer transfer = RTFTransfer.getInstance();
    TransferData data = transfer.getSupportedTypes()[ 0 ];
    String rtf
      = "{\\rtf1{\\colortbl;\\red255\\green0\\blue0;}\\cf1\\b "
      + "Hello World"
      + "}";
    transfer.javaToNative( rtf, data );
    assertEquals( 1, data.result );
    Object java = transfer.nativeToJava( data );
    assertTrue( java instanceof String );
    assertEquals( rtf, java );
  }

  @Test
  public void testHTMLTransferConversion() {
    Transfer transfer = HTMLTransfer.getInstance();
    TransferData data = transfer.getSupportedTypes()[ 0 ];
    String html = "<html></html>";
    transfer.javaToNative( html, data );
    assertEquals( 1, data.result );
    Object java = transfer.nativeToJava( data );
    assertTrue( java instanceof String );
    assertEquals( html, java );
  }

  @Test
  public void testTextToImageTransfer() {
    Transfer textTransfer = TextTransfer.getInstance();
    TransferData data = textTransfer.getSupportedTypes()[ 0 ];
    String text = "Hello World";
    textTransfer.javaToNative( text, data );
    assertEquals( 1, data.result );
    Transfer imageTransfer = ImageTransfer.getInstance();
    Object java = imageTransfer.nativeToJava( data );
    assertNull( java );
  }

  @Test
  public void testTextTransferWithIllegalData() {
    Transfer textTransfer = TextTransfer.getInstance();
    TransferData data = textTransfer.getSupportedTypes()[ 0 ];
    try {
      textTransfer.javaToNative( new Object(), data );
      fail( "Must not accept wrong input data" );
    } catch( SWTException expected ) {
    }
  }

  @Test
  public void testRegisterType() {
    int fooType = Transfer.registerType( "foo" );
    assertEquals( fooType, Transfer.registerType( "foo" ) );
    int barType = Transfer.registerType( "bar" );
    assertTrue( fooType != barType );
  }

  @Test
  public void testIsTransferSerializable() throws Exception {
    TextTransfer transfer = TextTransfer.getInstance();

    TextTransfer deserializedTransfer = Fixture.serializeAndDeserialize( transfer );

    TransferData type = transfer.getSupportedTypes()[ 0 ];
    TransferData deserializedType = deserializedTransfer.getSupportedTypes()[ 0 ];
    assertTrue( TransferData.sameType( type, deserializedType ) );
  }

  @Test
  public void testIsTransferDataSerializable() throws Exception {
    TransferData transferData = new TransferData();
    transferData.type = 123;
    transferData.data = "data";
    transferData.result = 456;

    TransferData deserializedTransferData = Fixture.serializeAndDeserialize( transferData );

    assertEquals( transferData.type, deserializedTransferData.type );
    assertEquals( transferData.data, deserializedTransferData.data );
    assertEquals( transferData.result, deserializedTransferData.result );
  }

}
