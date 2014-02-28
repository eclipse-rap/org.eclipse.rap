/*******************************************************************************
 * Copyright (c) 2002, 2014 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.lifecycle;

import org.eclipse.rap.rwt.remote.RemoteObject;


/**
 * @since 2.0
 * @deprecated New applications and custom widgets should not rely on lifecycle phases anymore, as
 *             this concept is going to be replaced. Use the {@link RemoteObject} API instead.
 * @see org.eclipse.rap.rwt.remote.RemoteObject
 */
@Deprecated
public class ProcessActionRunner {

  public static void add( Runnable runnable ) {
    org.eclipse.rap.rwt.internal.lifecycle.ProcessActionRunner.add( runnable );
  }

  public static boolean executeNext() {
    return org.eclipse.rap.rwt.internal.lifecycle.ProcessActionRunner.executeNext();
  }

  public static void execute() {
    org.eclipse.rap.rwt.internal.lifecycle.ProcessActionRunner.execute();
  }

}
