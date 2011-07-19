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
package com.vangent.hieos.DocViewer.client.view.patient;

import java.util.LinkedHashMap;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.vangent.hieos.DocViewer.client.model.config.Config;
import com.vangent.hieos.DocViewer.client.controller.DocViewerController;

/**
 * 
 * @author Bernie Thuman
 *
 */
public class PatientViewContainer extends Canvas {
	private final DocViewerController controller;
	private final PatientList patientList;
	private final PatientSearch patientSearch;

	/**
	 * @wbp.parser.constructor
	 * @param mainController
	 */
	public PatientViewContainer(DocViewerController mainController) {
		this.controller = mainController;
		this.patientList = new PatientList(controller);
		this.patientSearch = new PatientSearch(controller);
		final DynamicForm optionsForm = this.getOptionsForm();
	
		// Now add to the layout.
		final HLayout layout = new HLayout();
		layout.addMember(this.patientSearch);
		final LayoutSpacer spacer1 = new LayoutSpacer();
		spacer1.setWidth(5);
		layout.addMember(spacer1);
		layout.addMember(this.patientList);
		final LayoutSpacer spacer2 = new LayoutSpacer();
		spacer2.setWidth(10);
		layout.addMember(spacer2);
		layout.addMember(optionsForm);
		this.addChild(layout);
	}
	
	/**
	 * 
	 * @return
	 */
	private DynamicForm getOptionsForm()
	{
		final RadioGroupItem searchModeRadioGroupItem = new RadioGroupItem();
		searchModeRadioGroupItem.setTitle("");
		searchModeRadioGroupItem.setShowTitle(false);
		LinkedHashMap<String, String> searchModeMap = new LinkedHashMap<String, String>();
		searchModeMap.put(Config.VAL_SEARCH_MODE_HIE, "HIE");
		searchModeMap.put(Config.VAL_SEARCH_MODE_NHIN_EXCHANGE, "NHIN Exchange");
		searchModeRadioGroupItem.setValueMap(searchModeMap);
		searchModeRadioGroupItem.setDefaultValue(controller.getConfig().get(Config.KEY_SEARCH_MODE));
		searchModeRadioGroupItem.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				String searchMode = (String) event.getValue();
				controller.getConfig().put(Config.KEY_SEARCH_MODE, searchMode);
				//SC.say("value = " + searchMode);
			}
		});

		final DynamicForm optionsForm = new DynamicForm();
		optionsForm.setIsGroup(true);
		optionsForm.setHeight(40);
		optionsForm.setWidth(110);
		optionsForm.setNumCols(1);
		optionsForm.setGroupTitle("<b>Search Mode</b>");
		optionsForm.setFields(searchModeRadioGroupItem);

		return optionsForm;
	}

	/**
	 * 
	 * @param gridRecords
	 */
	public void updatePatientList(ListGridRecord[] gridRecords) {
		this.patientList.update(gridRecords);
	}
}