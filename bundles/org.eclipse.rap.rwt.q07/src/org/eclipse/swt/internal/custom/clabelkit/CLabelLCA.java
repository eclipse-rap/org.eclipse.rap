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
package org.eclipse.swt.internal.custom.clabelkit;

import java.io.IOException;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.theme.IThemeAdapter;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.graphics.ResourceFactory;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.Widget;

public final class CLabelLCA extends AbstractWidgetLCA {

  static final String PROP_TEXT = "text";
  static final String PROP_ALIGNMENT = "alignment";
  static final String PROP_IMAGE = "image";
  static final String PROP_LEFT_MARGIN = "leftMargin";
  static final String PROP_TOP_MARGIN = "topMargin";
  static final String PROP_RIGHT_MARGIN = "rightMargin";
  static final String PROP_BOTTOM_MARGIN = "bottomMargin";

  private static final Integer DEFAULT_ALIGNMENT = new Integer( SWT.LEFT );

  public void preserveValues( final Widget widget ) {
    CLabel label = ( CLabel )widget;
    ControlLCAUtil.preserveValues( label );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( label );
    adapter.preserve( PROP_TEXT, label.getText() );
    adapter.preserve( PROP_IMAGE, label.getImage() );
    adapter.preserve( PROP_ALIGNMENT, new Integer( label.getAlignment() ) );
    adapter.preserve( PROP_LEFT_MARGIN, new Integer( label.getLeftMargin() ) );
    adapter.preserve( PROP_TOP_MARGIN, new Integer( label.getTopMargin() ) );
    adapter.preserve( PROP_RIGHT_MARGIN,
                      new Integer( label.getRightMargin() ) );
    adapter.preserve( PROP_BOTTOM_MARGIN,
                      new Integer( label.getBottomMargin() ) );
    WidgetLCAUtil.preserveCustomVariant( label );
    WidgetLCAUtil.preserveBackgroundGradient( label );
  }

  public void readData( final Widget widget ) {
    CLabel label = ( CLabel )widget;
    ControlLCAUtil.processMouseEvents( label );
    ControlLCAUtil.processKeyEvents( label );
    ControlLCAUtil.processMenuDetect( label );
    WidgetLCAUtil.processHelp( label );
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

  public void renderChanges( final Widget widget ) throws IOException {
    CLabel label = ( CLabel )widget;
    ControlLCAUtil.writeChanges( label );
    writeText( label );
    writeImage( label );
    writeAlignment( label );
    writeMargins( label );
    WidgetLCAUtil.writeCustomVariant( label );
    WidgetLCAUtil.writeBackgroundGradient( label );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }

  private static void writeText( final CLabel label ) throws IOException {
    String text = label.getText();
    if( WidgetLCAUtil.hasChanged( label, PROP_TEXT, text, "" ) ) {
      if( text == null ) {
        text = "";
      }
      text = WidgetLCAUtil.escapeText( text, true );
      JSWriter writer = JSWriter.getWriterFor( label );
      text = WidgetLCAUtil.replaceNewLines( text, "<br/>" );
      writer.set( JSConst.QX_FIELD_LABEL, text );
    }
  }

  private static void writeImage( final CLabel label ) throws IOException {
    Image image = label.getImage();
    if( WidgetLCAUtil.hasChanged( label, Props.IMAGE, image, null ) ) {
      String imagePath;
      if( image == null ) {
        imagePath = null;
      } else {
        // TODO passing image bounds to qooxdoo can speed up rendering
        imagePath = ResourceFactory.getImagePath( image );
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

  private static void writeMargins( final CLabel label ) throws IOException {
    Integer leftMargin = new Integer( label.getLeftMargin() );
    Integer topMargin = new Integer( label.getTopMargin() );
    Integer rightMargin = new Integer( label.getRightMargin() );
    Integer bottomMargin = new Integer( label.getBottomMargin() );
    CLabelThemeAdapter themeAdapter
      = ( CLabelThemeAdapter )label.getAdapter( IThemeAdapter.class );
    Rectangle padding = themeAdapter.getPadding( label );
    Integer defLeftMargin = new Integer( padding.x );
    Integer defTopMargin = new Integer( padding.y );
    Integer defRightMargin = new Integer( padding.width - padding.x );
    Integer defBottomMargin = new Integer( padding.height - padding.y );
    if(    WidgetLCAUtil.hasChanged( label,
                                     PROP_LEFT_MARGIN,
                                     leftMargin,
                                     defLeftMargin )
        || WidgetLCAUtil.hasChanged( label,
                                     PROP_TOP_MARGIN,
                                     topMargin,
                                     defTopMargin )
        || WidgetLCAUtil.hasChanged( label,
                                     PROP_RIGHT_MARGIN,
                                     rightMargin,
                                     defRightMargin )
        || WidgetLCAUtil.hasChanged( label,
                                     PROP_BOTTOM_MARGIN,
                                     bottomMargin,
                                     defBottomMargin ) )
    {
      JSWriter writer = JSWriter.getWriterFor( label );
      Object[] args = new Object[]{
        topMargin,
        rightMargin,
        bottomMargin,
        leftMargin
      };
      writer.set( JSConst.QX_FIELD_PADDING, args );
    }
  }
}
