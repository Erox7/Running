const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.sendWelcomeNotification = functions.auth.user().onCreate(event => {
	const user = event.data;
	const uid = user.uid;
	const name = admin.database().ref('/User/{uid}/').child('name');
	
	var payload = {
		notification: {
			title: `New message from the backEnd`,
			body: 'Welcome' + name.toString() ,
			icon: '/images/firebase_ic.png'
		}
	};
	var token = admin.database().ref('/User/{uid}/').child('token');
	return admin.messaging().sendToDevice(token, payload)
});
