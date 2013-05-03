/*******************************************************************************
 * Copyright (c) 2011, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.widgets.fileuploadkit;

import static org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory.getClientObject;
import static org.eclipse.rap.rwt.internal.protocol.JsonUtil.createJsonArray;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.getStyles;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.readPropertyValue;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.renderProperty;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;

import java.io.IOException;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rap.rwt.internal.protocol.IClientObject;
import org.eclipse.rap.rwt.internal.widgets.IFileUploadAdapter;
import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.lifecycle.ControlLCAUtil;
import org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.rap.rwt.widgets.FileUpload;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;


public final class FileUploadLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.FileUpload";
  private static final String[] ALLOWED_STYLES = new String[] { "BORDER" };

  private static final String PROP_TEXT = "text";
  private static final String PROP_IMAGE = "image";

  @Override
  public void preserveValues( Widget widget ) {
    FileUpload fileUpload = ( FileUpload ) widget;
    ControlLCAUtil.preserveValues( fileUpload );
    WidgetLCAUtil.preserveCustomVariant( fileUpload );
    preserveProperty( fileUpload, PROP_TEXT, fileUpload.getText() );
    preserveProperty( fileUpload, PROP_IMAGE, fileUpload.getImage() );
  }

  @Override
  public void renderInitialization( Widget widget ) throws IOException {
    FileUpload fileUpload = ( FileUpload ) widget;
    IClientObject clientObject = ClientObjectFactory.getClientObject( fileUpload );
    clientObject.create( TYPE );
    clientObject.set( "parent", getId( fileUpload.getParent() ) );
    clientObject.set( "style", createJsonArray( getStyles( fileUpload, ALLOWED_STYLES ) ) );
  }

  public void readData( Widget widget ) {
    FileUpload fileUpload = ( FileUpload ) widget;
    readFileName( fileUpload );
  }

  @Override
  public void renderChanges( Widget widget ) throws IOException {
    FileUpload fileUpload = ( FileUpload ) widget;
    ControlLCAUtil.renderChanges( fileUpload );
    WidgetLCAUtil.renderCustomVariant( fileUpload );
    renderProperty( fileUpload, PROP_TEXT, fileUpload.getText(), "" );
    renderProperty( fileUpload, PROP_IMAGE, fileUpload.getImage(), null );
    renderSubmit( fileUpload );
  }

  /////////
  // Helper

  private void readFileName( FileUpload fileUpload ) {
    IFileUploadAdapter adapter = fileUpload.getAdapter( IFileUploadAdapter.class );
    String fileName = readPropertyValue( fileUpload, "fileName" );
    if( fileName != null ) {
      adapter.setFileName( fileName.equals( "" ) ? null : fileName );
      fileUpload.notifyListeners( SWT.Selection, new Event() );
    }
  }

  private static void renderSubmit( FileUpload fileUpload ) {
    String url = fileUpload.getAdapter( IFileUploadAdapter.class ).getAndResetUrl();
    if( url != null ) {
      getClientObject( fileUpload ).call( "submit", new JsonObject().add( "url", url ) );
    }
  }

}
