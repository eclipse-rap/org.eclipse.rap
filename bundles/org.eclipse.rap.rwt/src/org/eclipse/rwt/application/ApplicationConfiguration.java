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

import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.rwt.lifecycle.PhaseListener;
import org.eclipse.rwt.resources.IResource;
import org.eclipse.rwt.resources.ResourceLoader;
import org.eclipse.rwt.service.IServiceHandler;
import org.eclipse.rwt.service.ISettingStoreFactory;
import org.eclipse.swt.widgets.Widget;

/**
 * <strong>Note:</strong> This API is <em>provisional</em>. It is likely to change before the final
 * release.
 *
 * @since 1.5
 */
public interface ApplicationConfiguration {
  
  public enum OperationMode {
    STANDARD,
    JEE_COMPATIBILITY,
    SESSION_FAILOVER
  }

  void setOperationMode( OperationMode operationMode );
  
  void addEntryPoint( String entryPointName, Class<? extends IEntryPoint> entryPointType );

  void addBranding( AbstractBranding branding );

  void addStyleSheet( String themeId, String styleSheetLocation );
  
  void addStyleSheet( String themeId, String styleSheetLocation, ResourceLoader resourceLoader );
  
  void addPhaseListener( PhaseListener phaseListener );
  
  void setAttribute( String name, Object value );

  void setSettingStoreFactory( ISettingStoreFactory settingStoreFactory );

  void addThemableWidget( Class<? extends Widget> widget );
  
  void addServiceHandler( String serviceHandlerId, IServiceHandler serviceHandler );
  
  /////////////////////////////////////////////
  // TODO [fappel]: replace with proper mechanism (Javascript)
  void addResource( IResource resource );
}