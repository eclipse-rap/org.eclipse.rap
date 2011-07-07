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
package org.eclipse.swt.internal.widgets.labelkit;

import java.io.IOException;

import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;


final class SeparatorLabelLCA extends AbstractLabelLCADelegate {

  void preserveValues( final Label label ) {
    ControlLCAUtil.preserveValues( label );
    WidgetLCAUtil.preserveCustomVariant( label );
  }

  void readData( final Label label ) {
    ControlLCAUtil.processMouseEvents( label );
    ControlLCAUtil.processKeyEvents( label );
    ControlLCAUtil.processMenuDetect( label );
    WidgetLCAUtil.processHelp( label );
  }

  void renderInitialization( final Label label ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( label );
    writer.newWidget( "org.eclipse.swt.widgets.Separator" );
    ControlLCAUtil.writeStyleFlags( label );
    WidgetLCAUtil.writeStyleFlag( label, SWT.SEPARATOR, "SEPARATOR" );
    writeOrientation( label );
    writeLineStyle( label );
  }

  void renderChanges( final Label label ) throws IOException {
    ControlLCAUtil.writeChanges( label );
    WidgetLCAUtil.writeCustomVariant( label );
  }

  private static void writeOrientation( final Label label ) throws IOException {
    int style = label.getStyle();
    String orient = ( style & SWT.VERTICAL ) != 0 ? "vertical" : "horizontal";
    JSWriter writer = JSWriter.getWriterFor( label );
    writer.set( "lineOrientation", orient );
  }

  private static void writeLineStyle( final Label label ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( label );
    if( ( label.getStyle() & SWT.SHADOW_IN ) != 0 ) {
      writer.call( "setLineStyle", new Object[] { "rwt_SHADOW_IN" } );
    } else if( ( label.getStyle() & SWT.SHADOW_OUT ) != 0 ) {
      writer.call( "setLineStyle", new Object[] { "rwt_SHADOW_OUT" } );
    } else {
      writer.call( "setLineStyle", new Object[] { "rwt_SHADOW_NONE" } );
    }
  }
}
