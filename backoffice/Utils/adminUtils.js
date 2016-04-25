var models=require("../models");
var adminutils=function(login,password,email){
	
	this.login=login;
	this.password=password;
	this.email=email;
	
	
}

adminutils.prototype.addadmin=function(u1,callback){
		var admin=models.admin;
		if(u1){
		admin.create({
			
			"login" : u1.login,
			"password": u1.password,
			"email" : u1.email
			
			
		}).then(function(result){
			console.log("admin créer")
			callback(undefined,result)
		}).catch(function(err){
			console.log("admin pas créer")
		});
		
	
	}
}
adminutils.prototype.update=function(request,attributes,callback){
		var admin=models.admin;
		admin.find(request).then(function(results){
			if(results){
			
				results.updateAttributes(attributes).then(function(results){
					console.log("admin update");
					callback(undefined,results);
					
					
				}).catch(function(err){
					console.log("admin pas  update");
				})
			}else{
				console.log("pas de result")
			}
		
		}).catch(function(err){
			console.log(err);
		});
		
}
adminutils.prototype.delete = function(idAdmin, callback) {
	var admin = models.admin;
	if(idadmin) {
		admin.find({
			"where" : {
				id : idadmin
			}
		}).then(function(result) {
			if(result) {
				result.destroy().then(function(success) {
					callback(success);
				}).catch(function(err) {
					callback(err);
				});
			} else {
				callback("error can't find "+idadmin);
			}
		}).catch(function(err) {
			callback(err);
		});
	} else {
		callback(results);
	}
};

module.exports=adminutils;