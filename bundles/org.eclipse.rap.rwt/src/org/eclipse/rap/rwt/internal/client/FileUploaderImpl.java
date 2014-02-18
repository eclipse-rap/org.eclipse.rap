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

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.dnd.RemoteFile;
import org.eclipse.rap.rwt.internal.remote.ConnectionImpl;
import org.eclipse.rap.rwt.remote.RemoteObject;



public class FileUploaderImpl implements FileUploader {

  private static final String REMOTE_ID = "rwt.client.FileUploader";
  private final RemoteObject remoteObject;

  public FileUploaderImpl() {
    ConnectionImpl connection = ( ConnectionImpl )RWT.getUISession().getConnection();
    remoteObject = connection.createServiceObject( REMOTE_ID );
  }

  public void submit( String url, RemoteFile[] remoteFiles ) {
    JsonArray fileIds = new JsonArray();
    for( RemoteFile file : remoteFiles ) {
      fileIds.add( file.getFileId() );
    }
    JsonObject parameters = new JsonObject() .add( "url", url ) .add( "fileIds", fileIds );
    remoteObject.call( "submit", parameters );
  }

}
