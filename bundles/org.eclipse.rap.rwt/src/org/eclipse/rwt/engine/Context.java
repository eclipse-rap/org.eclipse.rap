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
package org.eclipse.rwt.engine;

import org.eclipse.rwt.AdapterFactory;
import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.rwt.lifecycle.PhaseListener;
import org.eclipse.rwt.resources.IResource;
import org.eclipse.rwt.service.IServiceHandler;
import org.eclipse.rwt.service.ISettingStoreFactory;
import org.eclipse.swt.widgets.Widget;


public interface Context {

  void addEntryPoint( String entryPointName, Class<? extends IEntryPoint> entryPointType );

  void addPhaseListener( PhaseListener phaseListener );

  void setSettingStoreFactory( ISettingStoreFactory settingStoreFactory );

  void addAddapterFactory( Class<?> adaptable, AdapterFactory adapterFactory );

  void addResource( IResource resource );

  void addServiceHandler( String serviceHandlerId, IServiceHandler serviceHandler );

  void addBranding( AbstractBranding branding );

  void addTheme( String themeId, String styleSheetLocation );

  void addThemableWidget( Class<? extends Widget> widget );

  void addThemeContribution( String themeId, String styleSheetLocation );

  void setAttribute( String name, Object value );
}