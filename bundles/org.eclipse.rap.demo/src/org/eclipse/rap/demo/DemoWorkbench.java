/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.demo;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.swt.lifecycle.IEntryPoint;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import com.w4t.engine.service.ContextProvider;


public class DemoWorkbench implements IEntryPoint {

  public Display createUI() {
    final Display result = PlatformUI.createDisplay();
    PlatformUI.createAndRunWorkbench( result, new DemoWorbenchAdvisor() );
    return result;
  }
}
