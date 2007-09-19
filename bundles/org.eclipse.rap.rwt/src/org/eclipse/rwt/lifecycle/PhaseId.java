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
 * <p>A type-safe enumeration that represents all standard lifecycle phases.</p>
 * These can be used for implementations of {@link PhaseListener}
 * @since 1.0
 */
public class PhaseId implements Comparable {
  
  private static int nextOrdinal;
  
  /**
   * <p>The PhaseId <code>ANY</code> is used by the 
   * {@link org.eclipse.rwt.engine.lifecycle.PhaseListener PhaseListener}
   * to signal interest in all phases.</p>
   */
  public static final PhaseId ANY = new PhaseId( "ANY" );
  
  /**
   * <p>The PhaseId <code>PREPARE_UI_ROOT</code> is used by the 
   * {@link org.eclipse.rwt.engine.lifecycle.PhaseListener PhaseListener}
   * to signal interest in the Prepare UI Root phase.</p>
   */
  public static final PhaseId PREPARE_UI_ROOT 
    = new PhaseId( "PREPARE_UI_ROOT" );
  
  /**
   * <p>The PhaseId <code>READ_DATA</code> is used by the 
   * {@link org.eclipse.rwt.engine.lifecycle.PhaseListener PhaseListener}
   * to signal interest in the read data phase.</p>
   */
  public static final PhaseId READ_DATA = new PhaseId( "READ_DATA" );
  
  /**
   * <p>The PhaseId <code>PROCESS_ACTION</code> is used by the 
   * {@link org.eclipse.rwt.engine.lifecycle.PhaseListener PhaseListener}
   * to signal interest in the process action phase.</p>
   */
  public static final PhaseId PROCESS_ACTION = new PhaseId( "PROCESS_ACTION" );
  
  /**
   * <p>The PhaseId <code>RENDER</code> is used by the 
   * {@link org.eclipse.rwt.engine.lifecycle.PhaseListener PhaseListener}
   * to signal interest in the render phase.</p>
   */
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