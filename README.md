# IoT System for Audiovisual Self-Administered Tests

The system consists of a node-RED solution, an Alexa solution, and an Android mobile application called (distFinder).

The CustomInstallNodered-master folder contains an installer (under improvement) with which it is possible to install node-red, the necessary plugins, and the necessary flow.

The node_red folder contains the flow related to the entire solution and the list of plugins and components used, which are also listed below:

node-red-contrib-alexa-cakebaked
node-red-contrib-cast
node-red-dashboard
node-red-node-sqlite
node-red-contrib-image-tools
node-red-node-ui-table
node-red-contrib-ui-media
To use the flow, it is necessary to install node-red, sqlite3, and download ngrok, and then install all the necessary plugins via the "manage palette" option in the node-red menu.

The Alexa folder contains the complete export of the 4 skills used. To use the system, it is necessary to have the 4 skills in your own account and perform the build; this way, they will be available in the current account (the compressed folder hearingTest.zip contains the audio files used for the hearing test).

The distFinder folder contains the Android application for distance measurement.

The BPMN folder contains the process diagrams of the three tests, created with BPMN graphical notation.
