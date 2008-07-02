/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal;

import org.eclipse.rwt.RWT;

public final class RWTMessages {

  private static final String BUNDLE_NAME = "org.eclipse.rwt.internal.messages"; //$NON-NLS-1$
  
  public String RWT_SessionTimeoutPageTitle;

  public String RWT_SessionTimeoutPageHeadline;
  
  public String RWT_SessionTimeoutPageMessage;

  public String RWT_MultipleInstancesError;

  /**
   * @return the session/request specific localized messages object
   */
  public static RWTMessages get() {
    Class clazz = RWTMessages.class;
    Object result = RWT.NLS.getISO8859_1Encoded( BUNDLE_NAME, clazz );
    return ( RWTMessages )result;
  }

  private RWTMessages() {
    // prevent instantiation
  }
}
