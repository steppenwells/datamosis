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
                payload: $scope.msg.payload,
                targetX: $scope.msg.tarx,
                targetY: $scope.msg.tary
        }).then(function(resp){
            console.log("message sent", resp);
        });
    }

    $scope.nodeData = [];
    $scope.listenAtX = 0;
    $scope.listenAtY = 0;

    $scope.dispSubject = 'foo';

    $scope.count = 0;

    $scope.tick = function() {
        $scope.simImg = '/tickImg?subject=' + $scope.dispSubject + '&c=' +  $scope.count++;

        $http.get('/nodeData', {
            params: {x: $scope.listenAtX, y: $scope.listenAtY}
        }).then(function(resp){
            console.log("node data resp", resp)
            $scope.nodeData = resp.data.nodeData;
        });

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