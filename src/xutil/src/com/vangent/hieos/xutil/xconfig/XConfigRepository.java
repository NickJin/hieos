/*
 * This code is subject to the HIEOS License, Version 1.0
 *
 * Copyright(c) 2008-2009 Vangent, Inc.  All rights reserved.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vangent.hieos.xutil.xconfig;

import java.util.ArrayList;
import java.util.Iterator;
//import java.lang.StringBuffer;
import javax.xml.namespace.QName;
import org.apache.axiom.om.OMElement;

/**
 *
 * @author thumbe
 */
public class XConfigRepository extends XConfigEntity {

    XConfigRegistry localRegistry = null;

    /**
     *
     * @return
     */
    public XConfigRegistry getLocalRegistry() {
        return this.localRegistry;
    }

    /**
     *
     * @param rootNode
     * @param xconf
     */
    protected void parseSpecializedParts(OMElement rootNode, XConfig xconf) {
        // Precondition: registries already parsed.

        OMElement node = rootNode.getFirstChildWithName(new QName("Registry"));
        if (node != null) {
            String registryName = node.getText();
            if ((registryName != null) && !registryName.equals("")) {
                this.localRegistry = xconf.getRegistryByName(registryName);
            }
        }
    }

    /**
     * 
     * @return
     */
    @Override
    public String toString() {
        StringBuffer sbuf = new StringBuffer();
        sbuf.append("\n Repository:");
        sbuf.append("\n   name: " + this.getName());
        sbuf.append("\n   uniqueId: " + this.getUniqueId());
        ArrayList<XConfigTransaction> al = this.getTransactions();
        for (Iterator it = al.iterator(); it.hasNext();) {
            XConfigTransaction txn = (XConfigTransaction) it.next();
            sbuf.append(txn.toString());
        }
        return sbuf.toString();
    }
}
