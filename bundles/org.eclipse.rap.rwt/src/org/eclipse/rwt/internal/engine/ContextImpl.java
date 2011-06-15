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
import org.eclipse.rwt.internal.service.ServiceManager;
import org.eclipse.rwt.internal.theme.*;
import org.eclipse.rwt.internal.theme.css.CssFileReader;
import org.eclipse.rwt.internal.theme.css.StyleSheet;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.rwt.lifecycle.PhaseListener;
import org.eclipse.rwt.resources.IResource;
import org.eclipse.rwt.service.IServiceHandler;
import org.eclipse.rwt.service.ISettingStoreFactory;
import org.eclipse.swt.widgets.Widget;

class ContextImpl implements Context {
  private final ApplicationContext applicationContext;
  private final Configurator configurator;

  static class ResourceLoaderImpl implements ResourceLoader {
    private final ClassLoader loader;
    
    private ResourceLoaderImpl( ClassLoader loader ) {
      this.loader = loader;
      
    }
    public InputStream getResourceAsStream( String resourceName ) throws IOException {
      return loader.getResourceAsStream( resourceName );
    }
  }
  
  ContextImpl( ApplicationContext applicationContext, Configurator configurator ) {
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
  
  public void setAttribute( String name, Object value ) {
    applicationContext.getApplicationStore().setAttribute( name, value );
  }

  private StyleSheet readStyleSheet( String styleSheetLocation ) {
    ClassLoader classLoader = configurator.getClass().getClassLoader();
    ResourceLoaderImpl loader = new ResourceLoaderImpl( classLoader );
    return readStyleSheet( styleSheetLocation, loader );
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