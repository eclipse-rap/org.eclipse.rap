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
package org.eclipse.rap.rwt.cluster.test.entrypoints;

import java.io.Serializable;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.swt.widgets.Display;


public class TimerExecEntryPoint implements EntryPoint {
  public static final int TIMER_DELAY = 2000;

  private static final String ATTRIBUTE_NAME = "wasInvoked";
  private static final Boolean ATTRIBUTE_VALUE = Boolean.TRUE;

  public static boolean wasRunnableExecuted( UISession uiSession ) {
    return ATTRIBUTE_VALUE.equals( uiSession.getAttribute( ATTRIBUTE_NAME ) ); 
  }

  public int createUI() {
    Display display = new Display();
    display.timerExec( TIMER_DELAY, new TimerExecRunnable() );
    return 0;
  }

  private static class TimerExecRunnable implements Runnable, Serializable {

    public void run() {
      RWT.getUISession().setAttribute( ATTRIBUTE_NAME, ATTRIBUTE_VALUE );
    }
  }
}
