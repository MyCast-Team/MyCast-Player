var models = require("../models");
var utils = require("../Utils");
var multer = require("multer");
var storage = multer.diskStorage({
    destination: function (req, file, callback) {
        callback(null, './uploads/');
    },
    filename: function (req, file, callback) {
        callback(null, file.fieldname + '-' + file.originalname);
    }
});


var fs = require("fs");
module.exports = function (app) {
    app.post("/plugin", multer({storage: storage}).single('plugin'), function (req, res, next) {

        var plugin = utils.plugin;

        if (req.body.author) {
            console.log(req.body.author);
            var u1 = new plugin(req.file.originalname, req.body.author);
            u1.addplugin(u1, function (err, data) {
                res.redirect("/ListePlugin")
            });

        }

    });

    app.get("/Listeplugin", function (req, res, next) {
        if (req.session.admin) {
            var plugin = models.plugin;

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
            plugin.findAll().then(function (results) {
                var str = "";
                for (var idx in results) {
                    str += "<li>" + results[idx].name + "    " + "<a id='deleteplugin' href='#' rel=" + results[idx].id + ">delete</a>/<a href='/updateplugin/" + results[idx].id + "'>update</a></li>"
                }
                fs.readFile("./views/listplugin.html", function (err, data) {
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
    app.get("/updateplugin/:id", function (req, res, next) {
        if (req.session.admin) {
            var plugin = models.plugin;
            var request = {
                "where": {
                    id: req.params.id
                }
            }
            plugin.find(request).then(function (results) {
                var str = "<input type='hidden' name='id' value='" + results.id + "'>";
                str += "<input type='text' name='name' value='" + results.name + "'>";
                str += "<input type='text' name='author' value='" + results.author + "'>";

                fs.readFile("./views/updateplugin.html", function (err, data) {
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


    app.delete("/deleteplugin/:id", function (req, res, next) {
        var plugin = models.plugin;
        var request = {
            "where": {
                id: req.params.id
            }
        }
        plugin.find(request).then(function (results) {
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
    app.put("/updateplugin", function (req, res, next) {
        var plugin = utils.plugin;
        var request = {
            "where": {
                id: req.body.id
            }
        }
        var attributes = {};
        if (req.body.name) {
            attributes.name = req.body.name;
        }

        if (req.body.author) {
            attributes.author = req.body.author;
        }


        var u1 = new plugin();
        u1.update(request, attributes, function (err, data) {
            res.json({
                "code": 2,
                "plugin": data
            })
        });


    });

}