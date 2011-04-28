/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.lifecycle;

import junit.framework.TestCase;

import org.eclipse.rwt.PhaseListenerHelper;


public class PhaseListenerUtil_Test extends TestCase {
  private static final PhaseEvent READ_DATA = PhaseListenerHelper.createReadDataEvent();
  private static final PhaseEvent PROCESS_ACTION = PhaseListenerHelper.createProcessActionEvent();
  private static final PhaseEvent PREPARE_UI_ROOT = PhaseListenerHelper.createPrepareUIRootEvent();
  private static final PhaseEvent RENDER = PhaseListenerHelper.createRenderEvent();

  public void testIsPrepareUIRoot() {
    assertFalse( PhaseListenerUtil.isPrepareUIRoot( RENDER ) );
    assertTrue( PhaseListenerUtil.isPrepareUIRoot( PREPARE_UI_ROOT ) );
  }
  
  public void testIsProcessAction() {
    assertFalse( PhaseListenerUtil.isProcessAction( RENDER ) );
    assertTrue( PhaseListenerUtil.isProcessAction( PROCESS_ACTION ) );
  }
  
  public void testIsRender() {
    assertFalse( PhaseListenerUtil.isRender( READ_DATA ) );
    assertTrue( PhaseListenerUtil.isRender( RENDER ) );
  }
  
  public void testIsReadData() {
    assertFalse( PhaseListenerUtil.isReadData( RENDER ) );
    assertTrue( PhaseListenerUtil.isReadData( READ_DATA ) );
  }
}
