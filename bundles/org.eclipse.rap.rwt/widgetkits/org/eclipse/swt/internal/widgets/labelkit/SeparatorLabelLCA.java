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
package org.eclipse.swt.internal.widgets.labelkit;

import java.io.IOException;

import org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rap.rwt.internal.protocol.IClientObject;
import org.eclipse.rap.rwt.lifecycle.ControlLCAUtil;
import org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.widgets.Label;


final class SeparatorLabelLCA extends AbstractLabelLCADelegate {

  private static final String TYPE = "rwt.widgets.Separator";
  private static final String[] ALLOWED_STYLES = new String[] {
    "SEPARATOR", "HORIZONTAL", "VERTICAL", "SHADOW_IN", "SHADOW_OUT", "SHADOW_NONE", "BORDER"
  };

  void preserveValues( Label label ) {
    ControlLCAUtil.preserveValues( label );
    WidgetLCAUtil.preserveCustomVariant( label );
  }

  void readData( Label label ) {
    ControlLCAUtil.processEvents( label );
    ControlLCAUtil.processKeyEvents( label );
    ControlLCAUtil.processMenuDetect( label );
    WidgetLCAUtil.processHelp( label );
  }

  void renderInitialization( Label label ) throws IOException {
    IClientObject clientObject = ClientObjectFactory.getClientObject( label );
    clientObject.create( TYPE );
    clientObject.set( "parent", WidgetUtil.getId( label.getParent() ) );
    clientObject.set( "style", WidgetLCAUtil.getStyles( label, ALLOWED_STYLES ) );
  }

  void renderChanges( Label label ) throws IOException {
    ControlLCAUtil.renderChanges( label );
    WidgetLCAUtil.renderCustomVariant( label );
  }
}
