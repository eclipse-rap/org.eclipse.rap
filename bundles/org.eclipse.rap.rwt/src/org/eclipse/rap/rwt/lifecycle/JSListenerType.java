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

import java.util.*;

import org.eclipse.rap.rwt.internal.protocol.IClientObject;


/**
 * @deprecated Use {@link IClientObject#listen(String, boolean)} instead
 * @since 2.0
 */
@Deprecated
public final class JSListenerType implements Comparable {

  public static final JSListenerType ACTION = new JSListenerType( "ACTION" );
  public static final JSListenerType STATE_AND_ACTION = new JSListenerType( "STATE_AND_ACTION" );

  private static int nextOrdinal;
  private final static JSListenerType[] values = new JSListenerType[] {
    ACTION,
    STATE_AND_ACTION
  };

  public static final List VALUES
    = Collections.unmodifiableList( Arrays.asList( values ) );

  private final String name;
  private final int ordinal;


  public JSListenerType( String name ) {
    this.name = name;
    ordinal = nextOrdinal++;
  }

  @Override
  public String toString() {
    return name;
  }

  public int compareTo( Object toCompare ) {
    return ordinal - ( ( JSListenerType )toCompare ).ordinal;
  }

  public int getOrdinal() {
    return ordinal;
  }
}
