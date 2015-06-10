/*******************************************************************************
 * Copyright (c) 2011, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.fileupload.test;

import org.eclipse.rap.fileupload.FileUploadEvent;
import org.eclipse.rap.fileupload.FileUploadListener;


public class TestFileUploadListener implements FileUploadListener {

  private FileUploadEvent lastEvent;
  protected StringBuffer log = new StringBuffer();

  @Override
  public void uploadProgress( FileUploadEvent event ) {
    this.lastEvent = event;
    log.append( "progress." );
  }

  @Override
  public void uploadFinished( FileUploadEvent event ) {
    this.lastEvent = event;
    log.append( "finished." );
  }

  @Override
  public void uploadFailed( FileUploadEvent event ) {
    this.lastEvent = event;
    log.append( "failed." );
  }

  public String getLog() {
    return log.toString();
  }

  public FileUploadEvent getLastEvent() {
    return lastEvent;
  }

}
