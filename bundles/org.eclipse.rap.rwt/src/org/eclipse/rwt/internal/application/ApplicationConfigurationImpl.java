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
package org.eclipse.rwt.internal.application;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;

import org.eclipse.rwt.AdapterFactory;
import org.eclipse.rwt.application.ApplicationConfiguration;
import org.eclipse.rwt.application.ApplicationConfigurator;
import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.internal.lifecycle.RWTLifeCycle;
import org.eclipse.rwt.internal.service.ServiceManager;
import org.eclipse.rwt.internal.theme.Theme;
import org.eclipse.rwt.internal.theme.ThemeManager;
import org.eclipse.rwt.internal.theme.css.CssFileReader;
import org.eclipse.rwt.internal.theme.css.StyleSheet;
import org.eclipse.rwt.internal.util.ParamCheck;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.rwt.lifecycle.IEntryPointFactory;
import org.eclipse.rwt.lifecycle.PhaseListener;
import org.eclipse.rwt.resources.IResource;
import org.eclipse.rwt.resources.ResourceLoader;
import org.eclipse.rwt.service.IServiceHandler;
import org.eclipse.rwt.service.ISettingStoreFactory;
import org.eclipse.swt.widgets.Widget;


@SuppressWarnings("deprecation")
public class ApplicationConfigurationImpl implements ApplicationConfiguration {

  private final ApplicationContext applicationContext;
  private final ApplicationConfigurator configurator;

  static class ResourceLoaderImpl implements ResourceLoader {

    private final ClassLoader loader;

    private ResourceLoaderImpl( ClassLoader loader ) {
      this.loader = loader;
    }

    public InputStream getResourceAsStream( String resourceName ) throws IOException {
      return loader.getResourceAsStream( resourceName );
    }
  }

  ApplicationConfigurationImpl( ApplicationContext applicationContext,
                                ApplicationConfigurator configurator )
  {
    this.applicationContext = applicationContext;
    this.configurator = configurator;
  }

  public void setOperationMode( OperationMode operationMode ) {
    ParamCheck.notNull( operationMode, "operationMode" );
    switch( operationMode ) {
      case JEE_COMPATIBILITY:
        break;
      case SWT_COMPATIBILITY:
        applicationContext.getLifeCycleFactory().configure( RWTLifeCycle.class );
        break;
      case SESSION_FAILOVER:
        new SessionFailoverConfigurator( applicationContext ).configure();
        break;
      default:
        throw new IllegalArgumentException( "Unsupported operation mode: " + operationMode );
    }
  }

  public void addPhaseListener( PhaseListener phaseListener ) {
    ParamCheck.notNull( phaseListener, "phaseListener" );

    applicationContext.getPhaseListenerRegistry().add( phaseListener );
  }

  public void setSettingStoreFactory( ISettingStoreFactory settingStoreFactory ) {
    ParamCheck.notNull( settingStoreFactory, "settingStoreFactory" );

    applicationContext.getSettingStoreManager().register( settingStoreFactory );
  }

  public void addEntryPoint( String servletPath, Class<? extends IEntryPoint> type ) {
    ParamCheck.notNull( servletPath, "servletPath" );
    ParamCheck.notNull( type, "type" );

    applicationContext.getEntryPointManager().registerByPath( servletPath, type, null );
  }

  public void addEntryPoint( String servletPath, IEntryPointFactory entryPointFactory ) {
    ParamCheck.notNull( servletPath, "servletPath" );
    ParamCheck.notNull( entryPointFactory, "entryPointFactory" );

    applicationContext.getEntryPointManager().registerByPath( servletPath, entryPointFactory, null );
  }

  /*
   * Only for backward compatibility with the extension point "org.eclipse.rap.ui.entrypoint"
   * attribute "parameter"
   */
  public void addEntryPointByParameter( String parameter, Class<? extends IEntryPoint> type ) {
    ParamCheck.notNull( parameter, "parameter" );
    ParamCheck.notNull( type, "type" );

    applicationContext.getEntryPointManager().registerByName( parameter, type );
  }

  /*
   * Only for backward compatibility with the extension point "org.eclipse.rap.ui.entrypoint"
   * attribute "parameter"
   */
  public void addEntryPointByParameter( String parameter, IEntryPointFactory entryPointFactory ) {
    ParamCheck.notNull( parameter, "parameter" );
    ParamCheck.notNull( entryPointFactory, "entryPointFactory" );

    applicationContext.getEntryPointManager().registerByName( parameter, entryPointFactory );
  }

  /*
   * Only for backward compatibility with the extension point "org.eclipse.rap.ui.adapterfactory"
   */
  public void addAdapterFactory( Class<?> adaptable, AdapterFactory adapterFactory ) {
    ParamCheck.notNull( adaptable, "adaptable" );
    ParamCheck.notNull( adapterFactory, "adapterFactory" );

    applicationContext.getAdapterManager().registerAdapters( adaptable, adapterFactory );
  }

  public void addResource( IResource resource ) {
    ParamCheck.notNull( resource, "resource" );

    applicationContext.getResourceRegistry().add( resource );
  }

  public void addServiceHandler( String serviceHandlerId, IServiceHandler serviceHandler ) {
    ParamCheck.notNull( serviceHandlerId, "serviceHandlerId" );
    ParamCheck.notNull( serviceHandler, "serviceHandler" );

    ServiceManager serviceManager = applicationContext.getServiceManager();
    serviceManager.registerServiceHandler( serviceHandlerId, serviceHandler );
  }

  public void addBranding( AbstractBranding branding ) {
    ParamCheck.notNull( branding, "branding" );

    applicationContext.getBrandingManager().register( branding );
  }

  public void addStyleSheet( String themeId, String styleSheetLocation ) {
    addStyleSheet( themeId, styleSheetLocation, new ResourceLoaderImpl( getClassLoader() ) );
  }

  public void addStyleSheet( String themeId, String styleSheetLocation, ResourceLoader resourceLoader ) {
    ParamCheck.notNull( themeId, "themeId" );
    ParamCheck.notNull( styleSheetLocation, "styleSheetLocation" );
    ParamCheck.notNull( resourceLoader, "resourceLoader" );

    StyleSheet styleSheet = readStyleSheet( styleSheetLocation, resourceLoader );
    ThemeManager themeManager = applicationContext.getThemeManager();
    Theme theme = themeManager.getTheme( themeId );
    if( theme != null ) {
      theme.addStyleSheet( styleSheet );
    } else {
      themeManager.registerTheme( new Theme( themeId, "unknown", styleSheet ) );
    }
  }

  public void addThemableWidget( Class<? extends Widget> widget ) {
    addThemableWidget( widget, new ResourceLoaderImpl( widget.getClassLoader() ) );
  }

  public void addThemableWidget( Class<? extends Widget> widget, ResourceLoader resourceLoader ) {
    ParamCheck.notNull( widget, "widget" );
    ParamCheck.notNull( resourceLoader, "resourceLoader" );

    applicationContext.getThemeManager().addThemeableWidget( widget, resourceLoader );
  }

  public void setAttribute( String name, Object value ) {
    applicationContext.getApplicationStore().setAttribute( name, value );
  }

  private ClassLoader getClassLoader() {
    return configurator.getClass().getClassLoader();
  }

  private StyleSheet readStyleSheet( String styleSheetLocation, ResourceLoader loader ) {
    StyleSheet result;
    try {
      result = CssFileReader.readStyleSheet( styleSheetLocation, loader );
    } catch( IOException ioe ) {
      String text = "Failed to read stylesheet from resource ''{0}''";
      Object[] args = new Object[] { styleSheetLocation };
      String msg = MessageFormat.format( text, args );
      throw new IllegalArgumentException( msg );
    }
    return result;
  }
}