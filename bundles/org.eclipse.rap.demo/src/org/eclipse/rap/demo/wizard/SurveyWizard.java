package org.eclipse.rap.demo.wizard;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;

public class SurveyWizard extends Wizard {

  public SurveyWizard() {
    // Add the pages
    addPage( new ComplaintsPage() );
    addPage( new MoreInformationPage() );
    addPage( new ThanksPage() );
    setWindowTitle( "RAP Survey Wizard" );
    setNeedsProgressMonitor( true );
  }

  /**
   * Called when user clicks Finish
   * 
   * @return boolean
   */
  public boolean performFinish() {
    // Dismiss the wizard
    try {
      getContainer().run( true, true, new IRunnableWithProgress() {
        public void run( final IProgressMonitor monitor )
          throws InvocationTargetException, InterruptedException
        {
          monitor.setTaskName( "taskName" );
          monitor.beginTask( "Doing silly things...", 100 );
          for( int i = 0; !monitor.isCanceled() && i < 100; i++ ) {
            monitor.worked( 1 );
            Thread.sleep( 100 );
          }
          monitor.done();
        }
      } );
    } catch( InvocationTargetException e ) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch( InterruptedException e ) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return true;
  }
}