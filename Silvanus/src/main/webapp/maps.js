let map;
let markers = [];
let points = [];
let polygons = {};

let click_listener;

//TODO Change map bounds
const PORTUGAL_BOUND = {
    north: -34.36,
    south: -47.35,
    west: 166.28,
    east: -175.81,
  };

function initMap() 
{
    var map_center = {lat:  38.659784, lng:  -9.202765};

    map = new google.maps.Map(document.getElementById('map'), {
        center: map_center,
        zoom: 15
    });

    createPolygon("#00aaff");
}

function point(_lat, _lng){
    return {lat: _lat , lng: _lng};
}

function line(p1, p2){
    return [p1,p2];
}

function distanceSquared(p1, p2){
    return Math.pow(p2.lat-p1.lat,2) + Math.pow(p2.lng-p1.lng,2);
}

function addMarker(coords){
    const marker = new google.maps.Marker({
        position: coords,
        map,
      });
    markers.push(marker);
}

function addLine(coords, color){
    const flightPath = new google.maps.Polyline({
        path: coords,
        geodesic: true,
        strokeColor: color,
        strokeOpacity: 1.0,
        strokeWeight: 2,
      });
    
      flightPath.setMap(map);
    
}

function addPolygon(coords, center, color){
    new google.maps.Polygon({
        map,
        paths: coords,
        strokeColor: color,
        strokeOpacity: 0.6,
        strokeWeight: 2,
        fillColor: color,
        fillOpacity: 0.30,
        geodesic: true,
      });
    
      addMarker(center);
}

function center(given_points){
    let center = [0.0,0.0];
    for ( let i = 0 ; i < given_points.length ; i++){
        const curr_point = given_points[i].toJSON();
        console.log(curr_point);
        center[0] += parseFloat(curr_point.lat)/given_points.length;
        center[1] += parseFloat(curr_point.lng)/given_points.length;
    }
    return point(center[0], center[1]);
}

function createPolygon(color){
    let other_markers = markers.length;
    click_listener = map.addListener("click", (mapsMouseEvent) => {
        const latLng = mapsMouseEvent.latLng;
        points.push(latLng);
        addMarker(latLng);
        console.log(points);

        if (points.length > 1){
            //
            const dist = distanceSquared(points[0].toJSON(), points[points.length-1].toJSON());
            if (dist < 5.0e-7){
                const polygon_center = center(points);
                console.log(polygon_center);
                setMarkers(other_markers, markers.length, null);
                addPolygon(points, polygon_center, color);
                points = [];
            } else {
                const new_line = line(points[points.length-2], points[points.length-1]);
                addLine(new_line, color);
            }
        }
    });
}

function clearListeners(component, type){
    google.maps.event.clearListeners(component, type);
}

function hideMarkers(){
    setAllMarkers(null);
}

function showMarkers(){
    setAllMarkers(map);
}

function setAllMarkers(value){
    setMarkers(0, markers.length, value)
}

function setMarkers(low_index, high_index, value){
    for (let i = low_index; i < high_index; i++) {
        markers[i].setMap(value);
    }
}