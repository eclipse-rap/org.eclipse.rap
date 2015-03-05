/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets;

import static org.eclipse.swt.internal.widgets.LayoutUtil.createGridLayout;
import static org.eclipse.swt.internal.widgets.LayoutUtil.createHorizontalFillData;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.widgets.FileUploadRunnable.State;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;


public class UploadPanel extends Composite {

  private final String[] fileNames;
  private Image emptyIcon;
  private Image waitingIcon;
  private Image uploadingIcon;
  private Image finishedIcon;
  private Image failedIcon;
  private final List<Label> icons;

  public UploadPanel( Composite parent, String[] fileNames ) {
    super( parent, SWT.NONE );
    this.fileNames = fileNames;
    initImages();
    setLayout( createGridLayout( 1, 0, 5 ) );
    icons = new ArrayList<Label>();
    createChildren();
  }

  private void initImages() {
    Display display = getDisplay();
    emptyIcon = ImageUtil.getImage( display, "empty.png" );
    waitingIcon = ImageUtil.getImage( display, "waiting.png" );
    uploadingIcon = ImageUtil.getImage( display, "uploading.png" );
    finishedIcon = ImageUtil.getImage( display, "finished.png" );
    failedIcon = ImageUtil.getImage( display, "failed.png" );
  }

  private void createChildren() {
    for( String fileName : fileNames ) {
      if( fileName != null ) {
        Composite container = new Composite( this, SWT.BORDER );
        container.setLayout( createContainerLayout() );
        container.setLayoutData( createHorizontalFillData() );
        Label icon = new Label( container, SWT.NONE );
        icon.setImage( emptyIcon );
        icons.add( icon );
        Label name = new Label( container, SWT.NONE );
        name.setLayoutData( createHorizontalFillData() );
        name.setText( fileName );
      }
    }
  }

  private static GridLayout createContainerLayout() {
    GridLayout layout = new GridLayout( 2, false );
    layout.verticalSpacing = 0;
    return layout;
  }

  void updateIcons( State state ) {
    for( Label icon : icons ) {
      if( !icon.isDisposed() ) {
        icon.setImage( getImage( state ) );
      }
    }
  }

  private Image getImage( State state ) {
    Image image = emptyIcon;
    if( state.equals( State.WAITING ) ) {
      image = waitingIcon;
    } else if( state.equals( State.UPLOADING ) ) {
      image = uploadingIcon;
    } else if( state.equals( State.FINISHED ) ) {
      image = finishedIcon;
    } else if( state.equals( State.FAILED ) ) {
      image = failedIcon;
    }
    return image;
  }

}
