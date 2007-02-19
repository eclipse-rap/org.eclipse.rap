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

package org.eclipse.rap.rwt.internal.widgets.labelkit;

import java.io.IOException;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.lifecycle.ControlLCAUtil;
import org.eclipse.rap.rwt.lifecycle.JSWriter;
import org.eclipse.rap.rwt.widgets.Label;
import org.eclipse.rap.rwt.widgets.Widget;


public class SeparatorLabelLCA extends AbstractLabelLCADelegate {

  void preserveValues( final Label label ) {
    ControlLCAUtil.preserveValues( label );
  }

  void readData( final Label label ) {
  }
  
  void renderInitialization( final Label label ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( label );
    Object[] args = new Object[] { getStyle( label ) };
    writer.newWidget( "org.eclipse.rap.rwt.widgets.Separator", args );
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
    if( ( style & RWT.HORIZONTAL ) != 0 ) {
      result.append( "HORIZONTAL|" );
    } else {
      result.append( "VERTICAL|" );
    }
    String shadow = "SHADOW_OUT";
    if( ( style & RWT.SHADOW_IN ) != 0 ) {
      shadow = "SHADOW_IN|";
    } else if( ( style & RWT.SHADOW_NONE ) != 0 ) {
      shadow = "SHADOW_NONE|";
    } 
    result.append( shadow );
    return result.toString();
  }
}
