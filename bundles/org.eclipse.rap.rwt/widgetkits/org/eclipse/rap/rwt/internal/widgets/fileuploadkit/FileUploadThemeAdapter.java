/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.widgets.fileuploadkit;

import org.eclipse.rap.rwt.widgets.FileUpload;
import org.eclipse.swt.internal.widgets.controlkit.ControlThemeAdapter;


public final class FileUploadThemeAdapter extends ControlThemeAdapter {

  public int getSpacing( FileUpload fileUpload ) {
    return getCssDimension( "FileUpload", "spacing", fileUpload );
  }

}