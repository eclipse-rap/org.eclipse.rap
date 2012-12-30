/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.controlkit;

import java.io.IOException;

import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.lifecycle.ControlLCAUtil;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;


public class ControlLCA extends AbstractWidgetLCA {

  public void preserveValues( Widget widget ) {
    ControlLCAUtil.preserveValues( ( Control )widget );
  }

  public void readData( Widget widget ) {
    // do nothing
  }

  public void renderInitialization( Widget widget ) throws IOException {
    // do nothing
  }

  public void renderChanges( Widget widget ) throws IOException {
    // do nothing
  }

}
