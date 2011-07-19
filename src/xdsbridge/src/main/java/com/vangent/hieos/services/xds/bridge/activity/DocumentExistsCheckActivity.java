/*
 * This code is subject to the HIEOS License, Version 1.0
 *
 * Copyright(c) 2011 Vangent, Inc.  All rights reserved.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vangent.hieos.services.xds.bridge.activity;

import java.util.List;
import com.vangent.hieos.services.xds.bridge.client.XDSDocumentRegistryClient;
import com.vangent.hieos.services.xds.bridge.message
    .GetDocumentsSQResponseMessage;
import com.vangent.hieos.services.xds.bridge.model.Document;
import com.vangent.hieos.services.xds.bridge.model.ResponseType
    .ResponseTypeStatus;
import com.vangent.hieos.services.xds.bridge.model.SubmitDocumentResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Class description
 *
 *
 * @version        v1.0, 2011-07-06
 * @author         Vangent
 */
public class DocumentExistsCheckActivity
        extends AbstractGetDocumentsSQActivity {

    /** Field description */
    private static final Logger logger =
        Logger.getLogger(DocumentExistsCheckActivity.class);

    /**
     * Constructs ...
     *
     *
     * @param client
     */
    public DocumentExistsCheckActivity(XDSDocumentRegistryClient client) {

        super(client);
    }

    /**
     * Method description
     *
     *
     * @param registryResponse
     * @param context
     *
     * @return
     */
    private boolean checkForSuccess(
            GetDocumentsSQResponseMessage registryResponse,
            SDRActivityContext context) {

        boolean result = false;

        SubmitDocumentResponse sdrResponse =
            context.getSubmitDocumentResponse();
        Document document = context.getDocument();

        List<String> objectRefs = parseObjectRefs(registryResponse, context);

        if (objectRefs.isEmpty()) {

            result = true;

        } else {

            String errmsg =
                "Document already exists in the registry. It will not be processed.";

            logger.error(errmsg);
            sdrResponse.addResponse(document, ResponseTypeStatus.Failure,
                                    errmsg);
        }

        return result;
    }

    /**
     * Method description
     *
     *
     * @param context
     *
     * @return
     */
    @Override
    public boolean execute(SDRActivityContext context) {

        boolean result = false;

        Document document = context.getDocument();

        if (isCheckDisabled(document)) {

            // if we generated the id
            // or the docId and replId are the same
            result = true;

        } else {

            GetDocumentsSQResponseMessage getDocsResp =
                callGetDocumentsSQ(context, document.getDocumentIdAsOID());

            result = checkForSuccess(getDocsResp, context);
        }

        return result;
    }

    /**
     * Method description
     *
     *
     * @param doc
     *
     * @return
     */
    private boolean isCheckDisabled(Document doc) {

        boolean result = false;

        // if we generated the id
        // or the docId and replId are the same
        if (doc.isGeneratedDocumentId()
                || StringUtils.equals(doc.getReplaceId(), doc.getId())) {

            result = true;
        }

        return result;
    }
}