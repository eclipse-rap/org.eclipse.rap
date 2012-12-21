/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.rap.rwt.jstest.TestContribution;


public class RWTTestsContribution implements TestContribution {

  private static final String PATH_PREFIX = "/org/eclipse/rwt/test/";

  private static final String[] RESOURCE_FILES = new String[] {
    "fixture/FakeServer.js",
    "fixture/NativeRequestMock.js",
    "fixture/Message.js",
    "Presenter.js",
    "TestRunner.js",
    "fixture/TestUtil.js",
    "Asserts.js",
    "Startup.js"
  };

  private static final String[] TEST_FILES = new String[] {
    "tests/ClientTest.js",
    "tests/ClientAPITest.js",
    "tests/TestUtilTest.js",
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
    "tests/ListTest.js",
    "tests/ComboTest.js",
    "tests/EventHandlerTest.js",
    "tests/EventUtilTest.js",
    "tests/MobileWebkitSupportTest.js",
    "tests/IFrameTest.js",
    "tests/DNDTest.js",
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
    "tests/BrowserTest.js",
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
    "tests/ServerTest.js",
    "tests/RemoteObjectTest.js",
    "tests/RemoteObjectFactoryTest.js"
  };

  public String getName() {
    return "rwt-test";
  }

  public String[] getResources() {
    List<String> result = new ArrayList<String>();
    for( String resource : RESOURCE_FILES ) {
      result.add( PATH_PREFIX + resource );
    }
    for( String resource : TEST_FILES ) {
      result.add( PATH_PREFIX + resource );
    }
    return toArray( result );
  }

  public InputStream getResourceAsStream( String resource ) throws IOException {
    return RWTTestsContribution.class.getResourceAsStream( resource );
  }

  private static String[] toArray( List<String> list ) {
    String[] array = new String[ list.size() ];
    list.toArray( array );
    return array;
  }

}
