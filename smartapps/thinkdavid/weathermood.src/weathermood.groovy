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
definition(
    name: "WeatherMood",
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
	subscribe(thelight, "switch.on", lightTurnedOnHandler)
	// TODO: subscribe to attributes, devices, locations, etc.
}

def lightTurnedOnHandler(evt) {
	log.debug "lightTurnedOn: $evt"
    
    def params = [
    uri: "http://api.wunderground.com",
    path: "/api/18c51436d4714689/conditions/q/VA/Richmond.json"
]

def weather
def temp

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

log.debug "${thelight.currentValue('hue')}"
if (weather == "Overcast") {
	thelight.setHue(100)
    thelight.setSaturation(100)
    log.debug "Color Changed: Overcast"
}
log.debug "${thelight.currentValue('hue')}"


}
