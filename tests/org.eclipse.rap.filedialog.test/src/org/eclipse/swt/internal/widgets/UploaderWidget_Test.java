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
package org.eclipse.swt.internal.widgets;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.eclipse.rap.rwt.widgets.FileUpload;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.junit.Test;


public class UploaderWidget_Test {

  @Test
  public void testSubmit_triggersFileUploadSubmit() {
    FileUpload fileUpload = mock( FileUpload.class );
    UploaderWidget uploader = new UploaderWidget( fileUpload );

    uploader.submit( "foo" );

    verify( fileUpload ).submit( eq( "foo" ) );
  }

  @Test
  public void testDispose_doesNotTriggersFileUploadDisposeOnSingle() {
    FileUpload fileUpload = mock( FileUpload.class );
    UploaderWidget uploader = new UploaderWidget( fileUpload );

    uploader.dispose();

    verify( fileUpload, never() ).dispose();
  }

  @Test
  public void testDispose_triggersFileUploadDisposeOnMulti() {
    FileUpload fileUpload = mock( FileUpload.class );
    doReturn( Integer.valueOf( SWT.MULTI ) ).when( fileUpload ).getStyle();
    UploaderWidget uploader = new UploaderWidget( fileUpload );

    uploader.dispose();

    verify( fileUpload ).dispose();
  }

  @Test
  public void testDispose_withAlreadyDisposedFileUpload() {
    FileUpload fileUpload = mock( FileUpload.class );
    doReturn( Boolean.TRUE).when( fileUpload ).isDisposed();
    doThrow( SWTException.class ).when( fileUpload ).getStyle();
    UploaderWidget uploader = new UploaderWidget( fileUpload );

    uploader.dispose();

    verify( fileUpload, never() ).dispose();
  }

}
