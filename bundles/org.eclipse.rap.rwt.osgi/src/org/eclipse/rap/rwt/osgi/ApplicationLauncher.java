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
package org.eclipse.rap.rwt.osgi;

import org.eclipse.rwt.application.ApplicationConfiguration;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;


/**
 * A launcher for RWT applications in the OSGi environment. An instance of this interface will be
 * available at runtime as an OSGi service.
 * <p>
 * Instead of using the ApplicationLauncher directly, an {@link ApplicationConfiguration} can be
 * provided as a service. For every available application configuration, the RWT OSGi integration
 * bundle will automatically start an application at an available HTTPService.
 * </p>
 *
 * @since 1.5
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ApplicationLauncher {

  /**
   * The name of a service property for an {@link ApplicationConfiguration} service to define a
   * context name for the application. For example, when an ApplicationConfiguration is registered
   * as a service with a property <code>contextName</code> set to <code>example</code>, its
   * entrypoints will be available at <code>http://host/example/entrypointName</code>.
   * <p>
   * For declarative services, add this element to the component declaration:
   * </p>
   * <pre>
   * &lt;property name=&quot;contextName&quot; type=&quot;String&quot; value=&quot;example&quot;/&gt;
   * </pre>
   */
  public static final String PROPERTY_CONTEXT_NAME = "contextName";

  /**
   * Launches an application with the given configuration at the given HTTPService.
   *
   * @param configuration the configuration of the application to start
   * @param httpService the http service to start the application at
   * @param httpContext the http context to use, or <code>null</code> to use the default context
   * @param contextName the context name of the application, defines the first URL path segment to
   *          the application
   * @param contextDirectory the name of a directory to store static web resources
   * @return a reference to tha application started
   */
  ApplicationReference launch( ApplicationConfiguration configuration,
                               HttpService httpService,
                               HttpContext httpContext,
                               String contextName,
                               String contextDirectory );

}
