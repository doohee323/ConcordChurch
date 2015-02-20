'use strict';

angular.module('concordchurchApp')
.factory('MeService', function ($resource, config) {
  return {
    R: $resource("/bbs/:id", {
    	id:"@id"
		})
	};
});
