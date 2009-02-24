/******************************************************************************* 
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.rap.ui.interactiondesign;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.rap.ui.interactiondesign.internal.ConfigurableStackProxy;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.internal.LayoutPart;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.internal.presentations.PresentablePart;
import org.eclipse.ui.internal.util.PrefUtil;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.ui.presentations.IPresentablePart;
import org.eclipse.ui.presentations.IStackPresentationSite;
import org.eclipse.ui.presentations.StackPresentation;

/**
 * This represents an object to configure different part properties e.g. 
 * toolitem visibility, viewmenu visibility or the 
 * <code>{@link ConfigurableStack}</code> for a selected part.
 * <p>
 * This is a ordinary <code>{@link Action}</code>. The 
 * <code>{@link #run()}</code> method can implement any configuration behaviour
 * e.g. a PupupDialog.
 * </p>
 * <p>
 * Additional this class provides methods for saving and laoding some 
 * presentation specific properties.
 * </p>
 * 
 * @since 1.2
 * @see Action
 * @see ConfigurableStack
 */
public abstract class ConfigurationAction extends Action {
  
  /**
   * Extracts the id of an <code>{@link ActionContributionItem}</code> from a
   * <code>{@link ToolItem}</code>.
   * @param item the <code>ToolItem</code>, which holds a 
   * <code>ActionContributionItem</code>.
   * 
   * @return the extracted id of an 
   */
  public static final String getActionIdFromToolItem( final ToolItem item ) {
    String result = "";
    Object data = item.getData();
    ActionContributionItem toolBarAction = ( ActionContributionItem ) data;
    result = toolBarAction.getId();
    return result;
  }
  private IStackPresentationSite site;
  
  private StackPresentation stackPresentation;
  
  private List configurationChangeListeners = new ArrayList();
  
  /**
   * Method to add a <code>{@link IConfigurationChangeListener}.
   * @param listener an instance of a <code>IConfigurationChangedListener
   * </code>.
   * 
   * @see IConfigurationChangeListener
   */
  public void addConfigurationChangeListener( 
    final IConfigurationChangeListener listener ) 
  {
    configurationChangeListeners.add( listener );    
  }
  
  /**
   * This method is called if the <code>{@link ConfigurableStack}</code> of a
   * part has changed. It just calls the 
   * <code>{@link IConfigurationChangeListener#presentationChanged(String)}
   * </code> method for all listeners added by 
   * <code>{@link #addConfigurationChangeListener(IConfigurationChangeListener)}
   * </code>.
   * 
   * @param newId the new <code>ConfigurableStack</code> id to make active.
   * 
   * @see ConfigurableStack
   * @see IConfigurationChangeListener
   */
  public void fireLayoutChange( final String newId ) {
    for( int i = 0; i < configurationChangeListeners.size(); i++ ) {
      IConfigurationChangeListener listener 
        = ( IConfigurationChangeListener ) configurationChangeListeners.get( i );
      listener.presentationChanged( newId );
      
    }
  }
  
  /**
   * This method can be called if the configuration of the view's toolbar has 
   * been changed. It calls 
   * <code>{@link IConfigurationChangeListener#toolBarChanged()}</code> for all
   * listeners registered by 
   * <code>{@link #addConfigurationChangeListener(IConfigurationChangeListener)}
   * </code>.
   * 
   * @see IConfigurationChangeListener
   */
  public void fireToolBarChange() {
    for( int i = 0; i < configurationChangeListeners.size(); i++ ) {
      IConfigurationChangeListener listener 
        = ( IConfigurationChangeListener ) configurationChangeListeners.get( i );
      listener.toolBarChanged();
      
    }
  }
  
  private String getActionIdentifier( 
    final String viewId, final String actionId )
  {
    return ConfigurableStackProxy.STACK_PRESENTATION_ID + "/"
      + viewId + "/" + actionId;
  }
  
  private String getPartMenuIdentifier( final String paneId ) {
    return ConfigurableStackProxy.STACK_PRESENTATION_ID + "/"
    + paneId + "/partMenu";
  }
  
  /**
   * This method returns the <code>IStackPresentationSite</code> from the 
   * <code>StackPresentation</code>, which belongs to this 
   * <code>ConfigurationAction</code>.
   * @return the <code>IStackPresentationSite</code> to communicate with the 
   * part.
   */
  public IStackPresentationSite getSite() {
    return site;
  }
  
  /**
   * Return the <code>{@link StackPresentation}</code>, which this action 
   * belongs to.
   * 
   * @return the <code>StackPresentation</code> object.
   * 
   * @see ConfigurableStack
   */
  public StackPresentation getStackPresentation() {
    return stackPresentation;
  }
  
  /**
   * Checks if the selected part has a menu or not.
   * 
   * @return <code>true</code> if the selected part has a menu.
   */
  public boolean hasPartMenu() {
    boolean result = false;
    IPresentablePart selectedPart = site.getSelectedPart();
    if( selectedPart instanceof PresentablePart ) {
      PresentablePart part = ( PresentablePart ) selectedPart;
      result = part.getPane().hasViewMenu();
    }    
    return result;
  }
  
  /**
   * This method is called right after the object is instantiated to set 
   * different fields. These fields are necessary to get the needed 
   * information about the current <code>{@link WorkbenchWindow}</code> state 
   * e.g. the selected part or the current <code>{@link ConfigurableStack}
   * </code>.
   * 
   * @param site the site used for communication between the presentation and
   * the workbench.
   * @param stackPresentation the current <code>{@link StackPresentation}</code>
   * or <code>ConfigurableStack</code> to get the part's toolbar and so on.
   * 
   * @see ConfigurableStack
   * @see IStackPresentationSite
   * @see StackPresentation
   */
  public void init( 
    final IStackPresentationSite site, 
    final StackPresentation stackPresentation ) 
  {
    this.site = site;
    this.stackPresentation = stackPresentation;
  }
  
  /** 
   * Check if the part menu is set visible by the user. 
   * 
   * @return the visibility of the part menu.
   */
  public boolean isPartMenuVisible() {
    boolean result = false;
    if( stackPresentation instanceof ConfigurableStack ) {
      ConfigurableStack configStack = ( ConfigurableStack ) stackPresentation;
      String paneId = configStack.getPaneId( site );
      String identifier = getPartMenuIdentifier( paneId );
      result = loadPartmenuVisibility( identifier );
    }
    return result;
  }
    
  private boolean loadPartmenuVisibility( final String identifier ) {
    boolean result = false;
    IPreferenceStore preferenceStore = PrefUtil.getAPIPreferenceStore();
    result = preferenceStore.getBoolean( identifier );
    return result;    
  }

  /**
   * Returns the visibility for an <code>{@link ActionContributionItem}</code>, 
   * which is contributed to a view's toolbar. By default all <code>
   * ActionContributionItem</code>s are not visible. The visibility is stored
   * in a <code>ScopedPreferenceStore</code>.
   * 
   * @param viewId the id of the view that holds the 
   * <code>ActionContributionItem</code>. 
   * @param actionId the unique id of the <code>{@link Action}</code> holding by 
   * an <code>ActionContributionItem</code>.
   * 
   * @return the visibility of the <code>ActionContributionItem</code>. If no 
   * value is stored, the default value is <code>false</code>.
   * 
   * @see ActionContributionItem
   * @see Action
   * @see ScopedPreferenceStore
   */
  public boolean isViewActionVisibile( 
    final String viewId, final String actionId )
  {
    boolean result = false;
    String identifier = getActionIdentifier( viewId, actionId );
    
    ScopedPreferenceStore prefStore 
      = ( ScopedPreferenceStore ) PrefUtil.getAPIPreferenceStore();
    result = prefStore.getBoolean( identifier );
    
    return result;
  }

  /**
   * Removes a <code>{@link IConfigurationChangeListener}</code> from this 
   * action.
   * 
   * @param listener the <code>IConfigurationChangeListener</code> to remove.
   */
  public void removeLayoutChangeListener( 
    final IConfigurationChangeListener listener ) 
  {
    boolean found = false;
    for( int i = 0; !found && i < configurationChangeListeners.size(); i++ ) {
      IConfigurationChangeListener current 
        = ( IConfigurationChangeListener ) configurationChangeListeners.get( i );
      if( current.equals( listener ) ) {
        configurationChangeListeners.remove( i );
        found = true;
      }
    }
  }
  
  /**
   * Saves the visibility of a part's menu in a 
   * <code>{@link ScopedPreferenceStore}</code>.
   * 
   * @param visible the new visibility to save.
   * 
   * @see ScopedPreferenceStore
   */
  public void savePartMenuVisibility( final boolean visible ) {
    IPreferenceStore preferenceStore = PrefUtil.getAPIPreferenceStore();
    if( stackPresentation instanceof ConfigurableStack ) {
      ConfigurableStack configStack = ( ConfigurableStack ) stackPresentation;
      String paneId = configStack.getPaneId( site );
      String identifier = getPartMenuIdentifier( paneId );
      preferenceStore.setValue( identifier, visible );
    }    
  }

  /**
   * Saves the <code>{@link ConfigurableStack}</code> id for the selected 
   * <code>{@link LayoutPart}</code>. 
   * 
   * @param id the unique id of the <code>ConfigurableStack</code> to save.
   * 
   * @see LayoutPart
   * @see ConfigurableStack
   */
  public void saveStackPresentationId( final String id ) {
    if( stackPresentation instanceof ConfigurableStack ) {
      String layoutPartId = ConfigurableStack.getLayoutPartId( site );
      if( site != null && layoutPartId != null ) {
      
        ScopedPreferenceStore prefStore 
          = ( ScopedPreferenceStore ) PrefUtil.getAPIPreferenceStore();
        prefStore.setValue( ConfigurableStackProxy.STACK_PRESENTATION_ID + "/"
                            + layoutPartId,   
                            id );           
      }  
    }
  }
  
  /**
   * Saves the visibility of a view action in a 
   * <code>{@link ScopedPreferenceStore}</code>.
   * 
   * @param viewId the id of the view, which holds the view's 
   * <code>{@link ActionContributionItem}</code>
   * @param actionId the id of the <code>ActionContributionItem</code>, which 
   * the new visibility belongs to.
   * @param visibility the value of the visibility.
   */
  public void saveViewActionVisibility( 
    final String viewId, final String actionId, boolean visibility )
  {
    String identifier = getActionIdentifier( viewId, actionId );
    
    ScopedPreferenceStore prefStore 
    = ( ScopedPreferenceStore ) PrefUtil.getAPIPreferenceStore();
    
    prefStore.setValue( identifier, visibility );
    
  }
   
  
}
