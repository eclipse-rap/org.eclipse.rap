/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets;

import org.eclipse.swt.internal.SerializableCompatibility;
import org.eclipse.swt.widgets.Widget;


/**
 * Utility class to generate ids for synchronizable widgets that are unique
 * within a session.
 * <p>
 * Note that this implementation is not synchronized. If multiple threads access
 * methods of this class concurrently, they <i>must</i> be synchronized
 * externally.
 * </p>
 */
public final class IdGeneratorImpl implements IdGenerator, SerializableCompatibility {

  private int lastId = 1;

  IdGeneratorImpl() {
    // prevent instantiation from outside
  }

  public String createId( Object object ) {
    lastId++;
    String prefix = "o";
    if( object instanceof String ) {
      prefix = ( String )object;
    } else if( object instanceof Widget ) {
      prefix = "w";
    }
    return prefix + lastId;
  }
}
