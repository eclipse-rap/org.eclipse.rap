/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.buttonkit;

import java.io.IOException;

import org.eclipse.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rwt.internal.protocol.IClientObject;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.Button;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.preserveListener;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.renderProperty;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.renderListener;


final class ButtonLCAUtil {

  private static final String TYPE = "rwt.widgets.Button";

  static final String PROP_TEXT = "text";
  static final String PROP_IMAGE = "image";
  static final String PROP_SELECTION = "selection";
  static final String PROP_ALIGNMENT = "alignment";
  static final String PROP_SELECTION_LISTENERS = "selection";

  private static final String PARAM_SELECTION = "selection";
  private static final Integer DEFAULT_ALIGNMENT = new Integer( SWT.CENTER );

  private ButtonLCAUtil() {
    // prevent instantiation
  }

  static void preserveValues( Button button ) {
    ControlLCAUtil.preserveValues( button );
    WidgetLCAUtil.preserveCustomVariant( button );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( button );
    adapter.preserve( PROP_TEXT, button.getText() );
    adapter.preserve( PROP_IMAGE, button.getImage() );
    adapter.preserve( PROP_SELECTION, Boolean.valueOf( button.getSelection() ) );
    adapter.preserve( PROP_ALIGNMENT, new Integer( button.getAlignment() ) );
    preserveListener( button, PROP_SELECTION_LISTENERS, SelectionEvent.hasListener( button ) );
  }

  static void renderInitialization( Button button ) {
    IClientObject clientObject = ClientObjectFactory.getForWidget( button );
    clientObject.create( TYPE );
    clientObject.setProperty( "parent", WidgetUtil.getId( button.getParent() ) );
    clientObject.setProperty( "style", WidgetLCAUtil.getStyles( button ) );
  }

  static void renderChanges( Button button ) throws IOException {
    ControlLCAUtil.renderChanges( button );
    WidgetLCAUtil.renderCustomVariant( button );
    renderText( button );
    renderImage( button );
    renderAlignment( button );
    renderSelection( button );
    renderListenSelection( button );
  }

  static boolean readSelection( Button button ) {
    String value = WidgetLCAUtil.readPropertyValue( button, PARAM_SELECTION );
    if( value != null ) {
      button.setSelection( Boolean.valueOf( value ).booleanValue() );
    }
    return value != null;
  }

  ///////////////////////////////////////////////////
  // Helping methods to render the changed properties

  private static void renderText( Button button ) {
    renderProperty( button, PROP_TEXT, button.getText(), "" );
  }

  private static void renderImage( Button button ) {
    Image image = button.getImage();
    if( WidgetLCAUtil.hasChanged( button, Props.IMAGE, image, null ) ) {
      Object[] args = null;
      if( image != null ) {
        String imagePath = ImageFactory.getImagePath( image );
        Rectangle bounds = image.getBounds();
        args = new Object[] {
          imagePath,
          new Integer( bounds.width ),
          new Integer( bounds.height )
        };
      }
      IClientObject clientObject = ClientObjectFactory.getForWidget( button );
      clientObject.setProperty( "image", args );
    }
  }

  private static void renderAlignment( Button button ) {
    if( ( button.getStyle() & SWT.ARROW ) == 0 ) {
      Integer newValue = new Integer( button.getAlignment() );
      if( WidgetLCAUtil.hasChanged( button, PROP_ALIGNMENT, newValue, DEFAULT_ALIGNMENT ) ) {
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
        IClientObject clientObject = ClientObjectFactory.getForWidget( button );
        clientObject.setProperty( "alignment", value );
      }
    }
  }

  private static void renderSelection( Button button ) {
    String prop = PROP_SELECTION;
    renderProperty( button, prop, Boolean.valueOf( button.getSelection() ), Boolean.FALSE );
  }

  private static void renderListenSelection( Button button ) {
    renderListener( button, PROP_SELECTION_LISTENERS, SelectionEvent.hasListener( button ), false );
  }
}
