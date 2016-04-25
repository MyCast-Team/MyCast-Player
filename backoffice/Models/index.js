var sequelize=require("./sequelize");
var user=require("./user");
var film=require("./film");
var userfilm=require("./userfilm");
var usermusique=require("./usermusique");
var musique=require("./musique");
var admin=require("./admin");
var plugin=require("./plugin");
var userplugin=require("./userplugin");
sequelize.sync();

module.exports={
	"sequelize":sequelize,
	"user":user,
        "film":film,
        "musique":musique,
        "userfilm":userfilm,
        "usermusique":usermusique,
        "admin":admin,
        "plugin":plugin,
        "userplugin":userplugin,
};
