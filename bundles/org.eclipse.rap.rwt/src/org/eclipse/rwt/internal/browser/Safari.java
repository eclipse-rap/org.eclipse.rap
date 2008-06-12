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
package org.eclipse.rwt.internal.browser;



/** 
 * <p>The base class for Safari browsers. No AJaX support.</p>
 */
public class Safari extends Default {

  public Safari( final boolean scriptEnabled ) {
    super( scriptEnabled );
  }

  public Safari( final boolean scriptEnabled, final boolean ajaxEnabled ) {
    super( scriptEnabled, false );
  }

  public Safari( final Browser browser ) {
    super( browser );
  }
  
  public boolean isXHTMLCapable() {
    return true;
  }
}
