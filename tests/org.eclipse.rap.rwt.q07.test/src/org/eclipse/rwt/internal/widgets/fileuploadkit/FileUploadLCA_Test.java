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
package org.eclipse.rwt.internal.widgets.fileuploadkit;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.widgets.IFileUploadAdapter;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.rwt.widgets.FileUpload;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;


public class FileUploadLCA_Test extends TestCase {

  private Display display;
  private Composite shell;

  public void testPreserveBounds() {
    FileUpload upload = new FileUpload( shell, SWT.NONE );
    Fixture.markInitialized( display );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( upload );
    Rectangle rectangle = new Rectangle( 10, 10, 10, 10 );
    upload.setBounds( rectangle );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( upload );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    Fixture.clearPreserved();
  }

  public void testPreserveFileName() {
    FileUpload upload = new FileUpload( shell, SWT.NONE );
    Fixture.markInitialized( display );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( upload );
    IFileUploadAdapter uploadAdapter = getFileUploadAdapter( upload );
    uploadAdapter.setFileName( "foo" );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( upload );
    assertEquals( "foo", adapter.getPreserved( FileUploadLCA.PROP_FILENAME ) );
    Fixture.clearPreserved();
  }

  public void testRenderTextAndImage() throws Exception {
    FileUpload upload = new FileUpload( shell, SWT.NONE );
    upload.setText( "Test" );
    Image image = Graphics.getImage( Fixture.IMAGE1 );
    upload.setImage( image );
    Fixture.markInitialized( upload );
    Fixture.preserveWidgets();
    Fixture.fakeResponseWriter();
    FileUploadLCA lca = new FileUploadLCA();
    lca.renderChanges( upload );
    String allMarkup = Fixture.getAllMarkup();
    assertTrue( allMarkup.indexOf( "w.setText( \"Test\" );" ) != -1 );
    String imageLocation = ImageFactory.getImagePath( image );
    String expected = "w.setImage( \"" + imageLocation + "\", 58, 12 );";
    assertTrue( allMarkup.indexOf( expected ) != -1 );
    Fixture.fakeResponseWriter();
    lca.preserveValues( upload );
    lca.renderChanges( upload );
    allMarkup = Fixture.getAllMarkup();
    assertTrue( allMarkup.indexOf( expected ) == -1 );
    Fixture.fakeResponseWriter();
    lca.preserveValues( upload );
    upload.setImage( null );
    lca.renderChanges( upload );
    allMarkup = Fixture.getAllMarkup();
    expected = "w.setImage( null, 0, 0 );";
    assertTrue( allMarkup.indexOf( expected ) != -1 );
  }

  public void testReadFileName() {
    FileUpload fileUpload = new FileUpload( shell, SWT.NONE );
    String uploadId = WidgetUtil.getId( fileUpload );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( uploadId + ".fileName", "foo" );
    Fixture.executeLifeCycleFromServerThread( );
    assertEquals( "foo", fileUpload.getFileName() );
    Fixture.fakeNewRequest( display );
    Fixture.executeLifeCycleFromServerThread( );
    assertEquals( "foo", fileUpload.getFileName() );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( uploadId + ".fileName", "" );
    Fixture.executeLifeCycleFromServerThread( );
    assertEquals( null, fileUpload.getFileName() );
  }

  public void testRenderFileName() throws Exception {
    FileUpload upload = new FileUpload( shell, SWT.NONE );
    IFileUploadAdapter uploadAdapter = getFileUploadAdapter( upload );
    uploadAdapter.setFileName( "foo" );
    Fixture.markInitialized( upload );
    Fixture.preserveWidgets();
    Fixture.fakeResponseWriter();
    FileUploadLCA lca = new FileUploadLCA();
    lca.preserveValues( upload );
    lca.renderChanges( upload );
    String allMarkup = Fixture.getAllMarkup();
    assertTrue( allMarkup.indexOf( "w.setFileName" ) == -1 );
  }

  public void testFireSelectionEvent() {
    final List eventLog = new ArrayList();
    FileUpload upload = new FileUpload( shell, SWT.NONE );
    upload.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        eventLog.add( event );
      }
    } );
    String uploadId = WidgetUtil.getId( upload );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( uploadId + ".fileName", "foo" );
    Fixture.executeLifeCycleFromServerThread( );
    assertEquals( "foo", upload.getFileName() );
    assertEquals( 1, eventLog.size() );
    SelectionEvent event = ( SelectionEvent )eventLog.get( 0 );
    assertSame( upload, event.widget );
  }

  public void testRenderSubmit() throws Exception {
    FileUpload upload = new FileUpload( shell, SWT.NONE );
    IFileUploadAdapter adapter = getFileUploadAdapter( upload );
    adapter.setFileName( "test.txt" );
    Fixture.markInitialized( upload );
    Fixture.preserveWidgets();
    Fixture.fakeResponseWriter();
    FileUploadLCA lca = new FileUploadLCA();
    lca.preserveValues( upload );
    lca.renderChanges( upload );
    String allMarkup = Fixture.getAllMarkup();
    assertTrue( allMarkup.indexOf( "w.submit" ) == -1 );
    lca.preserveValues( upload );
    upload.submit( "foo" );
    Fixture.fakeResponseWriter();
    lca.renderChanges( upload );
    allMarkup = Fixture.getAllMarkup();
    assertTrue( allMarkup.indexOf( "w.submit( \"foo\" )" ) != -1 );    
    Fixture.fakeResponseWriter();
    lca.preserveValues( upload );
    lca.renderChanges( upload );
    allMarkup = Fixture.getAllMarkup();
    assertTrue( allMarkup.indexOf( "w.submit" ) == -1 );
  }

  public void testRenderSubmitWhileFileIsNull() throws Exception {
    FileUpload upload = new FileUpload( shell, SWT.NONE );
    upload.submit( "foo" );
    Fixture.markInitialized( upload );
    Fixture.preserveWidgets();
    Fixture.fakeResponseWriter();
    FileUploadLCA lca = new FileUploadLCA();
    lca.preserveValues( upload );
    lca.renderChanges( upload );
    String allMarkup = Fixture.getAllMarkup();
    assertTrue( allMarkup.indexOf( "w.submit" ) == -1 );
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

  private IFileUploadAdapter getFileUploadAdapter( FileUpload upload ) {
    return ( IFileUploadAdapter )upload.getAdapter( IFileUploadAdapter.class );
  }
}
