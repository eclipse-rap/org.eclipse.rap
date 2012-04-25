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
package org.eclipse.rwt.application;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContext;

import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.internal.application.ApplicationContext;
import org.eclipse.rwt.internal.application.ApplicationContextUtil;
import org.eclipse.rwt.internal.resources.ResourceManagerImpl;
import org.eclipse.rwt.internal.util.ParamCheck;


/**
 * An instance of an RWT web application at runtime.
 * <p>
 * An <code>ApplicationInstance</code> shares the same scope and life cycle as
 * the <code>ServletContext</code>. In order to serve requests for an RWT
 * application, it must be created and started for the
 * <code>ServletContext</code> which receives the requests.
 * </p>
 * <p>
 * To create an <code>ApplicationInstance</code>, the <code>ServletContext</code>
 * it should be bound to must be given along with an
 * <code>ApplicationConfigurator</code> with which clients can configure the
 * application before it is started. Usually, the
 * <code>ApplicationInstance</code> is constructed and started in the
 * <code>contextInitialized()</code> method of a
 * <code>ServletContextListener</code> and stopped in its
 * <code>contextDestroyed()</code> method.
 * </p>
 * <p>
 * Alternatively, this task can be delegated to the
 * <code>RWTServletContextListener</code>. If this class is specified as a
 * listener in the deployment descriptor (web.xml), it starts an
 * ApplicationInstance when the servlet context is initialized and stops it when
 * the servlet context is destroyed. The <code>RWTServletContextListener</code>
 * looks for an <code>org.eclipse.rwt.Configurator</code> init-parameter. Its
 * value is assumed to be a class that implements
 * <code>ApplicationConfigurator</code> and is used to configure the
 * application.
 * </p>
 *
 * @since 1.5
 * @see ApplicationConfigurator
 * @see org.eclipse.rwt.engine.RWTServletContextListener
 * @see javax.servlet.ServletContext
 * @see javax.servlet.ServletContextListener
 * @noextend This class is not intended to be subclassed by clients.
 */
public class ApplicationInstance {

  public final static String RESOURCES = ResourceManagerImpl.RESOURCES;

  private final ApplicationContext applicationContext;

  /**
   * Constructs a new instance of this class given a configurator and the
   * servlet context it is bound to.
   *
   * @param configurator the the configurator to configure the application. Must
   *          not be <code>null</code>.
   * @param servletContext the servlet context this application is bound to.
   *          Must not be <code>null</code>.
   */
  public ApplicationInstance( ApplicationConfigurator configurator, ServletContext servletContext ) {
    ParamCheck.notNull( configurator, "configurator" );
    ParamCheck.notNull( servletContext, "servletContext" );

    applicationContext = new ApplicationContext( configurator, servletContext );
  }

  /**
   * Starts this application.
   *
   * @throws IllegalStateException if this application was already started.
   */
  public void start() {
    ApplicationContextUtil.set( applicationContext.getServletContext(), applicationContext );
    activateApplicationContext();
  }

  /**
   * Stops this application if it is running. Calling <code>stop()</code> on a non-running
   * application does nothing.
   */
  public void stop() {
    try {
      if( applicationContext.isActivated() ) {
        applicationContext.deactivate();
      }
    } finally {
      ApplicationContextUtil.remove( applicationContext.getServletContext() );
    }
  }

  /**
   * Returns the servlet paths for all entrypoints that are registered with this
   * application.
   *
   * @return an unmodifiable collection of the servlet paths, empty if no
   *         entrypoints have been registered
   */
  public Collection<String> getServletPaths() {
    Set<String> result = new HashSet<String>();
    Collection<String> servletPaths = applicationContext.getEntryPointManager().getServletPaths();
    result.addAll( servletPaths );
    AbstractBranding[] brandings = applicationContext.getBrandingManager().getAll();
    for( AbstractBranding branding : brandings ) {
      result.add( "/" + branding.getServletName() );
    }
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
