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
package org.eclipse.rap.rwt.jstest.internal;

import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.rap.rwt.application.EntryPointFactory;
import org.eclipse.rap.rwt.service.ServiceHandler;


public class RapTestApplicationConfigurator implements ApplicationConfiguration {

  public void configure( Application application ) {
    ServiceHandler serviceHandler = new ClientResourcesServiceHandler();
    application.addServiceHandler( ClientResourcesServiceHandler.ID, serviceHandler );
    EntryPointFactory factory = new EntryPointFactory() {
      public EntryPoint create() {
        return null;
      }
    };
    application.addEntryPoint( "/test", factory, null );
  }

}
