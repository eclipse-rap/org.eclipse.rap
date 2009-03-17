/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.displaykit;

import org.eclipse.rwt.internal.lifecycle.HtmlResponseWriter;
import org.eclipse.rwt.internal.resources.ResourceManager;
import org.eclipse.rwt.internal.resources.ResourceRegistry;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.IServiceStateInfo;
import org.eclipse.rwt.internal.util.HTML;
import org.eclipse.rwt.resources.IResource;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.rwt.resources.IResourceManager.RegisterOptions;



// TODO [rh] Should javaScript namespaces include widget and/or custom?
//      e.g. org/eclipse/swt/widgets/TabUtil.js
final class QooxdooResourcesUtil {


  private static final String CLIENT_LIBRARY_VARIANT
    = "org.eclipse.rwt.clientLibraryVariant";
  private static final String DEBUG_CLIENT_LIBRARY_VARIANT = "DEBUG";

  private static final String QX_JS
    = "qx.js";
  private static final String QX_DEBUG_JS
    = "qx-debug.js";

  private static final String KEY_EVENT_HANDLER_PATCH
    = "org/eclipse/rwt/KeyEventHandlerPatch.js";
  private static final String APPLICATION_JS
    = "org/eclipse/swt/Application.js";
  private static final String REQUEST_JS
    = "org/eclipse/swt/Request.js";
  private static final String WIDGET_MANAGER_JS
    = "org/eclipse/swt/WidgetManager.js";
  private static final String EVENT_UTIL_JS
    = "org/eclipse/swt/EventUtil.js";
  private static final String ASYNC_KEY_EVENT_UTIL_JS
    = "org/eclipse/rwt/AsyncKeyEventUtil.js";
  private static final String SYNC_KEY_EVENT_UTIL_JS
    = "org/eclipse/rwt/SyncKeyEventUtil.js";
  private static final String TAB_UTIL_JS
    = "org/eclipse/swt/TabUtil.js";
  private static final String BUTTON_UTIL_JS
    = "org/eclipse/swt/ButtonUtil.js";
  private static final String COMBO_UTIL_JS
    = "org/eclipse/swt/ComboUtil.js";
  private static final String TOOL_ITEM_JS
    = "org/eclipse/swt/ToolItemUtil.js";
  private static final String MENU_UTIL_JS
    = "org/eclipse/swt/MenuUtil.js";
  private static final String LINK_UTIL_JS
    = "org/eclipse/swt/LinkUtil.js";
  private static final String WIDGET_UTIL_JS
    = "org/eclipse/swt/WidgetUtil.js";
  private static final String CTAB_FOLDER_JS
    = "org/eclipse/swt/custom/CTabFolder.js";
  private static final String CTAB_ITEM_JS
    = "org/eclipse/swt/custom/CTabItem.js";
  private static final String CLABEL_UTIL_JS
    = "org/eclipse/swt/CLabelUtil.js";
  private static final String SASH_JS
    = "org/eclipse/swt/widgets/Sash.js";
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
  private static final String TREE_ITEM_UTIL_JS
    = "org/eclipse/swt/TreeItemUtil.js";
  private static final String TREE_COLUMN_JS
    = "org/eclipse/swt/widgets/TreeColumn.js";
  private static final String SCROLLED_COMPOSITE_JS
    = "org/eclipse/swt/custom/ScrolledComposite.js";
  private static final String SEPARATOR_JS
    = "org/eclipse/swt/widgets/Separator.js";
  private static final String LABEL_UTIL_JS
    = "org/eclipse/swt/LabelUtil.js";
  private static final String COMBO_JS
    = "org/eclipse/swt/widgets/Combo.js";
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
  private static final String EXTERNALBROWSER_JS
    = "org/eclipse/rwt/widgets/ExternalBrowser.js";
  private static final String PROGRESS_BAR_JS
    = "org/eclipse/swt/widgets/ProgressBar.js";
  private static final String BROWSER_JS
    = "org/eclipse/swt/browser/Browser.js";
  private static final String FONT_SIZE_CALCULATION_JS
    = "org/eclipse/swt/FontSizeCalculation.js";
  private static final String QX_CONSTANT_CORE_JS
    = "qx/constant/Core.js";
  private static final String QX_CONSTANT_LAYOUT_JS
    = "qx/constant/Layout.js";
  private static final String QX_CONSTANT_STYLE_JS
    = "qx/constant/Style.js";
  private static final String SCALE_JS
    = "org/eclipse/swt/widgets/Scale.js";
  private static final String DATE_TIME_DATE_JS
    = "org/eclipse/swt/widgets/DateTimeDate.js";
  private static final String DATE_TIME_TIME_JS
    = "org/eclipse/swt/widgets/DateTimeTime.js";
  private static final String DATE_TIME_CALENDAR_JS
    = "org/eclipse/swt/widgets/DateTimeCalendar.js";
  private static final String CALENDAR_JS
    = "org/eclipse/swt/widgets/Calendar.js";
  private static final String EXPAND_BAR_JS
    = "org/eclipse/swt/widgets/ExpandBar.js";
  private static final String EXPAND_ITEM_JS
    = "org/eclipse/swt/widgets/ExpandItem.js";
  private static final String SLIDER_JS
    = "org/eclipse/swt/widgets/Slider.js";
  private static final String CHECKBOX_JS
    = "org/eclipse/swt/widgets/CheckBox.js";
  private static final String RADIOBUTTON_JS
    = "org/eclipse/swt/widgets/RadioButton.js";
  private static final String CCOMBO_JS
    = "org/eclipse/swt/custom/CCombo.js";

  private QooxdooResourcesUtil() {
    // prevent intance creation
  }

  public static void registerResources() {
    ClassLoader loader = QooxdooResourcesUtil.class.getClassLoader();
    IResourceManager manager = ResourceManager.getInstance();
    ClassLoader bufferedLoader = manager.getContextLoader();
    manager.setContextLoader( loader );
    try {
      manager.register( "resource/static/history/historyHelper.html",
                        HTML.CHARSET_NAME_ISO_8859_1 );
      manager.register( "resource/static/html/blank.html",
                        HTML.CHARSET_NAME_ISO_8859_1 );
      manager.register( "resource/static/image/blank.gif" );
      manager.register( "resource/static/image/dotted_white.gif" );
      String libraryVariant = System.getProperty( CLIENT_LIBRARY_VARIANT );
      boolean isDebug = DEBUG_CLIENT_LIBRARY_VARIANT.equals( libraryVariant );
      if( isDebug ) {
        register( QX_DEBUG_JS, false );
      } else {
        register( QX_JS, false );
      }
      boolean compress = !isDebug;
      // TODO [rh] since qx 0.6.5 all constants seem to be 'inlined'
      //      these three files are here to keep DefaultAppearanceTheme.js
      //      happy that makes heavy use of constants
      register( QX_CONSTANT_CORE_JS, compress );
      register( QX_CONSTANT_LAYOUT_JS, compress );
      register( QX_CONSTANT_STYLE_JS, compress );

      register( KEY_EVENT_HANDLER_PATCH, compress );
      register( APPLICATION_JS, compress );
      register( REQUEST_JS, compress );
      register( WIDGET_MANAGER_JS, compress );
      register( EVENT_UTIL_JS, compress );
      register( ASYNC_KEY_EVENT_UTIL_JS, compress );
      register( SYNC_KEY_EVENT_UTIL_JS, compress );
      register( WIDGET_UTIL_JS, compress );
      register( SASH_JS, compress );
      register( TAB_UTIL_JS, compress );
      register( COMBO_UTIL_JS, compress );
      register( BUTTON_UTIL_JS, compress );
      register( TOOL_ITEM_JS, compress );
      register( MENU_UTIL_JS, compress );
      register( CTAB_ITEM_JS, compress );
      register( CTAB_FOLDER_JS, compress );
      register( COOL_ITEM_JS, compress );
      register( LIST_JS, compress );
      register( SHELL_JS, compress );
      register( TREE_JS, compress );
      register( TREE_ITEM_JS, compress );
      register( TREE_ITEM_UTIL_JS, compress );
      register( TREE_COLUMN_JS, compress );
      register( SCROLLED_COMPOSITE_JS, compress );
      register( SEPARATOR_JS, compress );
      register( LABEL_UTIL_JS, compress );
      register( COMBO_JS, compress );
      register( GROUP_JS, compress );
      register( TEXT_UTIL_JS, compress );
      register( SPINNER_JS, compress );
      register( TABLE_JS, compress );
      register( TABLE_COLUMN_JS, compress );
      register( TABLE_ITEM_JS, compress );
      register( TABLE_ROW_JS, compress );
      register( LINK_UTIL_JS, compress );
      register( EXTERNALBROWSER_JS, compress );
      register( BROWSER_JS, compress );
      register( PROGRESS_BAR_JS, compress );
      register( FONT_SIZE_CALCULATION_JS, compress );
      register( CLABEL_UTIL_JS, compress );
      register( SCALE_JS, compress );
      register( DATE_TIME_DATE_JS, compress );
      register( DATE_TIME_TIME_JS, compress );
      register( DATE_TIME_CALENDAR_JS, compress );
      register( CALENDAR_JS, compress );
      register( EXPAND_BAR_JS, compress );
      register( EXPAND_ITEM_JS, compress );
      register( SLIDER_JS, compress );
      register( CHECKBOX_JS, compress );
      register( RADIOBUTTON_JS, compress );
      register( CCOMBO_JS, compress );

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

  private static void register( final String libraryName,
                                final boolean compress )
  {
    IResourceManager manager = ResourceManager.getInstance();
    RegisterOptions option = RegisterOptions.VERSION;
    if( compress ) {
      option = RegisterOptions.VERSION_AND_COMPRESS;
    }
    manager.register( libraryName, HTML.CHARSET_NAME_ISO_8859_1, option );
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    HtmlResponseWriter responseWriter = stateInfo.getResponseWriter();
    responseWriter.useJSLibrary( libraryName );
  }
}
