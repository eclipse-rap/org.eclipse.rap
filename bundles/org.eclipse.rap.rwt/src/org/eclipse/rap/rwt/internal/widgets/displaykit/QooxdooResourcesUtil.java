/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.rwt.internal.widgets.displaykit;

import java.io.File;
import java.util.Enumeration;
import java.util.zip.ZipFile;
import org.eclipse.rap.rwt.internal.engine.ResourceRegistry;
import org.eclipse.rap.rwt.resources.IResource;
import org.eclipse.rap.rwt.resources.ResourceManager;
import com.w4t.*;
import com.w4t.IResourceManager.RegisterOptions;
import com.w4t.engine.service.ContextProvider;
import com.w4t.engine.service.IServiceStateInfo;


// TODO [rh] Should javaScript namespaces include widget and/or custom?
//      e.g. org/eclipse/rap/rwt/widgets/TabUtil.js
final class QooxdooResourcesUtil {
  
  private static final String SHELL_CAPTION_INACTIVE_GIF 
    = "org/eclipse/rap/rwt/widgets/shell/caption_inactive.gif";
  private static final String SHELL_CAPTION_ACTIVE_GIF 
    = "org/eclipse/rap/rwt/widgets/shell/caption_active.gif";
  private static final String DISPLAY_BG_GIF 
    = "org/eclipse/rap/rwt/widgets/display/bg.gif";
  private static final String TREE_FOLDER_CLOSED_GIF 
    = "org/eclipse/rap/rwt/widgets/tree/folder_closed.gif";
  private static final String TREE_FOLDER_OPEN_GIF 
    = "org/eclipse/rap/rwt/widgets/tree/folder_open.gif";
  
  private static final String APPEARANCE_JS
    = "org/eclipse/rap/rwt/DefaultAppearanceTheme.js";
  private static final String APPLICATION_JS 
    = "org/eclipse/rap/rwt/Application.js";
  private static final String REQUEST_JS 
    = "org/eclipse/rap/rwt/Request.js";
  private static final String WIDGET_MANAGER_JS 
    = "org/eclipse/rap/rwt/WidgetManager.js";
  private static final String EVENT_UTIL_JS 
    = "org/eclipse/rap/rwt/EventUtil.js";
  private static final String SASH_JS 
    = "org/eclipse/rap/rwt/Sash.js";
  private static final String TAB_UTIL_JS 
    = "org/eclipse/rap/rwt/TabUtil.js";
  private static final String BUTTON_UTIL_JS 
    = "org/eclipse/rap/rwt/ButtonUtil.js";
  private static final String COMBO_UTIL_JS 
    = "org/eclipse/rap/rwt/ComboUtil.js";
  private static final String TOOL_ITEM_JS = 
    "org/eclipse/rap/rwt/ToolItemUtil.js";
  private static final String MENU_UTIL_JS 
    = "org/eclipse/rap/rwt/MenuUtil.js";
  private static final String LINK_UTIL_JS
    = "org/eclipse/rap/rwt/LinkUtil.js";
  private static final String CTAB_FOLDER_JS 
    = "org/eclipse/rap/rwt/custom/CTabFolder.js";
  private static final String CTAB_ITEM_JS 
    = "org/eclipse/rap/rwt/custom/CTabItem.js";
  private static final String COOL_ITEM_JS 
    = "org/eclipse/rap/rwt/widgets/CoolItem.js";
  private static final String LIST_JS 
    = "org/eclipse/rap/rwt/widgets/List.js";
  private static final String SHELL_JS 
    = "org/eclipse/rap/rwt/widgets/Shell.js";
  private static final String TREE_JS 
    = "org/eclipse/rap/rwt/widgets/Tree.js";
  private static final String TREE_ITEM_JS 
    = "org/eclipse/rap/rwt/widgets/TreeItem.js";
  private static final String SCROLLED_COMPOSITE_JS 
    = "org/eclipse/rap/rwt/custom/ScrolledComposite.js";
  private static final String SEPARATOR_JS 
    = "org/eclipse/rap/rwt/widgets/Separator.js";
  private static final String LABEL_UTIL_JS
    = "org/eclipse/rap/rwt/LabelUtil.js";
  private static final String GROUP_JS
    = "org/eclipse/rap/rwt/widgets/Group.js";
  private static final String WIDGET_THEME_JS
    = "org/eclipse/rap/rwt/WidgetTheme.js";
  private static final String TEXT_UTIL_JS
    = "org/eclipse/rap/rwt/TextUtil.js";
  private static final String SPINNER_JS
    = "org/eclipse/rap/rwt/widgets/Spinner.js";
  private static final String TABLE_JS
    = "org/eclipse/rap/rwt/widgets/Table.js";
  private static final String TABLE_COLUMN_JS
    = "org/eclipse/rap/rwt/widgets/TableColumn.js";
  private static final String TABLE_ITEM_JS
    = "org/eclipse/rap/rwt/widgets/TableItem.js";  
  private static final String QX_CONSTANT_CORE_JS
    = "qx/constant/Core.js";
  private static final String QX_CONSTANT_LAYOUT_JS
    = "qx/constant/Layout.js";
  private static final String QX_CONSTANT_STYLE_JS
    = "qx/constant/Style.js";

  private QooxdooResourcesUtil() {
    // prevent intance creation
  }
  
  public static void registerResources() {
    ClassLoader loader = QooxdooResourcesUtil.class.getClassLoader();
    IResourceManager manager = ResourceManager.getInstance();
    ClassLoader bufferedLoader = manager.getContextLoader();
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    // TODO [rh] really need responseWriter? registerResources is called
    //      from ctor of DisplayLCA...
    HtmlResponseWriter responseWriter = stateInfo.getResponseWriter();
    try {
      manager.setContextLoader( loader );
      

      ///////////////////////////////////////////////
      // start generated code (see main method)

      manager.register( "resource/static/history/historyHelper.html", HTML.CHARSET_NAME_ISO_8859_1 );
      manager.register( "resource/static/image/blank.gif" );
      manager.register( "resource/static/image/dotted_white.gif" );
      manager.register( "script/custom.js", HTML.CHARSET_NAME_ISO_8859_1, RegisterOptions.VERSION );
      responseWriter.useJSLibrary( "script/custom.js" );


      // end generated code
      ///////////////////////////////////////////////

      registerWidgetTheme( manager );
      
      manager.register( TREE_FOLDER_OPEN_GIF );
      manager.register( TREE_FOLDER_CLOSED_GIF );
      manager.register( DISPLAY_BG_GIF );
      manager.register( SHELL_CAPTION_ACTIVE_GIF );
      manager.register( SHELL_CAPTION_INACTIVE_GIF );
      // TODO [rh] since qx 0.6.5 all constants seem to be 'inlined'
      //      these three files are here o keep DefaultAppearanceTheme.js
      //      happy that makes heavy use of constants 
      register( QX_CONSTANT_CORE_JS );
      register( QX_CONSTANT_LAYOUT_JS );
      register( QX_CONSTANT_STYLE_JS );

      register( APPEARANCE_JS );
      register( APPLICATION_JS );
      register( REQUEST_JS );
      register( WIDGET_MANAGER_JS );
      register( EVENT_UTIL_JS );
      register( SASH_JS );
      register( TAB_UTIL_JS );
      register( COMBO_UTIL_JS );
      register( BUTTON_UTIL_JS );
      register( TOOL_ITEM_JS );
      register( MENU_UTIL_JS );
      register( CTAB_ITEM_JS );
      register( CTAB_FOLDER_JS );
      register( COOL_ITEM_JS );
      register( LIST_JS );
      register( SHELL_JS );
      register( TREE_JS );
      register( TREE_ITEM_JS );
      register( SCROLLED_COMPOSITE_JS );
      register( SEPARATOR_JS );
      register( LABEL_UTIL_JS );
      register( GROUP_JS );
      register( WIDGET_THEME_JS );
      register( TEXT_UTIL_JS );
      register( SPINNER_JS );
      register( TABLE_JS );
      register( TABLE_COLUMN_JS );
      register( TABLE_ITEM_JS );
      register( LINK_UTIL_JS );
      
      // register contributions
      registerContributions();
    } finally {
      manager.setContextLoader( bufferedLoader );
    }
  }

  private static void registerContributions() {
    IResourceManager manager = ResourceManager.getInstance();
    ClassLoader contextLoader = manager.getContextLoader();
    try {
      IResource[] resources = ResourceRegistry.get();
      for( int i = 0; i < resources.length; i++ ) {
        if( !resources[ i ].isExternal() ) {
          manager.setContextLoader( resources[ i ].getLoader() );
          String charset = resources[ i ].getCharset();
          RegisterOptions options = resources[ i ].getOptions();
          String location = resources[ i ].getLocation();
          if( charset == null && options == null ) {
            manager.register( location );
          } else if( options == null ) {
            manager.register( location, charset );
          } else {
            manager.register( location, charset, options );
          }
          if( resources[ i ].isJSLibrary() ) {
            IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
            HtmlResponseWriter responseWriter = stateInfo.getResponseWriter();
            responseWriter.useJSLibrary( location );
          }
        }
      }
    } finally {
      manager.setContextLoader( contextLoader );
    }
  }

  private static void registerWidgetTheme( final IResourceManager manager ) {
    manager.register( "resource/widget/rap/arrows/down.gif" );
    manager.register( "resource/widget/rap/arrows/down_small.gif" );
    manager.register( "resource/widget/rap/arrows/down_tiny.gif" );
    manager.register( "resource/widget/rap/arrows/first.png" );
    manager.register( "resource/widget/rap/arrows/forward.gif" );
    manager.register( "resource/widget/rap/arrows/last.png" );
    manager.register( "resource/widget/rap/arrows/left.png" );
    manager.register( "resource/widget/rap/arrows/minimize.gif" );
    manager.register( "resource/widget/rap/arrows/next.gif" );
    manager.register( "resource/widget/rap/arrows/previous.gif" );
    manager.register( "resource/widget/rap/arrows/rewind.gif" );
    manager.register( "resource/widget/rap/arrows/right.png" );
    manager.register( "resource/widget/rap/arrows/up.gif" );
    manager.register( "resource/widget/rap/arrows/up_small.gif" );
    manager.register( "resource/widget/rap/arrows/up_tiny.gif" );
    manager.register( "resource/widget/rap/colorselector/brightness-field.jpg" );
    manager.register( "resource/widget/rap/colorselector/brightness-handle.gif" );
    manager.register( "resource/widget/rap/colorselector/huesaturation-field.jpg" );
    manager.register( "resource/widget/rap/colorselector/huesaturation-handle.gif" );
    manager.register( "resource/widget/rap/cursors/alias.gif" );
    manager.register( "resource/widget/rap/cursors/copy.gif" );
    manager.register( "resource/widget/rap/cursors/move.gif" );
    manager.register( "resource/widget/rap/cursors/nodrop.gif" );
    manager.register( "resource/widget/rap/datechooser/lastMonth.png" );
    manager.register( "resource/widget/rap/datechooser/lastYear.png" );
    manager.register( "resource/widget/rap/datechooser/nextMonth.png" );
    manager.register( "resource/widget/rap/datechooser/nextYear.png" );
    manager.register( "resource/widget/rap/menu/checkbox.gif" );
    manager.register( "resource/widget/rap/menu/menu-blank.gif" );
    manager.register( "resource/widget/rap/menu/radiobutton.gif" );
    manager.register( "resource/widget/rap/splitpane/knob-horizontal.png" );
    manager.register( "resource/widget/rap/splitpane/knob-vertical.png" );
    manager.register( "resource/widget/rap/table/ascending.png" );
    manager.register( "resource/widget/rap/table/boolean-false.png" );
    manager.register( "resource/widget/rap/table/boolean-true.png" );
    manager.register( "resource/widget/rap/table/descending.png" );
    manager.register( "resource/widget/rap/table/selectColumnOrder.png" );
    manager.register( "resource/widget/rap/tree/cross.gif" );
    manager.register( "resource/widget/rap/tree/cross_minus.gif" );
    manager.register( "resource/widget/rap/tree/cross_plus.gif" );
    manager.register( "resource/widget/rap/tree/end.gif" );
    manager.register( "resource/widget/rap/tree/end_minus.gif" );
    manager.register( "resource/widget/rap/tree/end_plus.gif" );
    manager.register( "resource/widget/rap/tree/line.gif" );
    manager.register( "resource/widget/rap/tree/minus.gif" );
    manager.register( "resource/widget/rap/tree/only_minus.gif" );
    manager.register( "resource/widget/rap/tree/only_plus.gif" );
    manager.register( "resource/widget/rap/tree/plus.gif" );
    manager.register( "resource/widget/rap/tree/start_minus.gif" );
    manager.register( "resource/widget/rap/tree/start_plus.gif" );
    manager.register( "resource/widget/rap/window/close.gif" );
    manager.register( "resource/widget/rap/window/maximize.gif" );
    manager.register( "resource/widget/rap/window/minimize.gif" );
    manager.register( "resource/widget/rap/window/restore.gif" );
  }
  
  private static void register( final String libraryName ) {
    IResourceManager manager = ResourceManager.getInstance();
    manager.register( libraryName, 
                      HTML.CHARSET_NAME_ISO_8859_1,
                      RegisterOptions.VERSION );
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    HtmlResponseWriter responseWriter = stateInfo.getResponseWriter();
    responseWriter.useJSLibrary( libraryName );
  }
  
  /** 
   * This is a utility method to create the image registration code.
   */
  public static void main( final String[] arx ) throws Exception {
    String projectDir = System.getProperty( "user.dir" );
    File file = new File( projectDir + "/lib/qooxdoo-0.6.5.jar");
    ZipFile archive = new ZipFile( file );
    Enumeration entries = archive.entries();
    System.out.println( file );
    while( entries.hasMoreElements() ) {
      String entry = entries.nextElement().toString();
      if( !entry.endsWith( "/" ) ) {
        if( entry.endsWith( ".html" ) ) {
          System.out.println(   "manager.register( \"" 
                              + entry 
                              + "\", HTML.CHARSET_NAME_ISO_8859_1 );" );
        } else if( entry.endsWith( ".js" ) ) {
          System.out.println(   "manager.register( \"" 
                              + entry
                              + "\", HTML.CHARSET_NAME_ISO_8859_1, "
                              + "RegisterOptions.VERSION );" );
          System.out.println(   "responseWriter.useJSLibrary( \"" 
                              + entry
                              + "\" );" );
        } else {
          System.out.println( "manager.register( \"" + entry + "\" );" );
        }
      }
    }
  }
}
