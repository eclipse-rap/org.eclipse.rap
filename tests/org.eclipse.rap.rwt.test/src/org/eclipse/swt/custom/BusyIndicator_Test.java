/*******************************************************************************
 * Copyright (c) 2007, 2015 Innoopract Informationssysteme GmbH and others.
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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.swt.widgets.Display;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public final class BusyIndicator_Test {

  @Rule
  public TestContext context = new TestContext();

  private Display display;

  @Before
  public void setUp() {
    display = new Display();
  }

  @Test
  public void testShowWhile() {
    Runnable runnable = mock( Runnable.class );

    BusyIndicator.showWhile( display, runnable );

    verify( runnable ).run();
  }

  @Test
  public void testShowWhile_withNullDisplay() {
    Runnable runnable = mock( Runnable.class );

    BusyIndicator.showWhile( null, runnable );

    verify( runnable ).run();
  }

  @Test( expected = IllegalArgumentException.class )
  public void testShowWhile_failsWithNullRunnable() {
    BusyIndicator.showWhile( null, null );
  }

}
