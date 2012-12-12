/*******************************************************************************
 * Copyright (c) 2011, 2012 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.application;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContext;

import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.application.ApplicationContextUtil;
import org.eclipse.rap.rwt.internal.resources.ResourceDirectory;
import org.eclipse.rap.rwt.internal.util.ParamCheck;


/**
 * An <code>ApplicationRunner</code> is used to start an RWT application with
 * the given <code>ApplicationConfiguration</code> in the given
 * <code>ServletContext</code>.
 * <p>
 * In most cases, application developers don't have to use this class directly.
 * Instead of this, the class <code>RWTServletContextListener</code> can be
 * registered as a listener in the deployment descriptor (web.xml). In this
 * case, the <code>ApplicationConfiguration</code> defined in the init-parameter
 * <code>org.eclipse.rap.applicationConfiguration</code> will be started by the
 * framework.
 * </p>
 * <p>
 * When a custom <code>ServletContextListener</code> is used, the
 * <code>ApplicationRunner</code> is usually constructed and started in the
 * <code>contextInitialized()</code> method and stopped in the
 * <code>contextDestroyed()</code> method.
 * </p>
 *
 * @since 2.0
 * @see ApplicationConfiguration
 * @see org.eclipse.rap.rwt.engine.RWTServletContextListener
 * @see javax.servlet.ServletContext
 * @see javax.servlet.ServletContextListener
 * @noextend This class is not intended to be subclassed by clients.
 */
public class ApplicationRunner {

  public final static String RESOURCES = ResourceDirectory.DIRNAME;

  private final ApplicationContextImpl applicationContext;

  /**
   * Constructs a new instance of this class given an application configuration and
   * the servlet context it is bound to.
   *
   * @param configuration the configuration for the application to start. Must not be
   *          <code>null</code>.
   * @param servletContext the servlet context this application is bound to.
   *          Must not be <code>null</code>.
   */
  public ApplicationRunner( ApplicationConfiguration configuration, ServletContext servletContext )
  {
    ParamCheck.notNull( configuration, "configuration" );
    ParamCheck.notNull( servletContext, "servletContext" );

    applicationContext = new ApplicationContextImpl( configuration, servletContext );
  }

  /**
   * Starts the application.
   *
   * @throws IllegalStateException if this application was already started.
   */
  public void start() {
    ApplicationContextUtil.set( applicationContext.getServletContext(), applicationContext );
    activateApplicationContext();
  }

  /**
   * Stops the application if it is running. Calling <code>stop()</code> on a non-running
   * application does nothing.
   */
  public void stop() {
    try {
      if( applicationContext.isActive() ) {
        applicationContext.deactivate();
      }
    } finally {
      ApplicationContextUtil.remove( applicationContext.getServletContext() );
    }
  }

  /**
   * @deprecated This method is not part of the RAP API. It will be removed in
   *             future versions.
   *             @noreference This method is not intended to be referenced by clients.
   */
  @Deprecated
  public Collection<String> getServletPaths() {
    Set<String> result = new HashSet<String>();
    Collection<String> servletPaths = applicationContext.getEntryPointManager().getServletPaths();
    result.addAll( servletPaths );
    return Collections.unmodifiableCollection( result );
  }

  private void activateApplicationContext() {
    try {
      applicationContext.activate();
    } catch( RuntimeException rte ) {
      ApplicationContextUtil.remove( applicationContext.getServletContext() );
      throw rte;
    }
  }
}
