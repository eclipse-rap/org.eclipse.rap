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
package org.eclipse.swt.internal.widgets;

import org.eclipse.rap.rwt.SingletonUtil;
import org.eclipse.swt.internal.SerializableCompatibility;


/**
 * Utility class to generate ids for synchronizable widgets that are unique
 * within a session.
 * <p>
 * Note that this implementation is not synchronized. If multiple threads access
 * methods of this class concurrently, they <i>must</i> be synchronized
 * externally.
 * </p>
 */
public final class IdGenerator implements SerializableCompatibility {

  public static IdGenerator getInstance() {
    return SingletonUtil.getSessionInstance( IdGenerator.class );
  }

  private int lastId = 1;

  IdGenerator() {
    // prevent instantiation from outside
  }

  public String newId( String prefix ) {
    lastId++;
    return prefix + lastId;
  }
}
