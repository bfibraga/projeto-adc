let map;
let geocoder;
let polygon_drawing_tools;
let route_drawing_tools;
let markers = [];

let last_index = 0;
let other_markers = 0;
let points = [];
let lines = [];
let registed_polygon;
let polygon_result = null;

let click_listener;

let viewport;
let viewport_center;
let viewport_zoom;
let viewport_moving = false;

let MAP_MODE = {
    "LIGHT": 'c5f91d16484f03de'
};

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
        zoom: 15,
        mapId: 'c5f91d16484f03de',
    });

    geocoder = new google.maps.Geocoder();

    initViewmap();
    initPolygonDrawingTools();


    //clearListeners(map, "click");
}

function initViewmap(){
    viewport = map.getBounds();
    viewport_center = map.getCenter();
    viewport_zoom = map.getZoom();

    map.addListener("dragstart", function(){
        viewport_moving = true;
    });

    map.addListener("dragend", function(){
        //Stops moving the map
        viewport_moving = false;
    });

    map.addListener("idle", function(){
        console.log("Request chunks");

        //Send request to load chunks of this viewport
        const ab = viewport.Ab;
        const ua = viewport.Ua;
        const bound_box = box(ua.hi, ua.lo, ab.hi, ab.lo);
        console.log(bound_box);

        //Request to DB

    })

    map.addListener("bounds_changed", function(){
        //Bounds of the map
        viewport = map.getBounds();
        viewport_center = map.getCenter();
        viewport_zoom = map.getZoom();
    });
}

function setCenter(latlng){
    map.setCenter(latlng);
}

function getViewport(){
    return {
        "bounds":viewport,
        "center":viewport_center,
        "zoom": viewport_zoom
    }
}

function initPolygonDrawingTools(){
    polygon_drawing_tools = new google.maps.drawing.DrawingManager({
        drawingMode: google.maps.drawing.OverlayType.POLYGON,
        drawingControl: false,
        drawingControlOptions: {
            position: google.maps.ControlPosition.TOP_LEFT,
            drawingModes: [
                google.maps.drawing.OverlayType.POLYGON,
            ],
        },
        polygonOptions:{
            editable:true,
            fillColor: "#00dd00",
            strokeColor: "#00ff00"
        },
    });

    polygon_drawing_tools.addListener("polygoncomplete", function(polygon){
        console.log("polygon complete");

        //Add event listener to edit and update all coords of last polygon
        google.maps.event.addListener(polygon.getPath(), 'set_at', function() {
            console.log("set_at");
            polygon_result = convertPath(polygon.getPath().Qd);
            console.log(polygon_result);
            console.log("Area " + area(polygon_result));
        });

        google.maps.event.addListener(polygon.getPath(), 'insert_at', function() {
            console.log("insert_at");
            polygon_result = convertPath(polygon.getPath().Qd);
            console.log(polygon_result);
            console.log("Area " + area(polygon_result));

        });

        google.maps.event.addListener(polygon.getPath(), 'remove_at', function() {
            console.log("remove_at");
            polygon_result = convertPath(polygon.getPath().Qd);
            console.log(polygon_result);
            console.log("Area " + area(polygon_result));

        });

        polygon_result = convertPath(polygon.getPath().Qd);
        console.log(polygon_result);
        console.log("Area " + area(polygon_result));

        let coord = polygon_result[0].lat + "," + polygon_result[0].lng;
        console.log(coord);
        codeAddress(coord, function(results){
            console.log(results);
            //Get most info of geocoding result
            //TODO Alter this part
            const index = 0 //results.length-3
            const formatted = results[index].formatted_address;
            console.log(formatted);
            document.getElementById("conselho-terrain").value = formatted;
        });

        setRegistedPolygon(null, polygon);
    });

    toggleDrawingControl(false);

    polygon_drawing_tools.setMap(null);
}

function convertPath(path_points){
    let result = [];
    for(let i = 0 ; i < path_points.length ; i++){
        let polygon_point = point(path_points[i].lat(), path_points[i].lng());
        result.push(polygon_point);
    }
    return result;
}

function setRegistedPolygon(visible, value){
    //Get last polygon and deletes old one
    if (registed_polygon != null){
        registed_polygon.setMap(visible);
    }
    registed_polygon = value;
}

// --- Geocoding Functions ---

function codeAddress(addr, task) {
    geocoder.geocode({ 'address': addr}, function(results, status) {
        if(status == 'OK') {
            //Function to execute
            task(results);
        }
        else {
            console.log('Geocode was not successful for the following reason: '+status);
        }
    });
}


// --- Utils Functions --- 

function point(_lat, _lng){
    return {lat: _lat , lng: _lng};
}

function line(p1, p2){
    return [p1,p2];
}

function box(top, bottom, left, right){
    return [
        point(left, top), //TL
        point(right, top), //TR
        point(right, bottom), //BR
        point(left, bottom) //BL
    ];
}

function distanceSquared(p1, p2){
    return Math.pow(p2.lat-p1.lat,2) + Math.pow(p2.lng-p1.lng,2);
}

//TODO Testing

function area(points){
    //First convert latlng to meters

    let factor = [0.0,0.0];
    for(let i = 0 ; i < points.length - 1 ; i++){
        let result = [
            points[i].lat*points[i+1].lng,
            points[i].lng*points[i+1].lat
        ];
        factor[0] += result[0];
        factor[1] += result[1];
    }
    let area = (factor[0] + factor[1])/2.0;
    return area;
}

function addMarker(coords){
    const marker = new google.maps.Marker({
        position: coords,
        map,
      });
    markers.push(marker);
}

function addLine(coords, color){
    const _line = new google.maps.Polyline({
        path: coords,
        geodesic: true,
        strokeColor: color,
        strokeOpacity: 1.0,
        strokeWeight: 2,
      });
    
    _line.setMap(map);
    lines.push(_line);
}

function addPolygon(coords, _center, color){
    const polygon = { 
        data: new google.maps.Polygon({
        map,
        paths: coords,
        strokeColor: color,
        strokeOpacity: 0.6,
        strokeWeight: 2,
        fillColor: color,
        fillOpacity: 0.30,
        geodesic: true,
        editable: true,
      }),
      center: _center
    };
    
    polygons.push(coords);
    polygons.push(polygon);

    //addMarker(_center);
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
   /* other_markers = markers.length;
    last_index = lines.length;
    click_listener = map.addListener("click", (mapsMouseEvent) => {
        const latLng = mapsMouseEvent.latLng;
        points.push(latLng);
        addMarker(latLng);
        console.log(points);

        if (points.length > 1){
            //
            const dist = distanceSquared(points[0].toJSON(), points[points.length-1].toJSON());
            if (dist < 5.0e-7){
                //Vaditation of the polygon

                //Creation of the polygon
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
    });*/
    toggleDrawingControl(false);
}

function toggleDrawingControl(value){
    polygon_drawing_tools.setOptions({
        drawingControl: value
    });
    if (value){
        polygon_drawing_tools.setMap(map);
        setRegistedPolygon(map, registed_polygon);
    } else {
        polygon_drawing_tools.setMap(null);
        setRegistedPolygon(null, registed_polygon);
    }
}

function clearTemporaryData(){
    setLines(last_index, lines.length, null);
    setMarkers(last_index, markers.length, null);
    points=[];
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
    if (value == null){
        lines = lines.slice(0, low_index-1);
    }
}

function setLines(low_index, high_index, value){
    for(let i = low_index ; i < high_index ; i++){
        lines[i].setMap(value);
    }
    if (value == null){
        lines = lines.slice(0, low_index-1);
    }
    console.log(lines);
}

async function submitPolygon(){
	await submitTerrain(polygon_result, []);
}