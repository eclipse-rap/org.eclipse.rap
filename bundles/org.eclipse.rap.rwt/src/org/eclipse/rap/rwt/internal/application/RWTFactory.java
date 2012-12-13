/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.application;

import org.eclipse.rap.rwt.internal.lifecycle.EntryPointManager;
import org.eclipse.rap.rwt.internal.lifecycle.LifeCycleAdapterFactory;
import org.eclipse.rap.rwt.internal.lifecycle.LifeCycleFactory;
import org.eclipse.rap.rwt.internal.lifecycle.PhaseListenerRegistry;
import org.eclipse.rap.rwt.internal.resources.ResourceDirectory;
import org.eclipse.rap.rwt.internal.resources.ResourceRegistry;
import org.eclipse.rap.rwt.internal.service.ServiceManagerImpl;
import org.eclipse.rap.rwt.internal.service.SettingStoreManager;
import org.eclipse.rap.rwt.internal.service.StartupPage;
import org.eclipse.rap.rwt.internal.textsize.ProbeStore;
import org.eclipse.rap.rwt.internal.textsize.TextSizeStorage;
import org.eclipse.rap.rwt.internal.theme.ThemeManager;
import org.eclipse.rap.rwt.service.ApplicationContext;
import org.eclipse.rap.rwt.service.ResourceManager;
import org.eclipse.swt.internal.graphics.FontDataFactory;
import org.eclipse.swt.internal.graphics.ImageDataFactory;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.internal.graphics.InternalImageFactory;
import org.eclipse.swt.internal.graphics.ResourceFactory;
import org.eclipse.swt.internal.widgets.DisplaysHolder;


public class RWTFactory {

  public static ApplicationContext getApplicationContext() {
    return ApplicationContextUtil.getInstance();
  }

  public static LifeCycleFactory getLifeCycleFactory() {
    return ApplicationContextUtil.getInstance().getLifeCycleFactory();
  }

  public static EntryPointManager getEntryPointManager() {
    return ApplicationContextUtil.getInstance().getEntryPointManager();
  }

  public static ServiceManagerImpl getServiceManager() {
    return ApplicationContextUtil.getInstance().getServiceManager();
  }

  public static StartupPage getStartupPage() {
    return ApplicationContextUtil.getInstance().getStartupPage();
  }

  public static SettingStoreManager getSettingStoreManager() {
    return ApplicationContextUtil.getInstance().getSettingStoreManager();
  }

  public static ResourceDirectory getResourceDirectory() {
    return ApplicationContextUtil.getInstance().getResourceDirectory();
  }

  public static PhaseListenerRegistry getPhaseListenerRegistry() {
    return ApplicationContextUtil.getInstance().getPhaseListenerRegistry();
  }

  public static ThemeManager getThemeManager() {
    return ApplicationContextUtil.getInstance().getThemeManager();
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

  public static LifeCycleAdapterFactory getLifeCycleAdapterFactory() {
    return ApplicationContextUtil.getInstance().getLifeCycleAdapterFactory();
  }

  public static ResourceRegistry getResourceRegistry() {
    return ApplicationContextUtil.getInstance().getResourceRegistry();
  }

  public static ResourceManager getResourceManager() {
    return ApplicationContextUtil.getInstance().getResourceManager();
  }

  private RWTFactory() {
    // prevent instantiation
  }

}
