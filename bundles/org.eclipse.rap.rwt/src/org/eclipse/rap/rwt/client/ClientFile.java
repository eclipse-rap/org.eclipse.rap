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
package org.eclipse.rap.rwt.client;


/**
 * Represents a file on the client
 *
 * @since 2.3
 */
public interface ClientFile {

  /**
   * The name of the file as it is known to the client, without path information.
   *
   * @return the file name as a string. May be empty, but never null.
   */
  String getName();

  /**
   * The string in lower case representing the MIME type of the File.
   *
   * @return the type of the file or an empty string if the type is unknown. Is never null.
   */
  String getType();

  /**
   * Returns the size of the File on the client.
   *
   * @return the size in bytes.
   */
  long getSize();

}
