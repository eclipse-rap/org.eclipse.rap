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
package org.eclipse.rap.rwt.internal.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class MnemonicUtil_Test {

  @Test
  public void testRemoveMnemonicsAmpersandCharacters_SingleMnemonics() {
    assertEquals( "foobar", MnemonicUtil.removeAmpersandControlCharacters( "foo&bar" ) );
  }

  @Test
  public void testRemoveMnemonicsAmpersandCharacters_MultipleMnemonics() {
    assertEquals( "foobar", MnemonicUtil.removeAmpersandControlCharacters( "foo&ba&r" ) );
  }

  @Test
  public void testRemoveMnemonicsAmpersandCharacters_DoubleAmpersands() {
    assertEquals( "foo&bar", MnemonicUtil.removeAmpersandControlCharacters( "foo&&ba&r" ) );
  }

  @Test
  public void testRemoveMnemonicsAmpersandCharacters_SequentialAmpersands() {
    assertEquals( "foo&bar", MnemonicUtil.removeAmpersandControlCharacters( "foo&&&bar" ) );
  }

  @Test
  public void testRemoveMnemonicsAmpersandCharacters_LastAmpersand() {
    assertEquals( "foobar", MnemonicUtil.removeAmpersandControlCharacters( "foobar&" ) );
  }

  @Test
  public void testFindMnemonicsCharacterIndex_SingleMnemonics() {
    assertEquals( 3, MnemonicUtil.findMnemonicCharacterIndex( "foo&bar" ) );
  }

  @Test
  public void testFindMnemonicsCharacterIndex_MultipleMnemonics() {
    assertEquals( 5, MnemonicUtil.findMnemonicCharacterIndex( "foo&ba&r" ) );
  }

  @Test
  public void testFindMnemonicsCharacterIndex_DoubleAmpersands() {
    assertEquals( 6, MnemonicUtil.findMnemonicCharacterIndex( "foo&&ba&r" ) );
  }

  @Test
  public void testFindMnemonicsCharacterIndex_SequentialAmpersand() {
    assertEquals( 4, MnemonicUtil.findMnemonicCharacterIndex( "foo&&&bar" ) );
  }

  @Test
  public void testFindMnemonicsCharacterIndex_LastAmpersands() {
    assertEquals( -1, MnemonicUtil.findMnemonicCharacterIndex( "foobar&" ) );
  }

  @Test
  public void testFindMnemonicsCharacterIndex_EmptyString() {
    assertEquals( -1, MnemonicUtil.findMnemonicCharacterIndex( "" ) );
  }

  @Test
  public void testFindMnemonicsCharacterIndex_AmpersandOnly() {
    assertEquals( -1, MnemonicUtil.findMnemonicCharacterIndex( "&" ) );
  }

}
