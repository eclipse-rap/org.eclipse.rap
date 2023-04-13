/*******************************************************************************
 * Copyright (c) 2011, 2016 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.widgets;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.eclipse.swt.widgets.Dialog;
import org.junit.Before;
import org.junit.Test;


public class DialogUtil_Test {

  private Dialog dialog;

  @Before
  public void setUp() {
    dialog = mock( Dialog.class );
  }

  @Test
  public void testOpenWithNullDialog() {
    DialogCallback dialogCallback = mock( DialogCallback.class );
    try {
      DialogUtil.open( null, dialogCallback );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  @Test
  public void testOpenWithNullDialogCallback() {
    DialogUtil.open( dialog, null );

    verify( dialog ).open( same( ( DialogCallback )null ) );
  }

  @Test
  public void testOpen() {
    DialogCallback dialogCallback = mock( DialogCallback.class );

    DialogUtil.open( dialog, dialogCallback );

    verify( dialog ).open( dialogCallback );
  }

}
