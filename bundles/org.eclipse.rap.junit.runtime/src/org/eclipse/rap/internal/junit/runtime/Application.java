package org.eclipse.rap.internal.junit.runtime;

import java.util.StringTokenizer;

import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * This class controls all aspects of the application's execution and is
 * contributed through the plugin.xml.
 */
public class Application implements IEntryPoint {

  public Display createUI() {
    final Display display = PlatformUI.createDisplay();
    UICallBack.activate( Application.class.getName() );
    RWT.getLifeCycle().addPhaseListener( new PhaseListener() {
      private static final long serialVersionUID = 1L;
      public void afterPhase( final PhaseEvent event ) {
        final String[] programArguments = getProgramArguments();
        Job job = new Job( "RAPJUnitTestRunner" ) {
          protected IStatus run( final IProgressMonitor monitor ) {
            UICallBack.runNonUIThreadWithFakeContext( display, new Runnable() {
              public void run() {
                RemotePluginTestRunner.main( programArguments );
              }
            } );
            return Status.OK_STATUS;
          }
          
        };
        job.schedule();
        RWT.getLifeCycle().removePhaseListener( this );
      }
      public void beforePhase( final PhaseEvent event ) {
      }
      public PhaseId getPhaseId() {
        return PhaseId.RENDER;
      }
    } );
    
    return display;
  }

  private String[] getProgramArguments() {
    String eclipseCommands = System.getProperty( "eclipse.commands" );
    StringTokenizer tokenizer = new StringTokenizer( eclipseCommands, "\n" );
    String[] arx = new String[ tokenizer.countTokens() ];
    for( int i = 0; i < arx.length; i++ ) {
      arx[ i ] = tokenizer.nextToken().trim();
    }
    return arx;
  }
}
