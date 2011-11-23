/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.jstest.internal;

import org.eclipse.rwt.application.ApplicationConfiguration;
import org.eclipse.rwt.application.ApplicationConfigurator;
import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.service.IServiceHandler;


public class RapTestApplicationConfigurator implements ApplicationConfigurator {

  public void configure( ApplicationConfiguration configuration ) {
    IServiceHandler serviceHandler = new ClientResourcesServiceHandler();
    configuration.addServiceHandler( "includeClientResources", serviceHandler );
    configuration.addBranding( new AbstractBranding() {
      @Override
      public String getServletName() {
        return "test";
      }
    } );
  }

}
