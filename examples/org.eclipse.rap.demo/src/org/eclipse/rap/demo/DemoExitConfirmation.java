/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.demo;

import org.eclipse.rap.ui.branding.IExitConfirmation;


public class DemoExitConfirmation implements IExitConfirmation {

  public boolean showExitConfirmation() {
    return true;
  }

  public String getExitConfirmationText() {
    return "Do you really wanna leave the party?";
  }
}
