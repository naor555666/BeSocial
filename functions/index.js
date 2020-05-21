
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


// approve chat conversation+give points to users
exports.approveChatConversation = functions.https.onCall((data, context) => {
    // data of chat sender and receiver from client
    const sender = data.sender
    const receiver = context.auth.uid
    var updates = {};


    return setUserSocialPointsForChatApproval(updates, sender, 2).then(() => {
        return setUserSocialPointsForChatApproval(updates, receiver, 1)
    }, () => 0).then(() => {
        updates[`/ChatConversations/${sender}/${receiver}/approved`] = true;
        updates[`/ChatConversations/${receiver}/${sender}/approved`] = true;
        console.log('before updates');
        return admin.database().ref().update(updates)
    }, () => 0
    ).catch();
});

exports.giveCredits = functions.database.ref('/EventsWithAttending/{eventId}/{userId}/isCheckedIn')
    .onCreate((snapshot, context) => {

        var eventCategory, isManagamentEvent
        admin.database().ref('/EventsWithAttending/' + context.params.eventId + "/" + context.params.userId).once('value').then(snapshot => {
            eventCategory = snapshot.child('eventCategory').val()
            isManagamentEvent = snapshot.child('companyManagmentEvent').val()
            return setUserSocialPointsForCheckIn(context, eventCategory, isManagamentEvent)
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

        } else {
            socialCreditsAmount = 100

        }
    } else if (isManagamentEvent === true) {
        socialCreditsAmount = 50

    } else {
        socialCreditsAmount = 0
    }
    console.log('social credits to give: ' + socialCreditsAmount);
    return socialCreditsAmount
}
function setUserSocialPointsForCheckIn(context, eventCategory, isManagamentEvent) {
    var socialStoreCredits
    var socialPoints
    var socialLevel

    admin.database().ref('/users/' + context.params.userId).once('value').then(snapshot => {
        socialStoreCredits = snapshot.child('socialStoreCredits').val()
        socialPoints = snapshot.child('socialPoints').val()
        socialLevel = snapshot.child('socialLevel').val()

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

function setUserSocialPointsForChatApproval(updates, userId, creditAmountToGive) {
    console.log('setUserSocialPointsForChatApproval invoked ');

    var socialStoreCredits
    var socialPoints
    var socialLevel

    return admin.database().ref('/users/' + userId).once('value').then(snapshot => {
        socialStoreCredits = snapshot.child('socialStoreCredits').val()
        socialPoints = snapshot.child('socialPoints').val()
        socialLevel = snapshot.child('socialLevel').val()

        socialStoreCredits += creditAmountToGive
        socialPoints += creditAmountToGive
        console.log('social points after update: ' + socialPoints);

        var pendingSocialLevel = checkForNewLevel(socialPoints)
        console.log('pending social level: ' + pendingSocialLevel);

        // updates['/Notifications/' + context.params.userId + '/socialStoreCredits'] = socialStoreCredits;


        if (socialLevel !== pendingSocialLevel) {
            updates['/users/' + userId + '/socialLevel'] = pendingSocialLevel;
        }
        updates['/users/' + userId + '/socialStoreCredits'] = socialStoreCredits;
        updates['/users/' + userId + '/socialPoints'] = socialPoints;
        console.log('finished one part inserting to updates');

        return 0
    }
        , reason => {
            console.error(reason); // Error!
            return 0
        }).catch();
}











