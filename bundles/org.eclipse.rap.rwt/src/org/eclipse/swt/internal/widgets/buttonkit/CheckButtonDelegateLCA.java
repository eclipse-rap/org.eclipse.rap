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

import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.widgets.Button;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.renderProperty;


final class CheckButtonDelegateLCA extends ButtonDelegateLCA {

  static final String PROP_GRAYED = "grayed";

  void preserveValues( Button button ) {
    ButtonLCAUtil.preserveValues( button );
    preserveProperty( button, PROP_GRAYED, Boolean.valueOf( button.getGrayed() ) );
  }

  void readData( Button button ) {
    ButtonLCAUtil.readSelection( button );
    ControlLCAUtil.processSelection( button, null, true );
    ControlLCAUtil.processMouseEvents( button );
    ControlLCAUtil.processKeyEvents( button );
    ControlLCAUtil.processMenuDetect( button );
    WidgetLCAUtil.processHelp( button );
  }

  void renderInitialization( Button button ) throws IOException {
    ButtonLCAUtil.renderInitialization( button );
  }

  void renderChanges( Button button ) throws IOException {
    ButtonLCAUtil.renderChanges( button );
    renderProperty( button, PROP_GRAYED, Boolean.valueOf( button.getGrayed() ), Boolean.FALSE );
  }
}
