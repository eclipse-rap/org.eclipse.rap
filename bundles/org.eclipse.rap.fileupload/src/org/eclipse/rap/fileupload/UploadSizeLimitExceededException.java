/*******************************************************************************
 * Copyright (c) 2013, 2017 EclipseSource and others.
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

public class UploadSizeLimitExceededException extends Exception {

  private final long sizeLimit;
  private final String fileName;

  /**
   * Constructs a <code>UploadSizeLimitExceededException</code> with permitted size.
   *
   * @param sizeLimit The maximum permitted file upload size in bytes.
   * @param fileName The name of the uploaded file when the execption occurs.
   *
   * @since 3.3
   */
  public UploadSizeLimitExceededException( long sizeLimit, String fileName ) {
    this.sizeLimit = sizeLimit;
    this.fileName = fileName;
  }

  /**
   * Returns the maximum permitted file upload size in bytes.
   */
  public long getSizeLimit() {
    return sizeLimit;
  }

  /**
   * Return the name of the uploaded file when the execption occurs.
   */
  public String getFileName() {
    return fileName;
  }

}
