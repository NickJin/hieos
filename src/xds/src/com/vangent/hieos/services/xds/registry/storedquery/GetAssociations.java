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

package com.vangent.hieos.services.xds.registry.storedquery;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.axiom.om.OMElement;

import com.vangent.hieos.xutil.exception.MetadataValidationException;
import com.vangent.hieos.xutil.exception.XdsException;
import com.vangent.hieos.xutil.exception.XdsInternalException;
import com.vangent.hieos.xutil.metadata.structure.Metadata;
import com.vangent.hieos.xutil.metadata.structure.MetadataParser;
import com.vangent.hieos.xutil.response.Response;
import com.vangent.hieos.xutil.query.StoredQuery;
import com.vangent.hieos.xutil.xlog.client.XLogMessage;

public class GetAssociations extends StoredQuery {

	public GetAssociations(HashMap<String, Object> params, boolean return_objects, Response response, XLogMessage log_message, boolean is_secure)
            throws MetadataValidationException {
		super(params, return_objects, response, log_message,  is_secure);

		//                    param name,             required?, multiple?, is string?,   same size as,    alternative
		validate_parm(params, "$uuid",                 true,      true,     true,         null,            null);

        if (this.has_validation_errors) {
			throw new MetadataValidationException("Metadata Validation error present");
        }
    }

	public Metadata run_internal() throws XdsException {
		Metadata metadata;

		ArrayList<String> uuids = get_arraylist_parm("$uuid");

		if (uuids!= null) {
			OMElement ele = get_associations(uuids, null);
			metadata = MetadataParser.parseNonSubmission(ele);
		} 
		else throw new XdsInternalException("GetAssociations Stored Query: $uuid not found as a multi-value parameter");

		return metadata;
	}



}
