/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.swt.internal.graphics;

import org.eclipse.rwt.internal.engine.ApplicationContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;


public final class ResourceFactory {

  /////////
  // Colors

  public static Color getColor( final int red,
                                final int green,
                                final int blue )
  {
    int colorNr = computeColorNr( red, green, blue );
    return getColor( colorNr );
  }

  public static int computeColorNr( final int red,
                                    final int green,
                                    final int blue )
  {
    if(    red > 255
        || red < 0
        || green > 255
        || green < 0
        || blue > 255
        || blue < 0 )
    {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    int colorNr = red | green << 8 | blue << 16;
    return colorNr;
  }

  private static Color getColor( final int value ) {
    return getInstance().getColor( value );
  }
  
  ////////
  // Fonts

  public static Font getFont( final FontData fontData ) {
    return getInstance().getFont( fontData );
  }
  
  public static String getImagePath( final Image image ) {
    return ImageFactory.getImagePath( image );
  }

  public static Cursor getCursor( final int style ) {
    return getInstance().getCursor( style );
  }
  
  ///////////////
  // Test helpers

  public static void clear() {
    getInstance().clear();
    ImageFactory.clear();
    InternalImageFactory.clear();
    ImageDataFactory.clear();
    FontDataFactory.clear();
  }

  static int colorsCount() {
    return getInstance().getColorsCount();
  }

  static int fontsCount() {
    return getInstance().getFontsCount();
  }

  static int cursorsCount() {
    return getInstance().getCursorsCount();
  }

  //////////////////
  // Helping methods
  
  private static ResourceFactoryInstance getInstance() {
    Class singletonType = ResourceFactoryInstance.class;
    return ( ResourceFactoryInstance )ApplicationContext.getSingleton( singletonType );
  }

  private ResourceFactory() {
    // prevent instantiation
  }
}
