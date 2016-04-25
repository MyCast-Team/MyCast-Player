var models = require("../models");
var utils = require("../Utils");
var fs = require("fs");
module.exports = function (app) {
    app.post("/addmusique", function (req, res, next) {
        var musique = utils.musique;
        console.log(utils);
        if (req.body.singer && req.body.producer && req.body.title && req.body.type) {
            var u1 = new musique(req.body.singer, req.body.producer, req.body.title, req.body.type);
            u1.addmusique(u1, function (err, data) {
                res.redirect("/ListeMusique")
            });

        }

    });

    app.get("/ListeMusique", function (req, res, next) {
        if (req.session.admin) {
            var musique = models.musique;

            /*var request={};
             if(req.query.limit){
             request.limit=parseInt(req.query.limit);
             
             
             }
             if(req.query.offset){
             request.offset=parseInt(req.query.offset);
             
             }
             if(req.query.lastname){
             request.where={
             "lastname":{
             "$like":"%"+req.query.lastname+"%"
             }
             };
             }*/
            musique.findAll().then(function (results) {
                var str = "";
                for (var idx in results) {
                    str += "<li>" + results[idx].title + "    " + "<a id='deletemusique' href='#' rel=" + results[idx].id + ">delete</a>/<a href='/updatemusique/" + results[idx].id + "'>update</a></li>"
                }
                fs.readFile("./views/listmusique.html", function (err, data) {
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
        } else {
            fs.readFile("./views/home.html", function (err, data) {
                res.type("html");
                res.send(data.toString());
            })
        }

    });
    app.get("/updatemusique/:id", function (req, res, next) {
        if (req.session.admin) {
            var musique = models.musique;
            var request = {
                "where": {
                    id: req.params.id
                }
            }
            musique.find(request).then(function (results) {
                var str = "<input type='hidden' name='id' value='" + results.id + "'>";
                str += "<input type='text' name='singer' value='" + results.singer + "'>";
                str += "<input type='text' name='producer' value='" + results.producer + "'>";
                str += "<input type='text' name='title' value='" + results.title + "'>";
                str += "<input type='text' name='type' value='" + results.type + "'>";

                fs.readFile("./views/updatemusique.html", function (err, data) {
                    res.type("html");
                    res.send(data.toString().split("$val").join(str));
                });
            }).catch(function (err) {
                res.json({
                    "code": 2,
                    "message": "Sequelize error",
                    "error": err
                })
            });
        } else {
            fs.readFile("./views/home.html", function (err, data) {
                res.type("html");
                res.send(data.toString());
            })
        }


    });


    app.delete("/deletemusique/:id", function (req, res, next) {
        var musique = models.musique;
        var request = {
            "where": {
                id: req.params.id
            }
        }
        musique.find(request).then(function (results) {
            if (results) {

                results.destroy().then(function (suc) {

                    res.json({
                        "code": 0,
                        "result": true
                    })
                }).catch(function (err) {
                    res.json({
                        "code": 2,
                        "message": "Sequelize error1",
                        "error": err
                    })
                })
            } else {
                res.json({
                    "code": 0,
                    "result": false
                })
            }

        }).catch(function (err) {
            res.json({
                "code": 2,
                "message": "blaSequelize error",
                "error": err
            })
        });
    });
    app.put("/updatemusique", function (req, res, next) {
        var musique = utils.musique;

        var request = {
            "where": {
                id: req.body.id
            }
        }
        var attributes = {};
        if (req.body.singer) {
            attributes.singer = req.body.singer;
        }

        if (req.body.producer) {
            attributes.producer = req.body.producer;
        }
        if (req.body.title) {
            attributes.title = req.body.title;
        }
        if (req.body.type) {
            attributes.type = req.body.type;
        }



        var u1 = new musique();
        u1.update(request, attributes, function (err, data) {
            res.send("/ListeMusique");
        });


    });

}