/*******************************************************************************
 * Copyright (c) 2010, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import java.util.TimerTask;

import org.eclipse.swt.internal.SerializableCompatibility;


class TimerExecTask extends TimerTask implements SerializableCompatibility {

  private final TimerExecScheduler scheduler;
  private final Runnable runnable;

  TimerExecTask( TimerExecScheduler scheduler, Runnable runnable ) {
    this.scheduler = scheduler;
    this.runnable = runnable;
    scheduler.serverPushManager.activateServerPushFor( this );
  }

  @Override
  public void run() {
    synchronized( scheduler.display.getDeviceLock() ) {
      scheduler.removeTask( this );
      if( !scheduler.display.isDisposed() ) {
        scheduler.display.asyncExec( runnable );
      }
    }
    scheduler.serverPushManager.deactivateServerPushFor( this );
  }

  @Override
  public boolean cancel() {
    scheduler.serverPushManager.deactivateServerPushFor( this );
    return super.cancel();
  }

  Runnable getRunnable() {
    return runnable;
  }

}
