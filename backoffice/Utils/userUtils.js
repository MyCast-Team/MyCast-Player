var models=require("../models");
var userutils=function(idlibrary){
	
	this.idlibrary=idlibrary;
	
}

userutils.prototype.adduser=function(u1,callback){
		var user=models.user;
		if(u1){
		user.create({
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
userutils.prototype.delete = function(idUser, callback) {
	var User = models.user;
	if(idUser) {
		User.find({
			"where" : {
				id : idUser
			}
		}).then(function(result) {
			if(result) {
				result.destroy().then(function(success) {
					callback(success);
				}).catch(function(err) {
					callback(err);
				});
			} else {
				callback("error can't find "+idUser);
			}
		}).catch(function(err) {
			callback(err);
		});
	} else {
		callback(results);
	}
};

module.exports=userutils;