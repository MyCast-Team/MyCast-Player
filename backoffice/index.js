var express = require("express");
var app = express();
var cookie=require("cookie-parser");
var session = require('express-session');

var bodyparser = require("body-parser");
var crypto = require("crypto");
var fs = require("fs");
var models = require("./models");

var busboy = require('connect-busboy');

var secret = '42';
function encrypt(text) {
    var cipher = crypto.createCipher('aes-256-ctr', secret)
    var crypted = cipher.update(text, 'utf8', 'hex')
    crypted += cipher.final('hex');
    return crypted;
}
app.use(session({
    secret: '2C44-4D44-WppQ38S',
    resave: true,
    saveUninitialized: true,
    cookie:{maxAge:360000}
}));

app.use(bodyparser.urlencoded({
    "extended": false

}));
app.use(cookie());
app.use(function(req, res, next) {
    if(Date.now()>req.session.cookie.expires){
        
        console.log("test maxage");
       req.session.destroy();
    }
  // if now() is after `req.session.cookie.expires`
  //   regenerate the session
  next();
});
app.use(busboy());
app.use('/css', express.static(__dirname + '/node_modules/bootstrap/dist/css')); // redirect CSS bootstrap
app.use('/js', express.static(__dirname + '/node_modules/jquery/dist')); // redirect CSS bootstrap
require('./route')(app);
app.get("/", function (req, res, next) {
    
    fs.readFile("./views/home.html", function (err, data) {
        res.type("html");
        res.send(data.toString());
    })
}
);
app.post("/connection", function (req, res, next) {
    var admin = models.admin;
    var request = {
        "where": {
            login: req.body.login,
            password: encrypt(new Buffer([req.body.password].toString()))
        }
    }
    admin.find(request).then(function (results) {
        if (results) {
            //session if cette session correct autoriser connection
            req.session.admin = true;
            fs.readFile("./views/connection.html", function (err, data) {
                res.type("html");
                res.send(data.toString().split("$val").join("Login success"));
            })
        } else {
            req.session.admin = false;
            fs.readFile("./views/connection.html", function (err, data) {
                res.type("html");
                res.send(data.toString().split("$val").join("Login failed"));
            })
        }
    }).catch(function (err) {
        res.json({
            "code": 2,
            "message": "Sequelize error",
            "error": err
        })
    });

}
);

app.listen(3000, function () {

    console.log("Server start : ok");
})