/*******************************************************************************
 * Copyright (c) 2012, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.jstest.internal;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rap.rwt.jstest.TestContribution;


public class RWTTestsContribution implements TestContribution {

  private static final String PATH_PREFIX = "/org/eclipse/rwt/test/";

  private static final String[] FILES = new String[] {
    "tests/ClientTest.js",
    "tests/ClientAPITest.js",
    "tests/TestUtilTest.js",
    "tests/ErrorHandlerTest.js",
    "tests/MessageProcessorTest.js",
    "tests/ProtocolWriterTest.js",
    "tests/RequestTest.js",
    "tests/HandlerUtilTest.js",
    "tests/EncodingUtilTest.js",
    "tests/DisplayTest.js",
    "tests/ThemeStoreTest.js",
    "tests/ShellProtocolIntegrationTest.js",
    "tests/StyleTest.js",
    "tests/BorderTest.js",
    "tests/WidgetTest.js",
    "tests/GroupTest.js",
    "tests/SashTest.js",
    "tests/LabelTest.js",
    "tests/LinkTest.js",
    "tests/SeparatorTest.js",
    "tests/CoolItemTest.js",
    "tests/ScaleTest.js",
    "tests/SliderTest.js",
    "tests/SpinnerTest.js",
    "tests/DateTimeDateTest.js",
    "tests/DateTimeTimeTest.js",
    "tests/DateTimeCalendarTest.js",
    "tests/ScrollBarTest.js",
    "tests/ScrolledCompositeTest.js",
    "tests/KeyEventSupportTest.js",
    "tests/MnemonicHandlerTest.js",
    "tests/ListTest.js",
    "tests/ComboTest.js",
    "tests/EventHandlerTest.js",
    "tests/EventUtilTest.js",
    "tests/MobileWebkitSupportTest.js",
    "tests/IFrameTest.js",
    "tests/DNDTest.js",
    "tests/CellRendererRegistryTest.js",
    "tests/TemplateTest.js",
    "tests/GridItemTest.js",
    "tests/GridColumnTest.js",
    "tests/GridRowTest.js",
    "tests/GridRowContainerTest.js",
    "tests/GridTest.js",
    "tests/GridUtilTest.js",
    "tests/AnimationTest.js",
    "tests/VisibilityAnimationMixinTest.js",
    "tests/GCTest.js",
    "tests/GCCanvasTest.js",
    "tests/GCVMLTest.js",
    "tests/ShellTest.js",
    "tests/CompositeTest.js",
    "tests/TextTest.js",
    "tests/ToolBarTest.js",
    "tests/ToolTipConfigTest.js",
    "tests/WidgetToolTipTest.js",
    "tests/MultiCellWidgetTest.js",
    "tests/ButtonTest.js",
    "tests/MenuTest.js",
    "tests/GraphicsMixinTest.js",
    "tests/SVGTest.js",
    "tests/VMLTest.js",
    "tests/ProgressBarTest.js",
    "tests/TabFolderTest.js",
    "tests/CTabFolderTest.js",
    "tests/FileUploadTest.js",
    "tests/ServerPushTest.js",
    "tests/ToolTipTest.js",
    "tests/ExpandBarTest.js",
    "tests/ControlDecoratorTest.js",
    "tests/ExternalBrowserTest.js",
    "tests/JavaScriptExecutorTest.js",
    "tests/UrlLauncherTest.js",
    "tests/JavaScriptLoaderTest.js",
    "tests/BrowserNavigationTest.js",
    "tests/FontSizeCalculationTest.js",
    "tests/MessageTest.js",
    "tests/ClientMessagesTest.js",
    "tests/ConnectionTest.js",
    "tests/RemoteObjectTest.js",
    "tests/RemoteObjectFactoryTest.js",
    "tests/Function_Test.js",
    "tests/EventBinding_Test.js",
    "tests/EventProxy_Test.js",
    "tests/Synchronizer_Test.js",
    "tests/WidgetProxyFactory_Test.js",
    "tests/BrowserTest.js"
  };

  public String getName() {
    return "rwt-tests";
  }

  public String[] getResources() {
    String[] result = new String[ FILES.length ];
    for( int i = 0; i < FILES.length; i++ ) {
      result[ i ] = PATH_PREFIX + FILES[ i ];
    }
    return result;
  }

  public InputStream getResourceAsStream( String resource ) throws IOException {
    return RWTTestsContribution.class.getResourceAsStream( resource );
  }

}
