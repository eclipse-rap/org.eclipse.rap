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
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContext;

import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.internal.application.ApplicationContext;
import org.eclipse.rwt.internal.application.ApplicationContextUtil;
import org.eclipse.rwt.internal.resources.ResourceManagerImpl;
import org.eclipse.rwt.internal.util.ParamCheck;


/**
 * This class represents an instance of an RWT web application at runtime.
 *
 * <p>
 * An <code>Application</code> shares the same scope and life cycle as the
 * <code>ServletContext</code>.
 * In order to serve requests for an RWT application, an <code>Application</code>
 * must be created and started for the <code>ServletContext</code> which receives
 * the requests.
 * </p>
 * <p>
 * To create an <code>Application</code>, the <code>ServletContext</code> it should be bound
 * to must be given along with an <code>ApplicationConfigurator</code> with which clients can
 * configure the application before it is started.
 * Usually, the <code>Application</code> is constructed and started in the
 * <code>contextInitialized()</code> method of a <code>ServletContextListener</code> and stopped
 * in its <code>contextDestroyed()</code> method.
 * </p>
 * <p>
 * Alternatively this task can be delegated to the <code>RWTServletContextListener</code>.
 * If this class is specified as a listener in the deployment descriptor (web.xml), it starts
 * an Application when the servlet context is initialized and stops it when the servlet
 * context is destroyed.
 * The <code>RWTServletContextListener</code> looks for an <code>org.eclipse.rwt.Configurator</code>
 * init-parameter.
 * Its value is assumed to be a class that implements <code>ApplicationConfigurator</code>
 * and is used to configure the application.
 * </p>
 *
 * <p><strong>Note:</strong> This API is <em>provisional</em>. It is likely to change before the
 * final release.</p>
 *
 * @since 1.5
 * @see ApplicationConfigurator
 * @see org.eclipse.rwt.engine.RWTServletContextListener
 * @see javax.servlet.ServletContext
 * @see javax.servlet.ServletContextListener
 * @noextend This class is not intended to be subclassed by clients.
 */
public class Application {

  public final static String RESOURCES = ResourceManagerImpl.RESOURCES;

  private final ApplicationContext applicationContext;

  /**
   * Constructs a new instance of this class given a configurator and the servlet context it
   * is bound to.
   * @param configurator the the configurator to configure the application. Must not be
   *   <code>null</code>.
   * @param servletContext the servlet context this application is bound to. Must not be
   *   <code>null</code>.
   */
  public Application( ApplicationConfigurator configurator, ServletContext servletContext ) {
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
   * Returns an array of all servlet names that are provided by this application.
   * <p>
   * Note: This is not the actual structure used by the receiver
   * to maintain its list of servlet names, so modifying the array will
   * not affect the receiver.
   * </p>
   *
   * @return all servlet names provided by this application. Returns an empty array if
   *   no servlet names are provided.
   */
  public String[] getServletNames() {
    Set<String> names = new HashSet<String>();
    Collection<String> servletPaths = applicationContext.getEntryPointManager().getServletPaths();
    for( String path : servletPaths ) {
      names.add( path );
    }
    AbstractBranding[] brandings = applicationContext.getBrandingManager().getAll();
    for( AbstractBranding branding : brandings ) {
      names.add( branding.getServletName() );
    }
    return names.toArray( new String[ names.size() ] );
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