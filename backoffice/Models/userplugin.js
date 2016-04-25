var sequelize=require("./sequelize");

module.exports=sequelize.import("userplugin",function(sequelize,datatypes){
	return sequelize.define("UserPlugin", {
		idUser:{
			type: datatypes.BIGINT,
			primaryKey:true,
			autoIncrement:true
		},
		idPlugin:{
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