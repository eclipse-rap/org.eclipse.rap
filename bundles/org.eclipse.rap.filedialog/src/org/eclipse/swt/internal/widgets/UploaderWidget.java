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

import org.eclipse.rap.rwt.widgets.FileUpload;
import org.eclipse.swt.SWT;


public class UploaderWidget implements Uploader {

  private final FileUpload fileUpload;

  public UploaderWidget( FileUpload fileUpload ) {
    this.fileUpload = fileUpload;
  }

  public void submit( String url ) {
    fileUpload.submit( url );
  }

  public void dispose() {
    if( !fileUpload.isDisposed() && ( fileUpload.getStyle() & SWT.MULTI ) != 0 ) {
      fileUpload.dispose();
    }
  }

}
