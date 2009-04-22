/******************************************************************************* 
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.rap.interactiondesign.tests;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.rap.interactiondesign.tests.impl.LayoutSetInitializerImpl;
import org.eclipse.rap.interactiondesign.tests.impl.LayoutSetInitializerImpl2;
import org.eclipse.rap.junit.RAPTestCase;
import org.eclipse.rap.ui.interactiondesign.layout.LayoutRegistry;
import org.eclipse.rap.ui.interactiondesign.layout.model.Layout;
import org.eclipse.rap.ui.interactiondesign.layout.model.LayoutSet;


public class LayoutRegistryTest extends RAPTestCase {
  
  private static final String LAYOUT_ID = "org.eclipse.rap.ui.defaultlayout";
  private static final String LAYOUT_ID2 
    = "org.eclipse.rap.ui.interactiondesign.test.layout2";
  private static final String SET_ID
    = "org.eclipse.rap.ui.interactiondesign.test.layoutSet";
  private static final String SET_ID2
    = "org.eclipse.rap.ui.interactiondesign.test.layoutSet2";
  private LayoutRegistry registry;

  protected void setUp() throws Exception {
    registry = LayoutRegistry.getInstance();
  }
  
  public void testGetActiveLayout() {
    Layout activeLayout = registry.getActiveLayout();
    String id = activeLayout.getId();
    assertTrue( LAYOUT_ID2.equals( id ) );
    LayoutSet layoutSet = activeLayout.getLayoutSet( SET_ID2 );
    String imagePath = layoutSet.getImagePath( LayoutSetInitializerImpl2.KEY2 );
    assertEquals( LayoutSetInitializerImpl2.IMAGEPATH2, imagePath );
    String defaultLayoutId = LayoutRegistry.DEFAULT_LAYOUT_ID;
    registry.setActiveLayout( defaultLayoutId, false );
    assertEquals( defaultLayoutId, registry.getActiveLayout().getId() );
    registry.setActiveLayout( id, false );
    assertEquals( id, registry.getActiveLayout().getId() );
  }
  
  public void testLayoutSetInitializer() {
    Layout activeLayout = registry.getActiveLayout();
    LayoutSet layoutSet = activeLayout.getLayoutSet( SET_ID2 );
    assertNotNull( layoutSet );
    String imagePath = layoutSet.getImagePath( LayoutSetInitializerImpl2.KEY2 );
    assertEquals( LayoutSetInitializerImpl2.IMAGEPATH2, imagePath );
  }
  
  public void testSetActiveLayout() {
    registry.setActiveLayout( LAYOUT_ID, false );
    Layout activeLayout = registry.getActiveLayout();
    assertEquals( LAYOUT_ID, activeLayout.getId() );
    LayoutSet layoutSet = activeLayout.getLayoutSet( SET_ID );
    String imagePath = layoutSet.getImagePath( LayoutSetInitializerImpl.KEY );
    assertEquals( LayoutSetInitializerImpl.IMAGEPATH, imagePath );
  }
  
  public void testSaveLayoutId() {
    String savedLayoutId = registry.getSavedLayoutId();
    String defaultString = IPreferenceStore.STRING_DEFAULT_DEFAULT;
    assertEquals( defaultString, savedLayoutId );
    Layout activeLayout = registry.getActiveLayout();
    String id = "";
    if( activeLayout.getId().equals( LAYOUT_ID ) ) {
      registry.setActiveLayout( LAYOUT_ID2, true );
      id = LAYOUT_ID2;
    } else {
      registry.setActiveLayout( LAYOUT_ID, true );
      id = LAYOUT_ID;
    }
    savedLayoutId = registry.getSavedLayoutId();
    assertEquals( id, savedLayoutId );
    registry.saveLayoutId( defaultString );
    savedLayoutId = registry.getSavedLayoutId();
    assertEquals( defaultString, savedLayoutId );
    registry.saveLayoutId( id );
    savedLayoutId = registry.getSavedLayoutId();
    assertEquals( id, savedLayoutId );
    registry.saveLayoutId( defaultString );
  }
  
}
