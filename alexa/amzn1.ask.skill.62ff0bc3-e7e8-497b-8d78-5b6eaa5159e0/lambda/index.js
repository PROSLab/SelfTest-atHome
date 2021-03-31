const url_cast = "https://unicam.ngrok.io/castvideo";
const url_result = "https://unicam.ngrok.io/videoresults";
const wait_cast_delay = 3500;
const Alexa = require('ask-sdk-core');
const http = require('http');

const LaunchRequestHandler = {
    canHandle(handlerInput) {
        return Alexa.getRequestType(handlerInput.requestEnvelope) === 'LaunchRequest';
    },
    async handle(handlerInput) {
        const speakOutput = 'Bene iniziamo, tavola 1, leggi questo numero';
        const repromtOutput = 'Non ho capito, ripeti per favore';
        
        const sessionAttributes = handlerInput.attributesManager.getSessionAttributes();
        sessionAttributes['step'] = 1;
        
        await getRemoteData(url_cast + '?step=1')
              .then((response) => {
                const data = JSON.parse(response);
              })
              .catch((err) => {
                console.log(`ERROR: ${err.message}`);
              });
      
        setTimeout(function(){ },  wait_cast_delay);
    
        return handlerInput.responseBuilder
            .speak(speakOutput)
            .reprompt(repromtOutput)
            .getResponse();
    }
};

const GetNumberIntentHandler = {
    canHandle(handlerInput) {
        return Alexa.getRequestType(handlerInput.requestEnvelope) === 'IntentRequest'
            && Alexa.getIntentName(handlerInput.requestEnvelope) === 'GetNumberIntent';
    },
    async handle(handlerInput) {

        const sessionAttributes = handlerInput.attributesManager.getSessionAttributes();
        const responseValue = handlerInput.requestEnvelope.request.intent.slots.numeri.value.toLowerCase();
        
        const current_step = sessionAttributes['step'];
        sessionAttributes['response' + current_step] = responseValue;
        sessionAttributes['step'] = (current_step + 1);
        const speakOutput = 'Tavola ' + (current_step + 1) + ', leggi questo numero';
        const repromtOutput = 'Non ho capito, ripeti per favore';
        
        if((current_step + 1) > 25){
        
            let response = "";
            for (let i = 1; i <= 25; i++){
                if(sessionAttributes['response' + i] !== undefined)
                    response += sessionAttributes['response' + i] + "_";
            }
            console.log(response);            

            await getRemoteData(url_result + '?response=' + response)
            .then((response) => {
            const data = JSON.parse(response);
            })
            .catch((err) => {
            console.log(`ERROR: ${err.message}`);
            });

            return handlerInput.responseBuilder
                .getResponse();
            
        }else{
            
            await getRemoteData(url_cast + '?step=' + (current_step + 1))
              .then((response) => {
                const data = JSON.parse(response);
              })
              .catch((err) => {
                console.log(`ERROR: ${err.message}`);
              });
      
            setTimeout(function(){ },  wait_cast_delay);
    
            return handlerInput.responseBuilder
                .speak(speakOutput)
                .reprompt(repromtOutput)
                .getResponse();
            
        }
    }
};

const getRemoteData = (url) => new Promise((resolve, reject) => {
  const client = url.startsWith('https') ? require('https') : require('http');
  const request = client.get(url, (response) => {
    if (response.statusCode < 200 || response.statusCode > 299) {
      reject(new Error(`Failed with status code: ${response.statusCode}`));
    }
    const body = [];
    response.on('data', (chunk) => body.push(chunk));
    response.on('end', () => resolve(body.join('')));
  });
  request.on('error', (err) => reject(err));
});

const HelpIntentHandler = {
    canHandle(handlerInput) {
        return Alexa.getRequestType(handlerInput.requestEnvelope) === 'IntentRequest'
            && Alexa.getIntentName(handlerInput.requestEnvelope) === 'AMAZON.HelpIntent';
    },
    handle(handlerInput) {
        const speakOutput = 'Puoi eseguire il test del daltonismo. Come posso aiutarti?';

        return handlerInput.responseBuilder
            .speak(speakOutput)
            .reprompt(speakOutput)
            .getResponse();
    }
};

const CancelAndStopIntentHandler = {
    canHandle(handlerInput) {
        return Alexa.getRequestType(handlerInput.requestEnvelope) === 'IntentRequest'
            && (Alexa.getIntentName(handlerInput.requestEnvelope) === 'AMAZON.CancelIntent'
                || Alexa.getIntentName(handlerInput.requestEnvelope) === 'AMAZON.StopIntent');
    },
    handle(handlerInput) {
        const speakOutput = 'Arrivederci!';

        return handlerInput.responseBuilder
            .speak(speakOutput)
            .getResponse();
    }
};
/* *
 * FallbackIntent triggers when a customer says something that doesn’t map to any intents in your skill
 * It must also be defined in the language model (if the locale supports it)
 * This handler can be safely added but will be ingnored in locales that do not support it yet 
 * */
const FallbackIntentHandler = {
    canHandle(handlerInput) {
        return Alexa.getRequestType(handlerInput.requestEnvelope) === 'IntentRequest'
            && Alexa.getIntentName(handlerInput.requestEnvelope) === 'AMAZON.FallbackIntent';
    },
    handle(handlerInput) {
        const speakOutput = 'Mi dispiace, non so cosa fare. Riprova.';

        return handlerInput.responseBuilder
            .speak(speakOutput)
            .reprompt(speakOutput)
            .getResponse();
    }
};
/* *
 * SessionEndedRequest notifies that a session was ended. This handler will be triggered when a currently open 
 * session is closed for one of the following reasons: 1) The user says "exit" or "quit". 2) The user does not 
 * respond or says something that does not match an intent defined in your voice model. 3) An error occurs 
 * */
const SessionEndedRequestHandler = {
    canHandle(handlerInput) {
        return Alexa.getRequestType(handlerInput.requestEnvelope) === 'SessionEndedRequest';
    },
    handle(handlerInput) {
        console.log(`~~~~ Session ended: ${JSON.stringify(handlerInput.requestEnvelope)}`);
        // Any cleanup logic goes here.
        return handlerInput.responseBuilder.getResponse(); // notice we send an empty response
    }
};
/* *
 * The intent reflector is used for interaction model testing and debugging.
 * It will simply repeat the intent the user said. You can create custom handlers for your intents 
 * by defining them above, then also adding them to the request handler chain below 
 * */
const IntentReflectorHandler = {
    canHandle(handlerInput) {
        return Alexa.getRequestType(handlerInput.requestEnvelope) === 'IntentRequest';
    },
    handle(handlerInput) {
        const intentName = Alexa.getIntentName(handlerInput.requestEnvelope);
        const speakOutput = `Hai eseguito ${intentName}`;

        return handlerInput.responseBuilder
            .speak(speakOutput)
            //.reprompt('add a reprompt if you want to keep the session open for the user to respond')
            .getResponse();
    }
};
/**
 * Generic error handling to capture any syntax or routing errors. If you receive an error
 * stating the request handler chain is not found, you have not implemented a handler for
 * the intent being invoked or included it in the skill builder below 
 * */
const ErrorHandler = {
    canHandle() {
        return true;
    },
    handle(handlerInput, error) {
        const speakOutput = 'Mi dispiace c\'è stato un problema. Prova ancora.';
        console.log(`~~~~ Error handled: ${JSON.stringify(error)}`);

        return handlerInput.responseBuilder
            .speak(speakOutput)
            .reprompt(speakOutput)
            .getResponse();
    }
};

/**
 * This handler acts as the entry point for your skill, routing all request and response
 * payloads to the handlers above. Make sure any new handlers or interceptors you've
 * defined are included below. The order matters - they're processed top to bottom 
 * */
exports.handler = Alexa.SkillBuilders.custom()
    .addRequestHandlers(
        LaunchRequestHandler,
        GetNumberIntentHandler,
        HelpIntentHandler,
        CancelAndStopIntentHandler,
        FallbackIntentHandler,
        SessionEndedRequestHandler,
        IntentReflectorHandler)
    .addErrorHandlers(
        ErrorHandler)
    .withCustomUserAgent('unicam/daltonismo/v1.2')
    .lambda();