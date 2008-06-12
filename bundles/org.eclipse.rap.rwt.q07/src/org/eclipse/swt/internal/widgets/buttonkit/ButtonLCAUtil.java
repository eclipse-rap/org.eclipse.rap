/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.buttonkit;

import java.io.IOException;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.graphics.ResourceFactory;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.Button;


final class ButtonLCAUtil {


  private static final String JS_PROP_HORIZONTAL_CHILDREN_ALIGN
    = "horizontalChildrenAlign";
  static final String PROP_SELECTION = "selection";
  static final String PROP_ALIGNMENT = "alignment";
  static final String PROP_DEFAULT = "defaultButton";
  private static final String PARAM_SELECTION = "selection";
  private static final Integer DEFAULT_ALIGNMENT = new Integer( SWT.CENTER );


  private ButtonLCAUtil() {
    // prevent instantiation
  }

  static void readSelection( final Button button ) {
    String value = WidgetLCAUtil.readPropertyValue( button, PARAM_SELECTION );
    if( value != null ) {
      button.setSelection( Boolean.valueOf( value ).booleanValue() );
    }
  }

  static void preserveValues( final Button button ) {
    ControlLCAUtil.preserveValues( button );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( button );
    adapter.preserve( Props.TEXT, button.getText() );
    adapter.preserve( Props.IMAGE, button.getImage() );
    adapter.preserve( PROP_SELECTION,
                      Boolean.valueOf( button.getSelection() ) );
    adapter.preserve( Props.SELECTION_LISTENERS,
                      Boolean.valueOf( SelectionEvent.hasListener( button ) ) );
    adapter.preserve( PROP_ALIGNMENT, new Integer( button.getAlignment() ) );
    adapter.preserve( PROP_DEFAULT, 
                      Boolean.valueOf( isDefaultButton( button ) ) );
  }

  static void writeText( final Button button ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( button );
    String text = button.getText();
    if( WidgetLCAUtil.hasChanged( button, Props.TEXT, text, "" ) ) {
      text = WidgetLCAUtil.escapeText( text, true );
      writer.set( JSConst.QX_FIELD_LABEL, text );
    }
  }

  static void resetText() throws IOException {
    JSWriter writer = JSWriter.getWriterForResetHandler();
    // Note [fappel]: reset doesn't work, so use setting to empty string
    writer.set( JSConst.QX_FIELD_LABEL, "" );
  }

  static void writeImage( final Button button ) throws IOException {
    Image image = button.getImage();
    if( WidgetLCAUtil.hasChanged( button, Props.IMAGE, image, null ) ) {
      String imagePath;
      if( image == null ) {
        imagePath = "";
      } else {
        imagePath = ResourceFactory.getImagePath( image );
      }
      JSWriter writer = JSWriter.getWriterFor( button );
      writer.set( JSConst.QX_FIELD_ICON, imagePath );
    }
  }

  static void resetImage() throws IOException {
    JSWriter writer = JSWriter.getWriterForResetHandler();
    writer.reset( JSConst.QX_FIELD_ICON );
  }

  static void writeAlignment( final Button button ) throws IOException {
    if( ( button.getStyle() & SWT.ARROW ) == 0 ) {
      Integer newValue = new Integer( button.getAlignment() );
      Integer defValue = DEFAULT_ALIGNMENT;
      if( WidgetLCAUtil.hasChanged( button, PROP_ALIGNMENT, newValue, defValue ) )
      {
        JSWriter writer = JSWriter.getWriterFor( button );
        String value;
        switch( newValue.intValue() ) {
          case SWT.LEFT:
            value = "left";
          break;
          case SWT.CENTER:
            value = "center";
          break;
          case SWT.RIGHT:
            value = "right";
          break;
          default:
            value = "left";
          break;
        }
        writer.set( JS_PROP_HORIZONTAL_CHILDREN_ALIGN, value );
      }
    }
  }

  static void resetAlignment() throws IOException {
    JSWriter writer = JSWriter.getWriterForResetHandler();
    writer.reset( JS_PROP_HORIZONTAL_CHILDREN_ALIGN );
  }

  static void writeSelection( final Button button ) throws IOException {
    Boolean newValue = Boolean.valueOf( button.getSelection() );
    Boolean defValue = Boolean.FALSE;
    JSWriter writer = JSWriter.getWriterFor( button );
    writer.set( PROP_SELECTION, JSConst.QX_FIELD_CHECKED, newValue, defValue );
  }

  static void resetSelection() throws IOException {
    JSWriter writer = JSWriter.getWriterForResetHandler();
    writer.set(  JSConst.QX_FIELD_CHECKED, Boolean.FALSE );
  }

  static void writeDefault( final Button button ) throws IOException {
    boolean isDefault = isDefaultButton( button );
    Boolean defValue = Boolean.valueOf( false );
    Boolean actValue = Boolean.valueOf( isDefault );
    if( WidgetLCAUtil.hasChanged( button, PROP_DEFAULT, actValue, defValue ) ) {
      if( isDefault ) {
        JSWriter writer = JSWriter.getWriterFor( button.getShell() );
        writer.set( "defaultButton", new Object[] { button } );
      }
    }
  }

  static void writeLabelMode( final Button button ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( button );
    writer.callStatic( "org.eclipse.swt.ButtonUtil.setLabelMode",
                       new Object[] { button } );
  }

  static boolean isDefaultButton( final Button button ) {
    return button.getShell().getDefaultButton() == button;
  }
}
