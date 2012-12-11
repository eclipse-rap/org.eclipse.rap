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
package org.eclipse.rap.rwt.internal.client;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.JavaScriptExecutor;
import org.eclipse.rap.rwt.internal.application.RWTFactory;
import org.eclipse.rap.rwt.internal.lifecycle.LifeCycleUtil;
import org.eclipse.rap.rwt.internal.protocol.ProtocolMessageWriter;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.lifecycle.PhaseEvent;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.lifecycle.PhaseListener;
import org.eclipse.rap.rwt.service.UISessionEvent;
import org.eclipse.rap.rwt.service.UISessionListener;
import org.eclipse.swt.widgets.Display;


public final class JavaScriptExecutorImpl implements
  JavaScriptExecutor, PhaseListener, UISessionListener
{

  private static final String JSEXECUTOR_TYPE = "rwt.client.JavaScriptExecutor";
  private static final String PARAM_CONTENT = "content";
  private static final String METHOD_EXECUTE = "execute";

  private final Display display;
  private final StringBuilder codeBuilder;

  public JavaScriptExecutorImpl() {
    display = Display.getCurrent();
    codeBuilder = new StringBuilder();
    RWTFactory.getLifeCycleFactory().getLifeCycle().addPhaseListener( this );
    RWT.getUISession().addUISessionListener( this );
  }

  public void execute( String code ) {
    codeBuilder.append( code );
  }

  ///////////////////////
  // PhaseListener

  public void beforePhase( PhaseEvent event ) {
    // do nothing
  }

  public void afterPhase( PhaseEvent event ) {
    String content = codeBuilder.toString().trim();
    if( display == LifeCycleUtil.getSessionDisplay() && content.length() > 0 ) {
      ProtocolMessageWriter protocolWriter = ContextProvider.getProtocolWriter();
      Map<String, Object> properties = new HashMap<String, Object>();
      properties.put( PARAM_CONTENT, content );
      protocolWriter.appendCall( JSEXECUTOR_TYPE, METHOD_EXECUTE, properties );
      codeBuilder.setLength( 0 );
    }
  }

  public PhaseId getPhaseId() {
    return PhaseId.RENDER;
  }

  ////////////////////
  // UISessionListener

  public void beforeDestroy( UISessionEvent event ) {
    RWTFactory.getLifeCycleFactory().getLifeCycle().removePhaseListener( this );
  }

}
