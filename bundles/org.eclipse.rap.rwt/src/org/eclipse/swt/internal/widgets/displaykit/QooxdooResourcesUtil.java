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

package org.eclipse.swt.internal.widgets.displaykit;

import java.io.File;
import java.util.Enumeration;
import java.util.zip.ZipFile;

import org.eclipse.swt.internal.engine.ResourceRegistry;
import org.eclipse.swt.resources.IResource;
import org.eclipse.swt.resources.ResourceManager;

import com.w4t.*;
import com.w4t.IResourceManager.RegisterOptions;
import com.w4t.engine.service.ContextProvider;
import com.w4t.engine.service.IServiceStateInfo;


// TODO [rh] Should javaScript namespaces include widget and/or custom?
//      e.g. org/eclipse/swt/widgets/TabUtil.js
final class QooxdooResourcesUtil {
  
  private static final String APPLICATION_JS 
    = "org/eclipse/swt/Application.js";
  private static final String REQUEST_JS 
    = "org/eclipse/swt/Request.js";
  private static final String WIDGET_MANAGER_JS 
    = "org/eclipse/swt/WidgetManager.js";
  private static final String EVENT_UTIL_JS 
    = "org/eclipse/swt/EventUtil.js";
  private static final String SASH_JS 
    = "org/eclipse/swt/Sash.js";
  private static final String TAB_UTIL_JS 
    = "org/eclipse/swt/TabUtil.js";
  private static final String BUTTON_UTIL_JS 
    = "org/eclipse/swt/ButtonUtil.js";
  private static final String COMBO_UTIL_JS 
    = "org/eclipse/swt/ComboUtil.js";
  private static final String TOOL_ITEM_JS = 
    "org/eclipse/swt/ToolItemUtil.js";
  private static final String MENU_UTIL_JS 
    = "org/eclipse/swt/MenuUtil.js";
  private static final String LINK_UTIL_JS
    = "org/eclipse/swt/LinkUtil.js";
  private static final String CTAB_FOLDER_JS 
    = "org/eclipse/swt/custom/CTabFolder.js";
  private static final String CTAB_ITEM_JS 
    = "org/eclipse/swt/custom/CTabItem.js";
  private static final String CLABEL_UTIL_JS 
  = "org/eclipse/swt/CLabelUtil.js";
  private static final String COOL_ITEM_JS 
    = "org/eclipse/swt/widgets/CoolItem.js";
  private static final String LIST_JS 
    = "org/eclipse/swt/widgets/List.js";
  private static final String SHELL_JS 
    = "org/eclipse/swt/widgets/Shell.js";
  private static final String TREE_JS 
    = "org/eclipse/swt/widgets/Tree.js";
  private static final String TREE_ITEM_JS 
    = "org/eclipse/swt/widgets/TreeItem.js";
  private static final String SCROLLED_COMPOSITE_JS 
    = "org/eclipse/swt/custom/ScrolledComposite.js";
  private static final String SEPARATOR_JS 
    = "org/eclipse/swt/widgets/Separator.js";
  private static final String LABEL_UTIL_JS
    = "org/eclipse/swt/LabelUtil.js";
  private static final String GROUP_JS
    = "org/eclipse/swt/widgets/Group.js";
  private static final String TEXT_UTIL_JS
    = "org/eclipse/swt/TextUtil.js";
  private static final String SPINNER_JS
    = "org/eclipse/swt/widgets/Spinner.js";
  private static final String TABLE_JS
    = "org/eclipse/swt/widgets/Table.js";
  private static final String TABLE_COLUMN_JS
    = "org/eclipse/swt/widgets/TableColumn.js";
  private static final String TABLE_ITEM_JS
    = "org/eclipse/swt/widgets/TableItem.js";  
  private static final String TABLE_ROW_JS
    = "org/eclipse/swt/widgets/TableRow.js";  
  private static final String EXTERNALBROWSER_UTIL_JS 
    = "org/eclipse/swt/externalbrowser/Util.js";
  private static final String PROGRESS_BAR_JS 
    = "org/eclipse/swt/widgets/ProgressBar.js";
  private static final String FONT_SIZE_CALCULATION_JS
    = "org/eclipse/swt/FontSizeCalculation.js";
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
      manager.register( "resource/static/html/blank.html" );
      manager.register( "resource/static/image/blank.gif" );
      manager.register( "resource/static/image/dotted_white.gif" );
      manager.register( "script/custom.js", HTML.CHARSET_NAME_ISO_8859_1, RegisterOptions.VERSION );
      responseWriter.useJSLibrary( "script/custom.js" );

      // end generated code
      ///////////////////////////////////////////////

      
      // TODO [rh] since qx 0.6.5 all constants seem to be 'inlined'
      //      these three files are here o keep DefaultAppearanceTheme.js
      //      happy that makes heavy use of constants 
      register( QX_CONSTANT_CORE_JS );
      register( QX_CONSTANT_LAYOUT_JS );
      register( QX_CONSTANT_STYLE_JS );

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
      register( TEXT_UTIL_JS );
      register( SPINNER_JS );
      register( TABLE_JS );
      register( TABLE_COLUMN_JS );
      register( TABLE_ITEM_JS );
      register( TABLE_ROW_JS );
      register( LINK_UTIL_JS );
      register( EXTERNALBROWSER_UTIL_JS );
      register( PROGRESS_BAR_JS );
      register( FONT_SIZE_CALCULATION_JS );
      register( CLABEL_UTIL_JS );
      
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
    File file = new File( projectDir + "/lib/qooxdoo-0.7.jar");
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
