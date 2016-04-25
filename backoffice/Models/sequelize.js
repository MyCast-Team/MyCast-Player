var sequelize=require("sequelize");

module.exports= new sequelize("mycast","root","",{
	pool:false,
	host:"localhost"
});
