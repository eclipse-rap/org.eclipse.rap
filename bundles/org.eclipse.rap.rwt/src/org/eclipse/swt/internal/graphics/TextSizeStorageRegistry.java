/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.graphics;

// TODO [fappel]: Finish implementation and revise this. This class is meant to 
//                be initialized once at application startup. Because of this
//                there should no need for synchronization. Due to the 
//                application scope of the storage synchronizations may cause 
//                also performance problems.
public class TextSizeStorageRegistry {
  
  private static ITextSizeStorage storage;
  
  public static void register( final ITextSizeStorage fontSizeStorage ) {
    if( storage != null ) {
      String msg
        = "A fontsize storage implementation has already been registered.";
      throw new IllegalStateException( msg );
    }
    storage = fontSizeStorage;
  }
  
  public static ITextSizeStorage obtain() {
    if( storage == null ) {
      storage = new DefaultTextSizeStorage();
    }
    return storage;
  }
}
