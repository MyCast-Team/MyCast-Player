var user=require("./userUtils");
var film=require("./filmUtils");
var admin=require("./adminUtils");
var musique=require("./musiqueUtils");
var plugin =require("./pluginUtils");
module.exports={
	"user" : user,
        "film":film,
        "musique":musique,
        "admin":admin,
        "plugin":plugin
};