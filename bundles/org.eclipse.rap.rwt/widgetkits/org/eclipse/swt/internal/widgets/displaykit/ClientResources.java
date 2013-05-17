/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH and others.
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
import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
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
    "rwt/runtime/BrowserFixes.js",
    "rwt/util/Arrays.js",
    "rwt/util/Variant.js",
    "rwt/client/Client.js",
    "rwt/util/Objects.js",
    "rwt/qx/Class.js",
    "rwt/qx/Mixin.js",
    "rwt/qx/LegacyProperty.js",
    "rwt/qx/Property.js",
    "rwt/util/Strings.js",
    "rwt/qx/Object.js",
    "rwt/util/Functions.js",
    "rwt/html/Viewport.js",
    "rwt/qx/Target.js",
    "rwt/event/Event.js",
    "rwt/event/DataEvent.js",
    "rwt/event/ChangeEvent.js",
    "rwt/client/Timer.js",
    "rwt/html/Entity.js",
    "rwt/html/EventRegistration.js",
    "rwt/event/EventHandlerUtil.js",
    "rwt/remote/HandlerRegistry.js",
    "rwt/remote/ObjectRegistry.js",
    "rwt/remote/HandlerUtil.js",
    "rwt/util/Encoding.js",
    "rwt/widgets/Display.js",
    "rwt/remote/handler/DisplayHandler.js",
    "rwt/widgets/base/Widget.js",
    "rwt/widgets/util/WidgetRenderAdapter.js",
    "rwt/animation/AnimationRenderer.js",
    "rwt/animation/Animation.js",
    "rwt/animation/AnimationUtil.js",
    "rwt/animation/VisibilityAnimationMixin.js",
    "rwt/html/Style.js",
    "rwt/html/Scroll.js",
    "rwt/html/StyleSheet.js",
    "rwt/widgets/base/Parent.js",
    "rwt/event/FocusEvent.js",
    "rwt/event/EventHandler.js",
    "rwt/html/Nodes.js",
    "rwt/event/DomEvent.js",
    "rwt/event/KeyEvent.js",
    "rwt/event/MouseEvent.js",
    "rwt/util/ObjectManager.js",
    "rwt/widgets/util/IframeManager.js",
    "rwt/widgets/CoolBar.js",
    "rwt/remote/handler/CoolBarHandler.js",
    "rwt/widgets/util/LayoutImpl.js",
    "rwt/util/Numbers.js",
    "rwt/widgets/util/CanvasLayoutImpl.js",
    "rwt/widgets/base/ClientDocument.js",
    "rwt/widgets/base/Terminator.js",
    "rwt/widgets/base/ClientDocumentBlocker.js",
    "rwt/theme/AppearanceManager.js",
    "rwt/util/Colors.js",
    "rwt/html/Border.js",
    "rwt/html/Font.js",
    "rwt/widgets/util/FocusHandler.js",
    "rwt/html/Location.js",
    "rwt/html/Style.js",
    "rwt/html/Overflow.js",
    "rwt/html/ImageManager.js",
    "rwt/html/Offset.js",
    "rwt/html/ScrollIntoView.js",
    "rwt/widgets/base/BoxLayout.js",
    "rwt/widgets/util/VerticalBoxLayoutImpl.js",
    "rwt/util/Validation.js",
    "rwt/widgets/util/HorizontalBoxLayoutImpl.js",
    "rwt/widgets/base/Atom.js",
    "rwt/remote/handler/LabelHandler.js",
    "rwt/widgets/base/Label.js",
    "rwt/widgets/base/Image.js",
    "rwt/html/ImagePreloaderManager.js",
    "rwt/html/ImagePreloader.js",
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
    "rwt/html/ImagePreloaderSystem.js",
    "rwt/html/Iframes.js",
    "rwt/remote/Request.js",
    "rwt/widgets/base/PopupAtom.js",
    "rwt/widgets/base/ToolTip.js",
    "rwt/widgets/util/ToolTipManager.js",
    "rwt/html/Window.js",
    "rwt/client/BrowserNavigation.js",
    "rwt/remote/handler/BrowserNavigationHandler.js",
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
    "rwt/remote/handler/MenuHandler.js",
    "rwt/remote/EventUtil.js",
    "rwt/widgets/base/WidgetToolTip.js",
    "rwt/widgets/util/GraphicsMixin.js",
    "rwt/graphics/GraphicsUtil.js",
    "rwt/graphics/VML.js",
    "rwt/graphics/SVG.js",
    "rwt/widgets/util/WidgetUtil.js",
    "rwt/theme/ThemeStore.js",
    "rwt/remote/handler/ThemeStoreHandler.js",
    "rwt/widgets/base/MultiCellWidget.js",
    "rwt/widgets/ListItem.js",
    "rwt/event/DomEventPatch.js",
    "rwt/widgets/util/MenuManager.js",
    "rwt/widgets/MenuItem.js",
    "rwt/remote/handler/MenuItemHandler.js",
    "rwt/widgets/util/RadioButtonUtil.js",
    "rwt/widgets/MenuBar.js",
    "rwt/remote/DNDSupport.js",
    "rwt/remote/handler/DropTargetHandler.js",
    "rwt/remote/handler/DragSourceHandler.js",
    "rwt/theme/ThemeValues.js",
    "rwt/widgets/Grid.js",
    "rwt/remote/handler/ScrollBarHandler.js",
    "rwt/remote/handler/GridHandler.js",
    "rwt/widgets/GridItem.js",
    "rwt/remote/handler/GridItemHandler.js",
    "rwt/widgets/util/GridDNDFeedback.js",
    "rwt/widgets/base/GridCellToolTip.js",
    "rwt/widgets/base/GridHeader.js",
    "rwt/widgets/GridColumn.js",
    "rwt/widgets/base/GridColumnLabel.js",
    "rwt/remote/handler/GridColumnHandler.js",
    "rwt/remote/handler/GridColumnGroupHandler.js",
    "rwt/widgets/Browser.js",
    "rwt/remote/handler/BrowserHandler.js",
    "rwt/widgets/ExternalBrowser.js",
    "rwt/remote/handler/ExternalBrowserHandler.js",
    "rwt/widgets/util/FontSizeCalculation.js",
    "rwt/remote/handler/TextSizeMeasurementHandler.js",
    "rwt/widgets/Label.js",
    "rwt/widgets/base/BasicButton.js",
    "rwt/widgets/ToolItem.js",
    "rwt/widgets/Group.js",
    "rwt/remote/handler/GroupHandler.js",
    "rwt/widgets/Shell.js",
    "rwt/remote/handler/ShellHandler.js",
    "rwt/widgets/ProgressBar.js",
    "rwt/remote/handler/ProgressBarHandler.js",
    "rwt/widgets/Link.js",
    "rwt/remote/handler/LinkHandler.js",
    "rwt/widgets/base/Scrollable.js",
    "rwt/widgets/ScrolledComposite.js",
    "rwt/remote/handler/ScrolledCompositeHandler.js",
    "rwt/widgets/ToolBar.js",
    "rwt/remote/handler/ToolBarHandler.js",
    "rwt/remote/handler/ToolItemHandler.js",
    "rwt/widgets/Scale.js",
    "rwt/remote/handler/ScaleHandler.js",
    "rwt/widgets/ToolItemSeparator.js",
    "rwt/theme/BorderDefinitions.js",
    "rwt/widgets/base/BasicList.js",
    "rwt/widgets/Combo.js",
    "rwt/remote/handler/ComboHandler.js",
    "rwt/widgets/util/FocusIndicator.js",
    "rwt/remote/handler/GCHandler.js",
    "rwt/widgets/GC.js",
    "rwt/graphics/VMLCanvas.js",
    "rwt/remote/handler/CompositeHandler.js",
    "rwt/widgets/Composite.js",
    "rwt/widgets/Sash.js",
    "rwt/remote/handler/SashHandler.js",
    "rwt/remote/handler/CanvasHandler.js",
    "rwt/widgets/List.js",
    "rwt/remote/handler/ListHandler.js",
    "rwt/widgets/util/TabUtil.js",
    "rwt/remote/handler/TabFolderHandler.js",
    "rwt/remote/handler/TabItemHandler.js",
    "rwt/widgets/base/Calendar.js",
    "rwt/widgets/CoolItem.js",
    "rwt/remote/handler/CoolItemHandler.js",
    "rwt/widgets/Button.js",
    "rwt/remote/handler/ButtonHandler.js",
    "rwt/widgets/FileUpload.js",
    "rwt/remote/handler/FileUploadHandler.js",
    "rwt/widgets/Slider.js",
    "rwt/remote/handler/SliderHandler.js",
    "rwt/widgets/Spinner.js",
    "rwt/remote/handler/SpinnerHandler.js",
    "rwt/widgets/DateTimeTime.js",
    "rwt/widgets/DateTimeDate.js",
    "rwt/widgets/DateTimeCalendar.js",
    "rwt/remote/handler/DateTimeHandler.js",
    "rwt/widgets/ExpandItem.js",
    "rwt/remote/handler/ExpandItemHandler.js",
    "rwt/widgets/ExpandBar.js",
    "rwt/remote/handler/ExpandBarHandler.js",
    "rwt/widgets/Text.js",
    "rwt/remote/handler/TextHandler.js",
    "rwt/widgets/Separator.js",
    "rwt/remote/handler/SeparatorHandler.js",
    "rwt/widgets/ControlDecorator.js",
    "rwt/remote/handler/ControlDecoratorHandler.js",
    "rwt/runtime/MobileWebkitSupport.js",
    "rwt/widgets/ToolTip.js",
    "rwt/remote/handler/ToolTipHandler.js",
    "rwt/remote/WidgetManager.js",
    "rwt/remote/MessageProcessor.js",
    "rwt/remote/MessageWriter.js",
    "rwt/client/ServerPush.js",
    "rwt/remote/handler/ServerPushHandler.js",
    "rwt/remote/Server.js",
    "rwt/widgets/CTabItem.js",
    "rwt/remote/handler/CTabItemHandler.js",
    "rwt/widgets/CTabFolder.js",
    "rwt/remote/handler/CTabFolderHandler.js",
    "rwt/remote/RemoteObject.js",
    "rwt/remote/RemoteObjectFactory.js",
    "rwt/remote/KeyEventSupport.js",
    "rwt/client/JavaScriptExecutor.js",
    "rwt/remote/handler/ConnectionMessagesHandler.js",
    "rwt/client/ClientMessages.js",
    "rwt/remote/handler/ClientMessagesHandler.js",
    "rwt/remote/handler/JavaScriptExecutorHandler.js",
    "rwt/client/UrlLauncher.js",
    "rwt/remote/handler/UrlLauncherHandler.js",
    "rwt/client/JavaScriptLoader.js",
    "rwt/remote/handler/JavaScriptLoaderHandler.js",
    "rwt/runtime/System.js",
    "rwt/remote/handler/ClientInfoHandler.js",
    "rwt/widgets/util/MnemonicHandler.js",
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

  private final ApplicationContextImpl applicationContext;
  private final ResourceManager resourceManager;
  private final ThemeManager themeManager;

  public ClientResources( ApplicationContextImpl applicationContext ) {
    this.applicationContext = applicationContext;
    resourceManager = applicationContext.getResourceManager();
    themeManager = applicationContext.getThemeManager();
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

  private void registerJavascriptFiles()
    throws IOException
  {
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
      theme.registerResources( applicationContext );
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

  private void registerJavascriptResource( ContentBuffer buffer, String name ) throws IOException {
    InputStream inputStream = buffer.getContentAsStream();
    try {
      resourceManager.register( name, inputStream );
    } finally {
      inputStream.close();
    }
    String location = resourceManager.getLocation( name );
    applicationContext.getStartupPage().setClientJsLibrary( location );
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
