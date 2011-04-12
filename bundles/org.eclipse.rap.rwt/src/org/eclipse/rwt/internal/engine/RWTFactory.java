/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.engine;

import org.eclipse.rwt.internal.ConfigurationReader;
import org.eclipse.rwt.internal.branding.BrandingManager;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.service.*;
import org.eclipse.rwt.internal.service.StartupPage.IStartupPageConfigurer;
import org.eclipse.rwt.internal.theme.ThemeAdapterManager;
import org.eclipse.rwt.internal.theme.ThemeManagerHolder;
import org.eclipse.rwt.service.IApplicationStore;
import org.eclipse.swt.internal.graphics.*;


public class RWTFactory {
  
  public static IApplicationStore getApplicationStore() {
    return ( IApplicationStore )ApplicationContext.getSingleton( ApplicationStoreImpl.class );
  }

  public static LifeCycleFactory getLifeCycleFactory() {
    return ( LifeCycleFactory )ApplicationContext.getSingleton( LifeCycleFactory.class );
  }
  
  public static BrandingManager getBrandingManager() {
    return ( BrandingManager )ApplicationContext.getSingleton( BrandingManager.class );
  }
  
  public static EntryPointManager getEntryPointManager() {
    return ( EntryPointManager )ApplicationContext.getSingleton( EntryPointManager.class );
  }

  public static ServiceManager getServiceManager() {
    Object singleton = ApplicationContext.getSingleton( ServiceManager.class );
    return ( ServiceManager )singleton;
  }

  public static StartupPage getStartupPage() {
    return ( StartupPage )ApplicationContext.getSingleton( StartupPage.class );
  }
  
  public static IStartupPageConfigurer getStartupPageConfigurer() {
    return ( IStartupPageConfigurer )ApplicationContext.getSingleton( StartupPageConfigurer.class );
  }

  public static SettingStoreManager getSettingStoreManager() {
    return ( SettingStoreManager )ApplicationContext.getSingleton( SettingStoreManager.class );
  }
  
  public static ConfigurationReader getConfigurationReader() {
    return ( ConfigurationReader )ApplicationContext.getSingleton( ConfigurationReader.class );
  }

  public static PhaseListenerRegistry getPhaseListenerRegistry() {
    return ( PhaseListenerRegistry )ApplicationContext.getSingleton( PhaseListenerRegistry.class );
  }
  
  public static ThemeManagerHolder getThemeManager() {
    return ( ThemeManagerHolder )ApplicationContext.getSingleton( ThemeManagerHolder.class );
  }

  public static ThemeAdapterManager getThemeAdapterManager() {
    return ( ThemeAdapterManager )ApplicationContext.getSingleton( ThemeAdapterManager.class );
  }

  public static TextSizeStorageRegistry getTextSizeStorageRegistry() {
    Object singleton = ApplicationContext.getSingleton( TextSizeStorageRegistry.class );
    return ( TextSizeStorageRegistry )singleton;
  }

  public static ImageFactory getImageFactory() {
    Object singleton = ApplicationContext.getSingleton( ImageFactory.class );
    return ( ImageFactory )singleton;
  }

  public static FontDataFactory getFontDataFactory() {
    return ( FontDataFactory )ApplicationContext.getSingleton( FontDataFactory.class );
  }

  public static ImageDataFactory getImageDataFactory() {
    return ( ImageDataFactory )ApplicationContext.getSingleton( ImageDataFactory.class );
  }

  public static InternalImageFactory getInternalImageFactory() {
    Object singleton = ApplicationContext.getSingleton( InternalImageFactory.class );
    return ( InternalImageFactory )singleton;
  }
  
  private RWTFactory() {
    // prevent instantiation
  }
}
