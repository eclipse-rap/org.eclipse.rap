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

package org.eclipse.swt.internal.widgets;

import com.w4t.SessionSingletonBase;

/**
 * <p>Utility class to generate id's for widgets that are unique within a 
 * session.</p>
 * <p>Note that this implementation is not synchronized. If multiple
 * threads access methods of this class concurrently, they <i>must</i> be 
 * synchronized externally.
 * </p>
 */
final class IdGenerator extends SessionSingletonBase {
  
  /**
   * <p>Returns the session-wide singleton instance of this class.</p>
   */
  static IdGenerator getInstance() {
    return ( IdGenerator )getInstance( IdGenerator.class );
  }
  
  private int lastId = 0;
  
  private IdGenerator() {
    // prevent instantiation from outside
  }
  
  /**
   * <p>Returns a session-wide unique integer. The first value returned
   * is 1.</p>
   */
  String newId() {
    lastId++;
    return new StringBuffer( "w" ).append( lastId ).toString();
  }
}
