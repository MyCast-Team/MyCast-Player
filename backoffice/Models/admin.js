var sequelize=require("./sequelize");

module.exports=sequelize.import("admin",function(sequelize,datatypes){
	return sequelize.define("Admin", {
		id:{
			type: datatypes.BIGINT,
			primaryKey:true,
			autoIncrement:true
		},
		
		login :{
			type: datatypes.STRING
		},
		password:{
			type: datatypes.STRING
		},
		email:{
			type: datatypes.STRING
		}
	}, {
		paranoid:true,
		freezeTableName:true,
		underscored:true
	
	});
})