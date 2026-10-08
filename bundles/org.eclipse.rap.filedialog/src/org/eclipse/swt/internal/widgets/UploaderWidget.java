/*******************************************************************************
 * Copyright (c) 2014, 2015 EclipseSource and others.
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
package org.eclipse.swt.internal.widgets;

import org.eclipse.rap.rwt.widgets.FileUpload;


public class UploaderWidget implements Uploader {

  private final FileUpload fileUpload;

  public UploaderWidget( FileUpload fileUpload ) {
    this.fileUpload = fileUpload;
  }

  @Override
  public void submit( String url ) {
    fileUpload.submit( url );
  }

  @Override
  public void dispose() {
    if( !fileUpload.isDisposed() ) {
      fileUpload.dispose();
    }
  }

}
