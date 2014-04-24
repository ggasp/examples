var dateformat=require('dateformat');
var client=require('./e3broadcast.js');
var async=require('async');
var fs=require('fs');

var log=function(msg) {
    console.log(new Date()+" "+msg);
};

client.createAPI("./config.json");

var transactionalMailingXml=fs.readFileSync("confirmation.xml",{encoding:"utf8"});
var recipients1=fs.readFileSync("recipients.csv",{encoding:"utf8"});
var recipients2=fs.readFileSync("recipients2.csv",{encoding:"utf8"});
var mailingId="TA01";

async.waterfall([
    function (callback) {
        log("creating a new transactional mailing");
        client.createTransactionalMailing(callback,transactionalMailingXml,mailingId);
    },
    function (callback) {
        log("publishing a revision");
        client.publishRevision(callback,mailingId);
    },
    function (revisionId,callback) {
        log("sending to first group with revisionId "+revisionId);
        client.sendTransactional(callback,mailingId,revisionId,recipients1);
    },
    function (revisionId,callback) {
        log("sending to second group");
        client.sendTransactional(callback,mailingId,revisionId,recipients2);
    }
], function(err,result) {
    if (err) {
        console.log("Error:",err);
    } else {
        log("done");
    }
});







