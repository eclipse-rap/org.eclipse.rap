/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.widgets.fileuploadkit;

import java.io.IOException;

import org.eclipse.rwt.internal.widgets.IFileUploadAdapter;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.rwt.widgets.FileUpload;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.Widget;


public final class FileUploadLCA extends AbstractWidgetLCA {

  public static final String PROP_FILENAME = "fileName";

  public void readData( Widget widget ) {
    FileUpload fileUpload = ( FileUpload ) widget;
    readFileName( fileUpload );
  }

  public void preserveValues( Widget widget ) {
    FileUpload fileUpload = ( FileUpload ) widget;
    ControlLCAUtil.preserveValues( fileUpload );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( fileUpload );
    adapter.preserve( Props.TEXT, fileUpload.getText() );
    adapter.preserve( Props.IMAGE, fileUpload.getImage() );
    adapter.preserve( PROP_FILENAME, fileUpload.getFileName() );
  }

  public void renderInitialization( Widget widget ) throws IOException {
    FileUpload fileUpload = ( FileUpload ) widget;
    JSWriter writer = JSWriter.getWriterFor( fileUpload );
    writer.newWidget( "org.eclipse.rwt.widgets.FileUpload" );
    ControlLCAUtil.writeStyleFlags( fileUpload );
  }

  public void renderChanges( Widget widget ) throws IOException {
    FileUpload fileUpload = ( FileUpload ) widget;
    ControlLCAUtil.writeChanges( fileUpload );
    writeText( fileUpload );
    writeImage( fileUpload );
    writeSubmit( fileUpload );
  }

  public void renderDispose( Widget widget ) throws IOException {
    FileUpload fileUpload = ( FileUpload ) widget;
    JSWriter writer = JSWriter.getWriterFor( fileUpload );
    writer.dispose();
  }
  
  /////////
  // Helper  

  private void readFileName( final FileUpload fileUpload ) {
    IFileUploadAdapter adapter
      = ( IFileUploadAdapter )fileUpload.getAdapter( IFileUploadAdapter.class );
    String fileName = WidgetLCAUtil.readPropertyValue( fileUpload, "fileName" );
    if( fileName != null ) {
      adapter.setFileName( fileName == "" ? null : fileName );
      SelectionEvent event = new SelectionEvent( fileUpload, null, SWT.Selection );
      event.processEvent();
    }
  }

  static void writeText( FileUpload fileUpload ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( fileUpload );
    String text = fileUpload.getText();     
    if( WidgetLCAUtil.hasChanged( fileUpload, Props.TEXT, text, null ) ) {
      text = WidgetLCAUtil.escapeText( text, true );
      writer.set( "text", text.equals( "" ) ? null : text );
    }
  }

  static void writeImage( FileUpload fileUpload ) throws IOException {
    Image image = fileUpload.getImage();
    if( WidgetLCAUtil.hasChanged( fileUpload, Props.IMAGE, image, null ) ) {
      String imagePath = ImageFactory.getImagePath( image );
      JSWriter writer = JSWriter.getWriterFor( fileUpload );
      Rectangle bounds = image != null ? image.getBounds() : null;
      Object[] args = new Object[]{
        imagePath,
        new Integer( bounds != null ? bounds.width : 0 ),
        new Integer( bounds != null ? bounds.height : 0 )
      };
      writer.set( "image", args );
    }
  }

  static void writeSubmit( FileUpload fileUpload ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( fileUpload );
    IFileUploadAdapter adapter 
      = ( IFileUploadAdapter )fileUpload.getAdapter( IFileUploadAdapter.class );
    String url = adapter.getAndResetUrl();
    if( url != null ) {
      writer.call( "submit", new Object[] { url } );
    }
  }
  
}
