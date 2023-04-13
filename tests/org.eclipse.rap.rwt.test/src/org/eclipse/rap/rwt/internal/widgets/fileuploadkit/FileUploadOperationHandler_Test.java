/*******************************************************************************
 * Copyright (c) 2013, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.widgets.fileuploadkit;

import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_SELECTION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.widgets.FileUpload;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


public class FileUploadOperationHandler_Test {

  private FileUpload fileUpload;
  private FileUploadOperationHandler handler;

  @Before
  public void setUp() {
    Fixture.setUp();
    Shell shell = new Shell( new Display() );
    fileUpload = new FileUpload( shell, SWT.MULTI );
    handler = new FileUploadOperationHandler( fileUpload );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testHandleSetEnabled() {
    JsonObject properties = new JsonObject().add( "enabled", false );

    handler.handleSet( fileUpload, properties );

    assertFalse( fileUpload.getEnabled() );
  }

  @Test
  public void testHandleSetFileNames() {
    JsonArray value = new JsonArray().add( "foo" ).add( "bar" );
    handler.handleSet( new JsonObject().add( "fileNames", value ) );

    String[] fileNames = fileUpload.getFileNames();
    assertEquals( "foo", fileNames[ 0 ] );
    assertEquals( "bar", fileNames[ 1 ] );
    assertEquals( "foo", fileUpload.getFileName() );
  }

  @Test
  public void testHandleSetFileNames_emptyArray() {
    handler.handleSet( new JsonObject().add( "fileNames", new JsonArray() ) );

    assertEquals( 0, fileUpload.getFileNames().length );
    assertNull( fileUpload.getFileName() );
  }

  @Test
  public void testHandleNotifySelection() {
    fileUpload = mock( FileUpload.class );
    handler = new FileUploadOperationHandler( fileUpload );
    JsonObject properties = new JsonObject().add( "altKey", true ).add( "shiftKey", true );

    handler.handleNotify( EVENT_SELECTION, properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( fileUpload ).notifyListeners( eq( SWT.Selection ), captor.capture() );
    assertEquals( SWT.ALT | SWT.SHIFT, captor.getValue().stateMask );
  }

}
