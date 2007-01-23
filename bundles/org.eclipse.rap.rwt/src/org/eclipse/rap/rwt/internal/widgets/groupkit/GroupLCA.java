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

package org.eclipse.rap.rwt.internal.widgets.groupkit;

import java.io.IOException;
import org.eclipse.rap.rwt.internal.widgets.ControlLCAUtil;
import org.eclipse.rap.rwt.internal.widgets.IWidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.Group;
import org.eclipse.rap.rwt.widgets.Widget;

public class GroupLCA extends AbstractWidgetLCA {
  
  private static final String PROP_TEXT = "text";

  public void preserveValues( final Widget widget ) {
    Group group = ( Group )widget;
    ControlLCAUtil.preserveValues( group );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( group );
    adapter.preserve( PROP_TEXT, group.getText() );
  }
  
  public void readData( final Widget widget ) {
  }
  
  public void renderInitialization( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.newWidget( "qx.ui.groupbox.GroupBox" );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    Group group = ( Group )widget;
    ControlLCAUtil.writeChanges( group );
    JSWriter writer = JSWriter.getWriterFor( group );
    writer.set( PROP_TEXT, "legend", group.getText(), "" );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }
}
