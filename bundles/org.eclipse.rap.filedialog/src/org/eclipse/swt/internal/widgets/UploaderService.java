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

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.ClientFile;
import org.eclipse.rap.rwt.client.service.ClientFileUploader;


public class UploaderService implements Uploader {

  private final ClientFile[] clientFiles;

  public UploaderService( ClientFile[] clientFiles ) {
    this.clientFiles = clientFiles;
  }

  public void submit( String url ) {
    ClientFileUploader service = RWT.getClient().getService( ClientFileUploader.class );
    if( service != null ) {
      service.submit( url, clientFiles );
    }
  }

  public void dispose() {
  }

}
