/*
 * This code is subject to the HIEOS License, Version 1.0
 *
 * Copyright(c) 2012 Vangent, Inc.  All rights reserved.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vangent.hieos.services.pixpdqv2.serviceimpl;

import com.vangent.hieos.hl7v2util.acceptor.HL7v2Acceptor;
import com.vangent.hieos.hl7v2util.config.AcceptorConfig;
import com.vangent.hieos.services.pixpdqv2.transactions.AdminRequestHandler;
import com.vangent.hieos.xutil.atna.ATNAAuditEvent;
import com.vangent.hieos.xutil.exception.SOAPFaultException;
import com.vangent.hieos.xutil.services.framework.XAbstractService;
import com.vangent.hieos.xutil.xconfig.XConfig;
import com.vangent.hieos.xutil.xconfig.XConfigActor;
import com.vangent.hieos.xutil.xconfig.XConfigObject;
import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.AxisService;
import org.apache.log4j.Logger;

/**
 *
 * @author Bernie Thuman
 */
public class AdminService extends XAbstractService {

    private final static Logger logger = Logger.getLogger(AdminService.class);
    private static XConfigActor config = null;          // Singleton.
    private static HL7v2Acceptor hl7v2Acceptor = null;  // Singleton.
    private static Thread hl7v2AcceptorThread = null;   // Singleton.

    @Override
    protected XConfigActor getConfigActor() {
        return config;
    }

    /**
     *
     * @param request
     * @return
     * @throws AxisFault
     */
    public OMElement GetConfig(OMElement request) throws AxisFault {
        OMElement response = null;
        try {
            beginTransaction("GetConfig (PIXPDQV2)", request);
            validateWS();
            validateNoMTOM();
            AdminRequestHandler handler = new AdminRequestHandler(this.log_message);
            handler.setConfigActor(config);
            response = handler.run(request, AdminRequestHandler.MessageType.GetConfig);
            endTransaction(handler.getStatus());
        } catch (SOAPFaultException ex) {
            throwAxisFault(ex);
        }
        return response;

    }

    /**
     * This will be called during the deployment time of the service.
     * Irrespective of the service scope this method will be called
     */
    @Override
    public void startUp(ConfigurationContext configctx, AxisService service) {
        logger.info("PIXPDQV2:AdminService:startUp()");
        try {
            XConfig xconf;
            xconf = XConfig.getInstance();
            XConfigObject homeCommunity = xconf.getHomeCommunityConfig();

            // FIXME: Do we need XConfig here?
            config = (XConfigActor) homeCommunity.getXConfigObjectWithName("pix", XConfig.PIX_MANAGER_TYPE);

            // Start up listener.
            // FIXME: Location HACK!
            AcceptorConfig acceptorConfig = new AcceptorConfig("C:/dev/hieos/config/empi/PIXPDQHL7v2AcceptorConfig.xml");
            AdminService.hl7v2Acceptor = new HL7v2Acceptor(acceptorConfig);
            AdminService.hl7v2Acceptor.start();
            //AdminService.hl7v2AcceptorThread = new Thread(AdminService.hl7v2Acceptor);
            //AdminService.hl7v2AcceptorThread.start();

        } catch (Exception ex) {
            logger.fatal("Unable to get configuration for service", ex);
        }
        this.ATNAlogStart(ATNAAuditEvent.ActorType.PIX_MANAGER);
    }

    /**
     * This will be called during the system shut down time. Irrespective
     * of the service scope this method will be called
     */
    @Override
    public void shutDown(ConfigurationContext configctx, AxisService service) {
        logger.info("PatientIdentifierCrossReferenceManager::shutDown()");
        AdminService.hl7v2Acceptor.shutdownAndAwaitTermination();
        this.ATNAlogStop(ATNAAuditEvent.ActorType.PIX_MANAGER);
    }
}
