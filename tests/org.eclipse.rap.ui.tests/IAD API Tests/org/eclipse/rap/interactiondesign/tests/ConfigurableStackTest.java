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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.rap.interactiondesign.tests.impl.ConfigurableStackImpl;
import org.eclipse.rap.interactiondesign.tests.impl.ConfigurableStackImpl2;
import org.eclipse.rap.interactiondesign.tests.impl.ConfigurationActionImpl;
import org.eclipse.rap.junit.RAPTestCase;
import org.eclipse.rap.ui.interactiondesign.ConfigurableStack;
import org.eclipse.rap.ui.interactiondesign.ConfigurationAction;
import org.eclipse.rap.ui.interactiondesign.PresentationFactory;
import org.eclipse.rap.ui.interactiondesign.internal.ConfigurableStackProxy;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.presentations.IPartMenu;
import org.eclipse.ui.presentations.IPresentablePart;
import org.eclipse.ui.presentations.IStackPresentationSite;
import org.eclipse.ui.presentations.StackPresentation;


public class ConfigurableStackTest extends RAPTestCase {

  public static final String VIEW_ID 
    = "org.eclipse.rap.ui.interactiondesign.test.view";
  public static final String VIEW2_ID 
    = "org.eclipse.rap.ui.interactiondesign.test.view2";
  private static final String STACK_ID 
    = "org.eclipse.rap.ui.interactiondesign.test.stackPresentation";
  private ConfigurableStack stack;
  private IStackPresentationSite site;
  private ConfigurableStackProxy proxy;
  
  protected void setUp() throws Exception {
    if( stack == null ) {
      stack = getConfigurableStack();
    }
    if( site == null ) {
      site = stack.getSite();
    }
  }  
  
  public void testGetLayoutPartId() {    
    String layoutPartId = ConfigurableStack.getLayoutPartId( site );
    assertEquals( "topLeftTest", layoutPartId );    
  }
  
  public void testGetStackPresentationId() {
    String stackPresentationId = stack.getStackPresentationId();
    assertEquals( STACK_ID, stackPresentationId );
  }
  
  public void testGetConfigAction() {
    ConfigurationAction configAction = stack.getConfigAction();
    assertTrue( configAction instanceof ConfigurationActionImpl );
  }
  
  public void testGetPaneId() {
    String paneId = stack.getPaneId( site );
    assertEquals( VIEW_ID, paneId );
  }
  
  public void testGetParent() {
    Composite parent = stack.getParent();
    assertNotNull( parent );
  }
  
  public void testGetPartToolBarManager() {
    IWorkbench workbench = PlatformUI.getWorkbench();
    IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
    IWorkbenchPage page = window.getActivePage();
    IViewPart view = page.findView( VIEW_ID );
    view.setFocus();
    IToolBarManager partToolBarManager = stack.getPartToolBarManager();
    assertNotNull( partToolBarManager );
  }
  
  public void testGetShowTitle() {
    assertFalse( stack.getShowTitle() );
  }
  
  public void testGetSavedStackId() {
    String savedStackId = ConfigurableStack.getSavedStackId( site );
    assertEquals( IPreferenceStore.STRING_DEFAULT_DEFAULT, savedStackId );
  }    
  
  public void testSetCurrentStackPresentation() {
    String id = "org.eclipse.rap.ui.interactiondesign.test.stackPresentation2";
    stack.setCurrentStackPresentation( id );
    assertNotNull( proxy );
    ConfigurableStack currentStack = proxy.getCurrentStackPresentation();
    assertTrue( currentStack instanceof ConfigurableStackImpl2 );  
    stack.setCurrentStackPresentation( STACK_ID );
    currentStack = proxy.getCurrentStackPresentation();
    assertTrue( currentStack instanceof ConfigurableStackImpl ); 
  }
  
  public void testCreatePartToolBar() {
    Control partToolBar = stack.createPartToolBar();
    assertNull( partToolBar );
    IToolBarManager manager = stack.getPartToolBarManager();
    createToolbarItems( manager ); 
    assertEquals( 5, manager.getItems().length );
    stack.getConfigAction().saveViewActionVisibility( VIEW_ID, 
                                                      "org.eclipse.actionid3", 
                                                      true );
    partToolBar = stack.createPartToolBar();
    assertNotNull( partToolBar );
    stack.getConfigAction().saveViewActionVisibility( VIEW_ID, 
                                                      "org.eclipse.actionid3", 
                                                      false );
    
  }

  public void testCreateViewMenu() {
    IPartMenu viewMenu = stack.createViewMenu();
    assertNull( viewMenu );    
    stack.getConfigAction().savePartMenuVisibility( true );
    viewMenu = stack.createViewMenu();
    assertNotNull( viewMenu );
    viewMenu.showMenu( new Point( 0, 0 ) );
    stack.getConfigAction().savePartMenuVisibility( false );
  }
  
  public void testProxyConfigAdaptable() {
    IPresentablePart[] partList = site.getPartList();
    IPresentablePart part = partList[ partList.length -1 ];
    proxy.removePart( part );
    proxy.addPart( part, null );
    IPartMenu menu = part.getMenu();
    IPartMenu viewMenu = stack.createViewMenu();
    assertEquals( menu, viewMenu );
    Control toolBar = part.getToolBar();
    Control viewToolBar = stack.createPartToolBar();
    assertEquals( toolBar, viewToolBar );
  }
  
  public void testProxyLoadSavedStack() {
    ConfigurationAction configAction = stack.getConfigAction();
    String id = "org.eclipse.rap.ui.interactiondesign.test.stackPresentation2";
    configAction.saveStackPresentationId( id );
    
    Composite parent = stack.getParent();
    ConfigurableStackProxy confProxy 
      = new ConfigurableStackProxy( parent,
                                    site,
                                    PresentationFactory.KEY_VIEW );
    ConfigurableStack currentStack = confProxy.getCurrentStackPresentation();
    String stackPresentationId = currentStack.getStackPresentationId();
    String stringDefaultDefault = IPreferenceStore.STRING_DEFAULT_DEFAULT;
    configAction.saveStackPresentationId( stringDefaultDefault );
    assertEquals( id, stackPresentationId );    
  }
  
  public void testProxySetShowTitle() {
    assertFalse( stack.getShowTitle() );
    proxy.setShowTitle( true );
    assertTrue( stack.getShowTitle() );
  }
  
  private void createToolbarItems( final IToolBarManager manager ) {
    for( int i = 0; i < 5; i++ ) {
      Action action = new Action() {};
      action.setId( "org.eclipse.actionid" + i );
      action.setText( "Action" + i );
      ActionContributionItem item = new ActionContributionItem( action );
      manager.add( item );      
    }    
  }
  
  private ConfigurableStack getConfigurableStack() {
    ConfigurableStack result = null;
    PresentationFactory factory 
      = PresentationFactoryTest.getPresentationFactory();
    Object adapter = factory.getAdapter( StackPresentation.class );
    assertTrue( adapter instanceof List );
    List proxyList = ( List ) adapter;
    assertTrue( proxyList.size() > 0 );
    boolean found = false;
    for( int i = 0; i < proxyList.size() && !found; i++ ) {
      Object element = proxyList.get( i );
      assertTrue( element instanceof ConfigurableStackProxy );
      proxy = ( ConfigurableStackProxy ) element;
      ConfigurableStack stack = proxy.getCurrentStackPresentation();
      if( stack instanceof ConfigurableStackImpl ) {
        found = true;
        result = ( ConfigurableStack ) stack;
      }
    }
    return result;
  }  
  
}
