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
package org.eclipse.rap.e4.apache.jxpath.ri.axes;

import org.eclipse.rap.e4.apache.jxpath.ri.Compiler;
import org.eclipse.rap.e4.apache.jxpath.ri.EvalContext;
import org.eclipse.rap.e4.apache.jxpath.ri.QName;
import org.eclipse.rap.e4.apache.jxpath.ri.compiler.NodeNameTest;
import org.eclipse.rap.e4.apache.jxpath.ri.compiler.NodeTest;
import org.eclipse.rap.e4.apache.jxpath.ri.compiler.NodeTypeTest;
import org.eclipse.rap.e4.apache.jxpath.ri.model.NodeIterator;
import org.eclipse.rap.e4.apache.jxpath.ri.model.NodePointer;

/**
 * EvalContext that walks the "attribute::" axis.
 *
 * @author Dmitri Plotnikov
 * @version $Revision: 652845 $ $Date: 2008-05-02 12:46:46 -0500 (Fri, 02 May 2008) $
 */
public class AttributeContext extends EvalContext {
    private static final QName WILDCARD = new QName(null, "*");

    private NodeTest nodeTest;
    private boolean setStarted = false;
    private NodeIterator iterator;
    private NodePointer currentNodePointer;

    /**
     * Create a new AttributeContext.
     * @param parentContext represents the previous step on the path
     * @param nodeTest is the name of the attribute we are looking for
     */
    public AttributeContext(EvalContext parentContext, NodeTest nodeTest) {
        super(parentContext);
        this.nodeTest = nodeTest;
    }

    public NodePointer getCurrentNodePointer() {
        return currentNodePointer;
    }

    public void reset() {
        setStarted = false;
        iterator = null;
        super.reset();
    }

    public boolean setPosition(int position) {
        if (position < getCurrentPosition()) {
            reset();
        }

        while (getCurrentPosition() < position) {
            if (!nextNode()) {
                return false;
            }
        }
        return true;
    }

    public boolean nextNode() {
        super.setPosition(getCurrentPosition() + 1);
        if (!setStarted) {
            setStarted = true;
            QName name;
            if (nodeTest instanceof NodeNameTest) {
                name = ((NodeNameTest) nodeTest).getNodeName();
            }
            else {
                if (nodeTest instanceof NodeTypeTest
                        && ((NodeTypeTest) nodeTest).getNodeType() == Compiler.NODE_TYPE_NODE) {
                    name = WILDCARD;
                }
                else {
                    iterator = null;
                    return false;
                }
            }
            iterator = parentContext.getCurrentNodePointer().attributeIterator(
                    name);
        }
        if (iterator == null) {
            return false;
        }
        if (!iterator.setPosition(iterator.getPosition() + 1)) {
            return false;
        }
        currentNodePointer = iterator.getNodePointer();
        return true;
    }
}
