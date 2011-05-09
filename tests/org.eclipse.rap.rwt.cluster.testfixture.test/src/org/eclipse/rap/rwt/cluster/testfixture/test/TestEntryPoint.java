/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.cluster.testfixture.test;

import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.swt.widgets.Display;


public class TestEntryPoint implements IEntryPoint {

  private static boolean wasCreateUIInvoked;

  public static void reset() {
    synchronized( TestEntryPoint.class ) {
      wasCreateUIInvoked = false;
    }
  }
  
  public static boolean wasCreateUIInvoked() {
    synchronized( TestEntryPoint.class ) {
      return wasCreateUIInvoked;
    }
  }
  
  public int createUI() {
    synchronized( TestEntryPoint.class ) {
      wasCreateUIInvoked = true;
    }
    new Display();
    return 0;
  }
}
