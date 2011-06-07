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
package org.eclipse.swt.graphics;

import java.io.*;

import org.eclipse.rwt.internal.engine.*;
import org.eclipse.rwt.internal.resources.ResourceUtil;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.rwt.service.ISessionStore;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import org.eclipse.swt.widgets.Display;


class ImageSerializer {
  
  private static class SerializableBytes implements Serializable {
    private static final long serialVersionUID = 1L;
    
    final byte[] data;
    
    SerializableBytes( byte[] data ) {
      this.data = data;
    }
  }

  private class PostDeserializationValidation implements ObjectInputValidation {
    private final SerializableBytes imageBytes;
  
    PostDeserializationValidation( SerializableBytes imageBytes ) {
      this.imageBytes = imageBytes;
    }
  
    public void validateObject() throws InvalidObjectException {
      PostDeserialization.addProcessor( getSessionStore(), new Runnable() {
        public void run() {
          InputStream inputStream = new ByteArrayInputStream( imageBytes.data );
          getResourceManager().register( image.internalImage.getResourceName(), inputStream );
        }
      } );
    }
  }

  private final Image image;

  ImageSerializer( Image image ) {
    this.image = image;
  }

  void writeObject( ObjectOutputStream stream ) throws IOException {
    stream.defaultWriteObject();
    stream.writeObject( new SerializableBytes( getImageBytes() ) );
  }

  void readObject( ObjectInputStream stream ) throws IOException, ClassNotFoundException {
    stream.defaultReadObject();
    SerializableBytes imageBytes = ( SerializableBytes )stream.readObject();
    stream.registerValidation( new PostDeserializationValidation( imageBytes ), 0 );
  }

  private byte[] getImageBytes() {
    String resourceName = image.internalImage.getResourceName();
    InputStream inputStream = getResourceManager().getRegisteredContent( resourceName );
    try {
      return ResourceUtil.readBinary( inputStream );
    } catch( IOException ioe ) {
      throw new RuntimeException( ioe );
    }
  }

  private ISessionStore getSessionStore() {
    Display display = ( Display )image.getDevice();
    IDisplayAdapter adapter = ( IDisplayAdapter )display.getAdapter( IDisplayAdapter.class );
    return adapter.getSessionStore();
  }

  private IResourceManager getResourceManager() {
    return ApplicationContextUtil.get( getSessionStore() ).getResourceManager();
  }
}
