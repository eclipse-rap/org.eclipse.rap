/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal;

/** 
 * <p>This interface provides the common W4 Toolkit parameter settings
 * for the file upload mechanism.</p> 
 * <p>This interface is not intended to be implemented by clients.</p>
 * @see org.eclipse.rwt.internal.IConfiguration 
 */
public interface IFileUpload {

  /**
   * <p>Returns the maximum allowed upload size in bytes or -1 for 
   * no maximum.</p>
   */
  long getMaxUploadSize();

  /**
   * <p>Returns the threshold of the allowed upload size in bytes which is 
   * kept in memory.</p>
   */
  int getMaxMemorySize();
}
