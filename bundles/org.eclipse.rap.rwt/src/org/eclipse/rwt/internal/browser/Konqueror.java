/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.browser;


/**
 * <p>
 * the basic implementation for Konqueror browsers.
 * </p>
 */
public class Konqueror extends Default {

  public Konqueror( final boolean scriptEnabled ) {
    super( scriptEnabled );
  }

  public Konqueror( final boolean scriptEnabled, final boolean ajaxEnabled ) {
    super( scriptEnabled, ajaxEnabled );
  }

  public Konqueror( final Browser browser ) {
    super( browser );
  }
}
