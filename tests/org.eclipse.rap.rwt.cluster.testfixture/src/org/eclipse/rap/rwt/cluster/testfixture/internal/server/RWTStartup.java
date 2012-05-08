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
package org.eclipse.rap.rwt.cluster.testfixture.internal.server;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.eclipse.rap.rwt.cluster.testfixture.server.IServletEngine;
import org.eclipse.rwt.application.ApplicationRunner;
import org.eclipse.rwt.application.Application;
import org.eclipse.rwt.application.Application.OperationMode;
import org.eclipse.rwt.application.ApplicationConfiguration;
import org.eclipse.rwt.lifecycle.IEntryPoint;


public class RWTStartup {

  public static ServletContextListener createServletContextListener(
    Class<? extends IEntryPoint> entryPointClass )
  {
    return new TestApplicationController( entryPointClass );
  }

  private static class TestApplicationController implements ServletContextListener {
    private final Class<? extends IEntryPoint> entryPointClass;
    private ApplicationRunner applicationRunner;

    private TestApplicationController( Class<? extends IEntryPoint> entryPointClass ) {
      this.entryPointClass = entryPointClass;
    }

    public void contextInitialized( ServletContextEvent event ) {
      ApplicationConfiguration configurator = new TestApplicationConfigurator( entryPointClass );
      applicationRunner = new ApplicationRunner( configurator, event.getServletContext() );
      applicationRunner.start();
    }

    public void contextDestroyed( ServletContextEvent event ) {
      applicationRunner.stop();
    }
  }

  private static class TestApplicationConfigurator implements ApplicationConfiguration {
    private final Class<? extends IEntryPoint> entryPointClass;

    private TestApplicationConfigurator( Class<? extends IEntryPoint> entryPointClass ) {
      this.entryPointClass = entryPointClass;
    }

    public void configure( Application configuration ) {
      configuration.setOperationMode( OperationMode.SESSION_FAILOVER );
      configuration.addEntryPoint( IServletEngine.SERVLET_PATH, entryPointClass, null );
    }
  }
}
