
var models = require("../models");
var utils = require("../Utils");
var fs = require("fs");


module.exports = function (app) {


    app.get("/ListeFilm", function (req, res, next) {
        if (req.session.admin) {
            var film = models.film;


            film.findAll().then(function (results) {
                var str = "";
                for (var idx in results) {
                    str += "<li>" + results[idx].name + "    " + "<a id='deletefilm' href='#' rel=" + results[idx].id + ">delete</a>/<a href='/updatefilm/" + results[idx].id + "'>update</a></li>"
                }
                fs.readFile("./views/listfilm.html", function (err, data) {
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
            });
        }

    });



    app.delete("/deletefilm/:id", function (req, res, next) {
        var film = utils.film;
        var u1 = new film();
        if (req.params.id) {
            u1.delete(req.params.id, function (result) {
                res.send("/ListeFilm");
            });
        }
    });
    app.get("/updatefilm/:id", function (req, res, next) {
        if (req.session.admin) {
            var film = models.film;

            var request = {
                "where": {
                    id: req.params.id
                }
            }
            film.find(request).then(function (results) {

                var str = "<input type='hidden' name='id' value='" + results.id + "'>";
                str += "<input type='text' name='name' value='" + results.name + "'>";
                str += "<input type='text' name='director' value='" + results.director + "'>";
                str += "<input type='text' name='date' value='" + results.date + "'>";


                fs.readFile("./views/updatefilm.html", function (err, data) {
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
            });
        }

    });
    app.put("/updatefilm", function (req, res, next) {
        var film = utils.film;
        var request = {
            "where": {
                id: req.body.id
            }
        }


        var attributes = {};
        if (req.body.name) {
            attributes.name = req.body.name;
        }

        if (req.body.director) {
            attributes.director = req.body.director;
        }
        if (req.body.date) {
            attributes.date = req.body.date;
        }


        var u1 = new film();
        u1.update(request, attributes, function (err, data) {
            res.send("/ListeFilm");
        });


    });
    app.post("/addfilm", function (req, res, next) {
        var film = utils.film;

        if (req.body.title && req.body.director && req.body.date) {
            var title = req.body.title;
            var director = req.body.director;
            var date = req.body.date;

            var u1 = new film(title, director, date);
            u1.addfilm(u1, function (err, data) {
                res.redirect("/ListeFilm")
            });

        }

    });

}