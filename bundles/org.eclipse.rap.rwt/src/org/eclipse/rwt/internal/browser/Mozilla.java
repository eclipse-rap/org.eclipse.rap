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
 * <p>
 * the basic implementation for Mozilla browsers.
 * </p>
 */
public class Mozilla extends Default {

  public Mozilla( final boolean scriptEnabled ) {
    super( scriptEnabled );
    this.ajaxCapable = true;
  }

  public Mozilla( final boolean scriptEnabled, final boolean ajaxEnabled ) {
    super( scriptEnabled, ajaxEnabled );
    this.ajaxCapable = true;
  }

  public Mozilla( final Browser browser ) {
    super( browser );
  }
  
  public boolean isXHTMLCapable() {
    return true;
  }
}
