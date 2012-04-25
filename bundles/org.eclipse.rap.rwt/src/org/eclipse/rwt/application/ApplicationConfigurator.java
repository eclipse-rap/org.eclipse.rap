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

import org.eclipse.rwt.lifecycle.IEntryPoint;


/**
 * <p><strong>Note:</strong> This API is <em>provisional</em>. It is likely to change before the final
 * release.</p>
 *
 * An <code>ApplicationConfigurator</code> instance is used to provide an RWT
 * <code>{@link ApplicationConfiguration ApplicationConfiguration}</code> to the RWT runtime system.
 * The latter is represented by an <code>{@link ApplicationInstance ApplicationProcess}</code>.
 * Each <code>ApplicationProcess</code> takes exactly one configurator.
 *
 * <p>The simplest implementation of an <code>ApplicationConfigurator</code> looks like this:
 * <pre>
 *   public class ExampleApplicationConfigurator implements ApplicationConfigurator {
 *    public void configure( ApplicationConfiguration configuration ) {
 *      configuration.addEntryPoint( "example", ExampleEntryPoint.class );
 *    }
 *  }</pre>
 * The <code>{@link ApplicationConfigurator#configure(ApplicationConfiguration) configure}</code>
 * method serves as callback for the <code>Application</code> instance. The application uses this
 * method to retrieve the configuration at runtime. The example above shows how to register an
 * <code>{@link IEntryPoint IEntryPoint}</code> that will be used by the application at runtime
 * to provide application specific UIs.
 * </p>
 *
 * <p>In general RWT developers do not have to interact with the <code>Application</code> instance
 * directly. An <code>ApplicationConfigurator</code> implementation generally gets picked up
 * by the declaration system of the surrounding container. In case of a servlet container for
 * example the configurator is registered as <code>context-param</code> in the
 * <code>web.xml</code>, with <code>OSGi</code> you might register it as a service using DS and
 * in the RAP workbench you do not see it at all as configuration is supplied via
 * <code>extension-points</code>.</p>
 *
 * <p>Note that the configurator is called only once during application lifetime. Configuration
 * of the application takes place before the system gets activated. Therefore manipulation of
 * the configuration instance at a later point in time is not an intended use case and will
 * likely have no effect to the system.</p>
 *
 * @see ApplicationInstance
 * @see ApplicationConfiguration
 * @since 1.5
 */
public interface ApplicationConfigurator {

  // TODO [fappel]: think about where to locate this documentation, since this is servlet
  //                specific
  /**
   * Value for the <code>param-name</code> of an <code>context-param</code> declaration that
   * declares an <code>ApplicationConfigurator</code> in a <code>web.xml</code>. The value of
   * <code>param-value</code> in such an declaration has to be the fully qualified class name
   * of the configuration implementation in question.
   */
  public static final String CONFIGURATOR_PARAM = "org.eclipse.rwt.Configurator";
  // TODO [fappel]: think about where to locate this documentation, since this is servlet
  //                specific
  public static final String RESOURCE_ROOT_LOCATION = "resource_root_location";

  /**
   * Callback method that allows to configure the given application configuration. See the
   * <code>{@link ApplicationConfigurator}</code> class documentation for more details.
   *
   * @param configuration Object that allows to apply various configuration settings to
   *                      the RWT runtime system
   */
  void configure( ApplicationConfiguration configuration );
}
