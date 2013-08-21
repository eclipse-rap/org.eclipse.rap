/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.scripting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

import org.eclipse.rap.rwt.internal.scripting.ClientListenerOperation.AddListener;
import org.eclipse.rap.rwt.internal.scripting.ClientListenerOperation.RemoveListener;
import org.eclipse.rap.rwt.scripting.ClientListener;
import org.eclipse.swt.SWT;
import org.junit.Test;


public class ClientListenerOperation_Test {

  @Test
  public void testCreation_addListenerOperation() {
    ClientListener listener = mock( ClientListener.class );

    ClientListenerOperation operation = new AddListener( SWT.Selection, listener );

    assertSame( listener, operation.getListener() );
    assertEquals( SWT.Selection, operation.getEventType() );
  }

  @Test
  public void testCreation_removeListenerOperation() {
    ClientListener listener = mock( ClientListener.class );

    ClientListenerOperation operation = new RemoveListener( SWT.Selection, listener );

    assertSame( listener, operation.getListener() );
    assertEquals( SWT.Selection, operation.getEventType() );
  }

}
