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

package org.eclipse.rwt.lifecycle;

import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.IServiceStateInfo;
import org.eclipse.rwt.internal.util.ParamCheck;


/**
 * TODO [rh] JavaDoc
 */
public final class JSVar {
  
  private static final String UNIQUE_NUMBER 
    = JSVar.class.getName() + "#uniqueNumber";
  
  private final String name;
  
  public JSVar() {
    name = uniqueVarName();
  }

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

  private static String uniqueVarName() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    Object attribute = stateInfo.getAttribute( UNIQUE_NUMBER );
    Integer lastUniqueNumber = ( Integer )attribute;
    if( lastUniqueNumber == null ) {
      lastUniqueNumber = new Integer( -1 );
    }
    int uniqueNumber = lastUniqueNumber.intValue() + 1;
    stateInfo.setAttribute( UNIQUE_NUMBER, new Integer( uniqueNumber ) );
    return "v" + uniqueNumber;
  }
}
