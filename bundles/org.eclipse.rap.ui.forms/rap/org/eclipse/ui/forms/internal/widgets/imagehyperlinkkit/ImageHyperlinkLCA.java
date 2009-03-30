/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.ui.forms.internal.widgets.imagehyperlinkkit;

import java.io.IOException;

import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.graphics.ResourceFactory;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.internal.widgets.hyperlinkkit.HyperlinkLCA;
import org.eclipse.ui.forms.widgets.ImageHyperlink;


public class ImageHyperlinkLCA extends HyperlinkLCA {

  static final String PROP_IMAGE = "image"; //$NON-NLS-1$

  public void renderChanges( Widget widget ) throws IOException {
    super.renderChanges( widget );
    writeImage( ( ImageHyperlink )widget );
  }

  public void preserveValues( Widget widget ) {
    super.preserveValues( widget );
    preserveImage( ( ImageHyperlink )widget );
  }

  private static void preserveImage( final ImageHyperlink imageHyperlink ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( imageHyperlink );
    adapter.preserve( PROP_IMAGE, imageHyperlink.getImage() );
  }

  private static void writeImage( final ImageHyperlink imageHyperlink )
    throws IOException
  {
    Image image = imageHyperlink.getImage();
    if( WidgetLCAUtil.hasChanged( imageHyperlink, PROP_IMAGE, image, null ) ) {
      String imagePath;
      if( image == null ) {
        imagePath = ""; //$NON-NLS-1$
      } else {
        imagePath = ResourceFactory.getImagePath( image );
      }
      JSWriter writer = JSWriter.getWriterFor( imageHyperlink );
      writer.set( "icon", imagePath ); //$NON-NLS-1$
    }
  }
}
