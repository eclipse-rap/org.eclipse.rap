package org.eclipse.rap.internal.design.example;

import org.eclipse.rwt.RWT;

public class Messages {

  private static final String BUNDLE_NAME 
    = "org.eclipse.rap.internal.design.example.messages"; //$NON-NLS-1$
  public String ConfigurationDialog_Cancel;
  public String ConfigurationDialog_ConfigurationFor;
  public String ConfigurationDialog_Ok;
  public String ConfigurationDialog_ViewMenu;
  public String ConfigurationDialog_VisibleActions;
  public String PerspectiveSwitcherBuilder_Close;
  public String PerspectiveSwitcherBuilder_Other;
  public String ViewStackPresentation_ConfButtonToolTipDisabled;
  public String ViewStackPresentation_ConfButtonToolTipEnabled;
  
  public static Messages get() {
    Object result = RWT.NLS.getISO8859_1Encoded( BUNDLE_NAME, Messages.class );
    return ( Messages )result;
  }

  private Messages() {
    //prevent initialization
  }
}
