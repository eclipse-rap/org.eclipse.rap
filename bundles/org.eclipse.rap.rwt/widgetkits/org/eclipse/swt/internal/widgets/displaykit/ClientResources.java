/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH and others.
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
import org.eclipse.rwt.internal.application.RWTFactory;
import org.eclipse.rwt.internal.resources.ContentBuffer;
import org.eclipse.rwt.internal.resources.JSFile;
import org.eclipse.rwt.internal.resources.SystemProps;
import org.eclipse.rwt.internal.theme.Theme;
import org.eclipse.rwt.internal.theme.ThemeManager;
import org.eclipse.rwt.internal.util.HTTP;
import org.eclipse.rwt.resources.IResource;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.rwt.resources.IResourceManager.RegisterOptions;


public final class ClientResources {

  private static final String CLIENT_JS = "client.js";
  private static final String JSON_JS = "json2.js";

  private static final String[] JAVASCRIPT_FILES = new String[] {
    "debug-settings.js",
    "qx/core/Bootstrap.js",
    "qx/lang/Core.js",
    "qx/core/Setting.js",
    "qx/lang/Array.js",
    "qx/core/Variant.js",
    "org/eclipse/rwt/Client.js",
    "qx/lang/Object.js",
    "qx/Class.js",
    "qx/Mixin.js",
    "qx/core/MUserData.js",
    "qx/core/LegacyProperty.js",
    "qx/core/Property.js",
    "qx/lang/String.js",
    "qx/core/Object.js",
    "qx/lang/Function.js",
    "qx/bom/Viewport.js",
    "qx/core/Target.js",
    "qx/event/type/Event.js",
    "qx/event/type/DataEvent.js",
    "qx/event/type/ChangeEvent.js",
    "qx/client/Timer.js",
    "qx/html/String.js",
    "qx/dom/String.js",
    "qx/html/Entity.js",
    "qx/html/EventRegistration.js",
    "qx/util/manager/MConnectedObject.js",
    "org/eclipse/rwt/HtmlUtil.js",
    "org/eclipse/rwt/protocol/AdapterRegistry.js",
    "org/eclipse/rwt/protocol/ObjectManager.js",
    "org/eclipse/rwt/protocol/AdapterUtil.js",
    "org/eclipse/rwt/protocol/EncodingUtil.js",
    "org/eclipse/rwt/Display.js",
    "org/eclipse/rwt/DisplayAdapter.js",
    "qx/ui/core/Widget.js",
    "org/eclipse/rwt/WidgetRenderAdapter.js",
    "qx/html/Dimension.js",
    "qx/html/Style.js",
    "qx/html/Scroll.js",
    "qx/html/StyleSheet.js",
    "qx/ui/core/Parent.js",
    "qx/event/type/FocusEvent.js",
    "org/eclipse/rwt/EventHandler.js",
    "qx/dom/Node.js",
    "org/eclipse/rwt/EventHandlerUtil.js",
    "qx/event/type/DomEvent.js",
    "qx/event/type/KeyEvent.js",
    "qx/event/type/MouseEvent.js",
    "qx/util/manager/Object.js",
    "qx/ui/embed/IframeManager.js",
    "qx/ui/layout/CanvasLayout.js",
    "org/eclipse/swt/widgets/CoolBar.js",
    "org/eclipse/swt/widgets/CoolBarAdapter.js",
    "qx/ui/layout/impl/LayoutImpl.js",
    "qx/lang/Number.js",
    "qx/ui/layout/impl/CanvasLayoutImpl.js",
    "qx/ui/core/ClientDocument.js",
    "qx/ui/basic/Terminator.js",
    "qx/ui/core/ClientDocumentBlocker.js",
    "qx/theme/manager/Appearance.js",
    "qx/util/manager/Value.js",
    "qx/util/ColorUtil.js",
    "org/eclipse/rwt/Border.js",
    "qx/ui/core/Font.js",
    "qx/io/Alias.js",
    "qx/event/handler/FocusHandler.js",
    "qx/bom/element/Location.js",
    "qx/bom/element/Style.js",
    "qx/bom/element/BoxSizing.js",
    "qx/bom/Document.js",
    "qx/bom/element/Overflow.js",
    "qx/io/image/Manager.js",
    "qx/html/Offset.js",
    "qx/html/ScrollIntoView.js",
    "qx/ui/layout/BoxLayout.js",
    "qx/ui/layout/impl/VerticalBoxLayoutImpl.js",
    "qx/util/Validation.js",
    "qx/ui/layout/impl/HorizontalBoxLayoutImpl.js",
    "qx/ui/basic/Atom.js",
    "org/eclipse/swt/widgets/LabelAdapter.js",
    "qx/ui/basic/Label.js",
    "qx/ui/basic/Image.js",
    "qx/io/image/PreloaderManager.js",
    "qx/io/image/Preloader.js",
    "qx/ui/form/ListItem.js",
    "qx/constant/Layout.js",
    "qx/constant/Style.js",
    "qx/io/remote/AbstractRemoteTransport.js",
    "qx/ui/layout/HorizontalBoxLayout.js",
    "qx/ui/form/Spinner.js",
    "qx/ui/form/TextField.js",
    "qx/ui/layout/VerticalBoxLayout.js",
    "qx/ui/form/Button.js",
    "qx/util/range/Range.js",
    "qx/ui/pageview/AbstractPageView.js",
    "qx/ui/pageview/tabview/TabView.js",
    "qx/ui/pageview/AbstractBar.js",
    "qx/ui/selection/RadioManager.js",
    "qx/ui/pageview/tabview/Bar.js",
    "qx/ui/pageview/AbstractPane.js",
    "qx/ui/pageview/tabview/Pane.js",
    "qx/ui/popup/Popup.js",
    "qx/ui/popup/PopupManager.js",
    "qx/ui/selection/SelectionManager.js",
    "qx/ui/selection/Selection.js",
    "org/eclipse/swt/widgets/AbstractSlider.js",
    "org/eclipse/rwt/widgets/ScrollBar.js",
    "qx/io/image/PreloaderSystem.js",
    "qx/io/remote/RequestQueue.js",
    "qx/io/remote/Exchange.js",
    "qx/io/remote/Response.js",
    "qx/util/Mime.js",
    "qx/io/remote/XmlHttpTransport.js",
    "qx/net/HttpRequest.js",
    "qx/html/Iframe.js",
    "qx/net/Http.js",
    "qx/io/remote/Request.js",
    "qx/ui/popup/PopupAtom.js",
    "qx/ui/popup/ToolTip.js",
    "qx/ui/popup/ToolTipManager.js",
    "qx/html/Window.js",
    "qx/client/History.js",
    "qx/event/handler/DragAndDropHandler.js",
    "qx/event/type/DragEvent.js",
    "qx/ui/embed/HtmlEmbed.js",
    "qx/ui/embed/Iframe.js",
    "qx/ui/pageview/AbstractButton.js",
    "qx/ui/groupbox/GroupBox.js",
    "qx/ui/resizer/MResizable.js",
    "qx/ui/resizer/ResizablePopup.js",
    "qx/ui/window/Window.js",
    "qx/ui/basic/HorizontalSpacer.js",
    "qx/ui/window/Manager.js",
    "qx/ui/menu/Separator.js",
    "qx/ui/pageview/AbstractPage.js",
    "qx/ui/pageview/tabview/Page.js",
    "qx/ui/pageview/tabview/Button.js",
    "org/eclipse/rwt/ErrorHandler.js",
    "org/eclipse/swt/LabelUtil.js",
    "org/eclipse/rwt/widgets/TreeRowContainer.js",
    "org/eclipse/rwt/TreeRowContainerWrapper.js",
    "org/eclipse/rwt/TreeUtil.js",
    "org/eclipse/rwt/widgets/TreeRow.js",
    "org/eclipse/rwt/AsyncKeyEventUtil.js",
    "org/eclipse/swt/Request.js",
    "org/eclipse/rwt/widgets/Menu.js",
    "org/eclipse/rwt/widgets/MenuAdapter.js",
    "org/eclipse/swt/EventUtil.js",
    "org/eclipse/rwt/FadeAnimationMixin.js",
    "org/eclipse/rwt/AnimationRenderer.js",
    "org/eclipse/rwt/Animation.js",
    "org/eclipse/rwt/widgets/WidgetToolTip.js",
    "org/eclipse/rwt/SyncKeyEventUtil.js",
    "org/eclipse/rwt/GraphicsMixin.js",
    "org/eclipse/rwt/GraphicsUtil.js",
    "org/eclipse/rwt/VML.js",
    "org/eclipse/rwt/SVG.js",
    "org/eclipse/swt/WidgetUtil.js",
    "org/eclipse/swt/theme/ThemeStore.js",
    "org/eclipse/rwt/widgets/MultiCellWidget.js",
    "org/eclipse/rwt/DomEventPatch.js",
    "org/eclipse/rwt/MenuManager.js",
    "org/eclipse/rwt/widgets/MenuItem.js",
    "org/eclipse/rwt/widgets/MenuItemAdapter.js",
    "org/eclipse/rwt/RadioButtonUtil.js",
    "org/eclipse/rwt/widgets/MenuBar.js",
    "org/eclipse/rwt/DNDSupport.js",
    "org/eclipse/rwt/DragSourceAdapter.js",
    "org/eclipse/swt/theme/ThemeValues.js",
    "org/eclipse/rwt/widgets/Tree.js",
    "org/eclipse/rwt/widgets/TreeAdapter.js",
    "org/eclipse/rwt/widgets/TreeItem.js",
    "org/eclipse/rwt/widgets/TreeItemAdapter.js",
    "org/eclipse/rwt/TreeDNDFeedback.js",
    "org/eclipse/swt/widgets/TableCellToolTip.js",
    "org/eclipse/rwt/widgets/TableHeader.js",
    "org/eclipse/swt/widgets/TableColumn.js",
    "org/eclipse/swt/widgets/TableColumnAdapter.js",
    "org/eclipse/swt/browser/Browser.js",
    "org/eclipse/swt/browser/BrowserAdapter.js",
    "org/eclipse/rwt/widgets/ExternalBrowser.js",
    "org/eclipse/rwt/widgets/ExternalBrowserAdapter.js",
    "org/eclipse/swt/FontSizeCalculation.js",
    "org/eclipse/rwt/widgets/BasicButton.js",
    "org/eclipse/rwt/widgets/ToolItem.js",
    "org/eclipse/swt/widgets/Group.js",
    "org/eclipse/swt/widgets/GroupAdapter.js",
    "org/eclipse/swt/widgets/Shell.js",
    "org/eclipse/swt/widgets/ShellAdapter.js",
    "org/eclipse/swt/widgets/ProgressBar.js",
    "org/eclipse/swt/widgets/ProgressBarAdapter.js",
    "org/eclipse/swt/widgets/Link.js",
    "org/eclipse/swt/widgets/LinkAdapter.js",
    "org/eclipse/swt/widgets/Scrollable.js",
    "org/eclipse/swt/custom/ScrolledComposite.js",
    "org/eclipse/swt/custom/ScrolledCompositeAdapter.js",
    "org/eclipse/rwt/widgets/ToolBar.js",
    "org/eclipse/rwt/widgets/ToolBarAdapter.js",
    "org/eclipse/rwt/widgets/ToolItemAdapter.js",
    "org/eclipse/swt/TextUtil.js",
    "org/eclipse/swt/widgets/Scale.js",
    "org/eclipse/swt/widgets/ScaleAdapter.js",
    "org/eclipse/rwt/widgets/ToolSeparator.js",
    "org/eclipse/swt/theme/BorderDefinitions.js",
    "org/eclipse/rwt/widgets/BasicList.js",
    "org/eclipse/swt/widgets/Combo.js",
    "org/eclipse/swt/widgets/ComboAdapter.js",
    "org/eclipse/rwt/FocusIndicator.js",
    "org/eclipse/swt/CLabelUtil.js",
    "org/eclipse/swt/custom/CLabelAdapter.js",
    "org/eclipse/swt/graphics/GCAdapter.js",
    "org/eclipse/swt/graphics/GC.js",
    "org/eclipse/rwt/VMLCanvas.js",
    "org/eclipse/swt/widgets/CompositeAdapter.js",
    "org/eclipse/swt/widgets/Composite.js",
    "org/eclipse/swt/widgets/Sash.js",
    "org/eclipse/swt/widgets/SashAdapter.js",
    "org/eclipse/swt/widgets/CanvasAdapter.js",
    "org/eclipse/swt/widgets/List.js",
    "org/eclipse/swt/widgets/ListAdapter.js",
    "org/eclipse/swt/TabUtil.js",
    "org/eclipse/swt/widgets/TabFolderAdapter.js",
    "org/eclipse/swt/widgets/TabItemAdapter.js",
    "org/eclipse/swt/widgets/Calendar.js",
    "org/eclipse/swt/widgets/CoolItem.js",
    "org/eclipse/swt/widgets/CoolItemAdapter.js",
    "org/eclipse/rwt/widgets/Button.js",
    "org/eclipse/rwt/widgets/ButtonAdapter.js",
    "org/eclipse/rwt/widgets/FileUpload.js",
    "org/eclipse/rwt/widgets/FileUploadAdapter.js",
    "org/eclipse/swt/widgets/Slider.js",
    "org/eclipse/swt/widgets/SliderAdapter.js",
    "org/eclipse/swt/widgets/Spinner.js",
    "org/eclipse/swt/widgets/SpinnerAdapter.js",
    "org/eclipse/swt/widgets/DateTimeTime.js",
    "org/eclipse/swt/widgets/DateTimeDate.js",
    "org/eclipse/swt/widgets/DateTimeCalendar.js",
    "org/eclipse/swt/widgets/DateTimeAdapter.js",
    "org/eclipse/swt/custom/CTabItem.js",
    "org/eclipse/swt/custom/CTabItemAdapter.js",
    "org/eclipse/swt/custom/CTabFolder.js",
    "org/eclipse/swt/custom/CTabFolderAdapter.js",
    "org/eclipse/swt/widgets/ExpandItem.js",
    "org/eclipse/swt/widgets/ExpandItemAdapter.js",
    "org/eclipse/swt/widgets/ExpandBar.js",
    "org/eclipse/swt/widgets/ExpandBarAdapter.js",
    "org/eclipse/rwt/widgets/Text.js",
    "org/eclipse/rwt/widgets/TextAdapter.js",
    "org/eclipse/rwt/KeyEventUtil.js",
    "org/eclipse/swt/widgets/Separator.js",
    "org/eclipse/swt/widgets/SeparatorAdapter.js",
    "org/eclipse/rwt/widgets/ControlDecorator.js",
    "org/eclipse/rwt/widgets/ControlDecoratorAdapter.js",
    "org/eclipse/rwt/MobileWebkitSupport.js",
    "org/eclipse/swt/widgets/ToolTip.js",
    "org/eclipse/swt/widgets/ToolTipAdapter.js",
    "org/eclipse/swt/WidgetManager.js",
    "org/eclipse/rwt/protocol/Processor.js",
    "org/eclipse/rwt/UICallBack.js",
    "org/eclipse/rwt/UICallBackAdapter.js",
    "org/eclipse/rwt/System.js"
  };

  private static final String[] WIDGET_IMAGES = new String[] {
    "resource/static/image/blank.gif",
    "resource/static/image/dotted_white.gif",
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

  private final IResourceManager resourceManager;
  private final ThemeManager themeManager;

  public ClientResources( IResourceManager resourceManager, ThemeManager themeManager ) {
    this.resourceManager = resourceManager;
    this.themeManager = themeManager;
  }

  public void registerResources() {
    try {
      registerTextResource( "resource/static/html/blank.html" );
      registerJavascriptFiles();
      registerThemeResources();
      registerWidgetImages();
      registerContributions();
    } catch( IOException ioe ) {
      throw new RuntimeException( "Failed to register resources.", ioe );
    }
  }

  private void registerJavascriptFiles() throws IOException {
    ContentBuffer contentBuffer = new ContentBuffer();
    String appearanceCode = themeManager.createQxAppearanceTheme();
    String json2Code = readResourceContent( JSON_JS );
    if( SystemProps.isDevelopmentMode() ) {
      for( int i = 0; i < JAVASCRIPT_FILES.length; i++ ) {
        append( contentBuffer, JAVASCRIPT_FILES[ i ] );
      }
    } else {
      append( contentBuffer, CLIENT_JS );
      json2Code = compress( json2Code );
      appearanceCode = compress( appearanceCode );
    }
    contentBuffer.append( json2Code.getBytes( HTTP.CHARSET_UTF_8 ) );
    contentBuffer.append( appearanceCode.getBytes( HTTP.CHARSET_UTF_8 ) );
    registerJavascriptResource( contentBuffer, "rap-client.js" );
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
    for( int i = 0; i < WIDGET_IMAGES.length; i++ ) {
      String resourcePath = WIDGET_IMAGES[ i ];
      InputStream inputStream = openResourceStream( resourcePath );
      resourceManager.register( resourcePath, inputStream );
      inputStream.close();
    }
  }

  private void registerContributions() throws IOException {
    IResource[] resources = RWTFactory.getResourceRegistry().get();
    for( int i = 0; i < resources.length; i++ ) {
      IResource resource = resources[ i ];
      if( !resource.isExternal() ) {
        String charset = resource.getCharset();
        RegisterOptions options = resource.getOptions();
        if( options == null ) {
          options = RegisterOptions.NONE;
        }
        String location = resource.getLocation();
        InputStream inputStream = resource.getLoader().getResourceAsStream( location );
        if( charset == null ) {
          resourceManager.register( location, inputStream );
        } else {
          resourceManager.register( location, inputStream, charset, options );
        }
        inputStream.close();
      }
    }
  }

  private void registerTextResource( String name ) throws IOException {
    InputStream inputStream = getClass().getClassLoader().getResourceAsStream( name );
    try {
      resourceManager.register( name, inputStream, HTTP.CHARSET_UTF_8, RegisterOptions.NONE );
    } finally {
      inputStream.close();
    }
  }

  private void registerJavascriptResource( ContentBuffer buffer, String name )
    throws IOException
  {
    InputStream inputStream = buffer.getContentAsStream();
    try {
      resourceManager.register( name, inputStream, HTTP.CHARSET_UTF_8, RegisterOptions.VERSION );
      String location = resourceManager.getLocation( name );
      RWTFactory.getStartupPage().getConfigurer().addJsLibrary( location );
    } finally {
      inputStream.close();
    }
  }

  private static String compress( String code ) throws IOException {
    JSFile jsFile = new JSFile( code );
    return jsFile.compress();
  }

  private static String readResourceContent( String location ) throws IOException {
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

  private static InputStream openResourceStream( String name ) {
    InputStream result = ClientResources.class.getClassLoader().getResourceAsStream( name );
    if( result == null ) {
      throw new IllegalArgumentException( "Resource not found: " + name );
    }
    return result;
  }
}
