/******************************************************************************* 
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.rap.internal.design.example.business.layoutsets;

import org.eclipse.rap.ui.interactiondesign.layout.model.ILayoutSetInitializer;
import org.eclipse.rap.ui.interactiondesign.layout.model.LayoutSet;


public class StackInitializer implements ILayoutSetInitializer {

  public static final String SET_ID 
    = "org.eclipse.rap.design.example.business.layoutset.stack";
  public static final String BORDER_TOP = "stack.border.top";
  public static final String BORDER_BOTTOM = "stack.border.bottom";
  public static final String BORDER_LEFT = "stack.border.left";
  public static final String BORDER_RIGHT = "stack.border.right";
  public static final String MAX_ACTIVE = "stack.max.active";
  public static final String MAX_INACTIVE = "stack.max.inactive";
  public static final String MIN_ACTIVE = "stack.min.active";
  public static final String MIN_INACTIVE = "stack.min.inactive";
  public static final String SEPARATOR_ACTIVE = "stack.conf.sep.active";
  public static final String SEPARATOR_INACTIVE = "stack.conf.sep.inactive";
  public static final String TAB_ACTIVE_BG_ACTIVE = "stack.tab.active.bg.act";
  public static final String TAB_ACTIVE_CLOSE_ACTIVE 
    = "stack.tab.active.close.active";
  public static final String TAB_ACTIVE_RIGHT_ACTIVE 
    = "stack.tab.active.right.active";
  public static final String CONF_ACTIVE = "stack.conf.active";
  public static final String CONF_INACTIVE = "stack.conf.inactive";
  public static final String CONF_BG_ACTIVE = "stack.conf.bg.active";
  public static final String TAB_INACTIVE_BG_ACTIVE 
    = "stack.tab.inactive.bg.act";
  public static final String CONF_BG_INACTIVE = "stack.conf.bg.inactive";
  public static final String TAB_INACTIVE_RIGHT_ACTIVE 
    = "stack.tab.inactive.right.active";
  public static final String TAB_INACTIVE_SEPARATOR_ACTIVE 
    = "stack.tab.inactive.separator.active";
  public static final String TAB_OVERFLOW_ACTIVE = "stack.tab.overflow.active";
  public static final String INACTIVE_CORNER = "stack.corner.inactive";
  public static final String TAB_INACTIVE_CORNER_ACTIVE 
    = "stack.inactive.corner.inactive";
  public static final String TAB_INACTIVE_CLOSE_ACTIVE 
    = "stack.inactive.close.active";
  public static final String VIEW_TOOLBAR_BG = "stack.viewtoolbar.bg";
  public static final String VIEW_TOOLBAR_LAYER = "stack.viewtoolbar.layer";
  public static final String VIEW_MENU_ICON = "stack.viewmenu.icon";
  public static final String VIEW_PULLDOWN = "stack.view.pulldown.arrow";
  

  public void initializeLayoutSet( LayoutSet layoutSet ) {
    String path = ILayoutSetConstants.IMAGE_PATH;
    layoutSet.addImagePath( MAX_ACTIVE, 
                            path + "stack_confarea_max_active.png" );
    layoutSet.addImagePath( MAX_INACTIVE, 
                            path + "stack_confarea_max_inactive.png" );
    layoutSet.addImagePath( MIN_ACTIVE, 
                            path + "stack_confarea_min_active.png" );
    layoutSet.addImagePath( MIN_INACTIVE, 
                            path + "stack_confarea_min_inactive.png" );
    layoutSet.addImagePath( SEPARATOR_ACTIVE, 
                            path + "stack_confarea_separator_active.png" );
    layoutSet.addImagePath( SEPARATOR_INACTIVE, 
                            path + "stack_confarea_separator_inactive.png" );
    layoutSet.addImagePath( TAB_ACTIVE_BG_ACTIVE, 
                            path + "stack_tab_active_bg_active.png" );
    layoutSet.addImagePath( TAB_ACTIVE_CLOSE_ACTIVE, 
                            path + "stack_tab_active_close_active.png" );
    layoutSet.addImagePath( TAB_ACTIVE_RIGHT_ACTIVE, 
                            path + "stack_tab_active_right_active.png" );
    layoutSet.addImagePath( CONF_ACTIVE, path + "stack_tab_conf_active.png" );
    layoutSet.addImagePath( CONF_INACTIVE, 
                            path + "stack_tab_conf_inactive.png" );
    layoutSet.addImagePath( CONF_BG_ACTIVE, 
                            path + "stack_tab_active_confarea_bg.png" );
    layoutSet.addImagePath( TAB_INACTIVE_BG_ACTIVE, 
                            path + "stack_tab_inactive_bg_active.png" );
    layoutSet.addImagePath( CONF_BG_INACTIVE, 
                            path + "stack_tab_inactive_confarea_bg.png" );
    layoutSet.addImagePath( TAB_INACTIVE_RIGHT_ACTIVE, 
                            path + "stack_tab_inactive_right_active.png" );
    layoutSet.addImagePath( TAB_INACTIVE_SEPARATOR_ACTIVE, 
                            path + "stack_tab_inactive_separator_active.png" );
    layoutSet.addImagePath( TAB_OVERFLOW_ACTIVE, 
                            path + "stack_tab_overflow_active.png" );
    layoutSet.addImagePath( BORDER_LEFT, path + "stack_border_left.png" );
    layoutSet.addImagePath( BORDER_RIGHT, path + "stack_border_right.png" );
    layoutSet.addImagePath( BORDER_TOP, path + "stack_border_top.png" );
    layoutSet.addImagePath( BORDER_BOTTOM, path + "stack_border_bottom.png" );
    layoutSet.addImagePath( INACTIVE_CORNER, 
                            path + "stack_inactive_corner.png" );
    layoutSet.addImagePath( TAB_INACTIVE_CORNER_ACTIVE, 
                            path + "stack_inactive_corner_active.png" );
    layoutSet.addImagePath( TAB_INACTIVE_CLOSE_ACTIVE, 
                            path + "stack_tab_inactive_close_active.png" );
    layoutSet.addImagePath( VIEW_TOOLBAR_BG, path + "viewtoolbar_bg.png" );
    layoutSet.addImagePath( VIEW_TOOLBAR_LAYER, path + "viewToolBarLayer.gif" );
    layoutSet.addImagePath( VIEW_MENU_ICON, path + "viewMenu.png" );
    layoutSet.addImagePath( VIEW_PULLDOWN, path + "viewPulldown.png" );
  }
}
