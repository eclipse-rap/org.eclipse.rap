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
import org.eclipse.swt.widgets.Label;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.renderProperty;

final class StandardLabelLCA extends AbstractLabelLCADelegate {

  private static final String TYPE = "rwt.widgets.Label";

  private static final String PROP_TEXT = "text";
  private static final String PROP_ALIGNMENT = "alignment";
  private static final String PROP_IMAGE = "image";

  private static final String DEFAULT_ALIGNMENT = "left";

  void preserveValues( Label label ) {
    ControlLCAUtil.preserveValues( label );
    WidgetLCAUtil.preserveCustomVariant( label );
    preserveProperty( label, PROP_TEXT, label.getText() );
    preserveProperty( label, PROP_IMAGE, label.getImage() );
    preserveProperty( label, PROP_ALIGNMENT, getAlignment( label ) );
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
    WidgetLCAUtil.renderCustomVariant( label );
    renderProperty( label, PROP_TEXT, label.getText(), "" );
    renderImage( label );
    renderProperty( label, PROP_ALIGNMENT, getAlignment( label ), DEFAULT_ALIGNMENT );
  }

  ///////////////////////////////////////////////////
  // Helping methods to render the changed properties

  private static void renderImage( Label label ) {
    Image newValue = label.getImage();
    if( WidgetLCAUtil.hasChanged( label, PROP_IMAGE, newValue, null ) ) {
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

  //////////////////
  // Helping methods

  private static String getAlignment( Label label ) {
    int alignment = label.getAlignment();
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