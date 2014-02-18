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
package org.eclipse.rap.rwt.internal.client;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.internal.dnd.RemoteFile;
import org.eclipse.rap.rwt.internal.remote.ConnectionImpl;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class FileUploaderImpl_Test {

  private static final String REMOTE_ID = "rwt.client.FileUploader";

  @Before
  public void setUp() {
    Fixture.setUp();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testCreatesRemoteObjectWithCorrectId() {
    ConnectionImpl connection = fakeConnection( mock( RemoteObject.class ) );

    new FileUploaderImpl();

    verify( connection ).createServiceObject( eq( REMOTE_ID ) );
  }

  @Test
  public void testSubmit_createsCallOperation() {
    RemoteObject remoteObject = mock( RemoteObject.class );
    fakeConnection( remoteObject );
    FileUploaderImpl uploader = new FileUploaderImpl();
    RemoteFile[] files = new RemoteFile[]{
      new RemoteFile( "fileId1" ),
      new RemoteFile( "fileId2" )
    };

    uploader.submit( "fooURL", files );

    JsonObject expected = new JsonObject()
      .add( "url", "fooURL" )
      .add( "fileIds", new JsonArray().add( "fileId1" ).add( "fileId2" ) );
    verify( remoteObject ).call( eq( "submit" ), eq( expected ) );
  }


  private static ConnectionImpl fakeConnection( RemoteObject remoteObject ) {
    ConnectionImpl connection = mock( ConnectionImpl.class );
    when( connection.createServiceObject( anyString() ) ).thenReturn( remoteObject );
    Fixture.fakeConnection( connection );
    return connection;
  }

}
