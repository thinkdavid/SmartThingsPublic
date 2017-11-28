/**
 *  Rotate Colors
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
    name: "Rotate Colors",
    namespace: "thinkdavid",
    author: "David Becher",
    description: "Rotate colors of the light over time slowly",
    category: "",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("What light would you like to change colors") {
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
	// TODO: subscribe to attributes, devices, locations, etc.
    subscribe(thelight, "switch.on", lightOnHandler)
}

def lightOnHandler(evt) {
	log.debug "lightOnHandler called"
    thelight.setSaturation(100)
    changeColor()
}

def changeColorTimer(time) {
	runIn(time, changeColor)
}

def changeColor() {
	if(thelight.currentValue('switch') == 'on') {
	def hue = thelight.currentValue('hue')
    log.debug hue
    	if (hue < 92) {
        	log.debug "under 92: ${hue}"
   			thelight.setHue(hue+8)
        } else {
        	log.debug "else: ${hue}"
        	thelight.setHue(0)
		}
	changeColorTimer(5)    
    }
}

// TODO: implement event handlers