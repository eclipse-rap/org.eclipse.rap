/*******************************************************************************
 * Copyright (c) 2011, 2016 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.widgets;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.InputStream;

import org.eclipse.rap.rwt.internal.lifecycle.WidgetLCA;
import org.eclipse.rap.rwt.internal.textsize.TextSizeUtil;
import org.eclipse.rap.rwt.internal.widgets.IFileUploadAdapter;
import org.eclipse.rap.rwt.internal.widgets.fileuploadkit.FileUploadLCA;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class FileUpload_Test {

  @Rule
  public TestContext context = new TestContext();

  private Display display;
  private Composite shell;
  private FileUpload upload;

  @Before
  public void setUp() {
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    upload = new FileUpload( shell, SWT.NONE );
  }

  @Test
  public void testStyle() {
    upload = new FileUpload( shell, SWT.BORDER | SWT.FLAT | SWT.MULTI );

    assertEquals( 0, upload.getStyle() & SWT.FLAT );
    assertEquals( SWT.BORDER, upload.getStyle() & SWT.BORDER );
    assertEquals( SWT.MULTI, upload.getStyle() & SWT.MULTI );
  }

  @Test
  public void testGetTextDefaultsToEmpty() {
    assertEquals( "", upload.getText() );
  }

  @Test
  public void testSetText() {
    upload.setText( "foo" );

    assertEquals( "foo", upload.getText() );
  }

  @Test
  public void testSetTextTwice() {
    upload.setText( "foo" );
    upload.setText( "bar" );

    assertEquals( "bar", upload.getText() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetTextToNull() {
    upload.setText( null );
  }

  @Test
  public void testGetImageDefaultsToNull() {
    assertEquals( null, upload.getImage() );
  }

  @Test
  public void testGetSetImage() {
    Image image1 = createImage( Fixture.IMAGE1 );
    upload.setImage( image1 );
    assertSame( image1, upload.getImage() );
    Image image2 = createImage( Fixture.IMAGE2 );
    upload.setImage( image2 );
    assertSame( image2, upload.getImage() );
  }

  @Test
  public void testSetImageToNull() {
    upload.setImage( createImage( Fixture.IMAGE1 ) );
    upload.setImage( null );
    assertEquals( null, upload.getImage() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetImageToDisposedImage() {
    Image image = createImage( Fixture.IMAGE1 );
    image.dispose();
    upload.setImage( image );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetFilterExtensionsToNull() {
    upload.setFilterExtensions( null );
  }

  public void testSetFilterExtensions() {
    String[] extensions = new String[] { "foo", "bar" };

    upload.setFilterExtensions( extensions );

    assertArrayEquals( extensions, upload.getFilterExtensions() );
  }

  @Test
  public void testGetFileNameDefaultsToNull() {
    assertNull( upload.getFileName() );
  }

  @Test
  public void testGetFileName() {
    IFileUploadAdapter adapter = getFileUploadAdapter( upload );
    adapter.setFileNames( new String[]{ "foo.txt" } );
    assertEquals( "foo.txt", upload.getFileName() );
  }

  @Test
  public void testGetFileNamesDefaultsToEmptyArray() {
    assertEquals( 0, upload.getFileNames().length );
  }

  @Test
  public void testGetFileNames() {
    upload = new FileUpload( shell, SWT.MULTI );
    IFileUploadAdapter adapter = getFileUploadAdapter( upload );

    adapter.setFileNames( new String[]{ "foo.txt", "bar.txt" } );

    String[] expected = new String[]{ "foo.txt", "bar.txt" };
    assertArrayEquals( expected, upload.getFileNames() );
  }

  @Test
  public void testGetFileNames_ReturnsSaveCopy() {
    upload = new FileUpload( shell, SWT.MULTI );
    IFileUploadAdapter adapter = getFileUploadAdapter( upload );
    adapter.setFileNames( new String[]{ "foo.txt", "bar.txt" } );

    upload.getFileNames()[ 0 ] = null;

    String[] expected = new String[]{ "foo.txt", "bar.txt" };
    assertArrayEquals( expected, upload.getFileNames() );
  }

  @Test
  public void testSubmit() {
    IFileUploadAdapter adapter = getFileUploadAdapter( upload );
    adapter.setFileNames( new String[] { "foo.txt" } );
    assertNull( adapter.getAndResetUrl() );
    upload.submit( "http://foo/" );
    assertEquals( "http://foo/", adapter.getAndResetUrl() );
  }

  @Test
  public void testSubmitCalledTwice() {
    IFileUploadAdapter adapter = getFileUploadAdapter( upload );
    assertNull( adapter.getAndResetUrl() );
    adapter.setFileNames( new String[]{ "foo.txt" } );
    upload.submit( "http://foo/" );
    upload.submit( "http://bar/" );
    assertEquals( "http://bar/", adapter.getAndResetUrl() );
  }

  @Test
  public void testSubmitWhileFileNameIsNull() {
    upload.submit( "http://bla/" );
    IFileUploadAdapter adapter = getFileUploadAdapter( upload );
    assertNull( adapter.getAndResetUrl() );
  }

  @Test
  public void testAddSelectionListener_registersUntypedListeners() {
    upload.addSelectionListener( mock( SelectionListener.class ) );

    assertTrue( upload.isListening( SWT.Selection ) );
    assertTrue( upload.isListening( SWT.DefaultSelection ) );
  }

  @Test
  public void testRemoveSelectionListener_unregistersUntypedListeners() {
    SelectionListener listener = mock( SelectionListener.class );
    upload.addSelectionListener( listener );

    upload.removeSelectionListener( listener );

    assertFalse( upload.isListening( SWT.Selection ) );
    assertFalse( upload.isListening( SWT.DefaultSelection ) );
  }

  @Test
  public void testNoLayoutAccepted() {
    assertEquals( null, upload.getLayout() );
    upload.setLayout( new GridLayout() );
    assertEquals( null, upload.getLayout() );
  }

  @Test
  public void testNoChildrenAccepted() {
    new Button( upload, SWT.PUSH );
    assertEquals( 0, upload.getChildren().length );
  }

  @Test
  public void testComputeSize() {
    // Text and image to use
    String text = "Click me!";
    Point extent = TextSizeUtil.stringExtent( shell.getFont(), text );
    assertEquals( new Point( 60, 16 ), extent );
    Image image = createImage( Fixture.IMAGE_100x50 );
    Point expected = new Point( 30, 22 );
    assertEquals( expected, upload.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    upload.setText( text );
    expected = new Point( 90, 28 );
    assertEquals( expected, upload.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    upload.setImage( image );
    expected = new Point( 192, 62 );
    assertEquals( expected, upload.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    upload.setText( "" );
    expected = new Point( 130, 62 );
    assertEquals( expected, upload.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    // PUSH button with BORDER
    upload = new FileUpload( shell, SWT.BORDER );
    expected = new Point( 32, 24 );
    assertEquals( expected, upload.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    upload.setText( text );
    upload.setImage( image );
    expected = new Point( 194, 64 );
    assertEquals( expected, upload.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    // fixed size
    expected = new Point( 102, 102 );
    assertEquals( expected, upload.computeSize( 100, 100 ) );
  }

  @Test
  public void testGetAdapter_LCA() {
    assertTrue( upload.getAdapter( WidgetLCA.class ) instanceof FileUploadLCA );
    assertSame( upload.getAdapter( WidgetLCA.class ), upload.getAdapter( WidgetLCA.class ) );
  }

  @SuppressWarnings( "resource" )
  private Image createImage( String name ) {
    ClassLoader loader = Fixture.class.getClassLoader();
    InputStream stream = loader.getResourceAsStream( name );
    return new Image( display, stream );
  }

  private static IFileUploadAdapter getFileUploadAdapter( FileUpload upload ) {
    return upload.getAdapter( IFileUploadAdapter.class );
  }

}
