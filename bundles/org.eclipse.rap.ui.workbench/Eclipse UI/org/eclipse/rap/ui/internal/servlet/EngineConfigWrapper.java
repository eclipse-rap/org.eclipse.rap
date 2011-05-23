/*******************************************************************************
 * Copyright (c) 2006, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rap.ui.internal.servlet;

import java.io.*;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;

import org.eclipse.core.runtime.*;
import org.eclipse.rap.ui.internal.application.ApplicationRegistry;
import org.eclipse.rap.ui.internal.branding.BrandingExtension;
import org.eclipse.rap.ui.internal.preferences.WorkbenchFileSettingStoreFactory;
import org.eclipse.rwt.AdapterFactory;
import org.eclipse.rwt.internal.ConfigurationReader;
import org.eclipse.rwt.internal.engine.*;
import org.eclipse.rwt.internal.lifecycle.UICallBackServiceHandler;
import org.eclipse.rwt.internal.resources.DefaultResourceManagerFactory;
import org.eclipse.rwt.internal.resources.JSLibraryServiceHandler;
import org.eclipse.rwt.internal.service.ServiceManager;
import org.eclipse.rwt.internal.theme.*;
import org.eclipse.rwt.internal.theme.css.CssFileReader;
import org.eclipse.rwt.internal.theme.css.StyleSheet;
import org.eclipse.rwt.internal.util.ClassUtil;
import org.eclipse.rwt.lifecycle.PhaseListener;
import org.eclipse.rwt.resources.IResource;
import org.eclipse.rwt.service.IServiceHandler;
import org.eclipse.rwt.service.ISettingStoreFactory;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;


/**
 * The underlying W4Toolkit runtime engine expects some configuration
 * infos read by the IEngineConfig implementation. We abuse the
 * <code>EngineConfigWrapper</code> to fake an appropriate environment
 * for the library.
 */
// TODO: [fappel] clean replacement mechanism that is anchored in W4Toolkit core
public final class EngineConfigWrapper implements IEngineConfig {
  
  private static final class DependentResource {
    public final IResource resource;
    public final String id;
    public final List dependencies;
    
    public DependentResource( IResource resource, String id, List dependencies ) {
      this.resource = resource;
      this.id = id;
      this.dependencies = dependencies;
    }
    
    public String toString() {
      return id != null ? id : resource.getClass().getName();
    }
  }

  //  extension point id for adapter factory registration
  private static final String ID_ADAPTER_FACTORY
    = "org.eclipse.rap.ui.adapterfactory";
  //  extension point id for entry point registration
  private static final String ID_ENTRY_POINT
    = "org.eclipse.rap.ui.entrypoint";
  //  extension point id for custom theme registration
  private static final String ID_THEMES
    = "org.eclipse.rap.ui.themes";
  private static final String ELEMENT_THEME = "theme";
  private static final String ELEMENT_THEME_CONTRIBUTION = "themeContribution";
  //  extension point id for custom themeable widget registration
  private static final String ID_THEMEABLE_WIDGETS
    = "org.eclipse.rap.ui.themeableWidgets";
  //  extension point id for phase listener registration
  private static final String ID_PHASE_LISTENER
    = "org.eclipse.rap.ui.phaselistener";
  //  extension point id for service handler registration
  private static final String ID_SERVICE_HANDLER
    = "org.eclipse.rap.ui.serviceHandler";
  //  extension point id for registration of resources (i.e. javascript)
  //  which needed to be loaded at page startup
  private static final String ID_RESOURCES
    = "org.eclipse.rap.ui.resources";
  // extension point id for registration of a setting store factories
  private static final String ID_SETTING_STORES
    = "org.eclipse.rap.ui.settingstores";

  private final EngineConfig engineConfig;
  private final ApplicationContext applicationContext;

  public EngineConfigWrapper( ApplicationContext applicationContext ) {
    this.applicationContext = applicationContext;
    this.engineConfig = new EngineConfig( findContextPath().toString() );
    init();
  }

  public File getServerContextDir() {
    return engineConfig.getServerContextDir();
  }

  public File getClassDir() {
    return engineConfig.getClassDir();
  }

  public File getLibDir() {
    return engineConfig.getLibDir();
  }

  //////////////////
  // helping methods

  private void init() {
    applicationContext.getConfigurationReader().setEngineConfig( this );
    configureLifeCycleFactory();
    registerPhaseListener();
    registerResourceManagerFactory();
    registerSettingStoreFactory();
    registerWorkbenchEntryPoint();
    registerThemeableWidgets();
    registerThemes();
    registerThemeContributions();
    registerFactories();
    registerResources();
    registerUICallBackServiceHandler();
    registerJSLibraryServiceHandler();
    registerCustomServiceHandlers();
    registerApplicationEntryPoints();
    registerBrandings();
  }

  private void configureLifeCycleFactory() {
    ConfigurationReader configurationReader = applicationContext.getConfigurationReader();
    applicationContext.getLifeCycleFactory().setConfigurationReader( configurationReader );
  }

  private void registerPhaseListener() {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IExtensionPoint point = registry.getExtensionPoint( ID_PHASE_LISTENER );
    IConfigurationElement[] elements = point.getConfigurationElements();
    for( int i = 0; i < elements.length; i++ ) {
      try {
        PhaseListener listener
          = ( PhaseListener )elements[ i ].createExecutableExtension( "class" );
        applicationContext.getPhaseListenerRegistry().add( listener );
      } catch( final CoreException ce ) {
        WorkbenchPlugin.getDefault().getLog().log( ce.getStatus() );
      }
    }
  }

  private void registerResourceManagerFactory() {
    DefaultResourceManagerFactory factory = new DefaultResourceManagerFactory();
    applicationContext.getResourceManagerProvider().registerFactory( factory );
  }

  private void registerSettingStoreFactory() {
    // determine which factory to use via an environment setting / config.ini
    ISettingStoreFactory result = null;
    String factoryId = getOSGiProperty( RWTServletContextListener.SETTING_STORE_FACTORY_PARAM );
    if( factoryId != null ) {
      IExtensionRegistry registry = Platform.getExtensionRegistry();
      IExtensionPoint point = registry.getExtensionPoint( ID_SETTING_STORES );
      IConfigurationElement[] elements = point.getConfigurationElements();
      for( int i = 0; i < elements.length; i++ ) {
        String id = elements[ i ].getAttribute( "id" );
        if( factoryId.equals( id ) ) {
          try {
            Object obj = elements[ i ].createExecutableExtension( "class" );
            if( obj instanceof ISettingStoreFactory ) {
              result = ( ISettingStoreFactory )obj;
            }
          } catch( CoreException cex ) {
            WorkbenchPlugin.log( cex.getStatus() );
          }
        }
      }
      if( result == null ) {
        String msg =   "Warning: could not find the factory with id '"
                     + factoryId
                     + "' in org.eclipse.rap.ui.settingstores";
        WorkbenchPlugin.log( WorkbenchPlugin.getStatus( new Throwable( msg ) ) );
      }
    }
    if( result == null ) {
      result = new WorkbenchFileSettingStoreFactory(); // default
    }
    applicationContext.getSettingStoreManager().register( result );
  }

  private static String getOSGiProperty( String name ) {
	Bundle systemBundle = Platform.getBundle( Constants.SYSTEM_BUNDLE_SYMBOLICNAME );
	return systemBundle.getBundleContext().getProperty( name );
  }

  private void registerFactories() {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IExtensionPoint point = registry.getExtensionPoint( ID_ADAPTER_FACTORY );
    IConfigurationElement[] elements = point.getConfigurationElements();
    for( int i = 0; i < elements.length; i++ ) {
      String contributorName = elements[ i ].getContributor().getName();
      String factoryName = elements[ i ].getAttribute( "factoryClass" );
      String adaptableName = elements[ i ].getAttribute( "adaptableClass" );
      try {
        Bundle bundle = Platform.getBundle( contributorName );
        Class factoryClass = bundle.loadClass( factoryName );
        Class adaptableClass = bundle.loadClass( adaptableName );
        AdapterFactory adapterFactory = ( AdapterFactory )ClassUtil.newInstance( factoryClass ) ;
        applicationContext.getAdapterManager().registerAdapters( adaptableClass, adapterFactory );
      } catch( Throwable thr ) {
        String text =   "Could not register adapter factory ''{0}'' "
                      + "for the adapter type ''{1}''.";
        Object[] param = new Object[] { factoryName, adaptableName};
        String msg = MessageFormat.format( text, param );
        Status status = new Status( IStatus.ERROR,
                                    contributorName,
                                    IStatus.OK,
                                    msg,
                                    thr );
        WorkbenchPlugin.getDefault().getLog().log( status );
      }
    }
  }

  private void registerWorkbenchEntryPoint() {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IExtensionPoint point = registry.getExtensionPoint( ID_ENTRY_POINT );
    IConfigurationElement[] elements = point.getConfigurationElements();
    for( int i = 0; i < elements.length; i++ ) {
      String contributorName = elements[ i ].getContributor().getName();
      String className = elements[ i ].getAttribute( "class" );
      String parameter = elements[ i ].getAttribute( "parameter" );
      String id = elements[ i ].getAttribute( "id" );
      try {
        Bundle bundle = Platform.getBundle( contributorName );
        Class clazz = bundle.loadClass( className );
        applicationContext.getEntryPointManager().register( parameter, clazz );
        EntryPointExtension.bind( id, parameter );
      } catch( final Throwable thr ) {
        String text =   "Could not register entry point ''{0}'' "
                      + "with request startup parameter ''{1}''.";
        Object[] param = new Object[] { className, parameter };
        String msg = MessageFormat.format( text, param );
        IStatus status = new Status( IStatus.ERROR,
                                     contributorName,
                                     IStatus.OK,
                                     msg,
                                     thr );
        WorkbenchPlugin.getDefault().getLog().log( status );
      }
    }
  }

  private void registerThemeableWidgets() {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IExtensionPoint ep = registry.getExtensionPoint( ID_THEMEABLE_WIDGETS );
    IConfigurationElement[] widgetExts = ep.getConfigurationElements();
    for( int i = 0; i < widgetExts.length; i++ ) {
      String contributorName = widgetExts[ i ].getContributor().getName();
      String widgetClass = widgetExts[ i ].getAttribute( "class" );
      try {
        final Bundle bundle = Platform.getBundle( contributorName );
        ResourceLoader resLoader = new ResourceLoader() {
          public InputStream getResourceAsStream( final String resourceName )
            throws IOException
          {
            InputStream result = null;
            // We need to call getResource() here since resources must be loaded
            // by the bundle classloader
            URL url = bundle.getResource( resourceName );
            if( url != null ) {
              result = url.openStream();
            }
            return result;
          }
        };
        Class widget = bundle.loadClass( widgetClass );
        getThemeManager().addThemeableWidget( widget, resLoader );
      } catch( final Throwable thr ) {
        String text = "Could not register themeable widget ''{0}''.";
        Object[] param = new Object[] { widgetClass };
        String message = MessageFormat.format( text, param );
        IStatus status
          = new Status( IStatus.ERROR, contributorName, message, thr );
        WorkbenchPlugin.getDefault().getLog().log( status );
      }
    }
    getThemeManager().initializeThemeableWidgets();
  }

  private ThemeManager getThemeManager() {
    return applicationContext.getThemeManager().getInstance();
  }

  private void registerThemes() {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IExtensionPoint ep = registry.getExtensionPoint( ID_THEMES );
    IConfigurationElement[] elements = ep.getConfigurationElements();
    for( int i = 0; i < elements.length; i++ ) {
      if( ELEMENT_THEME.equals( elements[ i ].getName() ) ) {
        String contributorName = elements[ i ].getContributor().getName();
        String themeId = elements[ i ].getAttribute( "id" );
        String themeName = elements[ i ].getAttribute( "name" );
        String themeFile = elements[ i ].getAttribute( "file" );
        if( !ThemeManager.DEFAULT_THEME_ID.equals( themeId ) ) {
          try {
            Bundle bundle = Platform.getBundle( contributorName );
            ResourceLoader loader = createResourceLoader( bundle );
            StyleSheet styleSheet = null;
            if( themeFile != null ) {
              styleSheet = CssFileReader.readStyleSheet( themeFile, loader );
            }
            Theme theme = new Theme( themeId, themeName, styleSheet );
            getThemeManager().registerTheme( theme );
          } catch( final Exception e ) {
            String text = "Could not register custom theme ''{0}'' "
                          + "from file ''{1}''.";
            Object[] param = new Object[]{ themeId, themeFile };
            String msg = MessageFormat.format( text, param );
            IStatus status = new Status( IStatus.ERROR, contributorName, msg, e );
            WorkbenchPlugin.getDefault().getLog().log( status );
          }
        }
      }
    }
  }

  private void registerThemeContributions() {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IExtensionPoint ep = registry.getExtensionPoint( ID_THEMES );
    IConfigurationElement[] elements = ep.getConfigurationElements();
    for( int i = 0; i < elements.length; i++ ) {
      if( ELEMENT_THEME_CONTRIBUTION.equals( elements[ i ].getName() ) ) {
        String contributorName = elements[ i ].getContributor().getName();
        String themeId = elements[ i ].getAttribute( "themeId" );
        String themeFile = elements[ i ].getAttribute( "file" );
        try {
          Bundle bundle = Platform.getBundle( contributorName );
          ResourceLoader loader = createResourceLoader( bundle );
          StyleSheet styleSheet = CssFileReader.readStyleSheet( themeFile,
                                                                loader );
          Theme theme = getThemeManager().getTheme( themeId );
          if( theme == null ) {
            throw new IllegalArgumentException( "No such theme defined: "
                                                + themeId );
          }
          theme.addStyleSheet( styleSheet );
        } catch( final Exception e ) {
          String text = "Could not register theme contribution for theme ''{0}'' "
                        + "from file ''{1}''.";
          Object[] param = new Object[]{ themeId, themeFile };
          String msg = MessageFormat.format( text, param );
          IStatus status = new Status( IStatus.ERROR, contributorName, msg, e );
          WorkbenchPlugin.getDefault().getLog().log( status );
        }
      }
    }
    getThemeManager().initialize();
  }

  private static ResourceLoader createResourceLoader( final Bundle bundle ) {
    ResourceLoader result = new ResourceLoader() {

      public InputStream getResourceAsStream( final String resourceName )
        throws IOException
      {
        InputStream result = null;
        IPath path = new Path( resourceName );
        URL url = FileLocator.find( bundle, path, null );
        if( url != null ) {
          result = url.openStream();
        }
        return result;
      }
    };
    return result;
  }

  // determine a faked context directory
  private static IPath findContextPath() {
    Bundle bundle = Platform.getBundle( PlatformUI.PLUGIN_ID );
    IPath stateLocation = Platform.getStateLocation( bundle );
    return stateLocation.append( "context" );
  }

  private void registerResources() {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IExtensionPoint point = registry.getExtensionPoint( ID_RESOURCES );
    IConfigurationElement[] elements = point.getConfigurationElements();
    DependentResource[] resources = loadResources( elements );
    resources = sortResources( resources );
    registerResources( resources );
  }

  private static DependentResource[] loadResources( IConfigurationElement[] elements ) {
    DependentResource[] result = new DependentResource[ elements.length ];
    for( int i = 0; i < elements.length; i++ ) {
      try {
        IResource resource = ( IResource )elements[ i ].createExecutableExtension( "class" );
        String resourceId = elements[ i ].getAttribute( "id" );
        IConfigurationElement[] dependsOn = elements[ i ].getChildren( "dependsOn" );
        List resourceDependencies = new ArrayList();
        for( int j = 0 ; j < dependsOn.length ; j++ ) {
          String dependency = dependsOn[ j ].getAttribute( "resourceId" );
          resourceDependencies.add( dependency );
        }
        result[ i ] = new DependentResource( resource, resourceId, resourceDependencies );
      } catch( CoreException ce ) {
        WorkbenchPlugin.getDefault().getLog().log( ce.getStatus() );
      }
    }
    return result;
  }

  private static DependentResource[] sortResources( DependentResource[] resources ) {
    DependentResource[] result = new DependentResource[ resources.length ];
    List sortedResourceIds = new ArrayList();
    List deferredResources = new ArrayList();
    int index = 0;
    for( int i = 0; i < resources.length; i++ ) {
      DependentResource resource = resources[ i ];
      if( resource != null ) {
        resource.dependencies.removeAll( sortedResourceIds );
        boolean checkDeferredResources = false;
        if( resource.dependencies.isEmpty() ) {
          result[ index++ ] = resource;
          sortedResourceIds.add( resource.id );
          checkDeferredResources = true;
        } else {
          deferredResources.add( resource );
        }
        while( checkDeferredResources ) {
          checkDeferredResources = false;
          for( Iterator iterator = deferredResources.iterator(); iterator.hasNext(); ) {
            DependentResource deferredResource = ( DependentResource )iterator.next();
            deferredResource.dependencies.removeAll( sortedResourceIds );
            if( deferredResource.dependencies.isEmpty() ) {
              result[ index++ ] = deferredResource;
              sortedResourceIds.add( deferredResource.id );
              iterator.remove();
              checkDeferredResources = true;
            }
          }
        }
      }
    }
    if( deferredResources.size() != 0 ) {
      String pluginId = WorkbenchPlugin.getDefault().getBundle().getSymbolicName();
      String message = "Dependencies could not be resolved for " + deferredResources;
      WorkbenchPlugin.getDefault().getLog().log( new Status( IStatus.ERROR, pluginId, message ) );
    }
    return result;
  }

  private void registerResources( DependentResource[] resources ) {
    for( int i = 0; i < resources.length; i++ ) {
      if( resources[ i ] != null ) {
        applicationContext.getResourceRegistry().add( resources[ i ].resource );
      }
    }
  }

  private void registerUICallBackServiceHandler() {
    ServiceManager serviceManager = applicationContext.getServiceManager();
    UICallBackServiceHandler handler = new UICallBackServiceHandler();
    serviceManager.registerServiceHandler( UICallBackServiceHandler.HANDLER_ID, handler );
  }

  private void registerJSLibraryServiceHandler() {
    ServiceManager serviceManager = applicationContext.getServiceManager();
    JSLibraryServiceHandler handler = new JSLibraryServiceHandler();
    serviceManager.registerServiceHandler( JSLibraryServiceHandler.HANDLER_ID, handler );
    // TODO [SystemStart]: move this to where the actual system initialization takes place
    applicationContext.getJSLibraryConcatenator().startJSConcatenation();
  }

  private void registerCustomServiceHandlers() {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IExtensionPoint point = registry.getExtensionPoint( ID_SERVICE_HANDLER );
    IConfigurationElement[] elements = point.getConfigurationElements();
    for( int i = 0; i < elements.length; i++ ) {
      try {
        String id = elements[ i ].getAttribute( "id" );
        if( id != null ) {
          Object extObject = elements[ i ].createExecutableExtension( "class" );
          IServiceHandler handler = ( IServiceHandler )extObject;
          applicationContext.getServiceManager().registerServiceHandler( id, handler );
        }
      } catch( final CoreException ce ) {
        WorkbenchPlugin.getDefault().getLog().log( ce.getStatus() );
      }
    }
  }

  private void registerBrandings() {
    try {
      new BrandingExtension( applicationContext ).read();
    } catch( final IOException ioe ) {
      throw new RuntimeException( "Unable to read branding extension", ioe );
    }
  }

  private void registerApplicationEntryPoints() {
    ApplicationRegistry.registerApplicationEntryPoints( applicationContext );
  }
}
