/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 *    Rüdiger Herrmann - bug 335112
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.displaykit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.eclipse.rap.rwt.internal.RWTProperties;
import org.eclipse.rap.rwt.internal.application.RWTFactory;
import org.eclipse.rap.rwt.internal.resources.ContentBuffer;
import org.eclipse.rap.rwt.internal.theme.QxAppearanceWriter;
import org.eclipse.rap.rwt.internal.theme.Theme;
import org.eclipse.rap.rwt.internal.theme.ThemeManager;
import org.eclipse.rap.rwt.internal.util.HTTP;
import org.eclipse.rap.rwt.service.ResourceManager;


public final class ClientResources {

  private static final String CLIENT_JS = "client.js";
  private static final String JSON_MIN_JS = "json2.min.js";

  private static final String[] JAVASCRIPT_FILES = new String[] {
    "debug-settings.js",
    "rwt/runtime/Bootstrap.js",
    "rwt/runtime/PrototypeExtender.js",
    "rwt/util/Array.js",
    "rwt/util/Variant.js",
    "rwt/client/Client.js",
    "rwt/util/Object.js",
    "qx/Class.js",
    "qx/Mixin.js",
    "qx/core/MUserData.js",
    "qx/core/LegacyProperty.js",
    "qx/core/Property.js",
    "rwt/util/String.js",
    "qx/core/Object.js",
    "rwt/util/Function.js",
    "qx/bom/Viewport.js",
    "qx/core/Target.js",
    "qx/event/type/Event.js",
    "qx/event/type/DataEvent.js",
    "qx/event/type/ChangeEvent.js",
    "rwt/client/Timer.js",
    "qx/html/String.js",
    "qx/dom/String.js",
    "qx/html/Entity.js",
    "qx/html/EventRegistration.js",
    "org/eclipse/rwt/EventHandlerUtil.js",
    "org/eclipse/rwt/HtmlUtil.js",
    "rwt/protocol/AdapterRegistry.js",
    "rwt/protocol/ObjectRegistry.js",
    "rwt/protocol/AdapterUtil.js",
    "rwt/protocol/EncodingUtil.js",
    "rwt/widgets/Display.js",
    "rwt/protocol/adapter/DisplayAdapter.js",
    "rwt/widgets/base/Widget.js",
    "org/eclipse/rwt/WidgetRenderAdapter.js",
    "rwt/animation/AnimationRenderer.js",
    "rwt/animation/Animation.js",
    "rwt/animation/AnimationUtil.js",
    "rwt/animation/VisibilityAnimationMixin.js",
    "qx/html/Dimension.js",
    "qx/html/Style.js",
    "qx/html/Scroll.js",
    "qx/html/StyleSheet.js",
    "rwt/widgets/base/Parent.js",
    "qx/event/type/FocusEvent.js",
    "org/eclipse/rwt/EventHandler.js",
    "qx/dom/Node.js",
    "qx/event/type/DomEvent.js",
    "qx/event/type/KeyEvent.js",
    "qx/event/type/MouseEvent.js",
    "rwt/util/ObjectManager.js",
    "qx/ui/embed/IframeManager.js",
    "rwt/widgets/CoolBar.js",
    "rwt/protocol/adapter/CoolBarAdapter.js",
    "qx/ui/layout/impl/LayoutImpl.js",
    "rwt/util/Number.js",
    "qx/ui/layout/impl/CanvasLayoutImpl.js",
    "rwt/widgets/base/ClientDocument.js",
    "rwt/widgets/base/Terminator.js",
    "rwt/widgets/base/ClientDocumentBlocker.js",
    "rwt/theme/AppearanceManager.js",
    "rwt/util/ColorUtil.js",
    "org/eclipse/rwt/Border.js",
    "qx/ui/core/Font.js",
    "qx/event/handler/FocusHandler.js",
    "qx/bom/element/Location.js",
    "qx/bom/element/Style.js",
    "qx/bom/element/BoxSizing.js",
    "qx/bom/Document.js",
    "qx/bom/element/Overflow.js",
    "qx/io/image/Manager.js",
    "qx/html/Offset.js",
    "qx/html/ScrollIntoView.js",
    "rwt/widgets/base/BoxLayout.js",
    "qx/ui/layout/impl/VerticalBoxLayoutImpl.js",
    "rwt/util/Validation.js",
    "qx/ui/layout/impl/HorizontalBoxLayoutImpl.js",
    "rwt/widgets/base/Atom.js",
    "rwt/protocol/adapter/LabelAdapter.js",
    "rwt/widgets/base/Label.js",
    "rwt/widgets/base/Image.js",
    "qx/io/image/PreloaderManager.js",
    "qx/io/image/Preloader.js",
    "qx/constant/Layout.js",
    "qx/constant/Style.js",
    "rwt/widgets/base/HorizontalBoxLayout.js",
    "rwt/widgets/base/Spinner.js",
    "rwt/widgets/base/BasicText.js",
    "rwt/widgets/base/VerticalBoxLayout.js",
    "rwt/widgets/base/Button.js",
    "rwt/util/Range.js",
    "rwt/widgets/TabFolder.js",
    "qx/ui/selection/RadioManager.js",
    "rwt/widgets/base/TabFolderBar.js",
    "rwt/widgets/base/TabFolderPane.js",
    "rwt/widgets/base/Popup.js",
    "qx/ui/popup/PopupManager.js",
    "qx/ui/selection/SelectionManager.js",
    "qx/ui/selection/Selection.js",
    "rwt/widgets/base/AbstractSlider.js",
    "rwt/widgets/base/ScrollBar.js",
    "qx/io/image/PreloaderSystem.js",
    "qx/html/Iframe.js",
    "rwt/remote/Request.js",
    "rwt/widgets/base/PopupAtom.js",
    "rwt/widgets/base/ToolTip.js",
    "qx/ui/popup/ToolTipManager.js",
    "qx/html/Window.js",
    "rwt/client/History.js",
    "rwt/protocol/adapter/BrowserHistoryAdapter.js",
    "qx/event/handler/DragAndDropHandler.js",
    "qx/event/type/DragEvent.js",
    "rwt/widgets/base/HtmlEmbed.js",
    "rwt/widgets/base/Iframe.js",
    "qx/ui/resizer/MResizable.js",
    "rwt/widgets/base/ResizablePopup.js",
    "rwt/widgets/base/Window.js",
    "rwt/widgets/base/HorizontalSpacer.js",
    "qx/ui/window/Manager.js",
    "rwt/widgets/MenuItemSeparator.js",
    "rwt/widgets/base/TabFolderPage.js",
    "rwt/widgets/TabItem.js",
    "rwt/runtime/ErrorHandler.js",
    "rwt/widgets/base/GridRowContainer.js",
    "org/eclipse/rwt/GridRowContainerWrapper.js",
    "org/eclipse/rwt/GridUtil.js",
    "rwt/widgets/base/GridRow.js",
    "rwt/widgets/Menu.js",
    "rwt/protocol/adapter/MenuAdapter.js",
    "org/eclipse/swt/EventUtil.js",
    "rwt/widgets/base/WidgetToolTip.js",
    "org/eclipse/rwt/GraphicsMixin.js",
    "org/eclipse/rwt/GraphicsUtil.js",
    "org/eclipse/rwt/VML.js",
    "org/eclipse/rwt/SVG.js",
    "org/eclipse/swt/WidgetUtil.js",
    "rwt/theme/ThemeStore.js",
    "rwt/protocol/adapter/ThemeStoreAdapter.js",
    "rwt/widgets/base/MultiCellWidget.js",
    "rwt/widgets/ListItem.js",
    "org/eclipse/rwt/DomEventPatch.js",
    "org/eclipse/rwt/MenuManager.js",
    "rwt/widgets/MenuItem.js",
    "rwt/protocol/adapter/MenuItemAdapter.js",
    "org/eclipse/rwt/RadioButtonUtil.js",
    "rwt/widgets/MenuBar.js",
    "org/eclipse/rwt/DNDSupport.js",
    "rwt/protocol/adapter/DropTargetAdapter.js",
    "rwt/protocol/adapter/DragSourceAdapter.js",
    "rwt/theme/ThemeValues.js",
    "rwt/widgets/Grid.js",
    "rwt/protocol/adapter/ScrollBarAdapter.js",
    "rwt/protocol/adapter/GridAdapter.js",
    "rwt/widgets/GridItem.js",
    "rwt/protocol/adapter/GridItemAdapter.js",
    "org/eclipse/rwt/GridDNDFeedback.js",
    "rwt/widgets/base/GridCellToolTip.js",
    "rwt/widgets/base/GridHeader.js",
    "rwt/widgets/GridColumn.js",
    "rwt/widgets/base/GridColumnLabel.js",
    "rwt/protocol/adapter/GridColumnAdapter.js",
    "rwt/protocol/adapter/GridColumnGroupAdapter.js",
    "rwt/widgets/Browser.js",
    "rwt/protocol/adapter/BrowserAdapter.js",
    "rwt/widgets/ExternalBrowser.js",
    "rwt/protocol/adapter/ExternalBrowserAdapter.js",
    "org/eclipse/swt/FontSizeCalculation.js",
    "rwt/protocol/adapter/TextSizeMeasurementAdatper.js",
    "rwt/widgets/Label.js",
    "rwt/widgets/base/BasicButton.js",
    "rwt/widgets/ToolItem.js",
    "rwt/widgets/Group.js",
    "rwt/protocol/adapter/GroupAdapter.js",
    "rwt/widgets/Shell.js",
    "rwt/protocol/adapter/ShellAdapter.js",
    "rwt/widgets/ProgressBar.js",
    "rwt/protocol/adapter/ProgressBarAdapter.js",
    "rwt/widgets/Link.js",
    "rwt/protocol/adapter/LinkAdapter.js",
    "rwt/widgets/base/Scrollable.js",
    "rwt/widgets/ScrolledComposite.js",
    "rwt/protocol/adapter/ScrolledCompositeAdapter.js",
    "rwt/widgets/ToolBar.js",
    "rwt/protocol/adapter/ToolBarAdapter.js",
    "rwt/protocol/adapter/ToolItemAdapter.js",
    "rwt/widgets/Scale.js",
    "rwt/protocol/adapter/ScaleAdapter.js",
    "rwt/widgets/ToolItemSeparator.js",
    "rwt/theme/BorderDefinitions.js",
    "rwt/widgets/base/BasicList.js",
    "rwt/widgets/Combo.js",
    "rwt/protocol/adapter/ComboAdapter.js",
    "org/eclipse/rwt/FocusIndicator.js",
    "rwt/protocol/adapter/GCAdapter.js",
    "org/eclipse/swt/graphics/GC.js",
    "org/eclipse/rwt/VMLCanvas.js",
    "rwt/protocol/adapter/CompositeAdapter.js",
    "rwt/widgets/Composite.js",
    "rwt/widgets/Sash.js",
    "rwt/protocol/adapter/SashAdapter.js",
    "rwt/protocol/adapter/CanvasAdapter.js",
    "rwt/widgets/List.js",
    "rwt/protocol/adapter/ListAdapter.js",
    "org/eclipse/swt/TabUtil.js",
    "rwt/protocol/adapter/TabFolderAdapter.js",
    "rwt/protocol/adapter/TabItemAdapter.js",
    "rwt/widgets/base/Calendar.js",
    "rwt/widgets/CoolItem.js",
    "rwt/protocol/adapter/CoolItemAdapter.js",
    "rwt/widgets/Button.js",
    "rwt/protocol/adapter/ButtonAdapter.js",
    "rwt/widgets/FileUpload.js",
    "rwt/protocol/adapter/FileUploadAdapter.js",
    "rwt/widgets/Slider.js",
    "rwt/protocol/adapter/SliderAdapter.js",
    "rwt/widgets/Spinner.js",
    "rwt/protocol/adapter/SpinnerAdapter.js",
    "rwt/widgets/DateTimeTime.js",
    "rwt/widgets/DateTimeDate.js",
    "rwt/widgets/DateTimeCalendar.js",
    "rwt/protocol/adapter/DateTimeAdapter.js",
    "rwt/widgets/ExpandItem.js",
    "rwt/protocol/adapter/ExpandItemAdapter.js",
    "rwt/widgets/ExpandBar.js",
    "rwt/protocol/adapter/ExpandBarAdapter.js",
    "rwt/widgets/Text.js",
    "rwt/protocol/adapter/TextAdapter.js",
    "rwt/widgets/Separator.js",
    "rwt/protocol/adapter/SeparatorAdapter.js",
    "rwt/widgets/ControlDecorator.js",
    "rwt/protocol/adapter/ControlDecoratorAdapter.js",
    "rwt/runtime/MobileWebkitSupport.js",
    "rwt/widgets/ToolTip.js",
    "rwt/protocol/adapter/ToolTipAdapter.js",
    "org/eclipse/swt/WidgetManager.js",
    "rwt/protocol/MessageProcessor.js",
    "rwt/protocol/MessageWriter.js",
    "rwt/client/UICallBack.js",
    "rwt/protocol/adapter/UICallBackAdapter.js",
    "rwt/remote/Server.js",
    "rwt/widgets/CTabItem.js",
    "rwt/protocol/adapter/CTabItemAdapter.js",
    "rwt/widgets/CTabFolder.js",
    "rwt/protocol/adapter/CTabFolderAdapter.js",
    "rwt/protocol/ServerObject.js",
    "rwt/protocol/ServerObjectFactory.js",
    "org/eclipse/rwt/KeyEventSupport.js",
    "rwt/client/JavaScriptExecutor.js",
    "rwt/protocol/adapter/JavaScriptExecutorAdapter.js",
    "rwt/client/JavaScriptLoader.js",
    "rwt/protocol/adapter/JavaScriptLoaderAdapter.js",
    "rwt/runtime/System.js",
    "rwt/protocol/adapter/ClientInfoAdapter.js",
    "rap.js"
  };

  private static final String[] WIDGET_IMAGES = new String[] {
    "resource/static/image/blank.gif",
    "resource/static/image/dotted_white.gif",
    "resource/widget/rap/arrows/chevron-left.png",
    "resource/widget/rap/arrows/chevron-right.png",
    "resource/widget/rap/arrows/chevron-left-hover.png",
    "resource/widget/rap/arrows/chevron-right-hover.png",
    "resource/widget/rap/tree/loading.gif",
    "resource/widget/rap/ctabfolder/maximize.gif",
    "resource/widget/rap/ctabfolder/minimize.gif",
    "resource/widget/rap/ctabfolder/restore.gif",
    "resource/widget/rap/ctabfolder/close.gif",
    "resource/widget/rap/ctabfolder/close_hover.gif",
    "resource/widget/rap/ctabfolder/ctabfolder-dropdown.png",
    "resource/widget/rap/cursors/alias.gif",
    "resource/widget/rap/cursors/copy.gif",
    "resource/widget/rap/cursors/move.gif",
    "resource/widget/rap/cursors/nodrop.gif",
    "resource/widget/rap/cursors/up_arrow.cur",
    "resource/widget/rap/scale/h_line.gif",
    "resource/widget/rap/scale/v_line.gif"
  };

  private final ResourceManager resourceManager;
  private final ThemeManager themeManager;

  public ClientResources( ResourceManager resourceManager, ThemeManager themeManager ) {
    this.resourceManager = resourceManager;
    this.themeManager = themeManager;
  }

  public void registerResources() {
    try {
      registerTextResource( "resource/static/html/blank.html" );
      registerJavascriptFiles();
      registerThemeResources();
      registerWidgetImages();
    } catch( IOException ioe ) {
      throw new RuntimeException( "Failed to register resources", ioe );
    }
  }

  private void registerJavascriptFiles() throws IOException {
    ContentBuffer contentBuffer = new ContentBuffer();
    String appearanceCode = getQxAppearanceThemeCode();
    if( RWTProperties.isDevelopmentMode() ) {
      for( String javascriptFile : JAVASCRIPT_FILES ) {
        append( contentBuffer, javascriptFile );
      }
    } else {
      append( contentBuffer, CLIENT_JS );
    }
    String json2Code = readResourceContent( JSON_MIN_JS );
    contentBuffer.append( json2Code.getBytes( HTTP.CHARSET_UTF_8 ) );
    contentBuffer.append( appearanceCode.getBytes( HTTP.CHARSET_UTF_8 ) );
    registerJavascriptResource( contentBuffer, "rap-client.js" );
  }

  private String getQxAppearanceThemeCode() {
    List<String> customAppearances = themeManager.getAppearances();
    return QxAppearanceWriter.createQxAppearanceTheme( customAppearances );
  }

  private void append( ContentBuffer contentBuffer, String location ) throws IOException {
    InputStream inputStream = openResourceStream( location );
    try {
      contentBuffer.append( inputStream );
    } finally {
      inputStream.close();
    }
  }

  private void registerThemeResources() {
    String[] themeIds = themeManager.getRegisteredThemeIds();
    for( String themeId : themeIds ) {
      Theme theme = themeManager.getTheme( themeId );
      theme.registerResources( resourceManager );
    }
  }

  private void registerWidgetImages() throws IOException {
    for( String resourcePath : WIDGET_IMAGES ) {
      InputStream inputStream = openResourceStream( resourcePath );
      resourceManager.register( resourcePath, inputStream );
      inputStream.close();
    }
  }

  private void registerTextResource( String name ) throws IOException {
    InputStream inputStream = openResourceStream( name );
    try {
      resourceManager.register( name, inputStream );
    } finally {
      inputStream.close();
    }
  }

  private void registerJavascriptResource( ContentBuffer buffer, String name )
    throws IOException
  {
    InputStream inputStream = buffer.getContentAsStream();
    try {
      resourceManager.register( name, inputStream );
    } finally {
      inputStream.close();
    }
    String location = resourceManager.getLocation( name );
    RWTFactory.getStartupPage().setClientJsLibrary( location );
  }

  private String readResourceContent( String location ) throws IOException {
    byte[] buffer = new byte[ 40960 ];
    InputStream inputStream = openResourceStream( location );
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    try {
      int read = inputStream.read( buffer );
      while( read != -1 ) {
        outputStream.write( buffer, 0, read );
        read = inputStream.read( buffer );
      }
    } finally {
      inputStream.close();
    }
    return outputStream.toString( HTTP.CHARSET_UTF_8 );
  }

  private InputStream openResourceStream( String name ) {
    return getClass().getClassLoader().getResourceAsStream( name );
  }
}
