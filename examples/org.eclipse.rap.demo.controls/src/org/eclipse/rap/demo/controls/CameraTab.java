/*******************************************************************************
 * Copyright (c) 2025 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.demo.controls;

import org.eclipse.rap.rwt.addons.camera.Camera;
import org.eclipse.rap.rwt.addons.camera.CameraListener;
import org.eclipse.rap.rwt.addons.camera.CameraOptions;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;


public final class CameraTab extends ExampleTab {

  private Camera camera;

  public CameraTab() {
    super( "Camera" );
    setHorizontalSashFormWeights( new int[] { 100, 0 } );
  }

  @Override
  protected void createStyleControls( Composite parent ) {
  }

  @Override
  protected void createExampleControls( Composite parent ) {
    parent.setLayout( new GridLayout( 2, true ) );
    camera = new Camera( parent, SWT.BORDER );
    camera.setLayoutData( new GridData( 400, 300 ) );
    Label picture = new Label( parent, SWT.BORDER );
    picture.setLayoutData( new GridData( 400, 300 ) );
    camera.addCameraListener( new CameraListener() {
      @Override
      public void receivedPicture( Image image ) {
        picture.setImage( image );
      }
    } );
    createTakePictureButton( parent );
    registerControl( camera );
  }

  private void createTakePictureButton( Composite parent ) {
    final Button button = new Button( parent, SWT.PUSH );
    button.setLayoutData( new GridData( SWT.CENTER, SWT.DEFAULT, false, false ) );
    button.setText( "Take Picture" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        CameraOptions options = new CameraOptions();
        options.setResolution( 400, 300 );
        camera.takePicture( options );
      }
    } );
  }
}
