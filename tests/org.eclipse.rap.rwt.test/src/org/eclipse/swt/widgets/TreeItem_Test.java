/******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.widgets;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.graphics.Graphics;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.ITreeItemAdapter;


@SuppressWarnings("deprecation")
public class TreeItem_Test extends TestCase {

  private Display display;
  private Shell shell;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
    shell = new Shell( display );
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testConstructor() {
    Tree tree = new Tree( shell, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    assertSame( display, item.getDisplay() );
    assertEquals( "", item.getText() );
    assertSame( item, tree.getItem( tree.getItemCount() - 1 ) );
    try {
      new TreeItem( ( TreeItem )null, SWT.NONE );
      fail( "Must not allow null-parent" );
    } catch( IllegalArgumentException iae ) {
      // expected
    }
    try {
      new TreeItem( ( Tree )null, SWT.NONE );
      fail( "Must not allow null-parent" );
    } catch( IllegalArgumentException iae ) {
      // expected
    }
    try {
      new TreeItem( tree, SWT.NONE, 5 );
      fail( "No exception thrown for illegal index argument" );
    } catch( IllegalArgumentException e ) {
      // expected
    }

    try {
      new TreeItem( item, SWT.NONE, 5 );
      fail( "No exception thrown for illegal index argument" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      new TreeItem( item, SWT.NONE, -1 );
      fail( "No exception thrown for illegal index argument" );
    } catch( IllegalArgumentException e ) {
   // expected
    }
  }

  public void testRemoveAll() {
    Tree tree = new Tree( shell, SWT.NONE );
    TreeItem item1 = new TreeItem( tree, SWT.NONE );
    TreeItem item11 = new TreeItem( item1, SWT.NONE );
    TreeItem item111 = new TreeItem( item11, SWT.NONE );
    TreeItem item2 = new TreeItem( tree, SWT.NONE );

    item1.removeAll();

    assertEquals( false, item1.isDisposed() );
    assertEquals( true, item11.isDisposed() );
    assertEquals( true, item111.isDisposed() );
    assertEquals( 0, item1.getItemCount() );
    assertEquals( false, item2.isDisposed() );
  }


  public void testVirtualRemoveAll() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    tree.setItemCount( 1 );
    TreeItem item = tree.getItem( 0 );
    item.setItemCount( 100 );
    TreeItem lastSubItem = item.getItem( 99 );

    assertFalse( lastSubItem.isDisposed() );

    item.removeAll();

    assertTrue( lastSubItem.isDisposed() );
  }

  public void testFont() {
    Tree tree = new Tree( shell, SWT.NONE );
    Font treeFont = Graphics.getFont( "BeautifullyCraftedTreeFont", 15, SWT.BOLD );
    tree.setFont( treeFont );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    assertSame( treeFont, item.getFont() );
    Font itemFont = Graphics.getFont( "ItemFont", 40, SWT.NORMAL );
    item.setFont( itemFont );
    assertSame( itemFont, item.getFont() );
    item.setFont( null );
    assertSame( treeFont, item.getFont() );
  }

  public void testChecked() {
    Tree tree = new Tree( shell, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    Tree checkedTree = new Tree( shell, SWT.CHECK );
    TreeItem checkedItem = new TreeItem( checkedTree, SWT.NONE );
    // Ensure that checked-property on a treeItem cannot be changed when tree
    // is missing CHECK style
    assertEquals( false, item.getChecked() );
    item.setChecked( true );
    assertEquals( false, item.getChecked() );
    // The check-property for a treeItem on a tree with style CHECK may be
    // changed
    assertEquals( false, checkedItem.getChecked() );
    checkedItem.setChecked( true );
    assertEquals( true, checkedItem.getChecked() );
  }

  public void testGetExpanded() {
    Tree tree = new Tree( shell, SWT.SINGLE );
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    treeItem.setExpanded( true );
    assertFalse( treeItem.getExpanded() );
    // there must be at least one subitem before you can set expanded true
    new TreeItem( treeItem, 0 );
    treeItem.setExpanded( true );
    assertTrue( treeItem.getExpanded() );
    treeItem.setExpanded( false );
    assertEquals( false, treeItem.getExpanded() );
  }

  public void testBackgroundColor() {
    Tree tree = new Tree( shell, SWT.SINGLE );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    // initial background color should match the parents one
    assertEquals( tree.getBackground(), item.getBackground() );
    // change the colors
    Color green = display.getSystemColor( SWT.COLOR_GREEN );
    item.setBackground( green );
    assertEquals( green, item.getBackground() );
  }

  public void testForegroundColor() {
    Tree tree = new Tree( shell, SWT.SINGLE );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    // initial foreground color should match the parents one
    assertEquals( tree.getForeground(), item.getForeground() );
    // change the colors
    Color green = display.getSystemColor( SWT.COLOR_GREEN );
    item.setForeground( green );
    assertEquals( green, item.getForeground() );
  }

  public void testClear() {
    Tree tree = new Tree( shell, SWT.SINGLE | SWT.CHECK );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    TreeItem subItem = new TreeItem( item, SWT.NONE );
    Color defaultForeground = subItem.getForeground();
    Color defaultBackground = subItem.getBackground();
    Font defaultFont = subItem.getFont();
    subItem.setBackground( display.getSystemColor( SWT.COLOR_CYAN ) );
    subItem.setForeground( display.getSystemColor( SWT.COLOR_CYAN ) );
    subItem.setFont( new Font( display, "Arial", 22, SWT.NORMAL ) );
    subItem.setText( "foo" );
    subItem.setImage( Graphics.getImage( Fixture.IMAGE1 ) );
    subItem.setChecked( true );
    item.clear( 0, false );
    assertEquals( "", subItem.getText() );
    assertEquals( null, subItem.getImage() );
    assertEquals( defaultForeground, subItem.getForeground() );
    assertEquals( defaultBackground, subItem.getBackground() );
    assertEquals( defaultFont, subItem.getFont() );
    assertFalse( subItem.getChecked() );
  }

  public void testClearRecursive() {
    Tree tree = new Tree( shell, SWT.SINGLE | SWT.CHECK );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    TreeItem subItem = new TreeItem( item, SWT.NONE );
    TreeItem subSubItem = new TreeItem( subItem, SWT.NONE );
    subItem.setText( "foo" );
    subSubItem.setText( "bar" );
    item.clear( 0, true );
    assertEquals( "", subItem.getText() );
    assertEquals( "", subSubItem.getText() );
  }

  public void testClearNonRecursive() {
    Tree tree = new Tree( shell, SWT.SINGLE | SWT.CHECK );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    TreeItem subItem = new TreeItem( item, SWT.NONE );
    TreeItem subSubItem = new TreeItem( subItem, SWT.NONE );
    subItem.setText( "foo" );
    subSubItem.setText( "bar" );
    item.clear( 0, false );
    assertEquals( "", subItem.getText() );
    assertEquals( "bar", subSubItem.getText() );
  }

  public void testClearAll() {
    Tree tree = new Tree( shell, SWT.SINGLE | SWT.CHECK );
    TreeItem root = new TreeItem( tree, SWT.NONE );
    for( int i = 0; i < 2; i++ ) {
      TreeItem item0 = new TreeItem( root, 0 );
      item0.setText( "Item " + i );
      for( int j = 0; j < 2; j++ ) {
        TreeItem item1 = new TreeItem( item0, 0 );
        item1.setText( "Item " + i + " " + j );
        for( int k = 0; k < 2; k++ ) {
          TreeItem item2 = new TreeItem( item1, 0 );
          item2.setText( "Item " + i + " " + j + " " + k );
        }
      }
    }
    root.clearAll( false );
    assertEquals( "", root.getItem( 0 ).getText() );
    assertEquals( "", root.getItem( 1 ).getText() );
    assertEquals( "Item 0 0", root.getItem( 0 ).getItem( 0 ).getText() );
    assertEquals( "Item 1 1", root.getItem( 1 ).getItem( 1 ).getText() );
    root.clearAll( true );
    assertEquals( "", root.getItem( 0 ).getItem( 0 ).getText() );
    assertEquals( "", root.getItem( 1 ).getItem( 1 ).getText() );
  }

  public void testSetGrayed() {
    Tree newTree = new Tree( shell, SWT.CHECK );
    TreeItem tItem = new TreeItem( newTree, 0 );
    assertEquals( false, tItem.getGrayed() );
    tItem.setGrayed( true );
    assertTrue( tItem.getGrayed() );
    tItem.setGrayed( false );
    assertEquals( false, tItem.getGrayed() );
    newTree.dispose();
  }

  public void testSetTextOnCell() {
    Tree tree = new Tree( shell, SWT.CHECK );
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );

    treeItem.setText( 0, "foo" );

    assertEquals( "foo", treeItem.getText( 0 ) );
  }

  public void testSetText() {
    Tree tree = new Tree( shell, SWT.CHECK );
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    String testString = "test";
    String testStrings[] = new String[]{ testString, testString + "1", testString + "2" };
    /*
     * Test the getText/setText API with a Tree that has only the default
     * column.
     */
    assertEquals( "", treeItem.getText( 1 ) );
    treeItem.setText( 1, testString );
    assertEquals( "", treeItem.getText( 1 ) );
    assertEquals( "", treeItem.getText( 0 ) );
    treeItem.setText( 0, testString );
    assertEquals( testString, treeItem.getText( 0 ) );
    treeItem.setText( -1, testStrings[ 1 ] );
    assertEquals( "", treeItem.getText( -1 ) );
    /*
     * Test the getText/setText API with a Tree that enough columns to fit all
     * test item texts.
     */
    tree = new Tree( shell, SWT.CHECK );
    treeItem = new TreeItem( tree, SWT.NONE );
    // tree.setText(TestStrings); // create anough columns for
    // TreeItem.setText(String[]) to work
    int columnCount = tree.getColumnCount();
    if( columnCount < 12 ) {
      for( int i = columnCount; i < 12; i++ ) {
        new TreeColumn( tree, SWT.NONE );
      }
    }
    TreeColumn[] columns = tree.getColumns();
    for( int i = 0; i < testStrings.length; i++ ) {
      columns[ i ].setText( testStrings[ i ] );
    }
    assertEquals( 0, treeItem.getText( 1 ).length() );
    treeItem.setText( 1, testString );
    assertEquals( testString, treeItem.getText( 1 ) );
    assertEquals( 0, treeItem.getText( 0 ).length() );
    treeItem.setText( 0, testString );
    assertEquals( testString, treeItem.getText( 0 ) );
    treeItem.setText( -1, testStrings[ 1 ] );
    assertEquals( 0, treeItem.getText( -1 ).length() );
    try {
      treeItem.setText( -1, null );
      fail( "No exception thrown for string == null" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      treeItem.setText( 0, null );
      fail( "No exception thrown for string == null" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testSetImage() {
    Tree tree = new Tree( shell, SWT.CHECK );
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    Image[] images = new Image[]{
      Graphics.getImage( Fixture.IMAGE1 ),
      Graphics.getImage( Fixture.IMAGE2 ),
      Graphics.getImage( Fixture.IMAGE3 )
    };
    assertNull( treeItem.getImage( 1 ) );
    treeItem.setImage( -1, null );
    assertNull( treeItem.getImage( -1 ) );
    treeItem.setImage( 0, images[ 0 ] );
    assertEquals( images[ 0 ], treeItem.getImage( 0 ) );
    String texts[] = new String[ images.length ];
    for( int i = 0; i < texts.length; i++ ) {
      texts[ i ] = String.valueOf( i );
    }
    // tree.setText(texts); // create enough columns for
    // TreeItem.setImage(Image[]) to work
    int columnCount = tree.getColumnCount();
    if( columnCount < texts.length ) {
      for( int i = columnCount; i < texts.length; i++ ) {
        new TreeColumn( tree, SWT.NONE );
      }
    }
    TreeColumn[] columns = tree.getColumns();
    for( int i = 0; i < texts.length; i++ ) {
      columns[ i ].setText( texts[ i ] );
    }
    treeItem.setImage( 1, images[ 1 ] );
    assertEquals( images[ 1 ], treeItem.getImage( 1 ) );
    treeItem.setImage( images );
    for( int i = 0; i < images.length; i++ ) {
      assertEquals( images[ i ], treeItem.getImage( i ) );
    }
    try {
      treeItem.setImage( ( Image[] )null );
      fail( "No exception thrown for images == null" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    // Test for a disposed Image in the array
    ClassLoader loader = Fixture.class.getClassLoader();
    InputStream stream = loader.getResourceAsStream( Fixture.IMAGE1 );
    Image image = new Image( display, stream );
    image.dispose();
    Image[] images2 = new Image[]{
      Graphics.getImage( Fixture.IMAGE1 ),
      image,
      Graphics.getImage( Fixture.IMAGE3 )
    };
    try {
      treeItem.setImage( images2 );
      fail( "No exception thrown for a disposed image" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    finally {
      try {
        stream.close();
      }
      catch(IOException e) {
        fail("Unable to close input stream.");
      }
    }
  }

  public void testSetImageI() {
    Tree tree = new Tree( shell, SWT.CHECK );
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    Image[] images = new Image[]{
      Graphics.getImage( Fixture.IMAGE1 ),
      Graphics.getImage( Fixture.IMAGE2 ),
      Graphics.getImage( Fixture.IMAGE3 )
    };
    // no columns
    assertEquals( null, treeItem.getImage( 0 ) );
    treeItem.setImage( 0, images[ 0 ] );
    assertEquals( images[ 0 ], treeItem.getImage( 0 ) );
    // index beyond range - no error
    treeItem.setImage( 10, images[ 0 ] );
    assertEquals( null, treeItem.getImage( 10 ) );
    // with columns
    new TreeColumn( tree, SWT.LEFT );
    new TreeColumn( tree, SWT.LEFT );
    // index beyond range - no error
    treeItem.setImage( 10, images[ 0 ] );
    assertEquals( null, treeItem.getImage( 10 ) );
    treeItem.setImage( 0, images[ 0 ] );
    assertEquals( images[ 0 ], treeItem.getImage( 0 ) );
    treeItem.setImage( 0, null );
    assertEquals( null, treeItem.getImage( 0 ) );
    treeItem.setImage( 0, images[ 0 ] );
    treeItem.setImage( images[ 1 ] );
    assertEquals( images[ 1 ], treeItem.getImage( 0 ) );
    treeItem.setImage( images[ 1 ] );
    treeItem.setImage( 0, images[ 0 ] );
    assertEquals( images[ 0 ], treeItem.getImage( 0 ) );

    // Test for a disposed Image in the array
    ClassLoader loader = Fixture.class.getClassLoader();
    InputStream stream = loader.getResourceAsStream( Fixture.IMAGE1 );
    Image image = new Image( display, stream );
    image.dispose();
    try {
      treeItem.setImage( image );
      fail( "No exception thrown for a disposed image" );
    } catch( IllegalArgumentException e ) {
      // expected
    } finally {
      try {
        stream.close();
      } catch( IOException e ) {
        fail( "Unable to close input stream." );
      }
    }
  }

  public void testSetForeground() {
    Tree tree = new Tree( shell, SWT.CHECK );
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    Color color = display.getSystemColor( SWT.COLOR_RED );
    treeItem.setForeground( color );
    assertEquals( color, treeItem.getForeground() );
    treeItem.setForeground( null );
    assertEquals( tree.getForeground(), treeItem.getForeground() );
    Color color2 = new Color( display, 255, 0, 0 );
    color2.dispose();
    try {
      treeItem.setForeground( color2 );
      fail( "Disposed Image must not be set." );
    } catch( IllegalArgumentException expected ) {
      // expected
    }
  }

  public void testSetBackground() {
    Tree tree = new Tree( shell, SWT.CHECK );
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    Color color = display.getSystemColor( SWT.COLOR_RED );
    treeItem.setBackground( color );
    assertEquals( color, treeItem.getBackground() );
    treeItem.setBackground( null );
    assertEquals( tree.getBackground(), treeItem.getBackground() );
    // Test for the case that the argument has been disposed
    Color color2 = new Color( display, 0, 255, 0 );
    color2.dispose();
    try {
      treeItem.setBackground( color2 );
      fail( "Disposed color must not be set." );
    } catch( IllegalArgumentException expected ) {
      // expected
    }
  }

  public void testSetForegroundI() {
    Tree tree = new Tree( shell, SWT.NONE );
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    Color red = display.getSystemColor( SWT.COLOR_RED );
    Color blue = display.getSystemColor( SWT.COLOR_BLUE );
    // no columns
    assertEquals( tree.getForeground(), treeItem.getForeground( 0 ) );
    assertEquals( treeItem.getForeground(), treeItem.getForeground( 0 ) );
    treeItem.setForeground( 0, red );
    assertEquals( red, treeItem.getForeground( 0 ) );
    // index beyond range - no error
    treeItem.setForeground( 10, red );
    assertEquals( treeItem.getForeground(), treeItem.getForeground( 10 ) );
    // with columns
    new TreeColumn( tree, SWT.LEFT );
    new TreeColumn( tree, SWT.LEFT );
    // index beyond range - no error
    treeItem.setForeground( 10, red );
    assertEquals( treeItem.getForeground(), treeItem.getForeground( 10 ) );
    treeItem.setForeground( 0, red );
    assertEquals( red, treeItem.getForeground( 0 ) );
    treeItem.setForeground( 0, null );
    assertEquals( tree.getForeground(), treeItem.getForeground( 0 ) );
    treeItem.setForeground( 0, blue );
    treeItem.setForeground( red );
    assertEquals( blue, treeItem.getForeground( 0 ) );
    treeItem.setForeground( 0, null );
    assertEquals( red, treeItem.getForeground( 0 ) );
    treeItem.setForeground( null );
    assertEquals( tree.getForeground(), treeItem.getForeground( 0 ) );
    Color color2 = new Color(display, 255, 0, 0);
    color2.dispose();
    try {
      treeItem.setForeground( 0, color2 );
      fail( "Disposed Image must not be set." );
    } catch( IllegalArgumentException expected ) {
      // expected
    }
  }

  public void testSetFontI() {
    Tree tree = new Tree( shell, SWT.NONE );
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    Font font = Graphics.getFont( "Helvetica", 10, SWT.NORMAL );
    // no columns
    assertTrue( tree.getFont().equals( treeItem.getFont( 0 ) ) );
    assertTrue( treeItem.getFont().equals( treeItem.getFont( 0 ) ) );
    treeItem.setFont( 0, font );
    assertTrue( font.equals( treeItem.getFont( 0 ) ) );
    // index beyond range - no error
    treeItem.setFont( 10, font );
    assertTrue( treeItem.getFont().equals( treeItem.getFont( 10 ) ) );
    // with columns
    new TreeColumn( tree, SWT.LEFT );
    new TreeColumn( tree, SWT.LEFT );
    // index beyond range - no error
    treeItem.setFont( 10, font );
    assertTrue( treeItem.getFont().equals( treeItem.getFont( 10 ) ) );
    treeItem.setFont( 0, font );
    assertTrue( font.equals( treeItem.getFont( 0 ) ) );
    treeItem.setFont( 0, null );
    assertTrue( tree.getFont().equals( treeItem.getFont( 0 ) ) );
    Font font2 = Graphics.getFont( "Helvetica", 20, SWT.NORMAL );
    treeItem.setFont( 0, font );
    treeItem.setFont( font2 );
    assertTrue( font.equals( treeItem.getFont( 0 ) ) );
    treeItem.setFont( 0, null );
    assertTrue( font2.equals( treeItem.getFont( 0 ) ) );
    treeItem.setFont( null );
    assertTrue( tree.getFont().equals( treeItem.getFont( 0 ) ) );
    // Test with a disposed font
    Font font3 = new Font( display, "Testfont", 10, SWT.BOLD );
    font3.dispose();
    try {
      treeItem.setFont( 0, font3 );
      fail( "Disposed font must not be set." );
    } catch( IllegalArgumentException expected ) {
      // expected
    }
  }

  public void testSetFont() {
    Tree tree = new Tree( shell, SWT.NONE );
    Font treeFont = Graphics.getFont( "BeautifullyCraftedTreeFont", 15, SWT.BOLD );
    tree.setFont( treeFont );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    assertSame( treeFont, item.getFont() );
    Font itemFont = Graphics.getFont( "ItemFont", 40, SWT.NORMAL );
    item.setFont( itemFont );
    assertSame( itemFont, item.getFont() );
    item.setFont( null );
    assertSame( treeFont, item.getFont() );
    // Test with a disposed font
    Font font = new Font( display, "Testfont", 10, SWT.BOLD );
    font.dispose();
    try {
      item.setFont( font );
      fail( "Disposed font must not be set." );
    } catch( IllegalArgumentException e ) {
      // Expected Exception
    }
  }

  public void testSetBackgroundI() {
    Tree tree = new Tree( shell, SWT.NONE );
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    Color red = display.getSystemColor( SWT.COLOR_RED );
    Color blue = display.getSystemColor( SWT.COLOR_BLUE );
    // no columns
    assertEquals( tree.getBackground(), treeItem.getBackground( 0 ) );
    assertEquals( treeItem.getBackground(), treeItem.getBackground( 0 ) );
    treeItem.setBackground( 0, red );
    assertEquals( red, treeItem.getBackground( 0 ) );
    // index beyond range - no error
    treeItem.setBackground( 10, red );
    assertEquals( treeItem.getBackground(), treeItem.getBackground( 10 ) );
    // with columns
    new TreeColumn( tree, SWT.LEFT );
    new TreeColumn( tree, SWT.LEFT );
    // index beyond range - no error
    treeItem.setBackground( 10, red );
    assertEquals( treeItem.getBackground(), treeItem.getBackground( 10 ) );
    treeItem.setBackground( 0, red );
    assertEquals( red, treeItem.getBackground( 0 ) );
    treeItem.setBackground( 0, null );
    assertEquals( tree.getBackground(), treeItem.getBackground( 0 ) );
    treeItem.setBackground( 0, blue );
    treeItem.setBackground( red );
    assertEquals( blue, treeItem.getBackground( 0 ) );
    treeItem.setBackground( 0, null );
    assertEquals( red, treeItem.getBackground( 0 ) );
    treeItem.setBackground( null );
    assertEquals( tree.getBackground(), treeItem.getBackground( 0 ) );
    // Test for the case that the argument has been disposed
    Color color = new Color( display, 0, 255, 0 );
    color.dispose();
    try {
      treeItem.setBackground( 0, color );
      fail( "Disposed color must not be set." );
    } catch( IllegalArgumentException expected ) {
      // expected
    }
  }

  public void testGetBoundsEmptyItem() {
    Tree tree = new Tree( shell, SWT.NONE );
    tree.setSize( 200, 200 );
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    TreeItem subItem = new TreeItem( treeItem, 0 );
    assertEquals( new Rectangle( 0, 0, 0, 0 ), subItem.getBounds() );
    treeItem.setText( "foo" );
    assertTrue( treeItem.getBounds().height > 0 );
    assertTrue( treeItem.getBounds().width > 0 );
  }

  public void testGetBoundsForInvalidColumns() {
    Tree tree = new Tree( shell, SWT.NONE );
    tree.setSize( 200, 200 );
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    Rectangle bounds = treeItem.getBounds( 0 );
    assertTrue( bounds.x > 0 && bounds.height > 0 );
    bounds = treeItem.getBounds( -1 );
    assertTrue( bounds.equals( new Rectangle( 0, 0, 0, 0 ) ) );
    bounds = treeItem.getBounds( 1 );
    assertTrue( bounds.equals( new Rectangle( 0, 0, 0, 0 ) ) );
  }

  public void testGetBoundsCollapsedSubItem() {
    Tree tree = new Tree( shell, SWT.NONE );
    tree.setSize( 200, 200 );
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    TreeItem subItem = new TreeItem( treeItem, SWT.NONE );
    Rectangle bounds = subItem.getBounds( 0 );
    assertTrue( bounds.equals( new Rectangle( 0, 0, 0, 0 ) ) );
    treeItem.setExpanded( true );
    bounds = subItem.getBounds( 0 );
    assertTrue( bounds.x > 0 && bounds.height > 0 );
    treeItem.setExpanded( false );
    bounds = subItem.getBounds( 0 );
    assertTrue( bounds.equals( new Rectangle( 0, 0, 0, 0 ) ) );
  }

  public void testGetBoundsExpandedSubItem() {
    Tree tree = new Tree( shell, SWT.NONE );
    tree.setSize( 200, 200 );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    TreeItem subItem = new TreeItem( item, SWT.NONE );
    item.setExpanded( true );
    subItem.setText( "hello" );

    Rectangle subItemBounds = subItem.getBounds( 0 );
    Rectangle itemBounds = item.getBounds( 0 );
    assertTrue( subItemBounds.x > itemBounds.x );
    assertTrue( subItemBounds.y >= itemBounds.y + itemBounds.height );
    Point stringExtent = Graphics.stringExtent( item.getFont(), "hello" );
    assertTrue( subItemBounds.height > stringExtent.y );
    assertTrue( subItemBounds.width > stringExtent.x );
  }

  public void testGetBoundsWithText() {
    String string = "hello";
    Tree tree = new Tree( shell, SWT.NONE );
    tree.setSize( 200, 200 );
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    Point stringExtent = Graphics.stringExtent( treeItem.getFont(), string );
    treeItem.setText( string );
    Rectangle withTextBounds = treeItem.getBounds( 0 );
    treeItem.setText( "" );
    Rectangle noTextBounds = treeItem.getBounds( 0 );
    assertTrue( withTextBounds.x > 0 );
    assertTrue( withTextBounds.height > stringExtent.y );
    assertTrue( withTextBounds.width > stringExtent.x );
    assertTrue( noTextBounds.x > 0 && noTextBounds.height > 0 );
    assertTrue( noTextBounds.width < withTextBounds.width );
  }

  public void testGetBoundsWithImage() {
    Image image = Graphics.getImage( Fixture.IMAGE1 );
    Rectangle imageBounds = image.getBounds();
    Tree tree = new Tree( shell, SWT.NONE );
    tree.setSize( 200, 200 );
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    treeItem.setImage( image );
    Rectangle boundsWithImage = treeItem.getBounds( 0 );
    treeItem.setImage( ( Image )null );
    Rectangle boundsNoImage = treeItem.getBounds( 0 );
    assertTrue( boundsWithImage.x > 0 );
    assertTrue( boundsWithImage.height >= imageBounds.height );
    assertTrue( boundsWithImage.width >= imageBounds.width );
    assertTrue( boundsNoImage.x > 0 && boundsNoImage.height > 0 );
  }

  public void testGetBoundsWithTextAndImage() {
    Image image = Graphics.getImage( Fixture.IMAGE1 );
    Rectangle imageBounds = image.getBounds();
    String string = "hello";
    Tree tree = new Tree( shell, SWT.NONE );
    tree.setSize( 200, 200 );
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    Point stringExtent = Graphics.stringExtent( treeItem.getFont(), string );
    treeItem.setText( string );
    Rectangle boundsTextOnly = treeItem.getBounds( 0 );
    treeItem.setImage( image );
    Rectangle boundsBoth = treeItem.getBounds( 0 );
    int maxHeight = Math.max( stringExtent.y, imageBounds.height );
    assertTrue( boundsBoth.x > 0 && boundsBoth.height > 0 );
    assertTrue( boundsBoth.width > boundsTextOnly.width );
    assertTrue( boundsBoth.width >= stringExtent.x + imageBounds.width );
    assertTrue( boundsBoth.height >= maxHeight );
  }

  public void testGetBoundsSubsequentRootItems() {
    Tree tree = new Tree( shell, SWT.NONE );
    TreeItem rootItem = new TreeItem( tree, SWT.NONE );
    TreeItem subItem = new TreeItem( rootItem, 0 );
    TreeItem rootItem2 = new TreeItem( tree, SWT.NONE );
    Rectangle bounds1 = rootItem.getBounds();
    Rectangle bounds2 = rootItem2.getBounds();
    assertTrue( bounds2.y >= bounds1.y + bounds1.height );
    assertEquals( new Rectangle( 0, 0, 0, 0 ), subItem.getBounds() );
    rootItem.setExpanded( true );

    assertTrue( subItem.getBounds().y >= rootItem.getBounds().y + rootItem.getBounds().height );
    assertTrue( rootItem2.getBounds().y >= subItem.getBounds().y + subItem.getBounds().height );
  }

  public void testGetBoundsSubsequentRootItems2() {
    Tree tree = new Tree( shell, SWT.NONE );
    TreeItem item1 = new TreeItem( tree, SWT.NONE );
    new TreeItem( item1, 0 );
    TreeItem item2 = new TreeItem( tree, SWT.NONE );
    Rectangle before = item2.getBounds();

    item1.setExpanded( true );

    Rectangle after = item2.getBounds();
    assertTrue( after.y > before.y );
  }

  public void testGetBoundsWithColumns() {
    Tree tree = new Tree( shell, SWT.NONE );
    TreeColumn column1 = new TreeColumn( tree, SWT.NONE );
    column1.setText( "foo" );
    column1.setWidth( 100 );
    TreeColumn column2 = new TreeColumn( tree, SWT.NONE );
    column2.setText( "foo" );
    column2.setWidth( 100 );
    TreeColumn column3 = new TreeColumn( tree, SWT.NONE );
    column3.setText( "foo" );
    column3.setWidth( 100 );
    TreeItem rootItem = new TreeItem( tree, SWT.NONE );
    TreeItem rootItem2 = new TreeItem( tree, SWT.NONE );
    TreeItem rootItem3 = new TreeItem( tree, SWT.NONE );
    TreeItem subItem = new TreeItem( rootItem, 0 );
    int rootItemWidth = rootItem.getBounds( 0 ).width;
    assertTrue( rootItemWidth < 100 ); // swt substracts indent
    assertEquals( 100, rootItem2.getBounds( 1 ).width );
    assertEquals( 100, rootItem3.getBounds( 2 ).width );
    assertEquals( 0, subItem.getBounds( 0 ).width );
    rootItem.setExpanded( true );
    assertTrue( subItem.getBounds( 0 ).width < rootItemWidth );
    assertTrue( rootItem.getBounds( 0 ).x > 0 );
    assertEquals( 100, rootItem.getBounds( 1 ).x );
    assertEquals( 200, rootItem.getBounds( 2 ).x );
  }

  public void testGetBoundsWithVisibleHeader() {
    Tree tree = new Tree( shell, SWT.NONE );
    tree.setHeaderVisible( true );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    Rectangle bounds = item.getBounds();
    assertTrue( bounds.y >= tree.getHeaderHeight() );
  }

  public void testBoundsSubItemBug219374() {
    Tree tree = new Tree( shell, SWT.NONE );
    TreeItem root = new TreeItem( tree, SWT.NONE );
    TreeItem root2 = new TreeItem( tree, SWT.NONE );
    TreeItem sub1 = new TreeItem( root, SWT.NONE );
    TreeItem sub2 = new TreeItem( root2, SWT.NONE );
    root2.setExpanded( true );
    // default height is 16
    assertEquals( 0, root.getBounds().y );
    assertEquals( 0, sub1.getBounds().y ); // not expanded
    assertEquals( 27, root2.getBounds().y );
    assertEquals( 54, sub2.getBounds().y );
    // default indent for each level is 16
    assertEquals( 16, root.getBounds().x );
    assertEquals( 0, sub1.getBounds().x ); // not expanded
    assertEquals( 16, root2.getBounds().x );
    assertEquals( 32, sub2.getBounds().x );
  }

  public void testGetBoundsWithColumnsAndScrolling() {
    Tree tree = new Tree( shell, SWT.NONE );
    tree.setSize( 150, 200 );
    TreeColumn column1 = new TreeColumn( tree, SWT.NONE );
    column1.setText( "foo" );
    column1.setWidth( 100 );
    TreeColumn column2 = new TreeColumn( tree, SWT.NONE );
    column2.setText( "foo" );
    column2.setWidth( 100 );
    TreeColumn column3 = new TreeColumn( tree, SWT.NONE );
    column3.setText( "foo" );
    column3.setWidth( 100 );
    TreeItem rootItem = new TreeItem( tree, SWT.NONE );
    TreeItem subItem = new TreeItem( rootItem, 0 );
    rootItem.setExpanded( true );
    tree.showColumn( column3 );
    assertEquals( -134, rootItem.getBounds( 0 ).x );
    assertEquals( -50, rootItem.getBounds( 1 ).x );
    assertEquals( 50, rootItem.getBounds( 2 ).x );
    assertEquals( -118, subItem.getBounds( 0 ).x );
    assertEquals( -50, subItem.getBounds( 1 ).x );
    assertEquals( 50, subItem.getBounds( 2 ).x );
  }

  public void testTreeItemAdapter() {
    Tree tree = new Tree( shell, SWT.SINGLE );
    int columnCount = 3;
    createColumns( tree, columnCount );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    ITreeItemAdapter adapter = item.getAdapter( ITreeItemAdapter.class );
    assertTrue( Arrays.equals( new Color[ 3 ], adapter.getCellBackgrounds() ) );
    assertTrue( Arrays.equals( new Color[ 3 ], adapter.getCellForegrounds() ) );
    assertTrue( Arrays.equals( new Font[ 3 ], adapter.getCellFonts() ) );
    Color bgColor = display.getSystemColor( SWT.COLOR_YELLOW );
    Color fgColor = display.getSystemColor( SWT.COLOR_BLUE );
    Font font = Graphics.getFont( "Helvetica", 12, SWT.NORMAL );
    item.setBackground( 0, bgColor );
    item.setForeground( 0, fgColor );
    item.setFont( 1, font );
    assertEquals( columnCount, adapter.getCellBackgrounds().length );
    assertEquals( columnCount, adapter.getCellForegrounds().length );
    assertEquals( columnCount, adapter.getCellFonts().length );
    assertEquals( bgColor, adapter.getCellBackgrounds()[ 0 ] );
    assertEquals( fgColor, adapter.getCellForegrounds()[ 0 ] );
    assertEquals( font, adapter.getCellFonts()[ 1 ] );
    assertNull( adapter.getCellBackgrounds()[ 1 ] );
    assertNull( adapter.getCellForegrounds()[ 1 ] );
    assertNull( adapter.getCellFonts()[ 0 ] );
  }

  public void testGetImageBoundsInvalidIndex() {
    Tree tree = new Tree( shell, SWT.SINGLE );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    assertEquals( new Rectangle( 0, 0, 0, 0 ), item.getImageBounds( 1 ) );
    assertEquals( new Rectangle( 0, 0, 0, 0 ), item.getImageBounds( -1 ) );
  }

  public void testGetImageBoundsColumns() {
    Tree tree = new Tree( shell, SWT.SINGLE );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    TreeColumn c1 = new TreeColumn( tree, SWT.NONE );
    c1.setWidth( 100 );
    TreeColumn c2 = new TreeColumn( tree, SWT.NONE );
    c2.setWidth( 100 );
    TreeColumn c3 = new TreeColumn( tree, SWT.NONE );
    c3.setWidth( 100 );
    item.setText( new String[]{
      "foo", "bar", "baz"
    } );
    Rectangle col0Bounds = item.getImageBounds( 0 );
    Rectangle col1Bounds = item.getImageBounds( 1 );
    Rectangle col2Bounds = item.getImageBounds( 2 );
    // without images, width and height should be 0
    assertEquals( 0, col0Bounds.height );
    assertEquals( 0, col0Bounds.width );
    assertEquals( 0, col1Bounds.height );
    assertEquals( 0, col1Bounds.width );
    assertEquals( 0, col2Bounds.height );
    assertEquals( 0, col2Bounds.width );
    // but x and y have to be set correctly
    assertTrue( col0Bounds.x > 0 ); // > 0 as we have an indent
    assertEquals( 106, col1Bounds.x );
    assertEquals( 206, col2Bounds.x );
    Image image = Graphics.getImage( Fixture.IMAGE1 );
    item.setImage( 0, image );
    item.setImage( 1, image );
    item.setImage( 2, image );
    Rectangle imageBounds = image.getBounds();
    col0Bounds = item.getImageBounds( 0 );
    col1Bounds = item.getImageBounds( 1 );
    col2Bounds = item.getImageBounds( 2 );
    assertEquals( imageBounds.height, col0Bounds.height );
    assertEquals( imageBounds.width, col0Bounds.width );
    assertEquals( imageBounds.height, col1Bounds.height );
    assertEquals( imageBounds.width, col1Bounds.width );
    assertEquals( imageBounds.height, col2Bounds.height );
    assertEquals( imageBounds.width, col2Bounds.width );
    assertTrue( col1Bounds.x > col0Bounds.x );
    assertTrue( col2Bounds.x > col1Bounds.x );
    TreeItem item2 = new TreeItem( tree, SWT.NONE );
    item2.setImage( 0, image );
    assertTrue( col0Bounds.y < item2.getImageBounds( 0 ).y );
  }

  public void testGetImageBoundsIndexOutOfBoundsBug() {
    Tree tree = new Tree( shell, SWT.SINGLE );
    new TreeItem( tree, SWT.NONE );
    new TreeItem( tree, SWT.NONE );
    new TreeItem( tree, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    TreeColumn c1 = new TreeColumn( tree, SWT.NONE );
    c1.setWidth( 100 );
    TreeColumn c2 = new TreeColumn( tree, SWT.NONE );
    c2.setWidth( 100 );
    TreeColumn c3 = new TreeColumn( tree, SWT.NONE );
    c3.setWidth( 100 );
    item.setText( new String[]{
      "foo", "bar", "baz"
    } );
    Rectangle col0Bounds = item.getImageBounds( 0 );
    Rectangle col1Bounds = item.getImageBounds( 1 );
    Rectangle col2Bounds = item.getImageBounds( 2 );
    // without images, width and height should be 0
    assertEquals( 0, col0Bounds.height );
    assertEquals( 0, col0Bounds.width );
    assertEquals( 0, col1Bounds.height );
    assertEquals( 0, col1Bounds.width );
    assertEquals( 0, col2Bounds.height );
    assertEquals( 0, col2Bounds.width );
    // but x and y have to be set correctly
    assertTrue( col0Bounds.x > 0 ); // > 0 as we have an indent
    assertEquals( 106, col1Bounds.x );
    assertEquals( 206, col2Bounds.x );
    Image image = Graphics.getImage( Fixture.IMAGE1 );
    item.setImage( 0, image );
    item.setImage( 1, image );
    item.setImage( 2, image );
    Rectangle imageBounds = image.getBounds();
    col0Bounds = item.getImageBounds( 0 );
    col1Bounds = item.getImageBounds( 1 );
    col2Bounds = item.getImageBounds( 2 );
    assertEquals( imageBounds.height, col0Bounds.height );
    assertEquals( imageBounds.width, col0Bounds.width );
    assertEquals( imageBounds.height, col1Bounds.height );
    assertEquals( imageBounds.width, col1Bounds.width );
    assertEquals( imageBounds.height, col2Bounds.height );
    assertEquals( imageBounds.width, col2Bounds.width );
    assertTrue( col1Bounds.x > col0Bounds.x );
    assertTrue( col2Bounds.x > col1Bounds.x );
    TreeItem item2 = new TreeItem( tree, SWT.NONE );
    item2.setImage( 0, image );
    assertTrue( col0Bounds.y < item2.getImageBounds( 0 ).y );
  }

  public void testGetImageBoundsForTreeColumn() {
    Tree tree = new Tree( shell, SWT.SINGLE );
    createColumns( tree, 1 );
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    treeItem.setImage( 1, Graphics.getImage( Fixture.IMAGE1 ) );

    assertEquals( treeItem.getImageBounds( 0 ).x, tree.getVisualCellLeft( 0, treeItem ) );
  }

  public void testDynamicColumnCountAttributes() {
    Tree tree = new Tree( shell, SWT.SINGLE );
    createColumns( tree, 1 );
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    treeItem.setFont( 0, display.getSystemFont() );
    treeItem.setForeground( 0, display.getSystemColor( SWT.COLOR_BLACK ) );
    treeItem.setBackground( 0, display.getSystemColor( SWT.COLOR_BLACK ) );
    treeItem.setImage( 0, Graphics.getImage( Fixture.IMAGE1 ) );
    createColumns( tree, 1 );
    treeItem.setFont( 1, display.getSystemFont() );
    treeItem.setForeground( 1, display.getSystemColor( SWT.COLOR_BLACK ) );
    treeItem.setBackground( 1, display.getSystemColor( SWT.COLOR_BLACK ) );
    treeItem.setImage( 1, Graphics.getImage( Fixture.IMAGE1 ) );
  }

  public void testTextBounds() {
    Tree tree = new Tree( shell, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    TreeColumn column1 = new TreeColumn( tree, SWT.NONE );
    column1.setWidth( 50 );
    TreeColumn column2 = new TreeColumn( tree, SWT.NONE );
    column2.setWidth( 50 );
    item.setText( 0, "col1" );
    item.setText( 1, "col2" );

    Rectangle textBounds1 = item.getTextBounds( 0 );
    Rectangle textBounds2 = item.getTextBounds( 1 );
    assertTrue( textBounds1.x + textBounds1.width <= textBounds2.x );
  }

  public void testTextBoundsWithInvalidIndex() {
    Tree tree = new Tree( shell, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    item.setText( "abc" );
    // without columns
    assertEquals( new Rectangle( 0, 0, 0, 0 ), item.getTextBounds( 123 ) );
    // with column
    new TreeColumn( tree, SWT.NONE );
    assertEquals( new Rectangle( 0, 0, 0, 0 ), item.getTextBounds( 123 ) );
  }

  public void testTextBoundsWithImageAndColumns() {
    Tree tree = new Tree( shell, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    TreeColumn column = new TreeColumn( tree, SWT.NONE );
    column.setWidth( 200 );

    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );
    item.setImage( 0, image );
    assertTrue( item.getTextBounds( 0 ).x > image.getBounds().width );
    item.setImage( 0, null );
    assertTrue( item.getTextBounds( 0 ).x < image.getBounds().width );
  }

  public void testTextBoundsWithChangedFont() {
    Tree tree = new Tree( shell, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    item.setText( "abc" );
    Rectangle origBounds = item.getTextBounds( 0 );
    item.setFont( Graphics.getFont( "Helvetica", 50, SWT.BOLD ) );
    Rectangle actualBounds = item.getTextBounds( 0 );
    assertTrue( actualBounds.width > origBounds.width );
    item.setFont( null );
    actualBounds = item.getTextBounds( 0 );
    assertEquals( origBounds, actualBounds );
  }

  public void testTextBoundsWithCheckboxTree() {
    Tree tree = new Tree( shell, SWT.CHECK );
    TreeColumn column = new TreeColumn( tree, SWT.LEFT );
    column.setWidth( 100 );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    item.setText( "rama rama ding dong" );
    Rectangle textBounds = item.getTextBounds( 0 );
    // Item 0 must share the first column with the check box
    assertTrue( textBounds.width < 84 );
  }

  public void testTextBoundsWithCollapsedParentItem() {
    Tree tree = new Tree( shell, SWT.NONE );
    TreeColumn column = new TreeColumn( tree, SWT.LEFT );
    column.setWidth( 100 );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    item.setText( "item" );
    item.setExpanded( false );
    TreeItem subitem = new TreeItem( item, SWT.NONE );
    subitem.setText( "subitem" );
    Rectangle emptyBounds = new Rectangle( 0, 0, 0, 0 );
    assertTrue( emptyBounds.equals( subitem.getTextBounds( 0 ) ) );
    item.setExpanded( true );
    assertFalse( emptyBounds.equals( subitem.getTextBounds( 0 ) ) );
  }

  public void testNewItemWithIndex() {
    Tree tree = new Tree( shell, SWT.NONE );
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    treeItem.setText( "1" );
    TreeItem treeItem2 = new TreeItem( tree, SWT.NONE, 0 );
    treeItem2.setText( "2" );
    assertEquals( 1, tree.indexOf( treeItem ) );
    assertEquals( 0, tree.indexOf( treeItem2 ) );
    // Try to add an item with an index which is out of bounds
    try {
      new TreeItem( tree, SWT.NONE, tree.getItemCount() + 8 );
      String msg
        = "Index out of bounds expected when creating an item with "
        + "index > itemCount";
      fail( msg );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    // Try to add an item with a negative index
    try {
      new TreeItem( tree, SWT.NONE, -1 );
      String msg
        = "Index out of bounds expected when creating an item with "
        + "index == -1";
      fail( msg );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testNewItemWithIndexAsChild() {
    Tree tree = new Tree( shell, SWT.NONE );
    TreeItem root = new TreeItem( tree, SWT.NONE );
    root.setText( "root" );
    TreeItem treeItem = new TreeItem( root, SWT.NONE );
    treeItem.setText( "1" );
    TreeItem treeItem2 = new TreeItem( root, SWT.NONE, 0 );
    treeItem2.setText( "2" );
    assertEquals( 0, tree.indexOf( root ) );
    assertEquals( 1, root.indexOf( treeItem ) );
    assertEquals( 0, root.indexOf( treeItem2 ) );
  }

  //////////
  // VIRTUAL

  public void testVirtualGetItemOutOfBounds() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    item.setItemCount( 10 );
    try {
      item.getItem( 10 );
      fail();
    } catch( IllegalArgumentException ex ) {
      // expected
    }
  }

  public void testVirtualGetItemDoesNotFireSetDataEvent() {
    final LoggingListener log = new LoggingListener();
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    tree.setItemCount( 100 );
    tree.setSize( 100, 100 );
    tree.addListener( SWT.SetData, log );
    tree.getItem( 99 );
    assertEquals( 0, log.size() );
  }

  public void testVirtualGetters() {
    Tree tree = new Tree( shell, SWT.VIRTUAL | SWT.CHECK );
    tree.setItemCount( 100 );
    tree.setSize( 100, 100 );
    final Color color = tree.getDisplay().getSystemColor( SWT.COLOR_RED );
    final Font font = new Font( display, new FontData( "serif", 10, 0 ) );
    final Image image = display.getSystemImage( SWT.ICON_ERROR );
    tree.addListener( SWT.SetData, new Listener() {
      public void handleEvent( Event event ) {
        TreeItem item = ( ( TreeItem )event.item );
        item.setBackground( color );
        item.setText( "foo" );
        item.setChecked( true );
        item.setFont( font );
        item.setForeground( color );
        item.setGrayed( true );
        item.setImage( image );
      }
    } );
    assertTrue( tree.getItem( 93 ).getGrayed() );
    assertTrue( tree.getItem( 94 ).getChecked() );
    assertEquals( font, tree.getItem( 96 ).getFont() );
    assertEquals( color, tree.getItem( 97 ).getForeground() );
    assertEquals( "foo", tree.getItem( 98 ).getText() );
    assertEquals( color, tree.getItem( 99 ).getBackground() );
    assertEquals( image, tree.getItem( 95 ).getImage() );
  }

  public void testGetterFireSetDataOnlyOnce() {
    final LoggingListener log = new LoggingListener();
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    tree.setItemCount( 100 );
    tree.setSize( 100, 100 );
    tree.addListener( SWT.SetData, log );
    TreeItem item = tree.getItem( 99 );
    item.getBackground();
    item.getBackground();
    item.getForeground();
    item.getForeground();
    assertEquals( 1, log.size() );
  }

  public void testVirtualSetDataEventItemIndexOnGetTextBounds() {
    final LoggingListener log = new LoggingListener();
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    createColumns( tree, 3 );
    tree.setItemCount( 100 );
    tree.setSize( 100, 100 );
    tree.addListener( SWT.SetData, log );
    TreeItem item = tree.getItem( 99 );
    item.getTextBounds( 2 );
    assertEquals( 1, log.size() );
    assertEquals( 99, log.get( 0 ).index );
  }

  public void testVirtualSetExpanded() {
    final Tree tree = new Tree( shell, SWT.VIRTUAL );
    final LoggingListener log = new LoggingListener();
    tree.addListener( SWT.SetData, log );
    tree.setItemCount( 100 );
    shell.open();
    tree.setSize( 100, 100 );
    log.clear();

    TreeItem item = tree.getItem( 99 );
    item.setItemCount( 1 );
    item.setExpanded( true );

    assertEquals( 0, log.size() );
    assertTrue( item.isCached() );
  }

  public void testVirtualSetExpandedWithoutSubItems() {
    final Tree tree = new Tree( shell, SWT.VIRTUAL );
    final LoggingListener log = new LoggingListener();
    tree.addListener( SWT.SetData, log );
    tree.setItemCount( 100 );
    shell.open();
    tree.setSize( 100, 100 );
    log.clear();

    TreeItem item = tree.getItem( 99 );
    item.setExpanded( true );

    assertEquals( 0, log.size() );
    assertFalse( item.getExpanded() );
    assertFalse( item.isCached() );
  }

  public void testVirtualSetter() {
    Color color = display.getSystemColor( SWT.COLOR_RED );
    Font font = new Font( display, new FontData( "serif", 10, 0 ) );
    Image image = display.getSystemImage( SWT.ICON_ERROR );
    final Tree tree = new Tree( shell, SWT.VIRTUAL | SWT.CHECK );
    final LoggingListener log = new LoggingListener();
    tree.addListener( SWT.SetData, log );
    tree.setItemCount( 100 );
    shell.open();
    tree.setSize( 100, 100 );
    log.clear();

    tree.getItem( 92 ).setForeground( 0, color );
    assertTrue( tree.getItem( 92 ).isCached() );
    assertEquals( color, tree.getItem( 92 ).getForeground( 0 ) );

    tree.getItem( 93 ).setGrayed( true );
    assertTrue( tree.getItem( 93 ).isCached() );
    assertTrue( tree.getItem( 93 ).getGrayed() );

    tree.getItem( 94 ).setChecked( true );
    assertTrue( tree.getItem( 94 ).isCached() );
    assertTrue( tree.getItem( 94 ).getChecked() );

    tree.getItem( 95 ).setImage( image );
    assertTrue( tree.getItem( 95 ).isCached() );
    assertEquals( image, tree.getItem( 95 ).getImage() );

    tree.getItem( 96 ).setFont( font );
    assertTrue( tree.getItem( 96 ).isCached() );
    assertEquals( font, tree.getItem( 96 ).getFont() );

    tree.getItem( 97 ).setForeground( color );
    assertTrue( tree.getItem( 97 ).isCached() );
    assertEquals( color, tree.getItem( 97 ).getForeground() );

    tree.getItem( 98 ).setText( "foo" );
    assertTrue( tree.getItem( 98 ).isCached() );
    assertEquals( "foo", tree.getItem( 98 ).getText() );

    tree.getItem( 99 ).setBackground( color );
    assertTrue( tree.getItem( 99 ).isCached() );
    assertEquals( color, tree.getItem( 99 ).getBackground() );

    assertEquals( 0, log.size() );
  }

  public void testVirtualNonCheckTreeSetter() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    tree.setItemCount( 100 );
    shell.open();
    tree.setSize( 100, 100 );
    tree.getItem( 93 ).setGrayed( true );
    assertFalse( tree.getItem( 93 ).isCached() );
    tree.getItem( 94 ).setChecked( true );
    assertFalse( tree.getItem( 94 ).isCached() );
  }

  public void testVirtualCheckTreeSetter() {
    Tree tree = new Tree( shell, SWT.VIRTUAL | SWT.CHECK );
    tree.setItemCount( 100 );
    shell.open();
    tree.setSize( 100, 100 );
    tree.getItem( 93 ).setGrayed( false );
    assertFalse( tree.getItem( 93 ).isCached() );
    tree.getItem( 94 ).setChecked( false );
    assertFalse( tree.getItem( 94 ).isCached() );
  }

  public void testVirtualSetItemCount() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    LoggingListener log = new LoggingListener();
    tree.addListener( SWT.SetData, log );
    tree.setItemCount( 100 );
    shell.open();
    tree.setSize( 100, 100 );
    log.clear();

    tree.getItem( 93 ).setItemCount( 22 );

    assertFalse( tree.getItem( 93 ).isCached() );
    assertEquals( 0, log.size() );
    assertEquals( 22, tree.getItem( 93 ).getItemCount() );
    assertTrue( tree.getItem( 93 ).isCached() );
    assertEquals( 1, log.size() );
  }

  public void testVirtualSetExpandedWithSetItemCount() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    LoggingListener log = new LoggingListener();
    tree.addListener( SWT.SetData, log );
    tree.setItemCount( 100 );
    tree.setSize( 100, 100 );
    shell.open();
    log.clear();
    tree.getItem( 99 ).setItemCount( 1 );
    assertFalse( tree.getItem( 99 ).isCached() );
    tree.getItem( 99 ).setExpanded( true );
    assertEquals( 0, log.size() );
    assertTrue( tree.getItem( 99 ).isCached() );
    assertTrue( tree.getItem( 99 ).getExpanded() );
  }

  public void testVirtualSetExpandedWithoutSetItemCount() {
    Tree tree = new Tree( shell, SWT.SINGLE );
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    treeItem.setExpanded( true );
    assertFalse( treeItem.getExpanded() );
    treeItem.setItemCount( 3 );
    treeItem.setExpanded( true );
    assertTrue( treeItem.getExpanded() );
  }

  public void testVirtualSetItemCountNegative() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    tree.setItemCount( -100 );
    assertEquals( 0, tree.getItemCount() );
  }

  public void testVirtualClear() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    TreeItem parentItem = new TreeItem( tree, SWT.NONE );
    TreeItem item = new TreeItem( parentItem, SWT.NONE );
    item.getText();
    assertTrue( item.isCached() );
    parentItem.clear( 0, false );
    assertFalse( item.isCached() );
  }

  public void testVirtualClearAll_DoesNotRequestData() {
    LoggingListener log = new LoggingListener();
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    TreeItem parentItem = new TreeItem( tree, SWT.NONE );
    TreeItem item = new TreeItem( parentItem, SWT.NONE );
    item.setText( "item" ); // materialize the item
    tree.addListener( SWT.SetData, log );

    parentItem.clearAll( true );

    assertEquals( 0, log.size() );
    assertFalse( item.isCached() );
  }

  public void testVirtualSetDataEventsOnSetExpand() {
    LoggingListener log = new LoggingListener();
    final Tree tree = new Tree( shell, SWT.VIRTUAL );
    tree.addListener( SWT.SetData, log );
    tree.setItemCount( 1 );
    TreeItem item = tree.getItem( 0 );
    item.setItemCount( 10 );
    tree.setSize( 100, 180 );
    assertEquals( 1, log.size() );
    log.clear();
    item.setExpanded( true );
    assertEquals( 6, log.size() );
  }

  public void testInsertColumn_ShiftData_() {
    Tree tree = new Tree( shell, SWT.BORDER );
    createColumns( tree, 3 );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    TreeItem subitem = new TreeItem( item, SWT.NONE );
    for( int i = 0; i < 3; i++ ) {
      item.setText( i, "cell" + i );
      subitem.setText( i, "subcell" + i );
    }

    new TreeColumn( tree, SWT.NONE, 1 );

    assertEquals( "cell0", item.getText( 0 ) );
    assertEquals( "", item.getText( 1 ) );
    assertEquals( "cell1", item.getText( 2 ) );
    assertEquals( "cell2", item.getText( 3 ) );
    assertEquals( "subcell0", subitem.getText( 0 ) );
    assertEquals( "", subitem.getText( 1 ) );
    assertEquals( "subcell1", subitem.getText( 2 ) );
    assertEquals( "subcell2", subitem.getText( 3 ) );
  }

  public void testRemoveColumn_RemoveData_() {
    Tree tree = new Tree( shell, SWT.BORDER );
    createColumns( tree, 3 );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    TreeItem subitem = new TreeItem( item, SWT.NONE );
    for( int i = 0; i < 3; i++ ) {
      item.setText( i, "cell" + i );
      subitem.setText( i, "subcell" + i );
    }

    tree.getColumn( 1 ).dispose();

    assertEquals( "cell0", item.getText( 0 ) );
    assertEquals( "cell2", item.getText( 1 ) );
    assertEquals( "subcell0", subitem.getText( 0 ) );
    assertEquals( "subcell2", subitem.getText( 1 ) );
  }

  public void testVirtualItemNotCachedInitially() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );

    tree.setItemCount( 1 );

    TreeItem item = tree.getItem( 0 );
    assertFalse( item.isCached() );
  }

  public void testVirtualGetBackground() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    tree.addListener( SWT.SetData, new Listener() {
      public void handleEvent( Event event ) {
        TreeItem item = ( TreeItem )event.item;
        item.setBackground( display.getSystemColor( SWT.COLOR_BLUE ) );
      }
    } );

    tree.setItemCount( 1 );

    TreeItem item = tree.getItem( 0 );
    assertEquals( display.getSystemColor( SWT.COLOR_BLUE ), item.getBackground() );
    assertTrue( item.isCached() );
  }

  public void testVirtualGetBackgroundWithDisposedItem() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    tree.addListener( SWT.SetData, new DisposingSetDataListener() );

    tree.setItemCount( 1 );

    TreeItem item = tree.getItem( 0 );
    try {
      item.getBackground();
      fail();
    } catch( SWTException expected ) {
      assertEquals( "Widget is disposed", expected.getMessage() );
    }
  }

  public void testVirtualGetCellBackground() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    createColumns( tree, 3 );
    tree.addListener( SWT.SetData, new Listener() {
      public void handleEvent( Event event ) {
        TreeItem item = ( TreeItem )event.item;
        item.setBackground( 1, display.getSystemColor( SWT.COLOR_BLUE ) );
      }
    } );

    tree.setItemCount( 1 );

    TreeItem item = tree.getItem( 0 );
    assertEquals( display.getSystemColor( SWT.COLOR_BLUE ), item.getBackground( 1 ) );
    assertTrue( item.isCached() );
  }

  public void testVirtualGetCellBackgroundWithDisposedItem() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    createColumns( tree, 3 );
    tree.addListener( SWT.SetData, new DisposingSetDataListener() );

    tree.setItemCount( 1 );

    TreeItem item = tree.getItem( 0 );
    try {
      item.getBackground( 1 );
      fail();
    } catch( SWTException expected ) {
      assertEquals( "Widget is disposed", expected.getMessage() );
    }
  }

  public void testVirtualGetForeground() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    tree.addListener( SWT.SetData, new Listener() {
      public void handleEvent( Event event ) {
        TreeItem item = ( TreeItem )event.item;
        item.setForeground( display.getSystemColor( SWT.COLOR_BLUE ) );
      }
    } );

    tree.setItemCount( 1 );

    TreeItem item = tree.getItem( 0 );
    assertEquals( display.getSystemColor( SWT.COLOR_BLUE ), item.getForeground() );
    assertTrue( item.isCached() );
  }

  public void testVirtualGetForegroundWithDisposedItem() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    tree.addListener( SWT.SetData, new DisposingSetDataListener() );

    tree.setItemCount( 1 );

    TreeItem item = tree.getItem( 0 );
    try {
      item.getForeground();
      fail();
    } catch( SWTException expected ) {
      assertEquals( "Widget is disposed", expected.getMessage() );
    }
  }

  public void testVirtualGetCellForeground() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    createColumns( tree, 3 );
    tree.addListener( SWT.SetData, new Listener() {
      public void handleEvent( Event event ) {
        TreeItem item = ( TreeItem )event.item;
        item.setForeground( 1, display.getSystemColor( SWT.COLOR_BLUE ) );
      }
    } );

    tree.setItemCount( 1 );

    TreeItem item = tree.getItem( 0 );
    assertEquals( display.getSystemColor( SWT.COLOR_BLUE ), item.getForeground( 1 ) );
    assertTrue( item.isCached() );
  }

  public void testVirtualGetCellForegroundWithDisposedItem() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    createColumns( tree, 3 );
    tree.addListener( SWT.SetData, new DisposingSetDataListener() );

    tree.setItemCount( 1 );

    TreeItem item = tree.getItem( 0 );
    try {
      item.getForeground( 1 );
      fail();
    } catch( SWTException expected ) {
      assertEquals( "Widget is disposed", expected.getMessage() );
    }
  }

  public void testVirtualGetText() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    tree.addListener( SWT.SetData, new Listener() {
      public void handleEvent( Event event ) {
        TreeItem item = ( TreeItem )event.item;
        item.setText( "foo" );
      }
    } );

    tree.setItemCount( 1 );

    TreeItem item = tree.getItem( 0 );
    assertEquals( "foo", item.getText() );
    assertTrue( item.isCached() );
  }

  public void testVirtualGetTextWithDisposedItem() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    tree.addListener( SWT.SetData, new DisposingSetDataListener() );

    tree.setItemCount( 1 );

    TreeItem item = tree.getItem( 0 );
    try {
      item.getText();
      fail();
    } catch( SWTException expected ) {
      assertEquals( "Widget is disposed", expected.getMessage() );
    }
  }

  public void testVirtualGetCellText() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    createColumns( tree, 3 );
    tree.addListener( SWT.SetData, new Listener() {
      public void handleEvent( Event event ) {
        TreeItem item = ( TreeItem )event.item;
        item.setText( 1, "foo" );
      }
    } );

    tree.setItemCount( 1 );

    TreeItem item = tree.getItem( 0 );
    assertEquals( "foo", item.getText( 1 ) );
    assertTrue( item.isCached() );
  }

  public void testVirtualGetCellTextWithDisposedItem() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    createColumns( tree, 3 );
    tree.addListener( SWT.SetData, new DisposingSetDataListener() );

    tree.setItemCount( 1 );

    TreeItem item = tree.getItem( 0 );
    try {
      item.getText( 1 );
      fail();
    } catch( SWTException expected ) {
      assertEquals( "Widget is disposed", expected.getMessage() );
    }
  }

  public void testVirtualGetFont() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    tree.addListener( SWT.SetData, new Listener() {
      public void handleEvent( Event event ) {
        TreeItem item = ( TreeItem )event.item;
        item.setFont( new Font( display, "Times", 10, SWT.BOLD ) );
      }
    } );

    tree.setItemCount( 1 );

    TreeItem item = tree.getItem( 0 );
    assertEquals( new Font( display, "Times", 10, SWT.BOLD ), item.getFont() );
    assertTrue( item.isCached() );
  }

  public void testVirtualGetFontWithDisposedItem() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    tree.addListener( SWT.SetData, new DisposingSetDataListener() );

    tree.setItemCount( 1 );

    TreeItem item = tree.getItem( 0 );
    try {
      item.getFont();
      fail();
    } catch( SWTException expected ) {
      assertEquals( "Widget is disposed", expected.getMessage() );
    }
  }

  public void testVirtualGetCellFont() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    createColumns( tree, 3 );
    tree.addListener( SWT.SetData, new Listener() {
      public void handleEvent( Event event ) {
        TreeItem item = ( TreeItem )event.item;
        item.setFont( 1, new Font( display, "Times", 10, SWT.BOLD ) );
      }
    } );

    tree.setItemCount( 1 );

    TreeItem item = tree.getItem( 0 );
    assertEquals( new Font( display, "Times", 10, SWT.BOLD ), item.getFont( 1 ) );
    assertTrue( item.isCached() );
  }

  public void testVirtualGetCellFontWithDisposedItem() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    createColumns( tree, 3 );
    tree.addListener( SWT.SetData, new DisposingSetDataListener() );

    tree.setItemCount( 1 );

    TreeItem item = tree.getItem( 0 );
    try {
      item.getFont( 1 );
      fail();
    } catch( SWTException expected ) {
      assertEquals( "Widget is disposed", expected.getMessage() );
    }
  }

  public void testVirtualGetImage() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    final Image image = new Image( display, 100, 100 );
    tree.addListener( SWT.SetData, new Listener() {
      public void handleEvent( Event event ) {
        TreeItem item = ( TreeItem )event.item;
        item.setImage( image );
      }
    } );

    tree.setItemCount( 1 );

    TreeItem item = tree.getItem( 0 );
    assertSame( image, item.getImage() );
    assertTrue( item.isCached() );
  }

  public void testVirtualGetImageWithDisposedItem() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    tree.addListener( SWT.SetData, new DisposingSetDataListener() );

    tree.setItemCount( 1 );

    TreeItem item = tree.getItem( 0 );
    try {
      item.getImage();
      fail();
    } catch( SWTException expected ) {
      assertEquals( "Widget is disposed", expected.getMessage() );
    }
  }

  public void testVirtualGetCellImage() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    createColumns( tree, 3 );
    final Image image = new Image( display, 100, 100 );
    tree.addListener( SWT.SetData, new Listener() {
      public void handleEvent( Event event ) {
        TreeItem item = ( TreeItem )event.item;
        item.setImage( 1, image );
      }
    } );

    tree.setItemCount( 1 );

    TreeItem item = tree.getItem( 0 );
    assertSame( image, item.getImage( 1 ) );
    assertTrue( item.isCached() );
  }

  public void testVirtualGetCellImageWithDisposedItem() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    createColumns( tree, 3 );
    tree.addListener( SWT.SetData, new DisposingSetDataListener() );

    tree.setItemCount( 1 );

    TreeItem item = tree.getItem( 0 );
    try {
      item.getImage( 1 );
      fail();
    } catch( SWTException expected ) {
      assertEquals( "Widget is disposed", expected.getMessage() );
    }
  }

  public void testVirtualGetBoundsMaterializeItems() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    tree.addListener( SWT.SetData, new Listener() {
      public void handleEvent( Event event ) {
        TreeItem item = ( TreeItem )event.item;
        item.setText( "Very long long long long long text" );
      }
    } );
    tree.setItemCount( 5 );

    Rectangle bounds =  tree.getItem( 0 ).getBounds();

    assertTrue( bounds.width > 100 );
  }

  public void testVirtualGetBoundsWithDisposedItem() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    createColumns( tree, 3 );
    tree.addListener( SWT.SetData, new DisposingSetDataListener() );

    tree.setItemCount( 1 );

    TreeItem item = tree.getItem( 0 );
    try {
      item.getBounds();
      fail();
    } catch( SWTException expected ) {
      assertEquals( "Widget is disposed", expected.getMessage() );
    }
  }

  public void testVirtualGetImageBoundsMaterializeItems() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    tree.addListener( SWT.SetData, new Listener() {
      public void handleEvent( Event event ) {
        TreeItem item = ( TreeItem )event.item;
        Image image = display.getSystemImage( SWT.ICON_ERROR );
        item.setImage( 0, image );
      }
    } );
    tree.setItemCount( 5 );

    Rectangle bounds =  tree.getItem( 0 ).getImageBounds( 0 );

    assertTrue( bounds.width > 10 );
  }

  public void testVirtualGetImageBoundsWithDisposedItem() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    createColumns( tree, 3 );
    tree.addListener( SWT.SetData, new DisposingSetDataListener() );

    tree.setItemCount( 1 );

    TreeItem item = tree.getItem( 0 );
    try {
      item.getImageBounds( 0 );
      fail();
    } catch( SWTException expected ) {
      assertEquals( "Widget is disposed", expected.getMessage() );
    }
  }

  public void testVirtualGetTextBoundsMaterializeItems() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    tree.addListener( SWT.SetData, new Listener() {
      public void handleEvent( Event event ) {
        TreeItem item = ( TreeItem )event.item;
        item.setText( 0, "Very long long long long long text" );
      }
    } );
    tree.setItemCount( 5 );

    Rectangle bounds =  tree.getItem( 0 ).getTextBounds( 0 );

    assertTrue( bounds.width > 100 );
  }

  public void testVirtualGetTextBoundsWithDisposedItem() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    createColumns( tree, 3 );
    tree.addListener( SWT.SetData, new DisposingSetDataListener() );

    tree.setItemCount( 1 );

    TreeItem item = tree.getItem( 0 );
    try {
      item.getTextBounds( 0 );
      fail();
    } catch( SWTException expected ) {
      assertEquals( "Widget is disposed", expected.getMessage() );
    }
  }

  public void testVirtualGetItemCount() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    createColumns( tree, 3 );
    tree.addListener( SWT.SetData, new Listener() {
      public void handleEvent( Event event ) {
        TreeItem item = ( TreeItem )event.item;
        item.setItemCount( 1 );
      }
    } );

    tree.setItemCount( 1 );

    TreeItem item = tree.getItem( 0 );
    assertEquals( 1, item.getItemCount() );
    assertTrue( item.isCached() );
  }

  public void testVirtualGetItemCountWithDisposedItem() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    createColumns( tree, 3 );
    tree.addListener( SWT.SetData, new DisposingSetDataListener() );

    tree.setItemCount( 1 );

    TreeItem item = tree.getItem( 0 );
    try {
      item.getItemCount();
      fail();
    } catch( SWTException expected ) {
      assertEquals( "Widget is disposed", expected.getMessage() );
    }
  }

  public void testVirtualGetChecked() {
    Tree tree = new Tree( shell, SWT.VIRTUAL | SWT.CHECK );
    createColumns( tree, 3 );
    tree.addListener( SWT.SetData, new Listener() {
      public void handleEvent( Event event ) {
        TreeItem item = ( TreeItem )event.item;
        item.setChecked( true );
      }
    } );

    tree.setItemCount( 1 );

    TreeItem item = tree.getItem( 0 );
    assertTrue( item.getChecked() );
    assertTrue( item.isCached() );
  }

  public void testVirtualGetCheckedWithDisposedItem() {
    Tree tree = new Tree( shell, SWT.VIRTUAL | SWT.CHECK );
    createColumns( tree, 3 );
    tree.addListener( SWT.SetData, new DisposingSetDataListener() );

    tree.setItemCount( 1 );

    TreeItem item = tree.getItem( 0 );
    try {
      item.getChecked();
      fail();
    } catch( SWTException expected ) {
      assertEquals( "Widget is disposed", expected.getMessage() );
    }
  }

  public void testVirtualGetGrayed() {
    Tree tree = new Tree( shell, SWT.VIRTUAL | SWT.CHECK );
    createColumns( tree, 3 );
    tree.addListener( SWT.SetData, new Listener() {
      public void handleEvent( Event event ) {
        TreeItem item = ( TreeItem )event.item;
        item.setGrayed( true );
      }
    } );

    tree.setItemCount( 1 );

    TreeItem item = tree.getItem( 0 );
    assertTrue( item.getGrayed() );
    assertTrue( item.isCached() );
  }

  public void testVirtualGetGrayedWithDisposedItem() {
    Tree tree = new Tree( shell, SWT.VIRTUAL | SWT.CHECK );
    createColumns( tree, 3 );
    tree.addListener( SWT.SetData, new DisposingSetDataListener() );

    tree.setItemCount( 1 );

    TreeItem item = tree.getItem( 0 );
    try {
      item.getGrayed();
      fail();
    } catch( SWTException expected ) {
      assertEquals( "Widget is disposed", expected.getMessage() );
    }
  }

  public void testSetExpandUpdatesFlatIndices() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    tree.setSize( 100, 100 );
    tree.setItemCount( 1 );
    TreeItem item = tree.getItem( 0 );
    item.setItemCount( 1 );

    item.setExpanded( true );

    assertEquals( 1, item.getItem( 0 ).getFlatIndex() );
  }

  public void testUpdateFlatIndicesOnItemDispose() {
    Tree tree = new Tree( shell, SWT.NONE );
    TreeItem item1 = new TreeItem( tree, SWT.NONE );
    TreeItem item2 = new TreeItem( tree, SWT.NONE );
    TreeItem subItem = new TreeItem( item2, SWT.NONE );
    item2.setExpanded( true );

    item1.dispose();

    assertEquals( 1, subItem.getFlatIndex() );
  }

  public void testGetCreatedItems_DoesNotContainNullItems() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    tree.setSize( 100, 100 );
    tree.setItemCount( 1 );
    TreeItem item = tree.getItem( 0 );
    item.setItemCount( 100 );

    item.setExpanded( true );

    assertTrue( item.getCreatedItems().length < 10 );
  }

  public void testGetCreatedItems_DoesNotContainPlaceholderItems() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    tree.setSize( 100, 100 );
    tree.setItemCount( 1 );
    TreeItem item = tree.getItem( 0 );
    item.setItemCount( 100 );

    item.setExpanded( true );
    item.getItems(); // create placeholder items

    assertTrue( item.getCreatedItems().length < 10 );
  }

  public void testUpdateSelectionOnRemoveAll() {
    Tree tree = new Tree( shell, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    TreeItem subItem = new TreeItem( item, SWT.NONE );
    tree.setSelection( subItem );

    item.removeAll();

    assertEquals( 0, tree.getSelectionCount() );
  }

  // see bug 371860
  public void testClearSetDataOrder() {
    final List<String> log = new ArrayList<String>();
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    tree.setSize( 100, 160 );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    item.setText( "item" );
    tree.addListener( SWT.SetData, new Listener() {
      public void handleEvent( Event event ) {
        TreeItem item = ( TreeItem )event.item;
        int i = item.getParent().indexOf( item );
        item.setText( "item " + i );
        log.add( "item" + i + "#SetData" );
      }
    } );
    TreeItem item0 = new TreeItem( item, SWT.NONE );
    item0.setText( "item0" );
    TreeItem item1 = new TreeItem( item, SWT.NONE );
    item1.setText( "item1" );
    item.setExpanded( true );

    log.add( "clear item0" );
    item.clear( 0, false );
    log.add( "clear item1" );
    item.clear( 1, false );
    log.add( "setItemCount" );
    item.setItemCount( 1 );
    display.readAndDispatch();  // redraw the tree

    assertEquals( 4, log.size() );
    assertEquals( "clear item0", log.get( 0 ) );
    assertEquals( "clear item1", log.get( 1 ) );
    assertEquals( "setItemCount", log.get( 2 ) );
    assertEquals( "item0#SetData", log.get( 3 ) );
  }

  public void testSelectionOnItemCollapse_Single() {
    Tree tree = new Tree( shell, SWT.SINGLE );
    TreeItem rootItem = new TreeItem( tree, SWT.NONE );
    TreeItem subItem = new TreeItem( rootItem, SWT.NONE );
    rootItem.setExpanded( true );
    tree.setSelection( new TreeItem[] { subItem } );

    rootItem.setExpanded( false );

    assertEquals( 1, tree.getSelectionCount() );
    assertSame( rootItem, tree.getSelection()[ 0 ] );
  }

  public void testSelectionOnItemCollapse_Multi() {
    Tree tree = new Tree( shell, SWT.MULTI );
    TreeItem rootItem = new TreeItem( tree, SWT.NONE );
    TreeItem subItem = new TreeItem( rootItem, SWT.NONE );
    TreeItem anotherRootItem = new TreeItem( tree, SWT.NONE );
    rootItem.setExpanded( true );
    tree.setSelection( new TreeItem[] { subItem, anotherRootItem } );

    rootItem.setExpanded( false );

    assertEquals( 1, tree.getSelectionCount() );
    assertSame( anotherRootItem, tree.getSelection()[ 0 ] );
  }

  public void testClearPreferredWidthBuffersRecursive() {
    Tree tree = new Tree( shell, SWT.NONE );
    TreeItem rootItem = new TreeItem( tree, SWT.NONE );
    TreeItem subItem = new TreeItem( rootItem, SWT.NONE );
    rootItem.setExpanded( true );

    tree.changed( new Control[ 0 ] );

    assertFalse( rootItem.hasPreferredWidthBuffer( 0 ) );
    assertFalse( subItem.hasPreferredWidthBuffer( 0 ) );
  }

  //////////////////
  // Helping methods

  private static TreeColumn[] createColumns( Tree tree, int count ) {
    TreeColumn[] result = new TreeColumn[ count ];
    for( int i = 0; i < count; i++ ) {
      TreeColumn column = new TreeColumn( tree, SWT.NONE );
      column.setText( i % 2 == 1 ? "foo" : "bar" );
      column.setWidth( 100 );
      result[ i ] = column;
    }
    return result;
  }

  private final static class DisposingSetDataListener implements Listener {

    public void handleEvent( Event event ) {
      TreeItem item = ( TreeItem )event.item;
      item.dispose();
    }
  }
}
