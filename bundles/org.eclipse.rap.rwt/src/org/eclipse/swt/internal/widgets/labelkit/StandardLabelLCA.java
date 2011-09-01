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
package org.eclipse.swt.internal.widgets.labelkit;

import java.io.IOException;

import org.eclipse.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rwt.internal.protocol.IClientObject;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.Label;

final class StandardLabelLCA extends AbstractLabelLCADelegate {

  private static final String TYPE = "rwt.widgets.Label";
  private static final String PROP_TEXT = "text";
  private static final String PROP_ALIGNMENT = "alignment";
  private static final String PROP_IMAGE = "image";

  private static final Integer DEFAULT_ALIGNMENT = new Integer( SWT.LEFT );

  void preserveValues( Label label ) {
    ControlLCAUtil.preserveValues( label );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( label );
    adapter.preserve( PROP_TEXT, label.getText() );
    adapter.preserve( PROP_IMAGE, label.getImage() );
    adapter.preserve( PROP_ALIGNMENT, new Integer( label.getAlignment() ) );
    WidgetLCAUtil.preserveCustomVariant( label );
  }

  void readData( Label label ) {
    ControlLCAUtil.processMouseEvents( label );
    ControlLCAUtil.processKeyEvents( label );
    ControlLCAUtil.processMenuDetect( label );
    WidgetLCAUtil.processHelp( label );
  }

  void renderInitialization( Label label ) throws IOException {
    IClientObject clientObject = ClientObjectFactory.getForWidget( label );
    clientObject.create( TYPE );
    clientObject.setProperty( "parent", WidgetUtil.getId( label.getParent() ) );
    clientObject.setProperty( "style", WidgetLCAUtil.getStyles( label ) );
  }

  void renderChanges( Label label ) throws IOException {
    ControlLCAUtil.renderChanges( label );
    renderText( label );
    renderImage( label );
    renderAlignment( label );
    WidgetLCAUtil.renderCustomVariant( label );
  }

  //////////////////////////////////////
  // Helping methods to write JavaScript

  private static void renderText( Label label ) {
    String newValue = label.getText();
    if( WidgetLCAUtil.hasChanged( label, PROP_TEXT, newValue, "" ) ) {
      IClientObject clientObject = ClientObjectFactory.getForWidget( label );
      clientObject.setProperty( "text", newValue );
    }
  }

  private static void renderImage( Label label ) {
    Image newValue = label.getImage();
    if( WidgetLCAUtil.hasChanged( label, Props.IMAGE, newValue, null ) ) {
      Object[] args = null;
      if( newValue != null ) {
        String imagePath = ImageFactory.getImagePath( newValue );
        Rectangle bounds = newValue.getBounds();
        args = new Object[] {
          imagePath,
          new Integer( bounds.width ),
          new Integer( bounds.height )
        };
      }
      IClientObject clientObject = ClientObjectFactory.getForWidget( label );
      clientObject.setProperty( "image", args );
    }
  }

  private static void renderAlignment( Label label ) {
    Integer newValue = new Integer( label.getAlignment() );
    if( WidgetLCAUtil.hasChanged( label, PROP_ALIGNMENT, newValue, DEFAULT_ALIGNMENT ) ) {
      IClientObject clientObject = ClientObjectFactory.getForWidget( label );
      clientObject.setProperty( PROP_ALIGNMENT, getAlignment( label.getAlignment() ) );
    }
  }

  private static String getAlignment( int alignment ) {
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