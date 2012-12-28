/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.custom;

import static org.junit.Assert.assertTrue;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public final class BusyIndicator_Test {

  @Before
  public void setUp() {
    Fixture.setUp();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
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

}
