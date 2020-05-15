

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();
// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
exports.helloWorld = functions.https.onRequest((request, response) => {
    response.send("Hello from Firebase!");
});

exports.giveCredits = functions.database.ref('/EventsWithAttending/{eventId}/{userId}/isCheckedIn')
    .onCreate((snapshot, context) => {
        var userId
        // Grab the current checked in user id.
        snapshot.ref.parent.child('userId').once('value').then(snapshot =>
            userId = snapshot.val).catch(()=>null)

        // console.log('userId', 'userId is' + userId);
        var userRef = firebase.database().ref('/users/{userId}')
        var socialStoreCredits
        userRef.once('socialStoreCredits').then(snapshot  =>
            socialStoreCredits = snapshot.val).catch(() => null);
        // console.log('credits', 'social credits now: ' + socialStoreCredits);

        socialStoreCredits += 50
        // console.log('credits', 'social credits now: ' + socialStoreCredits);
        return userRef.child('socialStoreCredits').set(socialStoreCredits)
    });










