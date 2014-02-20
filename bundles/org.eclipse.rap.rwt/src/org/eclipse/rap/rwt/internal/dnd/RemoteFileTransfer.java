/*******************************************************************************
 * Copyright (c) 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.dnd;

import org.eclipse.rap.rwt.SingletonUtil;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;


public class RemoteFileTransfer extends Transfer {

  static final String TYPE_NAME = "RemoteFile";
  static final int TYPE_ID = registerType( TYPE_NAME );

  private RemoteFileTransfer() {
  }

  public static RemoteFileTransfer getInstance() {
    return SingletonUtil.getSessionInstance( RemoteFileTransfer.class );
  }

  @Override
  public TransferData[] getSupportedTypes() {
    int[] types = getTypeIds();
    TransferData[] data = new TransferData[ types.length ];
    for( int i = 0; i < types.length; i++ ) {
      data[ i ] = new TransferData();
      data[ i ].type = types[ i ];
    }
    return data;
  }

  @Override
  public boolean isSupportedType( TransferData transferData ) {
    if( transferData != null ) {
      for( int typeId : getTypeIds() ) {
        if( transferData.type == typeId ) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  protected int[] getTypeIds() {
    return new int[]{ TYPE_ID };
  }

  @Override
  protected String[] getTypeNames() {
    return new String[]{ TYPE_NAME };
  }


  @Override
  public void javaToNative( Object object, TransferData transferData ) {
  }

  @Override
  public Object nativeToJava( TransferData transferData ) {
    return null;
  }

}
