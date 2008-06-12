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
 * <p>The implementation for Opera 8 and higher.</p>
 */
public class Opera9 extends Opera8up {
  
  public Opera9( final boolean scriptEnabled ) {
    super( scriptEnabled );
    this.ajaxCapable = true;
  }
  
  public Opera9( final boolean scriptEnabled, final boolean ajaxEnabled ) {
    super( scriptEnabled, ajaxEnabled );
    this.ajaxCapable = true;
  }
  
  public Opera9( final Browser browser ) {
    super( browser );
  }
}
