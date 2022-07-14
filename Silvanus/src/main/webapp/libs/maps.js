let map;
let geocoder;
let polygon_drawing_tools;
let route_drawing_tools;
let markers = [];
let markers_points = [];

let last_index = 0;
let other_markers = 0;
let points = [];
let lines = [];
let registed_polygon;
let registed_route;
let polygon_result = [];
let route_result = [];

let polygons = [];
let polygons_points = [];

let click_listener;

let viewport;
let viewport_center;
let viewport_zoom;
let viewport_moving = false;

let MAP_MODE = {
    "LIGHT": 'c5f91d16484f03de',
    "DARK": 'e00de21e9b37f13e'
};

//TODO Change map bounds
const PORTUGAL_BOUND = {
    north: 42.17,
    south: 36.85,
    west: -9.55,
    east: -6.17,
};

const fct_center = {lat:  38.659784, lng:  -9.202765};
const fct_zoom = 15;

const macao_center = {
    lat: 39.613428,
    lng: -7.977220
}
const macao_zoom = 10.0;

function initMap() 
{
    var map_center = macao_center;

    map = new google.maps.Map(document.getElementById('map'), {
        center: map_center,
        zoom: 11.5,
        mapId: 'c5f91d16484f03de',
        restriction: {
            latLngBounds: {
              north: PORTUGAL_BOUND.north,
              south: PORTUGAL_BOUND.south,
              east: PORTUGAL_BOUND.east,
              west: PORTUGAL_BOUND.west,
            },
          }
    });

    geocoder = new google.maps.Geocoder();

    initViewmap();
    initPolygonDrawingTools();
    initRouteDrawingTools();

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
        /*const ab = viewport.Ab;
        const ua = viewport.Ua;
        const bound_box = box(ua.hi, ua.lo, ab.hi, ab.lo);
        console.log(bound_box);*/

        //Request to DB
        console.log(viewport_center);
        console.log(viewport);
        let center = point(viewport_center.lat(), viewport_center.lng());
        console.log(center);
        loadChunk(center);
    })

    map.addListener("bounds_changed", function(){
        //Bounds of the map
        viewport = map.getBounds();
        viewport_zoom = map.getZoom();
    });

    map.addListener("center_changed", function(){
        viewport_center = map.getCenter();
    });
}

function setCenter(latlng){
    map.panTo(latlng);
}

function setZoom(zoom){
    map.setZoom(zoom);
}

function setBounds(bounds){
    map.fitBounds(bounds);
}

function getViewport(){
    return {
        "bounds":viewport,
        "center":viewport_center,
        "zoom": viewport_zoom
    }
}

function initRouteDrawingTools(){
    route_drawing_tools = new google.maps.drawing.DrawingManager({
        drawingMode: google.maps.drawing.OverlayType.POLYLINE,
        drawingControl: false,
        drawingControlOptions: {
            position: google.maps.ControlPosition.TOP_LEFT,
            drawingModes: [
                google.maps.drawing.OverlayType.POLYLINE,
            ],
        },
        polylineOptions:{
            editable:true,
            fillColor: "#0000dd",
            strokeColor: "#0000ff"
        },
    });

    route_drawing_tools.addListener("polylinecomplete", function(polyline){
        console.log("polyline complete")
        //Add event listener to edit and update all coords of last polygon
        google.maps.event.addListener(polyline.getPath(), 'set_at', function() {
            route_result = convertPath(polyline.getPath().getArray());
            console.log(route_result);
        });

        google.maps.event.addListener(polyline.getPath(), 'insert_at', function() {
            route_result = convertPath(polyline.getPath().getArray());
            console.log(route_result);
        });

        google.maps.event.addListener(polyline.getPath(), 'remove_at', function() {
            route_result = convertPath(polyline.getPath().getArray());
            console.log(route_result);

        });

        route_result = convertPath(polyline.getPath().getArray())
        console.log(route_result);

        setRoute(null, polyline);
    });

    toggleRouteDrawingControl(false);

    route_drawing_tools.setMap(null);
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
        //Add event listener to edit and update all coords of last polygon
        google.maps.event.addListener(polygon.getPath(), 'set_at', function() {
            polygon_result = convertPath(polygon.getPath().getArray());

        });

        google.maps.event.addListener(polygon.getPath(), 'insert_at', function() {
            polygon_result = convertPath(polygon.getPath().getArray());


        });

        google.maps.event.addListener(polygon.getPath(), 'remove_at', function() {
            polygon_result = convertPath(polygon.getPath().getArray());

        });

        polygon_result = convertPath(polygon.getPath().getArray());

        let coord = polygon_result[0].lat + "," + polygon_result[0].lng;
        codeAddress(coord, function(results){
            //Get most info of geocoding result
            //TODO Alter this part
            const index = results.length-3 //results.length-3
            const formatted = results[index].address_components;
            document.getElementById("townhall-terrain").value = formatted[0].long_name;
            document.getElementById("district-terrain").value = formatted[1].long_name;

        });

        setPolygon(null, polygon);
    });

    togglePolygonDrawingControl(false);

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

function setRegisted(register_polygon, visible, value){
    //Get last polygon and deletes old one
    if (register_polygon != null){
        register_polygon.setMap(visible);
    }
    register_polygon = value;
}

function setPolygon(visible, value){
    if (registed_polygon != null){
        registed_polygon.setMap(visible);
    }
    registed_polygon = value;
}

function setRoute(visible, value){
    if (registed_route != null){
        registed_route.setMap(visible);
    }
    registed_route = value;
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
    let exist = false;
    for (let marker_index = 0; marker_index < markers_points.length && !exist; marker_index++) {
        const element = markers_points[marker_index];
        exist = JSON.stringify(element) === JSON.stringify(coords);

    }
    
    if (!exist){
        const marker = new google.maps.Marker({
            position: coords,
            map,
        });
    
        markers.push(marker);
        markers_points.push(coords);
    }
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

function addPolygon(coords, color){
    let exist = false;

    for (let i = 0; i < polygons_points.length && !exist; i++) {
        const element = polygons_points[i];
        exist = JSON.stringify(element) === JSON.stringify(coords);
    }

    if(!exist){
        const polygon = new google.maps.Polygon({
            map,
            paths: coords,
            strokeColor: color,
            strokeOpacity: 0.6,
            strokeWeight: 2,
            fillColor: color,
            fillOpacity: 0.30,
            geodesic: true,
        });

        google.maps.event.addListener(polygon, 'click', function(event) {
            console.log("Clicked on Polygon");
        })

        polygons.push(polygon);
        polygons_points.push(coords);
    }
}

function addCluster(){
    cluster(markers);
}

function cluster(marker_list){
    // Add a marker clusterer to manage the markers.
  new MarkerClusterer({ marker_list, map });
}

function center(given_points){
    let center = [0.0,0.0];
    for ( let i = 0 ; i < given_points.length ; i++){
        const curr_point = given_points[i];
        console.log(curr_point);
        center[0] += parseFloat(curr_point.lat)/given_points.length;
        center[1] += parseFloat(curr_point.lng)/given_points.length;
    }
    return point(center[0], center[1]);
}

function toggleDrawing(value){
    togglePolygonDrawingControl(value);
    toggleRouteDrawingControl(value);
}

function toggleDrawingControl(tools, registed, value){
    tools.setOptions({
        drawingControl: value
    });
    const visible = value ? map : null;
    tools.setMap(visible);
    setRegisted(registed, visible, registed);
}

function togglePolygonDrawingControl(value){
    toggleDrawingControl(polygon_drawing_tools, registed_polygon, value);
}

function toggleRouteDrawingControl(value, confirmed){
    //setMenuID('route_definition_menu',String(value));

    polygon_drawing_tools.setOptions({
        drawingControl: !value
    });
    route_drawing_tools.setOptions({
        drawingControl: value
    });
    const visible = confirmed ? map : null;
    route_drawing_tools.setMap(visible);
    setRoute(visible, registed_route);
    //setMenuID("route_definition_menu",String(value));
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

function submitPolygon(){
	submitTerrain(polygon_result, route_result);
}

//---- Chunk Loading Related ----
let loaded_chunk = {};

function saveChunk(id, content){
    loaded_chunk[id] = content;
}

//TODO Make this function work
function hasChunk(id){
    if (loaded_chunk[id] === null){
        loaded_chunk[id] = false;
    }
    return loaded_chunk[id];
}