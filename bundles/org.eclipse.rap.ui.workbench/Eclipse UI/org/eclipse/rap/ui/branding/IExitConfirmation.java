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

package org.eclipse.rap.ui.branding;

/**
 * Implementations of this interface can be registered with the branding
 * extension point in order to control the confirmation dialog that can be shown
 * whenever the user tries to close the browser window or tab or to navigate to
 * another URL.
 */
public interface IExitConfirmation {

  /**
   * Indictes whether an exit confirmation dialog should be shown.
   * <p>
   * The exit confirmation dialog is shown whenever the user tries to close the
   * browser window or tab or to navigate to another URL. From within this
   * dialog, the user can cancel the operation.
   * </p>
   * </p>
   * Note that this is a <em>hint</em>. Currently, only Internet Explorer and
   * browsers of the Mozilla family respect this setting.
   * </p>
   * 
   * @return <code>true</code> if an exit confirmation dialog should be shown
   * @see #getExitMessage()
   */
  // keep Javadoc in sync with AbstractBranding
  public abstract boolean showExitDialog();

  /**
   * Returns the message to display in the exit confirmation dialog. Note that
   * <code>showExitDialog()</code> must return <code>true</code> to enable this
   * message.
   * 
   * @return the message to be displayed in the exit confirmation dialog
   * @see #showExitDialog()
   */
  // keep Javadoc in sync with AbstractBranding
  public abstract String getExitMessage();
}
