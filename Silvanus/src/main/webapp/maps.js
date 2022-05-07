let map;
let markers = [];
let points = [];
let polygons = {};

function initMap() 
{
    var map_center = {lat:  38.659784, lng:  -9.202765};

    map = new google.maps.Map(document.getElementById('map'), {
        center: map_center,
        zoom: 15
    });

    createPolygon();
}

function point(_lat, _lng){
    return {lat: _lat , lng: _lng};
}

function line(p1, p2){
    return [p1,p2];
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

function createPolygon(){
    map.addListener("click", (mapsMouseEvent) => {
        const latLng = mapsMouseEvent.latLng;
        points.push(latLng)
        addMarker(latLng);
        console.log(points);

        if (points.length > 1){
            const new_line = line(points[points.length-2], points[points.length-1]);
            addLine(new_line, "#FFFF00");
        }
    });
}

function hideMarkers(){
    setAllMarkers(null);
}

function showMarkers(){
    setAllMarkers(map);
}

function setAllMarkers(value){
    for (let i = 0; i < markers.length; i++) {
        markers[i].setMap(value);
      }
}