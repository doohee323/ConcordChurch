'use strict';

/**
 * @ngdoc function
 * @name concordchurchApp.controller:WordsCtrl
 * @description
 * # WordsCtrl
 * Controller of the concordchurchApp
 */
var bRun;
angular.module('concordchurchApp')
  .controller('WordsCtrl', function ($rootScope, $scope, $window, $stateParams, $state, $http, $location, $timeout, config, MeService) {
		$scope.$location = $location;
		var id = $stateParams.id;
  	var prefix = "/api/bunch/v2/me/";
		var currentRow = 1;
		
		$scope.alert = '';
		$scope.showListBottomSheet = function($event) {
		  $scope.alert = '';
		};	
		$scope.showGridBottomSheet = function($event) {
	    $scope.alert = '';
	  };	
	
	  $scope.next = function(id) {
	    if(!id) {
	    	id = currentRow + 1;
	    	currentRow = id;
	    }
			$scope.retrieve(id);
		}
	
	  $scope.prev = function(id) {
	    if(!id) {
	    	id = currentRow - 1;
	    	currentRow = id;
	    }
			$scope.retrieve(id);
		}
		
	  $scope.refresh = function(id) {
			if(!id) {
				id = currentRow;
			}
			MeService.R.get({'id':id}, function(data) {
				if(data.rows) {
					$scope.words = data.rows;
					localStorage.setItem(prefix + id, JSON.stringify($scope.words));
					if(Android) {
						Android.cacheJson(JSON.stringify($scope.words));
					}
			    config.curitem = $scope.words[0];
				}
			}, function(error) {
		   $scope.words = [{"img":"http://ckbch.org/_data/ckbch/file/m2_1/thumb/470570","link":"http://ckbch.org/_chboard/bcast/audio.php?bo_table=m2_1&ty=v&wr_id=470570",
		   "title":"하나님이 세상을 이처럼 사랑하사","content":"요3:14-16","speaker":"길영환 목사",
		   "date":"2014-12-21","bible":"[요] 3:14 모세가 광야에서 뱀을 든 것같이 인자도 들려야 하리니 [요] 3:15 이는 저를 믿는 자마...",
		   "video":"http://www.youtube.com/v/ghS_TOQJ4ow?version=3&hl=en_US&rel=0&autoplay=1",
		   "desc":"[요] 5:24 내가 진실로 진실로 너희에게 이르노니 내 말을 듣고 또 나 보내신 이를 믿는 자는 영생을 얻었고 심판에 이르지 아니하나니 사망에서 생명으로 옮겼느니라아멘"},{"img":"http://ckbch.org/_data/ckbch/file/m2_1/thumb/470570","link":"http://ckbch.org/_chboard/bcast/audio.php?bo_table=m2_1&ty=v&wr_id=470570","title":"그를 믿는 자마다 멸망하지 않고","content":"요3:16-21","speaker":"길영환 목사","date":"2014-12-14","bible":"[요] 3:16 하나님이 세상을 이처럼 사랑하사 독생자를 주셨으니 이는 저를 믿는 자마��� 멸망치 ...","video":"http://www.youtube.com/v/ghS_TOQJ4ow?version=3&hl=en_US&rel=0&autoplay=1","desc":"[요] 5:24 내가 진실로 진실로 너희에게 이르노니 내 말을 듣고 또 나 보내신 이를 믿는 자는 영생을 얻었고 심판에 이르지 아니하나니 사망에서 생명으로 옮겼느니라아멘"},{"img":"http://ckbch.org/_data/ckbch/file/m2_1/thumb/470570","link":"http://ckbch.org/_chboard/bcast/audio.php?bo_table=m2_1&ty=v&wr_id=470570","title":"영생(Eternal Life)","content":"요5:24","speaker":"길영환 목사","date":"2014-12-28","bible":"[요] 5:24 내가 진실로 진실로 너희에게 이르노니 내 말을 듣고 또 나 보내신 이를 믿는 자는 영...","video":"http://www.youtube.com/v/ghS_TOQJ4ow?version=3&hl=en_US&rel=0&autoplay=1","desc":"[요] 5:24 내가 진실로 진실로 너희에게 이르노니 내 말을 듣고 또 나 보내신 이를 믿는 자는 영생을 얻었고 심판에 이르지 아니하나니 사망에서 생명으로 옮겼느니라아멘"},{"img":"http://ckbch.org/_data/ckbch/file/m2_1/thumb/470570","link":"http://ckbch.org/_chboard/bcast/audio.php?bo_table=m2_1&ty=v&wr_id=470570","title":"다니엘의 감사","content":"단6:10","speaker":"길영환 목사","date":"2014-11-09","bible":"[단] 6:10 다니엘이 이 조서에 어인이 찍힌 것을 알고도 자기 집에 돌아가서는 그 방의 예루살렘...","video":"http://www.youtube.com/v/ghS_TOQJ4ow?version=3&hl=en_US&rel=0&autoplay=1","desc":"[요] 5:24 내가 진실로 진실로 너희에게 이르노니 내 말을 듣고 또 나 보내신 이를 믿는 자는 영생을 얻었고 심판에 이르지 아니하나니 사망에서 생명으로 옮겼느니라아멘"}];
			});
		}
	
	  $scope.retrieve = function(id) {
			var datast = localStorage.getItem(prefix + id);
			if(datast) {
				$scope.words = JSON.parse(datast);
		    config.curitem = $scope.words[0];
			} else {
				$scope.refresh(id);
		  }
		}
    
    $scope.open = function(item) {
    	config.item = item;
    	$state.go('video');
			return;    
    	item = JSON.parse(item);
    	$window.open(item.video, '', 'scrollbars=no,resizeable=no,toolbar=no,status=no,top=100,left=100,width=741,height=472');
    }
    
  $scope.title1 = 'Button';
  $scope.title4 = 'Warn';
  $scope.isDisabled = true;
  $scope.googleUrl = 'http://google.com';    
})

.controller('ListBottomSheetCtrl', function($scope, config) {
})
.controller('GridBottomSheetCtrl', function($scope, config) {
  $scope.items = [
    { name: 'facebook', icon: 'facebook' },
    { name: 'twitter', icon: 'twitter' },
    { name: 'google', icon: 'google' },
    { name: 'email', icon: 'email' },
    { name: 'sms', icon: 'sms' },
    { name: 'copy', icon: 'copy' }
  ];
  $scope.listItemClick = function($index) {
    var clickedItem = $scope.items[$index];
//    if(clickedItem.name == 'facebook') {
    	$scope.sendSns(clickedItem.name);
//    }
  };
  
	$scope.sendSns = function(sns) {
		config.curitem;
		var url = config.curitem.link;
		if(url.indexOf('#') > -1) url = url.substring(0, url.lastIndexOf('#'));
		var txt = '[콩코드 침례 교회] - title :' + config.curitem.title + ' - content: ' + config.curitem.bible;
		
	    var o;
	    var _url = encodeURIComponent(url);
	    var _txt = encodeURIComponent(txt);
	    var _br  = encodeURIComponent('\r\n');
	 
	    switch(sns) {
	        case 'facebook':
	            o = {
	                method:'popup',
	                url:'http://www.facebook.com/sharer/sharer.php?u=' + _url
	            };
	            break;
	        case 'twitter':
	            o = {
	                method:'popup',
	                url:'http://twitter.com/intent/tweet?text=' + _txt + '&url=' + _url
	            };
	            break;
	        case 'google':
	            o = {
	                method:'popup',
	                url:'https://plus.google.com/share?url=' + _url
	            };
	            break;
	        default:
	            alert('Not supported!');
	            return false;
	    }

	    switch(o.method) {
	        case 'popup':
	            window.open(o.url);
	            break;
	        case 'web2app':
	            if(navigator.userAgent.match(/android/i))
	            {
	                // Android
	                setTimeout(function(){ location.href = 'intent://' + o.param + '#Intent;' + o.g_proto + ';end'}, 100);
	            } else if(navigator.userAgent.match(/(iphone)|(ipod)|(ipad)/i)) {
	                // Apple
	                setTimeout(function(){ location.href = o.a_store; }, 200);          
	                setTimeout(function(){ location.href = o.a_proto + o.param }, 100);
	            } else {
	                alert('Mobile only!');
	            }
	            break;
		    }
  	}      
})
