/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
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
package org.eclipse.rwt.internal.engine;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.*;

import javax.servlet.*;

import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.internal.*;
import org.eclipse.rwt.internal.branding.BrandingManager;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.resources.*;
import org.eclipse.rwt.internal.service.*;
import org.eclipse.rwt.internal.theme.*;
import org.eclipse.rwt.internal.theme.css.CssFileReader;
import org.eclipse.rwt.internal.theme.css.StyleSheet;
import org.eclipse.rwt.internal.util.ClassInstantiationException;
import org.eclipse.rwt.internal.util.ClassUtil;
import org.eclipse.rwt.lifecycle.PhaseListener;
import org.eclipse.rwt.resources.IResource;
import org.eclipse.rwt.resources.IResourceManagerFactory;
import org.eclipse.rwt.service.ISettingStoreFactory;
import org.eclipse.rwt.service.RWTFileSettingStoreFactory;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;


public final class RWTServletContextListener implements ServletContextListener {

  private static final String PREFIX = "org.eclipse.rwt.";
  public static final String ENTRY_POINTS_PARAM = PREFIX + "entryPoints";
  public static final String THEMES_PARAM = PREFIX + "themes";
  public static final String RESOURCE_MANAGER_FACTORY_PARAM = PREFIX + "resourceManagerFactory";
  public static final String SETTING_STORE_FACTORY_PARAM = PREFIX + "settingStoreFactory";
  public static final String ADAPTER_FACTORIES_PARAM = PREFIX + "adapterFactories";
  public static final String PHASE_LISTENERS_PARAM = PREFIX + "phaseListeners";
  public static final String RESOURCES_PARAM = PREFIX + "resources";
  public static final String BRANDINGS_PARAM = PREFIX + "brandings";

  private static final String SEPARATOR = ",";

  private static final String REGISTERED_ENTRY_POINTS
    = RWTServletContextListener.class.getName() + "registeredEntryPoints";
  private static final String REGISTERED_PHASE_LISTENERS
    = RWTServletContextListener.class.getName() + "registeredPhaseListeners";
  private static final String REGISTERED_RESOURCES
    = RWTServletContextListener.class.getName() + "registeredResources";
  private static final String REGISTERED_BRANDINGS
    = RWTServletContextListener.class.getName() + "registeredBrandings";
  
  private static final ClassLoader CLASS_LOADER = RWTServletContextListener.class.getClassLoader();
  
  public static class ContextDestroyer implements Runnable {
    protected final ServletContext servletContext;

    public ContextDestroyer( ServletContext servletContext ) {
      this.servletContext = servletContext;
    }

    public void run() {
/////////////////////////////////////////////////////////////////////////
// TODO [fappel]: check which deregistration methods are really necessary
//                since all singletons get destroyed at the end of 
//                the context lifecycle. Commented deregisteredThemes
//                since this causes performance problems of tests...  
//      deregisterThemes( servletContext );
      deregisterBrandings( servletContext );
      deregisterEntryPoints( servletContext );
      deregisterPhaseListeners( servletContext );
      deregisterResources( servletContext );
      deregisterUICallBackServiceHandler();
      deregisterJSLibraryServiceHandler();
      LifeCycleFactory.destroy();
    }
  }

  public static class ContextInitializer implements Runnable {
    protected final ServletContext servletContext;

    public ContextInitializer( ServletContext servletContext ) {
      this.servletContext = servletContext;
    }

    public void run() {
      registerEngineConfig( servletContext );
      registerResourceManagerFactory( servletContext );
      registerThemes( servletContext );
      registerBrandings( servletContext );
      registerEntryPoints( servletContext );
      registerSettingStoreFactory( servletContext );
      registerAdapterFactories( servletContext );
      registerPhaseListener( servletContext );
      registerResources( servletContext );
      registerUICallBackServiceHandler();
      registerJSLibraryServiceHandler();
      ResourceUtil.startJsConcatenation();
    }
  }

  ///////////////////////////////////////////
  // implementation of ServletContextListener

  public void contextInitialized( final ServletContextEvent evt ) {
    ServletContext servletContext = evt.getServletContext();
    RWTContext rwtContext = registerDefaultRWTContext( servletContext );
    ContextInitializer initializer = new ContextInitializer( servletContext );
    RWTContextUtil.runWithInstance( rwtContext, initializer );
  }

  public void contextDestroyed( final ServletContextEvent evt ) {
    ServletContext servletContext = evt.getServletContext();
    RWTContext rwtContext = RWTContextUtil.getRWTContext( servletContext );
    ContextDestroyer destroyer = new ContextDestroyer( servletContext );
    RWTContextUtil.runWithInstance( rwtContext, destroyer );
    deregisterDefaultRWTContext( servletContext );
  }

  ////////////////////////////////////////////////////////////
  // helping methods - entry point registration/deregistration
  
  private RWTContext registerDefaultRWTContext( ServletContext servletContext ) {
    return RWTContextUtil.registerDefaultRWTContext( servletContext );
  }
  
  void deregisterDefaultRWTContext( ServletContext servletContext ) {
    RWTContextUtil.deregisterRWTContext( servletContext );
  }

  public static void registerEngineConfig( ServletContext servletContext ) {
    String realPath = servletContext.getRealPath( "/" );
    EngineConfig engineConfig = new EngineConfig( realPath );
    ConfigurationReader.setEngineConfig( engineConfig );
  }

  public static void registerEntryPoints( final ServletContext context ) {
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

  public static void deregisterEntryPoints( final ServletContext context ) {
    String[] entryPoints = getRegisteredEntryPoints( context );
    if( entryPoints != null ) {
      for( int i = 0; i < entryPoints.length; i++ ) {
        EntryPointManager.deregister( entryPoints[ i ] );
      }
    }
  }

  public static void setRegisteredEntryPoints( final ServletContext ctx,
                                               final Set entryPoints )
  {
    String[] value = new String[ entryPoints.size() ];
    entryPoints.toArray( value );
    ctx.setAttribute( REGISTERED_ENTRY_POINTS, value );
  }

  public static String[] getRegisteredEntryPoints( final ServletContext ctx ) {
    return ( String[] )ctx.getAttribute( REGISTERED_ENTRY_POINTS );
  }

  //////////////////////////////////////////////////
  // Helping methods - resource manager registration

  public static void registerResourceManagerFactory(
    final ServletContext context )
  {
    String factoryName = context.getInitParameter( RESOURCE_MANAGER_FACTORY_PARAM );
    if( factoryName != null ) {
      try {
        IResourceManagerFactory factory
          = ( IResourceManagerFactory )ClassUtil.newInstance( CLASS_LOADER, factoryName );
        ResourceManager.register( factory );
      } catch( ClassInstantiationException ex ) {
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
  
  public static void registerSettingStoreFactory(
    final ServletContext context ) 
  {
    if( !SettingStoreManager.hasFactory() ) {
      String factoryName
        = context.getInitParameter( SETTING_STORE_FACTORY_PARAM );
      if( factoryName != null ) {
        try {
          ISettingStoreFactory factory
            = ( ISettingStoreFactory )ClassUtil.newInstance( CLASS_LOADER, factoryName ); 
          SettingStoreManager.register( factory );
        } catch( ClassInstantiationException cie ) {
          String text = "Failed to register setting store factory ''{0}''.";
          String msg = MessageFormat.format( text, new Object[] { factoryName } );
          context.log( msg, cie );
        }
      } else {
        SettingStoreManager.register( new RWTFileSettingStoreFactory() );
      }
    }
  }
  
  /////////////////////////////////////////////////
  // Helping methods - adapter factory registration

  public static void registerAdapterFactories( final ServletContext context ) {
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
    }
  }

  ///////////////////////////////////////////////////////////////
  // Helping methods - phase listener registration/deregistration

  public static void registerPhaseListener( final ServletContext context ) {
    List phaseListeners = new ArrayList();
    String initParam = context.getInitParameter( PHASE_LISTENERS_PARAM );
    if( initParam != null ) {
      String[] listenerNames = initParam.split( SEPARATOR );
      for( int i = 0; i < listenerNames.length; i++ ) {
        String className = listenerNames[ i ].trim();
        try {
          PhaseListener listener = ( PhaseListener )ClassUtil.newInstance( CLASS_LOADER, className );
          phaseListeners.add( listener );
        } catch( ClassInstantiationException cie ) {
          String text = "Failed to register phase listener ''{0}''.";
          String msg = MessageFormat.format( text, new Object[] { className } );
          context.log( msg, cie );
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

  public static void deregisterPhaseListeners( final ServletContext context ) {
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
  
  public static void registerResources( final ServletContext context ) {
    List resources = new ArrayList();
    String initParam = context.getInitParameter( RESOURCES_PARAM );
    if( initParam != null ) {
      String[] resourceClassNames = initParam.split( SEPARATOR );
      for( int i = 0; i < resourceClassNames.length; i++ ) {
        String className = resourceClassNames[ i ].trim();
        try {
          IResource resource = ( IResource )ClassUtil.newInstance( CLASS_LOADER, className );
          resources.add( resource );
        } catch( ClassInstantiationException cie ) {
          String text = "Failed to register resource ''{0}''.";
          String msg = MessageFormat.format( text, new Object[] { className } );
          context.log( msg, cie );
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

  public static void deregisterResources( final ServletContext context ) {
    ResourceRegistry.clear();
  }

  ///////////////////////////////////////
  // Helping methods - theme registration

  public static void registerThemes( final ServletContext context ) {
    ThemeManager manager = ThemeManager.getInstance();
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
            StyleSheet styleSheet = CssFileReader.readStyleSheet( fileName, loader );
            Theme theme = new Theme( themeId, themeName, styleSheet );
            manager.registerTheme( theme );
          } catch( Exception e ) {
            String text = "Failed to register custom theme ''{0}'' from resource ''{1}''";
            Object[] args = new Object[] { themeId, fileName };
            String msg = MessageFormat.format( text, args );
            context.log( msg, e );
          }
        }
      }
    }
    manager.initialize();
  }

  public static void deregisterThemes( final ServletContext servletContext ) {
    ThemeManager.resetInstance();
  }

  ////////////////////////////////////////////////
  // Helping methods - UI callback service handler
  
  public static void registerUICallBackServiceHandler() {
    ServiceManager.registerServiceHandler( UICallBackServiceHandler.HANDLER_ID,
                                           new UICallBackServiceHandler() );
  }

  public static void deregisterUICallBackServiceHandler() {
    String id = UICallBackServiceHandler.HANDLER_ID;
    ServiceManager.unregisterServiceHandler( id );
  }

  //////////////////////////////////////////
  // Helping methods - branding registration
  
  public static void registerBrandings( final ServletContext servletContext ) {
    String value = servletContext.getInitParameter( BRANDINGS_PARAM );
    if( value != null ) {
      List registeredBrandings = new ArrayList(); 
      String[] brandings = value.split( SEPARATOR );
      for( int i = 0; i < brandings.length; i++ ) {
        String className = brandings[ i ].trim();
        try {
          AbstractBranding branding
            = ( AbstractBranding )ClassUtil.newInstance( CLASS_LOADER, className );
          BrandingManager.register( branding );
          registeredBrandings.add( branding );
        } catch( ClassInstantiationException cie ) {
          String text = "Failed to register branding ''{0}''.";
          String msg = MessageFormat.format( text, new Object[] { className } );
          servletContext.log( msg, cie );
        }
      }
      setRegisteredBrandings( servletContext, registeredBrandings );
    }
  }
  
  public static void setRegisteredBrandings( final ServletContext context, 
                                             final List brandings ) 
  {
    AbstractBranding[] registeredBrandings
      = new AbstractBranding[ brandings.size() ];
    brandings.toArray( registeredBrandings );
    context.setAttribute( REGISTERED_BRANDINGS, registeredBrandings );
  }
  
  public static void deregisterBrandings( final ServletContext context ) {
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
  
  public static void registerJSLibraryServiceHandler() {
    ServiceManager.registerServiceHandler( JSLibraryServiceHandler.HANDLER_ID,
                                           new JSLibraryServiceHandler() );
  }

  public static void deregisterJSLibraryServiceHandler() {
    String id = JSLibraryServiceHandler.HANDLER_ID;
    ServiceManager.unregisterServiceHandler( id );
  }
}