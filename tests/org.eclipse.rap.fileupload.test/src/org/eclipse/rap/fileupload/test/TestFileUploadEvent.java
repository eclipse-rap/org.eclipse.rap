/*******************************************************************************
 * Copyright (c) 2011, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.fileupload.test;

import org.eclipse.rap.fileupload.FileDetails;
import org.eclipse.rap.fileupload.FileUploadEvent;
import org.eclipse.rap.fileupload.FileUploadHandler;


public class TestFileUploadEvent extends FileUploadEvent {

  public TestFileUploadEvent( FileUploadHandler handler ) {
    super( handler );
  }

  private static final long serialVersionUID = 1L;

  @Override
  public FileDetails[] getFileDetails() {
    return new FileDetails[ 0 ];
  }

  @Override
  public long getContentLength() {
    return 0;
  }

  @Override
  public long getBytesRead() {
    return 0;
  }

  @Override
  public Exception getException() {
    return null;
  }

}