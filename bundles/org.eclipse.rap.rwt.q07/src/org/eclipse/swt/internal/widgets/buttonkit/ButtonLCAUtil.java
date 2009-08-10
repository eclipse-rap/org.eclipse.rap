/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.buttonkit;

import java.io.IOException;

import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.graphics.ResourceFactory;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.Button;


final class ButtonLCAUtil {

  private static final String JS_PROP_SELECTION = "selection";
  private static final String JS_PROP_HORIZONTAL_CHILDREN_ALIGN
    = "horizontalChildrenAlign";

  static final String PROP_SELECTION = "selection";
  static final String PROP_ALIGNMENT = "alignment";
  static final String PROP_DEFAULT = "defaultButton";
  static final String PROP_SELECTION_LISTENERS = "selectionListeners";

  private static final String PARAM_SELECTION = "selection";
  private static final Integer DEFAULT_ALIGNMENT = new Integer( SWT.CENTER );

  private ButtonLCAUtil() {
    // prevent instantiation
  }

  static boolean readSelection( final Button button ) {
    String value = WidgetLCAUtil.readPropertyValue( button, PARAM_SELECTION );
    if( value != null ) {
      button.setSelection( Boolean.valueOf( value ).booleanValue() );
    }
    return value != null;
  }

  static void preserveValues( final Button button ) {
    ControlLCAUtil.preserveValues( button );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( button );
    adapter.preserve( Props.TEXT, button.getText() );
    adapter.preserve( Props.IMAGE, button.getImage() );
    adapter.preserve( PROP_SELECTION,
                      Boolean.valueOf( button.getSelection() ) );
    adapter.preserve( PROP_SELECTION_LISTENERS,
                      Boolean.valueOf( SelectionEvent.hasListener( button ) ) );
    adapter.preserve( PROP_ALIGNMENT, new Integer( button.getAlignment() ) );
    adapter.preserve( PROP_DEFAULT,
                      Boolean.valueOf( isDefaultButton( button ) ) );
    boolean hasListeners = SelectionEvent.hasListener( button );
    adapter.preserve( Props.SELECTION_LISTENERS,
                      Boolean.valueOf( hasListeners ) );
    WidgetLCAUtil.preserveCustomVariant( button );
  }

  static void writeText( final Button button ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( button );
    String text = button.getText();     
    if( WidgetLCAUtil.hasChanged( button, Props.TEXT, text, null ) ) {
      text = WidgetLCAUtil.escapeText( text, true );
      writer.set( "text", text.equals( "" ) ? null : text );
    }
  }

  static void writeImage( final Button button ) throws IOException {
    Image image = button.getImage();
    if( WidgetLCAUtil.hasChanged( button, Props.IMAGE, image, null ) ) {
      String imagePath = ResourceFactory.getImagePath( image );
      JSWriter writer = JSWriter.getWriterFor( button );
      Rectangle bounds = image != null ? image.getBounds() : null;
      Object[] args = new Object[]{
        imagePath,
        new Integer( bounds != null ? bounds.width : 0 ),
        new Integer( bounds != null ? bounds.height : 0 )
      };
      writer.set( "image", args );
    }
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

  static void writeSelection( final Button button ) throws IOException {
    Boolean newValue = Boolean.valueOf( button.getSelection() );
    JSWriter writer = JSWriter.getWriterFor( button );
    writer.set( PROP_SELECTION, JS_PROP_SELECTION, newValue, Boolean.FALSE );
  }

  // TODO [rst] Can this be moved to ShellLCA ?
  static void writeDefault( final Button button ) throws IOException {
    boolean isDefault = isDefaultButton( button );
    Boolean defValue = Boolean.FALSE;
    Boolean actValue = Boolean.valueOf( isDefault );
    if( WidgetLCAUtil.hasChanged( button, PROP_DEFAULT, actValue, defValue ) ) {
      if( isDefault ) {
        JSWriter writer = JSWriter.getWriterFor( button.getShell() );
        writer.set( "defaultButton", new Object[] { button } );
      }
    }
  }

  static void writeSelectionListener( final Button button ) throws IOException {
    boolean hasListener = SelectionEvent.hasListener( button );
    Boolean newValue = Boolean.valueOf( hasListener );
    String prop = PROP_SELECTION_LISTENERS;
    if( WidgetLCAUtil.hasChanged( button, prop, newValue, Boolean.FALSE ) ) {
      JSWriter writer = JSWriter.getWriterFor( button );
      writer.set( "hasSelectionListener", newValue );
    }
  }

  static boolean isDefaultButton( final Button button ) {
    return button.getShell().getDefaultButton() == button;
  }

  static void writeChanges( final Button button ) throws IOException {
    ControlLCAUtil.writeChanges( button );
    writeText( button );
    writeImage( button );
    writeAlignment( button );
    writeSelection( button );
    writeSelectionListener( button );
    WidgetLCAUtil.writeCustomVariant( button );
  }
}
