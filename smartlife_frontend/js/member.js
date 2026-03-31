const BASE_URL = "http://localhost:8080/api/v1/members";

$(document).ready(function () {
    const token = localStorage.getItem("token");
    const currentAdminId = localStorage.getItem("userId");

    if (!token || !currentAdminId) {
        window.location.href = "../pages/signinup.html";
        return;
    }

    $.ajaxSetup({
        headers: {
            'Authorization': 'Bearer ' + token,
            'Content-Type': 'application/json'
        }
    });

    loadFamilyMembers();

    $('#memberForm').on('submit', function (e) {
        e.preventDefault();

        const memberData = {
            memberId: $('#memberId').val(),
            fullName: $('#fullName').val(),
            nickname: $('#nickname').val(),
            phoneNumber: $('#phoneNumber').val(),
            email: $('#email').val(),
            password: $('#password').val(),
            isTrackingEnabled: $('#isTrackingEnabled').is(':checked'),
            adminId: currentAdminId
        };

        const isUpdate = memberData.memberId && memberData.memberId !== "MEM-000";
        const url = isUpdate ? `${BASE_URL}/update` : `${BASE_URL}/save`;
        const method = isUpdate ? "PUT" : "POST";

        $.ajax({
            url: url,
            type: method,
            data: JSON.stringify(memberData),
            success: function (res) {
                alert(res);
                resetForm();
                loadFamilyMembers();
            },
            error: function (xhr) {
                alert("Error: " + (xhr.responseText || "Operation failed"));
            }
        });
    });

    function loadFamilyMembers() {
        $.ajax({
            url: `${BASE_URL}/all/${currentAdminId}`,
            type: "GET",
            success: function (res) {
                const members = res.data ? res.data : res;
                renderMemberTable(members);
            }
        });
    }

    function renderMemberTable(members) {
        const tableBody = $("#memberTableBody");
        tableBody.empty();

        if (!members || members.length === 0) {
            tableBody.append('<tr><td colspan="5" style="text-align:center;">No members found.</td></tr>');
            return;
        }

        $.each(members, function (i, m) {
            const statusBadge = m.trackingEnabled
                ? '<span style="color: #28a745;"><i class="fas fa-check-circle"></i> Active</span>'
                : '<span style="color: #dc3545;"><i class="fas fa-times-circle"></i> Off</span>';

            const row = `
                <tr>
                    <td>${m.memberId}</td>
                    <td><strong>${m.fullName}</strong><br><small>${m.email}</small></td>
                    <td>${m.nickname}</td>
                    <td>${statusBadge}</td>
                    <td>
                        <button class="btn-edit" onclick="editMember('${m.memberId}', '${m.fullName}', '${m.nickname}', '${m.phoneNumber}', '${m.email}', ${m.trackingEnabled})">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn-delete" onclick="deleteMember('${m.memberId}')">
                            <i class="fas fa-trash"></i>
                        </button>
                    </td>
                </tr>`;
            tableBody.append(row);
        });
    }

    window.deleteMember = function (id) {
        if (confirm("Are you sure you want to remove this family member?")) {
            $.ajax({
                url: `${BASE_URL}/delete/${id}/${currentAdminId}`,
                type: "DELETE",
                success: function (res) {
                    alert(res);
                    loadFamilyMembers();
                },
                error: function (xhr) {
                    alert("Delete Failed: " + xhr.responseText);
                }
            });
        }
    };

    window.editMember = function (id, name, nick, phone, email, tracking) {
        $('#memberId').val(id);
        $('#fullName').val(name);
        $('#nickname').val(nick);
        $('#phoneNumber').val(phone);
        $('#email').val(email);
        $('#isTrackingEnabled').prop('checked', tracking);

        $('.form-title').html('<i class="fas fa-user-edit"></i> Update Member');
        $('.btn-submit').text('Update Family Member');
    };

    function resetForm() {
        $('#memberForm')[0].reset();
        $('#memberId').val('MEM-000');
        $('.form-title').html('<i class="fas fa-user-plus"></i> Register Member');
        $('.btn-submit').text('Save Family Member');
    }
});