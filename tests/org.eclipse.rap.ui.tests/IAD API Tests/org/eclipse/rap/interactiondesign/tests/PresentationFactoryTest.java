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

import java.util.List;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.internal.provisional.action.ICoolBarManager2;
import org.eclipse.jface.internal.provisional.action.IToolBarContributionItem;
import org.eclipse.jface.internal.provisional.action.IToolBarManager2;
import org.eclipse.rap.interactiondesign.tests.impl.ConfigurableStackImpl;
import org.eclipse.rap.interactiondesign.tests.impl.PresentationFactoryImpl;
import org.eclipse.rap.junit.RAPTestCase;
import org.eclipse.rap.ui.interactiondesign.IWindowComposer;
import org.eclipse.rap.ui.interactiondesign.PresentationFactory;
import org.eclipse.rap.ui.interactiondesign.internal.ConfigurableStackProxy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.DefaultStackPresentationSite;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.presentations.IPresentablePart;
import org.eclipse.ui.presentations.IStackPresentationSite;
import org.eclipse.ui.presentations.StackPresentation;


public class PresentationFactoryTest extends RAPTestCase {
  
  private IStackPresentationSite site = new DefaultStackPresentationSite() {

    public IPresentablePart getSelectedPart() {
      return null;
    }

    public boolean isPartMoveable( IPresentablePart toMove ) {
      return false;
    }

    public boolean isStackMoveable() {
      return false;
    }

    public void close( IPresentablePart[] toClose ) {
      
    }

    public void flushLayout() {
      
    }

    public IPresentablePart[] getPartList() {
      return null;
    }

    public String getProperty( String id ) {
      return null;
    }
    
  };
  private PresentationFactory factory;
  
  protected void setUp() throws Exception {
    if( factory == null ) {
      factory = getPresentationFactory();
    }
  }
  
  public void testBrandingPresentationFactoryCoupling() {
    assertTrue( factory instanceof PresentationFactoryImpl );
  }
  
  public void testCreateViewPresentation() {
    Composite comp = getParentForStackPresentation();
    StackPresentation stackPresentation 
      = factory.createViewPresentation( comp, site );
    String expectedName = stackPresentation.getClass().getName();
    String actualName 
      = "org.eclipse.rap.ui.interactiondesign.internal.ConfigurableStackProxy";
    assertEquals( expectedName, actualName);    
  }
  
  public void testCreateStandaloneViewPresentation() {
    Composite comp = getParentForStackPresentation();
    StackPresentation stackPresentation 
      = factory.createStandaloneViewPresentation( comp, site, false );
    String expectedName = stackPresentation.getClass().getName();
    String actualName 
      = "org.eclipse.rap.ui.interactiondesign.internal.ConfigurableStackProxy";
    assertEquals( expectedName, actualName);    
  }
  
  public void testCreateEditorPresentation() {
    Composite comp = getParentForStackPresentation();
    StackPresentation stackPresentation 
      = factory.createEditorPresentation( comp, site );
    String expectedName = stackPresentation.getClass().getName();
    String actualName 
      = "org.eclipse.rap.ui.interactiondesign.internal.ConfigurableStackProxy";
    assertEquals( expectedName, actualName);    
  }
  
  public void testCreateCoolBarManager() {
    ICoolBarManager2 manager = factory.createCoolBarManager();
    assertTrue( manager instanceof ICoolBarManager2 );
  }
  
  public void testCreateMenuManager() {
    MenuManager manager = factory.createMenuBarManager();
    assertTrue( manager instanceof MenuManager );
  }
  
  public void testCreatePartMenuManager() {
    MenuManager manager = factory.createPartMenuManager();
    assertTrue( manager instanceof MenuManager );
  }
  
  public void testToolbarCreation() {
    IToolBarManager2 manager = factory.createToolBarManager();
    assertTrue( manager instanceof IToolBarManager2 );
    IToolBarContributionItem item 
      = factory.createToolBarContributionItem( manager, "" );
    assertTrue( item instanceof IToolBarContributionItem );    
  }
  
  public void testCreateViewToolBarManager() {
    IToolBarManager2 manager = factory.createViewToolBarManager();
    assertTrue( manager instanceof IToolBarManager2 );
  }
  
  public void testCreateWindowComposer() {
    IWindowComposer composer = factory.createWindowComposer();
    assertTrue( composer instanceof IWindowComposer );
  }
  
  public void testWindowComposer() {
    IWindowComposer composer = factory.createWindowComposer();
    Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
    Composite parent = composer.createWindowContents( shell, null );
    assertNotNull( parent );    
  }
  
  public void testStackPresentationPartCoupling() {
    Object adapter = factory.getAdapter( StackPresentation.class );
    assertTrue( adapter instanceof List );
    List proxyList = ( List ) adapter;
    assertTrue( proxyList.size() > 0 );
    boolean found = false;
    for( int i = 0; i < proxyList.size() && !found; i++ ) {
      Object element = proxyList.get( i );
      assertTrue( element instanceof ConfigurableStackProxy );
      ConfigurableStackProxy proxy = ( ConfigurableStackProxy ) element;
      if( proxy.getCurrentStackPresentation() 
            instanceof ConfigurableStackImpl ) {
        found = true;
      }
    }
    assertTrue( found );
  }


  private Composite getParentForStackPresentation() {
    Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
    Composite comp = new Composite( shell, SWT.NONE );
    return comp;
  }  

  public static PresentationFactory getPresentationFactory() {
    IWorkbenchWindow workbenchWindow 
      = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    WorkbenchWindow window = ( WorkbenchWindow ) workbenchWindow;
    PresentationFactory factory = window.getConfigurablePresentationFactory();
    return factory;
  }  
  
}
