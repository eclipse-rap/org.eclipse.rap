/******************************************************************************* 
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.rap.interactiondesign.tests.impl;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.rap.ui.interactiondesign.ConfigurableStack;
import org.eclipse.rap.ui.interactiondesign.ConfigurationAction;
import org.eclipse.rap.ui.interactiondesign.internal.ConfigurableStackProxy;
import org.eclipse.ui.internal.util.PrefUtil;
import org.eclipse.ui.preferences.ScopedPreferenceStore;


public class ConfigurationActionImpl extends ConfigurationAction {
  
  /**
   * Helper method to check if all view contrib items are visible by default.
   * @return true if all items are visible by default.
   */
  public static boolean allActionsVisible() {
    return visibility;
  }

  private static boolean visibility = false; 
  
  public void setGlobalVisibilityAttribute( final boolean visibility ) {
    ConfigurationActionImpl.visibility = visibility;
  }

  public ConfigurationActionImpl() {
  }
  
  public boolean isViewActionVisibile( final String viewId, 
                                       final String actionId )
  {
    boolean result = true;
    if( !allActionsVisible() ) {
      String identifier = getActionIdentifier( viewId, actionId );  
      ScopedPreferenceStore prefStore
        = ( ScopedPreferenceStore ) PrefUtil.getAPIPreferenceStore();
      result = prefStore.getBoolean( identifier );
    }
    return result;
  }  
  
  public boolean isPartMenuVisible() {
    boolean result = true;
    if( !allActionsVisible() ) {
      if( getStackPresentation() instanceof ConfigurableStack ) {
        ConfigurableStack configStack 
          = ( ConfigurableStack ) getStackPresentation();
        String paneId = configStack.getPaneId( getSite() );
        String identifier = getPartMenuIdentifier( paneId );
        result = loadPartmenuVisibility( identifier );
      }
    }
    return result;
  }
  
  private boolean loadPartmenuVisibility( final String identifier ) {
    boolean result = false;
    IPreferenceStore preferenceStore = PrefUtil.getAPIPreferenceStore();
    result = preferenceStore.getBoolean( identifier );
    return result;
  }
  
  private String getActionIdentifier( final String viewId,
                                      final String actionId )
  {
    return
        ConfigurableStackProxy.STACK_PRESENTATION_ID
      + "/"
      + viewId
      + "/" 
      + actionId;
  }

  private String getPartMenuIdentifier( final String paneId ) {
    return
        ConfigurableStackProxy.STACK_PRESENTATION_ID
      + "/"
      + paneId
      + "/partMenu";
  }
  
}
