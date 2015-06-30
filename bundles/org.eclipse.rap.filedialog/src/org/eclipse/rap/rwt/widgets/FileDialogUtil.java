/*******************************************************************************
 * Copyright (c) 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hannes Erven - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.widgets;

import org.eclipse.rap.rwt.client.ClientFile;
import org.eclipse.rap.rwt.dnd.ClientFileTransfer;
import org.eclipse.swt.internal.widgets.FileDialogAdapter;
import org.eclipse.swt.widgets.FileDialog;


/**
 * Utility class to enable access to RAP specific extensions to FileDialog.
 *
 * @since 3.1
 */
public abstract class FileDialogUtil {

  /**
   * Sets initial client files to be uploaded. The upload of these files will start immediately
   * after opening the dialog. Hence, this method must be called before opening the dialog.
   * <p>
   * A user can drag and drop files from the client operating system on any control with a drop
   * listener attached. In this case, the client files can be obtained from the
   * {@link ClientFileTransfer} object. A FileDialog can then be used to handle the upload and
   * display upload progress.
   * </p>
   *
   * @param files an array of client files to be added to the dialog
   */
  public static void setClientFiles( FileDialog dialog, ClientFile[] files ) {
    dialog.getAdapter( FileDialogAdapter.class ).setClientFiles( files );
  }

}
