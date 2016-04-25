var sequelize=require("./sequelize");

module.exports=sequelize.import("userfilm",function(sequelize,datatypes){
	return sequelize.define("UserFilm", {
		idUser:{
			type: datatypes.BIGINT,
			primaryKey:true,
			autoIncrement:true
		},
		idFilm:{
			type: datatypes.BIGINT,
			primaryKey:true,
			
		},
		date :{
			type: datatypes.DATE
		}
	}, {
		paranoid:true,
		freezeTableName:true,
		underscored:true
	
	});
})