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
package org.eclipse.rap.rwt.cluster.testfixture.internal.server;

import org.eclipse.rwt.engine.Configurator;
import org.eclipse.rwt.engine.Context;
import org.eclipse.rwt.internal.lifecycle.EntryPointManager;
import org.eclipse.rwt.lifecycle.IEntryPoint;

@SuppressWarnings("restriction")
public class SimpleLifeCycleConfigurator implements Configurator {
  
  public static void setEntryPointClass( Class<? extends IEntryPoint> entryPointClass ) {
    SimpleLifeCycleConfigurator.entryPointClass = entryPointClass;
  }

  public static Class<? extends IEntryPoint> getEntryPointClass() {
    return entryPointClass;
  }

  private static Class<? extends IEntryPoint> entryPointClass;

  public void configure( Context context ) {
    context.setLifeCycleMode( Context.LifeCycleMode.THREADLESS );
    context.addEntryPoint( EntryPointManager.DEFAULT, entryPointClass );
  }
}