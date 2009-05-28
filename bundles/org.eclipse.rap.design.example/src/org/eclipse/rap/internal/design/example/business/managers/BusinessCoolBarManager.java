/******************************************************************************* 
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.rap.internal.design.example.business.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.ContributionManager;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IContributionManagerOverrides;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.internal.provisional.action.ICoolBarManager2;
import org.eclipse.jface.internal.provisional.action.IToolBarContributionItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.rap.internal.design.example.business.CommandUtil;
import org.eclipse.rap.internal.design.example.business.CommandUtil.CommandParameter;
import org.eclipse.rap.internal.design.example.business.builder.CoolbarLayerBuilder;
import org.eclipse.rap.internal.design.example.business.builder.DummyBuilder;
import org.eclipse.rap.internal.design.example.business.layoutsets.CoolbarInitializer;
import org.eclipse.rap.internal.design.example.business.layoutsets.CoolbarOverflowInitializer;
import org.eclipse.rap.ui.interactiondesign.layout.ElementBuilder;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.menus.CommandContributionItem;


public class BusinessCoolBarManager extends ContributionManager 
  implements ICoolBarManager2 
{
  
  private static final String ACTIVE = "toolbarOverflowActive";
  private static final String INACTIVE = "toolbarOverflowInactive";
  private static final int SPACING = 25;
  private Map buttonItemMap = new HashMap();
  private ElementBuilder dummyBuilder;
  private Composite coolBar;
  private Composite overflowParent;
  private Button overflowOpenButton;
  private Button overflowCloseButton; 
  private Composite overflowLayer;
  private Image preservedWave;
  private Image newWave;
  private List overflowItems = new ArrayList();
  private List commanItems = new ArrayList();
  private Table overflowTable;
  private int indexOfIcon;
  private int indexOfText;
  private int indexOfPulldown;
  private Map commandParamMap = new HashMap();
  private Menu openMenu;
  
  private FocusListener focusListener = new FocusAdapter() {
    public void focusLost( FocusEvent event ) {
      // close the overflow if the table focus is lost
      closeOverflow( null );  
      toggleImages();
    }
  };
  
 
  
   
  
  public BusinessCoolBarManager() {
    super();
    // initialize a dummy builder to get the coolbar images
    dummyBuilder = new DummyBuilder( null, CoolbarInitializer.SET_ID );
  }

  public Control createControl2( final Composite parent ) {
    // create the coolbar control
    coolBar = new Composite( parent, SWT.NONE );
    coolBar.setData( WidgetUtil.CUSTOM_VARIANT, "compTrans" );
    RowLayout layout = new RowLayout();
    layout.spacing = SPACING;
    layout.wrap = false;
    layout.marginRight = 0;
    coolBar.setLayout( layout );   
    coolBar.addControlListener( new ControlAdapter() {
      public void controlResized( final ControlEvent e ) {
        // close the overflow and update the coolbar if the browser has resized
        if( openMenu != null ) {
          // TODO: Sometimes the menu don't close when the control is resized
          openMenu.setVisible( false );
        }
        closeOverflow( null );
        update( true );
      }
    } );
    
    return coolBar;
  }

  public void dispose() {
    if( coolBar != null && !coolBar.isDisposed() ) {
      coolBar.dispose();
    }
    IContributionItem[] items = getItems();
    for( int i = 0; i < items.length; i++ ) {
        items[ i ].dispose();
    }
  }

  public Control getControl2() {
    return coolBar;
  }

  public void refresh() {
  }

  public void resetItemOrder() {
  }

  public void setItems( final IContributionItem[] newItems ) {
    
  }

  public void add( final IToolBarManager toolBarManager ) {
    Assert.isNotNull( toolBarManager );    
    super.add( new ToolBarContributionItem( toolBarManager ) );
  }

  public IMenuManager getContextMenuManager() {
    return null;
  }

  public boolean getLockLayout() {
    return false;
  }

  public int getStyle() {
    return 0;
  }

  public void setContextMenuManager( final IMenuManager menuManager ) {
  }

  public void setLockLayout( final boolean value ) {    
  }

  public void add( final IAction action ) {    
    super.add( action );
  }

  public void add( final IContributionItem item ) {
    super.add( item );
  }

  public void appendToGroup( final String groupName, final IAction action ) {
    super.appendToGroup( groupName, action );
  }

  public void appendToGroup( 
    final String groupName, 
    final IContributionItem item ) 
  {
    super.appendToGroup( groupName, item );
  }

  public IContributionItem find( final String id ) {
    return super.find( id );
  }

  public IContributionItem[] getItems() {
    return super.getItems();
  }

  public IContributionManagerOverrides getOverrides() {
    return null;
  }

  public void insertAfter( final String id, final IAction action ) {
    super.insertAfter( id, action );
  }

  public void insertAfter( final String id, final IContributionItem item ) {
    super.insertAfter( id, item );
  }

  public void insertBefore( final String id, final IAction action ) {
    super.insertBefore( id, action );
  }

  public void insertBefore( final String id, final IContributionItem item ) {
    super.insertBefore( id, item );
  }

  public boolean isDirty() {
    return false;
  }

  public boolean isEmpty() {
    return false;
  }

  public void markDirty() {
  }

  public void prependToGroup( final String groupName, final IAction action ) {
    super.prependToGroup( groupName, action );
  }

  public void prependToGroup( 
    final String groupName, 
    final IContributionItem item ) 
  {
    super.prependToGroup( groupName, item );
  }

  public IContributionItem remove( final String id ) {
    return super.remove( id );
  }

  public IContributionItem remove( final IContributionItem item ) {
    return super.remove( item );
  }

  public void removeAll() {
    super.removeAll();
  }

  public void update( final boolean force ) {
    // dispose all coolbar buttons
    if( coolBar != null ) {
      Control[] children = coolBar.getChildren();
      for( int i = 0; i < children.length; i++ ) {
        if( !children[ i ].isDisposed() ) {
          children[ i ].dispose();
        }
      }
      
      final IContributionItem[] items = getItems();
      final List visibleItems = new ArrayList( items.length );
      // add the items to show in the coolbar
      for( int i = 0; i < items.length; i++ ) {
          final IContributionItem item = items[ i ];
          if ( item.isVisible() ) {
              visibleItems.add( item );
          }
      }
      
      for( int i = 0; i < visibleItems.size(); i++ ) {
        IContributionItem item = ( IContributionItem ) visibleItems.get( i );
        IToolBarManager manager = null;        
        // get the coolbar manager
        if( item instanceof IToolBarContributionItem ) {        
          IToolBarContributionItem toolItem = ( IToolBarContributionItem ) item;
          manager = toolItem.getToolBarManager();           
        } 
        
        // wrap contrib items to buttons and add these to the coolbar
        if( manager != null ) {
          IContributionItem[] toolItems = manager.getItems();
          for( int j = 0; j < toolItems.length; j++ ) {
            if( toolItems[ j ] instanceof ActionContributionItem ) {
              // actions
              ActionContributionItem actionItem 
                = ( ActionContributionItem ) toolItems[ j ];
              addActionToCoolBar( actionItem );
            } else if( toolItems[ j ] instanceof CommandContributionItem ) {
              // commands
              CommandContributionItem commandItem 
                = ( CommandContributionItem ) toolItems[ j ];
              addCommandToCoolBar( commandItem );
            }
          }
        }      
        
      }
      coolBar.pack();
      coolBar.layout( true ); 
      manageOverflow( );
    }
  }
  
  /*
   * This mathod manages the items which can not be shown in the coolbar because
   * it is to small. So an overflow will be shown including these items.
   */
  private void manageOverflow() {    
    int coolbarWidth = coolBar.getParent().getSize().x;    
    for( int childrenSize = getChildrenSize( coolBar ); 
         childrenSize > coolbarWidth; 
         childrenSize = getChildrenSize( coolBar ) ) 
    {
      // remove last children (button)
      int lastIndex = coolBar.getChildren().length - 1;
      Control child = coolBar.getChildren()[ lastIndex ];
      Object object = buttonItemMap.get( child );
      ContributionItem item = ( ContributionItem ) object;
      addOverflowItem( item );
      activeOverflowOpenButton();
      buttonItemMap.remove( child );
      child.dispose();
      child = null;       
    }

    // check if the overflow button should be activated or not
    checkOverflowActivation();    
  }

  private void checkOverflowActivation() {
    boolean foundInToolBar = true;
    for( int i = 0; i < overflowItems.size() && foundInToolBar; i++ ) {
      ContributionItem item = ( ContributionItem ) overflowItems.get( i );
      foundInToolBar = buttonItemMap.containsValue( item );
    }
    // If every item has a representation in the coolbar, the overflow button 
    // should be invisible
    if( foundInToolBar ) {
      deactivateOverflowButton();
    } else {
      activeOverflowOpenButton();
    }
  }

  private void addOverflowItem( final ContributionItem item ) {
    // add the contrib item to the overflow items if it's not allready in
    int indexOf = overflowItems.indexOf( item );
    if( indexOf == -1 ) {
      overflowItems.add( item );
    }
  }

  private void deactivateOverflowButton() {
    if( overflowOpenButton != null ) {
      overflowOpenButton.setVisible( false );
    }
  }

  /*
   * This method calculates the size of all children of the coolbar. This is
   * necessary to compare the correct sizes for the overflow.
   */
  private int getChildrenSize( final Composite comp ) {
    int result = 0;
    Control[] children = comp.getChildren();
    for( int i = 0; i < children.length; i++ ) {
      if( !children[ i ].isDisposed() ) {
        result += ( children[ i ].getSize().x + SPACING );
      }
    }
    return result;
  }
  
  /*
   * Creates and activates the overflow button
   */
  private void activeOverflowOpenButton() {
    if( overflowParent != null && overflowOpenButton == null ) {
      overflowOpenButton = new Button( overflowParent, SWT.PUSH );
      overflowOpenButton.setData( WidgetUtil.CUSTOM_VARIANT, INACTIVE );
      overflowOpenButton.setLayoutData( getOverflowButtonLayoutData() );
      
      overflowOpenButton.addSelectionListener( new SelectionAdapter() {
        public void widgetSelected( final SelectionEvent e ) {  
          // open the overflow and toggle the chefron icon
          createOverflowLayer();
          toggleImages();  
        }
      } );         
    }        
    overflowOpenButton.setVisible( true );
    // create the close button
    if( overflowCloseButton == null ) {
      overflowCloseButton = new Button( overflowParent, SWT.PUSH );
      overflowCloseButton.setData( WidgetUtil.CUSTOM_VARIANT, ACTIVE );
      overflowCloseButton.setLayoutData( getOverflowButtonLayoutData() );
      
    }
    overflowCloseButton.setVisible( false );
  }
  
  private FormData getOverflowButtonLayoutData() {
    String imageId = CoolbarInitializer.OVERFLOW_ACTIVE;
    Image image = dummyBuilder.getImage( imageId );
    FormData fdOverFlowButton = new FormData();
    fdOverFlowButton.left = new FormAttachment( 10 );
    fdOverFlowButton.top = new FormAttachment( 58 );
    if( image != null ) {
      fdOverFlowButton.width = image.getBounds().width;
      fdOverFlowButton.height = image.getBounds().height;
    }
    return fdOverFlowButton;
  }

  private void createOverflowLayer() {
    if( overflowLayer == null ) {
      ElementBuilder layerBuilder 
        = new CoolbarLayerBuilder( overflowParent.getParent(), 
                                   CoolbarOverflowInitializer.SET_ID );
      layerBuilder.build();
      overflowLayer = ( Composite ) layerBuilder.getControl();   
      overflowLayer.addFocusListener( focusListener );
      newWave = layerBuilder.getImage( CoolbarOverflowInitializer.WAVE );
    }

    FormData fdParent = ( FormData ) overflowParent.getLayoutData();
    FormData fdLayer = ( FormData ) overflowLayer.getParent().getLayoutData();
    fdLayer.left = fdParent.left;
    update( true );
    fillOverflowTable();
    
    overflowParent.getParent().layout( true );
    overflowLayer.getParent().moveAbove( null );
    overflowLayer.getParent().moveBelow( overflowParent );
  }

  private void fillOverflowTable() {
    if( overflowTable == null ) {
      overflowTable = new Table( overflowLayer, SWT.SINGLE | SWT.NO_SCROLL );
      overflowTable.setBackgroundMode( SWT.INHERIT_FORCE );
      FormData fdItemTable = new FormData();
      overflowTable.setLayoutData( fdItemTable );
      overflowTable.setData( WidgetUtil.CUSTOM_VARIANT, "overflow" );
      fdItemTable.top = new FormAttachment( 0, 4 );
      fdItemTable.left = new FormAttachment( 0, 93 );
      fdItemTable.bottom = new FormAttachment( 100, -2 );
      fdItemTable.right = new FormAttachment( 100 );  
      overflowTable.setBackgroundMode( SWT.INHERIT_FORCE );
      overflowTable.setHeaderVisible( false );
      overflowTable.setLinesVisible( false );
      // create columns
      TableColumn iconColumn = new TableColumn( overflowTable, SWT.NONE );
      iconColumn.setResizable( false );
      iconColumn.setMoveable( false );
      TableColumn textColumn = new TableColumn( overflowTable, SWT.NONE );
      TableColumn pulldownColumn = new TableColumn( overflowTable, SWT.NONE );
      pulldownColumn.setResizable( false );
      pulldownColumn.setMoveable( false );
      indexOfIcon = overflowTable.indexOf( iconColumn );
      indexOfText = overflowTable.indexOf( textColumn );
      indexOfPulldown = overflowTable.indexOf( pulldownColumn );
      overflowTable.addFocusListener( focusListener );
    }
    emptyOverflowTable();
    overflowTable.setVisible( true );
    
    // add selection support
    final Map itemMap = new HashMap();
    final Map actionMap = new HashMap();
    final MouseAdapter mousDownListner = new MouseAdapter() {
      public void mouseDown( MouseEvent e ) {        
        TableItem[] selection = overflowTable.getSelection();            
        TableItem item = selection[ 0 ];
        int indexOf = overflowTable.indexOf( item );
        Rectangle bounds = item.getBounds( indexOfPulldown );
        Object object = itemMap.get( new Integer( indexOf ) );
        Action action = null;
        if( object != null ) {
          if( object instanceof Action ) {
            // action
            action = ( Action ) object;                             
          } 
        }
        if( e.x < bounds.x && action != null ) {
          // action clicked
          closeOverflow( this );               
          action.run(); 
        } else {
          // pulldown clicked
          final Menu pulldownMenu 
            = getPulldownMenu( action, overflowTable, actionMap );
          if( pulldownMenu != null ) {      
            Display display = overflowTable.getDisplay();
            Point newLoc = display.map( overflowTable, 
                                        null, 
                                        bounds.x + 20, 
                                        bounds.y );
            pulldownMenu.setLocation( newLoc );
            pulldownMenu.setVisible( true );
            openMenu = pulldownMenu;
            final MouseAdapter adapter = this;
            pulldownMenu.addListener( SWT.Hide, new Listener() {              
              public void handleEvent( final Event event ) {
                closeOverflow( adapter );
                pulldownMenu.removeListener( SWT.Hide, this );
              }
            } );
          }
        }
      }
    };
    overflowTable.addMouseListener( mousDownListner );
    
    // fill the table
    clearCommandItems();
    String key = CoolbarOverflowInitializer.ARROW;
    ElementBuilder dummy 
      = new DummyBuilder( null, CoolbarOverflowInitializer.SET_ID );
    Image arrowIcon = dummy.getImage( key );
    for( int i = 0; i < overflowItems.size(); i++ ) {
      ContributionItem contrib = ( ContributionItem ) overflowItems.get( i );
      if( !buttonItemMap.containsValue( contrib ) ) {        
        TableItem tableItem = new TableItem( overflowTable, SWT.NONE );  
        Action action = null;
        if( contrib instanceof ActionContributionItem ) {
          // action
          ActionContributionItem actionItem 
            = ( ActionContributionItem ) contrib;
          action = ( Action ) actionItem.getAction();        
        } else if( contrib instanceof CommandContributionItem ) {
          // command
          CommandContributionItem item = ( CommandContributionItem ) contrib;
          action = CommandUtil.wrapCommand( item, coolBar );
          actionMap.put( action, item );
        }
        // icon column
        Integer value = new Integer( overflowTable.indexOf( tableItem ) );
        itemMap.put( value, action );
        ImageDescriptor imageDescriptor = action.getImageDescriptor();
        if( imageDescriptor != null ) {
          Image icon = imageDescriptor.createImage();
          tableItem.setImage( indexOfIcon, icon );          
        }
        // text column
        setTableItemStyle( tableItem );
        String text = action.getText();
        // reomve the & because there is no shortkey suppor tin the coolbar
        tableItem.setText( indexOfText, text.replaceAll( "&", "" ) );
        tableItem.setData( WidgetUtil.CUSTOM_VARIANT, "overflow" );
        // pulldown
        if( action.getStyle() == IAction.AS_DROP_DOWN_MENU ) {
          tableItem.setImage( indexOfPulldown, arrowIcon );  
        }
      }
    }

    // pack and set focus for the focuslistener     
    overflowTable.getColumn( indexOfIcon ).pack();
    overflowTable.getColumn( indexOfText ).pack();
    overflowTable.getColumn( indexOfPulldown ).pack();
    overflowTable.pack();   
    overflowLayer.layout( true, true );
    overflowTable.setFocus();
  }

  private Menu getPulldownMenu( 
    final Action action, 
    final Control parent, 
    final Map actionMap ) 
  {
    Menu result = null;
    if( action == null ) {
      throw new IllegalArgumentException();
    }
    IMenuCreator menuCreator = action.getMenuCreator();
    if( menuCreator != null ) {
      result = menuCreator.getMenu( parent );
    }
    if( actionMap != null ) {
      Object object = actionMap.get( action );
      if( object != null && object instanceof CommandContributionItem ) {
        CommandContributionItem item = ( CommandContributionItem ) object;
        CommandParameter param = extractCommandInformation( item );
        result = param.getMenu();
      }
    }
    return result;
  }

  private void emptyOverflowTable() {
    TableItem[] items = overflowTable.getItems();
    for( int i = 0; i < items.length; i++ ) {
      items[ i ].dispose();
    }
    overflowTable.clearAll();
    overflowTable.removeAll();
  }

  private void clearCommandItems() {
    for( int i = 0; i < commanItems.size(); i++ ) {
      MenuItem item = ( MenuItem ) commanItems.get( i );
      destroyItem( item );
    }
  }

  private void setTableItemStyle( final TableItem tableItem ) {
    Color color = dummyBuilder.getColor( CoolbarInitializer.OVERFLOW_COLOR );
    tableItem.setForeground( color );
  }

  /*
   * Change the images, this includes the chefron icon and the wave image
   */
  private void toggleImages() {
    Image wave = null;
    if( overflowOpenButton.isVisible() ) {
      // The button was inactive so active it
      overflowOpenButton.setVisible( false );
      overflowCloseButton.setVisible( true );
      wave = newWave;
      overflowLayer.getParent().setVisible( true );
      overflowLayer.setFocus();
    } else {
      overflowCloseButton.setVisible( false );
      overflowOpenButton.setVisible( true );
      overflowLayer.getParent().setVisible( false );
      wave = preservedWave;
    }
    overflowParent.setBackgroundImage( wave );
  }
  
  private void closeOverflow( MouseAdapter adapter ) {
    if( overflowLayer != null && preservedWave != null ) {
      boolean opened = overflowLayer.getParent().isVisible();
      if( opened ) {
        overflowLayer.getParent().setVisible( false );
        overflowParent.setBackgroundImage( preservedWave );
        overflowOpenButton.setData( WidgetUtil.CUSTOM_VARIANT, INACTIVE );
        overflowItems.clear();        
        clearCommandItems();
      }
      if( adapter != null ) {
        overflowTable.removeMouseListener( adapter );
      }
    }
  }

  private void addCommandToCoolBar( final CommandContributionItem item ) {
    CommandParameter param = extractCommandInformation( item );    
    if( param.getStyle() == CommandContributionItem.STYLE_PULLDOWN ) {
      // pull down button
      createPullDownButton( item, 
                            CommandUtil.wrapCommand( item, coolBar ), 
                            param.getStyle() );
    } else {
      final Button button = new Button( coolBar, param.getStyle() );
      Command command = param.getCommand();
      button.setData( command );
      button.setText( param.getText() );
      button.setToolTipText( param.getTooltipText() );
      button.setData( WidgetUtil.CUSTOM_VARIANT, "coolBar" );        
      button.setImage( param.getIcon() );
      button.addSelectionListener( new SelectionAdapter() {
        public void widgetSelected( final SelectionEvent e ) {
          try {
            Command buttonCommand = ( Command ) button.getData();
            buttonCommand.getHandler().execute( new ExecutionEvent() );
          } catch( ExecutionException e1 ) {
            e1.printStackTrace();
          }
        };
      } );
      button.addDisposeListener( new DisposeListener() {      
        public void widgetDisposed( DisposeEvent event ) {
          buttonItemMap.remove( button );
        }
      } );
      buttonItemMap.put( button, item );
    }          
  }  
  
  private CommandParameter extractCommandInformation( 
    final CommandContributionItem item ) 
  {
    CommandParameter result = null;
    Object object = commandParamMap.get( item );
    if( object == null ) {
      result = CommandUtil.extractCommandInformation( item, coolBar );
      commandParamMap.put( item, result );
    } else {
      result = ( CommandParameter ) object;
    }
    return result;
  }


  
  private void destroyItem( Item item ) {
    item.dispose();
    item = null;
  }
  
  /*
   * Calculates the coolbar button bounds
   */
  private void adjustButtonBounds( final Button button ) {
    Image image = dummyBuilder.getImage( CoolbarInitializer.BUTTON_BG );
    int height = image.getBounds().height;
    button.setSize( button.getSize().x, height );
  }

  private void addActionToCoolBar( final ActionContributionItem item ) {
    final IAction action = item.getAction();
    int actionStyle = action.getStyle();
    int style = getButtonStyle( actionStyle );       
        
    if( action.getStyle() == IAction.AS_DROP_DOWN_MENU ) {
      // drop down button
      createPullDownButton( item, action, style );
    } else {
      // create normal button
      final Button button = createCoolBarButton( coolBar, style, action );
      buttonItemMap.put( button, item );
      button.addDisposeListener( new DisposeListener() {      
        public void widgetDisposed( DisposeEvent event ) {
          buttonItemMap.remove( button );
        }
      } ); 
    }  
  }

  private void createPullDownButton( 
    final ContributionItem item,
    final IAction action,
    final int style )
  {    
    final Composite buttonParent = new Composite( coolBar, SWT.NONE );
    buttonItemMap.put( buttonParent, item );
    RowLayout layout = new RowLayout( SWT.HORIZONTAL );
    layout.spacing = 0;
    layout.marginBottom = 0;
    layout.marginHeight = 0;
    layout.marginLeft = 0;
    layout.marginRight = 0;
    layout.marginTop = 0;
    layout.marginWidth = 0;
    buttonParent.setLayout( layout );
    buttonParent.setData( WidgetUtil.CUSTOM_VARIANT, "compTrans" );
    Button button = createCoolBarButton( buttonParent, SWT.PUSH, action );
    buttonParent.addDisposeListener( new DisposeListener() {      
      public void widgetDisposed( final  DisposeEvent event ) {
        buttonItemMap.remove( buttonParent );
        clearComposite( buttonParent );
      }
    } ); 
    // create the pulldown arrow
    final Button arrow = new Button( buttonParent, SWT.PUSH );   
    arrow.setData( WidgetUtil.CUSTOM_VARIANT, "coolBarPulldown" );
    arrow.setImage( dummyBuilder.getImage( CoolbarInitializer.ARROW ) );
    final Menu menu = getItemMenu( item, action, button );
    arrow.setText( " " );
    arrow.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        if( menu != null ) {
          menu.setVisible( true );
          openMenu = menu;
          Display display = arrow.getDisplay();
          Point newLoc = display.map( arrow, null, 10, arrow.getSize().y );
          menu.setLocation( newLoc );
        }
      };
    } );
    buttonParent.layout( true );
  }

  private Menu getItemMenu( 
    final ContributionItem item,
    final IAction action,
    final Button button )
  {
    Menu menu;
    if( item instanceof CommandContributionItem ) {
      CommandParameter param 
        = extractCommandInformation( ( CommandContributionItem ) item );
      menu = param.getMenu();
    } else {
      menu = getPulldownMenu( ( Action ) action, button, null );
    }
    return menu;
  }

  private void clearComposite( final Composite comp ) {
    Control[] children = comp.getChildren();
    for( int i = 0; i < children.length; i++ ) {
      children[ i ].dispose();
    }    
  }

  private int getButtonStyle( int actionStyle ) {
    int style;
    switch( actionStyle ) {
      case IAction.AS_CHECK_BOX:
        style = SWT.CHECK;
      break;
      case IAction.AS_DROP_DOWN_MENU:
        style = SWT.PUSH;
      break;
      case IAction.AS_RADIO_BUTTON:
        style = SWT.RADIO;
      break;
      default:
        style = SWT.PUSH;
      break;
    }
    return style;
  }
  
  private Button createCoolBarButton( 
    final Composite parent, 
    final int style, 
    final IAction action ) 
  {
    final Button button = new Button( parent, style );
    adjustButtonBounds( button );
    button.setText( action.getText() );
    button.setToolTipText( action.getToolTipText() );
    if( action.getImageDescriptor() != null ) {
      button.setImage( action.getImageDescriptor().createImage() );
    }
    button.setData( WidgetUtil.CUSTOM_VARIANT, "coolBar" );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        try{      
          action.run();          
        } catch( Exception ex ) {
          
        }
      }
    } );           
    return button;
  }
  
  public void setOverflowParent( final Composite overflowParent ) {
    this.overflowParent = overflowParent;
    preservedWave = overflowParent.getBackgroundImage();
  }
    
}
