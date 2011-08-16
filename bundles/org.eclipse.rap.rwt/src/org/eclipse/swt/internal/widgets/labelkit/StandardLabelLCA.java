/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
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

import org.eclipse.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rwt.internal.protocol.IClientObject;
import org.eclipse.rwt.internal.util.EncodingUtil;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.Label;

final class StandardLabelLCA extends AbstractLabelLCADelegate {

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

  void preserveValues( final Label label ) {
    ControlLCAUtil.preserveValues( label );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( label );
    adapter.preserve( PROP_TEXT, label.getText() );
    adapter.preserve( PROP_IMAGE, label.getImage() );
    adapter.preserve( PROP_ALIGNMENT, new Integer( label.getAlignment() ) );
    WidgetLCAUtil.preserveCustomVariant( label );
  }

  void readData( final Label label ) {
    ControlLCAUtil.processMouseEvents( label );
    ControlLCAUtil.processKeyEvents( label );
    ControlLCAUtil.processMenuDetect( label );
    WidgetLCAUtil.processHelp( label );
  }

  void renderInitialization( final Label label ) throws IOException {
    IClientObject clientObject = ClientObjectFactory.getForWidget( label );
    clientObject.create( "org.eclipse.swt.widgets.Label" );
    clientObject.setProperty( "parent", WidgetUtil.getId( label.getParent() ) );
    clientObject.setProperty( "style", WidgetLCAUtil.getStyles( label ) );
  }

  void renderChanges( final Label label ) throws IOException {
    ControlLCAUtil.renderChanges( label );
    writeText( label );
    writeImage( label );
    writeAlignment( label );
    WidgetLCAUtil.renderCustomVariant( label );
  }

  //////////////////////////////////////
  // Helping methods to write JavaScript

  private static void writeText( final Label label ) throws IOException {
    if( WidgetLCAUtil.hasChanged( label, PROP_TEXT, label.getText(), "" ) ) {
      // Order is important here: escapeText, replace line breaks
      String text = WidgetLCAUtil.escapeText( label.getText(), true );
      text = WidgetLCAUtil.replaceNewLines( text, "<br/>" );
      text = EncodingUtil.replaceWhiteSpaces( text ); // fixes bug 192634
      JSWriter writer = JSWriter.getWriterFor( label );
      Object[] args = new Object[]{ label, text };
      writer.callStatic( JS_FUNC_LABEL_UTIL_SET_TEXT, args );
    }
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
        imagePath = ImageFactory.getImagePath( image );
      }
      Object[] args = new Object[]{ label, imagePath };
      writer.callStatic( JS_FUNC_LABEL_UTIL_SET_IMAGE, args );
    }
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