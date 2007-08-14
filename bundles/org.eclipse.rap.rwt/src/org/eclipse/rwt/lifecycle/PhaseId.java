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

import java.util.*;

/**
 * <p>A typesafe enumeration that represents all standard lifecycle phases.</p>
 * <p>The PhaseId <code>ANY</code> is used by the 
 * {@link org.eclipse.rwt.engine.lifecycle.PhaseListener PhaseListener}
 * to signal interest in all phaases.</p>
 */
public class PhaseId implements Comparable {
  
  private static int nextOrdinal;
  
  public static final PhaseId ANY = new PhaseId( "ANY" );
  public static final PhaseId PREPARE_UI_ROOT 
    = new PhaseId( "PREPARE_UI_ROOT" );
  public static final PhaseId READ_DATA = new PhaseId( "READ_DATA" );
  public static final PhaseId PROCESS_ACTION = new PhaseId( "PROCESS_ACTION" );
  public static final PhaseId RENDER = new PhaseId( "RENDER" );
  
  private final static PhaseId[] values = new PhaseId[] {
    ANY,
    PREPARE_UI_ROOT,
    READ_DATA,
    PROCESS_ACTION,
    RENDER
  };
  
  public static final List VALUES 
    = Collections.unmodifiableList( Arrays.asList( values ) );
  
  private final String phaseName;
  private final int ordinal;

  private PhaseId( final String phaseName ) {
    this.phaseName = phaseName;
    this.ordinal = nextOrdinal++;
  }
  
  public String toString() {
    return phaseName;
  }

  public int compareTo( final Object toCompare ) {
    return this.ordinal - ( ( PhaseId )toCompare ).ordinal;
  }

  public int getOrdinal() {
    return ordinal;
  }
}