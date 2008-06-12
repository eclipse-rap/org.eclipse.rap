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


/** <p>A default implementation for the org.eclipse.rap.Browser class. Subclasses of 
  * Browser represent vendor-specific and version-specific
  * information about the web browser that is used on the client side to 
  * display the pages from the current session.</p>
  */
public class Default extends Browser {
  
  public Default( final boolean scriptEnabled ) {
    super( scriptEnabled );
  }
  
  public Default( final boolean scriptEnabled, final boolean ajaxEnabled ) {
    super( scriptEnabled, ajaxEnabled );
  }
  
  public Default( final Browser browser ) {
    super( browser );
  }
}
