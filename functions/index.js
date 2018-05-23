const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.sendWelcomeNotification = functions.database.ref('/User/{Uid}')
    .onWrite((change, context) => {

    var uid = context.params.Uid;
    const promiseName = change.after.ref.child('name').once('value');
    const promiseToken = change.after.ref.child('token').once('value');

    return Promise.all([promiseName, promiseToken]).then(results => {
        const nameSnapshot = results[0];
        const tokenSnapshot = results[1];

        var payload = {
        		notification: {
        			title: `New message from the backEnd`,
        			body: 'Welcome' + nameSnapshot.val() ,
        			icon: '/images/firebase_ic.png'
        		}
        };
        const tokens = Object.keys(tokenSnapshot.val());
        return admin.messaging().sendToDevice(tokenSnapshot.val(), payload)

    })
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

admin.database().ref(`/User/${uid}/name`).once('value').then(function(snapshot){
        var watafak = uid;
        console.log("WATAFAKISGOINGON" + uid)
        namep = snapshot.val();
        console.log("YOWASAP" + namep);
    });*/


/*
return admin.database().ref(`/User/${uid}/name`).once('value').then(function(snapshot){

        var name = snapshot.val();
        return admin.database().ref(`/User/${uid}/token/`).once('value').then(function(snapshot){
            var token = snapshot.val();
            	var payload = {
            		notification: {
            			title: `New message from the backEnd`,
            			body: 'Welcome' + name ,
            			icon: '/images/firebase_ic.png'
            		}
            	};
            return admin.messaging().sendToDevice(token, payload)
        });
    });
*/