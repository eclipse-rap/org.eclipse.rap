/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.graphics;

import java.io.InputStream;

import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.graphics.ResourceFactory;


/**
 * TODO [rst] comment
 */
public class Graphics {

  public static Color getColor( final RGB rgb ) {
    return ResourceFactory.getColor( rgb.red, rgb.green, rgb.blue );
  }

  public static Color getColor( final int red, final int green, final int blue )
  {
    return ResourceFactory.getColor( red, green, blue );
  }

  public static Font getFont( final FontData data ) {
    return getFont( data.getName(), data.getHeight(), data.getStyle() );
  }

  public static Font getFont( final String name,
                              final int height,
                              final int style )
  {
    return ResourceFactory.getFont( name, height, style );
  }

  public static Image getImage( final String path ) {
    return ResourceFactory.findImage( path );
  }

  public static Image getImage( final String path,
                                final ClassLoader imageLoader )
  {
    return ResourceFactory.findImage( path, imageLoader );
  }

  public static Image getImage( final String path,
                                final InputStream inputStream )
  {
    return ResourceFactory.findImage( path, inputStream );
  }
}
