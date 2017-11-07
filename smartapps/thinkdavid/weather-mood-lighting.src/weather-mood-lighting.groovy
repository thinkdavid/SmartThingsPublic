/**
 *  WeatherMood
 *
 *  Copyright 2017 David Becher
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
import java.util.regex.* 
 
definition(
    name: "Weather Mood Lighting",
    namespace: "thinkdavid",
    author: "David Becher",
    description: "Changes the color of my bedroom light depending on the weather forecast.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png") {
    appSetting "WU"
}


preferences {
	section("Change the color of the light when it's on:") {
    	input "thelight", "capability.colorControl", required: true
    }
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
	// the selected light's switch will be the trigger for the smartApp.
	subscribe(thelight, "switch.on", lightTurnedOnHandler)
}

def lightTurnedOnHandler(evt) {
	log.debug "lightTurnedOn: $evt"

// Parameters for the Weather Underground API Call
    def params = [
    uri: "http://api.wunderground.com",
    path: "/api/18c51436d4714689/conditions/q/VA/Richmond.json"
]

def weather
def temp

// Weather Underground API Call to get the weather
try {
    httpGet(params) { resp ->
        log.debug "${resp.data.current_observation.weather}"
        log.debug "${resp.data.current_observation.temp_f}"
        temp = "${resp.data.current_observation.temp_f}"
        weather = "${resp.data.current_observation.weather}"
    }
} catch (e) {
    log.error "something went wrong: $e"
}

log.debug "before: ${thelight.currentValue('hue')}"
log.debug "before: ${thelight.currentValue('saturation')}"

//Depending on the return of weather, set the light's colour hue.
//Hue/Saturation color matches can be found in the template of Phillips Hue Mood Lighting
if (weather =~ ".*Cloudy" || weather == "Overcast") {
	thelight.setHue(23)
    thelight.setSaturation(56)
    log.debug "Color Changed: Cloudy"
}  else if (weather =~ ".*Rain.*") {
	thelight.setHue(70)
    thelight.setSaturation(100)
    log.debug "Color Changed: Rain"
} else {
	log.debug "No match for weather: ${weather}"
}

log.debug "after: ${thelight.currentValue('hue')}"
log.debug "after: ${thelight.currentValue('saturation')}"


}
