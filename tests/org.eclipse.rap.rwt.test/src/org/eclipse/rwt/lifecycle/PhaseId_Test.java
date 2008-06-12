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

package org.eclipse.rwt.lifecycle;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;


public class PhaseId_Test extends TestCase {
  
  public void testPhaseId() throws Exception {
    List values = PhaseId.VALUES;
    assertNotNull( values );
    assertEquals( 5, values.size() );
    
    Object phaseId0 = values.get( 0 );
    assertSame( PhaseId.ANY, phaseId0 );
    assertEquals( "ANY", PhaseId.ANY.toString() );
    
    Object phaseId1 = values.get( 1 );
    assertSame( PhaseId.PREPARE_UI_ROOT, phaseId1 );
    assertEquals( "PREPARE_UI_ROOT", PhaseId.PREPARE_UI_ROOT.toString() );
    
    Object phaseId2 = values.get( 2 );
    assertSame( PhaseId.READ_DATA, phaseId2 );
    assertEquals( "READ_DATA", PhaseId.READ_DATA.toString() );
    
    Object phaseId3 = values.get( 3 );
    assertSame( PhaseId.PROCESS_ACTION, phaseId3 );
    assertEquals( "PROCESS_ACTION", PhaseId.PROCESS_ACTION.toString() );
    
    Object phaseId4 = values.get( 4 );
    assertSame( PhaseId.RENDER, phaseId4 );
    assertEquals( "RENDER", PhaseId.RENDER.toString() );
    
    Object[] phases = values.toArray();
    Arrays.sort( phases );
    for( int i = 0; i < phases.length; i++ ) {
      assertSame( values.get( i ), phases[ i ] );
    }
    
    assertSame( PhaseId.ANY, values.get( PhaseId.ANY.getOrdinal() ) );
    assertSame( PhaseId.PREPARE_UI_ROOT, 
                values.get( PhaseId.PREPARE_UI_ROOT.getOrdinal() ) );
    assertSame( PhaseId.READ_DATA, 
                values.get( PhaseId.READ_DATA.getOrdinal() ) );
    assertSame( PhaseId.PROCESS_ACTION, 
                values.get( PhaseId.PROCESS_ACTION.getOrdinal() ) );
    assertSame( PhaseId.RENDER, 
                values.get( PhaseId.RENDER.getOrdinal() ) );
  }
}
