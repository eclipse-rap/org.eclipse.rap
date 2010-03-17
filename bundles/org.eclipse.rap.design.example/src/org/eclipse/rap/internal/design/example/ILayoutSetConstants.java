/******************************************************************************* 
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.rap.internal.design.example;


public interface ILayoutSetConstants {
  /*
   *  Image paths
   */
  public static final String IMAGE_PATH_FANCY = "img/fancy/";
  public static final String IMAGE_PATH_BUSINESS = "img/business/";
  
  /*
   *  LayoutSet Ids
   */
  public static final String SET_ID_CONFIG_DIALOG 
    = "org.eclipse.rap.design.example.layoutset.confdialog";   
  public static final String SET_ID_COOLBAR 
    = "org.eclipse.rap.design.example.layoutset.coolbar";  
  public static final String SET_ID_OVERFLOW 
  = "org.eclipse.rap.design.example.layoutset.coolbaroverflow";
  public static final String SET_ID_FOOTER 
    = "org.eclipse.rap.design.example.layoutset.footer"; 
  public static final String SET_ID_HEADER 
    = "org.eclipse.rap.design.example.layoutset.header"; 
  public static final String SET_ID_LOGO 
    = "org.eclipse.rap.design.example.layoutset.logo";
  public static final String SET_ID_MENUBAR 
    = "org.eclipse.rap.design.example.layoutset.menubar";
  public static final String SET_ID_PERSP 
    = "org.eclipse.rap.design.example.layoutset.perspective";
  public static final String SET_ID_STACKPRESENTATION 
    = "org.eclipse.rap.design.example.layoutset.stack";  

  /*
   * LayoutSet content
   */
  
  // ConfigDialogInitializer
  public static final String CONFIG_WHITE = "CONFIG_WHITE";
  public static final String CONFIG_BLACK = "CONFIG_BLACK";
  public static final String CONFIG_DIALOG_CLOSE = "dialog.close";
  public static final String CONFIG_DIALOG_ICON = "dialog.conf.icon";
  
  // CoolbarInitializer
  public static final String COOLBAR_OVERFLOW_INACTIVE 
    = "coolbar.overflow.inactive";
  public static final String COOLBAR_OVERFLOW_ACTIVE 
    = "coolbar.overflow.active";
  public static final String COOLBAR_BUTTON_BG = "coolbar.button.bg";
  public static final String COOLBAR_OVERFLOW_COLOR = "coolbar.overflow.color";
  public static final String COOLBAR_ARROW = "coolbar.arrow";
  public static final String COOLBAR_BUTTON_POS = "coolbar.layer.button.pos";
  public static final String COOLBAR_SPACING = "colbar.layer.spacing";
  
  // CoolbarOverflowInitializer
  public static final String OVERFLOW_BG = "coolbar.layer.bg";
  public static final String OVERFLOW_RIGHT = "coolbar.layer.right";
  public static final String OVERFLOW_LEFT = "coolbar.layer.left";
  public static final String OVERFLOW_WAVE = "coolbar.layer.wave";
  public static final String OVERFLOW_ARROW = "coolbar.layer.arrow";
  public static final String OVERFLOW_POS = "coolbar.layer.pos";
    
  // FooterInitializer
  public static final String FOOTER_LEFT = "footer.left";
  public static final String FOOTER_BG = "footer";
  public static final String FOOTER_RIGHT = "footer.right";
  
  // HeaderInitializer
  public static final String HEADER_LEFT = "header.left";
  public static final String HEADER_LEFT_BG = "header.left.bg";
  public static final String HEADER_WAVE = "header.wave";
  public static final String HEADER_RIGHT_BG = "header.right.bg";
  public static final String HEADER_RIGHT = "header.right"; 
  
  // LogoInitializer
  public static final String LOGO = "header.logo";
  public static final String LOGO_POSITION = "header.logo.position";
  
  // MenuBarInitializer
  public static final String MENUBAR_ARROW = "menubar.arrow";
  public static final String MENUBAR_BG = "menubar.bg";
  
  // PerspectiveSwitcherInitializer
  public static final String PERSP_CLOSE = "perspective.close";
  public static final String PERSP_LEFT_ACTIVE = "perspective.left.active";
  public static final String PERSP_RIGHT_ACTIVE = "perspective.right.active";
  public static final String PERSP_BG = "perspective.bg";
  public static final String PERSP_BG_ACTIVE = "perspective.bg.active";
  public static final String PERSP_BUTTON_POS = "perspective.button.position";
  
  // StackInitializer
  public static final String STACK_BORDER_TOP = "stack.border.top";
  public static final String STACK_BORDER_BOTTOM = "stack.border.bottom";
  public static final String STACK_BORDER_LEFT = "stack.border.left";
  public static final String STACK_BORDER_RIGHT = "stack.border.right";
  public static final String STACK_BORDER_BOTTOM_ACTIVE 
    = "stack.border.bottom.active";
  public static final String STACK_BORDER_LEFT_ACTIVE 
    = "stack.border.left.active";
  public static final String STACK_BORDER_RIGHT_AVTIVE 
    = "stack.border.right.active";
  public static final String STACK_CONF_ACTIVE = "stack.conf.active";
  public static final String STACK_CONF_INACTIVE = "stack.conf.inactive";
  public static final String STACK_CONF_BG_ACTIVE = "stack.conf.bg.active";
  public static final String STACK_TAB_INACTIVE_BG_ACTIVE 
    = "stack.tab.inactive.bg.act";
  public static final String STACK_TAB_BG_ACTIVE = "stack.tab.bg.active";
  public static final String STACK_CONF_BG_INACTIVE = "stack.conf.bg.inactive";
  public static final String STACK_TAB_INACTIVE_RIGHT_ACTIVE 
    = "stack.tab.inactive.right.active";
  public static final String STACK_TAB_INACTIVE_SEPARATOR_ACTIVE 
    = "stack.tab.inactive.separator.active";
  public static final String STACK_TAB_OVERFLOW_ACTIVE 
    = "stack.tab.overflow.active";
  public static final String STACK_INACTIVE_CORNER = "stack.corner.inactive";
  public static final String STACK_TAB_INACTIVE_CORNER_ACTIVE 
    = "stack.inactive.corner.inactive";
  public static final String STACK_VIEW_TOOLBAR_BG = "stack.viewtoolbar.bg";
  public static final String STACK_VIEW_MENU_ICON = "stack.viewmenu.icon";
  public static final String STACK_VIEW_PULLDOWN = "stack.view.pulldown.arrow";
  public static final String STACK_TABBAR_RIGHT_ACTIVE 
    = "stack.tabbar.right.active";
  public static final String STACK_TABBAR_LEFT_ACTIVE 
  = "stack.tabbar.left.active";
  public static final String STACK_TABBAR_RIGHT_INACTIVE 
    = "stack.tabbar.right.inactive";
  public static final String STACK_TABBAR_LEFT_INACTIVE 
    = "stack.tabbar.left.inactive";
  public static final String STACK_CONF_POSITION = "stack.conf.position";
  public static final String STACK_OVERFLOW_POSITION = "stack.overflow.pos";
  public static final String STACK_BUTTON_ACTIVE = "stack.button.active";
  public static final String STACK_BUTTON_INACTIVE = "stack.button.inactive";
  public static final String STACK_TABBG_POS = "stack.tabbg.pos";
  public static final String STACK_BUTTON_TOP = "stack.button.top";
  public static final String STACK_CONF_POS = "stack.conf.pos";
  public static final String STACK_TOP_STANDALONE_ACTIVE 
    = "stack.top.standalone.active";
  public static final String STACK_TOP_STANDALONE_INACTIVE 
    = "stack.top.standalone.inactive";
 }
