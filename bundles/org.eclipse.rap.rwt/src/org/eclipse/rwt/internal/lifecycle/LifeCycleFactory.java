/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing implementation
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import org.eclipse.rwt.internal.engine.RWTContext;
import org.eclipse.rwt.lifecycle.ILifeCycle;



/** <p>Supplies a factory method for lifecycle managers for various 
  * <code>LifeCycle</code> implementations.</p>
  */
public final class LifeCycleFactory {
  
  public static ILifeCycle getLifeCycle() {
    return getInstance().getLifeCycle();
  }
  
  public static void destroy() {
    getInstance().destroy(); 
  }
  
  private static LifeCycleFactoryInstance getInstance() {
    Class singletonType = LifeCycleFactoryInstance.class;
    Object singleton = RWTContext.getSingleton( singletonType );
    return ( LifeCycleFactoryInstance )singleton;
  }
  
  private LifeCycleFactory() {
    // prevent instantiation
  }
}