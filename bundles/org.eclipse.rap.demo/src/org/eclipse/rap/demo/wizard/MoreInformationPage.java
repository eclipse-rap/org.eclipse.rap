package org.eclipse.rap.demo.wizard;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

/**
 * This page gathers more information about the complaint
 */
class MoreInformationPage extends WizardPage {

  /**
   * MoreInformationPage constructor
   */
  public MoreInformationPage() {
    super( "More Info" );
    setTitle( "More Informations" );
    setMessage( "Please enter your comment", IMessageProvider.WARNING );
    setPageComplete( false );
  }

  /**
   * Creates the controls for this page
   */
  public void createControl( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 1, false ) );
    new Label( composite, SWT.LEFT ).setText( "Please enter your complaints" );
    final Text text = new Text( composite, 
                                SWT.MULTI | SWT.BORDER | SWT.V_SCROLL );
    text.setLayoutData( new GridData( GridData.FILL_BOTH ) );
    text.addModifyListener( new ModifyListener() {

      public void modifyText( ModifyEvent event ) {
        if( text.getText().length() > 0 ) {
          setMessage( "Great!", IMessageProvider.INFORMATION );
          setPageComplete( true );
        } else {
          setMessage( "Please enter your comment", IMessageProvider.WARNING );
          setPageComplete( false );
        }
      }
    } );
    setControl( composite );
  }
}