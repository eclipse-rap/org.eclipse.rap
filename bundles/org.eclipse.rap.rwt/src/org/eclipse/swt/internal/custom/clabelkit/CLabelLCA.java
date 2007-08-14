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

package org.eclipse.swt.internal.custom.clabelkit;

import java.io.IOException;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.Widget;

public class CLabelLCA extends AbstractWidgetLCA {

  private static final String PROP_TEXT = "text";
  private static final String PROP_ALIGNMENT = "alignment";
  private static final String PROP_IMAGE = "image";
  
  private static final Integer DEFAULT_ALIGNMENT = new Integer( SWT.LEFT );
  
  public void preserveValues( Widget widget ) {
    CLabel label = ( CLabel )widget;
    ControlLCAUtil.preserveValues( label );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( label );
    adapter.preserve( PROP_TEXT, label.getText() );
    adapter.preserve( PROP_IMAGE, label.getImage() );
    adapter.preserve( PROP_ALIGNMENT, new Integer( label.getAlignment() ) );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    CLabel label = ( CLabel )widget;
    ControlLCAUtil.writeChanges( label );
    writeText( label );
    writeImage( label );
    writeAlignment( label );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    CLabel label = ( CLabel )widget;
    JSWriter writer = JSWriter.getWriterFor( label );
    writer.newWidget( "qx.ui.basic.Atom" );
    
    if( ( widget.getStyle() & SWT.SHADOW_IN ) != 0 ) {
      writer.call( "addState", new Object[]{ "rwt_SHADOW_IN" } );
    } else if( ( widget.getStyle() & SWT.SHADOW_OUT ) != 0 ) {
      writer.call( "addState", new Object[]{ "rwt_SHADOW_OUT" } );
    } 
    
    ControlLCAUtil.writeStyleFlags( label );
    Object[] args = { label };
    writer.callStatic( "org.eclipse.swt.CLabelUtil.initialize", args  );
  }

  public void readData( final Widget widget ) {
  }
  
  public void createResetHandlerCalls( final String typePoolId )
    throws IOException
  {
  }
  
  public String getTypePoolId( final Widget widget ) throws IOException {
    return null;
  }

  private static void writeText( final CLabel label ) throws IOException {
    if( WidgetLCAUtil.hasChanged( label, PROP_TEXT, label.getText(), "" ) ) {
      JSWriter writer = JSWriter.getWriterFor( label );
      writer.set( JSConst.QX_FIELD_LABEL, label.getText() );
    }
  }

  private static void writeImage( final CLabel label ) throws IOException {
    Image image = label.getImage();
    if( WidgetLCAUtil.hasChanged( label, Props.IMAGE, image, null ) )
    {
      String imagePath;
      if( image == null ) {
        imagePath = null;
      } else {
        // TODO passing image bounds to qooxdoo can speed up rendering
        imagePath = Image.getPath( image );
      }
      JSWriter writer = JSWriter.getWriterFor( label );
      writer.set( JSConst.QX_FIELD_ICON, imagePath );
    }
  }

  private static void writeAlignment( final CLabel label ) throws IOException {
    Integer alignment = new Integer( label.getAlignment() );
    Integer defValue = DEFAULT_ALIGNMENT;
    if( WidgetLCAUtil.hasChanged( label, PROP_ALIGNMENT, alignment, defValue ) ) 
    {
      JSWriter writer = JSWriter.getWriterFor( label );
      Object[] args = new Object[]{
        label, getAlignment( label.getAlignment() )
      };
      // TODO [rh] re-use JSVar constants defined in JSConst
      writer.callStatic( "org.eclipse.swt.LabelUtil.setAlignment", args );
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
