/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.custom;

import junit.framework.TestCase;

import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.widgets.Display;


public final class BusyIndicator_Test extends TestCase {
  
  public void testShowWhile() {
    final boolean[] executed = new boolean[]{ false };
    Runnable runnable = new Runnable() {
      public void run() {
        executed[ 0 ] = true;
      }
    };
    Display display = new Display();
    // Test runnable execution
    executed[ 0 ] = false;
    BusyIndicator.showWhile( display, runnable );
    assertTrue( executed[ 0 ] );
    executed[ 0 ] = false;
    BusyIndicator.showWhile( null, runnable );
    assertTrue( executed[ 0 ] );
    // Test illegal arguments
    try {
      BusyIndicator.showWhile( null, null );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
