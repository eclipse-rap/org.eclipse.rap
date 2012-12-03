/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.cluster.testfixture.test;

import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.swt.widgets.Display;


public class TestEntryPoint implements EntryPoint {

  private static volatile boolean wasCreateUIInvoked;
  private static volatile Runnable runnable;

  public static void reset() {
    synchronized( TestEntryPoint.class ) {
      wasCreateUIInvoked = false;
    }
    runnable = null;
  }
  
  public static void setRunnable( Runnable runnable ) {
    TestEntryPoint.runnable = runnable;
  }
  
  public static boolean wasCreateUIInvoked() {
    synchronized( TestEntryPoint.class ) {
      return wasCreateUIInvoked;
    }
  }
  
  @SuppressWarnings("unused")
  public int createUI() {
    synchronized( TestEntryPoint.class ) {
      wasCreateUIInvoked = true;
    }
    new Display();
    if( runnable != null ) {
      runnable.run();
    }
    return 0;
  }
}
