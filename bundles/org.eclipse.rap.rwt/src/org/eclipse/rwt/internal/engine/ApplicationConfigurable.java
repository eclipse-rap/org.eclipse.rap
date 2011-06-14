/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.engine;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;

import org.eclipse.rwt.AdapterFactory;
import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.engine.Configurator;
import org.eclipse.rwt.engine.Context;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.resources.JSLibraryServiceHandler;
import org.eclipse.rwt.internal.service.ApplicationStoreImpl;
import org.eclipse.rwt.internal.service.ServiceManager;
import org.eclipse.rwt.internal.textsize.MeasurementListener;
import org.eclipse.rwt.internal.theme.*;
import org.eclipse.rwt.internal.theme.css.CssFileReader;
import org.eclipse.rwt.internal.theme.css.StyleSheet;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.rwt.lifecycle.PhaseListener;
import org.eclipse.rwt.resources.IResource;
import org.eclipse.rwt.service.*;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;


public class ApplicationConfigurable implements Configurable {
  private static final String ID_JS_LIBRARIES = JSLibraryServiceHandler.HANDLER_ID;
  private static final String ID_UI_CALL_BACK = UICallBackServiceHandler.HANDLER_ID;

  private final Configurator configurator;

  private static class ResourceLoaderImpl implements ResourceLoader {
    private final ClassLoader loader;
    
    private ResourceLoaderImpl( ClassLoader loader ) {
      this.loader = loader;
      
    }
    public InputStream getResourceAsStream( String resourceName ) throws IOException {
      return loader.getResourceAsStream( resourceName );
    }
  }
  
  private static class ContextImpl implements Context {
    private final ApplicationContext applicationContext;
    private final Configurator configurator;

    private ContextImpl( ApplicationContext applicationContext, Configurator configurator ) {
      this.applicationContext = applicationContext;
      this.configurator = configurator;
    }

    public void addPhaseListener( PhaseListener phaseListener ) {
      applicationContext.getPhaseListenerRegistry().add( phaseListener );
    }

    public void setSettingStoreFactory( ISettingStoreFactory settingStoreFactory ) {
      applicationContext.getSettingStoreManager().register( settingStoreFactory );
    }

    public void addEntryPoint( String entryPointName, Class<? extends IEntryPoint> type ) {
      applicationContext.getEntryPointManager().register( entryPointName, type );
    }

    public void addAddapterFactory( Class<?> adaptable, AdapterFactory adapterFactory ) {
      applicationContext.getAdapterManager().registerAdapters( adaptable, adapterFactory );
    }

    public void addResource( IResource resource ) {
      applicationContext.getResourceRegistry().add( resource );
    }

    public void addServiceHandler( String serviceHandlerId, IServiceHandler serviceHandler ) {
      ServiceManager serviceManager = applicationContext.getServiceManager();
      serviceManager.registerServiceHandler( serviceHandlerId, serviceHandler );
    }

    public void addBranding( AbstractBranding branding ) {
      applicationContext.getBrandingManager().register( branding );
    }

    public void addTheme( String themeId, String styleSheetLocation ) {
      StyleSheet styleSheet = readStyleSheet( styleSheetLocation );
      ThemeManager themeManager = applicationContext.getThemeManager();
      themeManager.registerTheme( new Theme( themeId, "unknown", styleSheet ) );
    }

    public void addThemableWidget( Class<? extends Widget> widget ) {
      ResourceLoaderImpl loader = new ResourceLoaderImpl( widget.getClassLoader() );
      applicationContext.getThemeManager().addThemeableWidget( widget, loader );
    }

    public void addThemeContribution( String themeId, String styleSheetLocation ) {
      StyleSheet styleSheet = readStyleSheet( styleSheetLocation );
      applicationContext.getThemeManager().getTheme( themeId ).addStyleSheet( styleSheet );
    }

    private StyleSheet readStyleSheet( String styleSheetLocation ) {
      StyleSheet result;
      ClassLoader classLoader = configurator.getClass().getClassLoader();
      ResourceLoaderImpl loader = new ResourceLoaderImpl( classLoader );
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

    public void setAttribute( String name, Object value ) {
      applicationContext.getApplicationStore().setAttribute( name, value );
    }
  }

  public ApplicationConfigurable( Configurator configurator ) {
    this.configurator = configurator;
  }

  public void configure( ApplicationContext applicationContext ) {
    configurator.configure( createContext( applicationContext ) );
    
    addInternalAdapterFactories( applicationContext );
    addInternalPhaseListeners( applicationContext );
    addInternalServiceHandlers( applicationContext );
    setInternalSettingStoreFactory( applicationContext );
  }

  public void reset( ApplicationContext applicationContext ) {
    applicationContext.getAdapterManager().deregisterAdapters();
    applicationContext.getBrandingManager().deregisterAll();
    applicationContext.getEntryPointManager().deregisterAll();
    applicationContext.getPhaseListenerRegistry().removeAll();
    applicationContext.getResourceRegistry().clear();
    applicationContext.getSettingStoreManager().deregisterFactory();
    resetConfiguration( applicationContext );
    resetApplicationStore( applicationContext );
  }

  private void resetApplicationStore( ApplicationContext applicationContext ) {
    IApplicationStore storeInstance = applicationContext.getApplicationStore();
    ApplicationStoreImpl applicationStore = ( ApplicationStoreImpl )storeInstance;
    applicationStore.reset();
    
  }

  private void resetConfiguration( ApplicationContext applicationContext ) {
    RWTConfiguration configurationInstance = applicationContext.getConfiguration();
    RWTConfigurationImpl configuration = ( RWTConfigurationImpl )configurationInstance;
    configuration.reset();
  }
  
  private void addInternalAdapterFactories( ApplicationContext applicationContext ) {
    LifeCycleAdapterFactory adapterFactory = new LifeCycleAdapterFactory();
    applicationContext.getAdapterManager().registerAdapters( Widget.class, adapterFactory );
    applicationContext.getAdapterManager().registerAdapters( Display.class, adapterFactory );
  }

  private void addInternalPhaseListeners( ApplicationContext applicationContext ) {
    applicationContext.getPhaseListenerRegistry().add( new CurrentPhase.Listener() );
    applicationContext.getPhaseListenerRegistry().add( new MeasurementListener() );
  }
  
  private void addInternalServiceHandlers( ApplicationContext applicationContext ) {
    ServiceManager serviceManager = applicationContext.getServiceManager();
    serviceManager.registerServiceHandler( ID_UI_CALL_BACK, new UICallBackServiceHandler() );
    serviceManager.registerServiceHandler( ID_JS_LIBRARIES, new JSLibraryServiceHandler() );
  }

  private void setInternalSettingStoreFactory( ApplicationContext applicationContext ) {
    if( !applicationContext.getSettingStoreManager().hasFactory() ) {
      applicationContext.getSettingStoreManager().register( new RWTFileSettingStoreFactory() );
    }
  }

  private ContextImpl createContext( ApplicationContext applicationContext ) {
    return new ContextImpl( applicationContext, configurator );
  }
}