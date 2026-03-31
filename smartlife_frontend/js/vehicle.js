const VEHICLE_API_URL = "http://localhost:8080/api/v1/vehicle";
let pickerMap;
let pickerMarker;
let pickerCircle;

$(document).ready(function () {
    const token = localStorage.getItem("token");
    const currentAdminId = localStorage.getItem("userId");

    if (!token || !currentAdminId) {
        window.location.href = "signinup.html";
        return;
    }

    initPickerMap();
    loadAllVehicles(currentAdminId);

    $('#fence_radius').on('input', function() {
        updatePickerCircle();
    });

    $('#vehicleForm').on('submit', function (e) {
        e.preventDefault();
        const id = $('#vehi_id').val();
        if (id && id !== "VEH-000") {
            updateVehicle(id, currentAdminId);
        } else {
            saveVehicle(currentAdminId);
        }
    });
});

function initPickerMap() {
    pickerMap = L.map('picker-map').setView([6.0367, 80.2170], 13);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; OpenStreetMap'
    }).addTo(pickerMap);

    pickerMap.on('click', function(e) {
        setPickerLocation(e.latlng.lat, e.latlng.lng);
    });
}

function setPickerLocation(lat, lng) {
    $('#fence_lat').val(lat.toFixed(6));
    $('#fence_lng').val(lng.toFixed(6));

    if (pickerMarker) {
        pickerMarker.setLatLng([lat, lng]);
    } else {
        pickerMarker = L.marker([lat, lng]).addTo(pickerMap);
    }
    updatePickerCircle();
}

function updatePickerCircle() {
    const lat = parseFloat($('#fence_lat').val());
    const lng = parseFloat($('#fence_lng').val());
    const radius = parseFloat($('#fence_radius').val());

    if (pickerCircle) pickerMap.removeLayer(pickerCircle);

    if (lat && lng && radius) {
        pickerCircle = L.circle([lat, lng], {
            color: '#00d2ff',
            fillColor: '#00d2ff',
            fillOpacity: 0.2,
            radius: radius
        }).addTo(pickerMap);
    }
}


function saveVehicle(adminId) {
    const vehicleData = getVehicleFormData(null, adminId);
    $.ajax({
        url: VEHICLE_API_URL + "/save",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(vehicleData),
        headers: { 'Authorization': 'Bearer ' + localStorage.getItem('token') },
        success: function () {
            alert("Vehicle Registered Successfully!");
            resetVehicleForm();
            loadAllVehicles(adminId);
        },
        error: function (xhr) {
            handleValidationErrors(xhr);
        }
    });
}

function updateVehicle(id, adminId) {
    const vehicleData = getVehicleFormData(id, adminId);
    $.ajax({
        url: VEHICLE_API_URL + "/update",
        type: "PUT",
        contentType: "application/json",
        data: JSON.stringify(vehicleData),
        headers: { 'Authorization': 'Bearer ' + localStorage.getItem('token') },
        success: function () {
            alert("Vehicle Details Updated!");
            resetVehicleForm();
            loadAllVehicles(adminId);
        },
        error: function (xhr) {
            handleValidationErrors(xhr);
        }
    });
}

function getVehicleFormData(id, adminId) {
    return {
        vehi_id: id,
        vehi_number: $('#vehi_number').val().toUpperCase().trim(),
        adminId: adminId,
        fenceLat: $('#fence_lat').val() ? parseFloat($('#fence_lat').val()) : null,
        fenceLng: $('#fence_lng').val() ? parseFloat($('#fence_lng').val()) : null,
        fenceRadius: $('#fence_radius').val() ? parseFloat($('#fence_radius').val()) : null
    };
}

function loadAllVehicles(adminId) {
    $.ajax({
        url: `${VEHICLE_API_URL}/all/${adminId}`,
        type: "GET",
        headers: { 'Authorization': 'Bearer ' + localStorage.getItem('token') },
        success: function (res) {
            const list = res.data ? res.data : res;
            const tableBody = $("#vehicleTableBody");
            tableBody.empty();

            if (list.length === 0) {
                tableBody.append("<tr><td colspan='4' style='text-align:center; padding:20px;'>No vehicles registered yet.</td></tr>");
                return;
            }

            $.each(list, function (index, v) {
                let vId = v.vehi_id || v.vehicleId;
                let row = `
                    <tr>
                        <td>${vId}</td>
                        <td><strong>${v.vehi_number}</strong></td>
                        <td><span class='status-badge' style='background:rgba(28,200,138,0.1); color:#1cc88a; padding:4px 8px; border-radius:4px;'>Active</span></td>
                        <td style='text-align: center;'>
                            <button class='btn-action edit' onclick='fillVehicleFormForEdit(${JSON.stringify(v)})' style='color: #4e73df; border:none; background:none; cursor:pointer; margin-right:15px;'><i class='fas fa-edit'></i></button>
                            <button class='btn-action delete' onclick='deleteVehicle("${vId}", "${adminId}")' style='color: #ff4757; border:none; background:none; cursor:pointer;'><i class='fas fa-trash'></i></button>
                        </td>
                    </tr>`;
                tableBody.append(row);
            });
        }
    });
}

function deleteVehicle(id, adminId) {
    if (confirm("Are you sure you want to remove this vehicle?")) {
        $.ajax({
            url: `${VEHICLE_API_URL}/delete/${id}`,
            type: "DELETE",
            headers: { 'Authorization': 'Bearer ' + localStorage.getItem('token') },
            success: function () {
                alert("Vehicle Removed!");
                loadAllVehicles(adminId);
            },
            error: function (xhr) {
                alert("Delete Failed!");
            }
        });
    }
}


function fillVehicleFormForEdit(v) {
    let id = v.vehi_id || v.vehicleId;
    $('#vehi_id').val(id);
    $('#vehi_number').val(v.vehi_number);

    $('#fence_lat').val(v.fenceLat);
    $('#fence_lng').val(v.fenceLng);
    $('#fence_radius').val(v.fenceRadius);

    if (v.fenceLat && v.fenceLng) {
        setPickerLocation(v.fenceLat, v.fenceLng);
        pickerMap.setView([v.fenceLat, v.fenceLng], 15);
    }

    $('#btnSaveVehicle').html('<i class="fas fa-save"></i> Update Vehicle');
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

function resetVehicleForm() {
    $('#vehicleForm')[0].reset();
    $('#vehi_id').val('VEH-000');

    if (pickerMarker) pickerMap.removeLayer(pickerMarker);
    if (pickerCircle) pickerMap.removeLayer(pickerCircle);
    pickerMarker = null;
    pickerCircle = null;

    $('#btnSaveVehicle').html('<i class="fas fa-plus"></i> Register Vehicle');
}

function handleValidationErrors(xhr) {
    const response = xhr.responseJSON;
    if (xhr.status === 400 && response && response.data) {
        let msg = "";
        $.each(response.data, function (field, message) {
            msg += `• ${field}: ${message}\n`;
        });
        alert("Validation Error:\n" + msg);
    } else {
        alert("Error: " + (response ? response.message : "Request failed"));
    }
}