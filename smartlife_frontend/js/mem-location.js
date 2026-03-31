const BASE_URL = "http://localhost:8080/api/v1/members";
let map;
let memberMarkers = {};

$(document).ready(function () {
    const token = localStorage.getItem("token");
    const userId = localStorage.getItem("userId");
    const role = localStorage.getItem("role");

    if (!token || !userId) {
        window.location.href = "signinup.html";
        return;
    }

    initMap();
    loadAllMembersLocation();

     if (role === 'MEMBER') {
        startLiveTracking(userId, token);
    }

    setInterval(loadAllMembersLocation, 5000);
});

function initMap() {
    map = L.map('family-tracking-map').setView([7.8731, 80.7718], 8);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; OpenStreetMap contributors'
    }).addTo(map);
}

function loadAllMembersLocation() {
    const token = localStorage.getItem("token");
    const role = localStorage.getItem("role");
    const adminId = role === 'ADMIN'
        ? localStorage.getItem('userId')
        : localStorage.getItem('adminId');

    if (!adminId || adminId === 'null') {
        console.error("AdminID not found — please login again.");
        return;
    }

    $.ajax({
        url: BASE_URL + "/all-locations/" + adminId,
        type: "GET",
        headers: { 'Authorization': 'Bearer ' + token },
        success: function (res) {
            const members = res.data ? res.data : res;

            Object.values(memberMarkers).forEach(m => map.removeLayer(m));
            memberMarkers = {};

            members.forEach(m => {
                if (m.lastLat && m.lastLng) {
                    const marker = L.marker([m.lastLat, m.lastLng]).addTo(map);

                    marker.bindPopup(`
                        <div style="text-align:center; font-family:'Poppins',sans-serif; min-width:150px;">
                            <h6 style="margin:0; color:#007bff; font-weight:bold;">${m.nickname}</h6>
                            <p style="margin:5px 0; font-size:13px;">📞 ${m.phoneNumber}</p>
                            <hr style="margin:8px 0;">
                            <a href="tel:${m.phoneNumber}" style="color:white; font-size:11px; text-decoration:none;
                               padding:4px 10px; border-radius:4px; background:#007bff; display:inline-block;">
                                📱 Call Now
                            </a>
                        </div>
                    `);

                    marker.bindTooltip(m.nickname, {
                        permanent: true,
                        direction: 'top',
                        offset: [0, -10]
                    }).openTooltip();

                    memberMarkers[m.memberId] = marker;
                }
            });

            $('#lblLastSync').text(new Date().toLocaleTimeString());
        },
        error: function (err) {
            console.error("Location Load Error:", err);
        }
    });
}

function startLiveTracking(memberId, token) {
    if (!navigator.geolocation) {
        alert("ඔබේ Browser GPS support කරන්නේ නැහැ.");
        return;
    }

    navigator.geolocation.watchPosition(
        (position) => {
            const lat = position.coords.latitude;
            const lng = position.coords.longitude;

            console.log(`GPS: ${lat}, ${lng} (±${position.coords.accuracy}m)`);

            $.ajax({
                url: BASE_URL + "/update-location",
                type: "POST",
                headers: { 'Authorization': 'Bearer ' + token },
                contentType: "application/json",
                data: JSON.stringify({ memberId, latitude: lat, longitude: lng }),
                success: () => console.log("Server location updated ✓")
            });
        },
        (err) => console.error("GPS Error:", err.message),
        { enableHighAccuracy: true, maximumAge: 0, timeout: 10000 }
    );
}

function centerMapOnSriLanka() {
    if (map) map.setView([7.8731, 80.7718], 8);
}