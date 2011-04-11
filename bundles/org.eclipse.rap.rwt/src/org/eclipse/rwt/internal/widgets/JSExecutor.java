/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.widgets;

import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.IServiceStateInfo;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.widgets.Display;


public final class JSExecutor {

  private static final String JS_EXECUTOR = JSExecutor.class.getName() + "#instance";

  public static void executeJS( String code ) {
    JSExecutorPhaseListener jsExecutor = getJSExecutor();
    if( jsExecutor == null ) {
      jsExecutor = new JSExecutorPhaseListener();
      RWTFactory.getLifeCycleFactory().getLifeCycle().addPhaseListener( jsExecutor );
      setJSExecutor( jsExecutor );
    }
    jsExecutor.append( code );
  }

  private JSExecutor() {
    // prevent instantiation
  }

  private static JSExecutorPhaseListener getJSExecutor() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    return ( JSExecutorPhaseListener )stateInfo.getAttribute( JS_EXECUTOR );
  }

  private static void setJSExecutor( JSExecutorPhaseListener jsExecutor ) {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    stateInfo.setAttribute( JS_EXECUTOR, jsExecutor );
  }

  private static class JSExecutorPhaseListener implements PhaseListener {
    private static final long serialVersionUID = 1L;

    private final StringBuffer code;
    private final Display display;

    JSExecutorPhaseListener() {
      this.display =  Display.getCurrent() ;
      this.code = new StringBuffer();
    }

    void append( String command ) {
      code.append( command );
    }

    public void beforePhase( PhaseEvent event ) {
      // do nothing
    }

    public void afterPhase( PhaseEvent event ) {
      if( display == RWTLifeCycle.getSessionDisplay() ) {
        IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
        JavaScriptResponseWriter writer = stateInfo.getResponseWriter();
        try {
          writer.write( code.toString() );
        } finally {
          RWTFactory.getLifeCycleFactory().getLifeCycle().removePhaseListener( this );
        }
      }
    }

    public PhaseId getPhaseId() {
      return PhaseId.RENDER;
    }
  }
}
