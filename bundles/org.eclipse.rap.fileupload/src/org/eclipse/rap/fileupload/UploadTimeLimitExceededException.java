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

public class UploadTimeLimitExceededException extends Exception {

  private final long timeLimit;
  private final String fileName;

  /**
   * Constructs a <code>UploadTimeLimitExceededException</code> with permitted time.
   *
   * @param timeLimit The maximum permitted file upload duration in milliseconds.
   * @param fileName The name of the uploaded file when the execption occurs.
   *
   * @since 3.3
   */
  public UploadTimeLimitExceededException( long timeLimit, String fileName ) {
    this.timeLimit = timeLimit;
    this.fileName = fileName;
  }

  /**
   * Returns the maximum permitted file upload duration in milliseconds.
   */
  public long getTimeLimit() {
    return timeLimit;
  }

  /**
   * Returns the name of the uploaded file when the execption occurs.
   */
  public String getFileName() {
    return fileName;
  }

}
