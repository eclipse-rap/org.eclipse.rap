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
package org.eclipse.rwt.application;

import org.eclipse.rwt.AdapterFactory;
import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.rwt.lifecycle.PhaseListener;
import org.eclipse.rwt.resources.IResource;
import org.eclipse.rwt.resources.ResourceLoader;
import org.eclipse.rwt.service.IServiceHandler;
import org.eclipse.rwt.service.ISettingStoreFactory;
import org.eclipse.swt.widgets.Widget;


public interface ApplicationConfiguration {

  enum LifeCycleMode {
    THREADED,
    THREADLESS
  }

  void addEntryPoint( String entryPointName, Class<? extends IEntryPoint> entryPointType );

  void setLifeCycleMode( LifeCycleMode lifeCycleMode );

  void addPhaseListener( PhaseListener phaseListener );

  void setSettingStoreFactory( ISettingStoreFactory settingStoreFactory );

  void addAdapterFactory( Class<?> adaptable, AdapterFactory adapterFactory );

  void addResource( IResource resource );

  void addServiceHandler( String serviceHandlerId, IServiceHandler serviceHandler );

  void addBranding( AbstractBranding branding );

  void addTheme( String themeId, String styleSheetLocation );

  void addTheme( String themeId, String styleSheetLocation, ResourceLoader resourceLoader );

  void addThemableWidget( Class<? extends Widget> widget );

  void addThemableWidget( Class<? extends Widget> widget, ResourceLoader resourceLoader );

  void addThemeContribution( String themeId, String styleSheetLocation );

  void addThemeContribution( String themeId, String styleSheetLocation, ResourceLoader loader );
  
  void setAttribute( String name, Object value );
}