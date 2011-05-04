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

import org.eclipse.rwt.internal.*;
import org.eclipse.rwt.internal.branding.BrandingManager;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.resources.*;
import org.eclipse.rwt.internal.service.*;
import org.eclipse.rwt.internal.service.StartupPage.IStartupPageConfigurer;
import org.eclipse.rwt.internal.textsize.ProbeStore;
import org.eclipse.rwt.internal.textsize.TextSizeStorageRegistry;
import org.eclipse.rwt.internal.theme.ThemeAdapterManager;
import org.eclipse.rwt.internal.theme.ThemeManagerHolder;
import org.eclipse.rwt.service.IApplicationStore;
import org.eclipse.swt.internal.graphics.*;
import org.eclipse.swt.internal.widgets.DisplaysHolder;


public class RWTFactory {
  
  public static IApplicationStore getApplicationStore() {
    return ( IApplicationStore )getApplicationSingleton( ApplicationStoreImpl.class );
  }

  public static LifeCycleFactory getLifeCycleFactory() {
    return ( LifeCycleFactory )getApplicationSingleton( LifeCycleFactory.class );
  }
  
  public static BrandingManager getBrandingManager() {
    return ( BrandingManager )getApplicationSingleton( BrandingManager.class );
  }
  
  public static EntryPointManager getEntryPointManager() {
    return ( EntryPointManager )getApplicationSingleton( EntryPointManager.class );
  }

  public static ServiceManager getServiceManager() {
    Object singleton = getApplicationSingleton( ServiceManager.class );
    return ( ServiceManager )singleton;
  }

  public static StartupPage getStartupPage() {
    return ( StartupPage )getApplicationSingleton( StartupPage.class );
  }
  
  public static IStartupPageConfigurer getStartupPageConfigurer() {
    return ( IStartupPageConfigurer )getApplicationSingleton( StartupPageConfigurer.class );
  }

  public static SettingStoreManager getSettingStoreManager() {
    return ( SettingStoreManager )getApplicationSingleton( SettingStoreManager.class );
  }
  
  public static ConfigurationReader getConfigurationReader() {
    return ( ConfigurationReader )getApplicationSingleton( ConfigurationReader.class );
  }

  public static PhaseListenerRegistry getPhaseListenerRegistry() {
    return ( PhaseListenerRegistry )getApplicationSingleton( PhaseListenerRegistry.class );
  }
  
  public static ThemeManagerHolder getThemeManager() {
    return ( ThemeManagerHolder )getApplicationSingleton( ThemeManagerHolder.class );
  }

  public static ThemeAdapterManager getThemeAdapterManager() {
    return ( ThemeAdapterManager )getApplicationSingleton( ThemeAdapterManager.class );
  }

  public static TextSizeStorageRegistry getTextSizeStorageRegistry() {
    return ( TextSizeStorageRegistry )getApplicationSingleton( TextSizeStorageRegistry.class );
  }

  public static ProbeStore getTextSizeProbeStore() {
    return ( ProbeStore )getApplicationSingleton( ProbeStore.class );
  }
  
  public static ImageFactory getImageFactory() {
    return ( ImageFactory )getApplicationSingleton( ImageFactory.class );
  }

  public static FontDataFactory getFontDataFactory() {
    return ( FontDataFactory )getApplicationSingleton( FontDataFactory.class );
  }

  public static ImageDataFactory getImageDataFactory() {
    return ( ImageDataFactory )getApplicationSingleton( ImageDataFactory.class );
  }

  public static ResourceFactory getResourceFactory() {
    return ( ResourceFactory )getApplicationSingleton( ResourceFactory.class );
  }

  public static InternalImageFactory getInternalImageFactory() {
    return ( InternalImageFactory )getApplicationSingleton( InternalImageFactory.class );
  }
  
  public static DisplaysHolder getDisplaysHolder() {
    return ( DisplaysHolder )getApplicationSingleton( DisplaysHolder.class );
  }

  public static JSLibraryConcatenator getJSLibraryConcatenator() {
    return ( JSLibraryConcatenator )getApplicationSingleton( JSLibraryConcatenator.class );
  }

  public static AdapterManager getAdapterManager() {
    return ( AdapterManager )getApplicationSingleton( AdapterManager.class );
  }

  public static ResourceRegistry getResourceRegistry() {
    return ( ResourceRegistry )getApplicationSingleton( ResourceRegistry.class );
  }

  public static ResourceManagerProvider getResourceManagerProvider() {
    return ( ResourceManagerProvider )getApplicationSingleton( ResourceManagerProvider.class );
  }
  
  private static Object getApplicationSingleton( Class type ) {
    return ApplicationContextUtil.getInstance().getInstance( type );
  }
  
  private RWTFactory() {
    // prevent instantiation
  }
}
