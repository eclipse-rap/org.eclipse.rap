/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import java.io.IOException;

import org.eclipse.rwt.internal.service.*;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.rwt.service.ISessionStore;

final class UICallBackActivator implements PhaseListener {

  private static final long serialVersionUID = 1L;

  private final String id;

  UICallBackActivator( ISessionStore session ) {
    this.id = session.getId();
  }

  public void beforePhase( final PhaseEvent event ) {
  }

  public void afterPhase( final PhaseEvent event ) {
    if( id.equals( ContextProvider.getSession().getId() ) ) {
      LifeCycleFactory.getLifeCycle().removePhaseListener( this );
      writeActivationJS();
    }
  }

  public PhaseId getPhaseId() {
    return PhaseId.RENDER;
  }

  void writeActivationJS() {
    UICallBackManager.getInstance().setActive( true );
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    HtmlResponseWriter writer = stateInfo.getResponseWriter();
    if( needsUICallBackActivation() ) {
      try {
        writer.write( UICallBackServiceHandler.JS_SEND_CALLBACK_REQUEST );
      } catch( IOException e ) {
        ServletLog.log( "", e );
      }
    }
  }

  private boolean needsUICallBackActivation() {
    UICallBackManager uiCallBackManager = UICallBackManager.getInstance();
    boolean isActive =  UICallBackServiceHandler.isUICallBackActive();
    boolean isCallBackBlocked = uiCallBackManager.isCallBackRequestBlocked();
    return isActive && !isCallBackBlocked;
  }
}