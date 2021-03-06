package gr.cti.android.experimentation.controller;

/*-
 * #%L
 * Smartphone Experimentation Web Service
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2015 - 2016 CTI - Computer Technology Institute and Press "Diophantus"
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import gr.cti.android.experimentation.model.PluginDTO;
import gr.cti.android.experimentation.service.BackendService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Map;

/**
 * @author Dimitrios Amaxilatis.
 */
@Controller
public class HomeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    BackendService backendService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(Principal principal, Map<String, Object> model) {
        model.put("principal", principal);
        model.put("sensors", backendService.getSensors());
        model.put("experiments", backendService.getExperiments());
        return "home";
    }

    @RequestMapping(value = "/experiment/list", method = RequestMethod.GET)
    public String experimentsList(Principal principal, HttpServletRequest req, Map<String, Object> model) {
        model.put("principal", principal);
        model.put("sensors", backendService.getSensors());
        model.put("experiments", backendService.getExperiments());
        return "exp-list";
    }

    @RequestMapping(value = "/experiment/certain/{experimentId}", method = RequestMethod.GET)
    public String experimentsList(Principal principal, Map<String, Object> model, @PathVariable("experimentId") final String experimentId) {
        model.put("principal", principal);
        model.put("sensors", backendService.getSensors());
        model.put("experiments", backendService.getExperiments());
        model.put("experiment", backendService.getExperiment(experimentId));
        model.put("regions", backendService.getExperimentRegions(experimentId));
        return "exp-view";
    }

    @RequestMapping(value = "/plugin/userPlugins", method = RequestMethod.GET)
    public String sensorList(Principal principal, Map<String, Object> model) {
        model.put("principal", principal);
        model.put("sensors", backendService.getSensors());
        model.put("experiments", backendService.getExperiments());
        return "plugin-list";
    }

    @RequestMapping(value = "/plugin/update/{pluginId}", method = RequestMethod.GET)
    public String showSensor(Principal principal, Map<String, Object> model, @PathVariable("pluginId") final long pluginId) {
        model.put("principal", principal);
        model.put("pluginId", pluginId);
        model.put("sensors", backendService.getSensors());
        model.put("experiments", backendService.getExperiments());
        return "plugin-edit";
    }

    @RequestMapping(value = "/plugin/addPlugin", method = RequestMethod.GET)
    public String addSensor(Principal principal, Map<String, Object> model) {
        model.put("principal", principal);
        model.put("sensors", backendService.getSensors());
        model.put("experiments", backendService.getExperiments());
        return "plugin-add";
    }

    @RequestMapping(value = "/plugin/addPlugin", method = RequestMethod.POST)
    public String postAddSensor(Principal principal, @ModelAttribute PluginDTO dto) {
        String resp = backendService.addPlugin(dto);
        return "redirect:/plugin/userPlugins";
    }

    @RequestMapping(value = "/plugin/update", method = RequestMethod.POST)
    public String postUpdateSensor(Principal principal, @ModelAttribute PluginDTO dto) {
        String resp = backendService.updatePlugin(dto);
        return "redirect:/plugin/update/" + dto.getId();
    }

    @RequestMapping(value = "/plugin/delete/{pluginId}", method = RequestMethod.GET)
    public String postAddSensor(Principal principal, @PathVariable("pluginId") final int pluginId) {
        backendService.deletePlugin(pluginId);
        return "redirect:/plugin/userPlugins";
    }

}
