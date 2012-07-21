/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.lifecycle;

import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.util.ParamCheck;
import org.eclipse.rap.rwt.service.IServiceStore;


/**
 * Instances of this class represent a JavaScript variable. In contrast to
 * Strings, JSVars are not enclosed in double quotes in the response.
 *
 * @since 2.0
 * @noextend This class is not intended to be subclassed by clients.
 * @deprecated As of RAP 1.5, the client-server protocol is based on JSON. This
 *             class is related to the old JavaScript-based protocol.
 */
@Deprecated
public final class JSVar {

  private static final String UNIQUE_NUMBER
    = JSVar.class.getName() + "#uniqueNumber";

  private final String name;

  /**
   * Creates a new JSVar instance with a generated name which is guaranteed to
   * be unique within the session.
   */
  public JSVar() {
    name = uniqueVarName();
  }

  /**
   * Creates a new JSVar instance with the given name.
   * @param name the variable name, must neither be <code>null</code> nor empty.

   */
  public JSVar( String name ) {
    ParamCheck.notNull( name, "name" );
    if( name.length() == 0 ) {
      String msg = "The argument 'name' must not be empty.";
      throw new IllegalArgumentException( msg );
    }
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }

  private static String uniqueVarName() {
    IServiceStore serviceStore = ContextProvider.getServiceStore();
    Object attribute = serviceStore.getAttribute( UNIQUE_NUMBER );
    Integer lastUniqueNumber = ( Integer )attribute;
    if( lastUniqueNumber == null ) {
      lastUniqueNumber = new Integer( -1 );
    }
    int uniqueNumber = lastUniqueNumber.intValue() + 1;
    serviceStore.setAttribute( UNIQUE_NUMBER, new Integer( uniqueNumber ) );
    return "v" + uniqueNumber;
  }
}
