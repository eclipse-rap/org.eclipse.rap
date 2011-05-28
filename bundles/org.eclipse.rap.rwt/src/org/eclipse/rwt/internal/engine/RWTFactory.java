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
import org.eclipse.rwt.internal.textsize.ProbeStore;
import org.eclipse.rwt.internal.textsize.TextSizeStorage;
import org.eclipse.rwt.internal.theme.ThemeAdapterManager;
import org.eclipse.rwt.internal.theme.ThemeManager;
import org.eclipse.rwt.service.IApplicationStore;
import org.eclipse.swt.internal.graphics.*;
import org.eclipse.swt.internal.widgets.DisplaysHolder;


public class RWTFactory {
  
  public static IApplicationStore getApplicationStore() {
    return ApplicationContextUtil.getInstance().getApplicationStore();
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
    return ApplicationContextUtil.getInstance().getStartupPage();
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
  
  public static ThemeManager getThemeManager() {
    return ApplicationContextUtil.getInstance().getThemeManager();
  }

  public static ThemeAdapterManager getThemeAdapterManager() {
    return ApplicationContextUtil.getInstance().getThemeAdapterManager();
  }

  public static TextSizeStorage getTextSizeStorage() {
    return ApplicationContextUtil.getInstance().getTextSizeStorage();
  }

  public static ProbeStore getProbeStore() {
    return ApplicationContextUtil.getInstance().getProbeStore();
  }
  
  public static ImageFactory getImageFactory() {
    return ApplicationContextUtil.getInstance().getImageFactory();
  }

  public static FontDataFactory getFontDataFactory() {
    return ApplicationContextUtil.getInstance().getFontDataFactory();
  }

  public static ImageDataFactory getImageDataFactory() {
    return ApplicationContextUtil.getInstance().getImageDataFactory();
  }

  public static ResourceFactory getResourceFactory() {
    return ApplicationContextUtil.getInstance().getResourceFactory();
  }

  public static InternalImageFactory getInternalImageFactory() {
    return ApplicationContextUtil.getInstance().getInternalImageFactory();
  }
  
  public static DisplaysHolder getDisplaysHolder() {
    return ApplicationContextUtil.getInstance().getDisplaysHolder();
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
  
  private RWTFactory() {
    // prevent instantiation
  }
}