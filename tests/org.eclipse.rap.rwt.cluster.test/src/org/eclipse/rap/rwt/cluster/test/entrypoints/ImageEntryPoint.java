/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
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

import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


@SuppressWarnings("restriction")
public class ImageEntryPoint implements EntryPoint {

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
    try {
      return createImage( display, "browser.gif" );
    } catch( IOException exception ) {
      throw new RuntimeException( exception );
    }
  }

  private Image createImage( Display display, String name ) throws IOException {
    InputStream inputStream = getClass().getResourceAsStream( name );
    try {
      return new Image( display, inputStream );
    } finally {
      inputStream.close();
    }
  }

}
