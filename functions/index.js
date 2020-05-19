
const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

const lvl2 = 200
const lvl3 = 800
const lvl4 = 2600
const lvl5 = 6000

const strlvl1 = "Shy Socializer"
const strlvl2 = "Out Of The Shell Socializer"
const strlvl3 = "Academic Socializer"
const strlvl4 = "Socialized Ninja Turtle"
const strlvl5 = "Socialosaurus"

const HELP_ME = "Help Me!"
// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
exports.helloWorld = functions.https.onRequest((request, response) => {
    response.send("Hello from Firebase!");
});

exports.giveCredits = functions.database.ref('/EventsWithAttending/{eventId}/{userId}/isCheckedIn')
    .onCreate((snapshot, context) => {

        var eventCategory, isManagamentEvent
        admin.database().ref('/EventsWithAttending/' + context.params.eventId + "/" + context.params.userId).once('value').then(snapshot => {
            eventCategory = snapshot.child('eventCategory').val()
            isManagamentEvent = snapshot.child('companyManagmentEvent').val()
            return setUserSocialPoints(context, eventCategory, isManagamentEvent)
        }, () => {
            return 0
        }).catch();
        return 1
    });


function checkForNewLevel(socialPoints) {
    var pendingSocialLevel
    if (socialPoints < lvl2) {
        pendingSocialLevel = strlvl1
    } else if (socialPoints >= lvl2 && socialPoints < lvl3) {
        pendingSocialLevel = strlvl2
    } else if (socialPoints >= lvl3 && socialPoints < lvl4) {
        pendingSocialLevel = strlvl3
    } else if (socialPoints >= lvl4 && socialPoints < lvl5) {
        pendingSocialLevel = strlvl4
    } else if (socialPoints >= lvl5) {
        pendingSocialLevel = strlvl5
    }
    return pendingSocialLevel
}

function setCreditAmountToGive(eventCategory, isManagamentEvent) {
    var socialCreditsAmount
    if (eventCategory === HELP_ME) {
        if (isManagamentEvent === false) {
            socialCreditsAmount = 50
            console.log('help me normal');

        } else {
            socialCreditsAmount = 100
            console.log('help me managment');

        }
    } else if (isManagamentEvent === true) {
        socialCreditsAmount = 50
        console.log('general managment');

    } else {
        socialCreditsAmount = 0
        console.log('general non-managment');
    }
    console.log('social credits to give: ' + socialCreditsAmount);
    return socialCreditsAmount
}
function setUserSocialPoints(context, eventCategory, isManagamentEvent) {
    var socialStoreCredits
    var socialPoints
    var socialLevel

    admin.database().ref('/users/' + context.params.userId).once('value').then(snapshot => {
        socialStoreCredits = snapshot.child('socialStoreCredits').val()
        // console.log('social credits before: ' + socialStoreCredits);
        socialPoints = snapshot.child('socialPoints').val()
        socialLevel = snapshot.child('socialLevel').val()
        console.log('current social level: ' + socialLevel);

        // console.log('social points before: ' + socialPoints);
        var creditAmountToGive = setCreditAmountToGive(eventCategory, isManagamentEvent)
        console.log('social credits to give: ' + creditAmountToGive);
        socialStoreCredits += creditAmountToGive
        socialPoints += creditAmountToGive

        var pendingSocialLevel = checkForNewLevel(socialPoints)
        console.log('pending social level: ' + pendingSocialLevel);

        // updates['/Notifications/' + context.params.userId + '/socialStoreCredits'] = socialStoreCredits;

        var updates = {};
        if (socialLevel !== pendingSocialLevel) {
            updates['/users/' + context.params.userId + '/socialLevel'] = pendingSocialLevel;
        }
        updates['/users/' + context.params.userId + '/socialStoreCredits'] = socialStoreCredits;
        updates['/users/' + context.params.userId + '/socialPoints'] = socialPoints;

        return admin.database().ref().update(updates)
    }, reason => {
        console.error(reason); // Error!
        return 0
    }).catch();
}













