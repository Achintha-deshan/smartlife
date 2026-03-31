const BASE_URL = "http://localhost:8080/api/v1/vehicle";
let map;
let vehicleMarkers = {};
let geofenceCircles = {};

$(document).ready(function () {
    const token = localStorage.getItem("token");
    const adminId = localStorage.getItem("userId");

    if (!token || !adminId) {
        window.location.href = "signinup.html";
        return;
    }

    initMap();
    loadVehicles();

    setInterval(loadVehicles, 5000);
});

function initMap() {
    map = L.map('vehicle-map').setView([6.0595, 80.2210], 14);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; OpenStreetMap'
    }).addTo(map);
}

function loadVehicles() {
    const adminId = localStorage.getItem("userId");
    const token = localStorage.getItem("token");

    $.ajax({
        url: `${BASE_URL}/all/${adminId}`,
        type: "GET",
        headers: { 'Authorization': 'Bearer ' + token },
        success: function (res) {
            const vehicles = res.data ? res.data : res;
            updateVehicleUI(vehicles);
        },
        error: function (err) {
            console.error("Data Fetch Error:", err);
        }
    });
}

function updateVehicleUI(vehicles) {
    let listHtml = "";
    let outOfZoneCount = 0;

    vehicles.forEach(v => {
        const isOut = v.status === "OUT_OF_ZONE";
        if (isOut) {
            outOfZoneCount++;
        }
        listHtml += `
    <a href="#" class="list-group-item list-group-item-action ${isOut ? 'list-group-item-danger' : 'list-group-item-light'}" 
       onclick="focusVehicle('${v.vehi_id}')" 
       style="border-left: 5px solid ${isOut ? '#dc3545' : '#28a745'}; margin-bottom: 5px;">
        <div class="d-flex justify-content-between align-items-center">
            <div>
                <strong class="d-block">${v.vehi_number}</strong>
                <small class="text-muted">ID: ${v.vehi_id}</small>
            </div>
            <span class="badge ${isOut ? 'bg-danger' : 'bg-success'}">${v.status}</span>
        </div>
    </a>`;

        if (v.lastLat && v.lastLng) {
            updateMarker(v);
            drawFence(v);
        }
    });

    $('#vehicle-list').html(listHtml);
    $('#activeCount').text("Fleet Size: " + vehicles.length);

    if (outOfZoneCount > 0) {
        $('#geofence-alert-box').removeClass('d-none');
        $('#alert-msg').text(`Security Alert: ${outOfZoneCount} Vehicle(s) out of Safe Zone!`);
    } else {
        $('#geofence-alert-box').addClass('d-none');
    }
}

function updateMarker(v) {
    const isOut = v.status === "OUT_OF_ZONE";
    const markerColor = isOut ? '#dc3545' : '#28a745';

    const dotIcon = L.divIcon({
        className: 'custom-div-icon',
        html: `<div style="background-color: ${markerColor}; 
                    width: 15px; height: 15px; border-radius: 50%; border: 2px solid white; 
                    box-shadow: 0 0 5px rgba(0,0,0,0.5);" 
                    class="${isOut ? 'marker-pulse' : ''}"></div>`,
        iconSize: [15, 15],
        iconAnchor: [7, 7]
    });

    if (vehicleMarkers[v.vehi_id]) {
        vehicleMarkers[v.vehi_id].setLatLng([v.lastLat, v.lastLng]);
        vehicleMarkers[v.vehi_id].setIcon(dotIcon);
    } else {
        vehicleMarkers[v.vehi_id] = L.marker([v.lastLat, v.lastLng], { icon: dotIcon }).addTo(map);
        vehicleMarkers[v.vehi_id].bindTooltip(v.vehi_number, { permanent: false, direction: 'top' });
    }
}

function drawFence(v) {
    if (geofenceCircles[v.vehi_id]) {
        map.removeLayer(geofenceCircles[v.vehi_id]);
    }

    if (v.fenceLat && v.fenceLng && v.fenceRadius) {
        const fenceColor = v.status === 'OUT_OF_ZONE' ? '#dc3545' : '#28a745';
        geofenceCircles[v.vehi_id] = L.circle([v.fenceLat, v.fenceLng], {
            color: fenceColor,
            fillColor: fenceColor,
            fillOpacity: 0.15,
            radius: v.fenceRadius
        }).addTo(map);
    }
}

function focusVehicle(id) {
    if (vehicleMarkers[id]) {
        const pos = vehicleMarkers[id].getLatLng();
        map.setView([pos.lat, pos.lng], 16);
        vehicleMarkers[id].openTooltip();
    }
}

function initMapCenter() {
    if (map) map.setView([6.0595, 80.2210], 13);
}