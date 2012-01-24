package org.eclipse.rap.rwt.jstest.internal;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rap.rwt.jstest.TestContribution;


public class RWTTestsContribution implements TestContribution {

  private static final String PATH_PREFIX = "/org/eclipse/rwt/test/";

  private static final String[] RESOURCE_FILES = new String[] {
    "fixture/RAPRequestPatch.js",
    "fixture/DummyRequest.js",
    "fixture/RAPServer.js",
    "Presenter.js",
    "TestRunner.js",
    "fixture/TestUtil.js",
    "Asserts.js",
    "Startup.js"
  };

  private static final String[] TEST_FILES = new String[] {
    "tests/ClientTest.js",
    "tests/TestUtilTest.js",
    "tests/ProtocolTest.js",
    "tests/AdapterUtilTest.js",
    "tests/EncodingUtilTest.js",
    "tests/DisplayTest.js",
    "tests/ShellProtocolIntegrationTest.js",
    "tests/HtmlUtilTest.js",
    "tests/BorderTest.js",
    "tests/WidgetTest.js",
    "tests/GroupTest.js",
    "tests/SashTest.js",
    "tests/LabelTest.js",
    "tests/LinkTest.js",
    "tests/SeparatorTest.js",
    "tests/CLabelTest.js",
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
    "tests/MobileWebkitSupportTest.js",
    "tests/IFrameTest.js",
    "tests/DNDTest.js",
    "tests/TreeItemTest.js",
    "tests/TableColumnTest.js",
    "tests/TreeRowTest.js",
    "tests/TreeRowContainerTest.js",
    "tests/TreeTest.js",
    "tests/TreeUtilTest.js",
    "tests/AnimationTest.js",
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
    "tests/UICallBackTest.js",
    "tests/SystemTest.js",
    "tests/ToolTipTest.js",
    "tests/ExpandBarTest.js",
    "tests/ControlDecoratorTest.js",
    "tests/ExternalBrowserTest.js",
    "tests/JSExecutorTest.js"
  };

  public String getName() {
    return "rwt-test";
  }

  public String[] getResources() {
    String[] resources = new String[ RESOURCE_FILES.length + TEST_FILES.length ];
    int i = 0;
    for( String resource : RESOURCE_FILES ) {
      resources[ i++ ] = PATH_PREFIX + resource;
    }
    for( String resource : TEST_FILES ) {
      resources[ i++ ] = PATH_PREFIX + resource;
    }
    return resources;
  }

  public InputStream getResourceAsStream( String resource ) throws IOException {
    InputStream resourceAsStream = RWTTestsContribution.class.getResourceAsStream( resource );
    return resourceAsStream;
  }

}
