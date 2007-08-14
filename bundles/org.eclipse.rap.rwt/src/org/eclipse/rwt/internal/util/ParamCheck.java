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
package org.eclipse.rwt.internal.util;

import java.text.MessageFormat;


/** <p>Utility class for doing common method parameter checks.</p>
 */
public final class ParamCheck {
  
  private static final String NOT_NULL_TEXT 
    = "The parameter ''{0}'' must not be null.";

  private ParamCheck() {
    // prevent instantiation
  }
  
  /** <p>Checks whether the given <code>param</code> is <code>null</code> and 
   *  throws a <code>NullPointerException</code> with the message 'The 
   *  parameter <em>paramName</em> must not be null.' if so.</p>
   * 
   *  @param param the object which must not be null.
   *  @param paramName the human-readable name of the <code>param</code>. */
  public static void notNull( final Object param, final String paramName ) {
    if ( param == null ) {
      Object[] args = new Object[] { paramName };
      String msg = MessageFormat.format( NOT_NULL_TEXT, args );
      throw new NullPointerException( msg );
    }
  }
}
