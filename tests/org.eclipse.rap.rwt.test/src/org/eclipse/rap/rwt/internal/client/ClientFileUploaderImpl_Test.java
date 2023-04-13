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
package org.eclipse.rap.rwt.internal.client;

import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.client.ClientFile;
import org.eclipse.rap.rwt.internal.remote.ConnectionImpl;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ClientFileUploaderImpl_Test {

  private static final String REMOTE_ID = "rwt.client.FileUploader";

  private ClientFileUploaderImpl uploader;

  @Before
  public void setUp() {
    Fixture.setUp();
    uploader = new ClientFileUploaderImpl();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testCreatesRemoteObjectWithCorrectId() {
    ConnectionImpl connection = fakeConnection( mock( RemoteObject.class ) );

    new ClientFileUploaderImpl();

    verify( connection ).createServiceObject( eq( REMOTE_ID ) );
  }

  @Test( expected = NullPointerException.class )
  public void testSubmit_failsWithNullUrl() {
    uploader.submit( null, new ClientFile[ 0 ] );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSubmit_failsWithEmptyUrl() {
    uploader.submit( "", new ClientFile[ 0 ] );
  }

  @Test( expected = NullPointerException.class )
  public void testSubmit_failsWithNullClientFiles() {
    uploader.submit( "fooURL", null );
  }

  @Test
  public void testSubmit_withEmptyClientFilesDoesNotCreateCallOperation() {
    RemoteObject remoteObject = mock( RemoteObject.class );
    fakeConnection( remoteObject );

    uploader = new ClientFileUploaderImpl();
    uploader.submit( "fooURL", new ClientFile[ 0 ] );

    verify( remoteObject, never() ).call( eq( "submit" ), any( JsonObject.class) );
  }

  @Test
  public void testSubmit_createsCallOperation() {
    RemoteObject remoteObject = mock( RemoteObject.class );
    fakeConnection( remoteObject );
    ClientFile[] files = new ClientFile[]{
      new ClientFileImpl( "fileId1", "", "", 0 ),
      new ClientFileImpl( "fileId2", "", "", 0 )
    };

    uploader = new ClientFileUploaderImpl();
    uploader.submit( "fooURL", files );

    JsonObject expected = new JsonObject()
      .add( "url", "fooURL" )
      .add( "fileIds", new JsonArray().add( "fileId1" ).add( "fileId2" ) )
      .add( "uploadId", "upload_0" );
    verify( remoteObject ).call( eq( "submit" ), eq( expected ) );
  }

  @Test
  public void testSubmit_returnsDifferentIds() {
    RemoteObject remoteObject = mock( RemoteObject.class );
    fakeConnection( remoteObject );
    ClientFile[] files = new ClientFile[]{
      new ClientFileImpl( "fileId1", "", "", 0 ),
      new ClientFileImpl( "fileId2", "", "", 0 )
    };

    uploader = new ClientFileUploaderImpl();
    String id1 = uploader.submit( "fooURL", files );
    String id2 = uploader.submit( "fooURL", files );

    assertNotEquals( id1, id2 );
  }

  @Test( expected = NullPointerException.class )
  public void testAbort_failsWithNullId() {
    uploader.abort( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testAbort_failsWithEmptyId() {
    uploader.abort( "" );
  }

  @Test
  public void testAbort_createsCallOperation() {
    RemoteObject remoteObject = mock( RemoteObject.class );
    fakeConnection( remoteObject );
    ClientFile[] files = new ClientFile[]{
      new ClientFileImpl( "fileId1", "", "", 0 ),
      new ClientFileImpl( "fileId2", "", "", 0 )
    };

    uploader = new ClientFileUploaderImpl();
    String id = uploader.submit( "fooURL", files );
    reset( remoteObject );
    uploader.abort( id );

    JsonObject expected = new JsonObject().add( "uploadId", "upload_0" );
    verify( remoteObject ).call( eq( "abort" ), eq( expected ) );
  }

  private static ConnectionImpl fakeConnection( RemoteObject remoteObject ) {
    ConnectionImpl connection = mock( ConnectionImpl.class );
    when( connection.createServiceObject( anyString() ) ).thenReturn( remoteObject );
    Fixture.fakeConnection( connection );
    return connection;
  }

}
