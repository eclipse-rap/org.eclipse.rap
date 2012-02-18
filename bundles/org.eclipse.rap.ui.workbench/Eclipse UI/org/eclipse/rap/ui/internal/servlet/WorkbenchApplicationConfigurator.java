/*******************************************************************************
 * Copyright (c) 2006, 2012 EclipseSource and others.
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.rap.ui.internal.application.EntryPointApplicationWrapper;
import org.eclipse.rap.ui.internal.branding.BrandingExtension;
import org.eclipse.rap.ui.internal.preferences.WorkbenchFileSettingStoreFactory;
import org.eclipse.rwt.AdapterFactory;
import org.eclipse.rwt.application.ApplicationConfiguration;
import org.eclipse.rwt.application.ApplicationConfiguration.OperationMode;
import org.eclipse.rwt.application.ApplicationConfigurator;
import org.eclipse.rwt.internal.application.ApplicationConfigurationImpl;
import org.eclipse.rwt.internal.util.ClassUtil;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.rwt.lifecycle.IEntryPointFactory;
import org.eclipse.rwt.lifecycle.PhaseListener;
import org.eclipse.rwt.resources.IResource;
import org.eclipse.rwt.resources.ResourceLoader;
import org.eclipse.rwt.service.IServiceHandler;
import org.eclipse.rwt.service.ISettingStoreFactory;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;


@SuppressWarnings( "deprecation" )
public final class WorkbenchApplicationConfigurator implements ApplicationConfigurator {

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

  private static final String RUN = "run"; //$NON-NLS-1$
  private static final String PI_RUNTIME = "org.eclipse.core.runtime"; //$NON-NLS-1$
  private static final String PT_APPLICATIONS = "applications"; //$NON-NLS-1$
  private static final String PT_APP_VISIBLE = "visible"; //$NON-NLS-1$

  private final ServiceReference httpServiceReference;

  WorkbenchApplicationConfigurator( ServiceReference httpServiceReference ) {
    this.httpServiceReference = httpServiceReference;
  }

  public void configure( ApplicationConfiguration configuration ) {
	configuration.setOperationMode( OperationMode.SWT_COMPATIBILITY );
    registerPhaseListener( configuration );
    registerSettingStoreFactory( configuration );
    registerWorkbenchEntryPoints( ( ApplicationConfigurationImpl )configuration );
    registerApplicationEntryPoints( ( ApplicationConfigurationImpl )configuration );
    registerThemeableWidgets( configuration );
    registerThemes( configuration );
    registerThemeContributions( configuration );
    registerAdapterFactories( ( ApplicationConfigurationImpl )configuration );
    registerResources( configuration );
    registerServiceHandlers( configuration );
    registerBrandings( configuration );
  }

  private void registerPhaseListener( ApplicationConfiguration configuration ) {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IExtensionPoint point = registry.getExtensionPoint( ID_PHASE_LISTENER );
    IConfigurationElement[] elements = point.getConfigurationElements();
    for( int i = 0; i < elements.length; i++ ) {
      try {
        Object instance = elements[ i ].createExecutableExtension( "class" );
        PhaseListener listener = ( PhaseListener )instance;
        configuration.addPhaseListener( listener );
      } catch( final CoreException ce ) {
        WorkbenchPlugin.getDefault().getLog().log( ce.getStatus() );
      }
    }
  }

  private void registerSettingStoreFactory( ApplicationConfiguration configuration ) {
    // determine which factory to use via an environment setting / config.ini
    String settingStoreFactoryParam = "org.eclipse.rwt.settingStoreFactory";
    String factoryId = getOSGiProperty( settingStoreFactoryParam );
    ISettingStoreFactory result = null;
    if( factoryId != null ) {
      result = loadSettingStoreFactory( factoryId );
    }
    if( result == null ) {
      result = new WorkbenchFileSettingStoreFactory(); // default
    }
    configuration.setSettingStoreFactory( result );
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

  private void registerAdapterFactories( ApplicationConfigurationImpl configuration ) {
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
        configuration.addAdapterFactory( adaptableClass, adapterFactory );
      } catch( Throwable thr ) {
        String text = "Could not register adapter factory ''{0}''  for the adapter type ''{1}''.";
        Object[] param = new Object[] { factoryName, adaptableName};
        logProblem( text, param, thr, contributorName );
      }
    }
  }

  private void registerWorkbenchEntryPoints( ApplicationConfigurationImpl configuration ) {
    for( IConfigurationElement element : getEntryPointExtensions() ) {
      String contributorName = element.getContributor().getName();
      String className = element.getAttribute( "class" );
      String parameter = element.getAttribute( "parameter" );
      String id = element.getAttribute( "id" );
      try {
        Bundle bundle = Platform.getBundle( contributorName );
        Class clazz = bundle.loadClass( className );
        configuration.addEntryPointByParameter( parameter, clazz );
        EntryPointParameters.register( id, parameter );
      } catch( final Throwable thr ) {
        String text = "Could not register entry point ''{0}'' with startup parameter ''{1}''.";
        Object[] param = new Object[] { className, parameter };
        logProblem( text, param, thr, contributorName );
      }
    }
  }

  private void registerApplicationEntryPoints( ApplicationConfigurationImpl configuration ) {
    for( IExtension extension : getApplicationExtensions() ) {
      IConfigurationElement configElement = extension.getConfigurationElements()[ 0 ];
      String contributorName = configElement.getContributor().getName();
      IConfigurationElement[] runElement = configElement.getChildren( RUN );
      String className = runElement[ 0 ].getAttribute( "class" ); //$NON-NLS-1$
      String applicationId = extension.getUniqueIdentifier();
      // [if] Use full qualified applicationParameter, see bug 321360
      String applicationParameter = extension.getUniqueIdentifier();
      String isVisible = configElement.getAttribute( PT_APP_VISIBLE );
      try {
        // ignore invisible applications
        if( isVisible == null || Boolean.valueOf( isVisible ).booleanValue() ) {
          Bundle bundle = Platform.getBundle( contributorName );
          Class clazz = bundle.loadClass( className );
          IEntryPointFactory factory = createApplicationEntryPointFactory( clazz );
          configuration.addEntryPointByParameter( applicationParameter, factory );
          EntryPointParameters.register( applicationId, applicationParameter );
        }
      } catch( ClassNotFoundException exception ) {
        String text =   "Could not register application ''{0}'' " //$NON-NLS-1$
                      + "with request startup parameter ''{1}''."; //$NON-NLS-1$
        Object[] params = new Object[]{ className, applicationParameter };
        logProblem( text, params, exception, contributorName );
      }
    }
  }

  private static IEntryPointFactory createApplicationEntryPointFactory( final Class applicationClass )
  {
    return new IEntryPointFactory() {

      public IEntryPoint create() {
        return new EntryPointApplicationWrapper( applicationClass );
      }
    };
  }

  private void registerThemeableWidgets( ApplicationConfiguration configuration ) {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IExtensionPoint ep = registry.getExtensionPoint( ID_THEMEABLE_WIDGETS );
    IConfigurationElement[] widgetExts = ep.getConfigurationElements();
    for( int i = 0; i < widgetExts.length; i++ ) {
      String contributorName = widgetExts[ i ].getContributor().getName();
      String widgetClass = widgetExts[ i ].getAttribute( "class" );
      try {
        final Bundle bundle = Platform.getBundle( contributorName );
        Class widget = bundle.loadClass( widgetClass );
        configuration.addThemableWidget( widget );
      } catch( final Throwable thr ) {
        String text = "Could not register themeable widget ''{0}''.";
        Object[] param = new Object[] { widgetClass };
        logProblem( text, param, thr, contributorName );
      }
    }
  }

  private void registerThemes( ApplicationConfiguration configuration ) {
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
          configuration.addStyleSheet( themeId, themeFile, resourceLoader );
        } catch( final Exception e ) {
          String text = "Could not register custom theme ''{0}'' from file ''{1}''.";
          Object[] param = new Object[]{ themeId, themeFile };
          logProblem( text, param, e, contributorName );
        }
      }
    }
  }

  private void registerThemeContributions( ApplicationConfiguration configuration ) {
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
          configuration.addStyleSheet( themeId, themeFile, loader );
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

  private void registerResources( ApplicationConfiguration configuration ) {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IExtensionPoint point = registry.getExtensionPoint( ID_RESOURCES );
    IConfigurationElement[] elements = point.getConfigurationElements();
    DependentResource[] resources = loadResources( elements );
    resources = sortResources( resources );
    registerResources( resources, configuration );
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

  private void registerResources( DependentResource[] resources,
                                  ApplicationConfiguration configuration )
  {
    for( int i = 0; i < resources.length; i++ ) {
      if( resources[ i ] != null ) {
        configuration.addResource( resources[ i ].resource );
      }
    }
  }

  private void registerServiceHandlers( ApplicationConfiguration configuration ) {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IExtensionPoint point = registry.getExtensionPoint( ID_SERVICE_HANDLER );
    IConfigurationElement[] elements = point.getConfigurationElements();
    for( int i = 0; i < elements.length; i++ ) {
      try {
        String id = elements[ i ].getAttribute( "id" );
        if( id != null ) {
          Object extObject = elements[ i ].createExecutableExtension( "class" );
          IServiceHandler handler = ( IServiceHandler )extObject;
          configuration.addServiceHandler( id, handler );
        }
      } catch( final CoreException ce ) {
        WorkbenchPlugin.getDefault().getLog().log( ce.getStatus() );
      }
    }
  }

  private void registerBrandings( ApplicationConfiguration configuration ) {
    try {
      new BrandingExtension( configuration, httpServiceReference ).read();
    } catch( final IOException ioe ) {
      throw new RuntimeException( "Unable to read branding extension", ioe );
    }
  }

  private static IConfigurationElement[] getEntryPointExtensions() {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IExtensionPoint extensionPoint = registry.getExtensionPoint( ID_ENTRY_POINT );
    IConfigurationElement[] elements = extensionPoint.getConfigurationElements();
    return elements;
  }

  private static IExtension[] getApplicationExtensions() {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    String extensionPointId = PI_RUNTIME + '.' + PT_APPLICATIONS;
    IExtensionPoint extensionPoint = registry.getExtensionPoint( extensionPointId );
    return extensionPoint.getExtensions();
  }

  private static void logProblem( String text,
                                  Object[] textParams,
                                  Throwable problem,
                                  String bundleId )
  {
    String msg = MessageFormat.format( text, textParams );
    Status status = new Status( IStatus.ERROR, bundleId, IStatus.OK, msg, problem );
    WorkbenchPlugin.getDefault().getLog().log( status );
  }

  private static final class DependentResource {
    public final IResource resource;
    public final String id;
    public final List dependencies;

    public DependentResource( IResource resource, String id, List dependencies ) {
      this.resource = resource;
      this.id = id;
      this.dependencies = dependencies;
    }

    @Override
    public String toString() {
      return id != null ? id : resource.getClass().getName();
    }
  }

}
