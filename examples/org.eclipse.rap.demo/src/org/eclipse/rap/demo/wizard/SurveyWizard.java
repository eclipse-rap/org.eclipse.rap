/*******************************************************************************
 * Copyright (c) 2008, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
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

  public boolean canFinish() {
    return    getContainer() != null
           && getContainer().getCurrentPage() instanceof ThanksPage;
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