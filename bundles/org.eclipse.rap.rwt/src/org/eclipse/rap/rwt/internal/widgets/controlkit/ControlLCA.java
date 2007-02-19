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

package org.eclipse.rap.rwt.internal.widgets.controlkit;

import java.io.IOException;
import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.lifecycle.ControlLCAUtil;
import org.eclipse.rap.rwt.widgets.Control;
import org.eclipse.rap.rwt.widgets.Widget;


public class ControlLCA extends AbstractWidgetLCA {

  public void preserveValues( final Widget widget ) {
    ControlLCAUtil.preserveValues( ( Control )widget );
  }
  
  public void readData( final Widget widget ) {
    // do nothing
  }
  
  public void renderInitialization( final Widget widget ) throws IOException {
    // do nothing
  }

  public void renderChanges( final Widget widget ) throws IOException {
    // do nothing
  }

  public void renderDispose( final Widget widget ) throws IOException {
    // do nothing
  }
}
