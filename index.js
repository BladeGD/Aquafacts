/* eslint no-var: off */
// import fb functions modules
const functions = require("firebase-functions");
// import admin module
const admin = require("firebase-admin");
admin.initializeApp(functions.config().firebase);

exports.sendNotification = functions.database.ref("/{uniqueID}/Data/{key}")
    .onCreate((snapshot, context) =>{
      const snap = snapshot.val();
      console.log(snap);
      const uniqueID = context.params.uniqueID;
      const temperature = snap.temperature;
      const ph = snap.ph;
      const salinity = snap.salinity;
      const timestamp = snap.timestamp;
      console.log(temperature);
      console.log(snap.ph);
      console.log(snap.timestamp);
      console.log("hi");

      const payload ={
        data: {
          temp: temperature.toString(),
          ph: ph.toString(),
          salinity: salinity.toString(),
          timestamp: timestamp.toString(),
        },
      };

      admin.messaging().sendToTopic(uniqueID, payload)
          .then((response) => {
            console.log("Notification sent");
            return response;
          })
          .catch((error) => {
            functions.logger.log("Notification failed", error);
          });
      return;
    });
