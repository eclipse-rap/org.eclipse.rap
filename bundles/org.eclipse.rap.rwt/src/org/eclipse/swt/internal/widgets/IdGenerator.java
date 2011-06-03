/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets;

import org.eclipse.rwt.SessionSingletonBase;
import org.eclipse.swt.internal.SerializableCompatibility;

/**
 * <p>Utility class to generate id's for widgets that are unique within a 
 * session.</p>
 * <p>Note that this implementation is not synchronized. If multiple
 * threads access methods of this class concurrently, they <i>must</i> be 
 * synchronized externally.
 * </p>
 */
final class IdGenerator implements SerializableCompatibility {
  private static final long serialVersionUID = 1L;

  static IdGenerator getInstance() {
    return ( IdGenerator )SessionSingletonBase.getInstance( IdGenerator.class );
  }
  
  private int lastId = 1;
  
  IdGenerator() {
    // prevent instantiation from outside
  }
  
  String newId() {
    lastId++;
    return new StringBuffer( "w" ).append( lastId ).toString();
  }
}
