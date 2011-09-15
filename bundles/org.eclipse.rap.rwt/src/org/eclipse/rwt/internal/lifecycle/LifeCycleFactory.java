/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import org.eclipse.rwt.internal.util.ClassUtil;
import org.eclipse.rwt.lifecycle.ILifeCycle;


public class LifeCycleFactory {
  private static final Class<RWTLifeCycle> DEFAULT_LIFE_CYCLE_CLASS = RWTLifeCycle.class;
  
  private Class<? extends LifeCycle> lifeCycleClass;
  private LifeCycle lifeCycle;
  
  public LifeCycleFactory() {
    lifeCycleClass = DEFAULT_LIFE_CYCLE_CLASS;
  }
  
  public ILifeCycle getLifeCycle() {
    return lifeCycle;
  }
  
  public void configure( Class<? extends LifeCycle> lifeCycleClass ) {
    this.lifeCycleClass = lifeCycleClass;
  }

  public void activate() {
    lifeCycle = ( LifeCycle )ClassUtil.newInstance( lifeCycleClass );
  }

  public void deactivate() {
    lifeCycleClass = DEFAULT_LIFE_CYCLE_CLASS;
    lifeCycle = null;
  }
}