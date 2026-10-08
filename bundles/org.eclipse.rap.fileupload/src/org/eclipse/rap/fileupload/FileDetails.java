/*******************************************************************************
 * Copyright (c) 2013, 2015 EclipseSource and others.
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
package org.eclipse.rap.fileupload;


/**
 * Provides details of the uploaded file like file name, content-type and size
 */
public interface FileDetails {

  /**
   * The content type as transmitted by the uploading client.
   *
   * @return the content type or <code>null</code> if unknown
   */
  String getContentType();

  /**
   * The original file name of the uploaded file, as transmitted by the client. If a path was
   * included in the request, it is stripped off.
   *
   * @return the plain file name without any path segments
   */
  String getFileName();

}
