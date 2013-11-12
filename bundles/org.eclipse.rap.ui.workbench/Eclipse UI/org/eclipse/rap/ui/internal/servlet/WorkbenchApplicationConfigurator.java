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
import java.util.HashMap;
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
import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.rap.rwt.application.EntryPointFactory;
import org.eclipse.rap.rwt.client.WebClient;
import org.eclipse.rap.rwt.internal.lifecycle.DefaultEntryPointFactory;
import org.eclipse.rap.rwt.lifecycle.PhaseListener;
import org.eclipse.rap.rwt.service.ResourceLoader;
import org.eclipse.rap.rwt.service.ServiceHandler;
import org.eclipse.rap.rwt.service.SettingStoreFactory;
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
  private static final String ID_SETTING_STORES = "org.eclipse.rap.ui.settingstores";

  private static final String PROP_SETTING_STORES_FACTORY
    = "org.eclipse.rap.rwt.settingStoreFactory";

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
    registerEntryPoints( application );
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
    SettingStoreFactory result = null;
    String factoryId = getOSGiProperty( PROP_SETTING_STORES_FACTORY );
    if( factoryId != null ) {
      result = loadSettingStoreFactory( factoryId );
    }
    if( result == null ) {
      result = new WorkbenchFileSettingStoreFactory(); // default
    }
    application.setSettingStoreFactory( result );
  }

  private SettingStoreFactory loadSettingStoreFactory( String factoryId ) {
    SettingStoreFactory result = null;
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

  private SettingStoreFactory loadSettingStoreFactory( IConfigurationElement element ) {
    SettingStoreFactory result = null;
    try {
      result = ( SettingStoreFactory )element.createExecutableExtension( "class" );
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
  private void registerEntryPoints( Application application ) {
    for( IConfigurationElement element : getEntryPointExtensions() ) {
      String id = element.getAttribute( "id" );
      String path = element.getAttribute( "path" );
      String className = element.getAttribute( "class" );
      String applicationId = element.getAttribute( "applicationId" );
      String brandingId = element.getAttribute( "brandingId" );
      try {
        EntryPointFactory entryPointFactory;
        if( className != null ) {
          Class<? extends EntryPoint> entryPointClass = loadClass( className, element );
          entryPointFactory = new DefaultEntryPointFactory( entryPointClass );
        } else if( applicationId != null ) {
          entryPointFactory = createEntryPointFactoryForApplication( applicationId );
        } else {
          throw new IllegalArgumentException( "Neither class nor applicationId specified" );
        }
        Map<String, String> properties = getBrandingProperties( brandingId );
        application.addEntryPoint( path, entryPointFactory, properties );
      } catch( final Throwable thr ) {
        String text = "Could not register entry point ''{0}'' with id ''{1}''.";
        Object[] param = new Object[] { className, id };
        logProblem( text, param, thr, element.getContributor().getName() );
      }
    }
  }

  @SuppressWarnings( "unchecked" )
  private EntryPointFactory createEntryPointFactoryForApplication( String applicationId )
    throws ClassNotFoundException
  {
    IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
    String extensionPointId = PI_RUNTIME + '.' + PT_APPLICATIONS;
    IExtension extension = extensionRegistry.getExtension( extensionPointId, applicationId );
    if( extension == null ) {
      String message = "Application extension not found by id: " + applicationId;
      throw new IllegalArgumentException( message );
    }
    IConfigurationElement configElement = extension.getConfigurationElements()[ 0 ];
    String isVisible = configElement.getAttribute( PT_APP_VISIBLE );
    if( isVisible != null && !Boolean.valueOf( isVisible ).booleanValue() ) {
      throw new IllegalArgumentException( "Application is not visible:" + applicationId );
    }
    IConfigurationElement[] runElement = configElement.getChildren( RUN );
    String className = runElement[ 0 ].getAttribute( "class" ); //$NON-NLS-1$
    Class<? extends IApplication> applicationClass = loadClass( className, configElement );
    return createApplicationEntryPointFactory( applicationClass );
  }

  private static EntryPointFactory
    createApplicationEntryPointFactory( final Class<? extends IApplication> applicationClass )
  {
    return new EntryPointFactory() {
      public EntryPoint create() {
        return new EntryPointApplicationWrapper( applicationClass );
      }
    };
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
        Class<? extends Widget> widget = (Class<? extends Widget>)bundle.loadClass( widgetClass );
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
          Object[] param = new Object[] { themeId, themeFile };
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
          Object[] param = new Object[] { themeId, themeFile };
          logProblem( text, param, e, contributorName );
        }
      }
    }
  }

  private static void registerResources( Application application ) {
    List<IResource> resources = ResourceReader.readResources();
    new ResourceRegisterer( application ).registerResources( resources );
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
    Map<String, String> result = new HashMap<String, String>();
    result.put( BrandingUtil.ENTRY_POINT_BRANDING, brandingId );
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

}
