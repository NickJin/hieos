/*
 *
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
 */package com.vangent.hieos.DocViewer.server.services.rpc.config;

import java.util.List;

import com.vangent.hieos.DocViewer.client.model.config.Config;
import com.vangent.hieos.DocViewer.client.model.config.DocumentTemplateConfig;
import com.vangent.hieos.DocViewer.client.services.rpc.ConfigRemoteService;
import com.vangent.hieos.DocViewer.server.framework.ServletUtilMixin;
import com.vangent.hieos.xutil.xconfig.XConfig;
import com.vangent.hieos.xutil.xconfig.XConfigObject;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ConfigRemoteServiceImpl extends RemoteServiceServlet implements
		ConfigRemoteService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7923244304825432784L;
	
	private final ServletUtilMixin servletUtil = new ServletUtilMixin();

	@Override
	public Config getConfig() {
		System.out.println("********* ConfigRemoteServiceImpl ********");

		// Get the mixin to allow access to xconfig.xml.
		servletUtil.init(this.getServletContext());

		// Create the Config instance that will be sent back to the client.
		Config config = new Config();

		// Now get the relevant properties

		// DefaultSearchMode:
		String defaultSearchMode = servletUtil
				.getProperty(Config.KEY_SEARCH_MODE);
		System.out.println("DefaultSearchMode = " + defaultSearchMode);
		if (defaultSearchMode == null) {
			defaultSearchMode = Config.VAL_SEARCH_MODE_HIE;
		}

		// TrimDocumentTabTitles:
		String trimDocumentTabTitles = servletUtil
				.getProperty(Config.KEY_TRIM_DOCUMENT_TAB_TITLES);
		if (trimDocumentTabTitles == null) {
			trimDocumentTabTitles = "false";
		}

		// TrimDocumentTabTitlesLength:
		String trimDocumentTabTitlesLength = servletUtil
				.getProperty(Config.KEY_TRIM_DOCUMENT_TAB_TITLES_LENGTH);
		if (trimDocumentTabTitlesLength == null) {
			trimDocumentTabTitlesLength = "50";
		}

		// Title:
		String title = servletUtil.getProperty(Config.KEY_TITLE);
		if (title == null) {
			title = "HIEOS Doc Viewer";
		}

		// LogoFileName:
		String logoFileName = servletUtil
				.getProperty(Config.KEY_LOGO_FILE_NAME);
		System.out.println("LogoFileName = " + logoFileName);
		if (logoFileName == null) {
			logoFileName = "search_computer.png";
		}

		// LogoWidth/LogoHeight:
		String logoWidth = servletUtil.getProperty(Config.KEY_LOGO_WIDTH);
		String logoHeigth = servletUtil.getProperty(Config.KEY_LOGO_HEIGHT);

		// Fill up the config:
		config.put(Config.KEY_SEARCH_MODE, defaultSearchMode);
		config.put(Config.KEY_TRIM_DOCUMENT_TAB_TITLES, trimDocumentTabTitles);
		config.put(Config.KEY_TRIM_DOCUMENT_TAB_TITLES_LENGTH,
				trimDocumentTabTitlesLength);
		config.put(Config.KEY_TITLE, title);
		config.put(Config.KEY_LOGO_FILE_NAME, logoFileName);
		config.put(Config.KEY_LOGO_WIDTH, logoWidth);
		config.put(Config.KEY_LOGO_HEIGHT, logoHeigth);

		this.loadDocumentTemplateConfigs(config);
		return config;
	}

	/**
	 * 
	 * @param config
	 */
	private void loadDocumentTemplateConfigs(
			Config config) {
		try {
			XConfig xconf = XConfig.getInstance();
			XConfigObject propertiesObject = xconf.getXConfigObjectByName(
					"DocViewerProperties", "DocViewerPropertiesType");
			XConfigObject documentTemplateListConfig = propertiesObject
					.getXConfigObjectWithName("DocumentTemplates",
							"DocumentTemplateListType");
			List<XConfigObject> configObjects = documentTemplateListConfig
					.getXConfigObjectsWithType("DocumentTemplateType");
			for (XConfigObject configObject : configObjects) {
				DocumentTemplateConfig documentTemplateConfig = new DocumentTemplateConfig();
				documentTemplateConfig.setDisplayName(configObject
						.getProperty("DisplayName"));
				documentTemplateConfig.setFileName(configObject
						.getProperty("FileName"));
				config.addDocumentTemplateConfig(documentTemplateConfig);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}