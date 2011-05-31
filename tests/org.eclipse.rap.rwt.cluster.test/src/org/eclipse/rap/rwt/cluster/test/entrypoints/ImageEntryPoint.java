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
package org.eclipse.rap.rwt.cluster.test.entrypoints;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


@SuppressWarnings("restriction")
public class ImageEntryPoint implements IEntryPoint {

  public static String imagePath;

  public int createUI() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.DIALOG_TRIM );
    shell.open();
    Image image = loadShellImage( display );
    imagePath = ImageFactory.getImagePath( image );
    shell.setImage( image );
    return 0;
  }

  private Image loadShellImage( Display display ) {
    InputStream inputStream = getClass().getResourceAsStream( "browser.gif" );
    Image image = new Image( display, inputStream );
    closeInputStream( inputStream );
    return image;
  }

  private static void closeInputStream( InputStream inputStream ) {
    try {
      inputStream.close();
    } catch( IOException e ) {
      throw new RuntimeException( e );
    }
  }
}
