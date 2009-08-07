/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.ui.internal;

import java.lang.reflect.Constructor;

import org.eclipse.swt.widgets.Display;


/**
 * An Adapter class that allows to mark the dependencies to the databinding
 * bundles as optional. 
 */
public class RealmAdapterHook {

  private static final String CLASS_REALM_ADAPTER
    = "org.eclipse.jface.internal.databinding.realmadapter.RealmAdapter";

  public static void runWithDefault( final Display display,
                                     final Runnable runnable ) {
    Runnable realmAdapter = null;
    try {
      Class realmAdapterClass = Class.forName( CLASS_REALM_ADAPTER );
      Class[] paramTypes = new Class[] { Display.class, Runnable.class };
      Constructor constructor = realmAdapterClass.getConstructor( paramTypes );
      Object[] params = new Object[] { display, runnable };
      realmAdapter = ( Runnable )constructor.newInstance( params );
    } catch( final ClassNotFoundException cnfe ) {
      // In case that the databinding bundle is not available we expect
      // this to happen. So we execute the runnable directly to spin the
      // UI event loop.
      realmAdapter = runnable;
    } catch( final Throwable shouldNotHappen ) {
      // Every thing else is unexpected, so there's probably some kind of
      // programming problem. Therefore I throw an RuntimeException, what
      // else should/can we do?
      String msg =   "An unexpected error occured while adapting the realm "
                   + "mechanism for databinding.";
      throw new RuntimeException( msg, shouldNotHappen );
    }
    realmAdapter.run();
  }
}
