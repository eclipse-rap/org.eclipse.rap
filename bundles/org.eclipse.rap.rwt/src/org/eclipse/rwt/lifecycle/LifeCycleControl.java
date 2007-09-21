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

import org.eclipse.rwt.internal.lifecycle.RWTLifeCycleBlockControl;


/**
 * TODO [fappel]: comment
 */
public class LifeCycleControl {
  
  /**
   * TODO [fappel]: comment
   */
  public final static class LifeCycleLock {
    public final Object data = RWTLifeCycleBlockControl.newLockData();
  }
  
  
  /**
   * TODO [fappel]: comment
   */
  public static void block( final LifeCycleLock lock ) {
    RWTLifeCycleBlockControl.block( lock );
  }
  
  /**
   * TODO [fappel]: comment
   */
  public static void resume( final LifeCycleLock lock ) {
    RWTLifeCycleBlockControl.resume( lock );
  }
}
