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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;

import org.eclipse.core.runtime.*;
import org.eclipse.rap.ui.internal.application.ApplicationRegistry;
import org.eclipse.rap.ui.internal.branding.BrandingExtension;
import org.eclipse.rap.ui.internal.preferences.WorkbenchFileSettingStoreFactory;
import org.eclipse.rwt.AdapterFactory;
import org.eclipse.rwt.engine.*;
import org.eclipse.rwt.internal.engine.configurables.SettingStoreManagerConfigurable;
import org.eclipse.rwt.internal.util.ClassUtil;
import org.eclipse.rwt.lifecycle.PhaseListener;
import org.eclipse.rwt.resources.IResource;
import org.eclipse.rwt.service.IServiceHandler;
import org.eclipse.rwt.service.ISettingStoreFactory;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.osgi.framework.*;


public final class RWTConfigurator implements Configurator {
  private static final String ID_ADAPTER_FACTORY = "org.eclipse.rap.ui.adapterfactory";
  private static final String ID_ENTRY_POINT = "org.eclipse.rap.ui.entrypoint";
  private static final String ID_THEMES = "org.eclipse.rap.ui.themes";
  private static final String ELEMENT_THEME = "theme";
  private static final String ELEMENT_THEME_CONTRIBUTION = "themeContribution";
  private static final String ID_THEMEABLE_WIDGETS = "org.eclipse.rap.ui.themeableWidgets";
  private static final String ID_PHASE_LISTENER = "org.eclipse.rap.ui.phaselistener";
  private static final String ID_SERVICE_HANDLER = "org.eclipse.rap.ui.serviceHandler";
  private static final String ID_RESOURCES = "org.eclipse.rap.ui.resources";
  private static final String ID_SETTING_STORES = "org.eclipse.rap.ui.settingstores";
  
  private final ServiceReference httpServiceReference;

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

  RWTConfigurator( ServiceReference httpServiceReference ) {
    this.httpServiceReference = httpServiceReference;
  }

  public void configure( Context context ) {
    registerPhaseListener( context );
    registerSettingStoreFactory( context );
    registerWorkbenchEntryPoint( context );
    registerThemeableWidgets( context );
    registerThemes( context );
    registerThemeContributions( context );
    registerFactories( context );
    registerResources( context );
    registerServiceHandlers( context );
    registerApplicationEntryPoints( context );
    registerBrandings( context );
  }

  private void registerPhaseListener( Context context ) {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IExtensionPoint point = registry.getExtensionPoint( ID_PHASE_LISTENER );
    IConfigurationElement[] elements = point.getConfigurationElements();
    for( int i = 0; i < elements.length; i++ ) {
      try {
        Object instance = elements[ i ].createExecutableExtension( "class" );
        PhaseListener listener = ( PhaseListener )instance;
        context.addPhaseListener( listener );
      } catch( final CoreException ce ) {
        WorkbenchPlugin.getDefault().getLog().log( ce.getStatus() );
      }
    }
  }

  private void registerSettingStoreFactory( Context context ) {
    // determine which factory to use via an environment setting / config.ini
    String settingStoreFactoryParam = SettingStoreManagerConfigurable.SETTING_STORE_FACTORY_PARAM;
    String factoryId = getOSGiProperty( settingStoreFactoryParam );
    ISettingStoreFactory result = null;
    if( factoryId != null ) {
      result = loadSettingStoreFactory( factoryId );
    }
    if( result == null ) {
      result = new WorkbenchFileSettingStoreFactory(); // default
    }
    context.setSettingStoreFactory( result );
  }

  private ISettingStoreFactory loadSettingStoreFactory( String factoryId ) {
    ISettingStoreFactory result = null;
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IExtensionPoint point = registry.getExtensionPoint( ID_SETTING_STORES );
    IConfigurationElement[] elements = point.getConfigurationElements();
    for( int i = 0; i < elements.length; i++ ) {
      String id = elements[ i ].getAttribute( "id" );
      if( factoryId.equals( id ) ) {
        result = loadSettingStoreFactory( elements[ i ] );
      }
    }
    if( result == null ) {
      String msg = "Unable to find setting store factory with id '" + factoryId + "'.";
      WorkbenchPlugin.log( msg );
    }
    return result;
  }

  private ISettingStoreFactory loadSettingStoreFactory( IConfigurationElement element ) {
    ISettingStoreFactory result = null;
    try {
      result = ( ISettingStoreFactory )element.createExecutableExtension( "class" );
    } catch( CoreException cex ) {
      WorkbenchPlugin.log( cex.getStatus() );
    }
    return result;
  }

  private static String getOSGiProperty( String name ) {
	Bundle systemBundle = Platform.getBundle( Constants.SYSTEM_BUNDLE_SYMBOLICNAME );
	return systemBundle.getBundleContext().getProperty( name );
  }

  private void registerFactories( Context context ) {
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
        context.addAddapterFactory( adaptableClass, adapterFactory );
      } catch( Throwable thr ) {
        String text = "Could not register adapter factory ''{0}''  for the adapter type ''{1}''.";
        Object[] param = new Object[] { factoryName, adaptableName};
        logProblem( text, param, thr, contributorName );
      }
    }
  }


  private void registerWorkbenchEntryPoint( Context context ) {
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
        context.addEntryPoint( parameter, clazz );
        EntryPointExtension.bind( id, parameter );
      } catch( final Throwable thr ) {
        String text = "Could not register entry point ''{0}'' with startup parameter ''{1}''.";
        Object[] param = new Object[] { className, parameter };
        logProblem( text, param, thr, contributorName );
      }
    }
  }

  private void registerThemeableWidgets( Context context ) {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IExtensionPoint ep = registry.getExtensionPoint( ID_THEMEABLE_WIDGETS );
    IConfigurationElement[] widgetExts = ep.getConfigurationElements();
    for( int i = 0; i < widgetExts.length; i++ ) {
      String contributorName = widgetExts[ i ].getContributor().getName();
      String widgetClass = widgetExts[ i ].getAttribute( "class" );
      try {
        final Bundle bundle = Platform.getBundle( contributorName );
        ResourceLoader resLoader = createThemableWidgetsResourceLoader( bundle );
        Class widget = bundle.loadClass( widgetClass );
        context.addThemableWidget( widget, resLoader );
      } catch( final Throwable thr ) {
        String text = "Could not register themeable widget ''{0}''.";
        Object[] param = new Object[] { widgetClass };
        logProblem( text, param, thr, contributorName );
      }
    }
  }


  private ResourceLoader createThemableWidgetsResourceLoader( final Bundle bundle ) {
    return new ResourceLoader() {
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
  }

  private void registerThemes( Context context ) {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IExtensionPoint ep = registry.getExtensionPoint( ID_THEMES );
    IConfigurationElement[] elements = ep.getConfigurationElements();
    for( int i = 0; i < elements.length; i++ ) {
      if( ELEMENT_THEME.equals( elements[ i ].getName() ) ) {
        String contributorName = elements[ i ].getContributor().getName();
        String themeId = elements[ i ].getAttribute( "id" );
        String themeFile = elements[ i ].getAttribute( "file" );
        try {
          Bundle bundle = Platform.getBundle( contributorName );
          ResourceLoader resourceLoader = createThemeResourceLoader( bundle );
          context.addTheme( themeId, themeFile, resourceLoader );
        } catch( final Exception e ) {
          String text = "Could not register custom theme ''{0}'' from file ''{1}''.";
          Object[] param = new Object[]{ themeId, themeFile };
          logProblem( text, param, e, contributorName );
        }
      }
    }
  }

  private void registerThemeContributions( Context context ) {
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
          ResourceLoader loader = createThemeResourceLoader( bundle );
          context.addThemeContribution( themeId, themeFile, loader );
        } catch( final Exception e ) {
          String text = "Could not register contribution for theme ''{0}'' from file ''{1}''.";
          Object[] param = new Object[]{ themeId, themeFile };
          logProblem( text, param, e, contributorName );
        }
      }
    }
  }

  private static ResourceLoader createThemeResourceLoader( final Bundle bundle ) {
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

  private void registerResources( Context context ) {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IExtensionPoint point = registry.getExtensionPoint( ID_RESOURCES );
    IConfigurationElement[] elements = point.getConfigurationElements();
    DependentResource[] resources = loadResources( elements );
    resources = sortResources( resources );
    registerResources( resources, context );
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

  private void registerResources( DependentResource[] resources, Context context ) {
    for( int i = 0; i < resources.length; i++ ) {
      if( resources[ i ] != null ) {
        context.addResource( resources[ i ].resource );
      }
    }
  }

  private void registerServiceHandlers( Context context ) {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IExtensionPoint point = registry.getExtensionPoint( ID_SERVICE_HANDLER );
    IConfigurationElement[] elements = point.getConfigurationElements();
    for( int i = 0; i < elements.length; i++ ) {
      try {
        String id = elements[ i ].getAttribute( "id" );
        if( id != null ) {
          Object extObject = elements[ i ].createExecutableExtension( "class" );
          IServiceHandler handler = ( IServiceHandler )extObject;
          context.addServiceHandler( id, handler );
        }
      } catch( final CoreException ce ) {
        WorkbenchPlugin.getDefault().getLog().log( ce.getStatus() );
      }
    }
  }

  private void registerBrandings( Context context ) {
    try {
      new BrandingExtension( context, httpServiceReference ).read();
    } catch( final IOException ioe ) {
      throw new RuntimeException( "Unable to read branding extension", ioe );
    }
  }

  private void registerApplicationEntryPoints( Context context ) {
    ApplicationRegistry.registerApplicationEntryPoints( context );
  }
  
  private void logProblem( String text, Object[] textParams, Throwable problem, String bundleId ) {
    String msg = MessageFormat.format( text, textParams );
    Status status = new Status( IStatus.ERROR, bundleId, IStatus.OK, msg, problem );
    WorkbenchPlugin.getDefault().getLog().log( status );
  }
}