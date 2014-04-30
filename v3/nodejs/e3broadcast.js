var fs=require('fs');
var request=require('request');
var sshConnection = require('ssh2');
var xmlbuilder=require('xmlbuilder');

var config={};
var self={};

var log=console.log;

exports.createAPI=function(config_filename){
    config=require(config_filename);
};

exports.createBatchMailing=function(callback,batchMailingXml,id){
   batchMailingXml=batchMailingXml.replace(/{domain}/,config.domain);
   makeAuthPostRequest("batch_mailings/"+id,batchMailingXml,function(err,result) {
       if (err) {
           callback(err);
       } else {
           callback(null,result);
       }
   });
};

exports.preview=function (callback,xml ,email, fields){
    var root = xmlbuilder.create('previewRequest');
    var recipient=root.ele('recipient').att('email',email);
    Object.keys(fields).forEach(function(field) {
        root.ele("field",field).att("key",fields[field]);
    });
    root.raw(xml);
    var previewXml=root.end({ pretty: true});
    makeAuthPostRequest("../beta/preview",previewXml,function(err,result) {
        if (err) {
            callback(err);
        } else {
            callback(null,result);
        }
    });
};

exports.addRecipients=function(callback,mailingId,recipientsCsv){
    makeAuthPostRequest("batch_mailings/"+mailingId+"/recipients",recipientsCsv,function(err,result) {
        if (err) {
            callback(err);
        } else {
            callback(null,result);
        }
    });
}

exports.finishRecipients=function(callback,mailingId){
    makeAuthPostRequest("batch_mailings/"+mailingId+"/recipients/status?status=Finished","",function (err,result) {
        if (err) {
            callback(err);
        } else {
            callback(null,result);
        }
    });    
}

exports.getStatus=function(callback,mailingId){
    makeAuthGetRequest("batch_mailings/"+mailingId+"/status",callback);
};

exports.createTransactionalMailing=function(callback,mailingXml,mailingId){
    mailingXml=mailingXml.replace(/{domain}/,config.domain);
    makeAuthPostRequest("transactional_mailings/"+mailingId,mailingXml,function (err,result) {
        log(result);
        if (err) {
            callback(err);
        } else {
            callback(result);
        }
    });
};

exports.getTransactionalMailing=function(callback,mailingId) {
    makeAuthGetRequest("transactional_mailings/"+mailingId,callback);
};

exports.publishRevision=function(callback,mailingId){
    makeAuthPostRequest("transactional_mailings/"+mailingId+"/revisions","",function (err,result) {
        if (err) {
            log(err);
            callback(err);
        } else {
            log(result);
            var revisionId=result.match(/id="(.*?)"/)[1];
            callback(null,revisionId);
        }
    });
};

exports.getRevisionList=function(callback,mailingId){
    makeAuthGetRequest("transactional_mailings/"+mailingId+"/revisions",callback);
};

exports.getTransactionalMailingRevision=function(callback,mailingId,revisionId) {
    makeAuthGetRequest("transactional_mailings/"+mailingId+"/revisions/"+revisionId,callback);
};

exports.deleteTransactionalMailingRevision=function(callback,mailingId,revisionId) {
    makeAuthDeleteRequest("transactional_mailings/"+mailingId+"/revisions/"+revisionId,callback);
};

exports.sendTransactional=function(callback,mailingId,revisionId,recipientsCsv){
    makeAuthPostRequest("transactional_mailings/"+mailingId+"/revisions/"+revisionId+"/recipients",recipientsCsv,function(err,result) {
        log(result);
        if (err) {
            callback(err);
        } else {
            callback(null,revisionId);
        }
    });
};

// HTTPS helper functions
function makeAuthPostRequest(path,body,callback) {
    var url=config.api_url+"/"+path;
    log("POST:"+url);
    request({method:'post',body:body,auth:{username:config.username,password:config.password},rejectUnauthorized: false,
            requestCert: true,
            agent: false,
            url:url},
        function(err,result,body) {
            callback(err,body);
        }
    );
}

function makeAuthGetRequest(path,callback) {
    var url=config.api_url+"/"+path;
    log("GET :"+url);
    request({method:'get',url:url,'auth': {'user': config.username,'pass': config.password,'sendImmediately': true },rejectUnauthorized: false,
            requestCert: true,
            agent: false},
        function(err,res,xml) {
            callback(err,xml);
        });
}

function makeAuthDeleteRequest(path,callback) {
    var url=config.api_url+"/"+path;
    log("DELE:"+url);
    request(
        {method:'get',url:url,'auth': {'user': config.username,'pass': config.password,'sendImmediately': true },rejectUnauthorized: false,
            requestCert: true,
            agent: false},
        function(err,result) {
            callback(err,result);
        }
    );
}

function makeAuthPutRequest(path,body,callback) {
    var url=config.api_url+"/"+path;
    log("PUT :"+url);
    request(
        {method:'put',url:url,body:body,'auth': {'user': config.username,'pass': config.password,'sendImmediately': true },rejectUnauthorized: false,
            requestCert: true,
            agent: false},
        function( err,res,result) {
            if (err) {
                callback(err,res);
            } else {
                callback(null,result);
            }
        }
    );
}
