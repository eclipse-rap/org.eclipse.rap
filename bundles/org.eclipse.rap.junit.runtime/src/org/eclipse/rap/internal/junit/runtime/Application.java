package org.eclipse.rap.internal.junit.runtime;

import org.eclipse.core.runtime.Platform;
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.internal.lifecycle.EntryPointManager;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.rwt.lifecycle.UICallBack;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.testing.ITestHarness;
import org.eclipse.ui.testing.TestableObject;

/**
 * This class controls all aspects of the application's execution and is
 * contributed through the plugin.xml.
 */
public class Application implements IEntryPoint, ITestHarness {

  private TestableObject fTestableObject;

  public int createUI() {
    UICallBack.activate( Application.class.getName() );

    fTestableObject = PlatformUI.getTestableObject();
    fTestableObject.setTestHarness( this );
    return createAndRunWorkbench();
  }

  private int createAndRunWorkbench() {
    int result;
    if( getEntryPoint() != null ) {
      result = EntryPointManager.createUI( getEntryPoint() );
    } else {
      result = createAndRunEmptyWorkbench();
    }
    return result;
  }

  private int createAndRunEmptyWorkbench() {
    Display display = PlatformUI.createDisplay();
    WorkbenchAdvisor workbenchAdvisor = new WorkbenchAdvisor(){
      public String getInitialWindowPerspectiveId() {
        return "org.eclipse.rap.junit.runtime.emptyPerspective";
      }
    };
    return PlatformUI.createAndRunWorkbench( display, workbenchAdvisor );
  }

  private String getEntryPoint() {
    String parameter = RWT.getRequest().getParameter( "testentrypoint" );
    String result = null;
    if( !"rapjunit".equals( parameter ) && !"".equals( parameter ) ) {
      result = parameter;
    }
    return result;
  }

  public void runTests() {
    fTestableObject.testingStarting();
    fTestableObject.runTest( new Runnable() {
      public void run() {
        RemotePluginTestRunner.main( Platform.getCommandLineArgs() );
      }
    } );
    fTestableObject.testingFinished();
  }
}
