var sequelize=require("./sequelize");

module.exports=sequelize.import("plugin",function(sequelize,datatypes){
	return sequelize.define("Plugin", {
		id:{
			type: datatypes.BIGINT,
			primaryKey:true,
			autoIncrement:true
		},
		
		name :{
			type: datatypes.STRING
		},
		author:{
			type: datatypes.STRING
		}
	}, {
		paranoid:true,
		freezeTableName:true,
		underscored:true
	
	});
})