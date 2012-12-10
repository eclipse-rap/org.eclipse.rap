/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.displaykit;

import org.eclipse.rap.rwt.internal.protocol.ProtocolMessageWriter;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.uicallback.UICallBackManager;
import org.eclipse.rap.rwt.service.UISession;


public class UICallBackRenderer {

  public static final String UI_CALLBACK_ID = "rwt.client.UICallBack";
  private static final String PROP_ACTIVE = "active";
  private static final String ATTR_PRESERVED_ACTIVATION
    = UICallBackRenderer.class.getName() + ".preservedActivation";

  private final UISession uiSession;
  private final UICallBackManager callbackManager;

  UICallBackRenderer() {
    uiSession = ContextProvider.getUISession();
    callbackManager = UICallBackManager.getInstance();
  }

  void render() {
    boolean activation = callbackManager.needsActivation();
    if( mustRender( activation ) ) {
      // Note [rst] UICallback activation can be changed at any time by a background thread.
      //            Therefore we need to preserve the same value that is rendered to the client.
      renderActivation( activation );
      preserveActivation( activation );
    }
  }

  private boolean mustRender( boolean activation ) {
    boolean result = hasChanged( activation );
    // do not render deactivation if there are pending runnables
    if( result && !activation && callbackManager.hasRunnables() ) {
      result = false;
    }
    return result;
  }

  private boolean hasChanged( boolean activation ) {
    return activation != getPreservedActivation();
  }

  private void preserveActivation( boolean activation ) {
    uiSession.setAttribute( ATTR_PRESERVED_ACTIVATION, Boolean.valueOf( activation ) );
  }

  private boolean getPreservedActivation() {
    Boolean preserved = ( Boolean )uiSession.getAttribute( ATTR_PRESERVED_ACTIVATION );
    return preserved != null ? preserved.booleanValue() : false;
  }

  private static void renderActivation( boolean activation ) {
    ProtocolMessageWriter writer = ContextProvider.getProtocolWriter();
    writer.appendSet( UI_CALLBACK_ID, PROP_ACTIVE, activation );
  }

}
