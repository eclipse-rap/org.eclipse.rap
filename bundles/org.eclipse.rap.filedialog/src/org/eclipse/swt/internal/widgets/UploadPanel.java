/*******************************************************************************
 * Copyright (c) 2013, 2017 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets;

import static org.eclipse.swt.internal.Compatibility.getMessage;
import static org.eclipse.swt.internal.widgets.LayoutUtil.createGridLayout;
import static org.eclipse.swt.internal.widgets.LayoutUtil.createHorizontalFillData;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rap.fileupload.UploadSizeLimitExceededException;
import org.eclipse.rap.fileupload.UploadTimeLimitExceededException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.widgets.FileUploadRunnable.State;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;


@SuppressWarnings( "restriction" )
public class UploadPanel extends Composite {

  private static long KB = 1000;
  private static long MB = 1000 * 1000;
  private static long SEC = 1000;
  private static long MIN = 60 * 1000;

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
    icons = new ArrayList<>();
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

  void updateToolTips( Exception exception ) {
    for( Label icon : icons ) {
      if( !icon.isDisposed() ) {
        icon.setToolTipText( getToolTip( exception ) );
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

  private String getToolTip( Exception exception ) {
    if( exception instanceof UploadSizeLimitExceededException ) {
      long size = ( ( UploadSizeLimitExceededException )exception ).getSizeLimit();
      String key = "SWT_UploadFailed_SizeLimitExceeded";
      key += icons.size() == 1 ? "_Single" : "_Multi";
      return getMessage( key, new Object[] {
        formatSize( size )
      } );
    } else if( exception instanceof UploadTimeLimitExceededException ) {
      long time = ( ( UploadTimeLimitExceededException )exception ).getTimeLimit();
      return getMessage( "SWT_UploadFailed_TimeLimitExceeded", new Object[] {
        formatTime( time )
      } );
    } else if( exception != null ) {
      return SWT.getMessage( "SWT_UploadFailed" );
    }
    return null;
  }

  private static String formatSize( long size ) {
    if( size >= MB ) {
      return Math.round( size / MB ) + " MB";
    } else if( size >= KB ) {
      return Math.round( size / KB ) + " kB";
    }
    return size + " B";
  }

  private static String formatTime( long time ) {
    if( time >= MIN ) {
      return Math.round( time / MIN ) + " min";
    } else if( time >= SEC ) {
      return Math.round( time / SEC ) + " sec";
    }
    return time + " milliseconds";
  }

}
