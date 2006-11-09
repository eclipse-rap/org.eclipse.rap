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

package org.eclipse.rap.rwt.lifecycle;

import com.w4t.ParamCheck;

/**
 * TODO [rh] JavaDoc
 */
public final class JSVar {
  
  private final String name;
  
  public JSVar( final String name ) {
    ParamCheck.notNull( name, "name" );
    if( name.length() == 0 ) {
      String msg = "The argument 'name' must not be empty.";
      throw new IllegalArgumentException( msg );
    }
    this.name = name;
  }
  
  public String toString() {
    return name;
  }
}
