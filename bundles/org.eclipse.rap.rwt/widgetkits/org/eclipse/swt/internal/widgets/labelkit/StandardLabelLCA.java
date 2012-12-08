/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
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

import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.renderProperty;

import java.io.IOException;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rap.rwt.internal.protocol.IClientObject;
import org.eclipse.rap.rwt.lifecycle.ControlLCAUtil;
import org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;


final class StandardLabelLCA extends AbstractLabelLCADelegate {

  private static final String TYPE = "rwt.widgets.Label";
  private static final String[] ALLOWED_STYLES = new String[] { "WRAP", "BORDER" };

  private static final String PROP_TEXT = "text";
  private static final String PROP_ALIGNMENT = "alignment";
  private static final String PROP_IMAGE = "image";
  private static final String PROP_MARKUP_ENABLED = "markupEnabled";

  private static final String DEFAULT_ALIGNMENT = "left";

  void preserveValues( Label label ) {
    ControlLCAUtil.preserveValues( label );
    WidgetLCAUtil.preserveCustomVariant( label );
    preserveProperty( label, PROP_TEXT, label.getText() );
    preserveProperty( label, PROP_IMAGE, label.getImage() );
    preserveProperty( label, PROP_ALIGNMENT, getAlignment( label ) );
  }

  void readData( Label label ) {
    ControlLCAUtil.processEvents( label );
    ControlLCAUtil.processKeyEvents( label );
    ControlLCAUtil.processMenuDetect( label );
    WidgetLCAUtil.processHelp( label );
  }

  void renderInitialization( Label label ) throws IOException {
    IClientObject clientObject = ClientObjectFactory.getClientObject( label );
    clientObject.create( TYPE );
    clientObject.set( "parent", WidgetUtil.getId( label.getParent() ) );
    clientObject.set( "style", WidgetLCAUtil.getStyles( label, ALLOWED_STYLES ) );
    renderProperty( label, PROP_MARKUP_ENABLED, isMarkupEnabled( label ), false );
  }

  void renderChanges( Label label ) throws IOException {
    ControlLCAUtil.renderChanges( label );
    WidgetLCAUtil.renderCustomVariant( label );
    renderProperty( label, PROP_TEXT, label.getText(), "" );
    renderProperty( label, PROP_IMAGE, label.getImage(), null );
    renderProperty( label, PROP_ALIGNMENT, getAlignment( label ), DEFAULT_ALIGNMENT );
  }

  //////////////////
  // Helping methods

  private static boolean isMarkupEnabled( Label label ) {
    return Boolean.TRUE.equals( label.getData( RWT.MARKUP_ENABLED ) );
  }

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