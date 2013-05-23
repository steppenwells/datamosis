function datamosisCtrl($scope, $http, $timeout) {

//    $scope.status = $http.get('/state', {
//        }).then(function(resp){
//            console.log("resp", resp);
//            return resp.data;
//        });

    $scope.msgTypes = [
        {name: "news", t: "news"},
        {name: "location", t: "loc"},
        {name: "program", t: "prog"}
    ]

    $scope.sendMsg = function() {
        console.log($scope.msg.type);
        $http.post('/sendMessage', {
                type: $scope.msg.type,
                x: $scope.msg.x,
                y: $scope.msg.y,
                subject: $scope.msg.subject,
                payload: $scope.msg.payload
        }).then(function(resp){
            console.log("message sent", resp);
        });
    }

    $scope.count = 0;

    $scope.tick = function() {
        $scope.simImg = '/tickImg?subject=' + $scope.disp.subject + '&c=' +  $scope.count++;
//        $http.get('/tick', {
//            params: {subject: $scope.disp.subject}
//        }).then(function(resp){
//            console.log("tick", resp);
//            $scope.status = resp.data;
//        });

        $timeout($scope.tick , 500);
    }

    $timeout($scope.tick , 500);
}