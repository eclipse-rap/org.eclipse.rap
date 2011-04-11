/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.swt.internal.graphics;

import org.eclipse.rwt.internal.engine.ApplicationContext;
import org.eclipse.swt.graphics.ImageData;


/**
 * This class provides ImageData for internal images. Small image data objects
 * are being cached.
 */
final class ImageDataFactory {

  public static ImageData findImageData( final InternalImage internalImage ) {
    return getInstance().findImageData( internalImage );
  }

  static void clear() {
    getInstance().clear();
  }

  private static ImageDataFactoryInstance getInstance() {
    Class singletonType = ImageDataFactoryInstance.class;
    Object singleton = ApplicationContext.getSingleton( singletonType );
    return ( ImageDataFactoryInstance )singleton;
  }
  
  private ImageDataFactory() {
    // prevent instantiation
  }
}
