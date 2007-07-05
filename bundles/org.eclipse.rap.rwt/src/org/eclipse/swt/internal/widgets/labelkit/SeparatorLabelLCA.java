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

package org.eclipse.swt.internal.widgets.labelkit;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.lifecycle.ControlLCAUtil;
import org.eclipse.swt.lifecycle.JSWriter;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Widget;


public class SeparatorLabelLCA extends AbstractLabelLCADelegate {

  private static final String QX_TYPE = "org.eclipse.swt.widgets.Separator";
  static final String TYPE_POOL_ID = SeparatorLabelLCA.class.getName();

  void preserveValues( final Label label ) {
    ControlLCAUtil.preserveValues( label );
  }

  void readData( final Label label ) {
  }

  void renderInitialization( final Label label ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( label );
    Object[] args = new Object[] { getStyle( label ) };
    writer.newWidget( QX_TYPE, args );
    ControlLCAUtil.writeStyleFlags( label );
  }

  void renderChanges( final Label label ) throws IOException {
    ControlLCAUtil.writeChanges( label );
  }

  void renderDispose( final Label label ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( label );
    writer.dispose();
  }

  private static String getStyle( final Widget widget ) {
    StringBuffer result = new StringBuffer();
    int style = widget.getStyle();
    if( ( style & SWT.HORIZONTAL ) != 0 ) {
      result.append( "HORIZONTAL|" );
    } else {
      result.append( "VERTICAL|" );
    }
    String shadow = "SHADOW_OUT";
    if( ( style & SWT.SHADOW_IN ) != 0 ) {
      shadow = "SHADOW_IN|";
    } else if( ( style & SWT.SHADOW_NONE ) != 0 ) {
      shadow = "SHADOW_NONE|";
    }
    result.append( shadow );
    return result.toString();
  }

  void createResetHandlerCalls( final String typePoolId ) throws IOException {
    ControlLCAUtil.resetChanges();
    ControlLCAUtil.resetStyleFlags();
  }

  String getTypePoolId( final Label label ) throws IOException {
    return TYPE_POOL_ID;
  }
}
