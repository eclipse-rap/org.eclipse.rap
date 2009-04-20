/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.engine;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.*;

import javax.servlet.*;

import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.internal.AdapterFactoryRegistry;
import org.eclipse.rwt.internal.branding.BrandingManager;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.resources.*;
import org.eclipse.rwt.internal.service.*;
import org.eclipse.rwt.internal.theme.ResourceLoader;
import org.eclipse.rwt.internal.theme.ThemeManager;
import org.eclipse.rwt.lifecycle.PhaseListener;
import org.eclipse.rwt.resources.IResource;
import org.eclipse.rwt.resources.IResourceManagerFactory;
import org.eclipse.rwt.service.ISettingStoreFactory;
import org.eclipse.rwt.service.RWTFileSettingStoreFactory;
import org.eclipse.swt.internal.widgets.WidgetAdapterFactory;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;


public final class RWTServletContextListener implements ServletContextListener {

  private static final String PREFIX = "org.eclipse.rwt.";
  public static final String ENTRY_POINTS_PARAM
    = PREFIX + "entryPoints";
  public static final String THEMES_PARAM
    = PREFIX + "themes";
  public static final String RESOURCE_MANAGER_FACTORY_PARAM
    = PREFIX + "resourceManagerFactory";
  public static final String SETTING_STORE_FACTORY_PARAM
    = PREFIX + "settingStoreFactory";
  public static final String ADAPTER_FACTORIES_PARAM
    = PREFIX + "adapterFactories";
  public static final String PHASE_LISTENERS_PARAM
    = PREFIX + "phaseListeners";
  public static final String RESOURCES_PARAM
    = PREFIX + "resources";
  public static final String BRANDINGS_PARAM
    = PREFIX + "brandings";

  private static final String SEPARATOR = ",";

  private static final String REGISTERED_ENTRY_POINTS
    = RWTServletContextListener.class.getName() + "registeredEntryPoints";
  private static final String REGISTERED_PHASE_LISTENERS
    = RWTServletContextListener.class.getName() + "registeredPhaseListeners";
  private static final String REGISTERED_RESOURCES
    = RWTServletContextListener.class.getName() + "registeredResources";
  private static final String REGISTERED_BRANDINGS
    = RWTServletContextListener.class.getName() + "registeredBrandings";
  
  ///////////////////////////////////////////
  // implementation of ServletContextListener

  public void contextInitialized( final ServletContextEvent evt ) {
    registerThemes( evt.getServletContext() );
    registerBrandings( evt.getServletContext() );
    registerEntryPoints( evt.getServletContext() );
    registerResourceManagerFactory( evt.getServletContext() );
    registerSettingStoreFactory( evt.getServletContext() );
    registerAdapterFactories( evt.getServletContext() );
    registerPhaseListener( evt.getServletContext() );
    registerResources( evt.getServletContext() );
    registerUICallBackServiceHandler();
    registerJSLibraryServiceHandler();
    LifeCycleServiceHandler.configurer 
      = new LifeCycleServiceHandlerConfigurer();
    ResourceUtil.startJsConcatenation();
  }

  public void contextDestroyed( final ServletContextEvent evt ) {
    deregisterThemes( evt.getServletContext() );
    deregisterBrandings( evt.getServletContext() );
    deregisterEntryPoints( evt.getServletContext() );
    deregisterPhaseListeners( evt.getServletContext() );
    deregisterResources( evt.getServletContext() );
    deregisterUICallBackServiceHandler();
    deregisterJSLibraryServiceHandler();
    LifeCycleFactory.destroy();
  }

  ////////////////////////////////////////////////////////////
  // helping methods - entry point registration/deregistration

  private static void registerEntryPoints( final ServletContext context ) {
    Set registeredEntryPoints = new HashSet();
    String value = context.getInitParameter( ENTRY_POINTS_PARAM );
    if( value != null ) {
      String[] entryPoints = value.split( SEPARATOR );
      for( int i = 0; i < entryPoints.length; i++ ) {
        String entryPoint = entryPoints[ i ];
        String[] parts = entryPoint.trim().split( "#" );
        String className = parts[ 0 ];
        String entryPointName = EntryPointManager.DEFAULT;
        if( parts.length > 1 ) {
          entryPointName = parts[ 1 ];
        }
        try {
          Class clazz = Class.forName( className );
          EntryPointManager.register( entryPointName, clazz );
          registeredEntryPoints.add( entryPointName );
        } catch( final Exception ex ) {
          String text = "Failed to register entry point ''{0}''.";
          Object[] args = new Object[] { entryPoint };
          String msg = MessageFormat.format( text, args );
          context.log( msg, ex );
        }
      }
    }
    setRegisteredEntryPoints( context, registeredEntryPoints );
  }

  private static void deregisterEntryPoints( final ServletContext context ) {
    String[] entryPoints = getRegisteredEntryPoints( context );
    if( entryPoints != null ) {
      for( int i = 0; i < entryPoints.length; i++ ) {
        EntryPointManager.deregister( entryPoints[ i ] );
      }
    }
  }

  private static void setRegisteredEntryPoints( final ServletContext ctx,
                                                final Set entryPoints )
  {
    String[] value = new String[ entryPoints.size() ];
    entryPoints.toArray( value );
    ctx.setAttribute( REGISTERED_ENTRY_POINTS, value );
  }

  private static String[] getRegisteredEntryPoints( final ServletContext ctx ) {
    return ( String[] )ctx.getAttribute( REGISTERED_ENTRY_POINTS );
  }

  //////////////////////////////////////////////////
  // Helping methods - resource manager registration

  private static void registerResourceManagerFactory(
    final ServletContext context )
  {
    String factoryName
      = context.getInitParameter( RESOURCE_MANAGER_FACTORY_PARAM );
    if( factoryName != null ) {
      try {
        Class clazz = Class.forName( factoryName );
        IResourceManagerFactory factory;
        factory = ( IResourceManagerFactory )clazz.newInstance();
        ResourceManager.register( factory );
      } catch( final Exception ex ) {
        String text = "Failed to register resource manager factory ''{0}''.";
        String msg = MessageFormat.format( text, new Object[] { factoryName } );
        context.log( msg, ex );
      }
    } else {
      ResourceManager.register( new DefaultResourceManagerFactory() );
    }
  }
  
  ///////////////////////////////////////////////////////
  // Helping methods - setting store factory registration
  
  private static void registerSettingStoreFactory(
    final ServletContext context ) 
  {
    if( !SettingStoreManager.hasFactory() ) {
      String factoryName
        = context.getInitParameter( SETTING_STORE_FACTORY_PARAM );
      if( factoryName != null ) {
        try {
          Class clazz = Class.forName( factoryName );
          ISettingStoreFactory factory; 
          factory = ( ISettingStoreFactory )clazz.newInstance();
          SettingStoreManager.register( factory );
        } catch( final Exception ex ) {
          String text = "Failed to register setting store factory ''{0}''.";
          String msg = MessageFormat.format( text, new Object[] { factoryName } );
          context.log( msg, ex );
        }
      } else {
        SettingStoreManager.register( new RWTFileSettingStoreFactory() );
      }
    }
  }
  
  /////////////////////////////////////////////////
  // Helping methods - adapter factory registration

  private static void registerAdapterFactories( final ServletContext context ) {
    String initParam = context.getInitParameter( ADAPTER_FACTORIES_PARAM );
    if( initParam != null ) {
      String[] factoryParams = initParam.split( SEPARATOR );
      for( int i = 0; i < factoryParams.length; i++ ) {
        String[] classNames = factoryParams[ i ].trim().split( "#" );
        if( classNames.length != 2 ) {
          Object[] param = new Object[] { factoryParams[ i ] };
          String text = "''{0}'' is not a valid factory-adaptable pair.";
          String msg = MessageFormat.format( text, param );
          context.log( msg );
        } else {
          try {
            Class factoryClass = Class.forName( classNames[ 0 ] );
            Class adaptableClass = Class.forName( classNames[ 1 ] );
            AdapterFactoryRegistry.add( factoryClass, adaptableClass );
          } catch( final Throwable thr ) {
            Object[] param = new Object[] { factoryParams[ i ] };
            String text;
            text = "Could not register the factory-adaptable ''{0}'' pair.";
            String msg = MessageFormat.format( text, param );
            context.log( msg, thr );
          }
        }
      }
    } else {
      AdapterFactoryRegistry.add( LifeCycleAdapterFactory.class,
                                  Widget.class );
      AdapterFactoryRegistry.add( LifeCycleAdapterFactory.class,
                                  Display.class );
      AdapterFactoryRegistry.add( WidgetAdapterFactory.class,
                                  Widget.class );
      AdapterFactoryRegistry.add( WidgetAdapterFactory.class,
                                  Display.class );
    }
  }

  ///////////////////////////////////////////////////////////////
  // Helping methods - phase listener registration/deregistration

  private static void registerPhaseListener( final ServletContext context ) {
    List phaseListeners = new ArrayList();
    String initParam = context.getInitParameter( PHASE_LISTENERS_PARAM );
    if( initParam != null ) {
      String[] listenerNames = initParam.split( SEPARATOR );
      for( int i = 0; i < listenerNames.length; i++ ) {
        String className = listenerNames[ i ].trim();
        try {
          Class clazz = Class.forName( className );
          PhaseListener listener = ( PhaseListener )clazz.newInstance();
          phaseListeners.add( listener );
        } catch( final Throwable thr ) {
          String text = "Failed to register phase listener ''{0}''.";
          String msg = MessageFormat.format( text, new Object[] { className } );
          context.log( msg, thr );
        }
      }
    } else {
      phaseListeners.add( new PreserveWidgetsPhaseListener() );
      phaseListeners.add( new CurrentPhase.Listener() );
    }
    PhaseListener[] registeredListeners;
    registeredListeners = new PhaseListener[ phaseListeners.size() ];
    phaseListeners.toArray( registeredListeners );
    for( int i = 0; i < registeredListeners.length; i++ ) {
      PhaseListenerRegistry.add( registeredListeners[ i ] );
    }
    context.setAttribute( REGISTERED_PHASE_LISTENERS, registeredListeners );
  }

  private static void deregisterPhaseListeners( final ServletContext context ) {
    PhaseListener[] listeners = getRegisteredPhaseListeners( context );
    if( listeners != null ) {
      for( int i = 0; i < listeners.length; i++ ) {
        PhaseListenerRegistry.remove( listeners[ i ] );
      }
    }
  }

  private static PhaseListener[] getRegisteredPhaseListeners(
    final ServletContext ctx )
  {
    return ( PhaseListener[] )ctx.getAttribute( REGISTERED_PHASE_LISTENERS );
  }

  //////////////////////////////////////////
  // Helping methods - resource registration
  
  private static void registerResources( final ServletContext context ) {
    List resources = new ArrayList();
    String initParam = context.getInitParameter( RESOURCES_PARAM );
    if( initParam != null ) {
      String[] resourceClassNames = initParam.split( SEPARATOR );
      for( int i = 0; i < resourceClassNames.length; i++ ) {
        String className = resourceClassNames[ i ].trim();
        try {
          Class clazz = Class.forName( className );
          IResource resource = ( IResource )clazz.newInstance();
          resources.add( resource );
        } catch( final Throwable thr ) {
          String text = "Failed to register resource ''{0}''.";
          String msg = MessageFormat.format( text, new Object[] { className } );
          context.log( msg, thr );
        }
      }
    }
    IResource[] registeredResources;
    registeredResources = new IResource[ resources.size() ];
    resources.toArray( registeredResources );
    for( int i = 0; i < registeredResources.length; i++ ) {
      ResourceRegistry.add( registeredResources[ i ] );
    }
    context.setAttribute( REGISTERED_RESOURCES, registeredResources );
  }

  private static void deregisterResources( final ServletContext context ) {
    ResourceRegistry.clear();
  }

  ///////////////////////////////////////
  // Helping methods - theme registration
  
  private static void registerThemes( final ServletContext context ) {
    ThemeManager manager = ThemeManager.getInstance();
    manager.initialize();
    String value = context.getInitParameter( THEMES_PARAM );
    ResourceLoader loader = new ResourceLoader() {
      public InputStream getResourceAsStream( final String resourceName )
        throws IOException
      {
        // IMPORTANT: use ClassLoader#getResourceAsStream instead of 
        // Class#getResourceAsStream to retrieve resource (see respective 
        // JavaDoc)
        return getClass().getClassLoader().getResourceAsStream( resourceName );
      }
    };
    if( value != null ) {
      String[] themes = value.split( SEPARATOR );
      for( int i = 0; i < themes.length; i++ ) {
        String[] parts = themes[ i ].trim().split( "#" );
        if( parts.length >= 2 ) {
          String themeId = parts[ 0 ];
          String fileName = parts[ 1 ];
          try {
            String themeName = "Unnamed Theme: " + themeId;
            manager.registerTheme( themeId, themeName, fileName, loader );
          } catch( Exception e ) {
            String text = "Failed to register custom theme ''{0}'' "
                          + "from resource ''{1}''";
            Object[] args = new Object[] { themeId, fileName };
            String msg = MessageFormat.format( text, args );
            context.log( msg, e );
          }
        }
      }
    }
  }

  private static void deregisterThemes( final ServletContext servletContext ) {
    ThemeManager.getInstance().reset();
  }

  ////////////////////////////////////////////////
  // Helping methods - UI callback service handler
  
  private static void registerUICallBackServiceHandler() {
    ServiceManager.registerServiceHandler( UICallBackServiceHandler.HANDLER_ID,
                                           new UICallBackServiceHandler() );
  }

  private static void deregisterUICallBackServiceHandler() {
    String id = UICallBackServiceHandler.HANDLER_ID;
    ServiceManager.unregisterServiceHandler( id );
  }

  //////////////////////////////////////////
  // Helping methods - branding registration
  
  private static void registerBrandings( final ServletContext servletContext ) {
    String value = servletContext.getInitParameter( BRANDINGS_PARAM );
    if( value != null ) {
      List registeredBrandings = new ArrayList(); 
      String[] brandings = value.split( SEPARATOR );
      for( int i = 0; i < brandings.length; i++ ) {
        String className = brandings[ i ].trim();
        try {
          Object newInstance = Class.forName( className ).newInstance();
          AbstractBranding branding = ( AbstractBranding )newInstance;
          BrandingManager.register( branding );
          registeredBrandings.add( branding );
        } catch( Exception e ) {
          String text = "Failed to register branding ''{0}''.";
          String msg = MessageFormat.format( text, new Object[] { className } );
          servletContext.log( msg, e );
        }
      }
      setRegisteredBrandings( servletContext, registeredBrandings );
    }
  }
  
  private static void setRegisteredBrandings( final ServletContext context, 
                                              final List brandings ) 
  {
    AbstractBranding[] registeredBrandings
      = new AbstractBranding[ brandings.size() ];
    brandings.toArray( registeredBrandings );
    context.setAttribute( REGISTERED_BRANDINGS, registeredBrandings );
  }
  
  private static void deregisterBrandings( final ServletContext context ) {
    AbstractBranding[] brandings 
      = ( AbstractBranding[] )context.getAttribute( REGISTERED_BRANDINGS );
    if( brandings != null ) {
      for( int i = 0; i < brandings.length; i++ ) {
        BrandingManager.deregister( brandings[ i ] );
      }
    }
  }
  
  ////////////////////////////////////////////////
  // Helping methods - JS Library service handler
  
  private static void registerJSLibraryServiceHandler() {
    ServiceManager.registerServiceHandler( JSLibraryServiceHandler.HANDLER_ID,
                                           new JSLibraryServiceHandler() );
  }
  
  private static void deregisterJSLibraryServiceHandler() {
    String id = JSLibraryServiceHandler.HANDLER_ID;
    ServiceManager.unregisterServiceHandler( id );
  }
}