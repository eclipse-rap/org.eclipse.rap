/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eclipse.rap.e4.apache.jxpath.ri.model.beans;

import org.eclipse.rap.e4.apache.jxpath.ri.compiler.NodeTest;
import org.eclipse.rap.e4.apache.jxpath.ri.model.NodeIterator;
import org.eclipse.rap.e4.apache.jxpath.ri.model.NodePointer;

/**
 * Combines child node iterators of all elements of a collection into one
 * aggregate child node iterator.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 652845 $ $Date: 2008-05-02 12:46:46 -0500 (Fri, 02 May 2008) $
 */
public class CollectionChildNodeIterator extends CollectionNodeIterator {

    private NodeTest test;

    /**
     * Create a new CollectionChildNodeIterator.
     * @param pointer CollectionPointer
     * @param test child test
     * @param reverse iteration order
     * @param startWith starting pointer
     */
    public CollectionChildNodeIterator(
        CollectionPointer pointer,
        NodeTest test,
        boolean reverse,
        NodePointer startWith) {
        super(pointer, reverse, startWith);
        this.test = test;
    }

    protected NodeIterator getElementNodeIterator(NodePointer elementPointer) {
        return elementPointer.childIterator(test, false, null);
    }
}
