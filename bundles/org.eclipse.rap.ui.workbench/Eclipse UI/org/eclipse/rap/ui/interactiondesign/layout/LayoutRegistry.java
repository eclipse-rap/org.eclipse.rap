/******************************************************************************* 
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.rap.ui.interactiondesign.layout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.rap.ui.interactiondesign.layout.model.ILayoutSetInitializer;
import org.eclipse.rap.ui.interactiondesign.layout.model.Layout;
import org.eclipse.rap.ui.interactiondesign.layout.model.LayoutSet;
import org.eclipse.rwt.SessionSingletonBase;
import org.eclipse.ui.internal.util.PrefUtil;

/**
 * This class represents a singleton object of a registry for 
 * <code>{@link Layout}</code> objects. This will contain all <code>Layout
 * </code>s, which are contributed to the
 * <code>org.eclipse.rap.ui.layouts</code> extension point.
 *
 * @since 1.2
 */
public class LayoutRegistry {
  
  /**
   * This is the default <code>LayoutSet</code> id for the fallback mechanism.
   */
  public static final String DEFAULT_LAYOUT_ID 
    = "org.eclipse.rap.ui.defaultlayout";
  
  private static final String LAYOUT_EXT_ID 
    = "org.eclipse.rap.ui.layouts";
  
  /**
   * The key for saving key/value pairs of <code>LayoutSet</code>s.
   */
  public static final String SAVED_LAYOUT_KEY = LAYOUT_EXT_ID + ".saved";
  
  private static Map layoutMap;
  private static Map layoutSetToPluginId; 
  private static List overridenLayoutSets;
  
  static {
    init();
  }
  
  private String activeLayoutId;  
  private Layout activeLayout;
  private List builders;
  
  private LayoutRegistry() {
    activeLayoutId = DEFAULT_LAYOUT_ID;
    builders = new ArrayList();
  }   
  
  /**
   * Returns the singleton instance of the <code>LayoutRegistry</code> object.
   * 
   * @return the singleton instance.
   */
  public static LayoutRegistry getInstance() {
    Object result = SessionSingletonBase.getInstance( LayoutRegistry.class );
    return ( LayoutRegistry )result;
  }  
  
  /**
   * Saves the new <code>Layout</code> id in a 
   * <code>ScopedPreferenceStore</code>.
   * 
   * @param id the new <code>Layout</code> id to save.
   */
  public void saveLayoutId( final String id ) {
    IPreferenceStore preferenceStore = PrefUtil.getAPIPreferenceStore();
    preferenceStore.putValue( SAVED_LAYOUT_KEY, id );
    preferenceStore.firePropertyChangeEvent( SAVED_LAYOUT_KEY, "", id );
  }
  
  /**
   * Sets the active <code>Layout</code> to the one, which belongs to the 
   * id in the parameter and save the new id if necessary.
   * 
   * @param id the new <code>Layout</code> id.
   * @param save if <code>true</code> then the new <code>Layout</code> will be
   * saved.
   */
  public void setActiveLayout( final String id, final boolean save ) {
    Object object = layoutMap.get( id );
    Layout newActive = ( Layout ) object;
    if( newActive != null ) {
      activeLayoutId = id;
      activeLayout = newActive;
      if( save ) {
        saveLayoutId( activeLayoutId );
      }
    }
  }

  /**
   * This method will call the <code>{@link ElementBuilder#dispose()}</code>
   * for all registered builders.
   * 
   * @see ElementBuilder#dispose()
   */
  public void disposeBuilders() {
    for( int i = 0; i < builders.size(); i++ ) {
      ElementBuilder builder = ( ElementBuilder ) builders.get( i );
      builder.dispose();      
    }
  }
  
  /**
   * Returns the active <code>Layout</code>. 
   *  
   * @return the active layout 
   * @see Layout
   */
  public Layout getActiveLayout() {
    Layout result = activeLayout;
    if( result == null ) {
      result =  ( Layout ) layoutMap.get( activeLayoutId );
      activeLayout = result;
    }         
    return result;
  }

  /**
   * Returns an <code>IExtension</code> array, which contains all Layouts 
   * contributed to the <code>org.eclipse.rap.ui.layouts</code> 
   * extension point.
   * 
   * @return all <code>Layout</code>s as an <code>IExtension<code> array.
   */
  public static IConfigurationElement[] getLayoutExtensions() {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    return registry.getConfigurationElementsFor( LAYOUT_EXT_ID );
  }
  
  /**
   * Reads the saved <code>Layout</code> id from a <code>ScopedPreferenceStore
   * </code>.
   * 
   * @return the saved <code>Layout</code> id or 
   * <code>{@link IPreferenceStore#STRING_DEFAULT_DEFAULT}</code> if no id is 
   * saved.
   */
  public String getSavedLayoutId() {
    String result = IPreferenceStore.STRING_DEFAULT_DEFAULT;
    IPreferenceStore preferenceStore = PrefUtil.getAPIPreferenceStore();
    result = preferenceStore.getString( SAVED_LAYOUT_KEY );    
    return result;
  }

  /**
   * This method is called, if the active <code>Layout</code> has changed. It 
   * will call <code>{@link ElementBuilder#dispose()}</code> for all registered
   * builders.
   * 
   * @see ElementBuilder#dispose()
   */
  public void notifyLayoutChanged() {
    disposeBuilders();
  }
  
  
  static String getPluginIdForLayoutSet( final String layoutSetId ) {
    return ( String )layoutSetToPluginId.get( layoutSetId );
  }
  
  /**
   * Adds a <code>{@link ElementBuilder}</code> to a List of builders.
   * 
   * @param builder a instance of a <code>ElementBuilder</code>
   * 
   * @see ElementBuilder
   */
  void registerBuilder( final ElementBuilder builder ) {
    builders.add( builder );
  }

  /**
   * Initialize the <code>{@link LayoutSet}</code> contributed to the
   * <code>org.eclipse.rap.ui.layouts</code> extension point.
   */
  private static void init() {
    layoutSetToPluginId = new HashMap();
    layoutMap = new HashMap();
    overridenLayoutSets = new ArrayList();
    IConfigurationElement[] elements = getLayoutExtensions();
    for( int i = 0; i < elements.length; i++ ) {
      String id = elements[ i ].getAttribute( "id" );
      
      Layout layout = ( Layout ) layoutMap.get( id );
      IConfigurationElement[] layoutSets 
        = elements[ i ].getChildren( "layoutSet" );
      
      if( layout == null ) {        
        layout = initLayout( layoutSets, id );
        layoutMap.put( id, layout );
      } else {
        createLayoutSets( layoutSets, layout );
      }
    }
  }

  private static Layout initLayout( final IConfigurationElement[] elements,
                                    final String id )
  {
    Layout result = new Layout( id );
    createLayoutSets( elements, result );
    return result;
  }

  private static void createLayoutSets( 
    final IConfigurationElement[] layoutSets, 
    final Layout layout ) 
  {
    if( layoutSets != null && layoutSets.length > 0 ) {
      for( int i = 0; i < layoutSets.length; i++ ) {
        IConfigurationElement layoutSetElement = layoutSets[ i ];

        String pluginId = layoutSetElement.getContributor().getName();
        String layoutSetId = layoutSetElement.getAttribute( "id" );
        String overrides = layoutSetElement.getAttribute( "overridesId" );
        if( overrides != null ) {
          // clear the old layoutset if it exists and create it with the new 
          // content. Additional create a new layoutset with the new id if 
          // someone want to override it again.
          layout.clearLayoutSet( overrides );
          LayoutSet layoutSet = layout.getLayoutSet( overrides );
          layoutSetToPluginId.remove( overrides );
          layoutSetToPluginId.put( overrides, pluginId );
          initializeLayoutSet( layoutSetElement, layoutSet );
          LayoutSet overridingLayoutSet = layout.getLayoutSet( layoutSetId );
          layoutSetToPluginId.put( layoutSetId, pluginId );
          initializeLayoutSet( layoutSetElement, overridingLayoutSet );
          overridenLayoutSets.add( overrides );
        } else {          
          if( !overridenLayoutSets.contains( layoutSetId ) ) {
            // a new layoutset will only created if it's not already overridden.
            layout.clearLayoutSet( layoutSetId );
            LayoutSet layoutSet = layout.getLayoutSet( layoutSetId );
            layoutSetToPluginId.put( layoutSetId, pluginId );  
            initializeLayoutSet( layoutSetElement, layoutSet );
          }
        }
      }      
    }
  }

  private static void initializeLayoutSet( 
    final IConfigurationElement layoutSetElement,
    final LayoutSet layoutSet )
  {
    try {
      Object initializer 
        = layoutSetElement.createExecutableExtension( "class" );
      if( initializer instanceof ILayoutSetInitializer ) {
        ILayoutSetInitializer layoutInitializer 
          = ( ILayoutSetInitializer ) initializer;
        layoutInitializer.initializeLayoutSet( layoutSet );
      }
    } catch( CoreException e ) {
      e.printStackTrace();
    }
  }

}
