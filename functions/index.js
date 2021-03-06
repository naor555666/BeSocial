
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

const NEW_RANK = "New rank"
const EVENT = 'Event'
const NEW_CONVERSTION = 'New Conversation'
// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions


// approve chat conversation+give points to users
exports.approveChatConversation = functions.https.onCall((data, context) => {
    // data of chat sender and receiver from client
    const senderId = data.sender
    const receiverId = context.auth.uid
    const senderName = data.senderName
    const receiverName = data.receiverName
    var updates = {};


    return setUserSocialPointsForChatApproval(updates, senderId, 2).then(() => {
        var newNotificationKey = admin.database().ref().child('Notifications').child(senderId).push().key;
        updates[`/Notifications/${senderId}/${newNotificationKey}`] = {
            type: NEW_CONVERSTION,
            socialPointsAmount: '2',
            IdToNavigate: receiverId,
            relatedName: receiverName
        }
        return setUserSocialPointsForChatApproval(updates, receiverId, 1)
    }, () => 0).then(() => {
        var newNotificationKey = admin.database().ref().child('Notifications').child(receiverId).push().key;
        updates[`/Notifications/${receiverId}/${newNotificationKey}`] = {
            type: NEW_CONVERSTION,
            socialPointsAmount: '1',
            IdToNavigate: senderId,
            relatedName: senderName
        }
        updates[`/ChatConversations/${senderId}/${receiverId}/approved`] = true;
        updates[`/ChatConversations/${receiverId}/${senderId}/approved`] = true;

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
            eventName = snapshot.child('eventName').val()
            return setUserSocialPointsForCheckIn(context, eventCategory, isManagamentEvent, eventName)
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
    return socialCreditsAmount
}

function setUserSocialPointsForCheckIn(context, eventCategory, isManagamentEvent, eventName) {
    var socialStoreCredits
    var socialPoints
    var socialLevel

    admin.database().ref('/users/' + context.params.userId).once('value').then(snapshot => {
        socialStoreCredits = snapshot.child('socialStoreCredits').val()
        socialPoints = snapshot.child('socialPoints').val()
        socialLevel = snapshot.child('socialLevel').val()

        var creditAmountToGive = setCreditAmountToGive(eventCategory, isManagamentEvent)
        socialStoreCredits += creditAmountToGive
        socialPoints += creditAmountToGive

        var pendingSocialLevel = checkForNewLevel(socialPoints)

        var newNotificationKey = admin.database().ref().child('Notifications').child(context.params.userId).push().key;
        var updates = {};

        updates[`/Notifications/${context.params.userId}/${newNotificationKey}`] = {
            type: EVENT,
            socialPointsAmount: creditAmountToGive.toString(),
            IdToNavigate: context.params.eventId,
            relatedName: eventName
        }

        if (socialLevel !== pendingSocialLevel) {
            updates['/users/' + context.params.userId + '/socialLevel'] = pendingSocialLevel;
            newNotificationKey = admin.database().ref().child('Notifications').child(context.params.userId).push().key;
            updates[`/Notifications/${context.params.userId}/${newNotificationKey}`] = {
                type: NEW_RANK,
                relatedName: pendingSocialLevel
            }
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



        if (socialLevel !== pendingSocialLevel) {
            updates['/users/' + userId + '/socialLevel'] = pendingSocialLevel;

            newNotificationKey = admin.database().ref().child('Notifications').child(context.params.userId).push().key;
            updates[`/Notifications/${userId}/${newNotificationKey}`] = {
                type: NEW_RANK,
                relatedName: pendingSocialLevel
            }
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











