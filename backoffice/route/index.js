
module.exports=function(app){
	require("./user")(app);
        require("./film")(app);
        require("./admin")(app);
        require("./musique")(app);
        require("./plugin")(app);
}