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
import org.eclipse.rap.rwt.resources.ResourceManager;
import com.w4t.*;
import com.w4t.IResourceManager.RegisterOptions;
import com.w4t.engine.service.ContextProvider;
import com.w4t.engine.service.IServiceStateInfo;


// TODO [rh] Should javaScript namespaces include widget and/or custom?
//      e.g. org/eclipse/rap/rwt/widgets/TabUtil.js
final class QooxdooResourcesUtil {
  
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
  private static final String TAB_FOLDER_UTIL_JS 
    = "org/eclipse/rap/rwt/TabFolderUtil.js";
  private static final String TOOLBAR_UTIL_JS 
  = "org/eclipse/rap/rwt/ToolBarUtil.js";
  private static final String BUTTON_UTIL_JS 
    = "org/eclipse/rap/rwt/ButtonUtil.js";
  private static final String COMBO_UTIL_JS 
    = "org/eclipse/rap/rwt/ComboUtil.js";
  private static final String TOOL_ITEM_JS = 
    "org/eclipse/rap/rwt/ToolItemUtil.js";
  private static final String MENU_UTIL_JS 
    = "org/eclipse/rap/rwt/MenuUtil.js";
  private static final String TABLE_UTIL_JS
    = "org/eclipse/rap/rwt/TableUtil.js";
  private static final String TABLE_MODEL_JS
    = "org/eclipse/rap/rwt/UnsortableTableModel.js";
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
  private static final String SCROLLED_COMPOSITE_JS 
    = "org/eclipse/rap/rwt/custom/ScrolledComposite.js";
  private static final String SEPARATOR_JS 
    = "org/eclipse/rap/rwt/widgets/Separator.js";
  private static final String LABEL_UTIL_JS
    = "org/eclipse/rap/rwt/LabelUtil.js";
  private static final String GROUP_JS
    = "org/eclipse/rap/rwt/widgets/Group.js";
  
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
      manager.register( "resource/widget/windows/arrows/down.gif" );
      manager.register( "resource/widget/windows/arrows/down_small.gif" );
      manager.register( "resource/widget/windows/arrows/down_tiny.gif" );
      manager.register( "resource/widget/windows/arrows/first.png" );
      manager.register( "resource/widget/windows/arrows/forward.gif" );
      manager.register( "resource/widget/windows/arrows/last.png" );
      manager.register( "resource/widget/windows/arrows/left.png" );
      manager.register( "resource/widget/windows/arrows/minimize.gif" );
      manager.register( "resource/widget/windows/arrows/next.gif" );
      manager.register( "resource/widget/windows/arrows/previous.gif" );
      manager.register( "resource/widget/windows/arrows/rewind.gif" );
      manager.register( "resource/widget/windows/arrows/right.png" );
      manager.register( "resource/widget/windows/arrows/Thumbs.db" );
      manager.register( "resource/widget/windows/arrows/up.gif" );
      manager.register( "resource/widget/windows/arrows/up_small.gif" );
      manager.register( "resource/widget/windows/arrows/up_tiny.gif" );
      manager.register( "resource/widget/windows/colorselector/brightness-field.jpg" );
      manager.register( "resource/widget/windows/colorselector/brightness-handle.gif" );
      manager.register( "resource/widget/windows/colorselector/huesaturation-field.jpg" );
      manager.register( "resource/widget/windows/colorselector/huesaturation-handle.gif" );
      manager.register( "resource/widget/windows/cursors/alias.gif" );
      manager.register( "resource/widget/windows/cursors/copy.gif" );
      manager.register( "resource/widget/windows/cursors/move.gif" );
      manager.register( "resource/widget/windows/cursors/nodrop.gif" );
      manager.register( "resource/widget/windows/datechooser/lastMonth.png" );
      manager.register( "resource/widget/windows/datechooser/lastYear.png" );
      manager.register( "resource/widget/windows/datechooser/nextMonth.png" );
      manager.register( "resource/widget/windows/datechooser/nextYear.png" );
      manager.register( "resource/widget/windows/menu/checkbox.gif" );
      manager.register( "resource/widget/windows/menu/menu-blank.gif" );
      manager.register( "resource/widget/windows/menu/radiobutton.gif" );
      manager.register( "resource/widget/windows/splitpane/knob-horizontal.png" );
      manager.register( "resource/widget/windows/splitpane/knob-vertical.png" );
      manager.register( "resource/widget/windows/table/ascending.png" );
      manager.register( "resource/widget/windows/table/boolean-false.png" );
      manager.register( "resource/widget/windows/table/boolean-true.png" );
      manager.register( "resource/widget/windows/table/descending.png" );
      manager.register( "resource/widget/windows/table/selectColumnOrder.png" );
      manager.register( "resource/widget/windows/tree/cross.gif" );
      manager.register( "resource/widget/windows/tree/cross_minus.gif" );
      manager.register( "resource/widget/windows/tree/cross_plus.gif" );
      manager.register( "resource/widget/windows/tree/end.gif" );
      manager.register( "resource/widget/windows/tree/end_minus.gif" );
      manager.register( "resource/widget/windows/tree/end_plus.gif" );
      manager.register( "resource/widget/windows/tree/line.gif" );
      manager.register( "resource/widget/windows/tree/minus.gif" );
      manager.register( "resource/widget/windows/tree/only_minus.gif" );
      manager.register( "resource/widget/windows/tree/only_plus.gif" );
      manager.register( "resource/widget/windows/tree/plus.gif" );
      manager.register( "resource/widget/windows/tree/start_minus.gif" );
      manager.register( "resource/widget/windows/tree/start_plus.gif" );
      manager.register( "resource/widget/windows/window/close.gif" );
      manager.register( "resource/widget/windows/window/maximize.gif" );
      manager.register( "resource/widget/windows/window/minimize.gif" );
      manager.register( "resource/widget/windows/window/restore.gif" );
      manager.register( "script/custom.js", HTML.CHARSET_NAME_ISO_8859_1, RegisterOptions.VERSION );
      responseWriter.useJSLibrary( "script/custom.js" );


      // end generated code
      ///////////////////////////////////////////////
      
      manager.register( "org/eclipse/rap/rwt/widgets/tree/folder_open.gif" );
      manager.register( "org/eclipse/rap/rwt/widgets/tree/folder_closed.gif" );
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
      register( TAB_FOLDER_UTIL_JS );
      register( TOOLBAR_UTIL_JS );
      register( COMBO_UTIL_JS );
      register( BUTTON_UTIL_JS );
      register( TOOL_ITEM_JS );
      register( MENU_UTIL_JS );
      register( TABLE_UTIL_JS );
      register( TABLE_MODEL_JS );
      register( CTAB_ITEM_JS );
      register( CTAB_FOLDER_JS );
      register( COOL_ITEM_JS );
      register( LIST_JS );
      register( SHELL_JS );
      register( TREE_JS );
      register( SCROLLED_COMPOSITE_JS );
      register( SEPARATOR_JS );
      register( LABEL_UTIL_JS );
      register( GROUP_JS );
    } finally {
      manager.setContextLoader( bufferedLoader );
    }
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
