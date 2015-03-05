/*******************************************************************************
 * Copyright (c) 2011, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.fileupload.test;

import static org.eclipse.rap.fileupload.test.FileUploadTestUtil.createFakeUploadRequest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/*
 * Tests to understand and verify how commons.fileupload works
 */
public class CommonsFileUpload_Test {

  private File tempDirectory;

  @Before
  public void setUp() {
    tempDirectory = FileUploadTestUtil.createTempDirectory();
  }

  @After
  public void tearDown() {
    FileUploadTestUtil.deleteRecursively( tempDirectory );
  }

  @Test
  public void testUploadEmptyFileWithZeroThreshold() throws FileUploadException {
    FileItemFactory factory = new DiskFileItemFactory( 0, tempDirectory );
    ServletFileUpload upload = new ServletFileUpload( factory );
    HttpServletRequest request = createFakeUploadRequest( "", "text/empty", "empty.txt" );

    List items = upload.parseRequest( request );

    assertEquals( 1, items.size() );
    DiskFileItem fileItem = ( DiskFileItem )items.get( 0 );
    assertEquals( "empty.txt", fileItem.getName() );
    assertEquals( "text/empty", fileItem.getContentType() );
    assertEquals( 0L, fileItem.getSize() );
    // Content and threshold is zero, empty file is not written
    assertFalse( fileItem.getStoreLocation().exists() );
  }

  @Test
  public void testUploadSmallerThanThreshold() throws FileUploadException {
    FileItemFactory factory = new DiskFileItemFactory( 100, tempDirectory );
    ServletFileUpload upload = new ServletFileUpload( factory );
    HttpServletRequest request = createFakeUploadRequest( "Hello World!\n",
                                                          "text/plain",
                                                          "hello.txt" );

    List items = upload.parseRequest( request );

    assertEquals( 1, items.size() );
    DiskFileItem fileItem = ( DiskFileItem )items.get( 0 );
    assertEquals( "hello.txt", fileItem.getName() );
    assertEquals( "text/plain", fileItem.getContentType() );
    assertEquals( "Hello World!\n", fileItem.getString() );
    // Content is smaller than threshold, therefore file is not written
    assertFalse( fileItem.getStoreLocation().exists() );
  }

  @Test
  public void testUploadLongFile() throws FileUploadException {
    FileItemFactory factory = new DiskFileItemFactory( 100, tempDirectory );
    ServletFileUpload upload = new ServletFileUpload( factory );
    StringBuffer content = new StringBuffer( 600 );
    for( int i = 0; i < 100; i++ ) {
      content.append( "Hello\n" );
    }
    HttpServletRequest request = createFakeUploadRequest( content.toString(),
                                                          "text/plain",
                                                          "long.txt" );

    List items = upload.parseRequest( request );
    DiskFileItem fileItem = ( DiskFileItem )items.get( 0 );

    assertEquals( "long.txt", fileItem.getName() );
    assertEquals( "text/plain", fileItem.getContentType() );
    assertEquals( 600, fileItem.getSize() );
    assertTrue( fileItem.getStoreLocation().exists() );
  }

}
