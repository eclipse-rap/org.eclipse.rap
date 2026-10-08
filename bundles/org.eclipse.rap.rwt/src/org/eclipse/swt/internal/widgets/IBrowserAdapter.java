/*******************************************************************************
 * Copyright (c) 2002, 2016 Innoopract Informationssysteme GmbH and others.
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
package org.eclipse.swt.internal.widgets;

import org.eclipse.swt.browser.BrowserFunction;

public interface IBrowserAdapter {

  String getText();

  String getExecuteScript();
  void setExecuteResult( boolean executeResult, Object evalResult );
  void setExecutePending( boolean executePending );
  boolean getExecutePending();
  boolean hasUrlChanged();
  void resetUrlChanged();

  BrowserFunction[] getBrowserFunctions();

}
