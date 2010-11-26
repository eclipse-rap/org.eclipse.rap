/*******************************************************************************
 * Copyright (c) 2002, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets;

import org.eclipse.swt.browser.BrowserFunction;

public interface IBrowserAdapter {

  String getText();

  String getExecuteScript();
  void setExecuteResult( boolean executeResult, Object evalResult );
  void setExecutePending( boolean executePending );
  boolean getExecutePending();

  BrowserFunction[] getBrowserFunctions();

}
