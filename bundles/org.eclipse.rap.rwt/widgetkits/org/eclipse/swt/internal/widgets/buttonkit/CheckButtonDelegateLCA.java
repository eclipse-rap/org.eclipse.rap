/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.buttonkit;

import java.io.IOException;

import org.eclipse.rap.rwt.lifecycle.ControlLCAUtil;
import org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.swt.widgets.Button;


final class CheckButtonDelegateLCA extends ButtonDelegateLCA {

  void preserveValues( Button button ) {
    ButtonLCAUtil.preserveValues( button );
  }

  void readData( Button button ) {
    ButtonLCAUtil.readSelection( button );
    ControlLCAUtil.processSelection( button, null, true );
    ControlLCAUtil.processEvents( button );
    ControlLCAUtil.processKeyEvents( button );
    ControlLCAUtil.processMenuDetect( button );
    WidgetLCAUtil.processHelp( button );
  }

  void renderInitialization( Button button ) throws IOException {
    ButtonLCAUtil.renderInitialization( button );
  }

  void renderChanges( Button button ) throws IOException {
    ButtonLCAUtil.renderChanges( button );
  }
}
