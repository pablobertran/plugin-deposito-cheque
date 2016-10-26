var exec = require('cordova/exec');

function DepositoCheque() {
    console.log("DepositoCheque.js: is created");
}

DepositoCheque.prototype.tomarCheque = function(successCallback, errorCallback, action, params) {
    console.log("DepositoCheque.js");
    console.log("Action: " + action);

    exec(successCallback, errorCallback, "DepositoCheque", action, params);
}


var depositoCheque = new DepositoCheque();
module.exports = depositoCheque;