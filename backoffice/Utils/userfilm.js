var models=require("../models");
var userutils=function(idlibrary,lastname,firstname,email,birthdate){
	
	this.idlibrary=idlibrary;
	this.lastname=lastname;
	this.firstname=firstname;
	this.email=email;
	this.birthdate=birthdate;
	
}

userutils.prototype.adduser=function(u1,callback){
		var user=models.user;
		if(u1){
		user.create({
			"id_library":u1.idlibrary,
			"lastname" : u1.lastname,
			"firstname": u1.firstname,
			"email" : u1.email,
			"birthdate" :u1.birthdate,
			
		}).then(function(result){
			console.log("user créer")
			callback(undefined,result)
		}).catch(function(err){
			console.log("user pas créer")
		});
		
	
	}
}
userutils.prototype.update=function(request,attributes,callback){
		var user=models.user;
		user.find(request).then(function(results){
			if(results){
			
				results.updateAttributes(attributes).then(function(results){
					console.log("user update");
					callback(undefined,results);
					
					
				}).catch(function(err){
					console.log("user pas  update");
				})
			}else{
				console.log("pas de result")
			}
		
		}).catch(function(err){
			console.log(err);
		});
		
}

module.exports=userutils;