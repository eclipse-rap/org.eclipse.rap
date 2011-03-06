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

import org.eclipse.rwt.internal.engine.RWTContext;
import org.eclipse.swt.graphics.FontData;


/**
 * This class creates and provides shared FontData instances for internal use.
 */
public final class FontDataFactory {

  public static FontData findFontData( final FontData fontData ) {
    return getInstance().findFontData( fontData );
  }

  static void clear() {
    getInstance().clear();
  }

  private static FontDataFactoryInstance getInstance() {
    Class singletonType = FontDataFactoryInstance.class;
    Object singleton = RWTContext.getSingleton( singletonType );
    return ( FontDataFactoryInstance )singleton;
  }
  
  private FontDataFactory() {
    // prevent instantiation
  }
}
