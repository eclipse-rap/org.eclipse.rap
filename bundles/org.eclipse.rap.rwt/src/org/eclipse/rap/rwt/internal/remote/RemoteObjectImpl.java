/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.remote;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.rap.rwt.internal.protocol.ProtocolMessageWriter;


public class RemoteObjectImpl implements RemoteObject {

  private final String target;
  private final List<RenderRunnable> renderQueue;
  private boolean destroyed;

  public RemoteObjectImpl( String id, final String type ) {
    target = id;
    destroyed = false;
    renderQueue = new ArrayList<RenderRunnable>();
    renderQueue.add( new RenderRunnable() {
      public void render( ProtocolMessageWriter writer ) {
        writer.appendCreate( target, type );
      }
    } );
  }

  public void set( final String name, final int value ) {
    checkDestroyed();
    renderQueue.add( new RenderRunnable() {
      public void render( ProtocolMessageWriter writer ) {
        writer.appendSet( target, name, value );
      }
    } );
  }

  public void set( final String name, final double value ) {
    checkDestroyed();
    renderQueue.add( new RenderRunnable() {
      public void render( ProtocolMessageWriter writer ) {
        writer.appendSet( target, name, value );
      }
    } );
  }

  public void set( final String name, final boolean value ) {
    checkDestroyed();
    renderQueue.add( new RenderRunnable() {
      public void render( ProtocolMessageWriter writer ) {
        writer.appendSet( target, name, value );
      }
    } );
  }

  public void set( final String name, final String value ) {
    checkDestroyed();
    renderQueue.add( new RenderRunnable() {
      public void render( ProtocolMessageWriter writer ) {
        writer.appendSet( target, name, value );
      }
    } );
  }

  public void set( final String name, final Object value ) {
    checkDestroyed();
    renderQueue.add( new RenderRunnable() {
      public void render( ProtocolMessageWriter writer ) {
        writer.appendSet( target, name, value );
      }
    } );
  }

  public void listen( final String eventType, final boolean listen ) {
    checkDestroyed();
    renderQueue.add( new RenderRunnable() {
      public void render( ProtocolMessageWriter writer ) {
        writer.appendListen( target, eventType, listen );
      }
    } );
  }

  public void call( final String method, final Map<String, Object> properties ) {
    checkDestroyed();
    renderQueue.add( new RenderRunnable() {
      public void render( ProtocolMessageWriter writer ) {
        writer.appendCall( target, method, properties );
      }
    } );
  }

  public void destroy() {
    checkDestroyed();
    renderQueue.add( new RenderRunnable() {
      public void render( ProtocolMessageWriter writer ) {
        writer.appendDestroy( target );
      }
    } );
    destroyed = true;
  }

  public boolean isDestroyed() {
    return destroyed;
  }

  public void render( ProtocolMessageWriter writer ) {
    for( RenderRunnable runnable : renderQueue ) {
      runnable.render( writer );
    }
    renderQueue.clear();
  }

  private void checkDestroyed() {
    if( destroyed ) {
      throw new IllegalStateException( "Remote object is destroyed" );
    }
  }

  public interface RenderRunnable {
    void render( ProtocolMessageWriter writer );
  }

}
