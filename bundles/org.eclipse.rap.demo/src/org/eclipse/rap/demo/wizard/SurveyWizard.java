package org.eclipse.rap.demo.wizard;

import org.eclipse.jface.wizard.Wizard;

public class SurveyWizard extends Wizard {

  public SurveyWizard() {
    // Add the pages
    addPage( new ComplaintsPage() );
    addPage( new MoreInformationPage() );
    addPage( new ThanksPage() );
    setWindowTitle( "RAP Survey Wizard" );
  }

  /**
   * Called when user clicks Finish
   * 
   * @return boolean
   */
  public boolean performFinish() {
    // Dismiss the wizard
    return true;
  }
}