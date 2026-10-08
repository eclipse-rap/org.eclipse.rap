/*******************************************************************************
 * Copyright (c) 2012, 2013 EclipseSource and others.
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
package org.eclipse.rap.rwt.internal.widgets.fileuploadkit;

import org.eclipse.rap.rwt.widgets.FileUpload;
import org.eclipse.swt.internal.widgets.controlkit.ControlThemeAdapterImpl;


public class FileUploadThemeAdapter extends ControlThemeAdapterImpl {

  public int getSpacing( FileUpload fileUpload ) {
    return getCssDimension( "FileUpload", "spacing", fileUpload );
  }

}
