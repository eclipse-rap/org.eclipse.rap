/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.engine;

import org.eclipse.rwt.internal.lifecycle.UICallBackServiceHandler;
import org.eclipse.rwt.internal.resources.JSLibraryServiceHandler;
import org.eclipse.rwt.internal.service.ServiceManager;


class ServiceManagerConfigurable implements Configurable {
  private static final String ID_JS_LIBRARIES = JSLibraryServiceHandler.HANDLER_ID;
  private static final String ID_UI_CALL_BACK = UICallBackServiceHandler.HANDLER_ID;

  public void configure( ApplicationContext context ) {
    ServiceManager serviceManager = context.getServiceManager();
    serviceManager.registerServiceHandler( ID_UI_CALL_BACK, new UICallBackServiceHandler() );
    serviceManager.registerServiceHandler( ID_JS_LIBRARIES, new JSLibraryServiceHandler() );
    // TODO [SystemStart]: move this to where the actual system initialization takes place
    context.getJSLibraryConcatenator().startJSConcatenation();
  }

  public void reset( ApplicationContext context ) {
    ServiceManager serviceManager = context.getServiceManager();
    serviceManager.unregisterServiceHandler( ID_UI_CALL_BACK );
    serviceManager.unregisterServiceHandler( ID_JS_LIBRARIES );
  }
}