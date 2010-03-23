/*******************************************************************************
 * Copyright (c) 2002, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.displaykit;

import java.io.InputStream;

import org.eclipse.rwt.internal.lifecycle.HtmlResponseWriter;
import org.eclipse.rwt.internal.resources.ResourceManager;
import org.eclipse.rwt.internal.resources.ResourceRegistry;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.IServiceStateInfo;
import org.eclipse.rwt.internal.util.HTML;
import org.eclipse.rwt.resources.*;
import org.eclipse.rwt.resources.IResourceManager.RegisterOptions;


// TODO [rh] Should javaScript namespaces include widget and/or custom?
//      e.g. org/eclipse/swt/widgets/TabUtil.js
final class QooxdooResourcesUtil {


  private static final String CLIENT_LIBRARY_VARIANT
    = "org.eclipse.rwt.clientLibraryVariant";
  private static final String DEBUG_CLIENT_LIBRARY_VARIANT = "DEBUG";

  private static final String QX_JS = "qx.js";
  private static final String QX_DEBUG_JS = "qx-debug.js";

  private static final String[] JAVASCRIPT_FILES = new String[]{
    // TODO [rh] since qx 0.6.5 all constants seem to be 'inlined'
    // these three files are here to keep DefaultAppearanceTheme.js
    // happy that makes heavy use of constants
    "qx/constant/Core.js",
    "qx/constant/Layout.js",
    "qx/constant/Style.js",
    
    "org/eclipse/rwt/KeyEventHandlerPatch.js",
    "org/eclipse/rwt/DomEventPatch.js",
    "org/eclipse/rwt/SVG.js",
    "org/eclipse/rwt/VML.js",
    "org/eclipse/rwt/GraphicsUtil.js",
    "org/eclipse/rwt/GraphicsMixin.js",
    "org/eclipse/rwt/RoundedBorder.js",
    "org/eclipse/swt/Application.js",
    "org/eclipse/swt/Request.js",
    "org/eclipse/swt/WidgetManager.js",
    "org/eclipse/swt/EventUtil.js",
    "org/eclipse/rwt/KeyEventUtil.js",
    "org/eclipse/rwt/AsyncKeyEventUtil.js",
    "org/eclipse/rwt/SyncKeyEventUtil.js",
    "org/eclipse/rwt/widgets/ToolTip.js",
    "org/eclipse/swt/WidgetUtil.js",
    "org/eclipse/swt/widgets/Composite.js",
    "org/eclipse/swt/widgets/Sash.js",
    "org/eclipse/swt/TabUtil.js",
    "org/eclipse/swt/custom/CTabItem.js",
    "org/eclipse/swt/custom/CTabFolder.js",
    "org/eclipse/swt/widgets/CoolItem.js",
    "org/eclipse/swt/widgets/List.js",
    "org/eclipse/swt/widgets/Shell.js",
    "org/eclipse/swt/widgets/Tree.js",
    "org/eclipse/swt/widgets/TreeItem.js",
    "org/eclipse/swt/TreeItemUtil.js",
    "org/eclipse/swt/widgets/TreeColumn.js",
    "org/eclipse/swt/custom/ScrolledComposite.js",
    "org/eclipse/swt/widgets/Separator.js",
    "org/eclipse/swt/LabelUtil.js",
    "org/eclipse/swt/widgets/Combo.js",
    "org/eclipse/swt/widgets/Group.js",
    "org/eclipse/swt/TextUtil.js",
    "org/eclipse/swt/widgets/Spinner.js",
    "org/eclipse/swt/widgets/Table.js",
    "org/eclipse/swt/widgets/TableColumn.js",
    "org/eclipse/swt/widgets/TableItem.js",
    "org/eclipse/swt/widgets/TableRow.js",
    "org/eclipse/swt/widgets/TableCellToolTip.js",
    "org/eclipse/rwt/widgets/ExternalBrowser.js",
    "org/eclipse/swt/browser/Browser.js",
    "org/eclipse/swt/widgets/ProgressBar.js",
    "org/eclipse/swt/FontSizeCalculation.js",
    "org/eclipse/swt/CLabelUtil.js",
    "org/eclipse/swt/widgets/Scale.js",
    "org/eclipse/swt/widgets/DateTimeDate.js",
    "org/eclipse/swt/widgets/DateTimeTime.js",
    "org/eclipse/swt/widgets/DateTimeCalendar.js",
    "org/eclipse/swt/widgets/Calendar.js",
    "org/eclipse/swt/widgets/ExpandBar.js",
    "org/eclipse/swt/widgets/ExpandItem.js",
    "org/eclipse/swt/widgets/Slider.js",
    "org/eclipse/rwt/RadioButtonUtil.js",
    "org/eclipse/swt/widgets/Link.js",
    "org/eclipse/rwt/widgets/MultiCellWidget.js",
    "org/eclipse/rwt/widgets/AbstractButton.js",
    "org/eclipse/rwt/widgets/Button.js",
    "org/eclipse/rwt/widgets/Menu.js",
    "org/eclipse/rwt/widgets/MenuItem.js",
    "org/eclipse/rwt/widgets/ToolBar.js",
    "org/eclipse/rwt/widgets/ToolItem.js",
    "org/eclipse/rwt/widgets/ToolSeparator.js",
    "org/eclipse/rwt/widgets/MenuBar.js",
    "org/eclipse/swt/theme/AppearancesBase.js",
    "org/eclipse/swt/theme/BordersBase.js",
    "org/eclipse/swt/theme/ThemeStore.js",
    "org/eclipse/swt/theme/ThemeValues.js",
    "org/eclipse/rwt/FocusIndicator.js",
    "org/eclipse/rwt/MenuManager.js",
    "org/eclipse/rwt/DNDSupport.js",
    "org/eclipse/rwt/TreeDNDFeedback.js",
    "org/eclipse/rwt/TableDNDFeedback.js",
    "org/eclipse/rwt/widgets/ControlDecorator.js",
    "org/eclipse/rwt/widgets/Text.js"
  };

  private static final String[] WIDGET_IMAGES = new String[]{
    "resource/static/image/blank.gif",
    "resource/static/image/dotted_white.gif",
    "resource/widget/rap/ctabfolder/maximize.gif",
    "resource/widget/rap/ctabfolder/minimize.gif",
    "resource/widget/rap/ctabfolder/restore.gif",
    "resource/widget/rap/ctabfolder/close.gif",
    "resource/widget/rap/ctabfolder/close_hover.gif",
    "resource/widget/rap/ctabfolder/chevron.gif",
    "resource/widget/rap/cursors/alias.gif",
    "resource/widget/rap/cursors/copy.gif",
    "resource/widget/rap/cursors/move.gif",
    "resource/widget/rap/cursors/nodrop.gif",
    "resource/widget/rap/cursors/up_arrow.cur",
    "resource/widget/rap/tree/cross.gif",
    "resource/widget/rap/tree/cross_minus.gif",
    "resource/widget/rap/tree/cross_plus.gif",
    "resource/widget/rap/tree/end.gif",
    "resource/widget/rap/tree/end_minus.gif",
    "resource/widget/rap/tree/end_plus.gif",
    "resource/widget/rap/tree/folder_open.gif",
    "resource/widget/rap/tree/folder_closed.gif",
    "resource/widget/rap/tree/line.gif",
    "resource/widget/rap/tree/minus.gif",
    "resource/widget/rap/tree/only_minus.gif",
    "resource/widget/rap/tree/only_plus.gif",
    "resource/widget/rap/tree/plus.gif",
    "resource/widget/rap/tree/start_minus.gif",
    "resource/widget/rap/tree/start_plus.gif",
    "resource/widget/rap/scale/h_line.gif",
    "resource/widget/rap/scale/v_line.gif",
    "resource/widget/rap/scale/h_thumb.gif",
    "resource/widget/rap/scale/v_thumb.gif",
    "resource/widget/rap/scale/h_marker_big.gif",
    "resource/widget/rap/scale/v_marker_big.gif",
    "resource/widget/rap/scale/h_marker_small.gif",
    "resource/widget/rap/scale/v_marker_small.gif",
  };

  private QooxdooResourcesUtil() {
    // prevent instance creation
  }

  public static void registerResources() {
    ClassLoader loader = QooxdooResourcesUtil.class.getClassLoader();
    IResourceManager manager = ResourceManager.getInstance();
    ClassLoader bufferedLoader = manager.getContextLoader();
    manager.setContextLoader( loader );
    try {
      // TODO [rst] Needed by qx.js - can we get rid of it?
      manager.register( "resource/static/html/blank.html",
                        HTML.CHARSET_NAME_ISO_8859_1 );
      registerJavascriptFiles();
    } finally {
      manager.setContextLoader( bufferedLoader );
    }
    registerWidgetImages();
    registerContributions();
  }

  private static void registerJavascriptFiles() {
    if( isDebug() ) {
      register( QX_DEBUG_JS, false );
    } else {
      register( QX_JS, false );
    }
    boolean compress = !isDebug();
    for( int i = 0; i < JAVASCRIPT_FILES.length; i++ ) {
      String resource = JAVASCRIPT_FILES[ i ];
      register( resource, compress );
    }
  }

  private static void registerWidgetImages() {
    IResourceManager manager = ResourceManager.getInstance();
    ClassLoader classLoader = QooxdooResourcesUtil.class.getClassLoader();
    for( int i = 0; i < WIDGET_IMAGES.length; i++ ) {
      String resourcePath = WIDGET_IMAGES[ i ];
      InputStream inputStream = classLoader.getResourceAsStream( resourcePath );
      if( inputStream == null ) {
        String mesg = "Resource not found: " + resourcePath;
        throw new IllegalArgumentException( mesg );
      }
      manager.register( resourcePath, inputStream );
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

  private static boolean isDebug() {
    String libraryVariant = System.getProperty( CLIENT_LIBRARY_VARIANT );
    boolean isDebug = DEBUG_CLIENT_LIBRARY_VARIANT.equals( libraryVariant );
    return isDebug;
  }
}
