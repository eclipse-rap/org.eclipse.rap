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

package org.eclipse.rwt.internal.lifecycle;

import java.util.*;


public class Scope implements Comparable {

  public static final Scope APPLICATION = new Scope( "APPLICATION" );
  public static final Scope SESSION = new Scope( "SESSION" );

  private static int nextOrdinal;
  private final static Scope[] values = new Scope[] {
    APPLICATION,
    SESSION
  };
  
  public static final List VALUES 
    = Collections.unmodifiableList( Arrays.asList( values ) );
  
  private final String scopeName;
  private final int ordinal;

  
  private Scope( final String scopeName ) {
    this.scopeName = scopeName;
    this.ordinal = nextOrdinal++;
  }
  
  public String toString() {
    return scopeName;
  }

  public int compareTo( final Object toCompare ) {
    return this.ordinal - ( ( Scope )toCompare ).ordinal;
  }

  public int getOrdinal() {
    return ordinal;
  }
}
