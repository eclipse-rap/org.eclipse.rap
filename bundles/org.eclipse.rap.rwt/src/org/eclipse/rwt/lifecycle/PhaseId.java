/*******************************************************************************
 * Copyright (c) 2002-2007 Innoopract Informationssysteme GmbH.
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
 * A type-safe enumeration that represents all standard life cycle phases. The
 * instances can be used to refer to a phase in implementations of
 * {@link PhaseListener}.
 * 
 * @since 1.0
 */
public class PhaseId implements Comparable {

  private static int nextOrdinal;
  
  /**
   * The PhaseId <code>ANY</code> is used by the {@link PhaseListener} to
   * signal interest in all phases.
   */
  public static final PhaseId ANY = new PhaseId( "ANY" );

  /**
   * The PhaseId <code>PREPARE_UI_ROOT</code> is used by the
   * {@link PhaseListener} to signal interest in the <em>Prepare UI Root</em>
   * phase.
   */
  public static final PhaseId PREPARE_UI_ROOT
    = new PhaseId( "PREPARE_UI_ROOT" );
  
  /**
   * The PhaseId <code>READ_DATA</code> is used by the {@link PhaseListener}
   * to signal interest in the <em>Read Data</em> phase.
   */
  public static final PhaseId READ_DATA = new PhaseId( "READ_DATA" );
  
  /**
   * The PhaseId <code>PROCESS_ACTION</code> is used by the
   * {@link PhaseListener} to signal interest in the <em>Process Action</em>
   * phase.
   */
  public static final PhaseId PROCESS_ACTION = new PhaseId( "PROCESS_ACTION" );
  
  /**
   * The PhaseId <code>RENDER</code> is used by the {@link PhaseListener} to
   * signal interest in the <em>Render</em> phase.
   */
  public static final PhaseId RENDER = new PhaseId( "RENDER" );
  
  private final static PhaseId[] values = new PhaseId[] {
    ANY,
    PREPARE_UI_ROOT,
    READ_DATA,
    PROCESS_ACTION,
    RENDER
  };
  
  /**
   * A list containing the instances of this enumeration.
   */
  // TODO [rst] Does this have to be public?
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
  
  /**
   * Returns the ordinal number that is used for comparison of PhaseIds.
   *
   * @return the ordinal number
   */
  // TODO [rst] Does this have to be public?
  public int getOrdinal() {
    return ordinal;
  }
}