package eu.smartsantander.androidExperimentation.controller.ui;

import eu.smartsantander.androidExperimentation.controller.BaseController;
import eu.smartsantander.androidExperimentation.model.Result;
import eu.smartsantander.androidExperimentation.repository.ResultRepository;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Dimitrios Amaxilatis.
 */
@Controller
public class RestController extends BaseController {
    /**
     * a log4j logger to print messages.
     */
    private static final Logger LOGGER = Logger.getLogger(RestController.class);


    @Autowired
    ResultRepository resultRepository;


    @RequestMapping(value = "/experiment/{experimentId}", method = RequestMethod.GET)
    public String experimentView(final Map<String, Object> model, @PathVariable("experimentId") final String experiment, @RequestParam(value = "deviceId", defaultValue = "0", required = false) final int deviceId, @RequestParam(value = "after", defaultValue = "0", required = false) final String after) {
        LOGGER.debug("experiment:" + experiment);
        if (deviceId == 0) {
            model.put("title", "Experiment " + experiment);
        } else {
            model.put("title", "Experiment " + experiment + " device:" + deviceId);
        }
        model.put("addressPoints", getExperimentData(experiment, deviceId, after).toString());
        LOGGER.debug("-----------------------------------");
        return "experiment";
    }

    @ResponseBody
    @RequestMapping(value = "/api/v1/experiment/{experimentId}", method = RequestMethod.GET, produces = "application/json")
    public String experimentViewApi(@PathVariable("experimentId") final String experiment, @RequestParam(value = "deviceId", defaultValue = "0", required = false) final int deviceId, @RequestParam(value = "after", defaultValue = "0", required = false) final String after) {
        return getExperimentData(experiment, deviceId, after).toString();
    }

    private JSONArray getExperimentData(final String experiment, final int deviceId, final String after) {
        DecimalFormat df = new DecimalFormat("#.00000000");
        long start;
        try {
            start = Long.parseLong(after);
        } catch (Exception e) {
            switch (after) {
                case "Today":
                case "today":
                    start = new DateTime().withMillisOfDay(0).getMillis();
                    break;
                case "Yesterday":
                case "yesterday":
                    start = new DateTime().withMillisOfDay(0).minusDays(1).getMillis();
                    break;
                default:
                    start = 0;
                    break;
            }
        }
        final Set<Result> results;
        if (deviceId == 0) {
            results = resultRepository.findByExperimentIdAndTimestampAfter(Integer.parseInt(experiment), start);
        } else {
            results = resultRepository.findByExperimentIdAndDeviceIdAndTimestampAfterOrderByTimestampAsc(Integer.parseInt(experiment), deviceId, start);
        }

        Map<String, Map<String, Long>> locationsHeatMap = new HashMap<>();
        for (Result result : results) {
            try {
                final JSONObject message = new JSONObject(result.getMessage());

                if (message.has(LATITUDE) && message.has(LONGITUDE)) {
                    String longitude = df.format(message.getDouble(LONGITUDE));
                    String latitude = df.format(message.getDouble(LATITUDE));
                    if (!locationsHeatMap.containsKey(longitude)) {
                        locationsHeatMap.put(longitude, new HashMap<>());
                    }
                    if (!locationsHeatMap.get(longitude).containsKey(latitude)) {
                        locationsHeatMap.get(longitude).put(latitude, 0L);
                    }
                    Long val = locationsHeatMap.get(longitude).get(latitude);
                    locationsHeatMap.get(longitude).put(latitude, val + 1);
                }
            } catch (Exception e) {
                //ignore
                //LOGGER.error(e, e);
            }
        }
        final JSONArray addressPoints = new JSONArray();
        for (String longit : locationsHeatMap.keySet()) {
            for (String latit : locationsHeatMap.get(longit).keySet()) {
                LOGGER.info("{" + longit + ":" + latit + "}");
                JSONArray measurement = new JSONArray();
                try {
                    measurement.put(Double.parseDouble(latit));
                    measurement.put(Double.parseDouble(longit));
                    measurement.put(String.valueOf(locationsHeatMap.get(longit).get(latit)));
//                    measurement.put("1");
                    addressPoints.put(measurement);
                } catch (JSONException e) {
                    LOGGER.error(e, e);
                }
            }
        }
        LOGGER.info(addressPoints.toString());
        return addressPoints;
    }
}