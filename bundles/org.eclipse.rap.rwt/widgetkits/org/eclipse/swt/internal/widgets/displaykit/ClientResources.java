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
    "rwt/qx/Class.js",
    "rwt/qx/Mixin.js",
    "rwt/qx/LegacyProperty.js",
    "rwt/qx/Property.js",
    "rwt/util/String.js",
    "rwt/qx/Object.js",
    "rwt/util/Function.js",
    "rwt/util/html/Viewport.js",
    "rwt/qx/Target.js",
    "rwt/event/Event.js",
    "rwt/event/DataEvent.js",
    "rwt/event/ChangeEvent.js",
    "rwt/client/Timer.js",
    "rwt/util/html/String.js",
    "rwt/util/html/Entity.js",
    "rwt/util/html/EventRegistration.js",
    "rwt/event/EventHandlerUtil.js",
    "rwt/util/html/HtmlUtil.js",
    "rwt/protocol/HandlerRegistry.js",
    "rwt/protocol/ObjectRegistry.js",
    "rwt/protocol/HandlerUtil.js",
    "rwt/protocol/EncodingUtil.js",
    "rwt/widgets/Display.js",
    "rwt/protocol/handler/DisplayHandler.js",
    "rwt/widgets/base/Widget.js",
    "rwt/widgets/util/WidgetRenderAdapter.js",
    "rwt/animation/AnimationRenderer.js",
    "rwt/animation/Animation.js",
    "rwt/animation/AnimationUtil.js",
    "rwt/animation/VisibilityAnimationMixin.js",
    "rwt/util/html/Style.js",
    "rwt/util/html/Scroll.js",
    "rwt/util/html/StyleSheet.js",
    "rwt/widgets/base/Parent.js",
    "rwt/event/FocusEvent.js",
    "rwt/event/EventHandler.js",
    "rwt/util/html/Node.js",
    "rwt/event/DomEvent.js",
    "rwt/event/KeyEvent.js",
    "rwt/event/MouseEvent.js",
    "rwt/util/ObjectManager.js",
    "rwt/widgets/util/IframeManager.js",
    "rwt/widgets/CoolBar.js",
    "rwt/protocol/handler/CoolBarHandler.js",
    "rwt/widgets/util/LayoutImpl.js",
    "rwt/util/Number.js",
    "rwt/widgets/util/CanvasLayoutImpl.js",
    "rwt/widgets/base/ClientDocument.js",
    "rwt/widgets/base/Terminator.js",
    "rwt/widgets/base/ClientDocumentBlocker.js",
    "rwt/theme/AppearanceManager.js",
    "rwt/util/ColorUtil.js",
    "rwt/util/html/Border.js",
    "rwt/util/html/Font.js",
    "rwt/widgets/util/FocusHandler.js",
    "rwt/util/html/Location.js",
    "rwt/util/html/Style.js",
    "rwt/util/html/BoxSizing.js",
    "rwt/util/html/Document.js",
    "rwt/util/html/Overflow.js",
    "rwt/util/html/ImageManager.js",
    "rwt/util/html/Offset.js",
    "rwt/util/html/ScrollIntoView.js",
    "rwt/widgets/base/BoxLayout.js",
    "rwt/widgets/util/VerticalBoxLayoutImpl.js",
    "rwt/util/Validation.js",
    "rwt/widgets/util/HorizontalBoxLayoutImpl.js",
    "rwt/widgets/base/Atom.js",
    "rwt/protocol/handler/LabelHandler.js",
    "rwt/widgets/base/Label.js",
    "rwt/widgets/base/Image.js",
    "rwt/util/html/ImagePreloaderManager.js",
    "rwt/util/html/ImagePreloader.js",
    "rwt/widgets/util/Layout.js",
    "rwt/widgets/base/HorizontalBoxLayout.js",
    "rwt/widgets/base/Spinner.js",
    "rwt/widgets/base/BasicText.js",
    "rwt/widgets/base/VerticalBoxLayout.js",
    "rwt/widgets/base/Button.js",
    "rwt/util/Range.js",
    "rwt/widgets/TabFolder.js",
    "rwt/widgets/util/RadioManager.js",
    "rwt/widgets/base/TabFolderBar.js",
    "rwt/widgets/base/TabFolderPane.js",
    "rwt/widgets/base/Popup.js",
    "rwt/widgets/util/PopupManager.js",
    "rwt/widgets/util/SelectionManager.js",
    "rwt/widgets/util/Selection.js",
    "rwt/widgets/base/AbstractSlider.js",
    "rwt/widgets/base/ScrollBar.js",
    "rwt/util/html/ImagePreloaderSystem.js",
    "rwt/util/html/Iframe.js",
    "rwt/remote/Request.js",
    "rwt/widgets/base/PopupAtom.js",
    "rwt/widgets/base/ToolTip.js",
    "rwt/widgets/util/ToolTipManager.js",
    "rwt/util/html/Window.js",
    "rwt/client/BrowserNavigation.js",
    "rwt/protocol/handler/BrowserNavigationHandler.js",
    "rwt/event/DragAndDropHandler.js",
    "rwt/event/DragEvent.js",
    "rwt/widgets/base/HtmlEmbed.js",
    "rwt/widgets/base/Iframe.js",
    "rwt/widgets/util/MResizable.js",
    "rwt/widgets/base/ResizablePopup.js",
    "rwt/widgets/base/Window.js",
    "rwt/widgets/base/HorizontalSpacer.js",
    "rwt/widgets/util/WindowManager.js",
    "rwt/widgets/MenuItemSeparator.js",
    "rwt/widgets/base/TabFolderPage.js",
    "rwt/widgets/TabItem.js",
    "rwt/runtime/ErrorHandler.js",
    "rwt/widgets/base/GridRowContainer.js",
    "rwt/widgets/util/GridRowContainerWrapper.js",
    "rwt/widgets/util/GridUtil.js",
    "rwt/widgets/base/GridRow.js",
    "rwt/widgets/Menu.js",
    "rwt/protocol/handler/MenuHandler.js",
    "rwt/remote/EventUtil.js",
    "rwt/widgets/base/WidgetToolTip.js",
    "rwt/widgets/util/GraphicsMixin.js",
    "rwt/graphics/GraphicsUtil.js",
    "rwt/graphics/VML.js",
    "rwt/graphics/SVG.js",
    "rwt/widgets/util/WidgetUtil.js",
    "rwt/theme/ThemeStore.js",
    "rwt/protocol/handler/ThemeStoreHandler.js",
    "rwt/widgets/base/MultiCellWidget.js",
    "rwt/widgets/ListItem.js",
    "rwt/event/DomEventPatch.js",
    "rwt/widgets/util/MenuManager.js",
    "rwt/widgets/MenuItem.js",
    "rwt/protocol/handler/MenuItemHandler.js",
    "rwt/widgets/util/RadioButtonUtil.js",
    "rwt/widgets/MenuBar.js",
    "rwt/remote/DNDSupport.js",
    "rwt/protocol/handler/DropTargetHandler.js",
    "rwt/protocol/handler/DragSourceHandler.js",
    "rwt/theme/ThemeValues.js",
    "rwt/widgets/Grid.js",
    "rwt/protocol/handler/ScrollBarHandler.js",
    "rwt/protocol/handler/GridHandler.js",
    "rwt/widgets/GridItem.js",
    "rwt/protocol/handler/GridItemHandler.js",
    "rwt/widgets/util/GridDNDFeedback.js",
    "rwt/widgets/base/GridCellToolTip.js",
    "rwt/widgets/base/GridHeader.js",
    "rwt/widgets/GridColumn.js",
    "rwt/widgets/base/GridColumnLabel.js",
    "rwt/protocol/handler/GridColumnHandler.js",
    "rwt/protocol/handler/GridColumnGroupHandler.js",
    "rwt/widgets/Browser.js",
    "rwt/protocol/handler/BrowserHandler.js",
    "rwt/widgets/ExternalBrowser.js",
    "rwt/protocol/handler/ExternalBrowserHandler.js",
    "rwt/widgets/util/FontSizeCalculation.js",
    "rwt/protocol/handler/TextSizeMeasurementHandler.js",
    "rwt/widgets/Label.js",
    "rwt/widgets/base/BasicButton.js",
    "rwt/widgets/ToolItem.js",
    "rwt/widgets/Group.js",
    "rwt/protocol/handler/GroupHandler.js",
    "rwt/widgets/Shell.js",
    "rwt/protocol/handler/ShellHandler.js",
    "rwt/widgets/ProgressBar.js",
    "rwt/protocol/handler/ProgressBarHandler.js",
    "rwt/widgets/Link.js",
    "rwt/protocol/handler/LinkHandler.js",
    "rwt/widgets/base/Scrollable.js",
    "rwt/widgets/ScrolledComposite.js",
    "rwt/protocol/handler/ScrolledCompositeHandler.js",
    "rwt/widgets/ToolBar.js",
    "rwt/protocol/handler/ToolBarHandler.js",
    "rwt/protocol/handler/ToolItemHandler.js",
    "rwt/widgets/Scale.js",
    "rwt/protocol/handler/ScaleHandler.js",
    "rwt/widgets/ToolItemSeparator.js",
    "rwt/theme/BorderDefinitions.js",
    "rwt/widgets/base/BasicList.js",
    "rwt/widgets/Combo.js",
    "rwt/protocol/handler/ComboHandler.js",
    "rwt/widgets/util/FocusIndicator.js",
    "rwt/protocol/handler/GCHandler.js",
    "rwt/widgets/GC.js",
    "rwt/graphics/VMLCanvas.js",
    "rwt/protocol/handler/CompositeHandler.js",
    "rwt/widgets/Composite.js",
    "rwt/widgets/Sash.js",
    "rwt/protocol/handler/SashHandler.js",
    "rwt/protocol/handler/CanvasHandler.js",
    "rwt/widgets/List.js",
    "rwt/protocol/handler/ListHandler.js",
    "rwt/widgets/util/TabUtil.js",
    "rwt/protocol/handler/TabFolderHandler.js",
    "rwt/protocol/handler/TabItemHandler.js",
    "rwt/widgets/base/Calendar.js",
    "rwt/widgets/CoolItem.js",
    "rwt/protocol/handler/CoolItemHandler.js",
    "rwt/widgets/Button.js",
    "rwt/protocol/handler/ButtonHandler.js",
    "rwt/widgets/FileUpload.js",
    "rwt/protocol/handler/FileUploadHandler.js",
    "rwt/widgets/Slider.js",
    "rwt/protocol/handler/SliderHandler.js",
    "rwt/widgets/Spinner.js",
    "rwt/protocol/handler/SpinnerHandler.js",
    "rwt/widgets/DateTimeTime.js",
    "rwt/widgets/DateTimeDate.js",
    "rwt/widgets/DateTimeCalendar.js",
    "rwt/protocol/handler/DateTimeHandler.js",
    "rwt/widgets/ExpandItem.js",
    "rwt/protocol/handler/ExpandItemHandler.js",
    "rwt/widgets/ExpandBar.js",
    "rwt/protocol/handler/ExpandBarHandler.js",
    "rwt/widgets/Text.js",
    "rwt/protocol/handler/TextHandler.js",
    "rwt/widgets/Separator.js",
    "rwt/protocol/handler/SeparatorHandler.js",
    "rwt/widgets/ControlDecorator.js",
    "rwt/protocol/handler/ControlDecoratorHandler.js",
    "rwt/runtime/MobileWebkitSupport.js",
    "rwt/widgets/ToolTip.js",
    "rwt/protocol/handler/ToolTipHandler.js",
    "rwt/widgets/util/WidgetManager.js",
    "rwt/protocol/MessageProcessor.js",
    "rwt/protocol/MessageWriter.js",
    "rwt/client/ServerPush.js",
    "rwt/protocol/handler/ServerPushHandler.js",
    "rwt/remote/Server.js",
    "rwt/widgets/CTabItem.js",
    "rwt/protocol/handler/CTabItemHandler.js",
    "rwt/widgets/CTabFolder.js",
    "rwt/protocol/handler/CTabFolderHandler.js",
    "rwt/protocol/RemoteObject.js",
    "rwt/protocol/RemoteObjectFactory.js",
    "rwt/remote/KeyEventSupport.js",
    "rwt/client/JavaScriptExecutor.js",
    "rwt/protocol/handler/JavaScriptExecutorHandler.js",
    "rwt/client/UrlLauncher.js",
    "rwt/protocol/handler/UrlLauncherHandler.js",
    "rwt/client/JavaScriptLoader.js",
    "rwt/protocol/handler/JavaScriptLoaderHandler.js",
    "rwt/runtime/System.js",
    "rwt/protocol/handler/ClientInfoHandler.js",
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
    } catch( Throwable t ) {
      System.out.println();
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
