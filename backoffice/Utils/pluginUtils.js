var models=require("../models");
var pluginutils=function(name,author){
	
	this.name=name;
	this.author=author;
	
	
}

pluginutils.prototype.addplugin=function(u1,callback){
		var plugin=models.plugin;
		if(u1){
		plugin.create({
			
			"name" : u1.name,
			"author": u1.author
			
			
			
		}).then(function(result){
			console.log("plugin créer")
			callback(undefined,result)
		}).catch(function(err){
			console.log("plugin pas créer")
		});
		
	
	}
}
pluginutils.prototype.update=function(request,attributes,callback){
		var plugin=models.plugin;
		plugin.find(request).then(function(results){
			if(results){
			
				results.updateAttributes(attributes).then(function(results){
					console.log("plugin update");
					callback(undefined,results);
					
					
				}).catch(function(err){
					console.log("plugin pas  update");
				})
			}else{
				console.log("pas de result")
			}
		
		}).catch(function(err){
			console.log(err);
		});
		
}
pluginutils.prototype.delete = function(idplugin, callback) {
	var plugin = models.plugin;
	if(idplugin) {
		plugin.find({
			"where" : {
				id : idplugin
			}
		}).then(function(result) {
			if(result) {
				result.destroy().then(function(success) {
					callback(success);
				}).catch(function(err) {
					callback(err);
				});
			} else {
				callback("error can't find "+idplugin);
			}
		}).catch(function(err) {
			callback(err);
		});
	} else {
		callback(results);
	}
};

module.exports=pluginutils;