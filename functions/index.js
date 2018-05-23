const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.sendWelcomeNotification = functions.database.ref('/User/{Uid}')
    .onWrite((change, context) => {

    var uid = context.params.Uid;
    var name = admin.database().ref(`/User/${uid}/name`).once('value');

	var payload = {
		notification: {
			title: `New message from the backEnd`,
			body: 'Welcome' + name.toString() ,
			icon: '/images/firebase_ic.png'
		}
	};
	var token = admin.database().ref(`/User/${uid}/token/`).once('value');
	return admin.messaging().sendToDevice(token.toString(), payload)

});
/*
exports.sendWelcomeNotification = functions.auth.user().onCreate(event => {
	const user = event.data;
	const uid = user.uid;
	const name = admin.database().ref('/User/{uid}/').child('name').once('value');
	
	var payload = {
		notification: {
			title: `New message from the backEnd`,
			body: 'Welcome' + name.toString() ,
			icon: '/images/firebase_ic.png'
		}
	};
	var token = admin.database().ref('/User/{uid}/').child('token').once('value');
	return admin.messaging().sendToDevice(token, payload)
});
*/
