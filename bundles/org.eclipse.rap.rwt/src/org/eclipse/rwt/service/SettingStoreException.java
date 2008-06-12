/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.service;


/**
 * This exception indicated a setting store operation failed to complete
 * normally. 
 * 
 * @since 1.1 
 */
public class SettingStoreException extends Exception {

  private static final long serialVersionUID = 1L;
  
  /**
   * Constructs a {@link SettingStoreException} with the specified detail 
   * message and cause.
   * @param message the message; may be null. 
   *                Can be retrieved using {@link #getMessage()}.
   * @param cause the cause; may be <code>null</code> to indicate that
   *              the cause is not known. Can be retrieved using 
   *              {@link #getCause()}. 
   */
  public SettingStoreException( final String message,
                                final Throwable cause ) {
    super( message, cause );
  }
}
