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

import org.eclipse.rwt.internal.AdapterManager;
import org.eclipse.rwt.internal.ConfigurationReader;
import org.eclipse.rwt.internal.branding.BrandingManager;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.resources.*;
import org.eclipse.rwt.internal.service.*;
import org.eclipse.rwt.internal.service.StartupPage.IStartupPageConfigurer;
import org.eclipse.rwt.internal.textsize.ProbeStore;
import org.eclipse.rwt.internal.textsize.TextSizeStorage;
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
    return ApplicationContextUtil.getInstance().getLifeCycleFactory();
  }
  
  public static BrandingManager getBrandingManager() {
    return ApplicationContextUtil.getInstance().getBrandingManager();
  }
  
  public static EntryPointManager getEntryPointManager() {
    return ApplicationContextUtil.getInstance().getEntryPointManager();
  }

  public static ServiceManager getServiceManager() {
    return ApplicationContextUtil.getInstance().getServiceManager();
  }

  public static StartupPage getStartupPage() {
    return ( StartupPage )getApplicationSingleton( StartupPage.class );
  }
  
  public static IStartupPageConfigurer getStartupPageConfigurer() {
    return ( IStartupPageConfigurer )getApplicationSingleton( StartupPageConfigurer.class );
  }

  public static SettingStoreManager getSettingStoreManager() {
    return ApplicationContextUtil.getInstance().getSettingStoreManager();
  }
  
  public static ConfigurationReader getConfigurationReader() {
    return ApplicationContextUtil.getInstance().getConfigurationReader();
  }

  public static PhaseListenerRegistry getPhaseListenerRegistry() {
    return ApplicationContextUtil.getInstance().getPhaseListenerRegistry();
  }
  
  public static ThemeManagerHolder getThemeManager() {
    return ApplicationContextUtil.getInstance().getThemeManager();
  }

  public static ThemeAdapterManager getThemeAdapterManager() {
    return ( ThemeAdapterManager )getApplicationSingleton( ThemeAdapterManager.class );
  }

  public static TextSizeStorage getTextSizeStorage() {
    return ( TextSizeStorage )getApplicationSingleton( TextSizeStorage.class );
  }

  public static ProbeStore getProbeStore() {
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
    return ApplicationContextUtil.getInstance().getJSLibraryConcatenator();
  }

  public static AdapterManager getAdapterManager() {
    return ApplicationContextUtil.getInstance().getAdapterManager();
  }

  public static ResourceRegistry getResourceRegistry() {
    return ApplicationContextUtil.getInstance().getResourceRegistry();
  }

  public static ResourceManagerProvider getResourceManagerProvider() {
    return ApplicationContextUtil.getInstance().getResourceManagerProvider();
  }
  
  private static Object getApplicationSingleton( Class type ) {
    return ApplicationContextUtil.getInstance().getInstance( type );
  }
  
  private RWTFactory() {
    // prevent instantiation
  }
}
