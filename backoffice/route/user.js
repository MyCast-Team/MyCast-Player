var models = require("../models");
var utils = require("../Utils");
var fs = require("fs");
var math = require('mathjs');
module.exports = function (app) {


    app.get("/ListeUser", function (req, res, next) {
        var user = models.user;

        if (req.session.admin) {
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
        } else {

            fs.readFile("./views/home.html", function (err, data) {
                res.type("html");
                res.send(data.toString());
            });
        }

    });
    app.get("/:id/ListeFilm", function (req, res, next) {
        var user = models.user;
        var film = models.film;
        var nbuser = 0;
        var nbmovie = 0;
        var userresult="";
        var movieresult="";
        var userfilm = models.userfilm;
        
        

            user.findAll().then(function (results) {
                nbuser = results.length;
                userresult=results;
            }).catch(function (err) {

                res.json({
                    "code": 2,
                    "message": "Sequelize error in user",
                    "error": err
                })
            })
            film.findAll().then(function (results) {
                nbmovie = results.length;
                movieresult=results;
            }).catch(function (err) {

                res.json({
                    "code": 2,
                    "message": "Sequelize error in film",
                    "error": err
                })
            })
            userfilm.findAll().then(function (results) {
               
                var matrice = math.matrix();
              
                matrice.resize([nbuser, nbmovie]);
                
                var matriceprime = math.matrix();
                matriceprime.resize([nbmovie, nbuser]);
                
                for (var i = 0, len = results.length; i < len; i++) {
                    var row = results[i];


                    matrice.subset(math.index(row.idUser - 1, row.idFilm - 1), 1);
                    matriceprime.subset(math.index(row.idFilm - 1, row.idUser - 1), 1);
                }
                var matrice3 = math.multiply(matriceprime, matrice);
                var matrice4 = math.multiply(matrice3, matriceprime);
                var matrice5 = math.transpose(matrice4);
                console.log(matrice5);
                for (var i = 0, len = results.length; i < len; i++) {
                    var row = results[i];
                    if (matrice.subset(math.index(row.idUser - 1, row.idFilm - 1)) == 1) {
                        matrice5.subset(math.index(row.idUser - 1, row.idFilm - 1), 0);
                    }
                }
           fs.truncate('filmuser.json', 0, function(){console.log('done')})
             for (var t = 0; t < userresult.length; t++) {
                    var rowuser = userresult[t];
                    console.log(t)
                    
                    for (var i = 0, len = movieresult.length; i < len; i++) {
                        console.log(i) 
                        var row = movieresult[i];
                       

                        if (matrice5.subset(math.index(t, i)) != 0 && rowuser.id==req.params.id) {
                            var reqstat = {
                                "user": rowuser.id,
                                "film": row.name
                            }
                           fs.appendFile("filmuser.json", JSON.stringify(reqstat) + "\n", function (err) {
                                if (err) {
                                    throw err;
                                }

                            })
                        }


                    }
                }
                console.log(matrice5);

            }).catch(function (err) {

                res.json({
                    "code": 2,
                    "message": "Sequelize error in userfilm",
                    "error": err
                })
            })
        
    });



        app.get("/:id/ListeMusique", function (req, res, next) {
        var user = models.user;
        var usermusique = models.usermusique;
        var musique = models.musique;
        var nbuser = 0;
        var nbmusique = 0;
        var userresult="";
        var musiqueresult="";
      
        
        

            user.findAll().then(function (results) {
                nbuser = results.length;
                userresult=results;
            }).catch(function (err) {

                res.json({
                    "code": 2,
                    "message": "Sequelize error in user",
                    "error": err
                })
            })
            musique.findAll().then(function (results) {
                nbmusique = results.length;
                musiqueresult=results;
            }).catch(function (err) {

                res.json({
                    "code": 2,
                    "message": "Sequelize error in film",
                    "error": err
                })
            })
            usermusique.findAll().then(function (results) {
               
                var matrice = math.matrix();
              
                matrice.resize([nbuser, nbmusique]);
                
                var matriceprime = math.matrix();
                matriceprime.resize([nbmusique, nbuser]);
                
                for (var i = 0, len = results.length; i < len; i++) {
                    var row = results[i];


                    matrice.subset(math.index(row.idUser - 1, row.idFilm - 1), 1);
                    matriceprime.subset(math.index(row.idFilm - 1, row.idUser - 1), 1);
                }
                var matrice3 = math.multiply(matriceprime, matrice);
                var matrice4 = math.multiply(matrice3, matriceprime);
                var matrice5 = math.transpose(matrice4);
                console.log(matrice5);
                for (var i = 0, len = results.length; i < len; i++) {
                    var row = results[i];
                    if (matrice.subset(math.index(row.idUser - 1, row.idFilm - 1)) == 1) {
                        matrice5.subset(math.index(row.idUser - 1, row.idFilm - 1), 0);
                    }
                }
           fs.truncate('musiqueuser.json', 0, function(){console.log('done')})
             for (var t = 0; t < userresult.length; t++) {
                    var rowuser = userresult[t];
                    console.log(t)
                    
                    for (var i = 0, len = musiqueresult.length; i < len; i++) {
                        console.log(i) 
                        var row = musiqueresult[i];
                       

                        if (matrice5.subset(math.index(t, i)) != 0 && rowuser.id==req.params.id) {
                            var reqstat = {
                                "user": rowuser.id,
                                "film": row.name
                            }
                           fs.appendFile("musiqueuser.json", JSON.stringify(reqstat) + "\n", function (err) {
                                if (err) {
                                    throw err;
                                }

                            })
                        }


                    }
                }
                console.log(matrice5);

            }).catch(function (err) {

                res.json({
                    "code": 2,
                    "message": "Sequelize error in userfilm",
                    "error": err
                })
            })
        
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