/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.widgets;

import java.io.InputStream;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.widgets.IFileUploadAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;


public class FileUpload_Test extends TestCase {

  private Display display;
  private Composite shell;

  public void testStyle() {
    FileUpload upload = new FileUpload( shell, SWT.BORDER | SWT.FLAT );
    assertEquals( 0, upload.getStyle() & SWT.FLAT );
    assertEquals( SWT.BORDER, upload.getStyle() & SWT.BORDER );
  }

  ///////
  // Text

  public void testGetTextDefaultsToEmpty() {
    FileUpload upload = new FileUpload( shell, SWT.NONE );
    assertEquals( "", upload.getText() );
  }

  public void testSetAndGetText() {
    FileUpload upload = new FileUpload( shell, SWT.NONE );
    upload.setText( "foo" );
    assertEquals( "foo", upload.getText() );
    upload.setText( "bar" );
    assertEquals( "bar", upload.getText() );
  }

  public void testSetTextToNull() {
    FileUpload upload = new FileUpload( shell, SWT.NONE );
    try {
      upload.setText( null );
      fail( "Must not allow setting text to null" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  ////////
  // Image

  public void testGetImageDefaultsToNull() {
    FileUpload upload = new FileUpload( shell, SWT.NONE );
    assertEquals( null, upload.getImage() );
  }

  public void testGetSetImage() {
    FileUpload upload = new FileUpload( shell, SWT.NONE );
    Image image1 = createImage( Fixture.IMAGE1 );
    upload.setImage( image1 );
    assertSame( image1, upload.getImage() );
    Image image2 = createImage( Fixture.IMAGE2 );
    upload.setImage( image2 );
    assertSame( image2, upload.getImage() );
  }

  public void testSetImageToNull() {
    FileUpload upload = new FileUpload( shell, SWT.NONE );
    upload.setImage( createImage( Fixture.IMAGE1 ) );
    upload.setImage( null );
    assertEquals( null, upload.getImage() );
  }

  public void testSetImageToDisposedImage() {
    FileUpload upload = new FileUpload( shell, SWT.NONE );
    Image image = createImage( Fixture.IMAGE1 );
    image.dispose();
    try {
      upload.setImage( image );
      fail( "Must not allow disposed image" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  ///////////
  // FileName

  public void testGetFileNameDefaultsToNull() {
    FileUpload upload = new FileUpload( shell, SWT.NONE );
    assertNull( upload.getFileName() );
  }

  public void testGetFileName() {
    FileUpload upload = new FileUpload( shell, SWT.NONE );
    IFileUploadAdapter adapter = getFileUploadAdapter( upload );
    adapter.setFileName( "foo.txt" );
    assertEquals( "foo.txt", upload.getFileName() );
  }

  /////////
  // Submit

  public void testSubmit() {
    FileUpload upload = new FileUpload( shell, SWT.NONE );
    IFileUploadAdapter adapter = getFileUploadAdapter( upload );
    adapter.setFileName( "foo.txt" );
    assertNull( adapter.getAndResetUrl() );
    upload.submit( "http://foo/" );
    assertEquals( "http://foo/", adapter.getAndResetUrl() );
  }

  public void testSubmitCalledTwice() {
    FileUpload upload = new FileUpload( shell, SWT.NONE );
    IFileUploadAdapter adapter = getFileUploadAdapter( upload );
    assertNull( adapter.getAndResetUrl() );
    adapter.setFileName( "foo.txt" );
    upload.submit( "http://foo/" );
    upload.submit( "http://bar/" );
    assertEquals( "http://bar/", adapter.getAndResetUrl() );
  }

  public void testSubmitWhileFileNameIsNull() {
    FileUpload upload = new FileUpload( shell, SWT.NONE );
    upload.submit( "http://bla/" );
    IFileUploadAdapter adapter = getFileUploadAdapter( upload );
    assertNull( adapter.getAndResetUrl() );
  }

  ////////////
  // Listeners

  public void testAddRemoveSelectionListener() {
    FileUpload upload = new FileUpload( shell, SWT.NONE );
    SelectionListener listener = new SelectionAdapter() {};
    assertFalse( SelectionEvent.hasListener( upload ) );
    upload.addSelectionListener( listener );
    assertTrue( SelectionEvent.hasListener( upload ) );
    upload.removeSelectionListener( listener );
    assertFalse( SelectionEvent.hasListener( upload ) );
  }

  /////////
  // Layout

  public void testNoLayoutAccepted() {
    FileUpload upload = new FileUpload( shell, SWT.NONE );
    assertEquals( null, upload.getLayout() );
    upload.setLayout( new GridLayout() );
    assertEquals( null, upload.getLayout() );
  }

  public void testNoChildrenAccepted() {
    FileUpload upload = new FileUpload( shell, SWT.NONE );
    new Button( upload, SWT.PUSH );
    assertEquals( 0, upload.getChildren().length );
  }

  public void testComputeSize() {
    // Text and image to use
    String text = "Click me!";
    Point extent = Graphics.stringExtent( shell.getFont(), text );
    assertEquals( new Point( 52, 14 ), extent );
    Image image = createImage( Fixture.IMAGE_100x50 );
    FileUpload upload = new FileUpload( shell, SWT.NONE );
    Point expected = new Point( 12, 16 );
    assertEquals( expected, upload.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    upload.setText( text );
    expected = new Point( 64, 20 );
    assertEquals( expected, upload.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    upload.setImage( image );
    expected = new Point( 166, 56 );
    assertEquals( expected, upload.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    upload.setText( "" );
    expected = new Point( 112, 56 );
    assertEquals( expected, upload.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    // PUSH button with BORDER
    upload = new FileUpload( shell, SWT.BORDER );
    expected = new Point( 12, 16 );
    assertEquals( expected, upload.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    upload.setText( text );
    upload.setImage( image );
    expected = new Point( 166, 56 );
    assertEquals( expected, upload.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    // fixed size
    expected = new Point( 102, 102 );
    assertEquals( expected, upload.computeSize( 100, 100 ) );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display, SWT.NONE );    
  }

  protected void tearDown() throws Exception {
    display.dispose();
    Fixture.tearDown();
  }

  private Image createImage( String name ) {
    ClassLoader loader = Fixture.class.getClassLoader();
    InputStream stream = loader.getResourceAsStream( name );
    return new Image( display, stream );
  }

  private static IFileUploadAdapter getFileUploadAdapter( FileUpload upload ) {
    return ( IFileUploadAdapter )upload.getAdapter( IFileUploadAdapter.class );
  }
}
