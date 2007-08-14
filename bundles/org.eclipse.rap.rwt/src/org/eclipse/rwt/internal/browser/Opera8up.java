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
 * <p>The implementation for Opera 8 and higher.</p>
 */
public class Opera8up extends Opera {
  
  public Opera8up( final boolean scriptEnabled ) {
    super( scriptEnabled );
  }
  
  public Opera8up( final boolean scriptEnabled, final boolean ajaxEnabled ) {
    super( scriptEnabled, ajaxEnabled );
  }
  
  public Opera8up( final Browser browser ) {
    super( browser );
  }
  
  public boolean isXHTMLCapable() {
    return true;
  }
}
