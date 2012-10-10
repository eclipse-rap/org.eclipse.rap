/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.widgets;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.internal.application.RWTFactory;
import org.eclipse.rap.rwt.internal.lifecycle.LifeCycleUtil;
import org.eclipse.rap.rwt.internal.protocol.ProtocolMessageWriter;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.lifecycle.PhaseEvent;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.lifecycle.PhaseListener;
import org.eclipse.rap.rwt.service.IServiceStore;
import org.eclipse.swt.widgets.Display;


public final class JavaScriptExecutor {

  private static final String JS_EXECUTOR = JavaScriptExecutor.class.getName() + "#instance";
  private static final String JSEXECUTOR_TYPE = "rwt.client.JavaScriptExecutor";
  private static final String PARAM_CONTENT = "content";
  private static final String METHOD_EXECUTE = "execute";

  public static void execute( String code ) {
    JSExecutorPhaseListener jsExecutor = getJSExecutor();
    if( jsExecutor == null ) {
      jsExecutor = new JSExecutorPhaseListener();
      RWTFactory.getLifeCycleFactory().getLifeCycle().addPhaseListener( jsExecutor );
      setJSExecutor( jsExecutor );
    }
    jsExecutor.append( code );
  }

  private JavaScriptExecutor() {
    // prevent instantiation
  }

  private static JSExecutorPhaseListener getJSExecutor() {
    IServiceStore serviceStore = ContextProvider.getServiceStore();
    return ( JSExecutorPhaseListener )serviceStore.getAttribute( JS_EXECUTOR );
  }

  private static void setJSExecutor( JSExecutorPhaseListener jsExecutor ) {
    IServiceStore serviceStore = ContextProvider.getServiceStore();
    serviceStore.setAttribute( JS_EXECUTOR, jsExecutor );
  }

  private static class JSExecutorPhaseListener implements PhaseListener {
    private final StringBuilder code;
    private final Display display;

    JSExecutorPhaseListener() {
      display =  Display.getCurrent() ;
      code = new StringBuilder();
    }

    void append( String command ) {
      code.append( command );
    }

    public void beforePhase( PhaseEvent event ) {
      // do nothing
    }

    public void afterPhase( PhaseEvent event ) {
      if( display == LifeCycleUtil.getSessionDisplay() ) {
        ProtocolMessageWriter protocolWriter = ContextProvider.getProtocolWriter();
        try {
          Map<String, Object> properties = new HashMap<String, Object>();
          properties.put( PARAM_CONTENT, code.toString().trim() );
          protocolWriter.appendCall( JSEXECUTOR_TYPE, METHOD_EXECUTE, properties );
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
