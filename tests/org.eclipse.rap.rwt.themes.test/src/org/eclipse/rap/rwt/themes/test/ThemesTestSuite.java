/******************************************************************************* 
* Copyright (c) 2010 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.rap.rwt.themes.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.rap.rwt.themes.test.business.BusinessTheme_Test;
import org.eclipse.rap.rwt.themes.test.fancy.ButtonFancyThemeCustomVariant_Test;
import org.eclipse.rap.rwt.themes.test.fancy.ButtonFancyTheme_Test;
import org.eclipse.rap.rwt.themes.test.fancy.CComboFancyTheme_Test;
import org.eclipse.rap.rwt.themes.test.fancy.CLabelFancyTheme_Test;
import org.eclipse.rap.rwt.themes.test.fancy.CTabFolderFancyTheme_Test;
import org.eclipse.rap.rwt.themes.test.fancy.ComboFancyTheme_Test;
import org.eclipse.rap.rwt.themes.test.fancy.CompositeFancyThemeCustomVariant_Test;
import org.eclipse.rap.rwt.themes.test.fancy.CompositeFancyTheme_Test;
import org.eclipse.rap.rwt.themes.test.fancy.DateTimeFancyTheme_Test;
import org.eclipse.rap.rwt.themes.test.fancy.DisplayFancyTheme_Test;
import org.eclipse.rap.rwt.themes.test.fancy.ExpandBarFancyTheme_Test;
import org.eclipse.rap.rwt.themes.test.fancy.FancyTheme_Test;
import org.eclipse.rap.rwt.themes.test.fancy.GroupFancyTheme_Test;
import org.eclipse.rap.rwt.themes.test.fancy.LabelFancyThemeCustomVariant_Test;
import org.eclipse.rap.rwt.themes.test.fancy.LabelFancyTheme_Test;
import org.eclipse.rap.rwt.themes.test.fancy.LinkFancyTheme_Test;
import org.eclipse.rap.rwt.themes.test.fancy.ListFancyThemeCustomVariant_Test;
import org.eclipse.rap.rwt.themes.test.fancy.ListFancyTheme_Test;
import org.eclipse.rap.rwt.themes.test.fancy.MenuFancyThemeCustomVariant_Test;
import org.eclipse.rap.rwt.themes.test.fancy.MenuFancyTheme_Test;
import org.eclipse.rap.rwt.themes.test.fancy.ProgressBarFancyTheme_Test;
import org.eclipse.rap.rwt.themes.test.fancy.ShellFancyThemeCustomVariant_Test;
import org.eclipse.rap.rwt.themes.test.fancy.ShellFancyTheme_Test;
import org.eclipse.rap.rwt.themes.test.fancy.SliderFancyTheme_Test;
import org.eclipse.rap.rwt.themes.test.fancy.SpinnerFancyTheme_Test;
import org.eclipse.rap.rwt.themes.test.fancy.TabFolderFancyTheme_Test;
import org.eclipse.rap.rwt.themes.test.fancy.TableFancyTheme_Test;
import org.eclipse.rap.rwt.themes.test.fancy.TextFancyTheme_Test;
import org.eclipse.rap.rwt.themes.test.fancy.ToolBarFancyThemeCustomVariant_Test;
import org.eclipse.rap.rwt.themes.test.fancy.ToolTipFancyTheme_Test;
import org.eclipse.rap.rwt.themes.test.fancy.TreeFancyTheme_Test;


public class ThemesTestSuite {
  
  public static Test suite() {
    TestSuite designSuite = new TestSuite( "Tests for RWT Themes" );
    TestSuite cssSuite = new TestSuite( "Tests again CSS files" );
    designSuite.addTest( cssSuite );
    // Add CSS Themes tests
    cssSuite.addTest( new TestSuite( BusinessTheme_Test.class, 
                                     "Business Theme" ) );
    cssSuite.addTest( new TestSuite( FancyTheme_Test.class, 
                                    "Fancy Theme" ) );
    
    TestSuite businessSuite = new TestSuite( "Business Theme Tests" );    
    designSuite.addTest( businessSuite );   

    TestSuite fancySuite = new TestSuite( "Fancy Theme Tests" );
    designSuite.addTest( fancySuite );
    TestSuite suite = fancySuite;
    // Fancy design tests
    suite.addTest( new TestSuite( ButtonFancyTheme_Test.class, "Button" ) );
    suite.addTest( new TestSuite( ButtonFancyThemeCustomVariant_Test.class, 
                                  "Button Custom Variants" ) );
    suite.addTest( new TestSuite( ShellFancyTheme_Test.class, "Shell" ) );
    suite.addTest( new TestSuite( ShellFancyThemeCustomVariant_Test.class, 
                                  "Shell Custom Variants" ) );
    suite.addTest( new TestSuite( TreeFancyTheme_Test.class, "Tree" ) );
    suite.addTest( new TestSuite( TableFancyTheme_Test.class, "Table" ) );
    suite.addTest( new TestSuite( ListFancyTheme_Test.class, "List" ) );
    suite.addTest( new TestSuite( ListFancyThemeCustomVariant_Test.class, 
                                  "List Custom Variants" ) );
    suite.addTest( new TestSuite( LinkFancyTheme_Test.class, "Link" ) );
    suite.addTest( new TestSuite( MenuFancyTheme_Test.class, "Menu" ) );
    suite.addTest( new TestSuite( MenuFancyThemeCustomVariant_Test.class, 
                                  "Menu Custom Variants" ) );
    suite.addTest( new TestSuite( CLabelFancyTheme_Test.class, "CLabel" ) );
    suite.addTest( new TestSuite( LabelFancyTheme_Test.class, "Label" ) );
    suite.addTest( new TestSuite( LabelFancyThemeCustomVariant_Test.class, 
                                  "Label Custom Variants" ) );
    suite.addTest( new TestSuite( ExpandBarFancyTheme_Test.class, 
                                  "ExpandBar" ) );
    suite.addTest( new TestSuite( ComboFancyTheme_Test.class, "Combo" ) );
    suite.addTest( new TestSuite( CComboFancyTheme_Test.class, "CCombo" ) );
    suite.addTest( new TestSuite( SpinnerFancyTheme_Test.class, "Spinner" ) );
    suite.addTest( new TestSuite( DateTimeFancyTheme_Test.class, 
                                  "DateTime" ) );
    suite.addTest( new TestSuite( TextFancyTheme_Test.class, "Text" ) );
    suite.addTest( new TestSuite( GroupFancyTheme_Test.class, "Group" ) );
    suite.addTest( new TestSuite( ProgressBarFancyTheme_Test.class, 
                                  "ProgressBar" ) );
    suite.addTest( new TestSuite( CompositeFancyTheme_Test.class, 
                                  "Composite" ) );
    suite.addTest( new TestSuite( CompositeFancyThemeCustomVariant_Test.class, 
                                  "Composite Custom Variants" ) );
    suite.addTest( new TestSuite( ToolTipFancyTheme_Test.class, "ToolTip" ) );
    suite.addTest( new TestSuite( SliderFancyTheme_Test.class, "Slider" ) );
    suite.addTest( new TestSuite( DisplayFancyTheme_Test.class, "Display" ) );
    suite.addTest( new TestSuite( TabFolderFancyTheme_Test.class, 
                                  "TabFolder" ) );
    suite.addTest( new TestSuite( CTabFolderFancyTheme_Test.class, 
                                  "CTabFolder" ) );
    suite.addTest( new TestSuite( ToolBarFancyThemeCustomVariant_Test.class, 
                                  "ToolBar Custom Variants" ) );
    
    return designSuite;
  }
}
