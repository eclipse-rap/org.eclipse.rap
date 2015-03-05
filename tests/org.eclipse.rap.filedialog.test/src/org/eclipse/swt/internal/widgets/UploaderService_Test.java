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
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.rap.rwt.client.Client;
import org.eclipse.rap.rwt.client.ClientFile;
import org.eclipse.rap.rwt.client.service.ClientFileUploader;
import org.eclipse.rap.rwt.internal.client.ClientFileImpl;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings( "restriction" )
public class UploaderService_Test {

  @Before
  public void setUp() {
    Fixture.setUp();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testSubmit_callsClientFileUploaderService() {
    ClientFileUploader clientService = mockClientFileUploaderService();
    ClientFile[] clientFiles = new ClientFile[] { new ClientFileImpl( "fileId", "", "", 0 ) };
    UploaderService uploader = new UploaderService( clientFiles );

    uploader.submit( "foo" );

    verify( clientService ).submit( eq( "foo" ), same( clientFiles ) );
  }

  private static ClientFileUploader mockClientFileUploaderService() {
    ClientFileUploader service = mock( ClientFileUploader.class );
    Client client = mock( Client.class );
    when( client.getService( ClientFileUploader.class ) ).thenReturn( service );
    Fixture.fakeClient( client );
    return service;
  }

}
