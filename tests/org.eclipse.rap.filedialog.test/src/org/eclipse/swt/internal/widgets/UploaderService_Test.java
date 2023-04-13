/*******************************************************************************
 * Copyright (c) 2014, 2019 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.rap.rwt.client.Client;
import org.eclipse.rap.rwt.client.ClientFile;
import org.eclipse.rap.rwt.client.service.ClientFileUploader;
import org.eclipse.rap.rwt.internal.client.ClientFileImpl;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.junit.Rule;
import org.junit.Test;


@SuppressWarnings( "restriction" )
public class UploaderService_Test {

  @Rule public TestContext context = new TestContext();

  @Test
  public void testSubmit_callsClientFileUploaderService() {
    ClientFileUploader clientService = mockClientFileUploaderService();
    ClientFile[] clientFiles = new ClientFile[] { new ClientFileImpl( "fileId", "", "", 0 ) };
    UploaderService uploader = new UploaderService( clientFiles );

    uploader.submit( "foo" );

    verify( clientService ).submit( eq( "foo" ), same( clientFiles ) );
  }

  @Test
  public void testDispose_callsClientFileUploaderService() {
    ClientFileUploader clientService = mockClientFileUploaderService();
    ClientFile[] clientFiles = new ClientFile[] { new ClientFileImpl( "fileId", "", "", 0 ) };
    UploaderService uploader = new UploaderService( clientFiles );

    uploader.submit( "foo" );
    uploader.dispose();

    verify( clientService ).abort( anyString() );
  }

  private ClientFileUploader mockClientFileUploaderService() {
    ClientFileUploader service = mock( ClientFileUploader.class );
    when( service.submit( anyString(), any() ) ).thenReturn( "uploadId" );
    Client client = mock( Client.class );
    when( client.getService( ClientFileUploader.class ) ).thenReturn( service );
    context.replaceClient( client );
    return service;
  }

}
