/*******************************************************************************
 * Copyright (c) 2002-2008 Innoopract Informationssysteme GmbH.
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

import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.graphics.ResourceFactory;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.Label;

public class StandardLabelLCA extends AbstractLabelLCADelegate {

  private static final String QX_TYPE = "qx.ui.basic.Atom";
  static final String TYPE_POOL_ID = StandardLabelLCA.class.getName();

  private static final String PROP_TEXT = "text";
  private static final String PROP_ALIGNMENT = "alignment";
  private static final String PROP_IMAGE = "image";

  private static final String JS_FUNC_LABEL_UTIL_SET_ALIGNMENT
    = "org.eclipse.swt.LabelUtil.setAlignment";
  private static final String JS_FUNC_LABEL_UTIL_SET_IMAGE
    = "org.eclipse.swt.LabelUtil.setImage";
  private static final String JS_FUNC_LABEL_UTIL_SET_TEXT
    = "org.eclipse.swt.LabelUtil.setText";
  private static final Integer DEFAULT_ALIGNMENT = new Integer( SWT.LEFT );
//  private static final Object[] PARAM_NULL = new Object[] { null };

  void preserveValues( final Label label ) {
    ControlLCAUtil.preserveValues( label );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( label );
    adapter.preserve( PROP_TEXT, label.getText() );
    adapter.preserve( PROP_IMAGE, label.getImage() );
    adapter.preserve( PROP_ALIGNMENT, new Integer( label.getAlignment() ) );
  }

  void readData( final Label label ) {
    ControlLCAUtil.processMouseEvents( label );
  }

  void renderInitialization( final Label label ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( label );
    writer.newWidget( QX_TYPE );
    ControlLCAUtil.writeStyleFlags( label );
    Boolean wrap = Boolean.valueOf( ( label.getStyle() & SWT.WRAP ) != 0 );
    Object[] args = { label };
    writer.callStatic( "org.eclipse.swt.LabelUtil.initialize", args );
    Object[] argsWrap = { label, wrap };
    writer.callStatic( "org.eclipse.swt.LabelUtil.setWrap", argsWrap );
    WidgetLCAUtil.writeCustomVariant( label );
  }

  void renderChanges( final Label label ) throws IOException {
    ControlLCAUtil.writeChanges( label );
    writeText( label );
    writeImage( label );
    writeAlignment( label );
  }

  void renderDispose( final Label label ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( label );
    writer.dispose();
  }

  void createResetHandlerCalls( final String typePoolId ) throws IOException {
    resetAlignment();
    resetText();
    resetImage();
    ControlLCAUtil.resetChanges();
    ControlLCAUtil.resetStyleFlags();
  }

  String getTypePoolId( final Label label ) {
    return TYPE_POOL_ID;
  }

  //////////////////////////////////////
  // Helping methods to write JavaScript

  private static void writeText( final Label label ) throws IOException {
    if( WidgetLCAUtil.hasChanged( label, PROP_TEXT, label.getText(), "" ) ) {
      // Order is important here: escapeText, replace line breaks
      String text = WidgetLCAUtil.escapeText( label.getText(), true );
      text = WidgetLCAUtil.replaceNewLines( text, "<br/>" );
      JSWriter writer = JSWriter.getWriterFor( label );
      Object[] args = new Object[]{ label, text };
      writer.callStatic( JS_FUNC_LABEL_UTIL_SET_TEXT, args );
    }
  }

  private static void resetText() throws IOException {
    JSWriter writer = JSWriter.getWriterForResetHandler();
    Object[] args = new Object[]{ JSWriter.WIDGET_REF, null };
    writer.callStatic( JS_FUNC_LABEL_UTIL_SET_TEXT, args );
  }

  private static void writeImage( final Label label ) throws IOException {
    Image image = label.getImage();
    if( WidgetLCAUtil.hasChanged( label, Props.IMAGE, image, null ) )
    {
      JSWriter writer = JSWriter.getWriterFor( label );
      String imagePath;
      if( image == null ) {
        imagePath = null;
      } else {
        // TODO passing image bounds to qooxdoo can speed up rendering
        imagePath = ResourceFactory.getImagePath( image );
      }
      Object[] args = new Object[]{ label, imagePath };
      writer.callStatic( JS_FUNC_LABEL_UTIL_SET_IMAGE, args );
    }
  }

  private static void resetImage() throws IOException {
    JSWriter writer = JSWriter.getWriterForResetHandler();
    Object[] args = new Object[]{ JSWriter.WIDGET_REF, null };
    writer.callStatic( JS_FUNC_LABEL_UTIL_SET_IMAGE, args );
  }

  private static void writeAlignment( final Label label ) throws IOException {
    Integer alignment = new Integer( label.getAlignment() );
    Integer defValue = DEFAULT_ALIGNMENT;
    if( WidgetLCAUtil.hasChanged( label, PROP_ALIGNMENT, alignment, defValue ) )
    {
      JSWriter writer = JSWriter.getWriterFor( label );
      Object[] args = new Object[]{
        label, getAlignment( label.getAlignment() )
      };
      writer.callStatic( JS_FUNC_LABEL_UTIL_SET_ALIGNMENT, args );
    }
  }

  private static void resetAlignment() throws IOException {
    JSWriter writer = JSWriter.getWriterForResetHandler();
    writer.reset( "horizontalChildrenAlign" );
    writer.reset( new String[] { "labelObject", "textAlign" } );
  }

  private static String getAlignment( final int alignment ) {
    String result;
    if( ( alignment & SWT.LEFT ) != 0 ) {
      result = "left";
    } else if( ( alignment & SWT.CENTER ) != 0 ) {
      result = "center";
    } else if( ( alignment & SWT.RIGHT ) != 0 ) {
      result = "right";
    } else {
      result = "left";
    }
    return result;
  }
}
