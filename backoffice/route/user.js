var models = require("../models");
var utils = require("../Utils");
var fs = require("fs");
module.exports = function (app) {


    app.get("/ListeUser", function (req, res, next) {
        var user = models.user;

        if(req.session.admin){
        user.findAll().then(function (results) {
            var str = "";
            for (var idx in results) {
                str += "<li>" + results[idx].id + "    " + "<a id='deleteuser' href='#' rel=" + results[idx].id + ">delete</a></li>"
            }
            fs.readFile("./views/listuser.html", function (err, data) {
                res.type("html");
                res.send(data.toString().split("$val").join(str));
            });
        }).catch(function (err) {

            res.json({
                "code": 2,
                "message": "Sequelize error",
                "error": err
            })
        })
    }else{
        
       fs.readFile("./views/home.html", function (err, data) {
                res.type("html");
                res.send(data.toString());
            }); 
    }

    });




    app.delete("/deleteuser/:id", function (req, res, next) {
        var user = utils.user;
        var u1 = new user();
        if (req.params.id) {
            u1.delete(req.params.id, function (result) {
                 res.send("/ListeUser");
            });
        } 
    });
    app.post("/addUser", function (req, res, next) {
        var user = utils.user;
        var u1 = new user();
        u1.adduser(u1, function (undefined, result) {
            res.redirect("/ListeUser");
        });


    });

}