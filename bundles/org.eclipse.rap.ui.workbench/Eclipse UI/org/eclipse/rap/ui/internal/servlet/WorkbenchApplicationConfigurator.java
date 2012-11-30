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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import org.eclipse.equinox.app.IApplication;
import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.Application.OperationMode;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.client.WebClient;
import org.eclipse.rap.rwt.internal.application.ApplicationContext;
import org.eclipse.rap.rwt.internal.application.ApplicationImpl;
import org.eclipse.rap.rwt.lifecycle.IEntryPoint;
import org.eclipse.rap.rwt.lifecycle.IEntryPointFactory;
import org.eclipse.rap.rwt.lifecycle.PhaseListener;
import org.eclipse.rap.rwt.resources.ResourceLoader;
import org.eclipse.rap.rwt.service.ServiceHandler;
import org.eclipse.rap.rwt.service.ISettingStoreFactory;
import org.eclipse.rap.ui.internal.application.EntryPointApplicationWrapper;
import org.eclipse.rap.ui.internal.branding.AbstractBranding;
import org.eclipse.rap.ui.internal.branding.BrandingExtension;
import org.eclipse.rap.ui.internal.branding.BrandingManager;
import org.eclipse.rap.ui.internal.branding.BrandingUtil;
import org.eclipse.rap.ui.internal.preferences.WorkbenchFileSettingStoreFactory;
import org.eclipse.rap.ui.resources.IResource;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;


public final class WorkbenchApplicationConfigurator implements ApplicationConfiguration {

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

  private final ServiceReference<HttpService> httpServiceReference;

  /*
   * Note [rst]: public as per request in https://bugs.eclipse.org/bugs/show_bug.cgi?id=372183
   */
  public WorkbenchApplicationConfigurator( ServiceReference<HttpService> httpServiceReference ) {
    this.httpServiceReference = httpServiceReference;
  }

  public void configure( Application application ) {
	  application.setOperationMode( OperationMode.SWT_COMPATIBILITY );
    registerPhaseListener( application );
    registerSettingStoreFactory( application );
    registerThemeableWidgets( application );
    registerThemes( application );
    registerThemeContributions( application );
    registerResources( application );
    registerServiceHandlers( application );
    registerBrandings( application ); // [rh] brandings must be red before apps/entry points
    registerWorkbenchEntryPoints( ( ApplicationImpl )application );
    registerApplicationEntryPoints( ( ApplicationImpl )application );
  }

  private void registerPhaseListener( Application application ) {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IExtensionPoint point = registry.getExtensionPoint( ID_PHASE_LISTENER );
    IConfigurationElement[] elements = point.getConfigurationElements();
    for( int i = 0; i < elements.length; i++ ) {
      try {
        Object instance = elements[ i ].createExecutableExtension( "class" );
        PhaseListener listener = ( PhaseListener )instance;
        application.addPhaseListener( listener );
      } catch( final CoreException ce ) {
        WorkbenchPlugin.getDefault().getLog().log( ce.getStatus() );
      }
    }
  }

  private void registerSettingStoreFactory( Application application ) {
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
    application.setSettingStoreFactory( result );
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

  @SuppressWarnings( "unchecked" )
  private void registerWorkbenchEntryPoints( ApplicationImpl application ) {
    for( IConfigurationElement element : getEntryPointExtensions() ) {
      String className = element.getAttribute( "class" );
      String path = element.getAttribute( "path" );
      String id = element.getAttribute( "id" );
      String brandingId = element.getAttribute( "brandingId" );
      try {
        Class<? extends IEntryPoint> entryPointClass = loadClass( className, element );
        Map<String, String> properties = getBrandingProperties( brandingId );
        properties.put( BrandingUtil.ENTRY_POINT_BRANDING, brandingId );
        application.addEntryPoint( path, entryPointClass, properties );
      } catch( final Throwable thr ) {
        String text = "Could not register entry point ''{0}'' with id ''{1}''.";
        Object[] param = new Object[] { className, id };
        logProblem( text, param, thr, element.getContributor().getName() );
      }
    }
  }

  @SuppressWarnings( "unchecked" )
  private void registerApplicationEntryPoints( ApplicationImpl application ) {
    for( IExtension extension : getApplicationExtensions() ) {
      IConfigurationElement configElement = extension.getConfigurationElements()[ 0 ];
      IConfigurationElement[] runElement = configElement.getChildren( RUN );
      String className = runElement[ 0 ].getAttribute( "class" ); //$NON-NLS-1$
      String applicationId = extension.getUniqueIdentifier();
      // [if] Use full qualified applicationParameter, see bug 321360
      String applicationParameter = extension.getUniqueIdentifier();
      String isVisible = configElement.getAttribute( PT_APP_VISIBLE );
      IConfigurationElement[] paramElements = runElement[ 0 ].getChildren( "parameter" );
      String brandingId = findParameter( paramElements, "brandingId" );
      String servletPath = findParameter( paramElements, "path" );
      try {
        if( isVisible == null || Boolean.valueOf( isVisible ).booleanValue() ) {
          Class<? extends IApplication> applicationClass = loadClass( className, configElement );
          IEntryPointFactory factory = createApplicationEntryPointFactory( applicationClass );
          Map<String, String> properties = getBrandingProperties( brandingId );
          properties.put( BrandingUtil.ENTRY_POINT_BRANDING, brandingId );
          application.addEntryPoint( servletPath, factory, properties );
          EntryPointParameters.register( applicationId, applicationParameter );
        }
      } catch( ClassNotFoundException exception ) {
        String text =   "Could not register application ''{0}'' " //$NON-NLS-1$
                      + "with servlet path ''{1}''."; //$NON-NLS-1$
        Object[] params = new Object[]{ className, servletPath };
        logProblem( text, params, exception, configElement.getContributor().getName() );
      }
    }
  }

  @SuppressWarnings( "unchecked" )
  private void registerThemeableWidgets( Application application ) {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IExtensionPoint ep = registry.getExtensionPoint( ID_THEMEABLE_WIDGETS );
    IConfigurationElement[] widgetExts = ep.getConfigurationElements();
    for( int i = 0; i < widgetExts.length; i++ ) {
      String contributorName = widgetExts[ i ].getContributor().getName();
      String widgetClass = widgetExts[ i ].getAttribute( "class" );
      try {
        final Bundle bundle = Platform.getBundle( contributorName );
        Class<? extends Widget> widget
          = (Class<? extends Widget>)bundle.loadClass( widgetClass );
        application.addThemableWidget( widget );
      } catch( final Throwable thr ) {
        String text = "Could not register themeable widget ''{0}''.";
        Object[] param = new Object[] { widgetClass };
        logProblem( text, param, thr, contributorName );
      }
    }
  }

  private void registerThemes( Application application ) {
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
          application.addStyleSheet( themeId, themeFile, resourceLoader );
        } catch( final Exception e ) {
          String text = "Could not register custom theme ''{0}'' from file ''{1}''.";
          Object[] param = new Object[]{ themeId, themeFile };
          logProblem( text, param, e, contributorName );
        }
      }
    }
  }

  private void registerThemeContributions( Application application ) {
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
          application.addStyleSheet( themeId, themeFile, loader );
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
      public InputStream getResourceAsStream( final String resourceName ) throws IOException {
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

  private static void registerResources( Application application ) {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IExtensionPoint point = registry.getExtensionPoint( ID_RESOURCES );
    IConfigurationElement[] elements = point.getConfigurationElements();
    DependentResource[] resources = loadResources( elements );
    resources = sortResources( resources );
    registerResources( application, resources );
  }

  private static String findParameter( IConfigurationElement[] paramElements, String parameterName ) {
    String result = null;
    for( IConfigurationElement paramElement : paramElements ) {
      if( parameterName.equals( paramElement.getAttribute( "name" ) ) ) {
        result = paramElement.getAttribute( "value" );
      }
    }
    return result;
  }

  private static IEntryPointFactory
    createApplicationEntryPointFactory( final Class<? extends IApplication> applicationClass )
  {
    return new IEntryPointFactory() {
      public IEntryPoint create() {
        return new EntryPointApplicationWrapper( applicationClass );
      }
    };
  }

  private static DependentResource[] loadResources( IConfigurationElement[] elements ) {
    DependentResource[] result = new DependentResource[ elements.length ];
    for( int i = 0; i < elements.length; i++ ) {
      try {
        IResource resource = ( IResource )elements[ i ].createExecutableExtension( "class" );
        String resourceId = elements[ i ].getAttribute( "id" );
        IConfigurationElement[] dependsOn = elements[ i ].getChildren( "dependsOn" );
        List<String> resourceDependencies = new ArrayList<String>();
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
    List<String> sortedResourceIds = new ArrayList<String>();
    List<DependentResource> deferredResources = new ArrayList<DependentResource>();
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
          Iterator<DependentResource> iterator = deferredResources.iterator();
          while( iterator.hasNext() ) {
            DependentResource deferredResource = iterator.next();
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

  private static void registerResources( Application application, DependentResource[] resources ) {
    for( DependentResource dependentResource : resources ) {
      if( dependentResource != null ) {
        registerResource( application, dependentResource.resource );
      }
    }
  }

  private static void registerResource( Application application, final IResource resource ) {
    ApplicationContext applicationContext = getApplicationContext( application );
    if( resource.isExternal() ) {
      applicationContext.getStartupPage().addJsLibrary( resource.getLocation() );
    } else {
      String location = resource.getLocation();
      application.addResource( location, new WorkbenchResourceLoader( resource ) );
    }
  }

  private void registerServiceHandlers( Application application ) {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IExtensionPoint point = registry.getExtensionPoint( ID_SERVICE_HANDLER );
    IConfigurationElement[] elements = point.getConfigurationElements();
    for( int i = 0; i < elements.length; i++ ) {
      try {
        String id = elements[ i ].getAttribute( "id" );
        if( id != null ) {
          Object extObject = elements[ i ].createExecutableExtension( "class" );
          ServiceHandler handler = ( ServiceHandler )extObject;
          application.addServiceHandler( id, handler );
        }
      } catch( final CoreException ce ) {
        WorkbenchPlugin.getDefault().getLog().log( ce.getStatus() );
      }
    }
  }

  private void registerBrandings( Application application ) {
    try {
      new BrandingExtension( application, httpServiceReference ).read();
    } catch( final IOException ioe ) {
      throw new RuntimeException( "Unable to read branding extension", ioe );
    }
  }
  
  private static AbstractBranding findBrandingById( String id ) {
    AbstractBranding result = null;
    AbstractBranding[] brandings = BrandingManager.getInstance().getAll();
    for( AbstractBranding branding : brandings ) {
      if( branding.getId() != null && branding.getId().equals( id ) ) {
        result = branding;
      }
    }
    return result;
  }

  private Map<String, String> getBrandingProperties( String brandingId ) {
    AbstractBranding branding = findBrandingById( brandingId );
    Map<String,String> result = new HashMap<String,String>();
    if( branding != null ) {
      result.put( WebClient.THEME_ID, branding.getThemeId() );
      result.put( WebClient.BODY_HTML, branding.getBody() );
      result.put( WebClient.PAGE_TITLE, branding.getTitle() );
      result.put( WebClient.FAVICON, branding.getFavIcon() );
      result.put( WebClient.HEAD_HTML, BrandingUtil.headerMarkup( branding ) );
    }
    return result;
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

  private static ApplicationContext getApplicationContext( Application application ) {
    // TODO [rh] remove ApplicationImpl#getAdapter(), this is the only caller
    ApplicationImpl applicationImpl = ( ApplicationImpl )application;
    return applicationImpl.getAdapter( ApplicationContext.class );
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


  private static <T> T loadClass( String className, IConfigurationElement element ) 
    throws ClassNotFoundException 
  {
    Bundle bundle = Platform.getBundle( element.getContributor().getName() );
    return ( T )bundle.loadClass( className );
  }

  private static class WorkbenchResourceLoader implements ResourceLoader {
    private final IResource resource;

    private WorkbenchResourceLoader( IResource resource ) {
      this.resource = resource;
    }

    public InputStream getResourceAsStream( String resourceName ) {
      return resource.getLoader().getResourceAsStream( resource.getLocation() );
    }
  }

  private static final class DependentResource {
    public final IResource resource;
    public final String id;
    public final List<String> dependencies;

    public DependentResource( IResource resource, String id, List<String> dependencies ) {
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
