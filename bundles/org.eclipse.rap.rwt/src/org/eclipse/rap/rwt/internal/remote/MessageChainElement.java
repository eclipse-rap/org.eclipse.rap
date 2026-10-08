/*******************************************************************************
 * Copyright (c) 2014, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.remote;

import org.eclipse.rap.rwt.internal.protocol.RequestMessage;
import org.eclipse.rap.rwt.internal.protocol.ResponseMessage;


public class MessageChainElement implements MessageFilterChain {

  private final MessageFilter filter;
  private final MessageChainElement nextElement;

  public MessageChainElement( MessageFilter filter, MessageChainElement nextElement ) {
    this.filter = filter;
    this.nextElement = nextElement;
  }

  @Override
  public ResponseMessage handleMessage( RequestMessage request ) {
    return filter.handleMessage( request, nextElement );
  }

  public MessageChainElement remove( MessageFilter filter ) {
    if( this.filter == filter ) {
      return nextElement;
    }
    MessageChainElement newNextElement = nextElement == null ? null : nextElement.remove( filter );
    if( newNextElement == nextElement ) {
      return this;
    }
    return new MessageChainElement( this.filter, newNextElement );
  }

}
