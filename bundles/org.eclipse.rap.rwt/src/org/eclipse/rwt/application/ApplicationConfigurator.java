/*******************************************************************************
 * Copyright (c) 2011, 2012 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 *    EclipseSource - ongoing developement
 ******************************************************************************/
package org.eclipse.rwt.application;


/**
 * An <code>ApplicationConfigurator</code> is used to provide the configuration
 * of an RWT application to the RWT runtime system. The configuration describes
 * the entrypoints, URLs, themes, etc. that constitute an application.
 * <p>
 * The <code> configure</code> method will be called by the framework in order
 * to configure an application instance before it is started. An implementation
 * must at least register an entrypoint that provides the user interface for the
 * application. A simple implementation of this interface looks like this:
 * </p>
 * <pre>
 * public class ExampleApplicationConfigurator implements ApplicationConfigurator {
 *   public void configure( ApplicationConfiguration configuration ) {
 *     configuration.addEntryPoint( &quot;/example&quot;, ExampleEntryPoint.class, null );
 *   }
 * }
 * </pre>
 * <p>
 * The <code>configure</code> method is called only once during the lifetime of
 * an application. The configuration of the application takes place before the
 * system is activated. Therefore, manipulation of the configuration instance at
 * a later point in time is unsupported.
 * </p>
 * <p>
 * There can be more than one application instance at runtime, running on
 * different network ports or in different contexts. In most cases, developers
 * do not have to create an application instance explicitly. The
 * <code>ApplicationConfigurator</code> implementation can be registered with
 * the the surrounding container instead. For example, in a servlet container,
 * the application can be registered as <code>context-param</code> in the
 * <code>web.xml</code> (see <code>CONFIGURATOR_PARAM</code>), in
 * <code>OSGi</code> it can be registered as a service for the service interface
 * <code>ApplicationConfiguration</code>, and when using the workbench with RAP,
 * the application is registered with an extension-point.
 * </p>
 * <p>
 * Apart from this, an <code>{@link ApplicationRunner ApplicationRunner}</code> can be used
 * to run an application with this configuration.
 * </p>
 *
 * @see ApplicationRunner
 * @see ApplicationConfiguration
 * @since 1.5
 */
public interface ApplicationConfigurator {

  /**
   * This constant contains the parameter name to register an
   * ApplicationConfigurator in a servlet container environment when running RAP
   * without OSGi. To do so, the fully class qualified name of the Application
   * implementation has to be registered as a <code>context-param</code> in the
   * <code>web.xml</code>. Example:
   * <pre>
   * &lt;context-param&gt;
   *   &lt;param-name&gt;org.eclipse.rwt.Configurator&lt;/param-name&gt;
   *   &lt;param-value&gt;com.example.ExampleConfigurator&lt;/param-value&gt;
   * &lt;/context-param&gt;
   * </pre>
   */
  public static final String CONFIGURATOR_PARAM = "org.eclipse.rwt.Configurator";
  // TODO [fappel]: think about where to locate this documentation, since this is servlet
  //                specific
  public static final String RESOURCE_ROOT_LOCATION = "resource_root_location";

  /**
   * Callback method that allows to configure the given application configuration. See the
   * class documentation for more details.
   *
   * @param configuration Object that allows to apply various configuration settings to
   *                      the RWT runtime system
   */
  void configure( ApplicationConfiguration configuration );
}
