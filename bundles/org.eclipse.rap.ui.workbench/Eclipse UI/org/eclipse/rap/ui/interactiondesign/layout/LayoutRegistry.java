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
   * id in the paramter and save the new id if necessary.
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
   * Returns the active <code>Layout</code>. If the active <code>Layout</code>
   * is not the default <code>Layout</code> then a hybrid <code>Layout</code>
   * is created. This contains the default <code>Layout</code> and the active
   * <code>Layout</code>. 
   * <p>
   * This is necessary because a active layout can override
   * single <code>LayoutSet</code>s of a <code>Layout</code> but their can also
   * be <code>LayoutSet</code>s, which the active <code>Layout</code> doesn't 
   * override.
   * </p>
   *  
   * @return the active layout containing default <code>LayoutSet</code>s if the
   * active <code>Layout</code> doesn't override all default <code>LayoutSet
   * </code>s.
   * 
   * @see LayoutSet
   * @see Layout
   */
  public Layout getActiveLayout() {
    Layout result = activeLayout;
    if( activeLayoutId.equals( DEFAULT_LAYOUT_ID ) ) {
      if( result == null ) {
        result =  ( Layout ) layoutMap.get( activeLayoutId );
        activeLayout = result;
      }      
    } else {
      result = createHybridLayout();
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
   * Additional it sets the active <code>Layout</code> to 
   * <code>{#DEFAULT_LAYOUT_ID}</code>.
   */
  private static void init() {
    layoutSetToPluginId = new HashMap();
    layoutMap = new HashMap();
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

  private static void createLayoutSets( final IConfigurationElement[] layoutSets, 
                                        final Layout layout ) 
  {
    if( layoutSets != null && layoutSets.length > 0 ) {
      for( int i = 0; i < layoutSets.length; i++ ) {
        IConfigurationElement layoutSetElement = layoutSets[ i ];

        String pluginId = layoutSetElement.getContributor().getName();
        String layoutSetId = layoutSetElement.getAttribute( "id" );

        layout.clearLayoutSet( layoutSetId );
        LayoutSet layoutSet = layout.getLayoutSet( layoutSetId );
        layoutSetToPluginId.put( layoutSetId, pluginId );

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
  }

  private void combineLayoutSets( final Layout layout,
                                  final Layout defaultLayout,
                                  final Layout activeLayout )
  {
    if( defaultLayout != null ) {
      Map defaultLayoutSets = defaultLayout.getLayoutSets();
      createLayoutSetFromLayout( layout, defaultLayoutSets );
    }
    Map activeLayoutSets = activeLayout.getLayoutSets();
    createLayoutSetFromLayout( layout, activeLayoutSets );
  }

  private Layout createHybridLayout() {
    // TODO [hs] think about cache for hybrid Layouts
    Layout result = new Layout( activeLayoutId );    
    Layout defaultLayout = ( Layout )layoutMap.get( DEFAULT_LAYOUT_ID );
    combineLayoutSets( result, defaultLayout, activeLayout );    
    return result;
  }
  
  private void createLayoutSetFromLayout( final Layout layout,
                                          final Map layoutSets )
  {
    Object[] keys = layoutSets.keySet().toArray();
    for( int i = 0; i < keys.length; i++ ) {
      String key = ( String )keys[ i ];
      LayoutSet set = ( LayoutSet )layoutSets.get( key );
      layout.clearLayoutSet( key );
      layout.addLayoutSet( set );
    }
  }
}
