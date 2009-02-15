/*******************************************************************************
 * Copyright (c) 2007, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.theme;


public final class ThemeManagerException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ThemeManagerException( final String message ) {
    super( message );
  }

  public ThemeManagerException( final String message, final Throwable cause ) {
    super( message, cause );
  }
}
