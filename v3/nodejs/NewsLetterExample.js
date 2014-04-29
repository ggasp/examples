var dateformat=require('dateformat');
var client=require('./e3broadcast.js');
var async=require('async');
var fs=require('fs');

var log=function(msg) {
    console.log(new Date()+" "+msg);
};

client.createAPI("./config.json");

var runTime = new Date();
runTime.setMinutes(runTime.getMinutes() + 5);

var runDate=dateformat(new Date(),"yyyy-mm-dd'T'HH:MM:sso");
var batchMailingXml=fs.readFileSync("newsletter.xml",{encoding:"utf8"});
batchMailingXml=batchMailingXml.replace(/\{rundate\}/,runDate);
var mailingID="NL01";
var recipients1=fs.readFileSync("recipients.csv",{encoding:"utf8"});

async.waterfall([
//    function (callback) {
//       log("creating preview");
//        client.preview(callback,batchMailingXml,'test@example.com', {'RCPT_TYPE_v2': '0','LANGUAGE_v2': 'en'});
//    },
    function (callback) {
//        log(result);
        log("creating new batch mailing");
        client.createBatchMailing(callback,batchMailingXml,mailingID);
    },
    function (result,callback) {
        log("adding recipients to recipient list");
        client.addRecipients(callback,recipients1);
    },
    function (result,callback) {
        log("finish recipient list");
        client.finishRecipients(callback);
    }
], function(err,result) {
    if (err) {
        console.log("Error" + err);
    } else {
        log("done - mails will be sent in 5 minutes");
        process.exit(0);
    }
});







