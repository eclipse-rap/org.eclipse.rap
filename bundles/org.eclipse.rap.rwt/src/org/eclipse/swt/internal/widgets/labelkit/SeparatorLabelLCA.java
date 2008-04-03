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

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;


public class SeparatorLabelLCA extends AbstractLabelLCADelegate {

  private static final String QX_TYPE = "org.eclipse.swt.widgets.Separator";
  private static final String JS_FUNC_ADD_LINE_STYLE = "addLineStyle";
  private static final String JS_FUNC_REMOVE_LINE_STYLE = "removeLineStyle";
  private static final String JS_FIELD_LINE_ORIENTATION = "lineOrientation";
  private static final Object[] PARAM_SHADOW_IN
    = new Object[] { JSConst.JS_STYLE_FLAG_SHADOW_IN };
  private static final Object[] PARAM_SHADOW_OUT
    = new Object[] { JSConst.JS_STYLE_FLAG_SHADOW_OUT };
  static final String TYPE_POOL_ID = SeparatorLabelLCA.class.getName();

  void preserveValues( final Label label ) {
    ControlLCAUtil.preserveValues( label );
  }

  void readData( final Label label ) {
  }

  void renderInitialization( final Label label ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( label );
    writer.newWidget( QX_TYPE );
    WidgetLCAUtil.writeCustomVariant( label );
    ControlLCAUtil.writeStyleFlags( label );
    writeStyle( label );
  }

  void renderChanges( final Label label ) throws IOException {
    ControlLCAUtil.writeChanges( label );
  }

  void renderDispose( final Label label ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( label );
    writer.dispose();
  }

  void createResetHandlerCalls( final String typePoolId ) throws IOException {
    ControlLCAUtil.resetChanges();
    ControlLCAUtil.resetStyleFlags();
    resetStyle();
  }

  String getTypePoolId( final Label label ) {
    return TYPE_POOL_ID;
  }

  private static void writeStyle( final Label label ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( label );
    int style = label.getStyle();
    String orient = ( style & SWT.VERTICAL ) != 0 ? "vertical" : "horizontal";
    writer.set( JS_FIELD_LINE_ORIENTATION, orient );
    if( ( style & SWT.SHADOW_IN ) != 0 ) {
      writer.call( JS_FUNC_ADD_LINE_STYLE, PARAM_SHADOW_IN );
    } else if( ( style & SWT.SHADOW_OUT ) != 0 ) {
      writer.call( JS_FUNC_ADD_LINE_STYLE, PARAM_SHADOW_OUT );
    }
  }

  private static void resetStyle() throws IOException {
    JSWriter writer = JSWriter.getWriterForResetHandler();
    writer.call( JS_FUNC_REMOVE_LINE_STYLE, PARAM_SHADOW_IN );
    writer.call( JS_FUNC_REMOVE_LINE_STYLE, PARAM_SHADOW_OUT );
    writer.reset( JS_FIELD_LINE_ORIENTATION );
  }
}
