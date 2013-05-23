function datamosisCtrl($scope, $http, $timeout) {

//    $scope.status = $http.get('/state', {
//        }).then(function(resp){
//            console.log("resp", resp);
//            return resp.data;
//        });

    $scope.sendMsg = function() {
        $http.post('/sendMessage', {
                type: "news",
                x: $scope.msg.x,
                y: $scope.msg.y,
                subject: $scope.msg.subject,
                payload: $scope.msg.payload
        }).then(function(resp){
            console.log("message sent", resp);
        });
    }

    $scope.tick = function() {
        $http.get('/tick', {
            params: {subject: $scope.disp.subject}
        }).then(function(resp){
            console.log("tick", resp);
            $scope.status = resp.data;
        });

        //$timeout($scope.tick , 2000);
    }

    //$timeout($scope.tick , 2000);
}