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

import java.util.Map;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.client.WebClient;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.rwt.lifecycle.IEntryPointFactory;
import org.eclipse.rwt.lifecycle.PhaseListener;
import org.eclipse.rwt.resources.IResource;
import org.eclipse.rwt.resources.ResourceLoader;
import org.eclipse.rwt.service.IServiceHandler;
import org.eclipse.rwt.service.ISettingStoreFactory;
import org.eclipse.rwt.widgets.DialogUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;


/**
 * This interface allows to configure various aspects of an
 * <code>Application</code> before it is started.
 * <p>
 * <strong>Note:</strong> This API is <em>provisional</em>. It is likely to
 * change before the final release.
 * </p>
 *
 * @see Application
 * @see ApplicationConfigurator
 * @noimplement This interface is not intended to be implemented by clients.
 * @since 1.5
 */
public interface ApplicationConfiguration {

  /**
   * Instances of this class represent a mode of operation for a RAP
   * application. The major difference between the operation modes is whether a
   * separate UI thread is started for every session (SWT_COMPATIBILITY) or not
   * (JEE_COMPATIBILITY).
   */
  public static enum OperationMode {
    /**
     * In this mode, the request thread will be marked as UI thread in SWT.
     * Information that is attached to the request thread, such as security or
     * transaction contexts, can be directly accessed. This mode is compatible
     * with the JEE specification.
     * <p>
     * As its only limitation, it does not support the SWT main loop (more
     * specifically, the method {@link Display#sleep()} is not implemented). As
     * a consequence, blocking dialogs aren't possible with this operation mode.
     * Instead of blocking dialogs, the class {@link DialogUtil} allows to
     * attach a callback to react on the closing of a dialog.
     * </p>
     * <p>
     * Unless there is a need for blocking dialogs (e.g. when using the Eclipse
     * workbench), this mode is recommended as it is more lightweight than
     * <code>SWT_COMPATIBILITY</code> .
     * </p>
     */
    JEE_COMPATIBILITY,
    /**
     * In this mode, a separate UI thread will be started for each user session.
     * All UI requests are processed in this thread while the request thread is
     * put on hold. After processing all events, the method
     * {@link Display#sleep()} lets the request thread continue and puts the UI
     * thread to sleep. This approach fully supports the SWT main loop and thus
     * also allows for blocking dialogs.
     * <p>
     * Information that is attached to the request thread, such as security or
     * transaction contexts, can only be accessed using the method
     * {@link RWT#requestThreadExec(Runnable)}.
     * </p>
     */
    SWT_COMPATIBILITY,
    /**
     * This mode behaves just like <code>JEE_COMAPTIBILTIY</code> but in
     * addition it registers the required servlet filter to support clustering.
     * This mode requires the servlet API 3.0.
     */
    SESSION_FAILOVER
  }

  /**
   * The operation mode in which the application will be running. The default is
   * <code>JEE_COMPATIBILITY</code>.
   *
   * @param operationMode the operation mode to be used, must not be
   *          <code>null</code>
   * @see OperationMode
   */
  void setOperationMode( OperationMode operationMode );

  /**
   * Registers an entry point at the given servlet path. A servlet path must
   * begin with slash ('/') and must not end with a slash ('/'). The root path
   * (&quot;/&quot;) is currently not supported, as well as nested paths (e.g.
   * &quot;/path/subpath&quot;).
   *
   * @param path a valid path to register the entry point at
   * @param entryPointType the entry point class to be registered, must not be
   *          <code>null</code>
   */
  void addEntryPoint( String path, Class<? extends IEntryPoint> entryPointType );

  /**
   * Registers an entry point at the given servlet path. A servlet path must
   * begin with slash ('/') and must not end with a slash ('/'). The root path
   * (&quot;/&quot;) is currently not supported, as well as nested paths (e.g.
   * &quot;/path/subpath&quot;). Properties can be specified to control
   * client-specific aspects of the entrypoint such as theme, icons, etc. The
   * acceptable keys and values depend on the client implementation. The class
   * {@link WebClient} provides constants for the default RAP client.
   *
   * @param path a valid path to register the entry point at
   * @param entryPointType the entry point class to be registered, must not be
   *          <code>null</code>
   * @param properties properties that control client-specific aspects of the
   *          application, such as theme, icons, etc., may be <code>null</code>
   */
  void addEntryPoint( String path,
                      Class<? extends IEntryPoint> entryPointType,
                      Map<String, String> properties );

  /**
   * Registers an entry point factory at the given servlet path. A servlet path
   * must begin with slash ('/') and must not end with slash ('/'). The root
   * path (&quot;/&quot;) is currently not supported, as well as nested paths
   * (e.g. &quot;/path/subpath&quot;).
   *
   * @param path a valid path to register the entry point at
   * @param entryPointFactory the entry point factory to be registered, must not
   *          be <code>null</code>
   */
  void addEntryPoint( String path, IEntryPointFactory entryPointFactory );

  /**
   * Registers an entry point factory at the given servlet path. A servlet path
   * must begin with slash ('/') and must not end with slash ('/'). The root
   * path (&quot;/&quot;) is currently not supported, as well as nested paths
   * (e.g. &quot;/path/subpath&quot;). Properties can be specified to control
   * client-specific aspects of the entrypoint such as theme, icons, etc. The
   * acceptable keys and values depend on the client implementation. The class
   * {@link WebClient} provides constants for the default RAP client.
   *
   * @param path a valid path to register the entry point at
   * @param entryPointFactory the entry point factory to be registered, must not
   *          be <code>null</code>
   * @param properties properties that control client-specific aspects of the
   *          application, such as theme, icons, etc., may be <code>null</code>
   */
  void addEntryPoint( String path,
                      IEntryPointFactory entryPointFactory,
                      Map<String, String> properties );

  /**
   * Adds a stylesheet that contains a theme or a theme contribution to the
   * application. If a theme with the given theme id exists already, then the
   * stylesheet is handled as a contribution to this theme, otherwise it is
   * registered as a new theme with the given id. The stylesheet file will be
   * loaded with the classloader of the configurator.
   *
   * @param themeId the id of the theme to register or to contribute to
   * @param styleSheetLocation the location of the CSS file in the format
   *          accepted by {@link ClassLoader#getResource(String)}
   * @see RWT#DEFAULT_THEME_ID
   */
  void addStyleSheet( String themeId, String styleSheetLocation );

  /**
   * Adds a stylesheet that contains a theme or a theme contribution to the
   * application. If a theme with the given theme id exists already, then the
   * stylesheet is handled as a contribution to this theme, otherwise it is
   * registered as a new theme with the given id. The stylesheet file will be
   * loaded using the given resource loader.
   *
   * @param themeId the id of the theme to register or to contribute to
   * @param styleSheetLocation the location of the CSS file in the format
   *          accepted by the given resource loader
   * @param resourceLoader the resource loader that is able to load the style
   *          sheet from the given location
   * @see RWT#DEFAULT_THEME_ID
   */
  void addStyleSheet( String themeId, String styleSheetLocation, ResourceLoader resourceLoader );

  void addPhaseListener( PhaseListener phaseListener );

  void setAttribute( String name, Object value );

  void setSettingStoreFactory( ISettingStoreFactory settingStoreFactory );

  void addThemableWidget( Class<? extends Widget> widget );

  void addServiceHandler( String serviceHandlerId, IServiceHandler serviceHandler );

  /////////////////////////////////////////////
  // TODO [fappel]: replace with proper mechanism (Javascript)
  void addResource( IResource resource );
}
