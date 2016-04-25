var sequelize=require("./sequelize");

module.exports=sequelize.import("usermusique",function(sequelize,datatypes){
	return sequelize.define("UserMusique", {
		iduser:{
			type: datatypes.BIGINT,
			primaryKey:true,
			autoIncrement:true
		},
		idmusique:{
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