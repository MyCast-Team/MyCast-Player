var sequelize=require("./sequelize");

module.exports=sequelize.import("film",function(sequelize,datatypes){
	return sequelize.define("Film", {
		id:{
			type: datatypes.BIGINT,
			primaryKey:true,
			autoIncrement:true
		},
		
                name :{
			type: datatypes.STRING
		},
		director:{
			type: datatypes.STRING
		},
		date:{
			type: datatypes.DATE
		}
	}, {
		paranoid:true,
		freezeTableName:true,
		underscored:true
	
	});
})